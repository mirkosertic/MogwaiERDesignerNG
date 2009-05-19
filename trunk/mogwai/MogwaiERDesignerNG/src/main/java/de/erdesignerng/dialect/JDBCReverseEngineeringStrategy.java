/**
 * Mogwai ERDesigner. Copyright (C) 2002 The Mogwai Project.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 */
package de.erdesignerng.dialect;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.exception.ElementAlreadyExistsException;
import de.erdesignerng.exception.ReverseEngineeringException;
import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.CascadeType;
import de.erdesignerng.model.Index;
import de.erdesignerng.model.IndexExpression;
import de.erdesignerng.model.IndexType;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.Relation;
import de.erdesignerng.model.Table;
import de.erdesignerng.model.View;
import de.erdesignerng.visual.common.ERDesignerWorldConnector;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-13 15:40:33 $
 * @param <T>
 *            the dialect
 */
public abstract class JDBCReverseEngineeringStrategy<T extends JDBCDialect> extends ReverseEngineeringStrategy<T> {

    private static final Logger LOGGER = Logger.getLogger(JDBCReverseEngineeringStrategy.class);

    public static final String TABLE_TABLE_TYPE = "TABLE";

    public static final String VIEW_TABLE_TYPE = "VIEW";

    protected JDBCReverseEngineeringStrategy(T aDialect) {
        super(aDialect);
    }

    protected void reverseEngineerAttribute(Model aModel, Attribute aAttribute, ReverseEngineeringOptions aOptions,
            ReverseEngineeringNotifier aNotifier, TableEntry aTable, Connection aConnection) throws SQLException {
    }

    /**
     * Reverse engineerer the sql statement for a view.
     * 
     * @param aViewEntry
     *            die view entry
     * @param aConnection
     *            the connection
     * @param aView
     *            the view
     * @return the sql statement
     * @throws SQLException
     *             is thrown in case of an exception
     * @throws ReverseEngineeringException
     *             is thrown in case of an exception
     */
    protected String reverseEngineerViewSQL(TableEntry aViewEntry, Connection aConnection, View aView)
            throws SQLException, ReverseEngineeringException {
        return null;
    }

    /**
     * Reverse enginner an existing view.
     * 
     * @param aModel
     *            the model
     * @param aOptions
     *            the options
     * @param aNotifier
     *            the notifier
     * @param aViewEntry
     *            the table
     * @param aConnection
     *            the connection
     * @throws SQLException
     *             is thrown in case of an error
     * @throws ReverseEngineeringException
     *             is thrown in case of an error
     */
    protected void reverseEngineerView(Model aModel, ReverseEngineeringOptions aOptions,
            ReverseEngineeringNotifier aNotifier, TableEntry aViewEntry, Connection aConnection) throws SQLException,
            ReverseEngineeringException {

        aNotifier.notifyMessage(ERDesignerBundle.ENGINEERINGTABLE, aViewEntry.getTableName());

        DatabaseMetaData theMetaData = aConnection.getMetaData();

        ResultSet theViewsResultSet = theMetaData.getTables(aViewEntry.getCatalogName(), aViewEntry.getSchemaName(),
                aViewEntry.getTableName(), new String[] { aViewEntry.getTableType() });
        while (theViewsResultSet.next()) {

            String theViewRemarks = theViewsResultSet.getString("REMARKS");

            View theView = new View();

            theView.setName(dialect.getCastType().cast(aViewEntry.getTableName()));
            theView.setOriginalName(aViewEntry.getTableName());

            if (!StringUtils.isEmpty(theViewRemarks)) {
                theView.setComment(theViewRemarks);
            }

            String theStatement = reverseEngineerViewSQL(aViewEntry, aConnection, theView);
            /*
             * try { SQLUtils.updateViewAttributesFromSQL(theView,
             * theStatement); } catch (Exception e) { throw new
             * ReverseEngineeringException("Problem reading view definition",
             * e); }
             */
            theView.setSql(theStatement);

            // We are done here
            try {
                aModel.addView(theView);
            } catch (Exception e) {
                throw new ReverseEngineeringException(e.getMessage(), e);
            }

        }
        theViewsResultSet.close();
    }

    /**
     * Reverse enginner an existing table.
     * 
     * @param aModel
     *            the model
     * @param aOptions
     *            the options
     * @param aNotifier
     *            the notifier
     * @param aTableEntry
     *            the table
     * @param aConnection
     *            the connection
     * @throws SQLException
     *             is thrown in case of an error
     * @throws ReverseEngineeringException
     *             is thrown in case of an error
     */
    protected void reverseEngineerTable(Model aModel, ReverseEngineeringOptions aOptions,
            ReverseEngineeringNotifier aNotifier, TableEntry aTableEntry, Connection aConnection) throws SQLException,
            ReverseEngineeringException {

        aNotifier.notifyMessage(ERDesignerBundle.ENGINEERINGTABLE, aTableEntry.getTableName());

        DatabaseMetaData theMetaData = aConnection.getMetaData();

        ResultSet theTablesResultSet = theMetaData.getTables(aTableEntry.getCatalogName(), aTableEntry.getSchemaName(),
                aTableEntry.getTableName(), new String[] { aTableEntry.getTableType() });
        while (theTablesResultSet.next()) {

            String theTableRemarks = theTablesResultSet.getString("REMARKS");

            Table theTable = new Table();

            theTable.setName(dialect.getCastType().cast(aTableEntry.getTableName()));
            theTable.setOriginalName(aTableEntry.getTableName());

            if (!StringUtils.isEmpty(theTableRemarks)) {
                theTable.setComment(theTableRemarks);
            }

            // Reverse engineer attributes
            ResultSet theColumnsResultSet = theMetaData.getColumns(aTableEntry.getCatalogName(), aTableEntry
                    .getSchemaName(), aTableEntry.getTableName(), null);
            while (theColumnsResultSet.next()) {

                String theColumnName = theColumnsResultSet.getString("COLUMN_NAME");
                String theTypeName = theColumnsResultSet.getString("TYPE_NAME");

                int theSize = theColumnsResultSet.getInt("COLUMN_SIZE");
                int theFraction = theColumnsResultSet.getInt("DECIMAL_DIGITS");
                int theRadix = theColumnsResultSet.getInt("NUM_PREC_RADIX");

                int theNullable = theColumnsResultSet.getInt("NULLABLE");

                String theDefaultValue = theColumnsResultSet.getString("COLUMN_DEF");
                if (!StringUtils.isEmpty(theDefaultValue)) {
                    theDefaultValue = theDefaultValue.trim();
                }
                String theColumnRemarks = theColumnsResultSet.getString("REMARKS");

                Attribute theAttribute = new Attribute();

                theAttribute.setName(dialect.getCastType().cast(theColumnName));
                if (!StringUtils.isEmpty(theColumnRemarks)) {
                    theAttribute.setComment(theColumnRemarks);
                }

                DataType theDataType = dialect.getDataTypes().findByName(convertColumnTypeToRealType(theTypeName));
                if (theDataType == null) {
                    throw new ReverseEngineeringException("Unknown data type " + theTypeName + " for "
                            + aTableEntry.getTableName() + "." + theColumnName);
                }

                boolean isNullable = true;
                switch (theNullable) {
                case DatabaseMetaData.columnNoNulls:
                    isNullable = false;
                    break;
                case DatabaseMetaData.columnNullable:
                    isNullable = true;
                    break;
                default:
                    // TODO [mirkosertic] What should happen here?
                }

                theAttribute.setDatatype(theDataType);
                theAttribute.setSize(theSize);
                theAttribute.setFraction(theFraction);
                theAttribute.setScale(theRadix);
                theAttribute.setDefaultValue(theDefaultValue);
                theAttribute.setNullable(isNullable);

                reverseEngineerAttribute(aModel, theAttribute, aOptions, aNotifier, aTableEntry, aConnection);

                try {
                    theTable.addAttribute(aModel, theAttribute);
                } catch (Exception e) {
                    throw new ReverseEngineeringException(e.getMessage(), e);
                }
            }
            theColumnsResultSet.close();

            // Reverse engineer primary keys
            reverseEngineerPrimaryKey(aModel, aTableEntry, theMetaData, theTable);

            // Reverse engineer indexes
            try {
                reverseEngineerIndexes(aModel, aTableEntry, theMetaData, theTable, aNotifier);
            } catch (SQLException e) {
                // if there is an sql exception, just ignore it
            }

            // We are done here
            try {
                aModel.addTable(theTable);
            } catch (Exception e) {
                throw new ReverseEngineeringException(e.getMessage(), e);
            }

        }
        theTablesResultSet.close();
    }

    protected void reverseEngineerPrimaryKey(Model aModel, TableEntry aTableEntry, DatabaseMetaData aMetaData,
            Table aTable) throws SQLException, ReverseEngineeringException {

        ResultSet thePrimaryKeyResultSet = aMetaData.getPrimaryKeys(aTableEntry.getCatalogName(), aTableEntry
                .getSchemaName(), aTableEntry.getTableName());
        Index thePrimaryKeyIndex = null;
        while (thePrimaryKeyResultSet.next()) {

            String thePKName = thePrimaryKeyResultSet.getString("PK_NAME");
            String theColumnName = thePrimaryKeyResultSet.getString("COLUMN_NAME");

            if (thePrimaryKeyIndex == null) {
                thePrimaryKeyIndex = new Index();
                thePrimaryKeyIndex.setIndexType(IndexType.PRIMARYKEY);
                thePrimaryKeyIndex.setName(convertIndexNameFor(aTable, thePKName));
                thePrimaryKeyIndex.setOriginalName(thePKName);
                if (StringUtils.isEmpty(thePrimaryKeyIndex.getName())) {
                    // Assume the default name is TABLE_NAME+"_FK"
                    thePrimaryKeyIndex.setName(aTableEntry.getTableName() + "_FK");
                }

                try {
                    aTable.addIndex(aModel, thePrimaryKeyIndex);
                } catch (Exception e) {
                    throw new ReverseEngineeringException(e.getMessage(), e);
                }
            }

            Attribute theIndexAttribute = aTable.getAttributes().findByName(dialect.getCastType().cast(theColumnName));
            if (theIndexAttribute == null) {
                throw new ReverseEngineeringException("Cannot find attribute " + theColumnName + " in table "
                        + aTable.getName());
            }

            try {
                thePrimaryKeyIndex.getExpressions().addExpressionFor(theIndexAttribute);
            } catch (ElementAlreadyExistsException e) {
                throw new ReverseEngineeringException("Error adding index attribute", e);
            }

        }
        thePrimaryKeyResultSet.close();
    }

    protected String convertIndexNameFor(Table aTable, String aIndexName) {
        return aIndexName;
    }

    protected void reverseEngineerIndexes(Model aModel, TableEntry aTableEntry, DatabaseMetaData aMetaData,
            Table aTable, ReverseEngineeringNotifier aNotifier) throws SQLException, ReverseEngineeringException {

        ResultSet theIndexResults = aMetaData.getIndexInfo(aTableEntry.getCatalogName(), aTableEntry.getSchemaName(),
                aTableEntry.getTableName(), false, true);
        Index theIndex = null;
        while (theIndexResults.next()) {

            String theIndexName = convertIndexNameFor(aTable, theIndexResults.getString("INDEX_NAME"));

            if ((theIndexName != null) && ((theIndex == null) || (!theIndex.getOriginalName().equals(theIndexName)))) {

                String theNewIndexName = dialect.getCastType().cast(theIndexName);

                if (aTable.getIndexes().findByName(theNewIndexName) == null) {
                    theIndex = new Index();
                    theIndex.setName(theNewIndexName);
                    theIndex.setOriginalName(theIndexName);

                    boolean isNonUnique = theIndexResults.getBoolean("NON_UNIQUE");
                    if (isNonUnique) {
                        theIndex.setIndexType(IndexType.NONUNIQUE);
                    } else {
                        theIndex.setIndexType(IndexType.UNIQUE);
                    }

                    aNotifier.notifyMessage(ERDesignerBundle.ENGINEERINGINDEX, theIndex.getName());

                    try {
                        aTable.addIndex(aModel, theIndex);
                    } catch (Exception e) {
                        throw new ReverseEngineeringException("Cannot add index " + theIndexName + " in table "
                                + aTable.getName() + " : " + e.getMessage(), e);
                    }
                } else {
                    theIndex = null;
                }
            }

            if (theIndex != null) {
                short aPosition = theIndexResults.getShort("ORDINAL_POSITION");

                String theColumnName = theIndexResults.getString("COLUMN_NAME");
                String theASCorDESC = theIndexResults.getString("ASC_OR_DESC");

                reverseEngineerIndexAttribute(aMetaData, aTableEntry, aTable, aNotifier, theIndex, theColumnName,
                        aPosition, theASCorDESC);
            }

        }
        theIndexResults.close();
    }

    /**
     * Reverse engineer an attribute within an index.
     * 
     * @param aMetaData
     *            the database meta data
     * @param aTableEntry
     *            the current table entry
     * @param aTable
     *            the table
     * @param aNotifier
     *            the notifier
     * @param aIndex
     *            the current index
     * @param aColumnName
     *            the column name
     * @param aPosition
     *            the column position
     * @param aASCorDESC
     *            "A" = Ascending, "D" = Descending, NULL = sort not supported
     * @throws SQLException
     *             in case of an error
     * @throws ReverseEngineeringException
     *             in case of an error
     */
    protected void reverseEngineerIndexAttribute(DatabaseMetaData aMetaData, TableEntry aTableEntry, Table aTable,
            ReverseEngineeringNotifier aNotifier, Index aIndex, String aColumnName, short aPosition, String aASCorDESC)
            throws SQLException, ReverseEngineeringException {
        Attribute theIndexAttribute = aTable.getAttributes().findByName(dialect.getCastType().cast(aColumnName));
        if (theIndexAttribute == null) {

            // The index is corrupt or
            // It is a oracle function based index
            // Should be overridden in
            if (aTable.getIndexes().contains(aIndex)) {

                aNotifier.notifyMessage(ERDesignerBundle.SKIPINDEX, aIndex.getName());
                aTable.getIndexes().remove(aIndex);
            }

        } else {
            try {
                aIndex.getExpressions().addExpressionFor(theIndexAttribute);
            } catch (ElementAlreadyExistsException e) {
                throw new ReverseEngineeringException("Error adding index attribute", e);
            }
        }
    }

    /**
     * Reverse engineer relations.
     * 
     * @param aModel
     *            the model
     * @param aOptions
     *            the options
     * @param aNotifier
     *            the notifier
     * @param aEntry
     *            the schema entry
     * @param aConnection
     *            the connection
     * @throws SQLException
     *             is thrown in case of an error
     * @throws ReverseEngineeringException
     *             is thrown in case of an error
     */
    protected void reverseEngineerRelations(Model aModel, ReverseEngineeringOptions aOptions,
            ReverseEngineeringNotifier aNotifier, SchemaEntry aEntry, Connection aConnection) throws SQLException,
            ReverseEngineeringException {

        DatabaseMetaData theMetaData = aConnection.getMetaData();

        String theSchemaName = null;
        String theCatalogName = null;
        if (aEntry != null) {
            theSchemaName = aEntry.getSchemaName();
            theCatalogName = aEntry.getCatalogName();
        }

        int theSysCounter = 0;

        List<Relation> theNewRelations = new ArrayList<Relation>();

        for (Table theTable : aModel.getTables()) {

            aNotifier.notifyMessage(ERDesignerBundle.ENGINEERINGRELATION, theTable.getName());

            String theOldFKName = null;

            // Foreign keys
            Relation theRelation = null;
            ResultSet theForeignKeys = theMetaData.getImportedKeys(theCatalogName, theSchemaName, theTable
                    .getOriginalName());
            while (theForeignKeys.next()) {
                String theFKName = theForeignKeys.getString("FK_NAME");

                if ((theRelation == null) || (!theFKName.equals(theOldFKName))) {

                    theOldFKName = theFKName;

                    String thePKTableName = theForeignKeys.getString("PKTABLE_NAME");
                    String theUpdateRule = theForeignKeys.getString("UPDATE_RULE");
                    String theDeleteRule = theForeignKeys.getString("DELETE_RULE");

                    Table theExportingTable = aModel.getTables().findByName(dialect.getCastType().cast(thePKTableName));
                    if (theExportingTable != null) {

                        // The relation is only added to the model
                        // if the exporting table is also part of the model
                        String theRelationName = dialect.getCastType().cast(theFKName);
                        theRelation = aModel.getRelations().findByName(theRelationName);

                        boolean addNew = false;
                        if (theRelation == null) {
                            addNew = true;
                        } else {
                            if (!theRelation.getExportingTable().equals(theExportingTable)
                                    || !theRelation.getImportingTable().equals(theTable)) {
                                theRelationName = "ERRELSYS_" + theSysCounter++;
                                addNew = true;
                            }
                        }

                        if (addNew) {

                            theRelation = new Relation();
                            theRelation.setName(dialect.getCastType().cast(theRelationName));
                            theRelation.setOriginalName(theRelationName);

                            theRelation.setExportingTable(theExportingTable);
                            theRelation.setImportingTable(theTable);

                            if (theUpdateRule != null) {
                                int theType = Integer.parseInt(theUpdateRule);

                                theRelation.setOnUpdate(getCascadeType(theType));
                            } else {

                                theRelation.setOnUpdate(CascadeType.NOTHING);
                            }

                            if (theDeleteRule != null) {
                                int theType = Integer.parseInt(theDeleteRule);

                                theRelation.setOnDelete(getCascadeType(theType));
                            } else {

                                theRelation.setOnDelete(CascadeType.NOTHING);
                            }

                            theNewRelations.add(theRelation);
                        }
                    }
                }

                if ((theRelation != null) && (theRelation.getImportingTable() != null)
                        && (theRelation.getExportingTable() != null)) {
                    String thePKColumnName = dialect.getCastType().cast(theForeignKeys.getString("PKCOLUMN_NAME"));
                    String theFKColumnName = dialect.getCastType().cast(theForeignKeys.getString("FKCOLUMN_NAME"));

                    Attribute theExportingAttribute = theRelation.getExportingTable().getAttributes().findByName(
                            dialect.getCastType().cast(thePKColumnName));
                    if (theExportingAttribute == null) {
                        throw new ReverseEngineeringException("Cannot find column " + thePKColumnName + " in table "
                                + theRelation.getExportingTable().getName());
                    }

                    Index thePrimaryKey = theRelation.getExportingTable().getPrimarykey();
                    if (thePrimaryKey == null) {
                        throw new ReverseEngineeringException("Table " + theRelation.getExportingTable().getName()
                                + " does not have a primary key");
                    }
                    IndexExpression theExpression = thePrimaryKey.getExpressions().findByAttributeName(thePKColumnName);
                    if (theExpression == null) {
                        throw new RuntimeException("Cannot find attribute " + thePKColumnName
                                + " in primary key for table " + theRelation.getExportingTable().getName());
                    }

                    Attribute theImportingAttribute = theRelation.getImportingTable().getAttributes().findByName(
                            theFKColumnName);
                    if (theImportingAttribute == null) {
                        throw new ReverseEngineeringException("Cannot find column " + theFKColumnName + " in table "
                                + theRelation.getImportingTable().getName());
                    }

                    theRelation.getMapping().put(theExpression, theImportingAttribute);
                }
            }
            theForeignKeys.close();
        }

        try {
            for (Relation theRelation : theNewRelations) {
                try {
                    aModel.addRelation(theRelation);
                } catch (ElementAlreadyExistsException e) {
                    // This might happen for instance on DB2 databases
                    // We will try to generate a new name here!!!
                    int counter = 0;
                    String theNewName = null;
                    while (counter == 0
                            || aModel.getRelations().findByName(dialect.getCastType().cast(theNewName)) != null) {
                        counter++;
                        theNewName = theRelation.getExportingTable().getName() + "_" + theRelation.getImportingTable()
                                + "_FK" + counter;
                    }
                    LOGGER.warn("Relation " + theRelation.getName() + " exists. Renaming it to " + theNewName);
                    theRelation.setName(theNewName);
                    aModel.addRelation(theRelation);
                }
            }
        } catch (Exception e) {
            throw new ReverseEngineeringException(e.getMessage(), e);
        }
    }

    /**
     * Get the list of available table types that shall be reverse engineered.
     * Default is only "TABLE", but can be overridden by subclasses.
     * 
     * @return the l
     */
    protected String[] getReverseEngineeringTableTypes() {
        return new String[] { TABLE_TABLE_TYPE };
    }

    /**
     * Test if a table type is a view.
     * 
     * @param aTableType
     *            the table type
     * @return true if yes, else false
     */
    protected boolean isTableTypeView(String aTableType) {
        return false;
    }

    /**
     * Check if the table is a valid table for reverse engineering.
     * 
     * @param aTableName
     *            the table name
     * @param aTableType
     *            the table type
     * @return true if the table is valid, else false
     */
    protected boolean isValidTable(String aTableName, String aTableType) {
        return true;
    }

    /**
     * Check if the table is a valid view for reverse engineering.
     * 
     * @param aTableName
     *            the table name
     * @param aTableType
     *            the table type
     * @return true if the table is valid, else false
     */
    protected boolean isValidView(String aTableName, String aTableType) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateModelFromConnection(Model aModel, ERDesignerWorldConnector aConnector, Connection aConnection,
            ReverseEngineeringOptions aOptions, ReverseEngineeringNotifier aNotifier) throws SQLException,
            ReverseEngineeringException {

        for (TableEntry theTable : aOptions.getTableEntries()) {
            if (isTableTypeView(theTable.getTableType())) {
                reverseEngineerView(aModel, aOptions, aNotifier, theTable, aConnection);
            } else {
                reverseEngineerTable(aModel, aOptions, aNotifier, theTable, aConnection);
            }
        }

        if (dialect.supportsSchemaInformation()) {
            for (SchemaEntry theEntry : aOptions.getSchemaEntries()) {
                reverseEngineerRelations(aModel, aOptions, aNotifier, theEntry, aConnection);
            }
        } else {
            reverseEngineerRelations(aModel, aOptions, aNotifier, null, aConnection);
        }

        aNotifier.notifyMessage(ERDesignerBundle.ENGINEERINGFINISHED, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SchemaEntry> getSchemaEntries(Connection aConnection) throws SQLException {

        List<SchemaEntry> theList = new ArrayList<SchemaEntry>();

        DatabaseMetaData theMetadata = aConnection.getMetaData();
        ResultSet theResult = theMetadata.getSchemas();

        while (theResult.next()) {
            String theSchemaName = theResult.getString("TABLE_SCHEM");
            String theCatalogName = theResult.getString("TABLE_CATALOG");

            theList.add(new SchemaEntry(theCatalogName, theSchemaName));
        }

        return theList;
    }

    protected List<TableEntry> getTablesForSchemaEntry(Connection aConnection, SchemaEntry aEntry) throws SQLException {

        List<TableEntry> theResult = new ArrayList<TableEntry>();

        DatabaseMetaData theMetaData = aConnection.getMetaData();

        // Reverse engineer tables
        ResultSet theTablesResultSet = null;
        String theCatalogName = null;
        String theSchemaName = null;
        if (aEntry != null) {
            theCatalogName = aEntry.getCatalogName();
            theSchemaName = aEntry.getSchemaName();
            theTablesResultSet = theMetaData.getTables(theCatalogName, theSchemaName, null,
                    getReverseEngineeringTableTypes());
        } else {
            theTablesResultSet = theMetaData.getTables(null, null, null, getReverseEngineeringTableTypes());
        }

        while (theTablesResultSet.next()) {

            String theTableType = theTablesResultSet.getString("TABLE_TYPE");
            String theTableName = theTablesResultSet.getString("TABLE_NAME");

            if (isTableTypeView(theTableType)) {
                if (isValidView(theTableName, theTableType)) {
                    TableEntry theEntry = new TableEntry(theCatalogName, theSchemaName, theTableName, theTableType);
                    theResult.add(theEntry);
                }
            } else {
                if (isValidTable(theTableName, theTableType)) {
                    TableEntry theEntry = new TableEntry(theCatalogName, theSchemaName, theTableName, theTableType);
                    theResult.add(theEntry);
                }
            }
        }
        theTablesResultSet.close();

        return theResult;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TableEntry> getTablesForSchemas(Connection aConnection, List<SchemaEntry> aSchemaEntries)
            throws SQLException {
        List<TableEntry> theResult = new ArrayList<TableEntry>();

        if (dialect.supportsSchemaInformation()) {

            for (SchemaEntry theEntry : aSchemaEntries) {
                theResult.addAll(getTablesForSchemaEntry(aConnection, theEntry));
            }

        } else {
            theResult.addAll(getTablesForSchemaEntry(aConnection, null));
        }

        return theResult;
    }
}
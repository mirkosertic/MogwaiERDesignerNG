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
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
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

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.exception.ReverseEngineeringException;
import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.CascadeType;
import de.erdesignerng.model.Index;
import de.erdesignerng.model.IndexType;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.Relation;
import de.erdesignerng.model.Table;
import de.erdesignerng.modificationtracker.EmptyModelModificationTracker;
import de.erdesignerng.modificationtracker.ModelModificationTracker;
import de.erdesignerng.visual.common.ERDesignerWorldConnector;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-03-02 15:46:49 $
 * @param <T>
 *            the dialect
 */
public abstract class JDBCReverseEngineeringStrategy<T extends JDBCDialect> extends ReverseEngineeringStrategy<T> {

    protected JDBCReverseEngineeringStrategy(T aDialect) {
        super(aDialect);
    }

    protected void reverseEngineerAttribute(Model aModel, Attribute aAttribute, ReverseEngineeringOptions aOptions,
            ReverseEngineeringNotifier aNotifier, TableEntry aTable, Connection aConnection) throws SQLException {
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
     *            the table name
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
                aTableEntry.getTableName(), new String[] { "TABLE" });
        while (theTablesResultSet.next()) {

            String theTableRemarks = theTablesResultSet.getString("REMARKS");

            Table theTable = new Table();
            theTable.setName(dialect.getCastType().cast(aTableEntry.getTableName()));

            if ((theTableRemarks != null) && (!"".equals(theTableRemarks))) {
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
                String theColumnRemarks = theColumnsResultSet.getString("REMARKS");

                Attribute theAttribute = new Attribute();
                theAttribute.setName(dialect.getCastType().cast(theColumnName));
                if ((theColumnRemarks != null) && (!"".equals(theColumnRemarks))) {
                    theAttribute.setComment(theColumnRemarks);
                }

                DataType theDataType = dialect.getDataTypeByName(convertColumnTypeToRealType(theTypeName));
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
                    // TODO [mse] What should happen here?
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
                    throw new ReverseEngineeringException(e.getMessage());
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
                throw new ReverseEngineeringException(e.getMessage());
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

                try {
                    aTable.addIndex(aModel, thePrimaryKeyIndex);
                } catch (Exception e) {
                    throw new ReverseEngineeringException(e.getMessage());
                }
            }

            Attribute theIndexAttribute = aTable.getAttributes().findByName(dialect.getCastType().cast(theColumnName));
            if (theIndexAttribute == null) {
                throw new ReverseEngineeringException("Cannot find attribute " + theColumnName + " in table "
                        + aTable.getName());
            }

            thePrimaryKeyIndex.getAttributes().add(theIndexAttribute);

        }
        thePrimaryKeyResultSet.close();
    }

    protected String convertIndexNameFor(Table aTable, String aIndexName) {
        return aIndexName;
    }

    protected void reverseEngineerIndexes(Model aModel, TableEntry aTableEntry, DatabaseMetaData aMetaData,
            Table aTable, ReverseEngineeringNotifier aNotifier) throws SQLException, ReverseEngineeringException {

        ResultSet theIndexResults = aMetaData.getIndexInfo(aTableEntry.getCatalogName(), aTableEntry.getCatalogName(),
                aTableEntry.getTableName(), false, true);
        Index theIndex = null;
        while (theIndexResults.next()) {

            String theIndexName = convertIndexNameFor(aTable, theIndexResults.getString("INDEX_NAME"));
            if ((theIndexName != null) && ((theIndex == null) || (!theIndex.getName().equals(theIndexName)))) {

                if (aTable.getIndexes().findByName(theIndexName) == null) {
                    theIndex = new Index();
                    theIndex.setName(theIndexName);

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
                                + aTable.getName() + " : " + e.getMessage());
                    }
                } else {
                    theIndex = null;
                }
            }

            if (theIndex != null) {
                String theColumnName = theIndexResults.getString("COLUMN_NAME");
                Attribute theIndexAttribute = aTable.getAttributes().findByName(
                        dialect.getCastType().cast(theColumnName));
                if (theIndexAttribute == null) {

                    // The index is corrupt or
                    // It is a oracle function based index
                    if (aTable.getIndexes().contains(theIndex)) {
                        aNotifier.notifyMessage(ERDesignerBundle.SKIPINDEX, theIndex.getName());
                        aTable.getIndexes().remove(theIndex);
                    }

                } else {
                    theIndex.getAttributes().add(theIndexAttribute);
                }
            }

        }
        theIndexResults.close();
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

        for (Table theTable : aModel.getTables()) {

            aNotifier.notifyMessage(ERDesignerBundle.ENGINEERINGRELATION, theTable.getName());

            // Foreign keys
            Relation theRelation = null;
            ResultSet theForeignKeys = theMetaData.getImportedKeys(theCatalogName, theSchemaName, theTable.getName());
            while (theForeignKeys.next()) {
                String theFKName = theForeignKeys.getString("FK_NAME");
                if ((theRelation == null) || (!theFKName.equals(theRelation.getName()))) {

                    String thePKTableName = theForeignKeys.getString("PKTABLE_NAME");
                    String theUpdateRule = theForeignKeys.getString("UPDATE_RULE");
                    String theDeleteRule = theForeignKeys.getString("DELETE_RULE");

                    Table theExportingTable = aModel.getTables().findByName(dialect.getCastType().cast(thePKTableName));
                    if (theExportingTable != null) {

                        // The relation is only added to the model
                        // if the exporting table is also part of the model
                        theRelation = new Relation();
                        theRelation.setName(dialect.getCastType().cast(theFKName));
                        theRelation.setExportingTable(theExportingTable);
                        theRelation.setImportingTable(theTable);

                        if (theUpdateRule != null) {
                            int theType = Integer.parseInt(theUpdateRule.toString());
                            theRelation.setOnUpdate(getCascadeType(theType));
                        } else {
                            theRelation.setOnUpdate(CascadeType.NOTHING);
                        }

                        if (theDeleteRule != null) {
                            int theType = Integer.parseInt(theDeleteRule.toString());
                            theRelation.setOnDelete(getCascadeType(theType));
                        } else {
                            theRelation.setOnDelete(CascadeType.NOTHING);
                        }

                        try {
                            aModel.addRelation(theRelation);
                        } catch (Exception e) {
                            throw new ReverseEngineeringException(e.getMessage());
                        }
                    }
                }

                if ((theRelation != null) && (theRelation.getImportingTable() != null)
                        && (theRelation.getExportingTable() != null)) {
                    String thePKColumnName = theForeignKeys.getString("PKCOLUMN_NAME");
                    String theFKColumnName = theForeignKeys.getString("FKCOLUMN_NAME");

                    Attribute theExportingAttribute = theRelation.getExportingTable().getAttributes().findByName(
                            dialect.getCastType().cast(thePKColumnName));
                    if (theExportingAttribute == null) {
                        throw new ReverseEngineeringException("Cannot find column " + thePKColumnName + " in table "
                                + theRelation.getExportingTable().getName());
                    }

                    Attribute theImportingAttribute = theRelation.getImportingTable().getAttributes().findByName(
                            dialect.getCastType().cast(theFKColumnName));
                    if (theImportingAttribute == null) {
                        throw new ReverseEngineeringException("Cannot find column " + theFKColumnName + " in table "
                                + theRelation.getImportingTable().getName());
                    }

                    theRelation.getMapping().put(theExportingAttribute, theImportingAttribute);
                }
            }
            theForeignKeys.close();
        }
    }

    protected String[] getReverseEngineeringTableTypes() {
        return new String[] { "TABLE" };
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

    @Override
    public Model createModelFromConnection(ERDesignerWorldConnector aConnector, Connection aConnection,
            ReverseEngineeringOptions aOptions, ReverseEngineeringNotifier aNotifier) throws SQLException,
            ReverseEngineeringException {

        Model theNewModel = aConnector.createNewModel();

        // The modification tracker is disabled during reverse engineering
        ModelModificationTracker theOldModificationTracker = theNewModel.getModificationTracker();
        theNewModel.setModificationTracker(new EmptyModelModificationTracker());

        theNewModel.setDialect(dialect);
        for (TableEntry theTable : aOptions.getTableEntries()) {
            reverseEngineerTable(theNewModel, aOptions, aNotifier, theTable, aConnection);
        }

        if (dialect.supportsSchemaInformation()) {
            for (SchemaEntry theEntry : aOptions.getSchemaEntries()) {
                reverseEngineerRelations(theNewModel, aOptions, aNotifier, theEntry, aConnection);
            }
        } else {
            reverseEngineerRelations(theNewModel, aOptions, aNotifier, null, aConnection);
        }

        theNewModel.setModificationTracker(theOldModificationTracker);
        aNotifier.notifyMessage(ERDesignerBundle.ENGINEERINGFINISHED, "");

        return theNewModel;
    }

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

    public void getDataTypes(Connection aConnection) throws SQLException {
        DatabaseMetaData theMetaData = aConnection.getMetaData();
        ResultSet theResult = theMetaData.getTypeInfo();
        while (theResult.next()) {
            String theTypeName = theResult.getString("TYPE_NAME");
            String thePrefix = theResult.getString("LITERAL_PREFIX");
            String theSuffix = theResult.getString("LITERAL_SUFFIX");
            String theCreateParams = theResult.getString("CREATE_PARAMS");

            System.out.println("registerType(\"" + theTypeName + "\",\"" + thePrefix + "\",\"" + theSuffix + "\",\""
                    + theCreateParams + "\");");
        }
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

            if (isValidTable(theTableName, theTableType)) {
                TableEntry theEntry = new TableEntry(theCatalogName, theSchemaName, theTableName);
                theResult.add(theEntry);
            }
        }
        theTablesResultSet.close();

        return theResult;
    }

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
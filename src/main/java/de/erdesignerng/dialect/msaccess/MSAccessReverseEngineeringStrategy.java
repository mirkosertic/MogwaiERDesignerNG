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
package de.erdesignerng.dialect.msaccess;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;

import de.erdesignerng.dialect.JDBCReverseEngineeringStrategy;
import de.erdesignerng.dialect.ReverseEngineeringNotifier;
import de.erdesignerng.dialect.ReverseEngineeringOptions;
import de.erdesignerng.dialect.TableEntry;
import de.erdesignerng.exception.ElementAlreadyExistsException;
import de.erdesignerng.exception.ElementInvalidNameException;
import de.erdesignerng.exception.ReverseEngineeringException;
import de.erdesignerng.model.CascadeType;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.Relation;
import de.erdesignerng.model.Table;
import de.erdesignerng.model.View;
import de.erdesignerng.modificationtracker.VetoException;

/**
 * @author $Author: dr-death $
 * @version $Date: 2009-11-06 01:30:00 $
 */
public class MSAccessReverseEngineeringStrategy extends JDBCReverseEngineeringStrategy<MSAccessDialect> {

    private static final int OBJECT_TYPE_TABLE = 1;

    private static final String TABLES = "Tables";

    private static final String SPACE = " ";

    private static final String COMMA = "," + SPACE;

    private static final String AS = "AS";

    private static final String ON = "ON";

    private static final String AND = "AND";

    private static final String FROM = "FROM";

    public MSAccessReverseEngineeringStrategy(MSAccessDialect aDialect) {
        super(aDialect);
    }

    // @Override
    // protected String convertColumnTypeToRealType(String aTypeName) {
    // throw new UnsupportedOperationException("Not supported yet.");
    // }

    // @Override
    // protected String convertIndexNameFor(Table aTable, String aIndexName) {
    // throw new UnsupportedOperationException("Not supported yet.");
    // }

    // @Override
    // protected CascadeType getCascadeType(int aValue) {
    // throw new UnsupportedOperationException("Not supported yet.");
    // }

    // @Override
    // protected String[] getReverseEngineeringTableTypes() {
    // throw new UnsupportedOperationException("Not supported yet.");
    // }

    // @Override
    // protected List<TableEntry> getTablesForSchemaEntry(Connection
    // aConnection, SchemaEntry aEntry) throws SQLException {
    // String theQuery = "SELECT MSysObjects.Name " +
    // "FROM MSysObjects LEFT JOIN MSysObjects AS MSysObjects_1 ON MSysObjects.ParentId = MSysObjects_1.Id "
    // +
    // "WHERE ((MSysObjects.Flags = ?) AND (MSysObjects.Type = ?) AND (MSysObjects_1.Name = ?));";
    // List<TableEntry> theList = new ArrayList<TableEntry>();
    //
    // PreparedStatement theStatement = aConnection.prepareStatement(theQuery);
    // theStatement.setInt(1, TABLE_FLAGS_ALL);
    // theStatement.setInt(2, OBJECT_TYPE_TABLE);
    // theStatement.setString(3, TABLES);
    //
    // ResultSet theTablesResultSet = null;
    //
    // try {
    // theTablesResultSet = theStatement.executeQuery();
    //
    // while (theTablesResultSet.next()) {
    // String theTableName = theTablesResultSet.getString("Name");
    //
    // theList.add(new TableEntry(null, null, theTableName, TABLE_TABLE_TYPE));
    // }
    //
    // } finally {
    // if (theTablesResultSet != null) {
    // theTablesResultSet.close();
    // }
    // theStatement.close();
    // }
    //
    // return theList;
    // }

    // @Override
    // protected boolean isValidTable(String aTableName, String aTableType) {
    // throw new UnsupportedOperationException("Not supported yet.");
    // }

    // @Override
    // protected boolean isValidView(String aTableName, String aTableType) {
    // throw new UnsupportedOperationException("Not supported yet.");
    // }

    // @Override
    // protected void reverseEngineerAttribute(Model aModel, Attribute
    // aAttribute, ReverseEngineeringOptions aOptions,
    // ReverseEngineeringNotifier aNotifier, TableEntry aTable, Connection
    // aConnection) throws SQLException {
    // throw new UnsupportedOperationException("Not supported yet.");
    // }

    // @Override
    // protected void reverseEngineerIndexAttribute(DatabaseMetaData aMetaData,
    // TableEntry aTableEntry, Table aTable, ReverseEngineeringNotifier
    // aNotifier, Index aIndex, String aColumnName, short aPosition, String
    // aASCorDESC) throws SQLException, ReverseEngineeringException {
    // throw new UnsupportedOperationException("Not supported yet.");
    // }

    // @Override
    // protected void reverseEngineerIndexes(Model aModel, TableEntry
    // aTableEntry, DatabaseMetaData aMetaData, Table aTable,
    // ReverseEngineeringNotifier aNotifier) throws SQLException,
    // ReverseEngineeringException {
    // throw new UnsupportedOperationException("Not supported yet.");
    // }

    @Override
    protected void reverseEngineerPrimaryKey(Model aModel, TableEntry aTableEntry, DatabaseMetaData aMetaData,
            Table aTable) throws SQLException, ReverseEngineeringException {
        // TODO [dr-death] IMPLEMENT RevEngPK
    }

    @Override
    protected void reverseEngineerRelations(Model aModel, ReverseEngineeringOptions aOptions,
            ReverseEngineeringNotifier aNotifier, TableEntry aTableEntry, Connection aConnection) throws SQLException,
            ReverseEngineeringException {
        String theQuery = "SELECT * " + "FROM MSysRelationships " + "WHERE (szReferencedObject = ?);";

        PreparedStatement theStatement = aConnection.prepareStatement(theQuery);
        theStatement.setString(1, aTableEntry.getTableName());

        ResultSet theRelations = null;

        try {
            theRelations = theStatement.executeQuery();

            while (theRelations.next()) {
                Relation theNewRelation = new Relation();

                theNewRelation.setName(theRelations.getString("szRelationship"));
                theNewRelation.setExportingTable(aModel.getTables().findByName(
                        theRelations.getString("szReferencedObject")));
                theNewRelation.setImportingTable(aModel.getTables().findByName(theRelations.getString("szObject")));

                Integer theNewRelationAttributes = theRelations.getInt("grbit");

                if (containsFlag(theNewRelationAttributes, RelationAttributeEnum.DB_RELATION_DELETE_CASCADE)) {
                    theNewRelation.setOnDelete(CascadeType.CASCADE);
                } else if (containsFlag(theNewRelationAttributes, RelationAttributeEnum.DB_RELATION_DELETE_SET_NULL)) {
                    theNewRelation.setOnDelete(CascadeType.SET_NULL);
                }

                if (containsFlag(theNewRelationAttributes, RelationAttributeEnum.DB_RELATION_UPDATE_CASCADE)) {
                    theNewRelation.setOnUpdate(CascadeType.CASCADE);
                }

                try {
                    aModel.addRelation(theNewRelation);
                } catch (ElementAlreadyExistsException ex) {
                    Logger.getLogger(MSAccessReverseEngineeringStrategy.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ElementInvalidNameException ex) {
                    Logger.getLogger(MSAccessReverseEngineeringStrategy.class.getName()).log(Level.SEVERE, null, ex);
                } catch (VetoException ex) {
                    Logger.getLogger(MSAccessReverseEngineeringStrategy.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        } finally {
            if (theRelations != null) {
                theRelations.close();
            }
            theStatement.close();
        }

    }

    // @Override
    // protected void reverseEngineerView(Model aModel,
    // ReverseEngineeringOptions aOptions, ReverseEngineeringNotifier aNotifier,
    // TableEntry aViewEntry, Connection aConnection) throws SQLException,
    // ReverseEngineeringException {
    // throw new UnsupportedOperationException("Not supported yet.");
    // }

    @Override
    protected String reverseEngineerViewSQL(TableEntry aViewEntry, Connection aConnection, View aView)
            throws SQLException, ReverseEngineeringException {
        String theViewSQL = "";

        QueryFragment theCommand = getSQLQuery(aConnection, aViewEntry.getTableName());
        QueryFragment theFields = getSQLInputFields(aConnection, aViewEntry.getTableName());
        QueryFragment theOptions = getSQLQueryOptions(aConnection, aViewEntry.getTableName());
        QueryFragment theFrom = getSQLFromExpression(aConnection, aViewEntry.getTableName());

        theViewSQL = merge(theCommand.getLeadingSQL(), theOptions.getLeadingSQL(), SPACE);
        theViewSQL = merge(theViewSQL, theFields.getLeadingSQL(), SPACE);
        theViewSQL = merge(theViewSQL, theOptions.getTrailingSQL(), COMMA);

        theViewSQL = merge(theViewSQL, theFrom.getLeadingSQL(), SPACE);

        return theViewSQL;

    }

    /**
     * Checks, if a flag is set in a combination of flags
     * 
     * @param theFlags
     * @param theFlag
     * @return true, if the combination contains the flag false, else
     */
    private boolean containsFlag(int theFlags, int theFlag) {
        return ((theFlags & theFlag) == theFlag);
    }

    /**
     * Merges two strings by forcing a specified separator.
     * 
     * @param aFirstString
     * @param aSecondString
     * @param aSeparator
     * @return merged string
     */
    private String merge(String aFirstString, String aSecondString, String aSeparator) {

        String theSQL = ((aFirstString == null) ? "" : aFirstString);

        if (!(StringUtils.isEmpty(aFirstString)) && !(theSQL.endsWith(aSeparator))
                && !(StringUtils.isEmpty(aSecondString))) {
            theSQL += aSeparator;
        }

        return theSQL + ((aSecondString == null) ? "" : aSecondString);

    }

    /**
     * Returns a ResultSet containing all records that represent a specified
     * attribute of a query.
     * 
     * @param aConnection
     * @param aViewName
     * @param theAttributeID
     * @return ResultSet containing all records that represent a specified
     *         attribute
     * @throws java.sql.SQLException
     */
    private ResultSet getSQLProperties(Connection aConnection, String aViewName, Short theAttributeID)
            throws SQLException {
        String theQuery = "SELECT MSysQueries.* "
                + "FROM MSysQueries LEFT JOIN MSysObjects ON MSysQueries.ObjectId = MSysObjects.Id "
                + "WHERE ((MSysObjects.Name = ?) AND (MSysQueries.Attribute = ?));";

        PreparedStatement theStatement = null;
        ResultSet theQueryProperties = null;

        // try {
        theStatement = aConnection.prepareStatement(theQuery);
        theStatement.setString(1, aViewName);
        theStatement.setShort(2, theAttributeID);

        theQueryProperties = theStatement.executeQuery();

        // theStatement.close();
        // } catch (SQLException ex) {
        // Logger.getLogger(MSAccessReverseEngineeringStrategy.class.getName()).log(Level.SEVERE,
        // null, ex);
        // }

        return theQueryProperties;
    }

    private QueryFragment getSQLQuery(Connection aConnection, String aViewName) throws SQLException {
        int theType = QueryProperties.QueryType.SELECT;
        String theSQL = "";
        String theSQLStart = "";
        String theSQLMid = "";
        String theSQLEnd = "";

        ResultSet theProperties = getSQLProperties(aConnection, aViewName, QueryProperties.QueryType.ID);

        if (theProperties != null && theProperties.next()) {
            theType = theProperties.getInt("Flag");
        }

        switch (theType) {

        case QueryProperties.QueryType.SELECT:
            theSQLStart = "SELECT";
            break;

        case QueryProperties.QueryType.SELECT_INTO:
            theSQLStart = "SELECT";
            theSQLEnd = "INTO";
            break;

        case QueryProperties.QueryType.INSERT_INTO:
            theSQLStart = "INSERT INTO";
            break;

        case QueryProperties.QueryType.UPDATE:
            theSQLStart = "UPDATE";
            break;

        case QueryProperties.QueryType.TRANSFORM:
            theSQLStart = "TRANSFORM";
            break;

        case QueryProperties.QueryType.DDL:
            theSQLStart = theProperties.getString("Expression");
            break;

        case QueryProperties.QueryType.PASS_THROUGH:
            // nothing to do here
            break;

        case QueryProperties.QueryType.UNION:
            theSQLMid = "UNION";
            break;

        default:
            throw new UnsupportedOperationException("Unknown QueryType!");

        }

        if (theProperties != null) {
            theProperties.close();
        }

        // TODO [dr-death] implement theSQLMid UNION
        return new QueryFragment(theType, theSQLStart, theSQLEnd);

    }

    private QueryFragment getSQLQueryOptions(Connection aConnection, String aViewName) throws SQLException {
        int theType = QueryProperties.QueryOptions.DEFAULT;
        String theSQLStart = "";
        String theSQLEnd = "";
        String theSQLAccessOptions = "";

        ResultSet theProperties = getSQLProperties(aConnection, aViewName, QueryProperties.QueryOptions.ID);

        if (theProperties != null && theProperties.next()) {
            theType = theProperties.getInt("Flag");
        }

        if (containsFlag(theType, QueryProperties.QueryOptions.RETURNS_ALL_FIELDS)) {
            theSQLEnd = "*";
        }

        if (containsFlag(theType, QueryProperties.QueryOptions.DISTINCT)) {
            theSQLStart = merge(theSQLStart, "DISTINCT", SPACE);
        }

        if (containsFlag(theType, QueryProperties.QueryOptions.OWNER_ACCESS_OPTION)) {
            theSQLAccessOptions = "WITH OWNER ACCESS OPTION";
        }

        if (containsFlag(theType, QueryProperties.QueryOptions.DISTINCTROW)) {
            theSQLStart = merge(theSQLStart, "DISTINCTROW", SPACE);
        }

        if (containsFlag(theType, QueryProperties.QueryOptions.TOP_COUNT)) {
            theSQLStart = merge(theSQLStart, "TOP", SPACE);
            theSQLStart = merge(theSQLStart, theProperties.getString("Name1"), SPACE);
        }

        if (containsFlag(theType, QueryProperties.QueryOptions.TOP_PERCENT)) {
            theSQLStart = merge(theSQLStart, "PERCENT", SPACE);
        }

        if (theProperties != null) {
            theProperties.close();
        }

        // TODO [dr-death] implement "WITH OWNER ACCESS OPTION"
        return new QueryFragment(theType, theSQLStart, theSQLEnd);
    }

    private QueryFragment getSQLInputFields(Connection aConnection, String aViewName) throws SQLException {
        int theType = QueryProperties.InputFields.DEFAULT;
        String theSQL = "";
        String theField = "";

        ResultSet theProperties = getSQLProperties(aConnection, aViewName, QueryProperties.InputFields.ID);

        while (theProperties != null && theProperties.next()) {
            theField = merge(theProperties.getString("Expression"), AS, SPACE);
            theField = merge(theField, theProperties.getString("Name1"), SPACE);
            theSQL = merge(theSQL, theField, COMMA);
        }

        if (theProperties != null) {
            theProperties.close();
        }

        return new QueryFragment(theType, theSQL);
    }

    private QueryFragment getSQLFromExpression(Connection aConnection, String aViewName) throws SQLException {
        int theType = 0;
        String theSQL = "";
        boolean hasJoins = false;
        String currentFirstTable = "";
        String currentSecondTable = "";
        String previousFirstTable = "";
        String previousSecondTable = "";
        String previousExpression = "";
        String previousConcatenation = null;

        ResultSet theProperties = getSQLProperties(aConnection, aViewName, QueryProperties.JoinTypes.ID);

        while (theProperties != null && theProperties.next()) {
            hasJoins = true;
            previousFirstTable = currentFirstTable;
            previousSecondTable = currentSecondTable;
            currentFirstTable = theProperties.getString("Name1");
            currentSecondTable = theProperties.getString("Name2");

            if ((previousFirstTable.length() > 0 && previousSecondTable.length() > 0)
                    && currentFirstTable.equalsIgnoreCase(previousFirstTable)
                    && currentSecondTable.equalsIgnoreCase(previousSecondTable)) {

                // alte Expression mit AND-Verknüpfung fortsetzen
                if (previousConcatenation != null) {
                    theSQL = merge(theSQL, previousConcatenation, SPACE);
                    theSQL = merge(theSQL, "(" + previousExpression + ")", SPACE);
                }
                previousConcatenation = AND;
                previousExpression = theProperties.getString("Expression");
            } else {
                // neue Expression beginnen
                if (previousConcatenation != null) {
                    theSQL = merge(theSQL, previousConcatenation, SPACE);
                    theSQL = merge(theSQL, (AND.equalsIgnoreCase(previousConcatenation) ? "(" : "")
                            + previousExpression + (AND.equalsIgnoreCase(previousConcatenation) ? ")" : ""), SPACE);
                }

                theSQL = currentFirstTable;

                switch (theProperties.getInt("Flag")) {
                case QueryProperties.JoinTypes.INNER_JOIN:
                    theSQL = merge(theSQL, "INNER JOIN", SPACE);
                    break;

                case QueryProperties.JoinTypes.LEFT_JOIN:
                    theSQL = merge(theSQL, "LEFT JOIN", SPACE);
                    break;

                case QueryProperties.JoinTypes.RIGHT_JOIN:
                    theSQL = merge(theSQL, "RIGHT JOIN", SPACE);
                    break;

                default:
                    throw new UnsupportedOperationException("Unknown JOIN-Type!");

                }

                theSQL = merge(theSQL, currentSecondTable, SPACE);

                previousConcatenation = ON;
                previousExpression = theProperties.getString("Expression");
            }
        }

        if (previousConcatenation != null) {
            theSQL = merge(theSQL, previousConcatenation, SPACE);
            theSQL = merge(theSQL, (AND.equalsIgnoreCase(previousConcatenation) ? "(" : "") + previousExpression
                    + (AND.equalsIgnoreCase(previousConcatenation) ? ")" : ""), SPACE);
        }

        if (!hasJoins) {
            // TODO [dr-death] use Tables (5) for FROM
        }

        if (theProperties != null) {
            theProperties.close();
        }

        theSQL = merge(FROM, theSQL, SPACE);

        return new QueryFragment(theType, theSQL);
    }

}
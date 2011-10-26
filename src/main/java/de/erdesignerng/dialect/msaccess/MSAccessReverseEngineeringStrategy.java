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
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author $Author: dr-death $
 * @version $Date: 2009-11-06 01:30:00 $
 */
public class MSAccessReverseEngineeringStrategy extends JDBCReverseEngineeringStrategy<MSAccessDialect> {

    private static final Logger LOGGER = Logger.getLogger(MSAccessReverseEngineeringStrategy.class);

    private static final int OBJECT_TYPE_TABLE = 1;

    private static final String TABLES = "Tables";

    private static final String SPACE = " ";

    private static final String COMMA = ", ";

    private static final String AS = "AS";

    private static final String ON = "ON";

    private static final String AND = "AND";

    private static final String FROM = "FROM";

    private static final String WHERE = "WHERE";

    private static final String GROUP_BY = "GROUP BY";

    private static final String HAVING = "HAVING";

    public MSAccessReverseEngineeringStrategy(MSAccessDialect aDialect) {
        super(aDialect);
    }

//    @Override
//    protected String convertColumnTypeToRealType(String aTypeName) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }

//    @Override
//    protected String convertIndexNameFor(Table aTable, String aIndexName) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }

//    @Override
//    protected CascadeType getCascadeType(int aValue) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }

//    @Override
//    protected String[] getReverseEngineeringTableTypes() {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }

//    @Override
//    protected boolean isValidTable(String aTableName, String aTableType) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }

//    @Override
//    protected boolean isValidView(String aTableName, String aTableType) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }

//    @Override
//    protected void reverseEngineerAttribute(Model aModel, Attribute aAttribute, ReverseEngineeringOptions aOptions, ReverseEngineeringNotifier aNotifier, TableEntry aTable, Connection aConnection) throws SQLException {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }

//    @Override
//    protected void reverseEngineerIndexAttribute(DatabaseMetaData aMetaData, TableEntry aTableEntry, Table aTable, ReverseEngineeringNotifier aNotifier, Index aIndex, String aColumnName, short aPosition, String aASCorDESC) throws SQLException, ReverseEngineeringException {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }

//    @Override
//    protected void reverseEngineerIndexes(Model aModel, TableEntry aTableEntry, DatabaseMetaData aMetaData, Table aTable, ReverseEngineeringNotifier aNotifier) throws SQLException, ReverseEngineeringException {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }

    @Override
    protected void reverseEngineerPrimaryKey(Model aModel, TableEntry aTableEntry, DatabaseMetaData aMetaData, Table aTable) throws SQLException, ReverseEngineeringException {
        // TODO [dr-death] IMPLEMENT RevEngPK
    }

    @Override
    protected void reverseEngineerRelations(Model aModel, ReverseEngineeringOptions aOptions, ReverseEngineeringNotifier aNotifier, TableEntry aTableEntry, Connection aConnection) throws SQLException, ReverseEngineeringException {
        // TODO [dr-death] manage relations with multiple fields
        String theQuery = "SELECT * " +
                "FROM MSysRelationships " +
                "WHERE (szObject = ?);";

        PreparedStatement theStatement = aConnection.prepareStatement(theQuery);
        theStatement.setString(1, aTableEntry.getTableName());

        ResultSet theRelations = null;

        try {
            theRelations = theStatement.executeQuery();

            while (theRelations.next()) {
                Relation theNewRelation = new Relation();

                theNewRelation.setName(theRelations.getString("szRelationship"));
                theNewRelation.setExportingTable(aModel.getTables().findByName(theRelations.getString("szReferencedObject")));
                theNewRelation.setImportingTable(aModel.getTables().findByName(theRelations.getString("szObject")));

                Integer theNewRelationAttributes = theRelations.getInt("grbit");

                if (containsFlag(theNewRelationAttributes, RelationAttributeEnum.DB_RELATION_DELETE_CASCADE)) {
                    theNewRelation.setOnDelete(CascadeType.CASCADE);
                } else if (containsFlag(theNewRelationAttributes, RelationAttributeEnum.DB_RELATION_DELETE_SET_NULL)) {
                    theNewRelation.setOnDelete(CascadeType.SETNULL);
                }

                if (containsFlag(theNewRelationAttributes, RelationAttributeEnum.DB_RELATION_UPDATE_CASCADE)) {
                    theNewRelation.setOnUpdate(CascadeType.CASCADE);
                }

                try {
                    aModel.addRelation(theNewRelation);
                } catch (ElementAlreadyExistsException ex) {
                    LOGGER.fatal(ex.getMessage());
                } catch (ElementInvalidNameException ex) {
                    LOGGER.fatal(ex.getMessage());
                } catch (VetoException ex) {
                    LOGGER.fatal(ex.getMessage());
                }
            }

        } finally {
            if (theRelations != null) {
                theRelations.close();
            }
            theStatement.close();
        }

    }

//    @Override
//    protected void reverseEngineerView(Model aModel, ReverseEngineeringOptions aOptions, ReverseEngineeringNotifier aNotifier, TableEntry aViewEntry, Connection aConnection) throws SQLException, ReverseEngineeringException {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }

    @Override
    protected String reverseEngineerViewSQL(TableEntry aViewEntry, Connection aConnection, View aView) throws SQLException {
        String theViewSQL;

        QueryFragment theCommand = getSQLQuery(aConnection, aViewEntry.getTableName());
        QueryFragment theFields = getSQLInputFields(aConnection, aViewEntry.getTableName());
        QueryFragment theOptions = getSQLQueryOptions(aConnection, aViewEntry.getTableName());
        QueryFragment theFrom = getSQLFromJoinExpression(aConnection, aViewEntry.getTableName());
        if (StringUtils.isEmpty(theFrom.getLeadingSQL())) {
            theFrom = getSQLFromTables(aConnection, aViewEntry.getTableName());
        }

        QueryFragment theWhere = getSQLWhereExpression(aConnection, aViewEntry.getTableName());
        QueryFragment theGroupBy = getSQLGroupByExpression(aConnection, aViewEntry.getTableName());
        QueryFragment theHaving = new QueryFragment("");
        if (theGroupBy != null) {
            theHaving = getSQLHavingExpression(aConnection, aViewEntry.getTableName());
        }

        theViewSQL = mergeRight("\n" + theCommand.getLeadingSQL(), SPACE, theOptions.getLeadingSQL());
        theViewSQL = mergeRight(theViewSQL, SPACE, theFields.getLeadingSQL());
        if (!theCommand.getLeadingSQL().endsWith(theOptions.getTrailingSQL())) {
            theViewSQL = mergeRight(theViewSQL, COMMA, theOptions.getTrailingSQL());
        }

        theViewSQL = mergeRight(theViewSQL, SPACE, "\n" + theFrom.getLeadingSQL());
        theViewSQL = mergeRight(theViewSQL, SPACE, "\n" + theWhere.getLeadingSQL());
        theViewSQL = mergeRight(theViewSQL, SPACE, "\n" + theGroupBy.getLeadingSQL());
        theViewSQL = mergeRight(theViewSQL, SPACE, "\n" + theHaving.getLeadingSQL());

        return theViewSQL;

    }

    /**
     * Checks, if a flag is set in a combination of flags
     *
     * @param theFlags
     * @param theFlag
     * @return true, if the combination contains the flag
     *         false, else
     */
    private boolean containsFlag(int theFlags, int theFlag) {
        return ((theFlags & theFlag) == theFlag);
    }

    /**
     * Merges a secondary string right to a primary string by ensuring the
     * useage of a specified separator in between. If the primary string is
     * empty <b>the secondary</b> string is returned.
     *
     * @param aPrimaryString
     * @param aSeparator
     * @param aSecondaryString
     * @return merged string
     */
    private String mergeRight(String aPrimaryString, String aSeparator, String aSecondaryString) {
        String theResult = ((aPrimaryString == null) ? "" : aPrimaryString);

        if (!(StringUtils.isEmpty(aPrimaryString)) && !(theResult.endsWith(aSeparator))
                && !(StringUtils.isEmpty(aSecondaryString))) {
            theResult += aSeparator;
        }

        return theResult + ((aSecondaryString == null) ? "" : aSecondaryString);

    }

    /**
     * Merges a secondary string left to a primary string by ensuring the
     * useage of a specified separator in beween. If the primary string is
     * empty <b>an empty</b> string is returned.
     *
     * @param aPrimaryString
     * @param aSeparator
     * @param aSecondaryString
     * @return merged string
     */
    private String mergeLeft(String aPrimaryString, String aSeparator, String aSecondaryString) {
        String thePrimaryString = ((aPrimaryString == null) ? "" : aPrimaryString);
        String theSecondaryString = ((aSecondaryString == null) ? "" : aSecondaryString);
        String theResult = thePrimaryString;

        if (!(StringUtils.isEmpty(thePrimaryString)) && !(StringUtils.isEmpty(theSecondaryString))) {
            if ((thePrimaryString.startsWith(aSeparator))) {
                theResult = theSecondaryString + thePrimaryString;
            } else {
                theResult = theSecondaryString + aSeparator + thePrimaryString;
            }
        }

        return theResult;

    }

    /**
     * Returns a ResultSet containing all records that represent a specified
     * attribute of a query.
     *
     * @param aConnection
     * @param aViewName
     * @param theAttributeID
     * @return ResultSet containing all records that represent a specified attribute
     * @throws java.sql.SQLException
     */
    private ResultSet getSQLProperties(Connection aConnection, String aViewName, Short theAttributeID) throws SQLException {
        String theQuery = "SELECT MSysQueries.* " +
                "FROM MSysQueries LEFT JOIN MSysObjects ON MSysQueries.ObjectId = MSysObjects.Id " +
                "WHERE ((MSysObjects.Name = ?) AND (MSysQueries.Attribute = ?));";

        PreparedStatement theStatement;
        ResultSet theQueryProperties;

        theStatement = aConnection.prepareStatement(theQuery);
        theStatement.setString(1, aViewName);
        theStatement.setShort(2, theAttributeID);

        theQueryProperties = theStatement.executeQuery();

        return theQueryProperties;
    }

    private QueryFragment getSQLQuery(Connection aConnection, String aViewName) throws SQLException {
        int theType = QueryProperties.QueryType.SELECT;
        String theSQLStart = "";
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
                //nothing to do here
                break;

            case QueryProperties.QueryType.UNION:
                break;

            default:
                throw new UnsupportedOperationException("Unknown QueryType!");

        }

        if (theProperties != null) {
            theProperties.close();
        }

        // TODO [dr-death] implement theSQLMid UNION
        return new QueryFragment(theSQLStart, theType, theSQLEnd);

    }

    private QueryFragment getSQLQueryOptions(Connection aConnection, String aViewName) throws SQLException {
        int theType = QueryProperties.QueryOptions.DEFAULT;
        String theSQLStart = "";
        String theSQLEnd = "";

        ResultSet theProperties = getSQLProperties(aConnection, aViewName, QueryProperties.QueryOptions.ID);

        if (theProperties != null && theProperties.next()) {
            theType = theProperties.getInt("Flag");
        }

        if (containsFlag(theType, QueryProperties.QueryOptions.RETURNS_ALL_FIELDS)) {
            theSQLEnd = "*";
        }

        if (containsFlag(theType, QueryProperties.QueryOptions.DISTINCT)) {
            theSQLStart = mergeRight(theSQLStart, SPACE, "DISTINCT");
        }

        if (containsFlag(theType, QueryProperties.QueryOptions.OWNER_ACCESS_OPTION)) {
        }

        if (containsFlag(theType, QueryProperties.QueryOptions.DISTINCTROW)) {
            theSQLStart = mergeRight(theSQLStart, SPACE, "DISTINCTROW");
        }

        if (containsFlag(theType, QueryProperties.QueryOptions.TOP_COUNT)) {
            theSQLStart = mergeRight(theSQLStart, SPACE, "TOP");
            theSQLStart = mergeRight(theSQLStart, SPACE, theProperties.getString("Name1"));
        }

        if (containsFlag(theType, QueryProperties.QueryOptions.TOP_PERCENT)) {
            theSQLStart = mergeRight(theSQLStart, SPACE, "PERCENT");
        }

        if (theProperties != null) {
            theProperties.close();
        }

        // TODO [dr-death] implement "WITH OWNER ACCESS OPTION"
        return new QueryFragment(theSQLStart, theType, theSQLEnd);
    }

    private QueryFragment getSQLInputFields(Connection aConnection, String aViewName) throws SQLException {
        int theType = QueryProperties.InputFields.DEFAULT;
        String theSQL = "";
        String theField;

        ResultSet theProperties = getSQLProperties(aConnection, aViewName, QueryProperties.InputFields.ID);

        while (theProperties != null && theProperties.next()) {
            theField = mergeRight(theProperties.getString("Expression"), SPACE + AS + SPACE, theProperties.getString("Name1"));
            theSQL = mergeRight(theSQL, COMMA, theField);
        }

        if (theProperties != null) {
            theProperties.close();
        }

        return new QueryFragment(theSQL, theType);
    }

    private QueryFragment getSQLFromJoinExpression(Connection aConnection, String aViewName) throws SQLException {
        String theSQL = "";
        String currentFirstTable = "";
        String currentSecondTable = "";
        String previousFirstTable;
        String previousSecondTable;
        String previousExpression = "";
        String previousConcatenation = null;

        ResultSet theProperties = getSQLProperties(aConnection, aViewName, QueryProperties.JoinTypes.ID);

        while (theProperties != null && theProperties.next()) {
            previousFirstTable = currentFirstTable;
            previousSecondTable = currentSecondTable;
            currentFirstTable = theProperties.getString("Name1");
            currentSecondTable = theProperties.getString("Name2");

            if ((previousFirstTable.length() > 0 && previousSecondTable.length() > 0)
                    && currentFirstTable.equalsIgnoreCase(previousFirstTable)
                    && currentSecondTable.equalsIgnoreCase(previousSecondTable)) {

                // alte Expression mit AND-Verknüpfung fortsetzen
                if (previousConcatenation != null) {
                    theSQL = mergeRight(theSQL, SPACE, previousConcatenation);
                    theSQL = mergeRight(theSQL, SPACE, "(" + previousExpression + ")");
                }
                previousConcatenation = AND;
                previousExpression = theProperties.getString("Expression");
            } else {
                // neue Expression beginnen
                if (previousConcatenation != null) {
                    theSQL = mergeRight(theSQL, SPACE, previousConcatenation);
                    theSQL = mergeRight(theSQL, SPACE, (AND.equalsIgnoreCase(previousConcatenation) ? "(" : "") + previousExpression + (AND.equalsIgnoreCase(previousConcatenation) ? ")" : ""));
                }

                theSQL = currentFirstTable;

                switch (theProperties.getInt("Flag")) {
                    case QueryProperties.JoinTypes.INNER_JOIN:
                        theSQL = mergeRight(theSQL, SPACE, "INNER JOIN");
                        break;

                    case QueryProperties.JoinTypes.LEFT_JOIN:
                        theSQL = mergeRight(theSQL, SPACE, "LEFT JOIN");
                        break;

                    case QueryProperties.JoinTypes.RIGHT_JOIN:
                        theSQL = mergeRight(theSQL, SPACE, "RIGHT JOIN");
                        break;

                    default:
                        throw new UnsupportedOperationException("Unknown JOIN-Type!");

                }

                theSQL = mergeRight(theSQL, SPACE, currentSecondTable);

                previousConcatenation = ON;
                previousExpression = theProperties.getString("Expression");
            }
        }

        if (previousConcatenation != null) {
            theSQL = mergeRight(theSQL, SPACE, previousConcatenation);
            theSQL = mergeRight(theSQL, SPACE, (AND.equalsIgnoreCase(previousConcatenation) ? "(" : "") + previousExpression + (AND.equalsIgnoreCase(previousConcatenation) ? ")" : ""));
        }

        if (theProperties != null) {
            theProperties.close();
        }

        theSQL = mergeLeft(theSQL, SPACE, FROM);

        return new QueryFragment(theSQL, null);
    }

    private QueryFragment getSQLFromTables(Connection aConnection, String aViewName) throws SQLException {
        String theSQL = "";
        ResultSet theProperties = getSQLProperties(aConnection, aViewName, QueryProperties.InputTables.ID);

        while (theProperties != null && theProperties.next()) {
            String theFieldWithAlias = mergeRight(theProperties.getString("Name1"), SPACE + AS + SPACE, theProperties.getString("Name2"));
            theSQL = mergeRight(theSQL, COMMA, theFieldWithAlias);
        }

        if (theProperties != null) {
            theProperties.close();
        }

        theSQL = mergeLeft(theSQL, SPACE, FROM);

        return new QueryFragment(theSQL, null);
    }

    private QueryFragment getSQLWhereExpression(Connection aConnection, String aViewName) throws SQLException {

        Integer theType = null;
        String theWhere = "";
        ResultSet theProperties = getSQLProperties(aConnection, aViewName, QueryProperties.WhereExpression.ID);

        if (theProperties != null && theProperties.next()) {
            theWhere = theProperties.getString("Expression");
            theType = theProperties.getInt("Flag");
        }

        if (theProperties != null) {
            theProperties.close();
        }

        theWhere = mergeLeft(theWhere, SPACE, WHERE);

        return new QueryFragment(theWhere, theType);

    }

    private QueryFragment getSQLGroupByExpression(Connection aConnection, String aViewName) throws SQLException {
        String theSQL = "";

        ResultSet theProperties = getSQLProperties(aConnection, aViewName, QueryProperties.GroupByExpression.ID);

        while (theProperties != null && theProperties.next()) {
            theSQL = mergeRight(theSQL, COMMA, theProperties.getString("Expression"));
        }

        if (theProperties != null) {
            theProperties.close();
        }

        theSQL = mergeLeft(theSQL, SPACE, GROUP_BY);

        return new QueryFragment(theSQL);
    }

    private QueryFragment getSQLHavingExpression(Connection aConnection, String aViewName) throws SQLException {
        String theSQL = "";

        ResultSet theProperties = getSQLProperties(aConnection, aViewName, QueryProperties.HavingExpression.ID);

        if (theProperties != null && theProperties.next()) {
            theSQL = theProperties.getString("Expression");
        }

        if (theProperties != null) {
            theProperties.close();
        }

        theSQL = mergeLeft(theSQL, SPACE, HAVING);

        return new QueryFragment(theSQL);
    }

}
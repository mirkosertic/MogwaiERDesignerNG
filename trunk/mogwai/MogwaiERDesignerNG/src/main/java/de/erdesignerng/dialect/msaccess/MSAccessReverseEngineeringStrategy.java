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

import de.erdesignerng.exception.ElementAlreadyExistsException;
import de.erdesignerng.exception.ElementInvalidNameException;
import de.erdesignerng.modificationtracker.VetoException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import de.erdesignerng.dialect.JDBCReverseEngineeringStrategy;
import de.erdesignerng.dialect.ReverseEngineeringNotifier;
import de.erdesignerng.dialect.ReverseEngineeringOptions;
import de.erdesignerng.dialect.TableEntry;
import de.erdesignerng.exception.ReverseEngineeringException;
import de.erdesignerng.model.CascadeType;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.Relation;
import de.erdesignerng.model.Table;
import de.erdesignerng.model.View;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author $Author: dr-death $
 * @version $Date: 2009-11-06 01:30:00 $
 */
public class MSAccessReverseEngineeringStrategy extends JDBCReverseEngineeringStrategy<MSAccessDialect>{

    private static final int OBJECT_TYPE_TABLE = 1;

    private static final String TABLES = "Tables";

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
//    protected List<TableEntry> getTablesForSchemaEntry(Connection aConnection, SchemaEntry aEntry) throws SQLException {
//        String theQuery = "SELECT MSysObjects.Name " +
//                          "FROM MSysObjects LEFT JOIN MSysObjects AS MSysObjects_1 ON MSysObjects.ParentId = MSysObjects_1.Id " +
//                          "WHERE ((MSysObjects.Flags = ?) AND (MSysObjects.Type = ?) AND (MSysObjects_1.Name = ?));";
//        List<TableEntry> theList = new ArrayList<TableEntry>();
//
//        PreparedStatement theStatement = aConnection.prepareStatement(theQuery);
//        theStatement.setInt(1, TABLE_FLAGS_ALL);
//        theStatement.setInt(2, OBJECT_TYPE_TABLE);
//        theStatement.setString(3, TABLES);
//
//        ResultSet theTablesResultSet = null;
//
//        try {
//            theTablesResultSet = theStatement.executeQuery();
//
//            while (theTablesResultSet.next()) {
//                String theTableName = theTablesResultSet.getString("Name");
//
//                theList.add(new TableEntry(null, null, theTableName, TABLE_TABLE_TYPE));
//            }
//
//        } finally {
//            if (theTablesResultSet != null) {
//                theTablesResultSet.close();
//            }
//            theStatement.close();
//        }
//
//        return theList;
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
        String theQuery = "SELECT * " +
                          "FROM MSysRelationships " +
                          "WHERE (szReferencedObject = ?);";

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
                if ((theNewRelationAttributes | RelationAttributeEnum.DB_RELATION_DELETE_CASCADE) == RelationAttributeEnum.DB_RELATION_DELETE_CASCADE) {
                    theNewRelation.setOnDelete(CascadeType.CASCADE);
                } else if ((theNewRelationAttributes | RelationAttributeEnum.DB_RELATION_DELETE_SET_NULL) == RelationAttributeEnum.DB_RELATION_DELETE_SET_NULL) {
                    theNewRelation.setOnDelete(CascadeType.SET_NULL);
                }

                if ((theNewRelationAttributes | RelationAttributeEnum.DB_RELATION_UPDATE_CASCADE ) == RelationAttributeEnum.DB_RELATION_UPDATE_CASCADE) {
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

//    @Override
//    protected void reverseEngineerView(Model aModel, ReverseEngineeringOptions aOptions, ReverseEngineeringNotifier aNotifier, TableEntry aViewEntry, Connection aConnection) throws SQLException, ReverseEngineeringException {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }

    @Override
    protected String reverseEngineerViewSQL(TableEntry aViewEntry, Connection aConnection, View aView) throws SQLException, ReverseEngineeringException {
        String theQuery = "SELECT MSysQueries.* " +
                          "FROM MSysQueries LEFT JOIN MSysObjects ON MSysQueries.ObjectId = MSysObjects.Id " +
                          "WHERE (MSysObjects.Name = ?) " +
                          "ORDER BY MSysQueries.Attribute;";

        String theViewSQL = "SELECT ";
        int theQueryType = QueryProperties.QueryType.DEFAULT;
        Short thePreviousAttribute = null;
        Short theCurrentAttribute = null;

        PreparedStatement theStatement = aConnection.prepareStatement(theQuery);
        theStatement.setString(1, aViewEntry.getTableName());

        ResultSet theQueryDetails = null;

        try {
            theQueryDetails = theStatement.executeQuery();

            while (theQueryDetails.next()) {
                thePreviousAttribute = theCurrentAttribute;
                theCurrentAttribute = theQueryDetails.getShort("Attribute");
                switch (theCurrentAttribute) {

                    /*
                     * CreationType
                     */
                    case QueryProperties.CreationType.ID:
                        // Nothing to do here
                        break;

                    /*
                     * QueryType
                     */
                    case QueryProperties.QueryType.ID:
                        theQueryType = theQueryDetails.getInt("Flag");

                        switch (theQueryType) {

                            case QueryProperties.QueryType.SELECT:
                                theViewSQL = "SELECT ";
                                break;

                            case QueryProperties.QueryType.SELECT_INTO:
                                theViewSQL = "SELECT ";
                                throw new UnsupportedOperationException("SELECT INTO not supportet yet.");

                            case QueryProperties.QueryType.INSERT_INTO:
                                theViewSQL = "INSERT INTO ";
                                throw new UnsupportedOperationException("INSERT INTO not supportet yet.");

                            case QueryProperties.QueryType.UPDATE:
                                theViewSQL = "UPDATE ";
                                throw new UnsupportedOperationException("UPDATE not supportet yet.");

                            case QueryProperties.QueryType.TRANSFORM:
                                theViewSQL = "TRANSFORM ";
                                throw new UnsupportedOperationException("TRANSFORM not supportet yet.");

                            case QueryProperties.QueryType.DDL:
                                theViewSQL = theQueryDetails.getString("Expression");
                                break;

                            case QueryProperties.QueryType.PASS_THROUGH:
                                throw new UnsupportedOperationException("PASS THROUGH not supportet yet.");

                            case QueryProperties.QueryType.UNION:
                                throw new UnsupportedOperationException("UNION not supportet yet.");

                            default:
                                theViewSQL = "SELECT ";

                        }
                        break;
 
                    /*
                     * QueryOptions
                     */
                    case QueryProperties.QueryOptions.ID:
                        break;

                    /*
                     * SourceDatabase
                     */
                    case QueryProperties.SourceDatabase.ID:
                        break;

                    /*
                     * InputTables
                     */
                    case QueryProperties.InputTables.ID:
                        break;

                    /*
                     * InputExpressions
                     */
                    case QueryProperties.Rows.ID:
                        if (QueryProperties.Rows.ID == thePreviousAttribute) {
                            theViewSQL += ", ";
                        }
                        theViewSQL += theQueryDetails.getString("Expression");
                        break;

                    /*
                     * JoinTypes
                     */
                    case QueryProperties.JoinTypes.ID:
                        theViewSQL += "\nFROM " + theQueryDetails.getString("Name1") + " ";

                        switch (theQueryDetails.getInt("Flag")) {
                            case QueryProperties.JoinTypes.INNER_JOIN:
                                theViewSQL += "INNER JOIN ";
                                break;

                            case QueryProperties.JoinTypes.LEFT_JOIN:
                                theViewSQL += "LEFT JOIN ";
                                break;

                            case QueryProperties.JoinTypes.RIGHT_JOIN:
                                theViewSQL += "RIGHT JOIN ";
                                break;
                        }

                        theViewSQL += theQueryDetails.getString("Name2") + " " +
                                      "ON " + theQueryDetails.getString("Expression");
                        break;

                    /*
                     * WhereExpression
                     */
                    case QueryProperties.WhereExpression.ID:
                        break;

                    /*
                     * GroupByExpression
                     */
                    case QueryProperties.GroupByExpression.ID:
                        break;

                    /*
                     * HavingExpression
                     */
                    case QueryProperties.HavingExpression.ID:
                        break;

                    /*
                     * ColumnOrder
                     */
                    case QueryProperties.ColumnOrder.ID:
                        break;

                    /*
                     * EndOfDefinition
                     */
                    case QueryProperties.EndOfDefinition.ID:
                        if (!theViewSQL.endsWith(";")) {
                            theViewSQL += ";";
                        }
                        break;

                    /*
                     * unknown property
                     */
                    default:
                        throw new UnsupportedOperationException("Unknown query property");

                }

            }

        } finally {
            if (theQueryDetails != null) {
                theQueryDetails.close();
            }
            theStatement.close();
        }

        return theViewSQL;
    }

//    @Override
//    public void updateModelFromConnection(Model aModel, ERDesignerWorldConnector aConnector, Connection aConnection, ReverseEngineeringOptions aOptions, ReverseEngineeringNotifier aNotifier) throws SQLException, ReverseEngineeringException {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }

}
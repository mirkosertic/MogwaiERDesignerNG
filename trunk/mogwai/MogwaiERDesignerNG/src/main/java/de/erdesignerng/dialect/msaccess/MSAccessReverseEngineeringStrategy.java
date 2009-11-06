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
import java.sql.SQLException;

import de.erdesignerng.dialect.JDBCReverseEngineeringStrategy;
import de.erdesignerng.dialect.ReverseEngineeringNotifier;
import de.erdesignerng.dialect.ReverseEngineeringOptions;
import de.erdesignerng.dialect.TableEntry;
import de.erdesignerng.exception.ReverseEngineeringException;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.Table;
import de.erdesignerng.model.View;

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
        // TODO [dr-death] IMPLEMENT RevEngRels
    }

//    @Override
//    protected void reverseEngineerView(Model aModel, ReverseEngineeringOptions aOptions, ReverseEngineeringNotifier aNotifier, TableEntry aViewEntry, Connection aConnection) throws SQLException, ReverseEngineeringException {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }

    @Override
    protected String reverseEngineerViewSQL(TableEntry aViewEntry, Connection aConnection, View aView) throws SQLException, ReverseEngineeringException {
        // TODO [dr-death] IMPLEMENT RevEngSQL
        return "SELECT * FROM Tabelle1";
    }

//    @Override
//    public void updateModelFromConnection(Model aModel, ERDesignerWorldConnector aConnector, Connection aConnection, ReverseEngineeringOptions aOptions, ReverseEngineeringNotifier aNotifier) throws SQLException, ReverseEngineeringException {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }

}
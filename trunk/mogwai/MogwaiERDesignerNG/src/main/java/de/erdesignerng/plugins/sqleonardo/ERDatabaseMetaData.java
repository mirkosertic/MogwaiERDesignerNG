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
package de.erdesignerng.plugins.sqleonardo;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.RowIdLifetime;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.Table;
import de.erdesignerng.model.View;

public class ERDatabaseMetaData implements DatabaseMetaData {

    private static final String COLUMN_DEFERRABILITY = "DEFERRABILITY";
    private static final String COLUMN_FK_NAME = "FK_NAME";
    private static final String COLUMN_DELETE_RULE = "DELETE_RULE";
    private static final String COLUMN_UPDATE_RULE = "UPDATE_RULE";
    private static final String COLUMN_FKCOLUMN_NAME = "FKCOLUMN_NAME";
    private static final String COLUMN_FKTABLE_NAME = "FKTABLE_NAME";
    private static final String COLUMN_FKTABLE_SCHEM = "FKTABLE_SCHEM";
    private static final String COLUMN_FKTABLE_CAT = "FKTABLE_CAT";
    private static final String COLUMN_PKCOLUMN_NAME = "PKCOLUMN_NAME";
    private static final String COLUMN_PKTABLE_NAME = "PKTABLE_NAME";
    private static final String COLUMN_PKTABLE_SCHEM = "PKTABLE_SCHEM";
    private static final String COLUMN_PKTABLE_CAT = "PKTABLE_CAT";
    private static final String COLUMN_PK_NAME = "PK_NAME";
    private static final String COLUMN_KEY_SEQ = "KEY_SEQ";
    private static final String COLUMN_SOURCE_DATA_TYPE = "SOURCE_DATA_TYPE";
    private static final String COLUMN_SCOPE_TABLE = "SCOPE_TABLE";
    private static final String COLUMN_SCOPE_SCHEMA = "SCOPE_SCHEMA";
    private static final String COLUMN_SCOPE_CATALOG = "SCOPE_CATALOG";
    private static final String COLUMN_IS_NULLABLE = "IS_NULLABLE";
    private static final String COLUMN_ORDINAL_POSITION = "ORDINAL_POSITION";
    private static final String COLUMN_CHAR_OCTET_LENGTH = "CHAR_OCTET_LENGTH";
    private static final String COLUMN_SQL_DATETIME_SUB = "SQL_DATETIME_SUB";
    private static final String COLUMN_SQL_DATA_TYPE = "SQL_DATA_TYPE";
    private static final String COLUMN_COLUMN_DEF = "COLUMN_DEF";
    private static final String COLUMN_NULLABLE = "NULLABLE";
    private static final String COLUMN_NUM_PREC_RADIX = "NUM_PREC_RADIX";
    private static final String COLUMN_DECIMAL_DIGITS = "DECIMAL_DIGITS";
    private static final String COLUMN_BUFFER_LENGTH = "BUFFER_LENGTH";
    private static final String COLUMN_COLUMN_SIZE = "COLUMN_SIZE";
    private static final String COLUMN_TYPE_NAME = "TYPE_NAME";
    private static final String COLUMN_DATA_TYPE = "DATA_TYPE";
    private static final String COLUMN_COLUMN_NAME = "COLUMN_NAME";
    private static final String COLUMN_REMARKS = "REMARKS";
    private static final String COLUMN_TABLE_NAME = "TABLE_NAME";
    private static final String COLUMN_TABLE_SCHEM = "TABLE_SCHEM";
    private static final String COLUMN_TABLE_CAT = "TABLE_CAT";
    private static final String COLUMN_TABLE_TYPE = "TABLE_TYPE";
    private static final String VIEW_TABLE_TYPE = "View";
    private static final String ENTITY_TABLE_TYPE = "Entity";

    private ERConnection connection;
    private Model ermodel;

    protected ERDatabaseMetaData(ERConnection connection, Model ermodel) {
        this.connection = connection;
        this.ermodel = ermodel;
    }

    public Connection getConnection() throws SQLException {
        return connection;
    }

    public String getIdentifierQuoteString() throws SQLException {
        return " ";
    }

    public int getMaxColumnNameLength() throws SQLException {
        return ermodel.getDialect().getMaxObjectNameLength();
    }

    public ResultSet getTableTypes() throws SQLException {
        ERRowInfo theRowInfo = new ERRowInfo();
        theRowInfo.addField(COLUMN_TABLE_TYPE);
        Map<String, Object> theRow = new HashMap<String, Object>();
        theRow.put(COLUMN_TABLE_TYPE, ENTITY_TABLE_TYPE);
        theRowInfo.addRow(theRow);
        theRow = new HashMap<String, Object>();
        theRow.put(COLUMN_TABLE_TYPE, VIEW_TABLE_TYPE);
        theRowInfo.addRow(theRow);

        return new ERResultSet(theRowInfo);
    }

    /**
     * if ERDesignerNG does not use Schemas leave return null.
     */
    public String getSchemaTerm() throws SQLException {
        return null;
    }

    /**
     * if ERDesignerNG does not use Schemas leave return null.
     */
    public ResultSet getSchemas() throws SQLException {
        return null;
    }

    public ResultSet getTables(String aCatalog, String aSchemaPattern, String aTableNamePattern, String[] aTypes)
            throws SQLException {
        ERRowInfo theRowInfo = new ERRowInfo();
        theRowInfo.addField(COLUMN_TABLE_CAT);
        theRowInfo.addField(COLUMN_TABLE_SCHEM);
        theRowInfo.addField(COLUMN_TABLE_NAME);
        theRowInfo.addField(COLUMN_TABLE_TYPE);
        theRowInfo.addField(COLUMN_REMARKS);

        if ((aTypes == null) || (ArrayUtils.contains(aTypes, ENTITY_TABLE_TYPE))) {
            for (Table theTable : ermodel.getTables()) {
                Map<String, Object> theRow = new HashMap<String, Object>();
                theRow.put(COLUMN_TABLE_NAME, theTable.getName());
                theRow.put(COLUMN_TABLE_TYPE, ENTITY_TABLE_TYPE);
                theRow.put(COLUMN_REMARKS, theTable.getComment());
                theRowInfo.addRow(theRow);
            }
        }
        if ((aTypes == null) || (ArrayUtils.contains(aTypes, VIEW_TABLE_TYPE))) {
            for (View theView : ermodel.getViews()) {
                Map<String, Object> theRow = new HashMap<String, Object>();
                theRow.put(COLUMN_TABLE_NAME, theView.getName());
                theRow.put(COLUMN_TABLE_TYPE, VIEW_TABLE_TYPE);
                theRow.put(COLUMN_REMARKS, theView.getComment());
                theRowInfo.addRow(theRow);
            }
        }

        return new ERResultSet(theRowInfo);
    }

    public ResultSet getColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern)
            throws SQLException {

        ERRowInfo theRowInfo = new ERRowInfo();
        theRowInfo.addField(COLUMN_TABLE_CAT);
        theRowInfo.addField(COLUMN_TABLE_SCHEM);
        theRowInfo.addField(COLUMN_TABLE_NAME);
        theRowInfo.addField(COLUMN_COLUMN_NAME);
        theRowInfo.addField(COLUMN_DATA_TYPE);
        theRowInfo.addField(COLUMN_TYPE_NAME);
        theRowInfo.addField(COLUMN_COLUMN_SIZE);
        theRowInfo.addField(COLUMN_BUFFER_LENGTH);
        theRowInfo.addField(COLUMN_DECIMAL_DIGITS);
        theRowInfo.addField(COLUMN_NUM_PREC_RADIX);
        theRowInfo.addField(COLUMN_NULLABLE);
        theRowInfo.addField(COLUMN_REMARKS);
        theRowInfo.addField(COLUMN_COLUMN_DEF);
        theRowInfo.addField(COLUMN_SQL_DATA_TYPE);
        theRowInfo.addField(COLUMN_SQL_DATETIME_SUB);
        theRowInfo.addField(COLUMN_CHAR_OCTET_LENGTH);
        theRowInfo.addField(COLUMN_ORDINAL_POSITION);
        theRowInfo.addField(COLUMN_IS_NULLABLE);
        theRowInfo.addField(COLUMN_SCOPE_CATALOG);
        theRowInfo.addField(COLUMN_SCOPE_SCHEMA);
        theRowInfo.addField(COLUMN_SCOPE_TABLE);
        theRowInfo.addField(COLUMN_SOURCE_DATA_TYPE);
        
        List<Table> theTables = new ArrayList<Table>();
        if (StringUtils.isEmpty(tableNamePattern)) {
            theTables.addAll(ermodel.getTables());
        } else {
            Table theTable = ermodel.getTables().findByName(tableNamePattern);
            if (theTable != null) {
                theTables.add(theTable);
            }
        }

        for (Table theTable : theTables) {
            int theNumber = 1;
            for (Attribute theAttribute : theTable.getAttributes()) {
                Map<String, Object> theRow = new HashMap<String, Object>();
                theRow.put(COLUMN_TABLE_NAME, theTable.getName());
                theRow.put(COLUMN_COLUMN_NAME, theAttribute.getName());
                theRow.put(COLUMN_TYPE_NAME, theAttribute.getDatatype().getName());
                theRow.put(COLUMN_COLUMN_SIZE, theAttribute.getSize());
                theRow.put(COLUMN_ORDINAL_POSITION, theNumber++);
                theRowInfo.addRow(theRow);
            }
        }

        return new ERResultSet(theRowInfo);
    }

    public ResultSet getPrimaryKeys(String aCatalog, String aSchema, String aTable) throws SQLException {

        ERRowInfo theRowInfo = new ERRowInfo();
        theRowInfo.addField(COLUMN_TABLE_CAT);
        theRowInfo.addField(COLUMN_TABLE_SCHEM);
        theRowInfo.addField(COLUMN_TABLE_NAME);
        theRowInfo.addField(COLUMN_COLUMN_NAME);
        theRowInfo.addField(COLUMN_KEY_SEQ);
        theRowInfo.addField(COLUMN_PK_NAME);

        // Check only for tables, views do not have a primary key

        /*Table theTable = ermodel.getTables().findByName(aTable);
        if (theTable != null) {
            Index theIndex = theTable.getPrimarykey();
            if (theIndex != null) {
                short theNumber = 1;
                for (Attribute  : theIndex.getExpressions()) {
                    Map<String, Object> theRow = new HashMap<String, Object>();
                    theRow.put(COLUMN_TABLE_NAME, theTable.getName());
                    theRow.put(COLUMN_COLUMN_NAME, theAttribute.getName());
                    theRow.put(COLUMN_PK_NAME, theIndex.getName());
                    theRow.put(COLUMN_KEY_SEQ, theNumber++);
                }
            }
        }*/

        return new ERResultSet(theRowInfo);
    }

    public ResultSet getImportedKeys(String aCatalog, String aSchema, String aTable) throws SQLException {

        ERRowInfo theRowInfo = new ERRowInfo();
        theRowInfo.addField(COLUMN_PKTABLE_CAT);
        theRowInfo.addField(COLUMN_PKTABLE_SCHEM);
        theRowInfo.addField(COLUMN_PKTABLE_NAME);
        theRowInfo.addField(COLUMN_PKCOLUMN_NAME);
        theRowInfo.addField(COLUMN_FKTABLE_CAT);
        theRowInfo.addField(COLUMN_FKTABLE_SCHEM);
        theRowInfo.addField(COLUMN_FKTABLE_NAME);
        theRowInfo.addField(COLUMN_FKCOLUMN_NAME);
        theRowInfo.addField(COLUMN_KEY_SEQ);
        theRowInfo.addField(COLUMN_UPDATE_RULE);
        theRowInfo.addField(COLUMN_DELETE_RULE);
        theRowInfo.addField(COLUMN_FK_NAME);
        theRowInfo.addField(COLUMN_PK_NAME);
        theRowInfo.addField(COLUMN_DEFERRABILITY);

        /*Table theTable = ermodel.getTables().findByName(aTable);
        if (theTable != null) {
            for (Relation theRelation : ermodel.getRelations().getForeignKeysFor(theTable)) {

                Index thePrimaryKey = theRelation.getExportingTable().getPrimarykey();
                for (Attribute theAttribute : thePrimaryKey.getAttributes()) {

                    Map<String, Object> theRow = new HashMap<String, Object>();
                    theRow.put(COLUMN_PKTABLE_NAME, theRelation.getExportingTable().getName());
                    theRow.put(COLUMN_PKCOLUMN_NAME, theAttribute.getName());
                    theRow.put(COLUMN_FKTABLE_NAME, theRelation.getImportingTable().getName());
                    theRow.put(COLUMN_FKCOLUMN_NAME, theRelation.getMapping().get(theAttribute).getName());
                    theRow.put(COLUMN_FK_NAME, theRelation.getName());
                    theRow.put(COLUMN_PK_NAME, thePrimaryKey.getName());

                    theRowInfo.addRow(theRow);
                }
            }
        }*/

        return new ERResultSet(theRowInfo);
    }

    public ResultSet getExportedKeys(String aCatalog, String aSchema, String aTable) throws SQLException {

        ERRowInfo theRowInfo = new ERRowInfo();
        theRowInfo.addField(COLUMN_PKTABLE_CAT);
        theRowInfo.addField(COLUMN_PKTABLE_SCHEM);
        theRowInfo.addField(COLUMN_PKTABLE_NAME);
        theRowInfo.addField(COLUMN_PKCOLUMN_NAME);
        theRowInfo.addField(COLUMN_FKTABLE_CAT);
        theRowInfo.addField(COLUMN_FKTABLE_SCHEM);
        theRowInfo.addField(COLUMN_FKTABLE_NAME);
        theRowInfo.addField(COLUMN_FKCOLUMN_NAME);
        theRowInfo.addField(COLUMN_KEY_SEQ);
        theRowInfo.addField(COLUMN_UPDATE_RULE);
        theRowInfo.addField(COLUMN_DELETE_RULE);
        theRowInfo.addField(COLUMN_FK_NAME);
        theRowInfo.addField(COLUMN_PK_NAME);
        theRowInfo.addField(COLUMN_DEFERRABILITY);

        /*Table theTable = ermodel.getTables().findByName(aTable);
        if (theTable != null) {
            for (Relation theRelation : ermodel.getRelations().getExportedKeysFor(theTable)) {

                Index thePrimaryKey = theRelation.getExportingTable().getPrimarykey();
                for (Attribute theAttribute : thePrimaryKey.getAttributes()) {

                    Map<String, Object> theRow = new HashMap<String, Object>();
                    theRow.put(COLUMN_PKTABLE_NAME, theRelation.getExportingTable().getName());
                    theRow.put(COLUMN_PKCOLUMN_NAME, theAttribute.getName());
                    theRow.put(COLUMN_FKTABLE_NAME, theRelation.getImportingTable().getName());
                    theRow.put(COLUMN_FKCOLUMN_NAME, theRelation.getMapping().get(theAttribute).getName());
                    theRow.put(COLUMN_FK_NAME, theRelation.getName());
                    theRow.put(COLUMN_PK_NAME, thePrimaryKey.getName());

                    theRowInfo.addRow(theRow);
                }
            }
        }*/
        
        return new ERResultSet(theRowInfo);
    }

    // ----------------------------------------------------------------------------------------------------------------------
    // method below are not important
    // ----------------------------------------------------------------------------------------------------------------------
    public int getDatabaseMajorVersion() throws SQLException {
        return 0;
    }

    public int getDatabaseMinorVersion() throws SQLException {
        return 0;
    }

    public String getDatabaseProductName() throws SQLException {
        return null;
    }

    public String getDatabaseProductVersion() throws SQLException {
        return null;
    }

    public int getDriverMajorVersion() {
        return 1;
    }

    public int getDriverMinorVersion() {
        return 0;
    }

    public String getDriverName() throws SQLException {
        return null;
    }

    public String getDriverVersion() throws SQLException {
        return null;
    }

    public int getJDBCMajorVersion() throws SQLException {
        return 0;
    }

    public int getJDBCMinorVersion() throws SQLException {
        return 0;
    }

    // ----------------------------------------------------------------------------------------------------------------------
    // method below are not used
    // ----------------------------------------------------------------------------------------------------------------------
    public boolean allProceduresAreCallable() throws SQLException {
        return false;
    }

    public boolean allTablesAreSelectable() throws SQLException {
        return false;
    }

    public boolean dataDefinitionCausesTransactionCommit() throws SQLException {
        return false;
    }

    public boolean dataDefinitionIgnoredInTransactions() throws SQLException {
        return false;
    }

    public boolean deletesAreDetected(int type) throws SQLException {
        return false;
    }

    public boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
        return false;
    }

    public ResultSet getAttributes(String catalog, String schemaPattern, String typeNamePattern,
            String attributeNamePattern) throws SQLException {
        return null;
    }

    public ResultSet getBestRowIdentifier(String catalog, String schema, String table, int scope, boolean nullable)
            throws SQLException {
        return null;
    }

    public String getCatalogSeparator() throws SQLException {
        return null;
    }

    public String getCatalogTerm() throws SQLException {
        return null;
    }

    public ResultSet getCatalogs() throws SQLException {
        return null;
    }

    public ResultSet getColumnPrivileges(String catalog, String schema, String table, String columnNamePattern)
            throws SQLException {
        return null;
    }

    public ResultSet getCrossReference(String primaryCatalog, String primarySchema, String primaryTable,
            String foreignCatalog, String foreignSchema, String foreignTable) throws SQLException {
        return null;
    }

    public int getDefaultTransactionIsolation() throws SQLException {
        return 0;
    }

    public String getExtraNameCharacters() throws SQLException {
        return null;
    }

    public ResultSet getIndexInfo(String catalog, String schema, String table, boolean unique, boolean approximate)
            throws SQLException {
        return null;
    }

    public int getMaxBinaryLiteralLength() throws SQLException {
        return 0;
    }

    public int getMaxCatalogNameLength() throws SQLException {
        return 0;
    }

    public int getMaxCharLiteralLength() throws SQLException {
        return 0;
    }

    public int getMaxColumnsInGroupBy() throws SQLException {
        return 0;
    }

    public int getMaxColumnsInIndex() throws SQLException {
        return 0;
    }

    public int getMaxColumnsInOrderBy() throws SQLException {
        return 0;
    }

    public int getMaxColumnsInSelect() throws SQLException {
        return 0;
    }

    public int getMaxColumnsInTable() throws SQLException {
        return 0;
    }

    public int getMaxConnections() throws SQLException {
        return 0;
    }

    public int getMaxCursorNameLength() throws SQLException {
        return 0;
    }

    public int getMaxIndexLength() throws SQLException {
        return 0;
    }

    public int getMaxProcedureNameLength() throws SQLException {
        return 0;
    }

    public int getMaxRowSize() throws SQLException {
        return 0;
    }

    public int getMaxSchemaNameLength() throws SQLException {
        return 0;
    }

    public int getMaxStatementLength() throws SQLException {
        return 0;
    }

    public int getMaxStatements() throws SQLException {
        return 0;
    }

    public int getMaxTableNameLength() throws SQLException {
        return 0;
    }

    public int getMaxTablesInSelect() throws SQLException {
        return 0;
    }

    public int getMaxUserNameLength() throws SQLException {
        return 0;
    }

    public String getNumericFunctions() throws SQLException {
        return null;
    }

    public ResultSet getProcedureColumns(String catalog, String schemaPattern, String procedureNamePattern,
            String columnNamePattern) throws SQLException {
        return null;
    }

    public String getProcedureTerm() throws SQLException {
        return null;
    }

    public ResultSet getProcedures(String catalog, String schemaPattern, String procedureNamePattern)
            throws SQLException {
        return null;
    }

    public int getResultSetHoldability() throws SQLException {
        return 0;
    }

    public String getSQLKeywords() throws SQLException {
        return null;
    }

    public int getSQLStateType() throws SQLException {
        return 0;
    }

    public String getSearchStringEscape() throws SQLException {
        return null;
    }

    public String getStringFunctions() throws SQLException {
        return null;
    }

    public ResultSet getSuperTables(String catalog, String schemaPattern, String tableNamePattern) throws SQLException {
        return null;
    }

    public ResultSet getSuperTypes(String catalog, String schemaPattern, String typeNamePattern) throws SQLException {
        return null;
    }

    public String getSystemFunctions() throws SQLException {
        return null;
    }

    public ResultSet getTablePrivileges(String catalog, String schemaPattern, String tableNamePattern)
            throws SQLException {
        return null;
    }

    public String getTimeDateFunctions() throws SQLException {
        return null;
    }

    public ResultSet getTypeInfo() throws SQLException {
        return null;
    }

    public ResultSet getUDTs(String catalog, String schemaPattern, String typeNamePattern, int[] types)
            throws SQLException {
        return null;
    }

    public String getURL() throws SQLException {
        return null;
    }

    public String getUserName() throws SQLException {
        return null;
    }

    public ResultSet getVersionColumns(String catalog, String schema, String table) throws SQLException {
        return null;
    }

    public boolean insertsAreDetected(int type) throws SQLException {
        return false;
    }

    public boolean isCatalogAtStart() throws SQLException {
        return false;
    }

    public boolean isReadOnly() throws SQLException {
        return false;
    }

    public boolean locatorsUpdateCopy() throws SQLException {
        return false;
    }

    public boolean nullPlusNonNullIsNull() throws SQLException {
        return false;
    }

    public boolean nullsAreSortedAtEnd() throws SQLException {
        return false;
    }

    public boolean nullsAreSortedAtStart() throws SQLException {
        return false;
    }

    public boolean nullsAreSortedHigh() throws SQLException {
        return false;
    }

    public boolean nullsAreSortedLow() throws SQLException {
        return false;
    }

    public boolean othersDeletesAreVisible(int type) throws SQLException {
        return false;
    }

    public boolean othersInsertsAreVisible(int type) throws SQLException {
        return false;
    }

    public boolean othersUpdatesAreVisible(int type) throws SQLException {
        return false;
    }

    public boolean ownDeletesAreVisible(int type) throws SQLException {
        return false;
    }

    public boolean ownInsertsAreVisible(int type) throws SQLException {
        return false;
    }

    public boolean ownUpdatesAreVisible(int type) throws SQLException {
        return false;
    }

    public boolean storesLowerCaseIdentifiers() throws SQLException {
        return false;
    }

    public boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
        return false;
    }

    public boolean storesMixedCaseIdentifiers() throws SQLException {
        return false;
    }

    public boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
        return false;
    }

    public boolean storesUpperCaseIdentifiers() throws SQLException {
        return false;
    }

    public boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
        return false;
    }

    public boolean supportsANSI92EntryLevelSQL() throws SQLException {
        return false;
    }

    public boolean supportsANSI92FullSQL() throws SQLException {
        return false;
    }

    public boolean supportsANSI92IntermediateSQL() throws SQLException {
        return false;
    }

    public boolean supportsAlterTableWithAddColumn() throws SQLException {
        return false;
    }

    public boolean supportsAlterTableWithDropColumn() throws SQLException {
        return false;
    }

    public boolean supportsBatchUpdates() throws SQLException {
        return false;
    }

    public boolean supportsCatalogsInDataManipulation() throws SQLException {
        return false;
    }

    public boolean supportsCatalogsInIndexDefinitions() throws SQLException {
        return false;
    }

    public boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException {
        return false;
    }

    public boolean supportsCatalogsInProcedureCalls() throws SQLException {
        return false;
    }

    public boolean supportsCatalogsInTableDefinitions() throws SQLException {
        return false;
    }

    public boolean supportsColumnAliasing() throws SQLException {
        return false;
    }

    public boolean supportsConvert() throws SQLException {
        return false;
    }

    public boolean supportsConvert(int fromType, int toType) throws SQLException {
        return false;
    }

    public boolean supportsCoreSQLGrammar() throws SQLException {
        return false;
    }

    public boolean supportsCorrelatedSubqueries() throws SQLException {
        return false;
    }

    public boolean supportsDataDefinitionAndDataManipulationTransactions() throws SQLException {
        return false;
    }

    public boolean supportsDataManipulationTransactionsOnly() throws SQLException {
        return false;
    }

    public boolean supportsDifferentTableCorrelationNames() throws SQLException {
        return false;
    }

    public boolean supportsExpressionsInOrderBy() throws SQLException {
        return false;
    }

    public boolean supportsExtendedSQLGrammar() throws SQLException {
        return false;
    }

    public boolean supportsFullOuterJoins() throws SQLException {
        return false;
    }

    public boolean supportsGetGeneratedKeys() throws SQLException {
        return false;
    }

    public boolean supportsGroupBy() throws SQLException {
        return false;
    }

    public boolean supportsGroupByBeyondSelect() throws SQLException {
        return false;
    }

    public boolean supportsGroupByUnrelated() throws SQLException {
        return false;
    }

    public boolean supportsIntegrityEnhancementFacility() throws SQLException {
        return false;
    }

    public boolean supportsLikeEscapeClause() throws SQLException {
        return false;
    }

    public boolean supportsLimitedOuterJoins() throws SQLException {
        return false;
    }

    public boolean supportsMinimumSQLGrammar() throws SQLException {
        return false;
    }

    public boolean supportsMixedCaseIdentifiers() throws SQLException {
        return false;
    }

    public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
        return false;
    }

    public boolean supportsMultipleOpenResults() throws SQLException {
        return false;
    }

    public boolean supportsMultipleResultSets() throws SQLException {
        return false;
    }

    public boolean supportsMultipleTransactions() throws SQLException {
        return false;
    }

    public boolean supportsNamedParameters() throws SQLException {
        return false;
    }

    public boolean supportsNonNullableColumns() throws SQLException {
        return false;
    }

    public boolean supportsOpenCursorsAcrossCommit() throws SQLException {
        return false;
    }

    public boolean supportsOpenCursorsAcrossRollback() throws SQLException {
        return false;
    }

    public boolean supportsOpenStatementsAcrossCommit() throws SQLException {
        return false;
    }

    public boolean supportsOpenStatementsAcrossRollback() throws SQLException {
        return false;
    }

    public boolean supportsOrderByUnrelated() throws SQLException {
        return false;
    }

    public boolean supportsOuterJoins() throws SQLException {
        return false;
    }

    public boolean supportsPositionedDelete() throws SQLException {
        return false;
    }

    public boolean supportsPositionedUpdate() throws SQLException {
        return false;
    }

    public boolean supportsResultSetConcurrency(int type, int concurrency) throws SQLException {
        return false;
    }

    public boolean supportsResultSetHoldability(int holdability) throws SQLException {
        return false;
    }

    public boolean supportsResultSetType(int type) throws SQLException {
        return false;
    }

    public boolean supportsSavepoints() throws SQLException {
        return false;
    }

    public boolean supportsSchemasInDataManipulation() throws SQLException {
        return false;
    }

    public boolean supportsSchemasInIndexDefinitions() throws SQLException {
        return false;
    }

    public boolean supportsSchemasInPrivilegeDefinitions() throws SQLException {
        return false;
    }

    public boolean supportsSchemasInProcedureCalls() throws SQLException {
        return false;
    }

    public boolean supportsSchemasInTableDefinitions() throws SQLException {
        return false;
    }

    public boolean supportsSelectForUpdate() throws SQLException {
        return false;
    }

    public boolean supportsStatementPooling() throws SQLException {
        return false;
    }

    public boolean supportsStoredProcedures() throws SQLException {
        return false;
    }

    public boolean supportsSubqueriesInComparisons() throws SQLException {
        return false;
    }

    public boolean supportsSubqueriesInExists() throws SQLException {
        return false;
    }

    public boolean supportsSubqueriesInIns() throws SQLException {
        return false;
    }

    public boolean supportsSubqueriesInQuantifieds() throws SQLException {
        return false;
    }

    public boolean supportsTableCorrelationNames() throws SQLException {
        return false;
    }

    public boolean supportsTransactionIsolationLevel(int level) throws SQLException {
        return false;
    }

    public boolean supportsTransactions() throws SQLException {
        return false;
    }

    public boolean supportsUnion() throws SQLException {
        return false;
    }

    public boolean supportsUnionAll() throws SQLException {
        return false;
    }

    public boolean updatesAreDetected(int type) throws SQLException {
        return false;
    }

    public boolean usesLocalFilePerTable() throws SQLException {
        return false;
    }

    public boolean usesLocalFiles() throws SQLException {
        return true;
    }

    public boolean autoCommitFailureClosesAllResultSets() throws SQLException {
        return false;
    }

    public ResultSet getClientInfoProperties() throws SQLException {
        return null;
    }

    public ResultSet getFunctionColumns(String arg0, String arg1, String arg2, String arg3) throws SQLException {
        return null;
    }

    public ResultSet getFunctions(String arg0, String arg1, String arg2) throws SQLException {
        return null;
    }

    public RowIdLifetime getRowIdLifetime() throws SQLException {
        return null;
    }

    public ResultSet getSchemas(String arg0, String arg1) throws SQLException {
        return null;
    }

    public boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException {
        return false;
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }
}
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
import java.sql.SQLException;
import java.util.List;

import de.erdesignerng.exception.ReverseEngineeringException;
import de.erdesignerng.model.CascadeType;
import de.erdesignerng.model.Model;
import de.erdesignerng.visual.common.ERDesignerWorldConnector;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-03-02 16:11:21 $
 * @param <T>
 *            the dialect
 */
public abstract class ReverseEngineeringStrategy<T extends Dialect> {

    protected static final String REMARKS = "REMARKS";

    protected static final String COLUMN_NAME = "COLUMN_NAME";

    protected static final String TYPE_NAME = "TYPE_NAME";

    protected static final String SIZE = "COLUMN_SIZE";

    protected static final String FRACTION = "DECIMAL_DIGITS";

    protected static final String RADIX = "NUM_PREC_RADIX";

    protected static final String NULLABLE = "NULLABLE";

    protected static final String DEFAULT_VALUE = "COLUMN_DEF";

    protected static final String PRIMARY_KEY = "PK_NAME";

    protected static final String INDEX_NAME = "INDEX_NAME";

    protected static final String NON_UNIQUE = "NON_UNIQUE";

    protected static final String ORDINAL_POSITION = "ORDINAL_POSITION";

    protected static final String SORT_ORDER = "ASC_OR_DESC";

    protected static final String FOREIGN_KEY = "FK_NAME";

    protected static final String PRIMARY_TABLE = "PKTABLE_NAME";

    protected static final String PRIMARY_SCHEMA = "PKTABLE_SCHEM";

    protected static final String UPDATE_RULE = "UPDATE_RULE";

    protected static final String DELETE_RULE = "DELETE_RULE";

    protected static final String PRIMARY_COLUMN = "PKCOLUMN_NAME";

    protected static final String FOREIGN_COLUMN = "FKCOLUMN_NAME";

    protected static final String SCHEMA = "TABLE_SCHEM";

    protected static final String CATALOG = "TABLE_CATALOG";

    protected static final String TABLE_TYPE = "TABLE_TYPE";

    protected static final String TABLE_NAME = "TABLE_NAME";

    protected static final String TABLE_TABLE_TYPE = "TABLE";

    protected static final String VIEW_TABLE_TYPE = "VIEW";

    private StringExtractor theRemarksExtractor = new StringExtractor(REMARKS);

    private StringExtractor theColumnNameExtractor = new StringExtractor(COLUMN_NAME);

    private StringExtractor theTypeNameExtractor = new StringExtractor(TYPE_NAME);

    private IntegerExtractor theSizeExtractor = new IntegerExtractor(SIZE);

    private IntegerExtractor theFractionExtractor = new IntegerExtractor(FRACTION);

    private IntegerExtractor theRadixExtractor = new IntegerExtractor(RADIX);

    private IntegerExtractor theNullableExtractor = new IntegerExtractor(NULLABLE);

    private StringExtractor theDefaultValueExtractor = new StringExtractor(DEFAULT_VALUE);

    private StringExtractor thePrimaryKeyExtractor = new StringExtractor(PRIMARY_KEY);

    private StringExtractor theIndexNameExtractor = new StringExtractor(INDEX_NAME);

    private BooleanExtractor theNonUniqueExtractor = new BooleanExtractor(NON_UNIQUE);

    private ShortExtractor thePositionExtractor = new ShortExtractor(ORDINAL_POSITION);

    private StringExtractor theSortOrderExtractor = new StringExtractor(SORT_ORDER);

    private StringExtractor theForeignKeyExtractor = new StringExtractor(FOREIGN_KEY);

    private StringExtractor thePrimaryTableExtractor = new StringExtractor(PRIMARY_TABLE);

    private StringExtractor thePrimarySchemaExtractor = new StringExtractor(PRIMARY_SCHEMA);

    private StringExtractor theUpdateRuleExtractor = new StringExtractor(UPDATE_RULE);

    private StringExtractor theDeleteRuleExtractor = new StringExtractor(DELETE_RULE);

    private StringExtractor thePrimaryColumnExtractor = new StringExtractor(PRIMARY_COLUMN);

    private StringExtractor theForeignColumnExtractor = new StringExtractor(FOREIGN_COLUMN);

    private StringExtractor theSchemaExtractor = new StringExtractor(SCHEMA);

    private StringExtractor theCatalogExtractor = new StringExtractor(CATALOG);

    private StringExtractor theTableTypeExtractor = new StringExtractor(TABLE_TYPE);

    private StringExtractor theTableNameExtractor = new StringExtractor(TABLE_NAME);

    protected T dialect;

    protected ReverseEngineeringStrategy(T aDialect) {
        dialect = aDialect;
    }

    public abstract void updateModelFromConnection(Model aModel, ERDesignerWorldConnector aConnector,
            Connection aConnection, ReverseEngineeringOptions aOptions, ReverseEngineeringNotifier aNotifier)
            throws SQLException, ReverseEngineeringException;

    public abstract List<SchemaEntry> getSchemaEntries(Connection aConnection) throws SQLException;

    /**
     * Convert a JDBC Cascade Type to the Mogwai CascadeType.
     * 
     * Default is CASCADE.
     * 
     * @param aValue
     *            the JDBC type
     * @return the CascadeType
     */
    protected CascadeType getCascadeType(int aValue) {
        switch (aValue) {
            case DatabaseMetaData.importedKeyNoAction:
                return CascadeType.NOTHING;
            case DatabaseMetaData.importedKeySetNull:
                return CascadeType.SET_NULL;
            case DatabaseMetaData.importedKeyCascade:
                return CascadeType.CASCADE;
            case DatabaseMetaData.importedKeyRestrict:
                return CascadeType.RESTRICT;
            default:
                return CascadeType.CASCADE;
        }
    }

    protected String convertColumnTypeToRealType(String aTypeName) {
        return aTypeName;
    }

    public abstract List<TableEntry> getTablesForSchemas(Connection aConnection, List<SchemaEntry> aSchemaEntries) throws SQLException;

    /**
     * Get the list of available table types that shall be reverse engineered.
     * Default is only "TABLE", but can be overridden by subclasses.
     *
     * @return the l
     */
    protected String[] getReverseEngineeringTableTypes() {
        return new String[]{TABLE_TABLE_TYPE, VIEW_TABLE_TYPE};
    }

    /**
     * Test if a table type is a view.
     *
     * @param aTableType
     *            the table type
     * @return true if yes, else false
     */
    protected boolean isTableTypeView(String aTableType) {
        return VIEW_TABLE_TYPE.equals(aTableType);
    }

    protected StringExtractor getCatalogExtractor() {
        return theCatalogExtractor;
    }

    protected void setCatalogExtractor(StringExtractor theCatalogExtractor) {
        this.theCatalogExtractor = theCatalogExtractor;
    }

    protected StringExtractor getColumnNameExtractor() {
        return theColumnNameExtractor;
    }

    protected void setColumnNameExtractor(StringExtractor theColumnNameExtractor) {
        this.theColumnNameExtractor = theColumnNameExtractor;
    }

    protected StringExtractor getDefaultValueExtractor() {
        return theDefaultValueExtractor;
    }

    protected void setDefaultValueExtractor(StringExtractor theDefaultvalueExtractor) {
        this.theDefaultValueExtractor = theDefaultvalueExtractor;
    }

    protected StringExtractor getDeleteRuleExtractor() {
        return theDeleteRuleExtractor;
    }

    protected void setDeleteRuleExtractor(StringExtractor theDeleteRuleExtractor) {
        this.theDeleteRuleExtractor = theDeleteRuleExtractor;
    }

    protected StringExtractor getForeignColumnExtractor() {
        return theForeignColumnExtractor;
    }

    protected void setForeignColumnExtractor(StringExtractor theForeignColumnExtractor) {
        this.theForeignColumnExtractor = theForeignColumnExtractor;
    }

    protected StringExtractor getForeignKeyExtractor() {
        return theForeignKeyExtractor;
    }

    protected void setForeignKeyExtractor(StringExtractor theForeignKeyExtractor) {
        this.theForeignKeyExtractor = theForeignKeyExtractor;
    }

    protected IntegerExtractor getFractionExtractor() {
        return theFractionExtractor;
    }

    protected void setFractionExtractor(IntegerExtractor theFractionExtractor) {
        this.theFractionExtractor = theFractionExtractor;
    }

    protected StringExtractor getIndexNameExtractor() {
        return theIndexNameExtractor;
    }

    protected void setIndexNameExtractor(StringExtractor theIndexNameExtractor) {
        this.theIndexNameExtractor = theIndexNameExtractor;
    }

    protected BooleanExtractor getNonUniqueExtractor() {
        return theNonUniqueExtractor;
    }

    protected void setNonUniqueExtractor(BooleanExtractor theNonUniqueExtractor) {
        this.theNonUniqueExtractor = theNonUniqueExtractor;
    }

    protected IntegerExtractor getNullableExtractor() {
        return theNullableExtractor;
    }

    protected void setNullableExtractor(IntegerExtractor theNullableExtractor) {
        this.theNullableExtractor = theNullableExtractor;
    }

    protected ShortExtractor getPositionExtractor() {
        return thePositionExtractor;
    }

    protected void setPositionExtractor(ShortExtractor thePositionExtractor) {
        this.thePositionExtractor = thePositionExtractor;
    }

    protected StringExtractor getPrimaryColumnExtractor() {
        return thePrimaryColumnExtractor;
    }

    protected void setPrimaryColumnExtractor(StringExtractor thePrimaryColumnExtractor) {
        this.thePrimaryColumnExtractor = thePrimaryColumnExtractor;
    }

    protected StringExtractor getPrimaryKeyExtractor() {
        return thePrimaryKeyExtractor;
    }

    protected void setPrimaryKeyExtractor(StringExtractor thePrimaryKeyExtractor) {
        this.thePrimaryKeyExtractor = thePrimaryKeyExtractor;
    }

    protected StringExtractor getPrimarySchemaExtractor() {
        return thePrimarySchemaExtractor;
    }

    protected void setPrimarySchemaExtractor(StringExtractor thePrimarySchemaExtractor) {
        this.thePrimarySchemaExtractor = thePrimarySchemaExtractor;
    }

    protected StringExtractor getPrimaryTableExtractor() {
        return thePrimaryTableExtractor;
    }

    protected void setPrimaryTableExtractor(StringExtractor thePrimaryTableExtractor) {
        this.thePrimaryTableExtractor = thePrimaryTableExtractor;
    }

    protected IntegerExtractor getRadixExtractor() {
        return theRadixExtractor;
    }

    protected void setRadixExtractor(IntegerExtractor theRadixExtractor) {
        this.theRadixExtractor = theRadixExtractor;
    }

    protected StringExtractor getRemarksExtractor() {
        return theRemarksExtractor;
    }

    protected void setRemarksExtractor(StringExtractor theRemarksExtractor) {
        this.theRemarksExtractor = theRemarksExtractor;
    }

    protected StringExtractor getSchemaExtractor() {
        return theSchemaExtractor;
    }

    protected void setSchemaExtractor(StringExtractor theSchemaExtractor) {
        this.theSchemaExtractor = theSchemaExtractor;
    }

    protected IntegerExtractor getSizeExtractor() {
        return theSizeExtractor;
    }

    protected void setSizeExtractor(IntegerExtractor theSizeExtractor) {
        this.theSizeExtractor = theSizeExtractor;
    }

    protected StringExtractor getSortOrderExtractor() {
        return theSortOrderExtractor;
    }

    protected void setSortOrderExtractor(StringExtractor theSortOrderExtractor) {
        this.theSortOrderExtractor = theSortOrderExtractor;
    }

    protected StringExtractor getTableNameExtractor() {
        return theTableNameExtractor;
    }

    protected void setTableNameExtractor(StringExtractor theTableNameExtractor) {
        this.theTableNameExtractor = theTableNameExtractor;
    }

    protected StringExtractor getTableTypeExtractor() {
        return theTableTypeExtractor;
    }

    protected void setTableTypeExtractor(StringExtractor theTableTypeExtractor) {
        this.theTableTypeExtractor = theTableTypeExtractor;
    }

    protected StringExtractor getTypeNameExtractor() {
        return theTypeNameExtractor;
    }

    protected void setTypeNameExtractor(StringExtractor theTypeNameExtractor) {
        this.theTypeNameExtractor = theTypeNameExtractor;
    }

    protected StringExtractor getUpdateRuleExtractor() {
        return theUpdateRuleExtractor;
    }

    protected void setUpdateRuleExtractor(StringExtractor theUpdateRuleExtractor) {
        this.theUpdateRuleExtractor = theUpdateRuleExtractor;
    }

}
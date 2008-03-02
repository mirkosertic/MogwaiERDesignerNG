package de.erdesignerng.dialect;

/**
 * Descriptor for a table.
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2008-03-02 13:44:51 $
 */
public class TableEntry extends SchemaEntry {

    private String tableName;

    public TableEntry(String aCatalogName, String aSchemaName, String aTableName) {
        super(aCatalogName, aSchemaName);
        tableName = aTableName;
    }

    /**
     * @return the tableName
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * @param tableName
     *            the tableName to set
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public String toString() {
        String theResult = super.toString();
        if (theResult != null) {
            return theResult + " " + tableName;
        }

        return tableName;
    }
}

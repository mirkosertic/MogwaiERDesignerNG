package de.erdesignerng.dialect;

/**
 * Descriptor for a table.
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2008-03-02 12:03:44 $
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
     * @param tableName the tableName to set
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}

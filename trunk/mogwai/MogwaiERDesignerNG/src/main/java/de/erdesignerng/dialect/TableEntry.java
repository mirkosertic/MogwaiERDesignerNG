package de.erdesignerng.dialect;

/**
 * Descriptor for a table.
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2009-02-13 18:47:14 $
 */
public class TableEntry extends SchemaEntry {

    private String tableName;
    private String tableType;

    public TableEntry(String aCatalogName, String aSchemaName, String aTableName, String aTableType) {
        super(aCatalogName, aSchemaName);
        tableName = aTableName;
        tableType = aTableType;
    }

    /**
     * @return the tableName
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * @param tableName
     *                the tableName to set
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * Gibt den Wert des Attributs <code>tableType</code> zurück.
     * 
     * @return Wert des Attributs tableType.
     */
    public String getTableType() {
        return tableType;
    }

    /**
     * Setzt den Wert des Attributs <code>tableType</code>.
     * 
     * @param tableType
     *                Wert für das Attribut tableType.
     */
    public void setTableType(String tableType) {
        this.tableType = tableType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        String theResult = super.toString();
        if (theResult != null) {
            return theResult + " " + tableName;
        }

        return tableName;
    }
}

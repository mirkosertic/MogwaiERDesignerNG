package de.erdesignerng.dialect;

import de.erdesignerng.model.TableType;

/**
 * Descriptor for a table.
 *
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-09 19:07:30 $
 */
public class TableEntry extends SchemaEntry {

	private final String tableName;

	private final TableType tableType;

	public TableEntry(String aCatalogName, String aSchemaName, String aTableName, TableType aTableType) {
		super(aCatalogName, aSchemaName);
		tableName = aTableName;
		tableType = aTableType;
	}

	public String getTableName() {
		return tableName;
	}

	public TableType getTableType() {
		return tableType;
	}

	@Override
	public String toString() {
		String theResult = super.toString();
		if (theResult != null) {
			return theResult + "." + tableName;
		}

		return tableName;
	}
}

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
package de.erdesignerng.dialect.oracle;

import de.erdesignerng.dialect.JDBCReverseEngineeringStrategy;
import de.erdesignerng.dialect.ReverseEngineeringNotifier;
import de.erdesignerng.dialect.SchemaEntry;
import de.erdesignerng.dialect.TableEntry;
import de.erdesignerng.exception.ReverseEngineeringException;
import de.erdesignerng.model.CascadeType;
import de.erdesignerng.model.Index;
import de.erdesignerng.model.Table;
import de.erdesignerng.model.View;
import org.apache.commons.lang.StringUtils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-09 19:07:31 $
 */
public class OracleReverseEngineeringStrategy extends JDBCReverseEngineeringStrategy<OracleDialect> {

	public OracleReverseEngineeringStrategy(OracleDialect aDialect) {
		super(aDialect);
	}

	@Override
	public List<SchemaEntry> getSchemaEntries(Connection aConnection) throws SQLException {

		List<SchemaEntry> theList = new ArrayList<>();

		DatabaseMetaData theMetadata = aConnection.getMetaData();
		ResultSet theResult = theMetadata.getSchemas();

		while (theResult.next()) {
			String theSchemaName = theResult.getString("TABLE_SCHEM");
			String theCatalogName = null;

			theList.add(new SchemaEntry(theCatalogName, theSchemaName));
		}

		return theList;
	}

	@Override
	protected boolean isValidTable(String aTableName) {
		// Check for recycle bin tables
		return (!aTableName.startsWith("BIN$")) && (!aTableName.contains("/"));
	}

	@Override
	protected boolean isValidView(String aViewName) {
		return !aViewName.contains("/") && !aViewName.contains("==");
	}

	@Override
	protected void reverseEngineerIndexAttribute(DatabaseMetaData aMetaData, TableEntry aTableEntry, Table aTable, ReverseEngineeringNotifier aNotifier, Index aIndex, String aColumnName, short aPosition, String aAscOrDesc) throws SQLException, ReverseEngineeringException {

		// This needs only to be checked if it is a function based index
		if (!aColumnName.endsWith("$")) {
			super.reverseEngineerIndexAttribute(aMetaData, aTableEntry, aTable, aNotifier, aIndex, aColumnName,
					aPosition, aAscOrDesc);
			return;
		}

		Connection theConnection = aMetaData.getConnection();
		PreparedStatement theStatement = theConnection.prepareStatement("SELECT * FROM USER_IND_EXPRESSIONS WHERE INDEX_NAME = ? AND TABLE_NAME = ? AND COLUMN_POSITION = ?");
		theStatement.setString(1, aIndex.getOriginalName());
		theStatement.setString(2, aTable.getOriginalName());
		theStatement.setShort(3, aPosition);
		ResultSet theResult = theStatement.executeQuery();
		boolean found = false;
		while (theResult.next()) {
			found = true;
			String theColumnExpression = theResult.getString("COLUMN_EXPRESSION");

			aIndex.getExpressions().addExpressionFor(theColumnExpression);
		}
		theResult.close();
		theStatement.close();
		if (!found) {
			throw new ReverseEngineeringException("Cannot find index column information for " + aColumnName + " index " + aIndex.getName() + " table " + aTable.getName());
		}
	}

	@Override
	protected String reverseEngineerViewSQL(TableEntry aViewEntry, Connection aConnection, View aView)
			throws SQLException {
		PreparedStatement theStatement = aConnection.prepareStatement("SELECT * FROM USER_VIEWS WHERE VIEW_NAME = ?");
		theStatement.setString(1, aViewEntry.getTableName());
		ResultSet theResult = null;
		try {
			theResult = theStatement.executeQuery();
			if (theResult.next()) {
				return theResult.getString("TEXT");
			}
			return null;
		} finally {
			if (theResult != null) {
				theResult.close();
			}
			theStatement.close();
		}
	}

	@Override
	protected CascadeType getCascadeType(int aValue) {
		switch (aValue) {
			case DatabaseMetaData.importedKeyRestrict:
				// Restrict is not supported my this db
				aValue = DatabaseMetaData.importedKeyNoAction;
				break;
		}
		return super.getCascadeType(aValue);
	}

	@Override
	protected String getEscapedPattern(DatabaseMetaData aMetaData, String aValue) throws SQLException {
		if (StringUtils.isEmpty(aValue)) {
			return aValue;
		}
		// Oracle is strange, just use a single / here!
		// The driver is just wrong.
		String thePrefix = "/";
		aValue = aValue.replace("_", thePrefix + "_");
		aValue = aValue.replace("%", thePrefix + "%");
		return aValue;
	}
}
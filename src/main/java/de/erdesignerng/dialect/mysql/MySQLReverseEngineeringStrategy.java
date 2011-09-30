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
package de.erdesignerng.dialect.mysql;

import de.erdesignerng.dialect.JDBCReverseEngineeringStrategy;
import de.erdesignerng.dialect.TableEntry;
import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.Table;
import de.erdesignerng.model.View;
import org.apache.commons.lang.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-09 19:07:30 $
 */
public class MySQLReverseEngineeringStrategy extends JDBCReverseEngineeringStrategy<MySQLDialect> {

	public MySQLReverseEngineeringStrategy(MySQLDialect aDialect) {
		super(aDialect);
	}

	@Override
	protected String convertIndexNameFor(Table aTable, String aIndexName) {
		if ("PRIMARY".equals(aIndexName)) {
			return "PK_" + aTable.getName();
		}
		return super.convertIndexNameFor(aTable, aIndexName);
	}

	@Override
	protected void reverseEngineerAttribute(Attribute<Table> aAttribute,
											TableEntry aEntry, Connection aConnection) throws SQLException {

		// Special treatment for BIT types
		if ("BIT".equals(aAttribute.getDatatype().getName())) {
			String theDefault = aAttribute.getDefaultValue();
			if (!StringUtils.isEmpty(theDefault) && (theDefault.length() == 1)) {
				int theDefaultInt = theDefault.charAt(0);
				aAttribute.setDefaultValue("" + theDefaultInt);
			}
		}

		Statement theStatement = aConnection.createStatement();
		ResultSet theResult = theStatement.executeQuery("DESCRIBE " + aEntry.getTableName());
		while (theResult.next()) {
			String theType = theResult.getString("Type");
			String theColumnName = theResult.getString("Field");
			if (aAttribute.getName().equals(theColumnName)) {
				String theExtra = theResult.getString("Extra");
				if ("AUTO_INCREMENT".equalsIgnoreCase(theExtra)) {
					aAttribute.setExtra("AUTO_INCREMENT PRIMARY KEY");
				}
				if (theType.toLowerCase().startsWith("enum") || theType.toLowerCase().startsWith("set")) {
					int p = theType.indexOf("(");
					int p2 = theType.lastIndexOf(")");
					theExtra = theType.substring(p + 1, p2);
					aAttribute.setExtra(theExtra);
				}
			}
		}
		theResult.close();
		theStatement.close();

		if (aAttribute.getDatatype().isJDBCStringType()) {
			String theDefaultValue = aAttribute.getDefaultValue();
			if (!StringUtils.isEmpty(theDefaultValue)) {
				if (!theDefaultValue.startsWith("'")) {
					theDefaultValue = dialect.getStringSeparatorChars() + theDefaultValue
							+ dialect.getStringSeparatorChars();
					aAttribute.setDefaultValue(theDefaultValue);
				}
			}
		}
	}

	@Override
	protected String reverseEngineerViewSQL(TableEntry aViewEntry, Connection aConnection, View aView)
			throws SQLException {
		PreparedStatement theStatement = aConnection
				.prepareStatement("SELECT * FROM INFORMATION_SCHEMA.VIEWS WHERE TABLE_NAME = ?");
		theStatement.setString(1, aViewEntry.getTableName());
		ResultSet theResult = null;
		try {
			theResult = theStatement.executeQuery();
			if (theResult.next()) {
				String theViewDefinition = theResult.getString("VIEW_DEFINITION");
				int p = theViewDefinition.indexOf("*/");
				if (p > 0) {
					theViewDefinition = theViewDefinition.substring(p + 2);
				}
				return theViewDefinition;
			}
			return null;
		} finally {
			if (theResult != null) {
				theResult.close();
			}
			theStatement.close();
		}
	}
}
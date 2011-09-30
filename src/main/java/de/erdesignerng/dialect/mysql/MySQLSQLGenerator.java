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

import de.erdesignerng.dialect.Statement;
import de.erdesignerng.dialect.StatementList;
import de.erdesignerng.dialect.sql92.SQL92SQLGenerator;
import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.Index;
import de.erdesignerng.model.Relation;
import de.erdesignerng.model.Table;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-06-13 16:48:59 $
 */
public class MySQLSQLGenerator extends SQL92SQLGenerator<MySQLDialect> {

	public MySQLSQLGenerator(MySQLDialect aDialect) {
		super(aDialect);
	}

	@Override
	public StatementList createAddPrimaryKeyToTable(Table aTable, Index aIndex) {
		boolean theHasAutoIncrement = false;
		for (Attribute<Table> theAttribute : aTable.getAttributes()) {
			String theExtra = theAttribute.getExtra();
			if (theExtra != null) {
				if (theExtra.toUpperCase().contains("AUTO_INCREMENT")) {
					theHasAutoIncrement = true;
				}
			}
		}

		if (theHasAutoIncrement) {
			return new StatementList();
		}
		return super.createAddPrimaryKeyToTable(aTable, aIndex);
	}

	@Override
	public StatementList createRenameTableStatement(Table aTable, String aNewName) {

		StatementList theResult = new StatementList();
		StringBuilder theStatement = new StringBuilder();

		theStatement.append("ALTER TABLE ");
		theStatement.append(createUniqueTableName(aTable));
		theStatement.append(" RENAME TO ");

		theStatement.append(aNewName);

		theResult.add(new Statement(theStatement.toString()));

		return theResult;
	}

	@Override
	public StatementList createRemovePrimaryKeyStatement(Table aTable, Index aIndex) {

		boolean theHasAutoIncrement = false;
		for (Attribute<Table> theAttribute : aTable.getAttributes()) {
			String theExtra = theAttribute.getExtra();
			if (theExtra != null) {
				if (theExtra.toUpperCase().contains("AUTO_INCREMENT")) {
					theHasAutoIncrement = true;
				}
			}
		}
		if (theHasAutoIncrement) {
			return new StatementList();
		}

		StatementList theResult = new StatementList();
		StringBuilder theStatement = new StringBuilder();

		theStatement.append("ALTER TABLE ");
		theStatement.append(createUniqueTableName(aTable));
		theStatement.append(" DROP PRIMARY KEY");

		theResult.add(new Statement(theStatement.toString()));

		return theResult;
	}

	@Override
	public StatementList createRenameAttributeStatement(Attribute<Table> anExistingAttribute, String aNewName) {
		Table theTable = anExistingAttribute.getOwner();

		StatementList theResult = new StatementList();
		StringBuilder theStatement = new StringBuilder();

		theStatement.append("ALTER TABLE ");
		theStatement.append(createUniqueTableName(theTable));
		theStatement.append(" CHANGE ");

		theStatement.append(anExistingAttribute.getName());
		theStatement.append(" ");
		theStatement.append(aNewName);
		theStatement.append(" ");
		theStatement.append(anExistingAttribute.getPhysicalDeclaration());
		theStatement.append(" ");

		boolean isNullable = anExistingAttribute.isNullable();

		if (!isNullable) {
			theStatement.append("NOT NULL");
		}

		theResult.add(new Statement(theStatement.toString()));

		return theResult;

	}

	@Override
	public StatementList createChangeAttributeStatement(Attribute<Table> anExistingAttribute, Attribute<Table> aNewAttribute) {
		Table theTable = anExistingAttribute.getOwner();

		StatementList theResult = new StatementList();
		StringBuilder theStatement = new StringBuilder();

		theStatement.append("ALTER TABLE ");
		theStatement.append(createUniqueTableName(theTable));
		theStatement.append(" MODIFY ");

		theStatement.append(anExistingAttribute.getName());
		theStatement.append(" ");
		theStatement.append(createAttributeDataDefinition(aNewAttribute));

		theResult.add(new Statement(theStatement.toString()));

		return theResult;
	}

	@Override
	public StatementList createRemoveRelationStatement(Relation aRelation) {

		Table theImportingTable = aRelation.getImportingTable();

		StatementList theResult = new StatementList();
		theResult.add(new Statement("ALTER TABLE " + createUniqueTableName(theImportingTable) + " DROP FOREIGN KEY "
				+ createUniqueRelationName(aRelation)));
		return theResult;
	}

	@Override
	protected String createCreateTableSuffix(Table aTable) {
		StringBuilder theBuilder = new StringBuilder();
		MySQLTableProperties theProperties = (MySQLTableProperties) getDialect().createTablePropertiesFor(aTable);
		boolean first = true;
		if (theProperties.getEngine() != null) {
			if (first) {
				first = false;
			} else {
				theBuilder.append(",");
			}
			theBuilder.append("ENGINE=");
			switch (theProperties.getEngine()) {
				case InnoDB:
					theBuilder.append("InnoDB");
					break;
				case ARCHIVE:
					theBuilder.append("ARCHIVE");
					break;
				case BDB:
					theBuilder.append("BDB");
					break;
				case BLACKHOLE:
					theBuilder.append("BLACKHOLE");
					break;
				case CSV:
					theBuilder.append("CSV");
					break;
				case EXAMPLE:
					theBuilder.append("EXAMPLE");
					break;
				case FEDERATED:
					theBuilder.append("FEDERATED");
					break;
				case MEMORY:
					theBuilder.append("MEMORY");
					break;
				case MERGE:
					theBuilder.append("MERGE");
					break;
				case MyISAM:
					theBuilder.append("MyISAM");
					break;
				case NDBCLUSTER:
					theBuilder.append("NDBCLUSTER");
					break;
			}
		}
		if (theProperties.getAvgRowLength() != null) {
			if (first) {
				first = false;
			} else {
				theBuilder.append(",");
			}
			theBuilder.append("AVG_ROW_LENGTH=");
			theBuilder.append(theProperties.getAvgRowLength());
		}
		if (theProperties.getCharacterSet() != null) {
			if (first) {
				first = false;
			} else {
				theBuilder.append(",");
			}
			theBuilder.append("CHARACTER SET=");
			theBuilder.append(theProperties.getCharacterSet());
		}
		if (theProperties.getChecksum() != null) {
			if (first) {
				first = false;
			} else {
				theBuilder.append(",");
			}
			theBuilder.append("CHECKSUM=");
			if (Boolean.TRUE.equals(theProperties.getChecksum())) {
				theBuilder.append("1");
			} else {
				theBuilder.append("0");
			}
		}
		if (theProperties.getDelayKeyWrite() != null) {
			if (first) {
				first = false;
			} else {
				theBuilder.append(",");
			}
			theBuilder.append("DELAY_KEY_WRITE=");
			if (Boolean.TRUE.equals(theProperties.getDelayKeyWrite())) {
				theBuilder.append("1");
			} else {
				theBuilder.append("0");
			}
		}
		if (theProperties.getInsertMethod() != null) {
			if (first) {
				first = false;
			} else {
				theBuilder.append(",");
			}
			theBuilder.append("INSERT_METHOD=");
			switch (theProperties.getInsertMethod()) {
				case FIRST:
					theBuilder.append("FIRST");
					break;
				case LAST:
					theBuilder.append("LAST");
					break;
				case NO:
					theBuilder.append("NO");
					break;
			}
		}
		if (theProperties.getMaxRows() != null) {
			if (first) {
				first = false;
			} else {
				theBuilder.append(",");
			}
			theBuilder.append("MAX_ROWS=");
			theBuilder.append(theProperties.getMaxRows());
		}
		if (theProperties.getMinRows() != null) {
			if (first) {
				first = false;
			} else {
				theBuilder.append(",");
			}
			theBuilder.append("MIN_ROWS=");
			theBuilder.append(theProperties.getMinRows());
		}
		if (theProperties.getPackKeys() != null) {
			if (first) {
				first = false;
			} else {
				theBuilder.append(",");
			}
			theBuilder.append("PACK_KEYS=");
			if (Boolean.TRUE.equals(theProperties.getPackKeys())) {
				theBuilder.append("1");
			} else {
				theBuilder.append("0");
			}
		}
		if (theProperties.getRowFormat() != null) {
			if (!first) {
				theBuilder.append(",");
			}
			theBuilder.append("INSERT_METHOD=");
			switch (theProperties.getRowFormat()) {
				case COMPACT:
					theBuilder.append("COMPACT");
					break;
				case COMPRESSED:
					theBuilder.append("COMPRESSED");
					break;
				case DEFAULT:
					theBuilder.append("DEFAULT");
					break;
				case DYNAMIC:
					theBuilder.append("DYNAMIC");
					break;
				case FIXED:
					theBuilder.append("FIXED");
					break;
				case REDUNDANT:
					theBuilder.append("REDUNDANT");
					break;
			}
		}

		if (theBuilder.length() > 0) {
			theBuilder.insert(0, ' ');
		}
		return theBuilder.toString();
	}
}
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

import de.erdesignerng.dialect.Statement;
import de.erdesignerng.dialect.StatementList;
import de.erdesignerng.dialect.sql92.SQL92SQLGenerator;
import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.Index;
import de.erdesignerng.model.Table;
import org.apache.commons.lang.StringUtils;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-13 15:40:33 $
 */
public class OracleSQLGenerator extends SQL92SQLGenerator<OracleDialect> {

	public OracleSQLGenerator(OracleDialect aDialect) {
		super(aDialect);
	}

	@Override
	protected String createAttributeDataDefinition(Attribute<Table> aAttribute) {

		StringBuilder theBuilder = new StringBuilder();
		theBuilder.append(aAttribute.getPhysicalDeclaration());
		boolean isNullable = aAttribute.isNullable();

		String theDefault = aAttribute.getDefaultValue();
		boolean hasDefault = false;
		if (!StringUtils.isEmpty(theDefault)) {
			hasDefault = true;
		}

		if ((!isNullable) && (!hasDefault)) {
			theBuilder.append(" NOT NULL");
		}

		if (hasDefault) {
			theBuilder.append(" DEFAULT ");
			theBuilder.append(theDefault);
		}

		String theExtra = aAttribute.getExtra();
		if (!StringUtils.isEmpty(theExtra) && !aAttribute.getDatatype().supportsExtra()) {
			theBuilder.append(" ");
			theBuilder.append(theExtra);
		}

		return theBuilder.toString();
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
	public StatementList createRenameAttributeStatement(Attribute<Table> anExistingAttribute, String aNewName) {
		Table theTable = anExistingAttribute.getOwner();

		StatementList theResult = new StatementList();
		StringBuilder theStatement = new StringBuilder();

		theStatement.append("ALTER TABLE ");
		theStatement.append(createUniqueTableName(theTable));
		theStatement.append(" RENAME COLUMN ");
		theStatement.append(anExistingAttribute.getName());
		theStatement.append(" TO ");
		theStatement.append(aNewName);

		theResult.add(new Statement(theStatement.toString()));

		return theResult;
	}

	@Override
	public StatementList createRemoveIndexFromTableStatement(Table aTable, Index aIndex) {
		StatementList theResult = new StatementList();
		StringBuilder theStatement = new StringBuilder();

		theStatement.append("DROP INDEX ");
		theStatement.append(aIndex.getName());

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
	protected String createCreateTableSuffix(Table aTable) {
		StringBuilder theResult = new StringBuilder();
		OracleTableProperties theProperties = (OracleTableProperties) getDialect().createTablePropertiesFor(aTable);
		if (!StringUtils.isEmpty(theProperties.getTableSpace())) {
			theResult.append(" TABLESPACE ").append(theProperties.getTableSpace());
		}
		return theResult.toString();
	}

	@Override
	protected String createCreateIndexSuffix(Index aIndex) {
		StringBuilder theResult = new StringBuilder();
		OracleIndexProperties theProperties = (OracleIndexProperties) getDialect().createIndexPropertiesFor(aIndex);
		if (!StringUtils.isEmpty(theProperties.getTableSpace())) {
			theResult.append(" TABLESPACE ").append(theProperties.getTableSpace());
		}
		return theResult.toString();
	}

	@Override
	protected String createCreatePrimaryKeySuffix(Index aIndex) {
		StringBuilder theResult = new StringBuilder();
		OracleIndexProperties theProperties = (OracleIndexProperties) getDialect().createIndexPropertiesFor(aIndex);
		if (!StringUtils.isEmpty(theProperties.getTableSpace())) {
			theResult.append(" TABLESPACE ").append(theProperties.getTableSpace());
		}
		return theResult.toString();
	}
}
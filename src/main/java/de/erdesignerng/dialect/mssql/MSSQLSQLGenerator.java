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
package de.erdesignerng.dialect.mssql;

import de.erdesignerng.dialect.Statement;
import de.erdesignerng.dialect.StatementList;
import de.erdesignerng.dialect.sql92.SQL92SQLGenerator;
import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.Index;
import de.erdesignerng.model.IndexExpression;
import de.erdesignerng.model.Relation;
import de.erdesignerng.model.Table;
import de.erdesignerng.model.View;
import org.apache.commons.lang.StringUtils;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-06-13 16:48:58 $
 */
public class MSSQLSQLGenerator extends SQL92SQLGenerator<MSSQLDialect> {

	public MSSQLSQLGenerator(final MSSQLDialect aDialect) {
		super(aDialect);
	}

	@Override
	public StatementList createRemoveRelationStatement(final Relation aRelation) {
		final Table theImportingTable = aRelation.getImportingTable();

		final StatementList theResult = new StatementList();
		theResult.add(new Statement("ALTER TABLE "
				+ createUniqueTableName(theImportingTable)
				+ " DROP CONSTRAINT " + aRelation.getName()));
		return theResult;
	}

	@Override
	public StatementList createRenameTableStatement(final Table aTable,
                                                    final String aNewName) {

		final StatementList theResult = new StatementList();
		theResult.add(new Statement("EXEC sp_rename '"
				+ createUniqueTableName(aTable) + "' , '" + aNewName + "'"));
		return theResult;

	}

	@Override
	public StatementList createRenameAttributeStatement(final Attribute<Table> anExistingAttribute, final String aNewName) {
		final Table theTable = anExistingAttribute.getOwner();

		final StatementList theResult = new StatementList();
		theResult.add(new Statement("EXEC sp_rename '" + theTable.getName()
				+ "." + anExistingAttribute.getName() + "' , '" + aNewName
				+ "' , 'COLUMN'"));
		return theResult;
	}

	@Override
	public StatementList createChangeAttributeStatement(final Attribute<Table> anExistingAttribute, final Attribute<Table> aNewAttribute) {
		final Table theTable = anExistingAttribute.getOwner();

		final StatementList theResult = new StatementList();
		final StringBuilder theStatement = new StringBuilder();

		theStatement.append("ALTER TABLE ");
		theStatement.append(createUniqueTableName(theTable));
		theStatement.append(" ALTER COLUMN ");

		theStatement.append(anExistingAttribute.getName());
		theStatement.append(" ");
		theStatement.append(createAttributeDataDefinition(aNewAttribute, true));

		theResult.add(new Statement(theStatement.toString()));

		return theResult;
	}

	@Override
	public StatementList createAddIndexToTableStatement(final Table aTable,
                                                        final Index aIndex) {
		final StatementList theResult = new StatementList();
		final StringBuilder theStatement = new StringBuilder();

		theStatement.append("CREATE ");

		switch (aIndex.getIndexType()) {
			case NONUNIQUE:
				break;
			default:
				theStatement.append(aIndex.getIndexType().toString()).append(" ");
				break;
		}

		final MSSQLIndexProperties theProperties = (MSSQLIndexProperties) getDialect()
				.createIndexPropertiesFor(aIndex);
		if (theProperties.getIndexType() != null) {
			switch (theProperties.getIndexType()) {
				case CLUSTERED:
					theStatement.append("CLUSTERED ");
					break;
				case NONCLUSTERED:
					theStatement.append("NONCLUSTERED ");
					break;
			}
		}

		theStatement.append("INDEX ");
		theStatement.append(aIndex.getName());
		theStatement.append(" ON ");
		theStatement.append(createUniqueTableName(aTable));
		theStatement.append(" (");

		for (int i = 0; i < aIndex.getExpressions().size(); i++) {
			final IndexExpression theIndexExpression = aIndex.getExpressions().get(i);

			if (i > 0) {
				theStatement.append(",");
			}

			if (!StringUtils.isEmpty(theIndexExpression.getExpression())) {
				theStatement.append(theIndexExpression.getExpression());
			} else {
				theStatement.append(theIndexExpression.getAttributeRef()
						.getName());
			}
		}

		theStatement.append(")");

		if (!StringUtils.isEmpty(theProperties.getFileGroup())) {
			theStatement.append(" ON ");
			theStatement.append("\"");
			theStatement.append(theProperties.getFileGroup());
			theStatement.append("\"");
		}

		theResult.add(new Statement(theStatement.toString()));

		return theResult;
	}

	@Override
	protected String createCreateTableSuffix(final Table aTable) {
		final MSSQLTableProperties theProperties = (MSSQLTableProperties) getDialect()
				.createTablePropertiesFor(aTable);

		final StringBuilder theStatement = new StringBuilder();
		if (!StringUtils.isEmpty(theProperties.getFileGroup())) {
			theStatement.append(" ON ");
			theStatement.append("\"");
			theStatement.append(theProperties.getFileGroup());
			theStatement.append("\"");
		}

		return theStatement.toString();
	}

	@Override
	public StatementList createAddViewStatement(final View aView) {
		final StatementList theResult = new StatementList();
		final StringBuilder theStatement = new StringBuilder();
		theStatement.append("CREATE VIEW ");
		theStatement.append(createUniqueViewName(aView));

		boolean first = true;
		int counter = 0;
		final MSSQLViewProperties theProperties = (MSSQLViewProperties) getDialect()
				.createViewPropertiesFor(aView);
		if (Boolean.TRUE.equals(theProperties.getEncryption())) {
			first = false;
			theStatement.append("WITH ");
			counter = 0;
			theStatement.append("ENCRYPTION");
			counter++;
		}
		if (Boolean.TRUE.equals(theProperties.getSchemaBinding())) {
			if (first) {
				first = false;
				theStatement.append("WITH ");
				counter = 0;
			} else {
				if (counter > 0) {
					theStatement.append(",");
				}
			}
			theStatement.append("SCHEMABINDING");
			counter++;
		}
		if (Boolean.TRUE.equals(theProperties.getViewMetaData())) {
			if (first) {
				theStatement.append("WITH ");
			} else {
				if (counter > 0) {
					theStatement.append(",");
				}
			}
			theStatement.append("VIEW_METADATA");
		}

		theStatement.append(" AS ");
		theStatement.append(aView.getSql());

		theResult.add(new Statement(theStatement.toString()));
		return theResult;
	}
}
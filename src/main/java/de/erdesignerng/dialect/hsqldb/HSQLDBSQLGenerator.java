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
package de.erdesignerng.dialect.hsqldb;

import de.erdesignerng.dialect.Statement;
import de.erdesignerng.dialect.StatementList;
import de.erdesignerng.dialect.sql92.SQL92SQLGenerator;
import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.Index;
import de.erdesignerng.model.IndexExpression;
import de.erdesignerng.model.Table;
import de.erdesignerng.model.View;
import org.apache.commons.lang.StringUtils;

/**
 * @author $Author: gniddelgesciht $
 * @version $Date: 2008/06/13 16:49:00 $
 */
public class HSQLDBSQLGenerator extends SQL92SQLGenerator<HSQLDBDialect> {

	public HSQLDBSQLGenerator(final HSQLDBDialect aDialect) {
		super(aDialect);
	}

	@Override
	protected void addAdditionalInformationToPreCreateTableStatement(
            final Table aTable, final StringBuilder aStatement) {
	}

	@Override
	public StatementList createAddViewStatement(final View aView) {
		final StatementList theResult = new StatementList();
		final StringBuilder theStatement = new StringBuilder();
		theStatement.append("CREATE ");

		theStatement.append("VIEW ");
		theStatement.append(createUniqueViewName(aView));
		theStatement.append(" AS ");
		theStatement.append(aView.getSql());
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
		theResult.add(new Statement(theStatement.toString()));
		return theResult;
	}

	@Override
	public StatementList createRemoveIndexFromTableStatement(final Table aTable,
                                                             final Index aIndex) {
		final StatementList theResult = new StatementList();
		final StringBuilder theStatement = new StringBuilder();

		theStatement.append("DROP INDEX ");
		theStatement.append(aIndex.getName());

		theResult.add(new Statement(theStatement.toString()));

		return theResult;
	}

	@Override
	public StatementList createRenameTableStatement(final Table aTable,
                                                    final String aNewName) {

		final StatementList theResult = new StatementList();
		final StringBuilder theStatement = new StringBuilder();

		theStatement.append("ALTER TABLE ");
		theStatement.append(createUniqueTableName(aTable));
		theStatement.append(" RENAME TO ");

		theStatement.append(aNewName);

		theResult.add(new Statement(theStatement.toString()));

		return theResult;
	}

	@Override
	public StatementList createRenameAttributeStatement(final Attribute<Table> anExistingAttribute, final String aNewName) {
		final Table theTable = anExistingAttribute.getOwner();

		final StatementList theResult = new StatementList();
		final StringBuilder theStatement = new StringBuilder();

		theStatement.append("ALTER TABLE ");
		theStatement.append(createUniqueTableName(theTable));
		theStatement.append(" ALTER COLUMN ");
		theStatement.append(anExistingAttribute.getName());
		theStatement.append(" RENAME TO ");
		theStatement.append(aNewName);

		theResult.add(new Statement(theStatement.toString()));

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
		theStatement.append(aNewAttribute.getPhysicalDeclaration());

		final boolean isNullable = aNewAttribute.isNullable();

		if (!isNullable) {
			theStatement.append(" NOT NULL");
		} else {
			theStatement.append(" NULL");
		}

		theResult.add(new Statement(theStatement.toString()));

		return theResult;
	}

	@Override
	public StatementList createAddSchemaStatement(final String aSchema) {
		final StatementList theResult = new StatementList();
		final StringBuilder theStatement = new StringBuilder();
		theStatement.append("CREATE SCHEMA ");
		theStatement.append(createUniqueSchemaName(aSchema));
		theStatement.append(" authorization SA");
		theResult.add(new Statement(theStatement.toString()));
		return theResult;
	}
}
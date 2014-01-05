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
package de.erdesignerng.dialect.sql92;

import de.erdesignerng.dialect.Dialect;
import de.erdesignerng.dialect.SQLGenerator;
import de.erdesignerng.dialect.Statement;
import de.erdesignerng.dialect.StatementList;
import de.erdesignerng.model.*;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;

/**
 * @param <T> the dialect
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-13 15:40:33 $
 */
public class SQL92SQLGenerator<T extends Dialect> extends SQLGenerator<T> {

	private static final StatementList EMPTY_STATEMENTLIST = new StatementList();

	protected SQL92SQLGenerator(T aDialect) {
		super(aDialect);
	}

	protected String createAttributeDataDefinition(Attribute<Table> aAttribute) {
		return createAttributeDataDefinition(aAttribute, false);
	}

	protected String createAttributeDataDefinition(Attribute<Table> aAttribute, boolean aIgnoreDefault) {
		StringBuilder theBuilder = new StringBuilder();
		if (aAttribute.getDatatype().isDomain() && getDialect().isSupportsDomains()) {
			theBuilder.append(aAttribute.getDatatype().getName());
		} else {
			theBuilder.append(aAttribute.getPhysicalDeclaration());
		}
		boolean isNullable = aAttribute.isNullable();

		if (!isNullable) {
			theBuilder.append(" NOT NULL");
		}

		if (!aIgnoreDefault) {
			String theDefault = aAttribute.getDefaultValue();
			if (!StringUtils.isEmpty(theDefault)) {
				theBuilder.append(" DEFAULT ");
				theBuilder.append(theDefault);
			}
		}

		String theExtra = aAttribute.getExtra();
		if (!StringUtils.isEmpty(theExtra) && !aAttribute.getDatatype().supportsExtra()) {
			theBuilder.append(" ");
			theBuilder.append(theExtra);
		}

		return theBuilder.toString();
	}

	protected String createCompleteAttributeDefinition(Attribute<Table> aAttribute) {
		StringBuilder theBuilder = new StringBuilder();
		theBuilder.append(createUniqueColumnName(aAttribute));
		theBuilder.append(" ");
		theBuilder.append(createAttributeDataDefinition(aAttribute));
		return theBuilder.toString();
	}

	@Override
	public StatementList createAddAttributeToTableStatement(Table aTable, Attribute<Table> aAttribute) {
		StatementList theResult = new StatementList();
		StringBuilder theStatement = new StringBuilder();

		theStatement.append("ALTER TABLE ");
		theStatement.append(createUniqueTableName(aTable));
		theStatement.append(" ADD ");
		theStatement.append(createCompleteAttributeDefinition(aAttribute));

		theResult.add(new Statement(theStatement.toString()));

		return theResult;
	}

	@Override
	public StatementList createAddIndexToTableStatement(Table aTable, Index aIndex) {
		StatementList theResult = new StatementList();
		StringBuilder theStatement = new StringBuilder();

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
			IndexExpression theIndexExpression = aIndex.getExpressions().get(i);

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
		theStatement.append(createCreateIndexSuffix(aIndex));

		theResult.add(new Statement(theStatement.toString()));

		return theResult;
	}

	@Override
	public StatementList createAddRelationStatement(Relation aRelation) {
		Table theImportingTable = aRelation.getImportingTable();
		Table theExportingTable = aRelation.getExportingTable();

		StatementList theResult = new StatementList();
		StringBuilder theStatement = new StringBuilder("ALTER TABLE ");
		theStatement.append(createUniqueTableName(theImportingTable));
		theStatement.append(" ADD CONSTRAINT ");
		theStatement.append(createUniqueRelationName(aRelation));
		theStatement.append(" FOREIGN KEY (");

		boolean first = true;
		for (Attribute<Table> theAttribute : aRelation.getMapping().values()) {
			if (!first) {
				theStatement.append(",");
			}
			theStatement.append(theAttribute.getName());
			first = false;
		}

		theStatement.append(") REFERENCES ");
		theStatement.append(createUniqueTableName(theExportingTable));
		theStatement.append("(");

		first = true;
		for (IndexExpression theExpression : aRelation.getMapping().keySet()) {
			if (!first) {
				theStatement.append(",");
			}
			if (!StringUtils.isEmpty(theExpression.getExpression())) {
				theStatement.append(theExpression.getExpression());
			} else {
				theStatement.append(theExpression.getAttributeRef().getName());
			}
			first = false;
		}

		theStatement.append(")");

		if (getDialect().isSupportsOnDelete()) {
			switch (aRelation.getOnDelete()) {
				case CASCADE:
					theStatement.append(" ON DELETE CASCADE");
					break;
				case RESTRICT:
					theStatement.append(" ON DELETE RESTRICT");
					break;
				case NOTHING:
					if (!getDialect().isSuppressONALLIfNOACTION()) {
						theStatement.append(" ON DELETE NO ACTION");
					}
					break;
				case SETNULL:
					theStatement.append(" ON DELETE SET NULL");
					break;
				default:
			}
		}

		if (getDialect().isSupportsOnUpdate()) {
			switch (aRelation.getOnUpdate()) {
				case CASCADE:
					theStatement.append(" ON UPDATE CASCADE");
					break;
				case RESTRICT:
					theStatement.append(" ON UPDATE RESTRICT");
					break;
				case NOTHING:
					if (!getDialect().isSuppressONALLIfNOACTION()) {
						theStatement.append(" ON UPDATE NO ACTION");
					}
					break;
				case SETNULL:
					theStatement.append(" ON UPDATE SET NULL");
					break;
				default:
			}
		}

		theResult.add(new Statement(theStatement.toString()));

		return theResult;
	}

	@Override
	public StatementList createChangeIndexStatement(Index anExistingIndex, Index aNewIndex) {
		StatementList theList = new StatementList();
		Table theTable = anExistingIndex.getOwner();

		if (anExistingIndex.getIndexType() == IndexType.PRIMARYKEY) {
			theList.addAll(createRemovePrimaryKeyStatement(theTable, anExistingIndex));
		} else {
			theList.addAll(createRemoveIndexFromTableStatement(theTable, anExistingIndex));
		}

		if (aNewIndex.getIndexType() == IndexType.PRIMARYKEY) {
			theList.addAll(createAddPrimaryKeyToTable(theTable, aNewIndex));
		} else {
			theList.addAll(createAddIndexToTableStatement(theTable, aNewIndex));
		}

		return theList;
	}

	@Override
	public StatementList createChangeRelationStatement(Relation aRelation, Relation aTempRelation) {
		StatementList theList = new StatementList();
		theList.addAll(createRemoveRelationStatement(aRelation));
		theList.addAll(createAddRelationStatement(aTempRelation));

		return theList;
	}

	@Override
	public StatementList createChangeTableCommentStatement(Table aTable, String aNewComment) {
		return EMPTY_STATEMENTLIST;
	}

	@Override
	public StatementList createRemoveAttributeFromTableStatement(Table aTable, Attribute<Table> aAttribute) {
		StatementList theResult = new StatementList();
		theResult.add(new Statement("ALTER TABLE "
				+ createUniqueTableName(aTable) + " DROP COLUMN "
				+ aAttribute.getName()));
		return theResult;
	}

	@Override
	public StatementList createRemoveIndexFromTableStatement(Table aTable, Index aIndex) {
		StatementList theResult = new StatementList();
		StringBuilder theStatement = new StringBuilder();

		theStatement.append("DROP INDEX ");
		theStatement.append(aIndex.getName());
		theStatement.append(" ON ");
		theStatement.append(createUniqueTableName(aTable));

		theResult.add(new Statement(theStatement.toString()));

		return theResult;
	}

	@Override
	public StatementList createRemoveRelationStatement(Relation aRelation) {
		Table theImportingTable = aRelation.getImportingTable();

		StatementList theResult = new StatementList();
		theResult.add(new Statement("ALTER TABLE "
				+ createUniqueTableName(theImportingTable)
				+ " DROP CONSTRAINT " + createUniqueRelationName(aRelation)));
		return theResult;
	}

	@Override
	public StatementList createRemoveTableStatement(Table aTable) {
		StatementList theResult = new StatementList();
		theResult.add(new Statement("DROP TABLE "
				+ createUniqueTableName(aTable)));
		return theResult;
	}

	@Override
	public StatementList createRenameTableStatement(Table aTable, String aNewName) {
		return EMPTY_STATEMENTLIST;
	}

	protected void addAdditionalInformationToPreCreateTableStatement(Table aTable, StringBuilder aStatement) {
	}

	@Override
	public StatementList createAddTableStatement(Table aTable) {
		StatementList theResult = new StatementList();
		StringBuilder theStatement = new StringBuilder();

		theStatement.append("CREATE ");
		addAdditionalInformationToPreCreateTableStatement(aTable, theStatement);

		theStatement.append("TABLE ");
		theStatement.append(createUniqueTableName(aTable));
		theStatement.append(" (");
		theStatement.append(SystemUtils.LINE_SEPARATOR);

		for (int i = 0; i < aTable.getAttributes().size(); i++) {
			Attribute<Table> theAttribute = aTable.getAttributes().get(i);

			theStatement.append(TAB);
			theStatement
					.append(createCompleteAttributeDefinition(theAttribute));

			if (i < aTable.getAttributes().size() - 1) {
				theStatement.append(",");
			}

			theStatement.append(SystemUtils.LINE_SEPARATOR);
		}
		theStatement.append(")");
		theStatement.append(createCreateTableSuffix(aTable));
		theResult.add(new Statement(theStatement.toString()));

		for (Index theIndex : aTable.getIndexes()) {
			if (IndexType.PRIMARYKEY == theIndex.getIndexType()) {
				theResult.addAll(createAddPrimaryKeyToTable(aTable, theIndex));
			} else {
				theResult.addAll(createAddIndexToTableStatement(aTable,
						theIndex));
			}
		}

		return theResult;
	}

	protected String createCreatePrimaryKeySuffix(Index aIndex) {
		return "";
	}


	protected String createCreateIndexSuffix(Index aIndex) {
		return "";
	}

	protected String createCreateTableSuffix(Table aTable) {
		return "";
	}

	@Override
	public StatementList createChangeAttributeStatement(Attribute<Table> anExistingAttribute, Attribute<Table> aNewAttribute) {
		return EMPTY_STATEMENTLIST;
	}

	@Override
	public StatementList createRemovePrimaryKeyStatement(Table aTable, Index aIndex) {
		StatementList theResult = new StatementList();
		StringBuilder theStatement = new StringBuilder();

		theStatement.append("ALTER TABLE ");
		theStatement.append(createUniqueTableName(aTable));
		theStatement.append(" DROP CONSTRAINT ");
		theStatement.append(aIndex.getName());

		theResult.add(new Statement(theStatement.toString()));

		return theResult;
	}

	@Override
	public StatementList createAddPrimaryKeyToTable(Table aTable, Index aIndex) {
		StatementList theResult = new StatementList();
		StringBuilder theStatement = new StringBuilder("ALTER TABLE ");

		theStatement.append(createUniqueTableName(aTable));
		theStatement.append(" ADD CONSTRAINT ");
		theStatement.append(aIndex.getName());
		theStatement.append(" PRIMARY KEY(");

		for (int i = 0; i < aIndex.getExpressions().size(); i++) {
			if (i > 0) {
				theStatement.append(",");
			}
			IndexExpression theExpression = aIndex.getExpressions().get(i);
			if (!StringUtils.isEmpty(theExpression.getExpression())) {
				theStatement.append(theExpression.getExpression());
			} else {
				theStatement.append(theExpression.getAttributeRef().getName());
			}
		}
		theStatement.append(")");
		theStatement.append(createCreatePrimaryKeySuffix(aIndex));
		theResult.add(new Statement(theStatement.toString()));

		return theResult;
	}

	@Override
	public StatementList createRenameAttributeStatement(Attribute<Table> anExistingAttribute, String aNewName) {
		return EMPTY_STATEMENTLIST;
	}

	@Override
	public StatementList createAddViewStatement(View aView) {
		StatementList theResult = new StatementList();
		StringBuilder theStatement = new StringBuilder();
		theStatement.append("CREATE VIEW ");
		theStatement.append(createUniqueViewName(aView));
		theStatement.append(" AS ");
		theStatement.append(aView.getSql());
		theResult.add(new Statement(theStatement.toString()));
		return theResult;
	}

	@Override
	public StatementList createChangeViewStatement(View aView) {
		StatementList theResult = new StatementList();
		theResult.addAll(createDropViewStatement(aView));
		theResult.addAll(createAddViewStatement(aView));
		return theResult;
	}

	@Override
	public StatementList createDropViewStatement(View aView) {
		StatementList theResult = new StatementList();
		StringBuilder theStatement = new StringBuilder();
		theStatement.append("DROP VIEW ");
		theStatement.append(createUniqueViewName(aView));
		theResult.add(new Statement(theStatement.toString()));
		return theResult;
	}

	@Override
	public StatementList createAddSchemaStatement(String aSchema) {
		StatementList theResult = new StatementList();
		StringBuilder theStatement = new StringBuilder();
		theStatement.append("CREATE SCHEMA ");
		theStatement.append(createUniqueSchemaName(aSchema));
		theResult.add(new Statement(theStatement.toString()));
		return theResult;
	}

	@Override
	public StatementList createAddDomainStatement(Domain aDomain) {
		if (getDialect().isSupportsDomains()) {
			StatementList theList = new StatementList();
			StringBuilder theBuilder = new StringBuilder();
			theBuilder.append("CREATE DOMAIN ");
			theBuilder.append(aDomain.getName());
			theBuilder.append(" ");
			theBuilder.append(aDomain.createTypeDefinitionFor(new Attribute<Table>()));
			if (!aDomain.isNullable()) {
				theBuilder.append(" NOT NULL");
			}
			theList.add(new Statement(theBuilder.toString()));
			return theList;
		}
		return EMPTY_STATEMENTLIST;
	}

	@Override
	public StatementList createDropDomainStatement(Domain aDomain) {
		if (getDialect().isSupportsDomains()) {
			StatementList theList = new StatementList();
			StringBuilder theBuilder = new StringBuilder();
			theBuilder.append("DROP DOMAIN ");
			theBuilder.append(aDomain.getName());
			theList.add(new Statement(theBuilder.toString()));
			return theList;
		}
		return EMPTY_STATEMENTLIST;
	}

	@Override
	public StatementList createAddCustomTypeStatement(CustomType aCustomType) {
		if (getDialect().isSupportsCustomTypes()) {
			StatementList theList = new StatementList();
			StringBuilder theBuilder = new StringBuilder();
			theBuilder.append("CREATE TYPE ");
			if (aCustomType.getAlias() != null) {
				theBuilder.append(aCustomType.getAlias());
			} else {
				theBuilder.append(aCustomType.getName());
			}
			theBuilder.append(" AS ");
			theBuilder.append(aCustomType.getSqlDefinition());
			theList.add(new Statement(theBuilder.toString()));

			return theList;
		}

		return EMPTY_STATEMENTLIST;
	}

	@Override
	public StatementList createDropCustomTypeStatement(CustomType aCustomType) {
		if (getDialect().isSupportsCustomTypes()) {
			StatementList theList = new StatementList();
			StringBuilder theBuilder = new StringBuilder();
			theBuilder.append("DROP TYPE ");
			if (aCustomType.getAlias() != null) {
				theBuilder.append(aCustomType.getAlias());
			} else {
				theBuilder.append(aCustomType.getName());
			}
			theList.add(new Statement(theBuilder.toString()));
			return theList;
		}

		return EMPTY_STATEMENTLIST;
	}

	@Override
	public String createSelectAllScriptFor(Table aTable, Map<Attribute<Table>, Object> aWhereValues) {
		StringBuilder theBuilder = new StringBuilder("SELECT * FROM ");
		theBuilder = theBuilder.append(createUniqueTableName(aTable));

		if (aWhereValues.size() > 0) {
			theBuilder.append(" WHERE ");
			boolean first = true;
			for (Map.Entry<Attribute<Table>, Object> theEntry : aWhereValues
					.entrySet()) {
				if (!first) {
					theBuilder.append(" AND ");
				}

				theBuilder.append(theEntry.getKey().getName());
				if (theEntry.getValue() != null) {
					theBuilder.append(" = ");
					if (theEntry.getKey().getDatatype().isJDBCStringType()) {
						theBuilder.append("'");
						theBuilder.append(theEntry.getValue());
						theBuilder.append("'");
					} else {
						theBuilder.append(theEntry.getValue());
					}
				} else {
					theBuilder.append(" IS NULL");
				}

				first = false;
			}
		}

		return theBuilder.toString();
	}

	@Override
	public String createSelectAllScriptFor(View aView) {
		return "SELECT * FROM " + createUniqueViewName(aView);
	}
}
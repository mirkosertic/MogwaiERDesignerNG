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

import org.apache.commons.lang.StringUtils;

import de.erdesignerng.PlattformConfig;
import de.erdesignerng.dialect.SQLGenerator;
import de.erdesignerng.dialect.Statement;
import de.erdesignerng.dialect.StatementList;
import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.Index;
import de.erdesignerng.model.IndexExpression;
import de.erdesignerng.model.IndexType;
import de.erdesignerng.model.Relation;
import de.erdesignerng.model.Table;
import de.erdesignerng.model.View;
import de.erdesignerng.modificationtracker.VetoException;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-13 15:40:33 $
 * @param <T>
 *            the dialect
 */
public class SQL92SQLGenerator<T extends SQL92Dialect> extends SQLGenerator<T> {

    private static final StatementList EMPTY_STATEMENTLIST = new StatementList();

    protected SQL92SQLGenerator(T aDialect) {
        super(aDialect);
    }

    protected String createAttributeDataDefinition(Attribute aAttribute) {
        return createAttributeDataDefinition(aAttribute, false);
    }

    protected String createAttributeDataDefinition(Attribute aAttribute, boolean aIgnoreDefault) {

        StringBuilder theBuilder = new StringBuilder();
        theBuilder.append(aAttribute.getPhysicalDeclaration());
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
        if (!StringUtils.isEmpty(theExtra)) {
            theBuilder.append(" ");
            theBuilder.append(theExtra);
        }

        return theBuilder.toString();
    }

    protected String createCompleteAttributeDefinition(Attribute aAttribute) {
        StringBuilder theBuilder = new StringBuilder();
        theBuilder.append(createUniqueColumnName(aAttribute));
        theBuilder.append(" ");
        theBuilder.append(createAttributeDataDefinition(aAttribute));
        return theBuilder.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StatementList createAddAttributeToTableStatement(Table aTable, Attribute aAttribute) throws VetoException {
        StatementList theResult = new StatementList();
        StringBuilder theStatement = new StringBuilder();

        theStatement.append("ALTER TABLE " + createUniqueTableName(aTable) + " ADD ");
        theStatement.append(createCompleteAttributeDefinition(aAttribute));

        theResult.add(new Statement(theStatement.toString()));

        return theResult;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StatementList createAddIndexToTableStatement(Table aTable, Index aIndex) throws VetoException {
        StatementList theResult = new StatementList();
        StringBuilder theStatement = new StringBuilder();

        theStatement.append("CREATE ");

        if (IndexType.UNIQUE.equals(aIndex.getIndexType())) {
            theStatement.append("UNIQUE ");
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
                theStatement.append(theIndexExpression.getAttributeRef().getName());
            }
        }

        theStatement.append(")");

        theResult.add(new Statement(theStatement.toString()));

        return theResult;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StatementList createAddRelationStatement(Relation aRelation) throws VetoException {
        Table theImportingTable = aRelation.getImportingTable();
        Table theExportingTable = aRelation.getExportingTable();

        StatementList theResult = new StatementList();
        StringBuilder theStatement = new StringBuilder("ALTER TABLE ");
        theStatement.append(createUniqueTableName(theImportingTable));
        theStatement.append(" ADD CONSTRAINT ");
        theStatement.append(createUniqueRelationName(aRelation));
        theStatement.append(" FOREIGN KEY (");

        boolean first = true;
        for (Attribute theAttribute : aRelation.getMapping().values()) {
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
                theStatement.append(" ON DELETE NO ACTION");
                break;
            case SET_NULL:
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
                theStatement.append(" ON UPDATE NO ACTION");
                break;
            case SET_NULL:
                theStatement.append(" ON UPDATE SET NULL");
                break;
            default:
            }
        }

        theResult.add(new Statement(theStatement.toString()));

        return theResult;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StatementList createChangeIndexStatement(Index aExistantIndex, Index aNewIndex) throws VetoException {
        StatementList theList = new StatementList();
        Table theTable = aExistantIndex.getOwner();
        if (aExistantIndex.getIndexType().equals(IndexType.PRIMARYKEY)) {
            theList.addAll(createRemovePrimaryKeyStatement(theTable, aExistantIndex));
        } else {
            theList.addAll(createRemoveIndexFromTableStatement(theTable, aExistantIndex));
        }
        if (aNewIndex.getIndexType().equals(IndexType.PRIMARYKEY)) {
            theList.addAll(createAddPrimaryKeyToTable(theTable, aNewIndex));
        } else {
            theList.addAll(createAddIndexToTableStatement(theTable, aNewIndex));
        }
        return theList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StatementList createChangeRelationStatement(Relation aRelation, Relation aTempRelation) throws VetoException {
        StatementList theList = new StatementList();
        theList.addAll(createRemoveRelationStatement(aRelation));
        theList.addAll(createAddRelationStatement(aTempRelation));
        return theList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StatementList createChangeTableCommentStatement(Table aTable, String aNewComment) throws VetoException {
        return EMPTY_STATEMENTLIST;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StatementList createRemoveAttributeFromTableStatement(Table aTable, Attribute aAttribute)
            throws VetoException {
        StatementList theResult = new StatementList();
        theResult.add(new Statement("ALTER TABLE " + createUniqueTableName(aTable) + " DROP COLUMN "
                + aAttribute.getName()));
        return theResult;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StatementList createRemoveIndexFromTableStatement(Table aTable, Index aIndex) throws VetoException {
        StatementList theResult = new StatementList();
        StringBuilder theStatement = new StringBuilder();

        theStatement.append("DROP INDEX ");
        theStatement.append(aIndex.getName());
        theStatement.append(" ON ");
        theStatement.append(createUniqueTableName(aTable));

        theResult.add(new Statement(theStatement.toString()));

        return theResult;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StatementList createRemoveRelationStatement(Relation aRelation) throws VetoException {

        Table theImportingTable = aRelation.getImportingTable();

        StatementList theResult = new StatementList();
        theResult.add(new Statement("ALTER TABLE " + createUniqueTableName(theImportingTable) + " DROP CONSTRAINT "
                + createUniqueRelationName(aRelation)));
        return theResult;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StatementList createRemoveTableStatement(Table aTable) throws VetoException {
        StatementList theResult = new StatementList();
        theResult.add(new Statement("DROP TABLE " + createUniqueTableName(aTable)));
        return theResult;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StatementList createRenameTableStatement(Table aTable, String aNewName) throws VetoException {
        return EMPTY_STATEMENTLIST;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StatementList createAddTableStatement(Table aTable) throws VetoException {

        StatementList theResult = new StatementList();
        StringBuilder theStatement = new StringBuilder();

        theStatement
                .append("CREATE TABLE " + createUniqueTableName(aTable) + " (" + PlattformConfig.getLineSeparator());
        for (int i = 0; i < aTable.getAttributes().size(); i++) {
            Attribute theAttribute = aTable.getAttributes().get(i);

            theStatement.append(TAB);
            theStatement.append(createCompleteAttributeDefinition(theAttribute));

            if (i < aTable.getAttributes().size() - 1) {
                theStatement.append(",");
            }

            theStatement.append(PlattformConfig.getLineSeparator());
        }
        theStatement.append(")");
        theStatement.append(createCreateTableSuffix(aTable));
        theResult.add(new Statement(theStatement.toString()));

        for (Index theIndex : aTable.getIndexes()) {
            if (IndexType.PRIMARYKEY.equals(theIndex.getIndexType())) {
                theResult.addAll(createAddPrimaryKeyToTable(aTable, theIndex));
            } else {
                theResult.addAll(createAddIndexToTableStatement(aTable, theIndex));
            }
        }

        return theResult;
    }

    protected String createCreateTableSuffix(Table aTable) {
        return "";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StatementList createChangeAttributeStatement(Attribute aExistantAttribute, Attribute aNewAttribute)
            throws VetoException {
        return EMPTY_STATEMENTLIST;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StatementList createRemovePrimaryKeyStatement(Table aTable, Index aIndex) throws VetoException {
        StatementList theResult = new StatementList();
        StringBuilder theStatement = new StringBuilder();

        theStatement.append("ALTER TABLE " + createUniqueTableName(aTable) + " DROP CONSTRAINT " + aIndex.getName());

        theResult.add(new Statement(theStatement.toString()));

        return theResult;
    }

    /**
     * {@inheritDoc}
     */
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
        theResult.add(new Statement(theStatement.toString()));

        return theResult;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StatementList createRenameAttributeStatement(Attribute aExistantAttribute, String aNewName)
            throws VetoException {
        return EMPTY_STATEMENTLIST;
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public StatementList createChangeViewStatement(View aView) {
        StatementList theResult = new StatementList();
        theResult.addAll(createDropViewStatement(aView));
        theResult.addAll(createAddViewStatement(aView));
        return theResult;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StatementList createDropViewStatement(View aView) {
        StatementList theResult = new StatementList();
        StringBuilder theStatement = new StringBuilder();
        theStatement.append("DROP VIEW ");
        theStatement.append(createUniqueViewName(aView));
        theResult.add(new Statement(theStatement.toString()));
        return theResult;
    }
}
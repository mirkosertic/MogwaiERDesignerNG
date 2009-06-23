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
package de.erdesignerng.dialect;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.Index;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.Relation;
import de.erdesignerng.model.Table;
import de.erdesignerng.model.View;
import de.erdesignerng.modificationtracker.VetoException;

/**
 * Base class for all SQL generators.
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-09 19:07:30 $
 * @param <T>
 *            the dialect
 */
public abstract class SQLGenerator<T extends Dialect> {

    public static final String TAB = "    ";

    private T dialect;

    public SQLGenerator(T aDialect) {
        dialect = aDialect;
    }

    /**
     * Gibt den Wert des Attributs <code>dialect</code> zur�ck.
     * 
     * @return Wert des Attributs dialect.
     */
    public T getDialect() {
        return dialect;
    }

    protected String getSchemaSeparator() {
        return ".";
    }

    protected String createUniqueTableName(Table aTable) {
        String theSchema = aTable.getSchema();
        if (!StringUtils.isEmpty(theSchema)) {
            return theSchema + getSchemaSeparator() + aTable.getName();
        }
        return aTable.getName();
    }

    protected String createUniqueViewName(View aView) {
        String theSchema = aView.getSchema();
        if (!StringUtils.isEmpty(theSchema)) {
            return theSchema + getSchemaSeparator() + aView.getName();
        }
        return aView.getName();
    }

    protected String createUniqueSchemaName(String aSchema) {
        return aSchema;
    }

    protected String createUniqueColumnName(Attribute aAttribute) {
        return aAttribute.getName();
    }

    protected String createUniqueRelationName(Relation aRelation) {
        return aRelation.getName();
    }

    public abstract StatementList createRemoveRelationStatement(Relation aRelation) throws VetoException;

    public abstract StatementList createRemoveTableStatement(Table aTable) throws VetoException;

    public abstract StatementList createAddAttributeToTableStatement(Table aTable, Attribute aAttribute)
            throws VetoException;

    public abstract StatementList createAddIndexToTableStatement(Table aTable, Index aIndex) throws VetoException;

    public abstract StatementList createAddRelationStatement(Relation aRelation) throws VetoException;

    public abstract StatementList createAddTableStatement(Table aTable) throws VetoException;

    public abstract StatementList createChangeAttributeStatement(Attribute aExistantAttribute, Attribute aNewAttribute)
            throws VetoException;

    public abstract StatementList createChangeIndexStatement(Index aExistantIndex, Index aNewIndex)
            throws VetoException;

    public abstract StatementList createChangeRelationStatement(Relation aRelation, Relation aTempRelation)
            throws VetoException;

    public abstract StatementList createChangeTableCommentStatement(Table aTable, String aNewComment)
            throws VetoException;

    public abstract StatementList createRemoveAttributeFromTableStatement(Table aTable, Attribute aAttribute)
            throws VetoException;

    public abstract StatementList createRemoveIndexFromTableStatement(Table aTable, Index aIndex) throws VetoException;

    public abstract StatementList createRenameTableStatement(Table aTable, String aNewName) throws VetoException;

    public abstract StatementList createRenameAttributeStatement(Attribute aExistantAttribute, String aNewName)
            throws VetoException;

    public abstract StatementList createRemovePrimaryKeyStatement(Table table, Index index) throws VetoException;

    public abstract StatementList createAddPrimaryKeyToTable(Table aTable, Index aIndex);

    /**
     * Create the DDL script for the whole model.
     * 
     * @param aModel
     *            the model
     * @return the lists of statements
     * @throws VetoException
     *             can be thrown if someone has a veto on execution
     */
    public StatementList createCreateAllObjects(Model aModel) throws VetoException {

        StatementList theResult = new StatementList();

        List<String> theSchemas = aModel.getUsedSchemas();
        for (String theSchema : theSchemas) {
            theResult.addAll(createAddSchemaStatement(theSchema));
        }
        for (Table theTable : aModel.getTables()) {
            theResult.addAll(createAddTableStatement(theTable));
        }
        for (View theView : aModel.getViews()) {
            theResult.addAll(createAddViewStatement(theView));
        }
        for (Relation theRelation : aModel.getRelations()) {
            theResult.addAll(createAddRelationStatement(theRelation));
        }

        return theResult;
    }

    public String createScriptStatementSeparator() {
        return ";";
    }

    public abstract StatementList createAddViewStatement(View aView) throws VetoException;

    public abstract StatementList createChangeViewStatement(View aView) throws VetoException;

    public abstract StatementList createDropViewStatement(View aView) throws VetoException;

    public abstract StatementList createAddSchemaStatement(String aSchema) throws VetoException;
}
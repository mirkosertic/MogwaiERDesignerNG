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
import de.erdesignerng.model.CustomType;
import de.erdesignerng.model.Domain;
import de.erdesignerng.model.Index;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.Relation;
import de.erdesignerng.model.Table;
import de.erdesignerng.model.View;

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

    private final T dialect;

    public SQLGenerator(T aDialect) {
        dialect = aDialect;
    }

    /**
     * Gibt den Wert des Attributs <code>dialect</code> zurück.
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

    public abstract StatementList createRemoveRelationStatement(Relation aRelation);

    public abstract StatementList createRemoveTableStatement(Table aTable);

    public abstract StatementList createAddAttributeToTableStatement(Table aTable, Attribute aAttribute);

    public abstract StatementList createAddIndexToTableStatement(Table aTable, Index aIndex);

    public abstract StatementList createAddRelationStatement(Relation aRelation);

    public abstract StatementList createAddTableStatement(Table aTable);

    public abstract StatementList createChangeAttributeStatement(Attribute aExistantAttribute, Attribute aNewAttribute);

    public abstract StatementList createChangeIndexStatement(Index aExistantIndex, Index aNewIndex);

    public abstract StatementList createChangeRelationStatement(Relation aRelation, Relation aTempRelation);

    public abstract StatementList createChangeTableCommentStatement(Table aTable, String aNewComment);

    public abstract StatementList createRemoveAttributeFromTableStatement(Table aTable, Attribute aAttribute);

    public abstract StatementList createRemoveIndexFromTableStatement(Table aTable, Index aIndex);

    public abstract StatementList createRenameTableStatement(Table aTable, String aNewName);

    public abstract StatementList createRenameAttributeStatement(Attribute aExistantAttribute, String aNewName);

    public abstract StatementList createRemovePrimaryKeyStatement(Table table, Index index);

    public abstract StatementList createAddPrimaryKeyToTable(Table aTable, Index aIndex);

    public abstract StatementList createAddDomainStatement(Domain aDomain);

    public abstract StatementList createDropDomainStatement(Domain aDomain);

    public abstract StatementList createAddCustomTypeStatement(CustomType aCustomType);

    public abstract StatementList createDropCustomTypeStatement(CustomType aCustomType);
    /**
     * Create the DDL script for the whole model.
     * 
     * @param aModel
     *            the model
     * @return the lists of statements
     */
    public StatementList createCreateAllObjects(Model aModel) {

        StatementList theResult = new StatementList();

        List<String> theSchemas = aModel.getUsedSchemas();
        for (String theSchema : theSchemas) {
            theResult.addAll(createAddSchemaStatement(theSchema));
        }
        for (Domain theDomain : aModel.getDomains()) {
            theResult.addAll(createAddDomainStatement(theDomain));
        }
        for (CustomType theCustomType : aModel.getCustomTypes()) {
            theResult.addAll(createAddCustomTypeStatement(theCustomType));
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

    public abstract StatementList createAddViewStatement(View aView);

    public abstract StatementList createChangeViewStatement(View aView);

    public abstract StatementList createDropViewStatement(View aView);

    public abstract StatementList createAddSchemaStatement(String aSchema);
}
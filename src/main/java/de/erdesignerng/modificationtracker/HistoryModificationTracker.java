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
package de.erdesignerng.modificationtracker;

import de.erdesignerng.dialect.SQLGenerator;
import de.erdesignerng.dialect.StatementList;
import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.Index;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.Relation;
import de.erdesignerng.model.Table;

public class HistoryModificationTracker implements ModelModificationTracker {

    private Model model;

    public HistoryModificationTracker(Model aModel) {
        model = aModel;
    }

    protected SQLGenerator getSQLGenerator() {
        return model.getDialect().createSQLGenerator();
    }

    protected void addStatementsToHistory(StatementList aStatement) throws VetoException {

    }

    public void addAttributeToTable(Table aTable, Attribute aAttribute) throws VetoException {
        addStatementsToHistory(getSQLGenerator().createAddAttributeToTableStatement(aTable, aAttribute));
    }

    public void addIndexToTable(Table aTable, Index aIndex) throws VetoException {
        addStatementsToHistory(getSQLGenerator().createAddIndexToTableStatement(aTable, aIndex));
    }

    public void addRelation(Relation aRelation) throws VetoException {
        addStatementsToHistory(getSQLGenerator().createAddRelationStatement(aRelation));
    }

    public void addTable(Table aTable) throws VetoException {
        addStatementsToHistory(getSQLGenerator().createAddTableStatement(aTable));
    }

    public void changeAttribute(Attribute aExistantAttribute, Attribute aNewAttribute) throws VetoException {
        addStatementsToHistory(getSQLGenerator().createChangeAttributeStatement(aExistantAttribute, aNewAttribute));
    }

    public void changeIndex(Index aExistantIndex, Index aNewIndex) throws VetoException {
        addStatementsToHistory(getSQLGenerator().createChangeIndexStatement(aExistantIndex, aNewIndex));
    }

    public void changeRelation(Relation aRelation, Relation aTempRelation) throws VetoException {
        addStatementsToHistory(getSQLGenerator().createChangeRelationStatement(aRelation, aTempRelation));
    }

    public void changeTableComment(Table aTable, String aNewComment) throws VetoException {
        addStatementsToHistory(getSQLGenerator().createChangeTableCommentStatement(aTable, aNewComment));
    }

    public void removeAttributeFromTable(Table aTable, String aSystemId) throws VetoException {
        addStatementsToHistory(getSQLGenerator().createRemoveAttributeFromTableStatement(aTable, aSystemId));
    }

    public void removeIndexFromTable(Table aTable, String aSystemId) throws VetoException {
        addStatementsToHistory(getSQLGenerator().createRemoveIndexFromTableStatement(aTable, aSystemId));
    }

    public void removeRelation(Relation aRelation) throws VetoException {
        addStatementsToHistory(getSQLGenerator().createRemoveRelationStatement(aRelation));
    }

    public void removeTable(Table aTable) throws VetoException {
        addStatementsToHistory(getSQLGenerator().createRemoveTableStatement(aTable));
    }

    public void renameAttribute(Attribute aExistantAttribute, String aNewName) throws VetoException {
        addStatementsToHistory(getSQLGenerator().createRenameAttributeStatement(aExistantAttribute, aNewName));
    }

    public void renameRelation(Relation aRelation, String aNewName) throws VetoException {
        addStatementsToHistory(getSQLGenerator().createRenameRelationStatement(aRelation, aNewName));
    }

    public void renameTable(Table aTable, String aNewName) throws VetoException {
        addStatementsToHistory(getSQLGenerator().createRenameTableStatement(aTable, aNewName));
    }
}
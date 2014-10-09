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
import de.erdesignerng.model.CustomType;
import de.erdesignerng.model.Domain;
import de.erdesignerng.model.Index;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.Relation;
import de.erdesignerng.model.Table;
import de.erdesignerng.model.View;

import java.util.stream.Collectors;

public class HistoryModificationTracker implements ModelModificationTracker {

	private final Model model;

	private final StatementList statements = new StatementList();

	public HistoryModificationTracker(Model aModel) {
		model = aModel;
	}

	protected SQLGenerator getSQLGenerator() {
		return model.getDialect().createSQLGenerator();
	}

	protected void addStatementsToHistory(StatementList aStatement) {
		statements.addAll(aStatement);
	}

	@Override
	public void addAttributeToTable(Table aTable, Attribute<Table> aAttribute) throws VetoException {
		addStatementsToHistory(getSQLGenerator().createAddAttributeToTableStatement(aTable, aAttribute));
	}

	@Override
	public void addIndexToTable(Table aTable, Index aIndex) throws VetoException {
		addStatementsToHistory(getSQLGenerator().createAddIndexToTableStatement(aTable, aIndex));
	}

	@Override
	public void addRelation(Relation aRelation) throws VetoException {
		addStatementsToHistory(getSQLGenerator().createAddRelationStatement(aRelation));
	}

	@Override
	public void addTable(Table aTable) throws VetoException {
		addStatementsToHistory(getSQLGenerator().createAddTableStatement(aTable));
	}

	@Override
	public void changeAttribute(Attribute<Table> anExistingAttribute, Attribute<Table> aNewAttribute) throws VetoException {
		addStatementsToHistory(getSQLGenerator().createChangeAttributeStatement(anExistingAttribute, aNewAttribute));
	}

	@Override
	public void changeIndex(Index anExistingIndex, Index aNewIndex) throws VetoException {
		addStatementsToHistory(getSQLGenerator().createChangeIndexStatement(anExistingIndex, aNewIndex));
	}

	@Override
	public void changeRelation(Relation aRelation, Relation aTempRelation) throws VetoException {
		addStatementsToHistory(getSQLGenerator().createChangeRelationStatement(aRelation, aTempRelation));
	}

	@Override
	public void changeTableComment(Table aTable, String aNewComment) throws VetoException {
		addStatementsToHistory(getSQLGenerator().createChangeTableCommentStatement(aTable, aNewComment));
	}

	@Override
	public void removeAttributeFromTable(Table aTable, Attribute<Table> aAttribute) throws VetoException {
		addStatementsToHistory(getSQLGenerator().createRemoveAttributeFromTableStatement(aTable, aAttribute));
	}

	@Override
	public void removeIndexFromTable(Table aTable, Index aIndex) throws VetoException {
		addStatementsToHistory(getSQLGenerator().createRemoveIndexFromTableStatement(aTable, aIndex));
	}

	@Override
	public void removeRelation(Relation aRelation) throws VetoException {
		addStatementsToHistory(getSQLGenerator().createRemoveRelationStatement(aRelation));
	}

	@Override
	public void removeTable(Table aTable) throws VetoException {
		addStatementsToHistory(getSQLGenerator().createRemoveTableStatement(aTable));
	}

	@Override
	public void renameAttribute(Attribute<Table> anExistingAttribute, String aNewName) throws VetoException {
		addStatementsToHistory(getSQLGenerator().createRenameAttributeStatement(anExistingAttribute, aNewName));
	}

	@Override
	public void renameTable(Table aTable, String aNewName) throws VetoException {
		addStatementsToHistory(getSQLGenerator().createRenameTableStatement(aTable, aNewName));
	}

	@Override
	public void removePrimaryKeyFromTable(Table aTable, Index aIndex) throws VetoException {
		addStatementsToHistory(getSQLGenerator().createRemovePrimaryKeyStatement(aTable, aIndex));
	}

	@Override
	public void addPrimaryKeyToTable(Table aTable, Index aIndex) throws VetoException {
		addStatementsToHistory(getSQLGenerator().createAddPrimaryKeyToTable(aTable, aIndex));
	}

	public StatementList getStatements() {
		return statements;
	}

	public StatementList getNotSavedStatements() {
		StatementList theResult = statements.stream().filter(theStatement -> !theStatement.isSaved()).collect(Collectors.toCollection(() -> new StatementList()));
        return theResult;
	}

	@Override
	public void addView(View aView) throws VetoException {
		addStatementsToHistory(getSQLGenerator().createAddViewStatement(aView));
	}

	@Override
	public void changeView(View aView) throws VetoException {
		addStatementsToHistory(getSQLGenerator().createChangeViewStatement(aView));
	}

	@Override
	public void removeView(View aView) throws VetoException {
		addStatementsToHistory(getSQLGenerator().createDropViewStatement(aView));
	}

	@Override
	public void addDomain(Domain aDomain) throws VetoException {
		addStatementsToHistory(getSQLGenerator().createAddDomainStatement(aDomain));
	}

	@Override
	public void removeDomain(Domain aDomain) throws VetoException {
		addStatementsToHistory(getSQLGenerator().createDropDomainStatement(aDomain));
	}

	@Override
	public void addCustomType(CustomType aCustomType) throws VetoException {
		addStatementsToHistory(getSQLGenerator().createAddCustomTypeStatement(aCustomType));
	}

	@Override
	public void removeCustomType(CustomType aCustomType) throws VetoException {
		addStatementsToHistory(getSQLGenerator().createDropCustomTypeStatement(aCustomType));
	}
}
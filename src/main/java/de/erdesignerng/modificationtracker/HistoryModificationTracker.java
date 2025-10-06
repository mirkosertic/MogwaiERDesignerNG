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

	public HistoryModificationTracker(final Model aModel) {
		model = aModel;
	}

	protected SQLGenerator getSQLGenerator() {
		return model.getDialect().createSQLGenerator();
	}

	protected void addStatementsToHistory(final StatementList aStatement) {
		statements.addAll(aStatement);
	}

	@Override
	public void addAttributeToTable(final Table aTable, final Attribute<Table> aAttribute) {
		addStatementsToHistory(getSQLGenerator().createAddAttributeToTableStatement(aTable, aAttribute));
	}

	@Override
	public void addIndexToTable(final Table aTable, final Index aIndex) {
		addStatementsToHistory(getSQLGenerator().createAddIndexToTableStatement(aTable, aIndex));
	}

	@Override
	public void addRelation(final Relation aRelation) {
		addStatementsToHistory(getSQLGenerator().createAddRelationStatement(aRelation));
	}

	@Override
	public void addTable(final Table aTable) {
		addStatementsToHistory(getSQLGenerator().createAddTableStatement(aTable));
	}

	@Override
	public void changeAttribute(final Attribute<Table> anExistingAttribute, final Attribute<Table> aNewAttribute) {
		addStatementsToHistory(getSQLGenerator().createChangeAttributeStatement(anExistingAttribute, aNewAttribute));
	}

	@Override
	public void changeIndex(final Index anExistingIndex, final Index aNewIndex) {
		addStatementsToHistory(getSQLGenerator().createChangeIndexStatement(anExistingIndex, aNewIndex));
	}

	@Override
	public void changeRelation(final Relation aRelation, final Relation aTempRelation) {
		addStatementsToHistory(getSQLGenerator().createChangeRelationStatement(aRelation, aTempRelation));
	}

	@Override
	public void changeTableComment(final Table aTable, final String aNewComment) {
		addStatementsToHistory(getSQLGenerator().createChangeTableCommentStatement(aTable, aNewComment));
	}

	@Override
	public void removeAttributeFromTable(final Table aTable, final Attribute<Table> aAttribute) {
		addStatementsToHistory(getSQLGenerator().createRemoveAttributeFromTableStatement(aTable, aAttribute));
	}

	@Override
	public void removeIndexFromTable(final Table aTable, final Index aIndex) {
		addStatementsToHistory(getSQLGenerator().createRemoveIndexFromTableStatement(aTable, aIndex));
	}

	@Override
	public void removeRelation(final Relation aRelation) {
		addStatementsToHistory(getSQLGenerator().createRemoveRelationStatement(aRelation));
	}

	@Override
	public void removeTable(final Table aTable) {
		addStatementsToHistory(getSQLGenerator().createRemoveTableStatement(aTable));
	}

	@Override
	public void renameAttribute(final Attribute<Table> anExistingAttribute, final String aNewName) {
		addStatementsToHistory(getSQLGenerator().createRenameAttributeStatement(anExistingAttribute, aNewName));
	}

	@Override
	public void renameTable(final Table aTable, final String aNewName) {
		addStatementsToHistory(getSQLGenerator().createRenameTableStatement(aTable, aNewName));
	}

	@Override
	public void removePrimaryKeyFromTable(final Table aTable, final Index aIndex) {
		addStatementsToHistory(getSQLGenerator().createRemovePrimaryKeyStatement(aTable, aIndex));
	}

	@Override
	public void addPrimaryKeyToTable(final Table aTable, final Index aIndex) {
		addStatementsToHistory(getSQLGenerator().createAddPrimaryKeyToTable(aTable, aIndex));
	}

	public StatementList getStatements() {
		return statements;
	}

	public StatementList getNotSavedStatements() {
        return statements.stream().filter(theStatement -> !theStatement.isSaved()).collect(Collectors.toCollection(StatementList::new));
	}

	@Override
	public void addView(final View aView) {
		addStatementsToHistory(getSQLGenerator().createAddViewStatement(aView));
	}

	@Override
	public void changeView(final View aView) {
		addStatementsToHistory(getSQLGenerator().createChangeViewStatement(aView));
	}

	@Override
	public void removeView(final View aView) {
		addStatementsToHistory(getSQLGenerator().createDropViewStatement(aView));
	}

	@Override
	public void addDomain(final Domain aDomain) {
		addStatementsToHistory(getSQLGenerator().createAddDomainStatement(aDomain));
	}

	@Override
	public void removeDomain(final Domain aDomain) {
		addStatementsToHistory(getSQLGenerator().createDropDomainStatement(aDomain));
	}

	@Override
	public void addCustomType(final CustomType aCustomType) {
		addStatementsToHistory(getSQLGenerator().createAddCustomTypeStatement(aCustomType));
	}

	@Override
	public void removeCustomType(final CustomType aCustomType) {
		addStatementsToHistory(getSQLGenerator().createDropCustomTypeStatement(aCustomType));
	}
}
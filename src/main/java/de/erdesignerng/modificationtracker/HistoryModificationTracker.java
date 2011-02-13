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
import de.erdesignerng.dialect.Statement;
import de.erdesignerng.dialect.StatementList;
import de.erdesignerng.model.*;

public class HistoryModificationTracker implements ModelModificationTracker {

	private final Model model;

	private final StatementList statements = new StatementList();

	public HistoryModificationTracker(Model aModel) {
		model = aModel;
	}

	protected SQLGenerator getSQLGenerator() {
		return model.getDialect().createSQLGenerator();
	}

	protected void addStatementsToHistory(StatementList aStatement) throws VetoException {
		statements.addAll(aStatement);
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

	public void changeAttribute(Attribute anExistingAttribute, Attribute aNewAttribute) throws VetoException {
		addStatementsToHistory(getSQLGenerator().createChangeAttributeStatement(anExistingAttribute, aNewAttribute));
	}

	public void changeIndex(Index anExistingIndex, Index aNewIndex) throws VetoException {
		addStatementsToHistory(getSQLGenerator().createChangeIndexStatement(anExistingIndex, aNewIndex));
	}

	public void changeRelation(Relation aRelation, Relation aTempRelation) throws VetoException {
		addStatementsToHistory(getSQLGenerator().createChangeRelationStatement(aRelation, aTempRelation));
	}

	public void changeTableComment(Table aTable, String aNewComment) throws VetoException {
		addStatementsToHistory(getSQLGenerator().createChangeTableCommentStatement(aTable, aNewComment));
	}

	public void removeAttributeFromTable(Table aTable, Attribute aAttribute) throws VetoException {
		addStatementsToHistory(getSQLGenerator().createRemoveAttributeFromTableStatement(aTable, aAttribute));
	}

	public void removeIndexFromTable(Table aTable, Index aIndex) throws VetoException {
		addStatementsToHistory(getSQLGenerator().createRemoveIndexFromTableStatement(aTable, aIndex));
	}

	public void removeRelation(Relation aRelation) throws VetoException {
		addStatementsToHistory(getSQLGenerator().createRemoveRelationStatement(aRelation));
	}

	public void removeTable(Table aTable) throws VetoException {
		addStatementsToHistory(getSQLGenerator().createRemoveTableStatement(aTable));
	}

	public void renameAttribute(Attribute anExistingAttribute, String aNewName) throws VetoException {
		addStatementsToHistory(getSQLGenerator().createRenameAttributeStatement(anExistingAttribute, aNewName));
	}

	public void renameTable(Table aTable, String aNewName) throws VetoException {
		addStatementsToHistory(getSQLGenerator().createRenameTableStatement(aTable, aNewName));
	}

	public void removePrimaryKeyFromTable(Table aTable, Index aIndex) throws VetoException {
		addStatementsToHistory(getSQLGenerator().createRemovePrimaryKeyStatement(aTable, aIndex));
	}

	public void addPrimaryKeyToTable(Table aTable, Index aIndex) throws VetoException {
		addStatementsToHistory(getSQLGenerator().createAddPrimaryKeyToTable(aTable, aIndex));
	}

	public StatementList getStatements() {
		return statements;
	}

	public StatementList getNotSavedStatements() {
		StatementList theResult = new StatementList();
		for (Statement theStatement : statements) {
			if (!theStatement.isSaved()) {
				theResult.add(theStatement);
			}
		}
		return theResult;
	}

	public void addView(View aView) throws VetoException {
		addStatementsToHistory(getSQLGenerator().createAddViewStatement(aView));
	}

	public void changeView(View aView) throws VetoException {
		addStatementsToHistory(getSQLGenerator().createChangeViewStatement(aView));
	}

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

	public void addCustomType(CustomType aCustomType) throws VetoException {
		addStatementsToHistory(getSQLGenerator().createAddCustomTypeStatement(aCustomType));
	}

	public void removeCustomType(CustomType aCustomType) throws VetoException {
		addStatementsToHistory(getSQLGenerator().createDropCustomTypeStatement(aCustomType));
	}
}
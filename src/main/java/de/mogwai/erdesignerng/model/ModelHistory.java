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
package de.mogwai.erdesignerng.model;

/**
 * An empty model history.
 * 
 * @author Mirko Sertic <mail@mirkosertic.de>
 */
public class ModelHistory {

	private Model model;

	public ModelHistory(Model aModel) {
		model = aModel;
	}

	protected void addSQLToHistory(String aSQL) {
		System.out.println(aSQL);
	}

	public void createAttributeChangedCommand(Table aTable,
			String aAttributeName, Domain aDomain, boolean aNullable,

			String aDefaultValue) {

		String theSQL = model.getDialect().createAlterAttributeSQL(aTable,
				aAttributeName, aDomain, aNullable);
		addSQLToHistory(theSQL);
	}

	public void createRenameAttributeCommand(Table aTable,
			Attribute aAttribute, String aNewName) {

		String theSQL = model.getDialect().createRenameAttributeSQL(aTable,
				aAttribute, aNewName);
		addSQLToHistory(theSQL);
	}

	public void createRenameRelationCommand(Relation aRelation, String aNewName) {
		String theSQL = model.getDialect().createRenameRelationSQL(aRelation,
				aNewName);
		addSQLToHistory(theSQL);
	}

	public void createRenameTableCommand(Table aTable, String aNewName) {
		String theSQL = model.getDialect().createRenameTableSQL(aTable,
				aNewName);
		addSQLToHistory(theSQL);
	}

	public void createDeleteCommand(Attribute aAttribute) {
		String theSQL = model.getDialect().createDropAttributeSQL(aAttribute);
		addSQLToHistory(theSQL);
	}

	public void createDeleteCommand(Table aTable) {
		String theSQL = model.getDialect().createDropTableSQL(aTable);
		addSQLToHistory(theSQL);
	}

	public void createDeleteCommand(Relation aRelation) {
		String theSQL = model.getDialect().createDropRelationSQL(aRelation);
		addSQLToHistory(theSQL);
	}

	public void createDeleteCommand(Index aIndex) {
		String theSQL = model.getDialect().createDropIndexSQL(aIndex);
		addSQLToHistory(theSQL);
	}

	public void createRenameIndexCommand(Table aTable, Index aIndex,
			String aNewName) {
		String theSQL = model.getDialect().createRenameIndexSQL(aTable, aIndex,
				aNewName);
		addSQLToHistory(theSQL);
	}

	public void createAddRelationCommand(Relation aRelation) {
		String theSQL = model.getDialect().createAddRelationSQL(aRelation);
		addSQLToHistory(theSQL);

	}

	public void createAddTableCommand(Table aTable) {
		String theSQL = model.getDialect().createAddTableSQL(aTable);
		addSQLToHistory(theSQL);
	}

	public void createAddAttributeCommand(Attribute aAttribute) {
		String theSQL = model.getDialect().createAddAttributeSQL(aAttribute);
		addSQLToHistory(theSQL);
	}

	public void createAddIndexCommand(Index aIndex) {
		String theSQL = model.getDialect().createAddIndexSQL(aIndex);
		addSQLToHistory(theSQL);
	}
}

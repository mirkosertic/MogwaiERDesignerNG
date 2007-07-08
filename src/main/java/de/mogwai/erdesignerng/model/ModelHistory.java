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

import java.util.List;


/**
 * @author $Author: mirkosertic $
 * @version $Date: 2007-07-08 10:06:34 $
 */
public class ModelHistory {

	private Model model;

	public ModelHistory(Model aModel) {
		model = aModel;
	}

	protected void addSQLToHistory(List<String> aSQL) {
		if (aSQL != null) {
			for (String aString : aSQL) {
				System.out.println(aString + ";");
			}
		}
	}

	public void createAttributeChangedCommand(Table aTable,
			String aAttributeName, Domain aDomain, boolean aNullable,

			String aDefaultValue) {

		addSQLToHistory(model.getDialect().createAlterAttributeSQL(aTable,
				aAttributeName, aDomain, aNullable));
	}

	public void createRenameAttributeCommand(Table aTable,
			Attribute aAttribute, String aNewName) {

		addSQLToHistory(model.getDialect().createRenameAttributeSQL(aTable,
				aAttribute, aNewName));
	}

	public void createRenameRelationCommand(Relation aRelation, String aNewName) {
		addSQLToHistory(model.getDialect().createRenameRelationSQL(aRelation,
				aNewName));
	}

	public void createRenameTableCommand(Table aTable, String aNewName) {
		addSQLToHistory(model.getDialect().createRenameTableSQL(aTable,
				aNewName));
	}

	public void createDeleteCommand(Attribute aAttribute) {
		addSQLToHistory(model.getDialect().createDropAttributeSQL(aAttribute));
	}

	public void createDeleteCommand(Table aTable) {
		addSQLToHistory(model.getDialect().createDropTableSQL(aTable));
	}

	public void createDeleteCommand(Relation aRelation) {
		addSQLToHistory(model.getDialect().createDropRelationSQL(aRelation));
	}

	public void createDeleteCommand(Index aIndex) {
		addSQLToHistory(model.getDialect().createDropIndexSQL(aIndex));
	}

	public void createRenameIndexCommand(Table aTable, Index aIndex,
			String aNewName) {
		addSQLToHistory(model.getDialect().createRenameIndexSQL(aTable, aIndex,
				aNewName));
	}

	public void createAddRelationCommand(Relation aRelation) {
		addSQLToHistory(model.getDialect().createAddRelationSQL(aRelation));

	}

	public void createAddTableCommand(Table aTable) {
		addSQLToHistory(model.getDialect().createAddTableSQL(aTable));
	}

	public void createAddAttributeCommand(Attribute aAttribute) {
		addSQLToHistory(model.getDialect().createAddAttributeSQL(aAttribute));
	}

	public void createAddIndexCommand(Index aIndex) {
		addSQLToHistory(model.getDialect().createAddIndexSQL(aIndex));
	}
}

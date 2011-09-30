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

import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.CustomType;
import de.erdesignerng.model.Domain;
import de.erdesignerng.model.Index;
import de.erdesignerng.model.Relation;
import de.erdesignerng.model.Table;
import de.erdesignerng.model.View;

public class EmptyModelModificationTracker implements ModelModificationTracker {

	@Override
	public void removeRelation(Relation aRelation) throws VetoException {
	}

	@Override
	public void removeTable(Table aTable) throws VetoException {
	}

	@Override
	public void addAttributeToTable(Table aTable, Attribute<Table> aAttribute) throws VetoException {
	}

	@Override
	public void addIndexToTable(Table aTable, Index aIndex) throws VetoException {
	}

	@Override
	public void addRelation(Relation aRelation) throws VetoException {
	}

	@Override
	public void addTable(Table aTable) throws VetoException {
	}

	@Override
	public void changeAttribute(Attribute<Table> anExistingAttribute, Attribute<Table> aNewAttribute) throws VetoException {
	}

	@Override
	public void changeIndex(Index anExistingIndex, Index aNewIndex) throws VetoException {
	}

	@Override
	public void changeRelation(Relation aRelation, Relation aTempRelation) throws VetoException {
	}

	@Override
	public void changeTableComment(Table aTable, String aNewComment) throws VetoException {
	}

	@Override
	public void removeAttributeFromTable(Table aTable, Attribute<Table> aAttribute) throws VetoException {
	}

	@Override
	public void removeIndexFromTable(Table aTable, Index aIndex) throws VetoException {
	}

	@Override
	public void renameAttribute(Attribute<Table> anExistingAttribute, String aNewName) throws VetoException {
	}

	@Override
	public void renameTable(Table aTable, String aNewName) throws VetoException {
	}

	@Override
	public void removePrimaryKeyFromTable(Table aTable, Index aIndex) throws VetoException {
	}

	@Override
	public void addPrimaryKeyToTable(Table aTable, Index aIndex) throws VetoException {
	}

	@Override
	public void addView(View aView) throws VetoException {
	}

	@Override
	public void changeView(View aView) throws VetoException {
	}

	@Override
	public void removeView(View aView) throws VetoException {
	}

	@Override
	public void addDomain(Domain domain) throws VetoException {
	}

	@Override
	public void removeDomain(Domain domain) throws VetoException {
	}

	@Override
	public void addCustomType(CustomType aCustomType) throws VetoException {
	}

	@Override
	public void removeCustomType(CustomType aCustomType) throws VetoException {
	}
}
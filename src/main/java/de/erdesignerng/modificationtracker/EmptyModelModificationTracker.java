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
	public void removeRelation(final Relation aRelation) {
	}

	@Override
	public void removeTable(final Table aTable) {
	}

	@Override
	public void addAttributeToTable(final Table aTable, final Attribute<Table> aAttribute) {
	}

	@Override
	public void addIndexToTable(final Table aTable, final Index aIndex) {
	}

	@Override
	public void addRelation(final Relation aRelation) {
	}

	@Override
	public void addTable(final Table aTable) {
	}

	@Override
	public void changeAttribute(final Attribute<Table> anExistingAttribute, final Attribute<Table> aNewAttribute) {
	}

	@Override
	public void changeIndex(final Index anExistingIndex, final Index aNewIndex) {
	}

	@Override
	public void changeRelation(final Relation aRelation, final Relation aTempRelation) {
	}

	@Override
	public void changeTableComment(final Table aTable, final String aNewComment) {
	}

	@Override
	public void removeAttributeFromTable(final Table aTable, final Attribute<Table> aAttribute) {
	}

	@Override
	public void removeIndexFromTable(final Table aTable, final Index aIndex) {
	}

	@Override
	public void renameAttribute(final Attribute<Table> anExistingAttribute, final String aNewName) {
	}

	@Override
	public void renameTable(final Table aTable, final String aNewName) {
	}

	@Override
	public void removePrimaryKeyFromTable(final Table aTable, final Index aIndex) {
	}

	@Override
	public void addPrimaryKeyToTable(final Table aTable, final Index aIndex) {
	}

	@Override
	public void addView(final View aView) {
	}

	@Override
	public void changeView(final View aView) {
	}

	@Override
	public void removeView(final View aView) {
	}

	@Override
	public void addDomain(final Domain domain) {
	}

	@Override
	public void removeDomain(final Domain domain) {
	}

	@Override
	public void addCustomType(final CustomType aCustomType) {
	}

	@Override
	public void removeCustomType(final CustomType aCustomType) {
	}
}
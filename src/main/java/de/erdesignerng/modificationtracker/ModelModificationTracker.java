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

public interface ModelModificationTracker {

	void removeTable(Table aTable);

	void removeRelation(Relation aRelation);

	void addTable(Table aTable);

	void removeAttributeFromTable(Table aTable, Attribute<Table> aAttribute);

	void removeIndexFromTable(Table aTable, Index aIndex);

	void addAttributeToTable(Table aTable, Attribute<Table> aAttribute);

	void changeAttribute(Attribute<Table> anExistingAttribute, Attribute<Table> aNewAttribute);

	void addIndexToTable(Table aTable, Index aIndex);

	void changeIndex(Index anExistingIndex, Index aNewIndex);

	void renameTable(Table aTable, String aNewName);

	void changeTableComment(Table aTable, String aNewComment);

	void renameAttribute(Attribute<Table> anExistingAttribute, String aNewName);

	void addRelation(Relation aRelation);

	void changeRelation(Relation aRelation, Relation aTempRelation);

	void removePrimaryKeyFromTable(Table aTable, Index aIndex);

	void addPrimaryKeyToTable(Table aTable, Index aIndex);

	void addView(View aView);

	void removeView(View aView);

	void changeView(View aView);

	void addDomain(Domain aDomain);

	void removeDomain(Domain aDomain);

	void addCustomType(CustomType aCustomType);

	void removeCustomType(CustomType aCustomType);

}
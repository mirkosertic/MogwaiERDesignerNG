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
import de.erdesignerng.model.Index;
import de.erdesignerng.model.Relation;
import de.erdesignerng.model.Table;

public interface ModelModificationTracker {

    void removeTable(Table aTable) throws VetoException;

    void removeRelation(Relation aRelation) throws VetoException;

    void addTable(Table aTable) throws VetoException;

    void removeAttributeFromTable(Table aTable, Attribute aAttribute) throws VetoException;

    void removeIndexFromTable(Table aTable, Index aIndex) throws VetoException;

    void addAttributeToTable(Table aTable, Attribute aAttribute) throws VetoException;

    void changeAttribute(Attribute aExistantAttribute, Attribute aNewAttribute) throws VetoException;

    void addIndexToTable(Table aTable, Index aIndex) throws VetoException;

    void changeIndex(Index aExistantIndex, Index aNewIndex) throws VetoException;

    void renameTable(Table aTable, String aNewName) throws VetoException;

    void changeTableComment(Table aTable, String aNewComment) throws VetoException;

    void renameAttribute(Attribute aExistantAttribute, String aNewName) throws VetoException;

    void addRelation(Relation aRelation) throws VetoException;

    void changeRelation(Relation aRelation, Relation aTempRelation) throws VetoException;

    void removePrimaryKeyFromTable(Table aTable, Index aIndex) throws VetoException;

    void addPrimaryKeyToTable(Table aTable, Index aIndex) throws VetoException;
}

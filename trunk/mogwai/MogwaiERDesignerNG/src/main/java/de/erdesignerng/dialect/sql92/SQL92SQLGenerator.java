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
package de.erdesignerng.dialect.sql92;

import de.erdesignerng.dialect.SQLGenerator;
import de.erdesignerng.dialect.StatementList;
import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.Index;
import de.erdesignerng.model.Relation;
import de.erdesignerng.model.Table;
import de.erdesignerng.modificationtracker.VetoException;

public class SQL92SQLGenerator<T extends SQL92Dialect> extends SQLGenerator<T> {

    protected SQL92SQLGenerator(T aDialect) {
        super(aDialect);
    }

    @Override
    public StatementList createAddAttributeToTableStatement(Table aTable, Attribute aAttribute) throws VetoException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StatementList createAddIndexToTableStatement(Table aTable, Index aIndex) throws VetoException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StatementList createAddRelationStatement(Relation aRelation) throws VetoException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StatementList createAddTableStatement(Table aTable) throws VetoException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StatementList createChangeAttributeStatement(Attribute aExistantAttribute, Attribute aNewAttribute) throws VetoException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StatementList createChangeIndexStatement(Index aExistantIndex, Index aNewIndex) throws VetoException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StatementList createChangeRelationStatement(Relation aRelation, Relation aTempRelation) throws VetoException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StatementList createChangeTableCommentStatement(Table aTable, String aNewComment) throws VetoException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StatementList createRemoveAttributeFromTableStatement(Table aTable, String aSystemId) throws VetoException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StatementList createRemoveIndexFromTableStatement(Table aTable, String aSystemId) throws VetoException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StatementList createRemoveRelationStatement(Relation aRelation) throws VetoException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StatementList createRemoveTableStatement(Table aTable) throws VetoException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StatementList createRenameAttributeStatement(Attribute aExistantAttribute, String aNewName) throws VetoException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StatementList createRenameRelationStatement(Relation aRelation, String aNewName) throws VetoException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StatementList createRenameTableStatement(Table aTable, String aNewName) throws VetoException {
        // TODO Auto-generated method stub
        return null;
    }
}
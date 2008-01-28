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
import de.erdesignerng.dialect.Statement;
import de.erdesignerng.dialect.StatementList;
import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.Index;
import de.erdesignerng.model.IndexType;
import de.erdesignerng.model.Relation;
import de.erdesignerng.model.Table;
import de.erdesignerng.modificationtracker.VetoException;

public class SQL92SQLGenerator<T extends SQL92Dialect> extends SQLGenerator<T> {

    protected SQL92SQLGenerator(T aDialect) {
        super(aDialect);
    }

    @Override
    public StatementList createAddAttributeToTableStatement(Table aTable, Attribute aAttribute) throws VetoException {
        StatementList theResult = new StatementList();
        StringBuilder theStatement = new StringBuilder();

        theStatement.append("ALTER TABLE " + aTable.getName() + " ADD ");

        theStatement.append(aAttribute.getName());
        theStatement.append(" ");
        theStatement.append(getDialect().getPhysicalDeclarationFor(aAttribute.getDomain()));
        theStatement.append(" ");

        boolean isNullable = aAttribute.isNullable();

        if (!isNullable) {
            theStatement.append("NOT NULL");
        }

        theResult.add(new Statement(theStatement.toString()));

        return theResult;
    }

    @Override
    public StatementList createAddIndexToTableStatement(Table aTable, Index aIndex) throws VetoException {
        StatementList theResult = new StatementList();
        StringBuilder theStatement = new StringBuilder();

        theStatement.append("CREATE ");

        if (IndexType.UNIQUE.equals(aIndex.getIndexType())) {
            theStatement.append("UNIQUE ");
        }

        theStatement.append("INDEX ");
        theStatement.append(aIndex.getName());
        theStatement.append(" ON ");
        theStatement.append(aTable.getName());
        theStatement.append(" (");

        for (int i = 0; i < aIndex.getAttributes().size(); i++) {
            Attribute theAttribute = aIndex.getAttributes().get(i);

            if (i > 0) {
                theStatement.append(",");
            }

            theStatement.append(theAttribute.getName());
        }

        theStatement.append(")");

        theResult.add(new Statement(theStatement.toString()));

        return theResult;
    }

    @Override
    public StatementList createAddRelationStatement(Relation aRelation) throws VetoException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StatementList createChangeIndexStatement(Index aExistantIndex, Index aNewIndex) throws VetoException {
        StatementList theList = new StatementList();
        Table theTable = aExistantIndex.getOwner();
        theList.addAll(createRemoveIndexFromTableStatement(theTable, aExistantIndex));
        theList.addAll(createAddIndexToTableStatement(theTable, aNewIndex));
        return theList;
    }

    @Override
    public StatementList createChangeRelationStatement(Relation aRelation, Relation aTempRelation) throws VetoException {
        StatementList theList = new StatementList();
        theList.addAll(createRemoveRelationStatement(aRelation));
        theList.addAll(createAddRelationStatement(aTempRelation));
        return theList;
    }

    @Override
    public StatementList createChangeTableCommentStatement(Table aTable, String aNewComment) throws VetoException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StatementList createRemoveAttributeFromTableStatement(Table aTable, Attribute aAttribute)
            throws VetoException {
        StatementList theResult = new StatementList();
        theResult.add(new Statement("ALTER TABLE " + aTable.getName() + " DROP COLUMN " + aAttribute.getName()));
        return theResult;
    }

    @Override
    public StatementList createRemoveIndexFromTableStatement(Table aTable, Index aIndex) throws VetoException {
        StatementList theResult = new StatementList();
        StringBuilder theStatement = new StringBuilder();

        theStatement.append("ALTER TABLE ");
        theStatement.append(aTable.getName());
        theStatement.append(" DROP INDEX ");

        theStatement.append(aIndex.getName());

        theResult.add(new Statement(theStatement.toString()));

        return theResult;
    }

    @Override
    public StatementList createRemoveRelationStatement(Relation aRelation) throws VetoException {

        Table theImportingTable = aRelation.getImportingTable();

        StatementList theResult = new StatementList();
        theResult.add(new Statement("ALTER TABLE " + theImportingTable.getName() + " DROP FOREIGN KEY "
                + aRelation.getName()));
        return theResult;
    }

    @Override
    public StatementList createRemoveTableStatement(Table aTable) throws VetoException {
        StatementList theResult = new StatementList();
        theResult.add(new Statement("DROP TABLE " + aTable.getName()));
        return theResult;
    }

    @Override
    public StatementList createRenameAttributeStatement(Attribute aExistantAttribute, String aNewName)
            throws VetoException {
        return null;
    }

    @Override
    public StatementList createRenameRelationStatement(Relation aRelation, String aNewName) throws VetoException {
        return null;
    }

    @Override
    public StatementList createRenameTableStatement(Table aTable, String aNewName) throws VetoException {
        return null;
    }

    @Override
    public StatementList createAddTableStatement(Table aTable) throws VetoException {
        return null;
    }

    @Override
    public StatementList createChangeAttributeStatement(Attribute aExistantAttribute, Attribute aNewAttribute)
            throws VetoException {
        return null;
    }

    @Override
    public StatementList createRemovePrimaryKeyStatement(Table table, Index index) throws VetoException {
        return null;
    }

    @Override
    public StatementList createAddPrimaryKeyToTable(Table aTable, Index aIndex) {
        return null;
    }

}
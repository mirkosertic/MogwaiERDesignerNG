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
package de.erdesignerng.dialect.mysql;

import de.erdesignerng.dialect.Statement;
import de.erdesignerng.dialect.StatementList;
import de.erdesignerng.dialect.sql92.SQL92SQLGenerator;
import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.Index;
import de.erdesignerng.model.IndexType;
import de.erdesignerng.model.Relation;
import de.erdesignerng.model.Table;
import de.erdesignerng.modificationtracker.VetoException;

public class MySQLSQLGenerator extends SQL92SQLGenerator<MySQLDialect> {

    public MySQLSQLGenerator(MySQLDialect aDialect) {
        super(aDialect);
    }

    @Override
    public StatementList createAddTableStatement(Table aTable) throws VetoException {

        StatementList theResult = new StatementList();
        StringBuilder theStatement = new StringBuilder();

        theStatement.append("CREATE TABLE " + aTable.getName() + " (\n");
        for (int i = 0; i < aTable.getAttributes().size(); i++) {
            Attribute theAttribute = aTable.getAttributes().get(i);

            theStatement.append(TAB);
            theStatement.append(theAttribute.getName());
            theStatement.append(" ");
            theStatement.append(getDialect().getPhysicalDeclarationFor(theAttribute.getDomain()));
            theStatement.append(" ");

            boolean isNullable = theAttribute.isNullable();
            if (theAttribute.isPrimaryKey() && (!getDialect().isNullablePrimaryKeyAllowed())) {
                isNullable = false;
            }

            if (!isNullable) {
                theStatement.append("NOT NULL");
            }

            if (i < aTable.getAttributes().size() - 1) {
                theStatement.append(",");
            }

            theStatement.append("\n");
        }
        theStatement.append(")");
        theResult.add(new Statement(theStatement.toString()));

        for (Index theIndex : aTable.getIndexes()) {
            if (IndexType.PRIMARYKEY.equals(theIndex.getIndexType())) {
                theResult.addAll(createAddPrimaryKeyToTable(aTable, theIndex));
            } else {
                theResult.addAll(createAddIndexToTableStatement(aTable, theIndex));
            }
        }

        return theResult;
    }

    @Override
    public StatementList createChangeAttributeStatement(Attribute aExistantAttribute, Attribute aNewAttribute)
            throws VetoException {
        Table theTable = aExistantAttribute.getOwner();

        StatementList theResult = new StatementList();
        StringBuilder theStatement = new StringBuilder();

        theStatement.append("ALTER TABLE " + theTable.getName() + " MODIFY ");

        theStatement.append(aExistantAttribute.getName());
        theStatement.append(" ");
        theStatement.append(getDialect().getPhysicalDeclarationFor(aNewAttribute.getDomain()));
        theStatement.append(" ");

        boolean isNullable = aNewAttribute.isNullable();
        if (aNewAttribute.isPrimaryKey() && (!getDialect().isNullablePrimaryKeyAllowed())) {
            isNullable = false;
        }

        if (!isNullable) {
            theStatement.append("NOT NULL");
        }

        theResult.add(new Statement(theStatement.toString()));

        return theResult;
    }

    @Override
    public StatementList createRenameTableStatement(Table aTable, String aNewName) throws VetoException {

        StatementList theResult = new StatementList();
        StringBuilder theStatement = new StringBuilder();

        theStatement.append("ALTER TABLE " + aTable.getName() + " RENAME TO ");

        theStatement.append(aNewName);

        theResult.add(new Statement(theStatement.toString()));

        return theResult;
    }

    @Override
    public StatementList createRemovePrimaryKeyStatement(Table aTable, Index aIndex) throws VetoException {
        StatementList theResult = new StatementList();
        StringBuilder theStatement = new StringBuilder();

        theStatement.append("ALTER TABLE " + aTable.getName() + " DROP PRIMARY KEY");

        theResult.add(new Statement(theStatement.toString()));

        return theResult;
    }

    @Override
    public StatementList createAddPrimaryKeyToTable(Table aTable, Index aIndex) {

        StatementList theResult = new StatementList();
        StringBuilder theStatement = new StringBuilder();

        theStatement = new StringBuilder("ALTER TABLE ");
        theStatement.append(aTable.getName());
        theStatement.append(" ADD CONSTRAINT ");
        theStatement.append(aIndex.getName());
        theStatement.append(" PRIMARY KEY(");

        for (int i = 0; i < aIndex.getAttributes().size(); i++) {
            if (i > 0) {
                theStatement.append(",");
            }
            theStatement.append(aIndex.getAttributes().get(i).getName());
        }
        theStatement.append(")");
        theResult.add(new Statement(theStatement.toString()));

        return theResult;
    }

    @Override
    public StatementList createRenameAttributeStatement(Attribute aExistantAttribute, String aNewName)
            throws VetoException {
        Table theTable = aExistantAttribute.getOwner();

        StatementList theResult = new StatementList();
        StringBuilder theStatement = new StringBuilder();

        theStatement.append("ALTER TABLE " + theTable.getName() + " CHANGE ");

        theStatement.append(aExistantAttribute.getName());
        theStatement.append(" ");
        theStatement.append(aNewName);
        theStatement.append(" ");
        theStatement.append(getDialect().getPhysicalDeclarationFor(aExistantAttribute.getDomain()));
        theStatement.append(" ");

        boolean isNullable = aExistantAttribute.isNullable();
        if (aExistantAttribute.isPrimaryKey() && (!getDialect().isNullablePrimaryKeyAllowed())) {
            isNullable = false;
        }

        if (!isNullable) {
            theStatement.append("NOT NULL");
        }

        theResult.add(new Statement(theStatement.toString()));

        return theResult;

    }

    @Override
    public StatementList createAddRelationStatement(Relation aRelation) throws VetoException {

        Table theImportingTable = aRelation.getImportingTable();
        Table theExportingTable = aRelation.getExportingTable();

        StatementList theResult = new StatementList();
        StringBuilder theStatement = new StringBuilder();

        theStatement = new StringBuilder("ALTER TABLE ");
        theStatement.append(theImportingTable.getName());
        theStatement.append(" ADD CONSTRAINT ");
        theStatement.append(aRelation.getName());
        theStatement.append(" FOREIGN KEY (");

        boolean first = true;
        for (Attribute theAttribute : aRelation.getMapping().values()) {
            if (!first) {
                theStatement.append(",");
            }
            theStatement.append(theAttribute.getName());
            first = false;
        }

        theStatement.append(") REFERENCES ");
        theStatement.append(theExportingTable.getName());
        theStatement.append("(");

        first = true;
        for (Attribute theAttribute : aRelation.getMapping().keySet()) {
            if (!first) {
                theStatement.append(",");
            }
            theStatement.append(theAttribute.getName());
            first = false;
        }

        theStatement.append(")");

        switch (aRelation.getOnDelete()) {
        case CASCADE:
            theStatement.append(" ON DELETE CASCADE");
            break;
        case NOTHING:
            theStatement.append(" ON DELETE NO ACTION");
            break;
        case SET_NULL:
            theStatement.append(" ON DELETE SET NULL");
            break;
        default:
        }

        switch (aRelation.getOnUpdate()) {
        case CASCADE:
            theStatement.append(" ON UPDATE CASCADE");
            break;
        case NOTHING:
            theStatement.append(" ON UPDATE NO ACTION");
            break;
        case SET_NULL:
            theStatement.append(" ON UPDATE SET NULL");
            break;
        default:
        }

        theResult.add(new Statement(theStatement.toString()));

        return theResult;
    }
}
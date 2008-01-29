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

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-01-29 20:27:56 $
 * @param <T> the dialect
 */
public class SQL92SQLGenerator<T extends SQL92Dialect> extends SQLGenerator<T> {

    protected SQL92SQLGenerator(T aDialect) {
        super(aDialect);
    }

    @Override
    public StatementList createAddAttributeToTableStatement(Table aTable, Attribute aAttribute) throws VetoException {
        StatementList theResult = new StatementList();
        StringBuilder theStatement = new StringBuilder();

        theStatement.append("ALTER TABLE " + escapeTableName(aTable.getName()) + " ADD ");

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
        theStatement.append(escapeTableName(aTable.getName()));
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
        Table theImportingTable = aRelation.getImportingTable();
        Table theExportingTable = aRelation.getExportingTable();

        StatementList theResult = new StatementList();
        StringBuilder theStatement = new StringBuilder();

        theStatement = new StringBuilder("ALTER TABLE ");
        theStatement.append(escapeTableName(theImportingTable.getName()));
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

        if (getDialect().isSupportsOnDelete()) {
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
        }

        if (getDialect().isSupportsOnUpdate()) {
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
        }

        theResult.add(new Statement(theStatement.toString()));

        return theResult;
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
        theResult.add(new Statement("ALTER TABLE " + escapeTableName(aTable.getName()) + " DROP COLUMN "
                + aAttribute.getName()));
        return theResult;
    }

    @Override
    public StatementList createRemoveIndexFromTableStatement(Table aTable, Index aIndex) throws VetoException {
        StatementList theResult = new StatementList();
        StringBuilder theStatement = new StringBuilder();

        theStatement.append("DROP INDEX ");
        theStatement.append(aIndex.getName());
        theStatement.append(" ON ");
        theStatement.append(escapeTableName(aTable.getName()));

        theResult.add(new Statement(theStatement.toString()));

        return theResult;
    }

    @Override
    public StatementList createRemoveRelationStatement(Relation aRelation) throws VetoException {

        Table theImportingTable = aRelation.getImportingTable();

        StatementList theResult = new StatementList();
        theResult.add(new Statement("ALTER TABLE " + escapeTableName(theImportingTable.getName())
                + " DROP CONSTRAINT " + aRelation.getName()));
        return theResult;
    }

    @Override
    public StatementList createRemoveTableStatement(Table aTable) throws VetoException {
        StatementList theResult = new StatementList();
        theResult.add(new Statement("DROP TABLE " + escapeTableName(aTable.getName())));
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

        StatementList theResult = new StatementList();
        StringBuilder theStatement = new StringBuilder();

        theStatement.append("CREATE TABLE " + escapeTableName(aTable.getName()) + " (\n");
        for (int i = 0; i < aTable.getAttributes().size(); i++) {
            Attribute theAttribute = aTable.getAttributes().get(i);

            theStatement.append(TAB);
            theStatement.append(theAttribute.getName());
            theStatement.append(" ");
            theStatement.append(getDialect().getPhysicalDeclarationFor(theAttribute.getDomain()));
            theStatement.append(" ");

            boolean isNullable = theAttribute.isNullable();

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
        return null;
    }

    @Override
    public StatementList createRemovePrimaryKeyStatement(Table aTable, Index aIndex) throws VetoException {
        StatementList theResult = new StatementList();
        StringBuilder theStatement = new StringBuilder();

        theStatement
                .append("ALTER TABLE " + escapeTableName(aTable.getName()) + " DROP CONSTRAINT " + aIndex.getName());

        theResult.add(new Statement(theStatement.toString()));

        return theResult;
    }

    @Override
    public StatementList createAddPrimaryKeyToTable(Table aTable, Index aIndex) {

        StatementList theResult = new StatementList();
        StringBuilder theStatement = new StringBuilder();

        theStatement = new StringBuilder("ALTER TABLE ");
        theStatement.append(escapeTableName(aTable.getName()));
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
}
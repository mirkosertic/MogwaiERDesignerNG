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
package de.erdesignerng.dialect.h2;

import de.erdesignerng.dialect.Statement;
import de.erdesignerng.dialect.StatementList;
import de.erdesignerng.dialect.sql92.SQL92SQLGenerator;
import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.Index;
import de.erdesignerng.model.Table;
import de.erdesignerng.modificationtracker.VetoException;

/**
 * @author $Author: gniddelgesciht $
 * @version $Date: 2008/06/13 16:49:00 $
 */
public class H2SQLGenerator extends SQL92SQLGenerator<H2Dialect> {

    public H2SQLGenerator(H2Dialect aDialect) {
        super(aDialect);
    }

    @Override
    public StatementList createRemoveIndexFromTableStatement(Table aTable, Index aIndex) throws VetoException {
        StatementList theResult = new StatementList();
        StringBuilder theStatement = new StringBuilder();

        theStatement.append("DROP INDEX ");
        theStatement.append(aIndex.getName());

        theResult.add(new Statement(theStatement.toString()));

        return theResult;
    }

    @Override
    public StatementList createRenameTableStatement(Table aTable, String aNewName) throws VetoException {

        StatementList theResult = new StatementList();
        StringBuilder theStatement = new StringBuilder();

        theStatement.append("ALTER TABLE ");
        theStatement.append(createUniqueTableName(aTable));
        theStatement.append(" RENAME TO ");

        theStatement.append(aNewName);

        theResult.add(new Statement(theStatement.toString()));

        return theResult;
    }

    @Override
    public StatementList createRenameAttributeStatement(Attribute aExistantAttribute, String aNewName)
            throws VetoException {

        Table theTable = aExistantAttribute.getOwner();

        StatementList theResult = new StatementList();
        StringBuilder theStatement = new StringBuilder();

        theStatement.append("ALTER TABLE ");
        theStatement.append(createUniqueTableName(theTable));
        theStatement.append(" ALTER COLUMN ");
        theStatement.append(aExistantAttribute.getName());
        theStatement.append(" RENAME TO ");
        theStatement.append(aNewName);

        theResult.add(new Statement(theStatement.toString()));

        return theResult;
    }

    @Override
    public StatementList createChangeAttributeStatement(Attribute aExistantAttribute, Attribute aNewAttribute)
            throws VetoException {

        Table theTable = aExistantAttribute.getOwner();

        StatementList theResult = new StatementList();

        StringBuilder theStatement = new StringBuilder();

        theStatement.append("ALTER TABLE ");
        theStatement.append(createUniqueTableName(theTable));
        theStatement.append(" ALTER COLUMN ");
        theStatement.append(aExistantAttribute.getName());
        theStatement.append(" ");
        theStatement.append(aNewAttribute.getPhysicalDeclaration());

        boolean isNullable = aNewAttribute.isNullable();

        if (!isNullable) {
            theStatement.append(" NOT NULL");
        } else {
            theStatement.append(" NULL");
        }

        theResult.add(new Statement(theStatement.toString()));

        return theResult;
    }

    @Override
    public StatementList createAddSchemaStatement(String aSchema) throws VetoException {
        StatementList theResult = new StatementList();
        StringBuilder theStatement = new StringBuilder();
        theStatement.append("CREATE SCHEMA ");
        theStatement.append(createUniqueSchemaName(aSchema));
        theStatement.append(" authorization DBA");
        theResult.add(new Statement(theStatement.toString()));
        return theResult;
    }
}
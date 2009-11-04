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
package de.erdesignerng.dialect.mssql;

import de.erdesignerng.dialect.Statement;
import de.erdesignerng.dialect.StatementList;
import de.erdesignerng.dialect.sql92.SQL92SQLGenerator;
import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.Relation;
import de.erdesignerng.model.Table;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-06-13 16:48:58 $
 */
public class MSSQLSQLGenerator extends SQL92SQLGenerator<MSSQLDialect> {

    public MSSQLSQLGenerator(MSSQLDialect aDialect) {
        super(aDialect);
    }

    @Override
    public StatementList createRemoveRelationStatement(Relation aRelation) {
        Table theImportingTable = aRelation.getImportingTable();

        StatementList theResult = new StatementList();
        theResult.add(new Statement("ALTER TABLE " + createUniqueTableName(theImportingTable) + " DROP CONSTRAINT "
                + aRelation.getName()));
        return theResult;
    }

    @Override
    public StatementList createRenameTableStatement(Table aTable, String aNewName) {

        StatementList theResult = new StatementList();
        theResult.add(new Statement("EXEC sp_rename '" + createUniqueTableName(aTable) + "' , '" + aNewName + "'"));
        return theResult;

    }

    @Override
    public StatementList createRenameAttributeStatement(Attribute aExistantAttribute, String aNewName) {

        Table theTable = aExistantAttribute.getOwner();

        StatementList theResult = new StatementList();
        theResult.add(new Statement("EXEC sp_rename '" + theTable.getName() + "." + aExistantAttribute.getName()
                + "' , '" + aNewName + "' , 'COLUMN'"));
        return theResult;
    }

    @Override
    public StatementList createChangeAttributeStatement(Attribute aExistantAttribute, Attribute aNewAttribute) {
        Table theTable = aExistantAttribute.getOwner();

        StatementList theResult = new StatementList();
        StringBuilder theStatement = new StringBuilder();

        theStatement.append("ALTER TABLE " + createUniqueTableName(theTable) + " ALTER COLUMN ");

        theStatement.append(aExistantAttribute.getName());
        theStatement.append(" ");
        theStatement.append(createAttributeDataDefinition(aNewAttribute, true));

        theResult.add(new Statement(theStatement.toString()));

        return theResult;
    }
}

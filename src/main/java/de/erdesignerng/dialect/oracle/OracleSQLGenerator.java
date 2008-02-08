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
package de.erdesignerng.dialect.oracle;

import de.erdesignerng.dialect.Statement;
import de.erdesignerng.dialect.StatementList;
import de.erdesignerng.dialect.sql92.SQL92SQLGenerator;
import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.Index;
import de.erdesignerng.model.Table;
import de.erdesignerng.modificationtracker.VetoException;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-02-08 19:38:19 $
 */
public class OracleSQLGenerator extends SQL92SQLGenerator<OracleDialect> {

    public OracleSQLGenerator(OracleDialect aDialect) {
        super(aDialect);
    }

    @Override
    protected String createAttributeDataDefinition(Attribute aAttribute) {
        StringBuilder theBuilder = new StringBuilder();
        theBuilder.append(aAttribute.getPhysicalDeclaration());
        boolean isNullable = aAttribute.isNullable();

        String theDefault = aAttribute.getDefaultValue();        
        boolean hasDefault = false;
        if ((theDefault != null) && (!"".equals(theDefault))) {
            hasDefault = true;
        }
        
        if ((!isNullable) && (!hasDefault)) {
            theBuilder.append(" NOT NULL");
        }
        
        if (hasDefault) {
            theBuilder.append(" DEFAULT ");
            theBuilder.append(theDefault);
        }
        
        String theExtra = aAttribute.getExtra();
        if ((theExtra != null) && (!"".equals(theExtra))) {
            theBuilder.append(" ");
            theBuilder.append(theExtra);
        }
        
        return theBuilder.toString();
    }    
    
    @Override
    public StatementList createRenameTableStatement(Table aTable, String aNewName) throws VetoException {

        StatementList theResult = new StatementList();
        StringBuilder theStatement = new StringBuilder();

        theStatement.append("ALTER TABLE ");
        theStatement.append(escapeTableName(aTable.getName()));
        theStatement.append(" RENAME TO ");

        theStatement.append(aNewName);

        theResult.add(new Statement(theStatement.toString()));

        return theResult;
    }

    @Override
    public StatementList createRenameAttributeStatement(Attribute aExistantAttribute, String aNewName) throws VetoException {
        
        Table theTable = aExistantAttribute.getOwner();
        
        StatementList theResult = new StatementList();
        StringBuilder theStatement = new StringBuilder();

        theStatement.append("ALTER TABLE ");
        theStatement.append(escapeTableName(theTable.getName()));
        theStatement.append(" RENAME COLUMN ");
        theStatement.append(aExistantAttribute.getName());
        theStatement.append(" TO ");
        theStatement.append(aNewName);

        theResult.add(new Statement(theStatement.toString()));

        return theResult;
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
    public StatementList createChangeAttributeStatement(Attribute aExistantAttribute, Attribute aNewAttribute)
            throws VetoException {
        Table theTable = aExistantAttribute.getOwner();

        StatementList theResult = new StatementList();
        StringBuilder theStatement = new StringBuilder();

        theStatement.append("ALTER TABLE " + escapeTableName(theTable.getName()) + " MODIFY ");

        theStatement.append(aExistantAttribute.getName());
        theStatement.append(" ");
        theStatement.append(createAttributeDataDefinition(aNewAttribute));

        theResult.add(new Statement(theStatement.toString()));

        return theResult;
    }    
}
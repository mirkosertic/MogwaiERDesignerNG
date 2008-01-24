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
package de.erdesignerng.test;

import java.sql.Connection;

import junit.framework.TestCase;
import de.erdesignerng.dialect.Statement;
import de.erdesignerng.dialect.StatementList;
import de.erdesignerng.exception.ElementAlreadyExistsException;
import de.erdesignerng.exception.ElementInvalidNameException;
import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.Domain;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.Table;
import de.erdesignerng.modificationtracker.HistoryModificationTracker;
import de.erdesignerng.modificationtracker.VetoException;

public abstract class BaseUseCases extends TestCase {

    protected Model model;

    protected Connection connection;

    protected class MyTracker extends HistoryModificationTracker {

        public MyTracker(Model aModel) {
            super(aModel);
        }

        @Override
        protected void addStatementsToHistory(StatementList aStatement) throws VetoException {
            handleStatements(aStatement);
        }

    };

    protected void handleStatements(StatementList aStatement) throws VetoException {
        if (aStatement != null) {
            for (Statement theStatement : aStatement) {
                String theSQL = theStatement.getSql();
                try {
                    connection.createStatement().execute(theSQL);
                } catch (Exception e) {
                    throw new VetoException(e);
                }
            }
        } else {
            throw new IllegalArgumentException("Statement may not be null!");
        }
    }
    
    public abstract Domain createCharDomain(String aName, int aLength);

    public void testCreateAndDropTable() throws ElementAlreadyExistsException, ElementInvalidNameException,
            VetoException {

        Domain theDomain = createCharDomain("TESTDOMAIN", 20);
        
        model.addDomain(theDomain);

        Table theTempTable = new Table();
        theTempTable.setName("testCreateAndDropTable");
        for (int i = 0; i < 5; i++) {
            Attribute theAttribute = new Attribute();
            theAttribute.setDomain(theDomain);
            theAttribute.setName("COLUMN_" + i);
            theAttribute.setPrimaryKey(i < 2);
            theTempTable.addAttribute(model, theAttribute);
        }

        model.addTable(theTempTable);
        model.removeTable(theTempTable);
    }
}

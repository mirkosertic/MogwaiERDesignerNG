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
import de.erdesignerng.model.Index;
import de.erdesignerng.model.IndexType;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.Relation;
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

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void handleStatements(StatementList aStatement) throws VetoException {
        if (aStatement != null) {
            for (Statement theStatement : aStatement) {
                String theSQL = theStatement.getSql();
                try {
                    System.out.println(theSQL);
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
        theTempTable.setName("test1");
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

    public void testDropAttribute() throws ElementAlreadyExistsException, ElementInvalidNameException, VetoException {

        Domain theDomain = createCharDomain("TESTDOMAIN", 20);

        model.addDomain(theDomain);

        Table theTempTable = new Table();
        theTempTable.setName("test2");
        for (int i = 0; i < 5; i++) {
            Attribute theAttribute = new Attribute();
            theAttribute.setDomain(theDomain);
            theAttribute.setName("COLUMN_" + i);
            theAttribute.setPrimaryKey(i < 2);
            theTempTable.addAttribute(model, theAttribute);
        }

        model.addTable(theTempTable);
        model.removeAttributeFromTable(theTempTable, theTempTable.getAttributes().get(0));

        model.removeTable(theTempTable);
    }

    public void testAddAttribute() throws ElementAlreadyExistsException, ElementInvalidNameException, VetoException {

        Domain theDomain = createCharDomain("TESTDOMAIN", 20);

        model.addDomain(theDomain);

        Table theTempTable = new Table();
        theTempTable.setName("test3");
        for (int i = 0; i < 5; i++) {
            Attribute theAttribute = new Attribute();
            theAttribute.setDomain(theDomain);
            theAttribute.setName("COLUMN_" + i);
            theAttribute.setPrimaryKey(i < 2);
            theTempTable.addAttribute(model, theAttribute);
        }

        Attribute theNewAttribute = new Attribute();
        theNewAttribute.setDomain(theDomain);
        theNewAttribute.setName("LALA");

        model.addTable(theTempTable);
        model.addAttributeToTable(theTempTable, theNewAttribute);

        model.removeTable(theTempTable);
    }

    public void testModifyAttribute() throws Exception {

        Domain theDomain = createCharDomain("TESTDOMAIN", 20);

        model.addDomain(theDomain);

        Table theTempTable = new Table();
        theTempTable.setName("test4");
        for (int i = 0; i < 5; i++) {
            Attribute theAttribute = new Attribute();
            theAttribute.setDomain(theDomain);
            theAttribute.setName("COLUMN_" + i);
            theAttribute.setPrimaryKey(i < 2);
            theTempTable.addAttribute(model, theAttribute);
        }

        model.addTable(theTempTable);

        Attribute theAttribute = theTempTable.getAttributes().get(0);

        Attribute theNewAttribute = theAttribute.clone();
        theNewAttribute.setDomain(createCharDomain("TESTDOMAIN2", 40));

        model.changeAttribute(theAttribute, theNewAttribute);

        model.removeTable(theTempTable);
    }

    public void testRenameAttribute() throws Exception {

        Domain theDomain = createCharDomain("TESTDOMAIN", 20);

        model.addDomain(theDomain);

        Table theTempTable = new Table();
        theTempTable.setName("test5");
        for (int i = 0; i < 5; i++) {
            Attribute theAttribute = new Attribute();
            theAttribute.setDomain(theDomain);
            theAttribute.setName("COLUMN_" + i);
            theAttribute.setPrimaryKey(i < 2);
            theTempTable.addAttribute(model, theAttribute);
        }

        model.addTable(theTempTable);

        Attribute theAttribute = theTempTable.getAttributes().get(0);

        model.renameAttribute(theAttribute, "LALA");

        model.removeAttributeFromTable(theTempTable, theAttribute);

        model.removeTable(theTempTable);
    }

    public void testRenameTable() throws ElementAlreadyExistsException, ElementInvalidNameException, VetoException {

        Domain theDomain = createCharDomain("TESTDOMAIN", 20);

        model.addDomain(theDomain);

        Table theTempTable = new Table();
        theTempTable.setName("test6");
        for (int i = 0; i < 5; i++) {
            Attribute theAttribute = new Attribute();
            theAttribute.setDomain(theDomain);
            theAttribute.setName("COLUMN_" + i);
            theAttribute.setPrimaryKey(i < 2);
            theTempTable.addAttribute(model, theAttribute);
        }

        model.addTable(theTempTable);

        model.renameTable(theTempTable, "lulu");

        model.removeTable(theTempTable);
    }

    public void testDropPrimaryKey() throws ElementAlreadyExistsException, ElementInvalidNameException, VetoException {

        Domain theDomain = createCharDomain("TESTDOMAIN", 20);

        model.addDomain(theDomain);

        Table theTempTable = new Table();
        theTempTable.setName("test7");
        for (int i = 0; i < 5; i++) {
            Attribute theAttribute = new Attribute();
            theAttribute.setDomain(theDomain);
            theAttribute.setName("COLUMN_" + i);
            theAttribute.setPrimaryKey(i < 2);
            theTempTable.addAttribute(model, theAttribute);
        }
        
        Index theIndex = new Index();
        theIndex.setName("TESTINDEX");
        theIndex.setIndexType(IndexType.PRIMARYKEY);
        theIndex.getAttributes().add(theTempTable.getAttributes().get(0));
        theTempTable.addIndex(model, theIndex);

        model.addTable(theTempTable);
        
        model.removeIndex(theTempTable, theIndex);
        
        model.removeTable(theTempTable);
    }
    
    public void testAddPrimaryKey() throws ElementAlreadyExistsException, ElementInvalidNameException, VetoException {

        Domain theDomain = createCharDomain("TESTDOMAIN", 20);

        model.addDomain(theDomain);

        Table theTempTable = new Table();
        theTempTable.setName("test8");
        for (int i = 0; i < 5; i++) {
            Attribute theAttribute = new Attribute();
            theAttribute.setDomain(theDomain);
            theAttribute.setName("COLUMN_" + i);
            theTempTable.addAttribute(model, theAttribute);
        }
        
        model.addTable(theTempTable);
        
        Index theIndex = new Index();
        theIndex.setName("PK");
        theIndex.setIndexType(IndexType.PRIMARYKEY);
        
        theIndex.getAttributes().add(theTempTable.getAttributes().get(0));
        theIndex.getAttributes().add(theTempTable.getAttributes().get(1));        
        
        model.addIndexToTable(theTempTable, theIndex);
        
        model.removeTable(theTempTable);
    }
    
    public void testAddUniqueAndNonUniqueIndex() throws ElementAlreadyExistsException, ElementInvalidNameException, VetoException {

        Domain theDomain = createCharDomain("TESTDOMAIN", 20);

        model.addDomain(theDomain);

        Table theTempTable = new Table();
        theTempTable.setName("test9");
        for (int i = 0; i < 5; i++) {
            Attribute theAttribute = new Attribute();
            theAttribute.setDomain(theDomain);
            theAttribute.setName("COLUMN_" + i);
            theTempTable.addAttribute(model, theAttribute);
        }
        
        model.addTable(theTempTable);
        
        Index theUniqueIndex = new Index();
        theUniqueIndex.setName("INX1");
        theUniqueIndex.setIndexType(IndexType.UNIQUE);
        
        theUniqueIndex.getAttributes().add(theTempTable.getAttributes().get(0));
        
        model.addIndexToTable(theTempTable, theUniqueIndex);

        Index theNonUnuqueIndex = new Index();
        theNonUnuqueIndex.setName("INX2");
        theNonUnuqueIndex.setIndexType(IndexType.NONUNIQUE);
        
        theNonUnuqueIndex.getAttributes().add(theTempTable.getAttributes().get(1));
        
        model.addIndexToTable(theTempTable, theNonUnuqueIndex);
        
        model.removeTable(theTempTable);
    }    
 
    public void testDropUniqueAndNonUniqueIndex() throws ElementAlreadyExistsException, ElementInvalidNameException, VetoException {

        Domain theDomain = createCharDomain("TESTDOMAIN", 20);

        model.addDomain(theDomain);

        Table theTempTable = new Table();
        theTempTable.setName("test10");
        for (int i = 0; i < 5; i++) {
            Attribute theAttribute = new Attribute();
            theAttribute.setDomain(theDomain);
            theAttribute.setName("COLUMN_" + i);
            theTempTable.addAttribute(model, theAttribute);
        }
        
        model.addTable(theTempTable);
        
        Index theUniqueIndex = new Index();
        theUniqueIndex.setName("INX1");
        theUniqueIndex.setIndexType(IndexType.UNIQUE);
        
        theUniqueIndex.getAttributes().add(theTempTable.getAttributes().get(0));
        
        model.addIndexToTable(theTempTable, theUniqueIndex);

        Index theNonUnuqueIndex = new Index();
        theNonUnuqueIndex.setName("INX2");
        theNonUnuqueIndex.setIndexType(IndexType.NONUNIQUE);
        
        theNonUnuqueIndex.getAttributes().add(theTempTable.getAttributes().get(1));
        
        model.addIndexToTable(theTempTable, theNonUnuqueIndex);
        
        model.removeIndex(theTempTable, theUniqueIndex);
        model.removeIndex(theTempTable, theNonUnuqueIndex);
        
        model.removeTable(theTempTable);
    }    

    public void testChangeIndex() throws Exception {

        Domain theDomain = createCharDomain("TESTDOMAIN", 20);

        model.addDomain(theDomain);

        Table theTempTable = new Table();
        theTempTable.setName("test11");
        for (int i = 0; i < 5; i++) {
            Attribute theAttribute = new Attribute();
            theAttribute.setDomain(theDomain);
            theAttribute.setName("COLUMN_" + i);
            theTempTable.addAttribute(model, theAttribute);
        }
        
        model.addTable(theTempTable);
        
        Index theUniqueIndex = new Index();
        theUniqueIndex.setName("INX1");
        theUniqueIndex.setIndexType(IndexType.UNIQUE);
        
        theUniqueIndex.getAttributes().add(theTempTable.getAttributes().get(0));
        
        model.addIndexToTable(theTempTable, theUniqueIndex);
        
        Index theClone = theUniqueIndex.clone();
        theClone.setName("LALAINDEX");
        theClone.setIndexType(IndexType.NONUNIQUE);
        
        model.changeIndex(theUniqueIndex, theClone);

        model.removeIndex(theTempTable, theUniqueIndex);
        
        model.removeTable(theTempTable);
    }    

    public void testAddRelation() throws Exception {

        Domain theDomain = createCharDomain("TESTDOMAIN", 20);

        model.addDomain(theDomain);

        Table theTableA = new Table();
        theTableA.setName("test12a");
        for (int i = 0; i < 5; i++) {
            Attribute theAttribute = new Attribute();
            theAttribute.setDomain(theDomain);
            theAttribute.setName("COLUMNA_" + i);
            theTableA.addAttribute(model, theAttribute);
        }
        
        Index theTableAIndex = new Index();
        theTableAIndex.setIndexType(IndexType.PRIMARYKEY);
        theTableAIndex.setName("test12a_pk");
        theTableAIndex.getAttributes().add(theTableA.getAttributes().get(0));
        theTableA.addIndex(model, theTableAIndex);

        Table theTableB = new Table();
        theTableB.setName("test12b");
        for (int i = 0; i < 5; i++) {
            Attribute theAttribute = new Attribute();
            theAttribute.setDomain(theDomain);
            theAttribute.setName("COLUMNB_" + i);
            theTableB.addAttribute(model, theAttribute);
        }
        
        Index theTableBIndex = new Index();
        theTableBIndex.setIndexType(IndexType.PRIMARYKEY);
        theTableBIndex.setName("test12b_pk");
        theTableBIndex.getAttributes().add(theTableB.getAttributes().get(0));
        theTableB.addIndex(model, theTableBIndex);
        
        model.addTable(theTableA);
        model.addTable(theTableB);
        
        Relation theRelation = new Relation();
        theRelation.setName("REL");
        theRelation.setExportingTable(theTableA);
        theRelation.setImportingTable(theTableB);
        theRelation.getMapping().put(theTableA.getAttributes().get(0), theTableB.getAttributes().get(0));
        
        model.removeTable(theTableB);
        model.removeTable(theTableA);        
    } 
    
    public void testDropRelation() throws Exception {

        Domain theDomain = createCharDomain("TESTDOMAIN", 20);

        model.addDomain(theDomain);

        Table theTableA = new Table();
        theTableA.setName("test13a");
        for (int i = 0; i < 5; i++) {
            Attribute theAttribute = new Attribute();
            theAttribute.setDomain(theDomain);
            theAttribute.setName("COLUMNA_" + i);
            theTableA.addAttribute(model, theAttribute);
        }
        
        Index theTableAIndex = new Index();
        theTableAIndex.setIndexType(IndexType.PRIMARYKEY);
        theTableAIndex.setName("test12a_pk");
        theTableAIndex.getAttributes().add(theTableA.getAttributes().get(0));
        theTableA.addIndex(model, theTableAIndex);

        Table theTableB = new Table();
        theTableB.setName("test13b");
        for (int i = 0; i < 5; i++) {
            Attribute theAttribute = new Attribute();
            theAttribute.setDomain(theDomain);
            theAttribute.setName("COLUMNB_" + i);
            theTableB.addAttribute(model, theAttribute);
        }
        
        Index theTableBIndex = new Index();
        theTableBIndex.setIndexType(IndexType.PRIMARYKEY);
        theTableBIndex.setName("test13b_pk");
        theTableBIndex.getAttributes().add(theTableB.getAttributes().get(0));
        theTableB.addIndex(model, theTableBIndex);
        
        model.addTable(theTableA);
        model.addTable(theTableB);
        
        Relation theRelation = new Relation();
        theRelation.setName("REL");
        theRelation.setExportingTable(theTableA);
        theRelation.setImportingTable(theTableB);
        theRelation.getMapping().put(theTableA.getAttributes().get(0), theTableB.getAttributes().get(0));
        
        model.addRelation(theRelation);
        
        model.removeRelation(theRelation);
        
        model.removeTable(theTableB);
        model.removeTable(theTableA);        
    }    
    
    public void testChangeRelation() throws Exception {

        Domain theDomain = createCharDomain("TESTDOMAIN", 20);

        model.addDomain(theDomain);

        Table theTableA = new Table();
        theTableA.setName("test14a");
        for (int i = 0; i < 5; i++) {
            Attribute theAttribute = new Attribute();
            theAttribute.setDomain(theDomain);
            theAttribute.setName("COLUMNA_" + i);
            theTableA.addAttribute(model, theAttribute);
        }
        
        Index theTableAIndex = new Index();
        theTableAIndex.setIndexType(IndexType.PRIMARYKEY);
        theTableAIndex.setName("test14a_pk");
        theTableAIndex.getAttributes().add(theTableA.getAttributes().get(0));
        theTableA.addIndex(model, theTableAIndex);

        Table theTableB = new Table();
        theTableB.setName("test14b");
        for (int i = 0; i < 5; i++) {
            Attribute theAttribute = new Attribute();
            theAttribute.setDomain(theDomain);
            theAttribute.setName("COLUMNB_" + i);
            theTableB.addAttribute(model, theAttribute);
        }
        
        Index theTableBIndex = new Index();
        theTableBIndex.setIndexType(IndexType.PRIMARYKEY);
        theTableBIndex.setName("test14b_pk");
        theTableBIndex.getAttributes().add(theTableB.getAttributes().get(0));
        theTableB.addIndex(model, theTableBIndex);
        
        model.addTable(theTableA);
        model.addTable(theTableB);
        
        Relation theRelation = new Relation();
        theRelation.setName("REL");
        theRelation.setExportingTable(theTableA);
        theRelation.setImportingTable(theTableB);
        theRelation.getMapping().put(theTableA.getAttributes().get(0), theTableB.getAttributes().get(0));
        
        model.addRelation(theRelation);

        Relation theClone = theRelation.clone();
        theClone.setName("LALAREL");
        model.changeRelation(theRelation, theClone);
        
        model.removeRelation(theRelation);
        
        model.removeTable(theTableB);
        model.removeTable(theTableA);        
    }    
}
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

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.Types;

import junit.framework.TestCase;
import de.erdesignerng.dialect.DataType;
import de.erdesignerng.dialect.Statement;
import de.erdesignerng.dialect.StatementList;
import de.erdesignerng.exception.ElementAlreadyExistsException;
import de.erdesignerng.exception.ElementInvalidNameException;
import de.erdesignerng.model.Attribute;
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
                java.sql.Statement theJDBCStatement = null;
                try {
                    theJDBCStatement = connection.createStatement();
                    theJDBCStatement.execute(theSQL);
                    assertTrue(theSQL, true);
                    System.out.println(theSQL);
                } catch (Exception e) {
                    throw new VetoException(theStatement.getSql(), e);
                } finally {
                    if (theJDBCStatement != null) {
                        try {
                            theJDBCStatement.close();
                        } catch (Exception e) {
                            // Do nothing here
                        }
                    }
                }
            }
        } else {
            throw new IllegalArgumentException("Statement may not be null!");
        }
    }

    public abstract void setTextAttribute(Attribute aAttribute);

    public String getTypeName(int aIntValue) throws IllegalAccessException {
        Class<Types> theClass = java.sql.Types.class;
        for (Field theField : theClass.getFields()) {
            int theValue = theField.getInt(theClass);
            if (aIntValue == theValue) {
                return theClass.getName() + "." + theField.getName();
            }
        }
        return "";
    }

    public void testCreateTableWithAllDatatypes() throws ElementAlreadyExistsException, ElementInvalidNameException,
            VetoException {

        Table theTable = new Table();
        theTable.setName("testTable");
        int i = 0;
        for (DataType theType : model.getDialect().getDataTypes()) {
            if ((!theType.isIdentity()) && (theType.getMaxOccoursPerTable() < 0)) {

                Attribute theAttribute = new Attribute();
                theAttribute.setName("ATTR_" + i);
                setTextAttribute(theAttribute);

                theTable.getAttributes().add(theAttribute);
                i++;
            }
        }

        model.addTable(theTable);
        model.removeTable(theTable);
    }

    public void testCreateAndDropTable() throws ElementAlreadyExistsException, ElementInvalidNameException,
            VetoException {

        Table theTempTable = new Table();
        theTempTable.setName("test1");
        for (int i = 0; i < 5; i++) {
            Attribute theAttribute = new Attribute();
            setTextAttribute(theAttribute);
            theAttribute.setName("COLUMN_" + i);
            theTempTable.addAttribute(model, theAttribute);
        }

        model.addTable(theTempTable);
        model.removeTable(theTempTable);
    }

    public void testDropAttribute() throws ElementAlreadyExistsException, ElementInvalidNameException, VetoException {

        Table theTempTable = new Table();
        theTempTable.setName("test2");
        for (int i = 0; i < 5; i++) {
            Attribute theAttribute = new Attribute();
            setTextAttribute(theAttribute);
            theAttribute.setName("COLUMN_" + i);
            theTempTable.addAttribute(model, theAttribute);
        }

        model.addTable(theTempTable);
        model.removeAttributeFromTable(theTempTable, theTempTable.getAttributes().get(0));

        model.removeTable(theTempTable);
    }

    public void testAddAttribute() throws ElementAlreadyExistsException, ElementInvalidNameException, VetoException {

        Table theTempTable = new Table();
        theTempTable.setName("test3");
        for (int i = 0; i < 5; i++) {
            Attribute theAttribute = new Attribute();
            setTextAttribute(theAttribute);
            theAttribute.setName("COLUMN_" + i);
            theTempTable.addAttribute(model, theAttribute);
        }

        Attribute theNewAttribute = new Attribute();
        setTextAttribute(theNewAttribute);
        theNewAttribute.setName("LALA");

        model.addTable(theTempTable);
        model.addAttributeToTable(theTempTable, theNewAttribute);

        model.removeTable(theTempTable);
    }

    public void testModifyAttribute() throws Exception {

        Table theTempTable = new Table();
        theTempTable.setName("test4");
        for (int i = 0; i < 5; i++) {
            Attribute theAttribute = new Attribute();
            setTextAttribute(theAttribute);
            theAttribute.setName("COLUMN_" + i);
            theTempTable.addAttribute(model, theAttribute);
        }

        model.addTable(theTempTable);

        Attribute theAttribute = theTempTable.getAttributes().get(0);

        Attribute theNewAttribute = theAttribute.clone();
        setTextAttribute(theNewAttribute);

        model.changeAttribute(theAttribute, theNewAttribute);

        model.removeTable(theTempTable);
    }

    public void testRenameAttribute() throws Exception {

        Table theTempTable = new Table();
        theTempTable.setName("test5");
        for (int i = 0; i < 5; i++) {
            Attribute theAttribute = new Attribute();
            setTextAttribute(theAttribute);
            theAttribute.setName("COLUMN_" + i);
            theTempTable.addAttribute(model, theAttribute);
        }

        model.addTable(theTempTable);

        Attribute theAttribute = theTempTable.getAttributes().get(0);

        model.renameAttribute(theAttribute, "LALA");

        model.removeAttributeFromTable(theTempTable, theAttribute);

        model.removeTable(theTempTable);
    }

    public void testRenameTable() throws ElementAlreadyExistsException, ElementInvalidNameException, VetoException {

        Table theTempTable = new Table();
        theTempTable.setName("test6");
        for (int i = 0; i < 5; i++) {
            Attribute theAttribute = new Attribute();
            setTextAttribute(theAttribute);
            theAttribute.setName("COLUMN_" + i);
            theTempTable.addAttribute(model, theAttribute);
        }

        model.addTable(theTempTable);

        model.renameTable(theTempTable, "lulu");

        model.removeTable(theTempTable);
    }

    public void testDropPrimaryKey() throws ElementAlreadyExistsException, ElementInvalidNameException, VetoException {

        Table theTempTable = new Table();
        theTempTable.setName("test7");
        for (int i = 0; i < 5; i++) {
            Attribute theAttribute = new Attribute();
            setTextAttribute(theAttribute);
            theAttribute.setName("COLUMN_" + i);
            theAttribute.setNullable(false);
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

        Table theTempTable = new Table();
        theTempTable.setName("test8");
        for (int i = 0; i < 5; i++) {
            Attribute theAttribute = new Attribute();
            setTextAttribute(theAttribute);
            theAttribute.setName("COLUMN_" + i);
            theAttribute.setNullable(false);
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

    public void testAddUniqueAndNonUniqueIndex() throws ElementAlreadyExistsException, ElementInvalidNameException,
            VetoException {

        Table theTempTable = new Table();
        theTempTable.setName("test9");
        for (int i = 0; i < 5; i++) {
            Attribute theAttribute = new Attribute();
            setTextAttribute(theAttribute);
            theAttribute.setName("COLUMN_" + i);
            theTempTable.addAttribute(model, theAttribute);
        }

        model.addTable(theTempTable);

        Index theUniqueIndex = new Index();
        theUniqueIndex.setName("test9_idx1");
        theUniqueIndex.setIndexType(IndexType.UNIQUE);

        theUniqueIndex.getAttributes().add(theTempTable.getAttributes().get(0));

        model.addIndexToTable(theTempTable, theUniqueIndex);

        Index theNonUnuqueIndex = new Index();
        theNonUnuqueIndex.setName("test9_idx2");
        theNonUnuqueIndex.setIndexType(IndexType.NONUNIQUE);

        theNonUnuqueIndex.getAttributes().add(theTempTable.getAttributes().get(1));

        model.addIndexToTable(theTempTable, theNonUnuqueIndex);

        model.removeTable(theTempTable);
    }

    public void testDropUniqueAndNonUniqueIndex() throws ElementAlreadyExistsException, ElementInvalidNameException,
            VetoException {

        Table theTempTable = new Table();
        theTempTable.setName("test10");
        for (int i = 0; i < 5; i++) {
            Attribute theAttribute = new Attribute();
            setTextAttribute(theAttribute);
            theAttribute.setName("COLUMN_" + i);
            theTempTable.addAttribute(model, theAttribute);
        }

        model.addTable(theTempTable);

        Index theUniqueIndex = new Index();
        theUniqueIndex.setName("test10_idx1");
        theUniqueIndex.setIndexType(IndexType.UNIQUE);

        theUniqueIndex.getAttributes().add(theTempTable.getAttributes().get(0));

        model.addIndexToTable(theTempTable, theUniqueIndex);

        Index theNonUnuqueIndex = new Index();
        theNonUnuqueIndex.setName("test10_idx2");
        theNonUnuqueIndex.setIndexType(IndexType.NONUNIQUE);

        theNonUnuqueIndex.getAttributes().add(theTempTable.getAttributes().get(1));

        model.addIndexToTable(theTempTable, theNonUnuqueIndex);

        model.removeIndex(theTempTable, theUniqueIndex);
        model.removeIndex(theTempTable, theNonUnuqueIndex);

        model.removeTable(theTempTable);
    }

    public void testChangeIndex() throws Exception {

        Table theTempTable = new Table();
        theTempTable.setName("test11");
        for (int i = 0; i < 5; i++) {
            Attribute theAttribute = new Attribute();
            setTextAttribute(theAttribute);
            theAttribute.setName("COLUMN_" + i);
            theTempTable.addAttribute(model, theAttribute);
        }

        model.addTable(theTempTable);

        Index theUniqueIndex = new Index();
        theUniqueIndex.setName("test11_idx1");
        theUniqueIndex.setIndexType(IndexType.UNIQUE);

        theUniqueIndex.getAttributes().add(theTempTable.getAttributes().get(0));

        model.addIndexToTable(theTempTable, theUniqueIndex);

        Index theClone = theUniqueIndex.clone();
        theClone.setName("test11_idx2");
        theClone.setIndexType(IndexType.NONUNIQUE);

        model.changeIndex(theUniqueIndex, theClone);

        model.removeIndex(theTempTable, theUniqueIndex);

        model.removeTable(theTempTable);
    }

    public void testAddRelation() throws Exception {

        Table theTableA = new Table();
        theTableA.setName("test12a");
        for (int i = 0; i < 5; i++) {
            Attribute theAttribute = new Attribute();
            setTextAttribute(theAttribute);
            theAttribute.setName("COLUMNA_" + i);
            theAttribute.setNullable(false);
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
            setTextAttribute(theAttribute);
            theAttribute.setName("COLUMNB_" + i);
            theAttribute.setNullable(false);
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
        model.addRelation(theRelation);

        model.removeTable(theTableB);
        model.removeTable(theTableA);
    }

    public void testDropRelation() throws Exception {

        Table theTableA = new Table();
        theTableA.setName("test13a");
        for (int i = 0; i < 5; i++) {
            Attribute theAttribute = new Attribute();
            setTextAttribute(theAttribute);
            theAttribute.setName("COLUMNA_" + i);
            theAttribute.setNullable(false);
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
            setTextAttribute(theAttribute);
            theAttribute.setName("COLUMNB_" + i);
            theAttribute.setNullable(false);
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

        Table theTableA = new Table();
        theTableA.setName("test14a");
        for (int i = 0; i < 5; i++) {
            Attribute theAttribute = new Attribute();
            setTextAttribute(theAttribute);
            theAttribute.setName("COLUMNA_" + i);
            theAttribute.setNullable(false);
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
            setTextAttribute(theAttribute);
            theAttribute.setName("COLUMNB_" + i);
            theAttribute.setNullable(false);
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

    public void testCreateTableWithDefaultValues() throws ElementAlreadyExistsException, ElementInvalidNameException,
            VetoException {

        Table theTable = new Table();
        theTable.setName("test15");

        Attribute theAttribute1 = new Attribute();
        theAttribute1.setName("AT1");
        setTextAttribute(theAttribute1);
        theTable.addAttribute(model, theAttribute1);

        Attribute theAttribute2 = new Attribute();
        theAttribute2.setName("AT2");
        theAttribute2.setNullable(false);
        setTextAttribute(theAttribute2);
        theTable.addAttribute(model, theAttribute2);

        Attribute theAttribute3 = new Attribute();
        theAttribute3.setName("AT3");
        theAttribute3.setDefaultValue("'LALA'");
        setTextAttribute(theAttribute3);
        theTable.addAttribute(model, theAttribute3);

        Attribute theAttribute4 = new Attribute();
        theAttribute4.setName("AT4");
        theAttribute4.setNullable(false);
        theAttribute4.setDefaultValue("'LALA'");
        setTextAttribute(theAttribute4);
        theTable.addAttribute(model, theAttribute4);

        model.addTable(theTable);
        model.removeTable(theTable);
    }

    public void testCreateTableChangeDefaultChangeNullable() throws Exception {

        Table theTable = new Table();
        theTable.setName("test16");

        Attribute theAttribute1 = new Attribute();
        theAttribute1.setName("AT1");
        setTextAttribute(theAttribute1);
        theTable.addAttribute(model, theAttribute1);

        Attribute theAttribute2 = new Attribute();
        theAttribute2.setName("AT2");
        theAttribute2.setNullable(false);
        setTextAttribute(theAttribute2);
        theTable.addAttribute(model, theAttribute2);

        Attribute theAttribute3 = new Attribute();
        theAttribute3.setName("AT3");
        theAttribute3.setDefaultValue("'LALA'");
        setTextAttribute(theAttribute3);
        theTable.addAttribute(model, theAttribute3);

        Attribute theAttribute4 = new Attribute();
        theAttribute4.setName("AT4");
        theAttribute4.setNullable(false);
        theAttribute4.setDefaultValue("'LALA'");
        setTextAttribute(theAttribute4);
        theTable.addAttribute(model, theAttribute4);

        model.addTable(theTable);
        
        Attribute theClone = theAttribute1.clone();
        theClone.setDefaultValue("'LULU'");
        model.changeAttribute(theAttribute1, theClone);

        theClone = theAttribute2.clone();
        theClone.setDefaultValue("'LULU'");
        model.changeAttribute(theAttribute2, theClone);

        theClone = theAttribute3.clone();
        theClone.setDefaultValue(null);
        model.changeAttribute(theAttribute3, theClone);

        theClone = theAttribute4.clone();
        theClone.setDefaultValue(null);
        model.changeAttribute(theAttribute4, theClone);

        model.removeTable(theTable);
    }
}
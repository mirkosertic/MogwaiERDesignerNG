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
package de.erdesignerng.test.sql;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import de.erdesignerng.dialect.DataType;
import de.erdesignerng.dialect.Dialect;
import de.erdesignerng.dialect.SQLGenerator;
import de.erdesignerng.dialect.Statement;
import de.erdesignerng.dialect.StatementList;
import de.erdesignerng.exception.ElementAlreadyExistsException;
import de.erdesignerng.exception.ElementInvalidNameException;
import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.CascadeType;
import de.erdesignerng.model.Index;
import de.erdesignerng.model.IndexType;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.Relation;
import de.erdesignerng.model.Table;
import de.erdesignerng.modificationtracker.HistoryModificationTracker;
import de.erdesignerng.modificationtracker.VetoException;
import de.erdesignerng.test.BaseERDesignerTestCaseImpl;

/**
 * Base test class for all sql generators. 
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2009-02-24 19:36:27 $
 */
public abstract class AbstractDialectTestCase extends BaseERDesignerTestCaseImpl {

    protected Dialect dialect;
    protected DataType textDataType;
    protected DataType intDataType;
    protected String basePath;    

    protected Table createReferenceTable(Model aModel, String aName, boolean aWithIndexes)
            throws ElementAlreadyExistsException, ElementInvalidNameException {
        Table theTable = new Table();
        theTable.setName(aName);

        // Create Primary Key
        Index thePrimaryKey = new Index();
        thePrimaryKey.setName(aName + "_PK");
        thePrimaryKey.setIndexType(IndexType.PRIMARYKEY);

        Attribute thePK1Attribute = new Attribute();
        thePK1Attribute.setName("PK1");
        thePK1Attribute.setDatatype(textDataType);
        thePK1Attribute.setSize(10);
        thePK1Attribute.setNullable(false);
        theTable.addAttribute(aModel, thePK1Attribute);

        Attribute thePK2Attribute = new Attribute();
        thePK2Attribute.setName("PK2");
        thePK2Attribute.setDatatype(intDataType);
        thePK2Attribute.setNullable(false);
        thePK2Attribute.setDefaultValue("42");
        theTable.addAttribute(aModel, thePK2Attribute);

        thePrimaryKey.getExpressions().addExpressionFor(thePK1Attribute);
        thePrimaryKey.getExpressions().addExpressionFor(thePK2Attribute);

        if (aWithIndexes) {
            theTable.addIndex(aModel, thePrimaryKey);
        }

        // No PK attribute
        Attribute theAt1 = new Attribute();
        theAt1.setName("AT1");
        theAt1.setDatatype(textDataType);
        theAt1.setSize(5);
        theAt1.setNullable(true);
        theTable.addAttribute(aModel, theAt1);

        // Non Unique Indexed attribute
        Attribute theAt2 = new Attribute();
        theAt2.setName("AT2");
        theAt2.setDatatype(textDataType);
        theAt2.setSize(5);
        theAt2.setNullable(false);
        theTable.addAttribute(aModel, theAt2);

        Index theNonUniqueIndex = new Index();
        theNonUniqueIndex.setName(aName + "_IDX1");
        theNonUniqueIndex.setIndexType(IndexType.NONUNIQUE);
        theNonUniqueIndex.getExpressions().addExpressionFor(theAt2);

        if (aWithIndexes) {
            theTable.addIndex(aModel, theNonUniqueIndex);
        }

        // Unique Indexed attribute
        Attribute theAt3 = new Attribute();
        theAt3.setName("AT3");
        theAt3.setDatatype(textDataType);
        theAt3.setSize(5);
        theAt3.setNullable(true);
        theTable.addAttribute(aModel, theAt3);

        Index theUniqueIndex = new Index();
        theUniqueIndex.setName(aName + "_IDX2");
        theUniqueIndex.setIndexType(IndexType.UNIQUE);
        theUniqueIndex.getExpressions().addExpressionFor(theAt3);
        if (aWithIndexes) {
            theTable.addIndex(aModel, theUniqueIndex);
        }
        return theTable;
    }

    protected String statementListToString(StatementList aStatements, SQLGenerator aGenerator) {
        StringWriter theStringWriter = new StringWriter();
        PrintWriter thePrintWriter = new PrintWriter(theStringWriter);
        for (Statement theStatement : aStatements) {
            thePrintWriter.print(theStatement.getSql());
            thePrintWriter.println(aGenerator.createScriptStatementSeparator());
        }
        thePrintWriter.flush();
        return theStringWriter.toString().trim();
    }
    
    public void testCreateTableWithPKAndIndex() throws ElementAlreadyExistsException, ElementInvalidNameException,
            VetoException, IOException {

        Model theModel = new Model();
        theModel.setDialect(dialect);

        Table theTable = createReferenceTable(theModel, "TESTTABLE", true);

        theModel.addTable(theTable);

        HistoryModificationTracker theTracker = new HistoryModificationTracker(theModel);
        theModel.setModificationTracker(theTracker);

        SQLGenerator theGenerator = dialect.createSQLGenerator();
        StatementList theStatementList = theGenerator.createCreateAllObjects(theModel);

        String theStatements = statementListToString(theStatementList, theGenerator);
        String theReference = readResourceFile(basePath + "testCreateTableWithPKAndIndex.sql");

        //System.out.println(theStatements);
        assertTrue(theStatements.equals(theReference));
    }

    public void testRenameAttribute() throws ElementAlreadyExistsException, ElementInvalidNameException, VetoException,
            IOException {

        Model theModel = new Model();
        theModel.setDialect(dialect);

        Table theTable = createReferenceTable(theModel, "TESTTABLE", true);

        theModel.addTable(theTable);

        HistoryModificationTracker theTracker = new HistoryModificationTracker(theModel);
        theModel.setModificationTracker(theTracker);

        // Modify the model here
        theModel.renameAttribute(theTable.getAttributes().findByName("AT1"), "AT1_NEW");

        SQLGenerator theGenerator = dialect.createSQLGenerator();
        StatementList theStatementList = theTracker.getStatements();

        String theStatements = statementListToString(theStatementList, theGenerator);
        // System.out.println(theStatements);

        String theReference = readResourceFile(basePath + "testRenameAttribute.sql");

        assertTrue(theStatements.equals(theReference));

    }

    public void testAddAttribute() throws ElementAlreadyExistsException, ElementInvalidNameException, VetoException,
            IOException {

        Model theModel = new Model();
        theModel.setDialect(dialect);

        Table theTable = createReferenceTable(theModel, "TESTTABLE", true);

        theModel.addTable(theTable);

        HistoryModificationTracker theTracker = new HistoryModificationTracker(theModel);
        theModel.setModificationTracker(theTracker);

        // Modify the model here
        Attribute theAt1 = new Attribute();
        theAt1.setName("AT5");
        theAt1.setDatatype(textDataType);
        theAt1.setSize(5);
        theAt1.setNullable(false);

        theModel.addAttributeToTable(theTable, theAt1);

        SQLGenerator theGenerator = dialect.createSQLGenerator();
        StatementList theStatementList = theTracker.getStatements();

        String theStatements = statementListToString(theStatementList, theGenerator);
        // System.out.println(theStatements);

        String theReference = readResourceFile(basePath + "testAddAttribute.sql");

        assertTrue(theStatements.equals(theReference));

    }

    public void testRenameTable() throws ElementAlreadyExistsException, ElementInvalidNameException, VetoException,
            IOException {

        Model theModel = new Model();
        theModel.setDialect(dialect);

        Table theTable = createReferenceTable(theModel, "TESTTABLE", true);

        theModel.addTable(theTable);

        HistoryModificationTracker theTracker = new HistoryModificationTracker(theModel);
        theModel.setModificationTracker(theTracker);

        // Modify the model here
        theModel.renameTable(theTable, "TESTTABLE1");

        SQLGenerator theGenerator = dialect.createSQLGenerator();
        StatementList theStatementList = theTracker.getStatements();

        String theStatements = statementListToString(theStatementList, theGenerator);
        // System.out.println(theStatements);

        String theReference = readResourceFile(basePath + "testRenameTable.sql");

        assertTrue(theStatements.equals(theReference));

    }

    public void testDropIndex() throws ElementAlreadyExistsException, ElementInvalidNameException, VetoException,
            IOException {

        Model theModel = new Model();
        theModel.setDialect(dialect);

        Table theTable = createReferenceTable(theModel, "TESTTABLE", true);

        theModel.addTable(theTable);

        HistoryModificationTracker theTracker = new HistoryModificationTracker(theModel);
        theModel.setModificationTracker(theTracker);

        // Modify the model here
        theModel.removeIndex(theTable, theTable.getPrimarykey());
        theModel.removeIndex(theTable, theTable.getIndexes().findByName("TESTTABLE_IDX1"));

        SQLGenerator theGenerator = dialect.createSQLGenerator();
        StatementList theStatementList = theTracker.getStatements();

        String theStatements = statementListToString(theStatementList, theGenerator);
        // System.out.println(theStatements);

        String theReference = readResourceFile(basePath + "testDropIndex.sql");

        assertTrue(theStatements.equals(theReference));

    }

    public void testChangeAttribute() throws Exception {

        Model theModel = new Model();
        theModel.setDialect(dialect);

        Table theTable = createReferenceTable(theModel, "TESTTABLE", true);

        theModel.addTable(theTable);

        HistoryModificationTracker theTracker = new HistoryModificationTracker(theModel);
        theModel.setModificationTracker(theTracker);

        // Modify the model here
        Attribute theNewInfo = new Attribute();
        theNewInfo.setName("ATTX");
        theNewInfo.setNullable(false);
        theNewInfo.setDatatype(intDataType);
        theModel.changeAttribute(theTable.getAttributes().findByName("AT1"), theNewInfo);

        SQLGenerator theGenerator = dialect.createSQLGenerator();
        StatementList theStatementList = theTracker.getStatements();

        String theStatements = statementListToString(theStatementList, theGenerator);
        // System.out.println(theStatements);

        String theReference = readResourceFile(basePath + "testChangeAttribute.sql");

        assertTrue(theStatements.equals(theReference));

    }

    public void testCreateRelation() throws Exception {

        Model theModel = new Model();
        theModel.setDialect(dialect);

        Table theExporting = createReferenceTable(theModel, "TESTTABLE1", true);
        Table theImporting = createReferenceTable(theModel, "TESTTABLE2", true);

        theModel.addTable(theExporting);
        theModel.addTable(theImporting);

        Relation theRelation = new Relation();
        theRelation.setExportingTable(theExporting);
        theRelation.setImportingTable(theImporting);
        theRelation.setName("REL_1");
        theRelation.setOnDelete(CascadeType.CASCADE);
        theRelation.setOnUpdate(CascadeType.CASCADE);
        
        Index theExportingPrimaryKey = theExporting.getPrimarykey();
        theRelation.getMapping().put(theExportingPrimaryKey.getExpressions().findByAttributeName("PK1"),
                theImporting.getAttributes().findByName("PK1"));
        theRelation.getMapping().put(theExportingPrimaryKey.getExpressions().findByAttributeName("PK2"),
                theImporting.getAttributes().findByName("PK2"));

        HistoryModificationTracker theTracker = new HistoryModificationTracker(theModel);
        theModel.setModificationTracker(theTracker);

        // Modify the model here
        theModel.addRelation(theRelation);

        SQLGenerator theGenerator = dialect.createSQLGenerator();
        StatementList theStatementList = theTracker.getStatements();

        String theStatements = statementListToString(theStatementList, theGenerator);
        // System.out.println(theStatements);

        String theReference = readResourceFile(basePath + "testCreateRelation.sql");

        assertTrue(theStatements.equals(theReference));

    }

    public void testDropRelation() throws Exception {

        Model theModel = new Model();
        theModel.setDialect(dialect);

        Table theExporting = createReferenceTable(theModel, "TESTTABLE1", true);
        Table theImporting = createReferenceTable(theModel, "TESTTABLE2", true);

        theModel.addTable(theExporting);
        theModel.addTable(theImporting);

        Relation theRelation = new Relation();
        theRelation.setExportingTable(theExporting);
        theRelation.setImportingTable(theImporting);
        theRelation.setName("REL_1");
        theRelation.setOnDelete(CascadeType.CASCADE);
        theRelation.setOnUpdate(CascadeType.CASCADE);
        
        Index theExportingPrimaryKey = theExporting.getPrimarykey();
        theRelation.getMapping().put(theExportingPrimaryKey.getExpressions().findByAttributeName("PK1"),
                theImporting.getAttributes().findByName("PK1"));
        theRelation.getMapping().put(theExportingPrimaryKey.getExpressions().findByAttributeName("PK2"),
                theImporting.getAttributes().findByName("PK2"));

        theModel.addRelation(theRelation);

        HistoryModificationTracker theTracker = new HistoryModificationTracker(theModel);
        theModel.setModificationTracker(theTracker);

        // Modify the model here
        theModel.removeRelation(theRelation);

        SQLGenerator theGenerator = dialect.createSQLGenerator();
        StatementList theStatementList = theTracker.getStatements();

        String theStatements = statementListToString(theStatementList, theGenerator);
        // System.out.println(theStatements);

        String theReference = readResourceFile(basePath + "testDropRelation.sql");

        assertTrue(theStatements.equals(theReference));
    }

    public void testDropAttribute() throws Exception {

        Model theModel = new Model();
        theModel.setDialect(dialect);

        Table theTable = createReferenceTable(theModel, "TESTTABLE1", true);

        HistoryModificationTracker theTracker = new HistoryModificationTracker(theModel);
        theModel.setModificationTracker(theTracker);

        // Modify the model here
        theModel.removeAttributeFromTable(theTable, theTable.getAttributes().findByName("AT1"));

        SQLGenerator theGenerator = dialect.createSQLGenerator();
        StatementList theStatementList = theTracker.getStatements();

        String theStatements = statementListToString(theStatementList, theGenerator);
        // System.out.println(theStatements);

        String theReference = readResourceFile(basePath + "testDropAttribute.sql");

        assertTrue(theStatements.equals(theReference));
    }

    public void testDropTable() throws Exception {

        Model theModel = new Model();
        theModel.setDialect(dialect);

        Table theTable = createReferenceTable(theModel, "TESTTABLE1", true);

        HistoryModificationTracker theTracker = new HistoryModificationTracker(theModel);
        theModel.setModificationTracker(theTracker);

        // Modify the model here
        theModel.removeTable(theTable);

        SQLGenerator theGenerator = dialect.createSQLGenerator();
        StatementList theStatementList = theTracker.getStatements();

        String theStatements = statementListToString(theStatementList, theGenerator);
        // System.out.println(theStatements);

        String theReference = readResourceFile(basePath + "testDropTable.sql");

        assertTrue(theStatements.equals(theReference));
    }

    public void testChangeIndex() throws Exception {

        Model theModel = new Model();
        theModel.setDialect(dialect);

        Table theTable = createReferenceTable(theModel, "TESTTABLE", true);

        theModel.addTable(theTable);

        HistoryModificationTracker theTracker = new HistoryModificationTracker(theModel);
        theModel.setModificationTracker(theTracker);

        Index theIndex = theTable.getPrimarykey();
        Index theClone = theIndex.clone();
        theClone.setName("TESTPK");

        theModel.changeIndex(theIndex, theClone);

        theIndex = theTable.getIndexes().findByName("TESTTABLE_IDX1");
        theClone = theIndex.clone();
        theClone.setName("TESTINDEX");

        theModel.changeIndex(theIndex, theClone);

        theIndex = theTable.getIndexes().findByName("TESTTABLE_IDX2");
        theClone = theIndex.clone();
        theClone.setName("TESTINDEX2");

        theModel.changeIndex(theIndex, theClone);

        SQLGenerator theGenerator = dialect.createSQLGenerator();
        StatementList theStatementList = theTracker.getStatements();

        String theStatements = statementListToString(theStatementList, theGenerator);
        // System.out.println(theStatements);

        String theReference = readResourceFile(basePath + "testChangeIndex.sql");

        assertTrue(theStatements.equals(theReference));

    }

    public void testChangeRelation() throws Exception {

        Model theModel = new Model();
        theModel.setDialect(dialect);

        Table theExporting = createReferenceTable(theModel, "TESTTABLE1", true);
        Table theImporting = createReferenceTable(theModel, "TESTTABLE2", true);

        theModel.addTable(theExporting);
        theModel.addTable(theImporting);

        Relation theRelation = new Relation();
        theRelation.setExportingTable(theExporting);
        theRelation.setImportingTable(theImporting);
        theRelation.setName("REL_1");
        theRelation.setOnDelete(CascadeType.CASCADE);
        theRelation.setOnUpdate(CascadeType.CASCADE);
        
        Index theExportingPrimaryKey = theExporting.getPrimarykey();
        theRelation.getMapping().put(theExportingPrimaryKey.getExpressions().findByAttributeName("PK1"),
                theImporting.getAttributes().findByName("PK1"));
        theRelation.getMapping().put(theExportingPrimaryKey.getExpressions().findByAttributeName("PK2"),
                theImporting.getAttributes().findByName("PK2"));

        // Modify the model here
        theModel.addRelation(theRelation);

        HistoryModificationTracker theTracker = new HistoryModificationTracker(theModel);
        theModel.setModificationTracker(theTracker);

        Relation theNew = theRelation.clone();
        theNew.setName("TESTREL");

        theModel.changeRelation(theRelation, theNew);

        SQLGenerator theGenerator = dialect.createSQLGenerator();
        StatementList theStatementList = theTracker.getStatements();

        String theStatements = statementListToString(theStatementList, theGenerator);
        // System.out.println(theStatements);

        String theReference = readResourceFile(basePath + "testChangeRelation.sql");

        assertTrue(theStatements.equals(theReference));

    }
}
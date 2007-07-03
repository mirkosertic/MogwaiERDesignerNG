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
package de.mogwai.erdesignerng.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import junit.framework.TestCase;
import de.mogwai.erdesignerng.exception.CannotDeleteException;
import de.mogwai.erdesignerng.exception.ElementAlreadyExistsException;
import de.mogwai.erdesignerng.exception.ElementInvalidNameException;
import de.mogwai.erdesignerng.io.ModelIOUtilities;
import de.mogwai.erdesignerng.model.Attribute;
import de.mogwai.erdesignerng.model.Domain;
import de.mogwai.erdesignerng.model.Index;
import de.mogwai.erdesignerng.model.IndexType;
import de.mogwai.erdesignerng.model.Model;
import de.mogwai.erdesignerng.model.Relation;
import de.mogwai.erdesignerng.model.Table;
import de.mogwai.erdesignerng.util.dialect.mysql.MySQLDialect;

/**
 * Test for the database model.
 * 
 * @author Mirko Sertic <mail@mirkosertic.de>
 */
public class ModelTest extends TestCase {

	public void testTableWithoutName() {
		Model theModel = new Model();
		theModel.setDialect(new MySQLDialect());
		Table theTable = new Table();
		try {
			theModel.addTable(theTable);

			fail("Elements without a name are not allowed in model!");

		} catch (ElementAlreadyExistsException e) {

			fail("Wrong exception thrown");

		} catch (ElementInvalidNameException e) {
		}
	}

	public void testTableAdd() {
		Model theModel = new Model();
		theModel.setDialect(new MySQLDialect());
		Table theTable = new Table();
		theTable.setName("TABLE1");
		try {
			theModel.addTable(theTable);

			assertEquals(theTable.getOwner(), theModel);

		} catch (ElementAlreadyExistsException e) {

			fail("Wrong exception thrown");

		} catch (ElementInvalidNameException e) {

			fail("Wrong exception thrown");
		}
	}

	public void testTableDuplicateAdd() {
		Model theModel = new Model();
		theModel.setDialect(new MySQLDialect());
		Table theTable = new Table();
		theTable.setName("TABLE1");
		try {
			theModel.addTable(theTable);

			assertEquals(theTable.getOwner(), theModel);

		} catch (ElementAlreadyExistsException e) {

			fail("Wrong exception thrown");

		} catch (ElementInvalidNameException e) {

			fail("Wrong exception thrown");
		}

		theTable = new Table();
		theTable.setName("table1");
		try {
			theModel.addTable(theTable);

			fail("Cannot add table with same name twice");

		} catch (ElementAlreadyExistsException e) {

		} catch (ElementInvalidNameException e) {

			fail("Wrong exception thrown");

		}
	}

	public void testUseCase1() {
		Model theModel = new Model();
		theModel.setDialect(new MySQLDialect());

		Domain theDomain = new Domain();
		try {
			theModel.addDomain(theDomain);

			fail("Cannot add unnamed domain!");
		} catch (ElementAlreadyExistsException e1) {
			fail("Wrong exception");
		} catch (ElementInvalidNameException e1) {
		}

		theDomain.setName("DOMAIN1");
		try {
			theModel.addDomain(theDomain);
		} catch (ElementAlreadyExistsException e1) {
			fail("Wrong exception");
		} catch (ElementInvalidNameException e1) {
			fail("Wrong exception");
		}

		Table theTable = new Table();
		theTable.setName("TABLE1");
		try {
			theModel.addTable(theTable);

			assertEquals(theTable.getOwner(), theModel);

		} catch (ElementAlreadyExistsException e) {

			fail("Wrong exception thrown");

		} catch (ElementInvalidNameException e) {

			fail("Wrong exception thrown");
		}

		Attribute theAttribute = new Attribute();
		try {
			theTable.addAttribute(theModel, theAttribute);

			fail("Cannot add attribute without a name");
		} catch (ElementAlreadyExistsException e) {
			fail("Wrong exceptio here");
		} catch (ElementInvalidNameException e) {
		}

		theAttribute.setName("at1");
		try {
			theTable.addAttribute(theModel, theAttribute);

			assertEquals(theAttribute.getOwner(), theTable);
		} catch (ElementAlreadyExistsException e) {
			fail("Wrong exception here");
		} catch (ElementInvalidNameException e) {
			fail("Wrong exception here");
		}

		assertEquals("AT1", theAttribute.getName());

		theAttribute = new Attribute();
		theAttribute.setName("AT1");

		try {
			theTable.addAttribute(theModel, theAttribute);

			fail("Cannot add attribute with same name");
		} catch (ElementAlreadyExistsException e) {
		} catch (ElementInvalidNameException e) {
			fail("Wrong exception here");
		}

		theAttribute.setName("at2");
		try {
			theTable.addAttribute(theModel, theAttribute);
		} catch (ElementAlreadyExistsException e) {
			fail("Wrong exception here");
		} catch (ElementInvalidNameException e) {
			fail("Wrong exception here");
		}

		assertEquals("AT2", theAttribute.getName());

		try {
			theAttribute.renameTo("At1");

			fail("Cannot rename to existant attribute");
		} catch (ElementAlreadyExistsException e) {
		} catch (ElementInvalidNameException e) {
		}

		try {
			theAttribute.renameTo("aT3");
		} catch (ElementAlreadyExistsException e) {
			fail("Cannot rename to new attribute");
		} catch (ElementInvalidNameException e) {
		}

		assertEquals("AT3", theAttribute.getName());

		try {
			theTable.renameTo("table3");
		} catch (ElementAlreadyExistsException e) {
			fail("Cannot rename to new attribute");
		} catch (ElementInvalidNameException e) {
		}

		assertEquals("TABLE3", theTable.getName());

		theAttribute.setDefinition(theDomain, true, null);

	}

	public void testUseCase2() throws ElementAlreadyExistsException,
			ElementInvalidNameException {

		Model theModel = new Model();
		theModel.setDialect(new MySQLDialect());

		Domain theDomain = new Domain();
		theDomain.setName("DOMAIN1");
		theModel.addDomain(theDomain);

		Table theTable1 = new Table();
		theTable1.setName("TABLE1");

		for (int i = 0; i < 5; i++) {
			Attribute theAttribute = new Attribute();
			theAttribute.setName("a1_" + i);
			theAttribute.setDefinition(theDomain, true, null);

			theTable1.addAttribute(theModel, theAttribute);
		}

		theModel.addTable(theTable1);

		Table theTable2 = new Table();
		theTable2.setName("TABLE2");

		for (int i = 0; i < 5; i++) {
			Attribute theAttribute = new Attribute();
			theAttribute.setName("a2_" + i);
			theAttribute.setDefinition(theDomain, true, null);

			theTable2.addAttribute(theModel, theAttribute);
		}

		theModel.addTable(theTable2);

		Relation theRelation = new Relation();
		theRelation.setName("REL1");
		theRelation.setImportingTable(theTable1);
		theRelation.setExportingTable(theTable2);
		theRelation.getMapping().put(theTable1.getAttributes().get(0),
				theTable2.getAttributes().get(0));

		theModel.addRelation(theRelation);

		try {
			theTable1.delete();

			fail("Cannot delete table used by relations");
		} catch (CannotDeleteException e) {
		}

		try {
			theTable2.delete();

			fail("Cannot delete table used by relations");
		} catch (CannotDeleteException e) {
		}

		Attribute theAttribute = theTable1.getAttributes().get(0);
		try {
			theAttribute.delete();

			fail("Cannot delete attribute used by relations");
		} catch (CannotDeleteException e) {
		}

		theAttribute = theTable2.getAttributes().get(0);
		try {
			theAttribute.delete();

			fail("Cannot delete attribute used by relations");
		} catch (CannotDeleteException e) {
		}

		theAttribute = theTable2.getAttributes().get(1);
		try {
			theAttribute.delete();
		} catch (CannotDeleteException e) {
			fail("Cannot delete unused attribute!");
		}

		theRelation.renameTo("REL1222");
		try {
			theRelation.delete();
		} catch (CannotDeleteException e) {
			fail("Cannot delete unused relation");
		}
	}

	public void testUseCase3() throws ElementAlreadyExistsException,
			ElementInvalidNameException, IOException {

		Model theModel = new Model();
		theModel.setDialect(new MySQLDialect());

		Domain theDomain = new Domain();
		theDomain.setName("DOMAIN1");
		theModel.addDomain(theDomain);

		Table theTable1 = new Table();
		theTable1.setName("TABLE1");

		for (int i = 0; i < 5; i++) {
			Attribute theAttribute = new Attribute();
			theAttribute.setName("a1_" + i);
			theAttribute.setDefinition(theDomain, true, null);

			theTable1.addAttribute(theModel, theAttribute);
		}

		theModel.addTable(theTable1);

		Table theTable2 = new Table();
		theTable2.setName("TABLE2");

		Index theIndex = new Index();
		theIndex.setIndexType(IndexType.PRIMARYKEY);

		for (int i = 0; i < 5; i++) {
			Attribute theAttribute = new Attribute();
			theAttribute.setName("a2_" + i);
			theAttribute.setDefinition(theDomain, true, null);

			theTable2.addAttribute(theModel, theAttribute);

			theIndex.getAttributes().add(theAttribute);
		}

		theTable2.getIndexes().add(theIndex);

		theModel.addTable(theTable2);

		Relation theRelation = new Relation();
		theRelation.setName("REL1");
		theRelation.setImportingTable(theTable1);
		theRelation.setExportingTable(theTable2);
		theRelation.getMapping().put(theTable1.getAttributes().get(0),
				theTable2.getAttributes().get(0));

		theModel.addRelation(theRelation);

		// File theTempFile = File.createTempFile("TEST", ".xml");
		File theTempFile = new File("c:\\temp\\test.xml");

		try {
			FileOutputStream theStream = new FileOutputStream(theTempFile);
			ModelIOUtilities.getInstance().serializeModelToXML(theModel,
					theStream);
			theStream.close();
		} catch (Exception e) {
			fail("Cannot save model");
		}

		try {
			FileInputStream theStream = new FileInputStream(theTempFile);
			Model theLoadedModel = ModelIOUtilities.getInstance()
					.deserializeModelFromXML(theStream);
			theStream.close();

			// Very rude check
			assertEquals(theModel.getDomains().size(), theLoadedModel
					.getDomains().size());
			assertEquals(theModel.getTables().size(), theLoadedModel
					.getTables().size());
			assertEquals(theModel.getRelations().size(), theLoadedModel
					.getRelations().size());
			assertEquals(theModel.getTables().get(1).getIndexes().size(),
					theModel.getTables().get(1).getIndexes().size());
			assertEquals(theModel.getTables().get(1).getIndexes().get(0)
					.getAttributes().size(), theModel.getTables().get(1)
					.getIndexes().get(0).getAttributes().size());

		} catch (Exception e) {
			e.printStackTrace();
			fail("Cannot load model");
		}

	}

}

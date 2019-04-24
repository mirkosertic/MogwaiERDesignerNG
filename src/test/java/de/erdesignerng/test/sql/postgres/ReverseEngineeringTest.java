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
package de.erdesignerng.test.sql.postgres;

import de.erdesignerng.dialect.Dialect;
import de.erdesignerng.dialect.JDBCReverseEngineeringStrategy;
import de.erdesignerng.dialect.ReverseEngineeringOptions;
import de.erdesignerng.dialect.SQLGenerator;
import de.erdesignerng.dialect.SchemaEntry;
import de.erdesignerng.dialect.TableNamingEnum;
import de.erdesignerng.dialect.postgres.PostgresDialect;
import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.Index;
import de.erdesignerng.model.IndexExpression;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.Relation;
import de.erdesignerng.model.Table;
import de.erdesignerng.model.View;
import de.erdesignerng.modificationtracker.HistoryModificationTracker;
import org.junit.Ignore;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * Test for XML based model io.
 *
 * @author $Author: mirkosertic $
 * @version $Date: 2008-11-16 17:48:26 $
 */
@Ignore
public class ReverseEngineeringTest extends AbstractConnectionTest {

	public void testReverseEngineerPostgreSQL() throws Exception {

		Connection theConnection = null;
		try {
			theConnection = createConnection();

			loadSQL(theConnection, "db.sql");

			Dialect theDialect = new PostgresDialect();
			JDBCReverseEngineeringStrategy<PostgresDialect> theST = theDialect.getReverseEngineeringStrategy();

			Model theModel = new Model();
			theModel.setDialect(theDialect);
			theModel.setModificationTracker(new HistoryModificationTracker(theModel));

			ReverseEngineeringOptions theOptions = new ReverseEngineeringOptions();
			theOptions.setTableNaming(TableNamingEnum.INCLUDE_SCHEMA);
			theOptions.getSchemaEntries().add(new SchemaEntry("", "public"));
			theOptions.getSchemaEntries().add(new SchemaEntry("", "schemaa"));
			theOptions.getSchemaEntries().add(new SchemaEntry("", "schemab"));
			theOptions.getTableEntries().addAll(
					theST.getTablesForSchemas(theConnection, theST.getSchemaEntries(theConnection)));

			theST.updateModelFromConnection(theModel, new EmptyWorldConnector(), theConnection, theOptions,
					new EmptyReverseEngineeringNotifier());

			// Implement Unit Tests here
			Table theTable = theModel.getTables().findByNameAndSchema("table1", "schemaa");
			assertTrue(theTable != null);
			assertTrue("Tablecomment".equals(theTable.getComment()));
			Attribute<Table> theAttribute = theTable.getAttributes().findByName("tb1_1");
			assertTrue(theAttribute != null);
			assertTrue(!theAttribute.isNullable());
			assertTrue(theAttribute.getDatatype().getName().equals("testdomain"));
			assertTrue("Columncomment".equals(theAttribute.getComment()));
			theAttribute = theTable.getAttributes().findByName("tb1_2");
			assertTrue(theAttribute != null);
			assertTrue(theAttribute.isNullable());
			assertTrue(theAttribute.getDatatype().getName().equals("varchar"));
			assertTrue(theAttribute.getSize() == 100);
			theAttribute = theTable.getAttributes().findByName("tb1_3");
			assertTrue(theAttribute != null);
			assertTrue(!theAttribute.isNullable());
			assertTrue(theAttribute.getDatatype().getName().equals("numeric"));
			assertTrue(theAttribute.getSize() == 20);
			assertTrue(theAttribute.getFraction() == 5);

			Index thePK = theTable.getPrimarykey();
			assertTrue(thePK != null);
			assertTrue("pk1".equals(thePK.getName()));
			assertTrue(thePK.getExpressions().findByAttributeName("tb1_1") != null);

			theTable = theModel.getTables().findByNameAndSchema("table1", "schemab");
			assertTrue(theTable != null);
			theAttribute = theTable.getAttributes().findByName("tb2_1");
			assertTrue(theAttribute != null);
			theAttribute = theTable.getAttributes().findByName("tb2_2");
			assertTrue(theAttribute != null);
			theAttribute = theTable.getAttributes().findByName("tb2_3");
			assertTrue(theAttribute != null);

			View theView = theModel.getViews().findByNameAndSchema("view1", "schemab");
			assertTrue(theView != null);

			theView = theModel.getViews().findByNameAndSchema("view1", "schemaa");
			assertTrue(theView == null);

			theTable = theModel.getTables().findByNameAndSchema("table2", "schemab");
			Index theIndex = theTable.getIndexes().findByName("tabl22_idx3");
			assertTrue(theIndex != null);
			assertTrue(theIndex.getExpressions().size() == 1);
			assertTrue(theIndex.getExpressions().findByAttributeName("tb3_2") == null);
			assertTrue("upper((tb3_2)::text)".equals(theIndex.getExpressions().get(0).getExpression()));

			assertTrue(theModel.getRelations().size() == 2);

			Relation theRelation = theModel.getRelations().findByName("fk1");
			assertTrue(theRelation != null);
			assertTrue("table1".equals(theRelation.getImportingTable().getName()));
			assertTrue("schemab".equals(theRelation.getImportingTable().getSchema()));

			assertTrue("table1".equals(theRelation.getExportingTable().getName()));
			assertTrue("schemaa".equals(theRelation.getExportingTable().getSchema()));

			assertTrue(theRelation.getMapping().size() == 1);
			Map.Entry<IndexExpression, Attribute<Table>> theEntry = theRelation.getMapping().entrySet().iterator().next();
			assertTrue("tb2_1".equals(theEntry.getValue().getName()));
			assertTrue("tb1_1".equals(theEntry.getKey().getAttributeRef().getName()));

			SQLGenerator theGenerator = theDialect.createSQLGenerator();
			String theResult = statementListToString(theGenerator.createCreateAllObjects(theModel), theGenerator);

			System.out.println("Res==");
			System.out.println(theResult);
			System.out.println("RES==");

			String theReference = readResourceFile("result.sql");

			assertTrue(compareStrings(theResult, theReference));
		} finally {
			if (theConnection != null) {
				theConnection.close();
			}
		}
	}

	public void testReverseEngineeredSQL() throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, SQLException, IOException {
		Connection theConnection = null;
		try {
			theConnection = createConnection();

			loadSingleSQL(theConnection, "result.sql");
		} finally {
			if (theConnection != null) {

				theConnection.close();
			}
		}
	}
}
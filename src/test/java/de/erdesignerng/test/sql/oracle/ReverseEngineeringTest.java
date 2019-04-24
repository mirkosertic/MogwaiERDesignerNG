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
package de.erdesignerng.test.sql.oracle;

import de.erdesignerng.dialect.*;
import de.erdesignerng.dialect.oracle.OracleDialect;
import de.erdesignerng.model.*;
import de.erdesignerng.modificationtracker.HistoryModificationTracker;
import org.junit.Ignore;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Test for XML based model io.
 *
 * @author $Author: mirkosertic $
 * @version $Date: 2008-11-16 17:48:26 $
 */
@Ignore
public class ReverseEngineeringTest extends AbstractConnectionTest {

	public void testReverseEngineerOracle() throws Exception {

		Connection theConnection = null;
		try {
			theConnection = createConnection();

			loadSQL(theConnection, "db.sql");

			Dialect theDialect = new OracleDialect();
			JDBCReverseEngineeringStrategy<OracleDialect> theST = theDialect.getReverseEngineeringStrategy();

			Model theModel = new Model();
			theModel.setDialect(theDialect);
			theModel.setModificationTracker(new HistoryModificationTracker(theModel));

			List<SchemaEntry> theAllSchemas = theST.getSchemaEntries(theConnection);

			List<SchemaEntry> theSchemas = new ArrayList<>();
			for (SchemaEntry theEntry : theAllSchemas) {
				System.out.println(theEntry.getSchemaName());

				// Only dbo schemas
				if ("MOGWAI".equals(theEntry.getSchemaName())) {
					theSchemas.add(theEntry);
				}
			}

			ReverseEngineeringOptions theOptions = new ReverseEngineeringOptions();
			theOptions.setTableNaming(TableNamingEnum.STANDARD);
			theOptions.getTableEntries().addAll(theST.getTablesForSchemas(theConnection, theSchemas));

			theST.updateModelFromConnection(theModel, new EmptyWorldConnector(), theConnection, theOptions,
					new EmptyReverseEngineeringNotifier());

			// Implement Unit Tests here
			Table theTable = theModel.getTables().findByName("TABLE1");
			assertTrue(theTable != null);
			Attribute<Table> theAttribute = theTable.getAttributes().findByName("TB2_1");
			assertTrue(theAttribute != null);
			assertTrue(!theAttribute.isNullable());
			assertTrue(theAttribute.getDatatype().getName().equals("VARCHAR2"));
			assertTrue(theAttribute.getSize() == 20);
			theAttribute = theTable.getAttributes().findByName("TB2_2");
			assertTrue(theAttribute != null);
			assertTrue(theAttribute.isNullable());
			assertTrue(theAttribute.getDatatype().getName().equals("VARCHAR2"));
			assertTrue(theAttribute.getSize() == 100);
			theAttribute = theTable.getAttributes().findByName("TB2_3");
			assertTrue(theAttribute != null);
			assertTrue(!theAttribute.isNullable());
			assertTrue(theAttribute.getDatatype().getName().equals("NUMBER"));
			assertTrue(theAttribute.getSize() == 20);
			assertTrue(theAttribute.getFraction() == 5);
			theAttribute = theTable.getAttributes().findByName("TB2_4");
			assertTrue(theAttribute != null);
			assertTrue(theAttribute.isNullable());
			assertTrue(theAttribute.getDatatype().getName().equals("NUMBER"));
			assertTrue(theAttribute.getSize() == 9);
			assertTrue(theAttribute.getFraction() == 0);

			theTable = theModel.getTables().findByName("TABLE2");
			assertTrue(theTable != null);
			theAttribute = theTable.getAttributes().findByName("TB3_1");
			assertTrue(theAttribute != null);
			theAttribute = theTable.getAttributes().findByName("TB3_2");
			assertTrue(theAttribute != null);
			theAttribute = theTable.getAttributes().findByName("TB3_3");
			assertTrue(theAttribute != null);

			Index thePK = theTable.getPrimarykey();
			assertTrue(thePK != null);
			assertTrue(thePK.getExpressions().findByAttributeName("TB3_1") != null);

			View theView = theModel.getViews().findByName("VIEW1");
			assertTrue(theView != null);

			Relation theRelation = theModel.getRelations().findByName("FK1");
			assertTrue(theRelation != null);
			assertTrue("TABLE1".equals(theRelation.getImportingTable().getName()));
			assertTrue("TABLE2".equals(theRelation.getExportingTable().getName()));

			assertTrue(theRelation.getMapping().size() == 1);
			Map.Entry<IndexExpression, Attribute<Table>> theEntry = theRelation.getMapping().entrySet().iterator().next();
			assertTrue("TB2_1".equals(theEntry.getValue().getName()));
			assertTrue("TB3_1".equals(theEntry.getKey().getAttributeRef().getName()));

			SQLGenerator theGenerator = theDialect.createSQLGenerator();
			String theResult = statementListToString(theGenerator.createCreateAllObjects(theModel), theGenerator);

			System.out.println("RES==");
			System.out.println(theResult);

			String theReference = readResourceFile("result.sql");

			assertTrue(theResult.equals(theReference));

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
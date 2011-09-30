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
package de.erdesignerng.test.sql.hsqldb;

import de.erdesignerng.dialect.*;
import de.erdesignerng.dialect.hsqldb.HSQLDBDialect;
import de.erdesignerng.model.*;
import de.erdesignerng.modificationtracker.HistoryModificationTracker;
import de.erdesignerng.test.sql.AbstractReverseEngineeringTestImpl;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Test for XML based model io.
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2008-11-16 17:48:26 $
 */
public class ReverseEngineeringTest extends AbstractReverseEngineeringTestImpl {

	public void testReverseEngineerH2() throws Exception {

		Class.forName("org.hsqldb.jdbc.JDBCDriver").newInstance();
		Connection theConnection = null;
		try {
			theConnection = DriverManager.getConnection("jdbc:hsqldb:mem:bname", "sa", "");

			loadSQL(theConnection, "db.sql");

			Dialect theDialect = new HSQLDBDialect();
			JDBCReverseEngineeringStrategy<HSQLDBDialect> theST = theDialect.getReverseEngineeringStrategy();

			Model theModel = new Model();
			theModel.setDialect(theDialect);
			theModel.setModificationTracker(new HistoryModificationTracker(theModel));

			ReverseEngineeringOptions theOptions = new ReverseEngineeringOptions();
			theOptions.setTableNaming(TableNamingEnum.INCLUDE_SCHEMA);
			theOptions.getTableEntries().addAll(
					theST.getTablesForSchemas(theConnection, theST.getSchemaEntries(theConnection)));

			theST.updateModelFromConnection(theModel, new EmptyWorldConnector(), theConnection, theOptions,
					new EmptyReverseEngineeringNotifier());

			// Implement Unit Tests here
			Table theTable = theModel.getTables().findByNameAndSchema("TABLE1", "SCHEMAA");
			assertTrue(theTable != null);
			Attribute<Table> theAttribute = theTable.getAttributes().findByName("TB1_1");
			assertTrue(theAttribute != null);
			assertTrue(!theAttribute.isNullable());
			assertTrue(theAttribute.getDatatype().getName().equals("varchar"));
			assertTrue(theAttribute.getSize() == 20);
			theAttribute = theTable.getAttributes().findByName("TB1_2");
			assertTrue(theAttribute != null);
			assertTrue(theAttribute.isNullable());
			assertTrue(theAttribute.getDatatype().getName().equals("varchar"));
			assertTrue(theAttribute.getSize() == 100);
			theAttribute = theTable.getAttributes().findByName("TB1_3");
			assertTrue(theAttribute != null);
			assertTrue(!theAttribute.isNullable());
			assertTrue(theAttribute.getDatatype().getName().equals("numeric"));
			assertTrue(theAttribute.getSize() == 20);
			assertTrue(theAttribute.getFraction() == 5);

			Index thePK = theTable.getPrimarykey();
			assertTrue(thePK != null);
			assertTrue(thePK.getExpressions().findByAttributeName("TB1_1") != null);

			theTable = theModel.getTables().findByNameAndSchema("TABLE1", "SCHEMAB");
			assertTrue(theTable != null);
			theAttribute = theTable.getAttributes().findByName("TB2_1");
			assertTrue(theAttribute != null);
			theAttribute = theTable.getAttributes().findByName("TB2_2");
			assertTrue(theAttribute != null);
			theAttribute = theTable.getAttributes().findByName("TB2_3");
			assertTrue(theAttribute != null);

			View theView = theModel.getViews().findByNameAndSchema("VIEW1", "SCHEMAB");
			assertTrue(theView != null);

			theView = theModel.getViews().findByNameAndSchema("VIEW1", "SCHEMAA");
			assertTrue(theView == null);

			SQLGenerator theGenerator = theDialect.createSQLGenerator();
			String theResult = statementListToString(theGenerator.createCreateAllObjects(theModel), theGenerator);

			System.out.println("RES=");
			System.out.println(theResult);
			System.out.println("RES=");

			String theReference = readResourceFile("result.sql");

			assertTrue(compareStrings(theResult, theReference));

		} finally {
			if (theConnection != null) {

				theConnection.createStatement().execute("SHUTDOWN");
				theConnection.close();
			}
		}
	}

	public void testReverseEngineeredSQL() throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, SQLException, IOException {
		Class.forName("org.hsqldb.jdbc.JDBCDriver").newInstance();
		Connection theConnection = null;
		try {
			theConnection = DriverManager.getConnection("jdbc:hsqldb:mem:cname", "sa", "");

			loadSingleSQL(theConnection, "result.sql");
		} finally {
			if (theConnection != null) {

				theConnection.createStatement().execute("SHUTDOWN");
				theConnection.close();
			}
		}
	}
}
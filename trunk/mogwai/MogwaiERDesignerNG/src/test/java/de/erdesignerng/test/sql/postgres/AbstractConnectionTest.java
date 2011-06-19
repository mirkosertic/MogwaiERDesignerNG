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

import de.erdesignerng.test.sql.AbstractReverseEngineeringTestImpl;

import java.sql.*;

public abstract class AbstractConnectionTest extends AbstractReverseEngineeringTestImpl {

	private Connection connection;

	@Override
	protected void setUp() throws Exception {

		connection = null;

		Class.forName("org.postgresql.Driver").newInstance();

		Connection theConnection = createConnection();

		Statement theStatement = theConnection.createStatement();
		try {
			theStatement.execute("DROP SCHEMA schemaa CASCADE");
		} catch (Exception e) {
		}
		try {
			theStatement.execute("DROP SCHEMA schemab CASCADE");
		} catch (Exception e) {
		}

		try {
			theStatement.execute("DROP domain testdomain");
		} catch (Exception e) {
		}

		DatabaseMetaData theMeta = theConnection.getMetaData();
		ResultSet theResultSet = theMeta.getTables("mogwai", "%", "%", null);
		while (theResultSet.next()) {
			String theTablename = theResultSet.getString("TABLE_NAME");
			if (theTablename.startsWith("mogrep")) {
				try {
					theStatement.execute("DROP TABLE " + theTablename + " CASCADE");
				} catch (Exception e) {
					// Ignore this
				}
			}
		}

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();

		if (connection != null) {
			connection.close();
		}
	}

	protected Connection createConnection() throws SQLException {
		if (connection == null) {
			connection = DriverManager.getConnection("jdbc:postgresql://" + getDBServerName() + ":5432/mogwai",
					"mogwai", "mogwai");
		}
		return connection;
	}
}
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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import de.erdesignerng.test.sql.AbstractReverseEngineeringTestImpl;

public abstract class AbstractConnectionTest extends AbstractReverseEngineeringTestImpl {

	private Connection connection;

	@Override
	protected void setUp() throws Exception {

		connection = null;

		Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
		Connection theConnection = null;
		theConnection = DriverManager.getConnection("jdbc:oracle:thin:@" + getDBServerName() + ":1521:XE",
				"sys as sysdba", "sys");

		Statement theStatement = theConnection.createStatement();
		try {
			theStatement.execute("DROP USER mogwai CASCADE");
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			theStatement.execute("CREATE USER mogwai IDENTIFIED BY mogwai");
			theStatement.execute("GRANT DBA TO mogwai");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		theConnection.close();

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
			connection = DriverManager.getConnection("jdbc:oracle:thin:@" + getDBServerName() + ":1521:XE", "mogwai",
					"mogwai");
		}
		return connection;
	}
}
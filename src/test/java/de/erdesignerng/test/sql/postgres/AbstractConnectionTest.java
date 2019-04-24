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
import org.junit.Rule;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class AbstractConnectionTest extends AbstractReverseEngineeringTestImpl {

	@Rule
	public PostgreSQLContainer container = new PostgreSQLContainer();

	private Connection connection;

	@Override
	protected void setUp() throws Exception {

		connection = null;

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
			connection = DriverManager.getConnection(container.getJdbcUrl(),
					container.getUsername(), container.getPassword());
		}
		return connection;
	}
}
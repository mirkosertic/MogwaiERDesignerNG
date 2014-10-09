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
package de.erdesignerng.model.serializer.repository;

import org.hibernate.connection.ConnectionProvider;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class ThreadbasedConnectionProvider implements ConnectionProvider {

	private static final ThreadLocal<Connection> connection = new ThreadLocal<>();

	public static void initializeForThread(Connection aConnection) {
		connection.set(aConnection);
	}

	public static void cleanup() {
		connection.set(null);
	}

	@Override
	public void close() {
	}

	@Override
	public void closeConnection(Connection conn) throws SQLException {
	}

	@Override
	public void configure(Properties aProps) {
	}

	@Override
	public Connection getConnection() throws SQLException {
		return connection.get();
	}

	@Override
	public boolean supportsAggressiveRelease() {
		return false;
	}
}
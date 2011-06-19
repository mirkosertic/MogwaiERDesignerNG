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

import de.erdesignerng.test.io.repository.RepositioryHelper;
import de.erdesignerng.test.sql.AbstractReverseEngineeringTestImpl;
import org.apache.commons.io.IOUtils;
import org.hibernate.dialect.HSQLDialect;

import java.sql.Connection;
import java.sql.DriverManager;

public class RepositoryIOTest extends AbstractReverseEngineeringTestImpl {

	public void testLoadSaveRepository() throws
			Exception {

		Class.forName("org.hsqldb.jdbc.JDBCDriver").newInstance();
		Connection theConnection = null;
		try {
			theConnection = DriverManager.getConnection("jdbc:hsqldb:mem:dname", "sa", "");

			Class theHibernateDialect = HSQLDialect.class;

			String theModelResource = "/de/erdesignerng/test/io/repository/examplemodel.mxm";

			String theNewFile = RepositioryHelper.performRepositorySaveAndLoad(theModelResource, theHibernateDialect,
					theConnection);

			String theOriginalFile = IOUtils.toString(getClass().getResourceAsStream(theModelResource));

			assertTrue(compareStrings(theOriginalFile, theNewFile));

		} finally {
			if (theConnection != null) {

				theConnection.createStatement().execute("SHUTDOWN");
				theConnection.close();
			}
		}
	}
}

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
package de.erdesignerng.test.sql.mssql;

import java.sql.Connection;

import org.apache.commons.io.IOUtils;
import org.hibernate.dialect.SQLServerDialect;

import de.erdesignerng.test.io.repository.RepositioryHelper;
import org.junit.Ignore;

@Ignore
public class RepositoryIOTest extends AbstractConnectionTest {

	public void testLoadSaveRepository() throws Exception {

		Connection theConnection = createConnection();

		Class theHibernateDialect = SQLServerDialect.class;

		String theModelResource = "/de/erdesignerng/test/io/repository/examplemodel.mxm";

		String theNewFile = RepositioryHelper.performRepositorySaveAndLoad(theModelResource, theHibernateDialect,
				theConnection);

		String theOriginalFile = IOUtils.toString(getClass().getResourceAsStream(theModelResource));

		assertTrue(compareStrings(theOriginalFile, theNewFile));
	}
}
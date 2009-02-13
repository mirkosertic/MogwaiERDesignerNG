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
package de.erdesignerng.test.sql.mysql;

import de.erdesignerng.dialect.mysql.MySQLDialect;
import de.erdesignerng.test.sql.AbstractDialectTestCase;

/**
 * Test for the MySQL SQL Generator. 
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2009-02-13 18:47:14 $
 */
public class MySQLDialectTest extends AbstractDialectTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        dialect = new MySQLDialect();
        textDataType = dialect.getDataTypes().findByName("VARCHAR");
        intDataType = dialect.getDataTypes().findByName("INTEGER");
        basePath = "/de/erdesignerng/test/sql/mysql/";
    }
}

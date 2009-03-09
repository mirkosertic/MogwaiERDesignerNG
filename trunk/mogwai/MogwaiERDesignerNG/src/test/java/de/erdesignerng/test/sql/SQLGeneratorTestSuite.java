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
package de.erdesignerng.test.sql;

import de.erdesignerng.test.sql.mssql.MSSQLDialectTest;
import de.erdesignerng.test.sql.mysql.MySQLDialectTest;
import de.erdesignerng.test.sql.mysqlinnodb.MySQLInnoDBDialectTest;
import de.erdesignerng.test.sql.oracle.OracleDialectTest;
import de.erdesignerng.test.sql.postgres.PostgresDialectTest;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Testsuite for all SQL Generators.
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-09 19:07:29 $
 */
public class SQLGeneratorTestSuite extends TestSuite {

    public static Test suite() {
        TestSuite theSuite = new TestSuite();
        theSuite.addTestSuite(MSSQLDialectTest.class);
        theSuite.addTestSuite(MySQLDialectTest.class);
        theSuite.addTestSuite(MySQLInnoDBDialectTest.class);
        theSuite.addTestSuite(OracleDialectTest.class);
        theSuite.addTestSuite(PostgresDialectTest.class);
        return theSuite;
    }
}

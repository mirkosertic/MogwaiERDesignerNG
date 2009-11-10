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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import de.erdesignerng.test.sql.AbstractReverseEngineeringTestImpl;

public class AbstractConnectionTest extends AbstractReverseEngineeringTestImpl {

    @Override
    protected void setUp() throws Exception {
        Class.forName("org.postgresql.Driver").newInstance();

        Connection theConnection = null;
        theConnection = createConnection();

        Statement theStatement = theConnection.createStatement();
        try {
            theStatement.execute("DROP SCHEMA schemaa CASCADE");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            theStatement.execute("DROP SCHEMA schemab CASCADE");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            theStatement.execute("DROP domain testdomain");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected Connection createConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:postgresql://" + getDBServerName() + ":5432/mogwai", "mogwai",
                "mogwai");
    }
}
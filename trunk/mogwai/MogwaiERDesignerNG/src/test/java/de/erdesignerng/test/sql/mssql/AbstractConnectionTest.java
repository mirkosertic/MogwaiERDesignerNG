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
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import de.erdesignerng.test.sql.AbstractReverseEngineeringTestImpl;

public class AbstractConnectionTest extends AbstractReverseEngineeringTestImpl {

    @Override
    protected void setUp() throws Exception {

        Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
        Connection theConnection = null;

        theConnection = DriverManager.getConnection("jdbc:jtds:sqlserver://" + getDBServerName() + ":1433/master",
                "sa", "Mirko123!");

        Statement theStatement = theConnection.createStatement();
        try {
            theStatement.execute("drop database mogwai");
        } catch (Exception e) {
            e.printStackTrace();
        }

        theStatement.execute("create database mogwai");
        theConnection.close();
    }

    protected Connection createConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:jtds:sqlserver://"+getDBServerName()+":1433/mogwai", "sa", "Mirko123!");
    }
}
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
package de.erdesignerng.test;

import java.sql.DriverManager;

import de.erdesignerng.dialect.postgres.PostgresDialect;
import de.erdesignerng.model.Domain;
import de.erdesignerng.model.Model;

/**
 * Test for MySQL dialect.
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2008-01-29 21:17:24 $
 */
public class PostgresTest extends BaseUseCases {

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        model = new Model();
        model.setDialect(new PostgresDialect());

        model.setModificationTracker(new MyTracker(model));

        Class.forName("org.postgresql.Driver");
        connection = DriverManager.getConnection("jdbc:postgresql://192.168.0.140:5432/erdesigner", "erdesigner", "erdesigner");
    }

    @Override
    public Domain createCharDomain(String aName, int aLength) {
        Domain theDomain = new Domain();
        theDomain.setName(aName);
        theDomain.setDatatype("char");
        theDomain.setDomainSize(aLength);
        return theDomain;
    }
}

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
package de.erdesignerng.plugins.squirrel.dialect;

import java.sql.Connection;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.session.ISession;
import de.erdesignerng.dialect.DataType;
import de.erdesignerng.dialect.Dialect;
import de.erdesignerng.dialect.ReverseEngineeringStrategy;
import de.erdesignerng.dialect.SQLGenerator;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-01-19 15:25:31 $
 */
public class SquirrelDialect extends Dialect {

    private ISession session;
    
    public SquirrelDialect(ISession aSession) {
        session = aSession;
    }
    
    public ISession getSession() {
        return session;
    }
    
    @Override
    public Connection createConnection(ClassLoader aClassLoader, String aDriver, String aUrl, String aUser, String aPassword) throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {
        return super.createConnection(aClassLoader, aDriver, aUrl, aUser, aPassword);
    }

    @Override
    protected DataType createDataTypeFor(String aTypeName, String aCreateParams) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SQLGenerator createSQLGenerator() {
        return new SquirrelSQLGenerator(this);
    }

    @Override
    public String getDriverClassName() {
        return null;
    }

    @Override
    public String getDriverURLTemplate() {
        return null;
    }

    @Override
    public ReverseEngineeringStrategy getReverseEngineeringStrategy() {
        return new SquirrelReverseEngineeringStrategy(this);
    }

    @Override
    public String getUniqueName() {
        return "Squirrel";
    }
}
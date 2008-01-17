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
package de.erdesignerng.dialect.mssql;

import de.erdesignerng.dialect.DataType;
import de.erdesignerng.dialect.JDBCReverseEngineeringStrategy;
import de.erdesignerng.dialect.NameCastType;
import de.erdesignerng.dialect.SQLGenerator;
import de.erdesignerng.dialect.sql92.SQL92Dialect;

/**
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2008-01-17 19:34:29 $
 */
public class MSSQLDialect extends SQL92Dialect {

    public MSSQLDialect() {
        super("mssql_datatypes.xml");
        setSpacesAllowedInObjectNames(false);
        setCaseSensitive(false);
        setMaxObjectNameLength(128);
        setNullablePrimaryKeyAllowed(false);
        setCastType(NameCastType.UPPERCASE);
    }

    @Override
    public JDBCReverseEngineeringStrategy getReverseEngineeringStrategy() {
        return new MSSQLReverseEngineeringStrategy(this);
    }

    @Override
    public String getUniqueName() {
        return "MSSQLDialect";
    }

    @Override
    public String getDriverClassName() {
        return "net.sourceforge.jtds.jdbc.Driver";
    }

    @Override
    public String getDriverURLTemplate() {
        return "jdbc:jtds:sqlserver://<host>/<db>";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DataType createDataTypeFor(String aTypeName, String aCreateParams) {
        return new MSSQLDataType(aTypeName, aCreateParams);
    }

    @Override
    public SQLGenerator createSQLGenerator() {
        return new MSSQLSQLGenerator(this);
    }    
}

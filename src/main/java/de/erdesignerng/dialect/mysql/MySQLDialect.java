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
package de.erdesignerng.dialect.mysql;

import de.erdesignerng.dialect.DataType;
import de.erdesignerng.dialect.JDBCReverseEngineeringStrategy;
import de.erdesignerng.dialect.NameCastType;
import de.erdesignerng.dialect.SQLGenerator;
import de.erdesignerng.dialect.sql92.SQL92Dialect;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-01-29 22:04:11 $
 */
public class MySQLDialect extends SQL92Dialect {

    public MySQLDialect() {
        super("mysql_datatypes.xml");
        setSpacesAllowedInObjectNames(false);
        setCaseSensitive(true);
        setMaxObjectNameLength(30);
        setNullablePrimaryKeyAllowed(false);
        setCastType(NameCastType.NOTHING);

    }

    @Override
    public JDBCReverseEngineeringStrategy getReverseEngineeringStrategy() {
        return new MySQLReverseEngineeringStrategy(this);
    }

    @Override
    public String getUniqueName() {
        return "MySQLDialect";
    }

    @Override
    public String getDriverClassName() {
        return "com.mysql.jdbc.Driver";
    }

    @Override
    public String getDriverURLTemplate() {
        return "jdbc:mysql://<host>/<db>";
    }

    @Override
    public boolean supportsSchemaInformation() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DataType createDataTypeFor(String aId, String aTypeName, String aCreateParams) {
        return new MySQLDataType(aId, aTypeName, aCreateParams);
    }

    @Override
    public SQLGenerator createSQLGenerator() {
        return new MySQLSQLGenerator(this);
    }
}
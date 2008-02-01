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

import de.erdesignerng.dialect.JDBCReverseEngineeringStrategy;
import de.erdesignerng.dialect.NameCastType;
import de.erdesignerng.dialect.SQLGenerator;
import de.erdesignerng.dialect.sql92.SQL92Dialect;

/**
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2008-02-01 17:20:24 $
 */
public class MSSQLDialect extends SQL92Dialect {

    public MSSQLDialect() {
        setSpacesAllowedInObjectNames(false);
        setCaseSensitive(false);
        setMaxObjectNameLength(128);
        setNullablePrimaryKeyAllowed(false);
        setCastType(NameCastType.UPPERCASE);

        registerType(new MSSQLDataType("bit", "", java.sql.Types.BIT));
        registerType(new MSSQLDataType("tinyint", "", java.sql.Types.TINYINT));
        registerType(new MSSQLDataType("tinyint identity", "", java.sql.Types.TINYINT, true));
        registerType(new MSSQLDataType("bigint", "", java.sql.Types.BIGINT));
        registerType(new MSSQLDataType("bigint identity", "", java.sql.Types.BIGINT, true));
        registerType(new MSSQLDataType("varbinary", "$size", java.sql.Types.VARBINARY));
        registerType(new MSSQLDataType("binary", "$size", java.sql.Types.BINARY));
        registerType(new MSSQLDataType("timestamp", "", java.sql.Types.BINARY));
        registerType(new MSSQLDataType("char", "$size", java.sql.Types.CHAR));
        registerType(new MSSQLDataType("nchar", "$size", java.sql.Types.CHAR));
        registerType(new MSSQLDataType("uniqueidentifier", "", java.sql.Types.CHAR));
        registerType(new MSSQLDataType("numeric", "$size,$fraction", java.sql.Types.NUMERIC));
        registerType(new MSSQLDataType("numeric() identity", "$size,$fraction", java.sql.Types.NUMERIC, true));
        registerType(new MSSQLDataType("decimal", "$size,$fraction", java.sql.Types.DECIMAL));
        registerType(new MSSQLDataType("money", "", java.sql.Types.DECIMAL));
        registerType(new MSSQLDataType("smallmoney", "", java.sql.Types.DECIMAL));
        registerType(new MSSQLDataType("decimal() identity", "$size,$fraction", java.sql.Types.DECIMAL, true));
        registerType(new MSSQLDataType("int", "", java.sql.Types.INTEGER));
        registerType(new MSSQLDataType("int identity", "", java.sql.Types.INTEGER, true));
        registerType(new MSSQLDataType("smallint", "", java.sql.Types.SMALLINT));
        registerType(new MSSQLDataType("smallint identity", "", java.sql.Types.SMALLINT, true));
        registerType(new MSSQLDataType("real", "", java.sql.Types.REAL));
        registerType(new MSSQLDataType("float", "", java.sql.Types.DOUBLE));
        registerType(new MSSQLDataType("varchar", "$size", java.sql.Types.VARCHAR));
        registerType(new MSSQLDataType("nvarchar", "$size", java.sql.Types.VARCHAR));
        registerType(new MSSQLDataType("sysname", "", java.sql.Types.VARCHAR));
        registerType(new MSSQLDataType("sql_variant", "", java.sql.Types.VARCHAR));
        registerType(new MSSQLDataType("datetime", "", java.sql.Types.TIMESTAMP));
        registerType(new MSSQLDataType("smalldatetime", "", java.sql.Types.TIMESTAMP));
        registerType(new MSSQLDataType("image", "", java.sql.Types.BLOB));
        registerType(new MSSQLDataType("ntext", "", java.sql.Types.CLOB));
        registerType(new MSSQLDataType("xml", "", java.sql.Types.CLOB));
        registerType(new MSSQLDataType("text", "", java.sql.Types.CLOB));
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

    @Override
    public SQLGenerator createSQLGenerator() {
        return new MSSQLSQLGenerator(this);
    }
}

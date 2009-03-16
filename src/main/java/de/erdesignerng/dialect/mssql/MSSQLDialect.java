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

import java.sql.Types;

import de.erdesignerng.dialect.NameCastType;
import de.erdesignerng.dialect.sql92.SQL92Dialect;

/**
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2008-11-02 14:20:18 $
 */
public class MSSQLDialect extends SQL92Dialect {

    public MSSQLDialect() {
        setSpacesAllowedInObjectNames(false);
        setCaseSensitive(false);
        setMaxObjectNameLength(128);
        setNullablePrimaryKeyAllowed(false);
        setCastType(NameCastType.UPPERCASE);

        registerType(new MSSQLDataType("bit", "", Types.BIT, Types.BOOLEAN));
        registerType(new MSSQLDataType("tinyint", "", Types.TINYINT));
        registerType(new MSSQLDataType("tinyint identity", "", true, Types.TINYINT));
        registerType(new MSSQLDataType("bigint", "", Types.BIGINT));
        registerType(new MSSQLDataType("bigint identity", "", true, Types.BIGINT));
        registerType(new MSSQLDataType("varbinary", "$size", Types.VARBINARY));
        registerType(new MSSQLDataType("binary", "$size", Types.BINARY, Types.LONGVARBINARY));
        registerType(new MSSQLDataType("timestamp", "", Types.TIMESTAMP, Types.DATE, Types.TIME));
        registerType(new MSSQLDataType("char", "$size", Types.CHAR));
        registerType(new MSSQLDataType("nchar", "$size", Types.CHAR));
        registerType(new MSSQLDataType("uniqueidentifier", "", Types.CHAR));
        registerType(new MSSQLDataType("numeric", "$size,$fraction", Types.NUMERIC));
        registerType(new MSSQLDataType("numeric() identity", "$size,$fraction", true, Types.NUMERIC));
        registerType(new MSSQLDataType("decimal", "$size,$fraction", Types.DECIMAL));
        registerType(new MSSQLDataType("money", "", Types.DECIMAL));
        registerType(new MSSQLDataType("smallmoney", "", Types.DECIMAL));
        registerType(new MSSQLDataType("decimal() identity", "$size,$fraction", true, Types.DECIMAL));
        registerType(new MSSQLDataType("int", "", Types.INTEGER));
        registerType(new MSSQLDataType("int identity", "", true, Types.INTEGER));
        registerType(new MSSQLDataType("smallint", "", Types.SMALLINT));
        registerType(new MSSQLDataType("smallint identity", "", true, Types.SMALLINT));
        registerType(new MSSQLDataType("real", "", Types.REAL));
        registerType(new MSSQLDataType("float", "", Types.DOUBLE, Types.FLOAT));
        registerType(new MSSQLDataType("varchar", "$size", Types.VARCHAR));
        registerType(new MSSQLDataType("nvarchar", "$size", Types.VARCHAR));
        registerType(new MSSQLDataType("sysname", "", Types.VARCHAR));
        registerType(new MSSQLDataType("sql_variant", "", Types.VARCHAR));
        registerType(new MSSQLDataType("datetime", "", Types.TIMESTAMP, Types.DATE, Types.TIME));
        registerType(new MSSQLDataType("smalldatetime", "", Types.TIMESTAMP, Types.DATE, Types.TIME));
        registerType(new MSSQLDataType("image", "", Types.BLOB));
        registerType(new MSSQLDataType("ntext", "", Types.CLOB, Types.LONGVARCHAR));
        registerType(new MSSQLDataType("xml", "", Types.SQLXML));
        registerType(new MSSQLDataType("text", "", Types.CLOB));

        seal();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MSSQLReverseEngineeringStrategy getReverseEngineeringStrategy() {
        return new MSSQLReverseEngineeringStrategy(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUniqueName() {
        return "MSSQLDialect";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDriverClassName() {
        return "net.sourceforge.jtds.jdbc.Driver";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDriverURLTemplate() {
        return "jdbc:jtds:sqlserver://<host>/<db>";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MSSQLSQLGenerator createSQLGenerator() {
        return new MSSQLSQLGenerator(this);
    }

    @Override
    public Class getHibernateDialectClass() {
        return org.hibernate.dialect.SQLServerDialect.class;
    }
}

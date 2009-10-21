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

import de.erdesignerng.dialect.DataType;
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
        setCastType(NameCastType.NOTHING);

        registerType(createDataType("bit", "", Types.BIT, Types.BOOLEAN));
        registerType(createDataType("tinyint", "", Types.TINYINT));
        registerType(createDataType("tinyint identity", "", true, Types.TINYINT));
        registerType(createDataType("bigint", "", Types.BIGINT));
        registerType(createDataType("bigint identity", "", true, Types.BIGINT));
        registerType(createDataType("varbinary", "$size", Types.VARBINARY));
        registerType(createDataType("binary", "$size", Types.BINARY, Types.LONGVARBINARY));
        registerType(createDataType("timestamp", "", Types.TIMESTAMP, Types.DATE, Types.TIME));
        registerType(createDataType("char", "$size", Types.CHAR));
        registerType(createDataType("nchar", "$size", Types.CHAR));
        registerType(createDataType("uniqueidentifier", "", Types.CHAR));
        registerType(createDataType("numeric", "$size,$fraction", Types.NUMERIC));
        registerType(createDataType("numeric() identity", "$size,$fraction", true, Types.NUMERIC));
        registerType(createDataType("decimal", "$size,$fraction", Types.DECIMAL));
        registerType(createDataType("money", "", Types.DECIMAL));
        registerType(createDataType("smallmoney", "", Types.DECIMAL));
        registerType(createDataType("decimal() identity", "$size,$fraction", true, Types.DECIMAL));
        registerType(createDataType("int", "", Types.INTEGER));
        registerType(createDataType("int identity", "", true, Types.INTEGER));
        registerType(createDataType("smallint", "", Types.SMALLINT));
        registerType(createDataType("smallint identity", "", true, Types.SMALLINT));
        registerType(createDataType("real", "", Types.REAL));
        registerType(createDataType("float", "", Types.DOUBLE, Types.FLOAT));
        registerType(createDataType("varchar", "$size", Types.VARCHAR));
        registerType(createDataType("nvarchar", "$size", Types.VARCHAR));
        registerType(createDataType("sysname", "", Types.VARCHAR));
        registerType(createDataType("sql_variant", "", Types.VARCHAR));
        registerType(createDataType("datetime", "", Types.TIMESTAMP, Types.DATE, Types.TIME));
        registerType(createDataType("smalldatetime", "", Types.TIMESTAMP, Types.DATE, Types.TIME));
        registerType(createDataType("image", "", Types.BLOB));
        registerType(createDataType("ntext", "", Types.CLOB, Types.LONGVARCHAR));
        registerType(createDataType("xml", "", Types.SQLXML));
        registerType(createDataType("text", "", Types.CLOB));
        registerType(createDataType("date", "", Types.DATE));

        seal();
    }

    @Override
    public MSSQLReverseEngineeringStrategy getReverseEngineeringStrategy() {
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
    public MSSQLSQLGenerator createSQLGenerator() {
        return new MSSQLSQLGenerator(this);
    }

    @Override
    public Class getHibernateDialectClass() {
        return org.hibernate.dialect.SQLServerDialect.class;
    }

    @Override
    public DataType createDataType(String aName, String aDefinition, int... aJdbcType) {
        return new MSSQLDataType(aName, aDefinition, aJdbcType);
    }

    @Override
    public DataType createDataType(String aName, String aDefinition, boolean aIdentity, int... aJdbcType) {
        return new MSSQLDataType(aName, aDefinition, aIdentity, aJdbcType);
    }
}

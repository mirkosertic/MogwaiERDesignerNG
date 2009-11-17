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
package de.erdesignerng.dialect.postgres;

import java.sql.Types;

import de.erdesignerng.dialect.DataType;
import de.erdesignerng.dialect.GenericDataTypeImpl;
import de.erdesignerng.dialect.NameCastType;
import de.erdesignerng.dialect.sql92.SQL92Dialect;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-11-15 17:04:23 $
 */
public class PostgresDialect extends SQL92Dialect {

    public PostgresDialect() {
        setSpacesAllowedInObjectNames(true);
        setCaseSensitive(false);
        setMaxObjectNameLength(255);
        setNullablePrimaryKeyAllowed(false);
        setCastType(NameCastType.NOTHING);
        setSupportsDomains(true);
        setSupportsSchemaInformation(true);
        addSystemSchema("information_schema");
        addSystemSchema("pg_catalog");
        addSystemSchema("pg_toast_temp");
        addSystemSchema("pg_toast_temp_1");

        registerType(createDataType("bool", "", Types.BIT));
        registerType(createDataType("bytea", "", Types.BINARY, Types.VARBINARY));
        registerType(createDataType("name", "", Types.VARCHAR));
        registerType(createDataType("int8", "", Types.BIGINT));
        registerType(createDataType("bigserial", "", true, Types.BIGINT));
        registerType(createDataType("int2", "", Types.SMALLINT, Types.TINYINT));
        registerType(createDataType("int4", "", Types.INTEGER));
        registerType(createDataType("serial", "", true, Types.INTEGER));
        registerType(createDataType("text", "", Types.VARCHAR));
        registerType(createDataType("oid", "", Types.INTEGER));
        registerType(createDataType("float4", "", Types.REAL));
        registerType(createDataType("float8", "", Types.DOUBLE, Types.FLOAT));
        registerType(createDataType("money", "", Types.DOUBLE));
        registerType(createDataType("bpchar", "", Types.CHAR));
        registerType(createDataType("varchar", "[" + GenericDataTypeImpl.SIZE_TOKEN + "]", Types.VARCHAR));
        registerType(createDataType("date", "", Types.DATE));
        registerType(createDataType("time", "", Types.TIME));
        registerType(createDataType("timestamp", "", Types.TIMESTAMP));
        registerType(createDataType("timestamptz", "", Types.TIMESTAMP));
        registerType(createDataType("timestamp with time zone", "", Types.TIMESTAMP));
        registerType(createDataType("timetz", "", Types.TIME));
        registerType(createDataType("bit", "", Types.BIT));
        registerType(createDataType("numeric", GenericDataTypeImpl.SIZE_TOKEN + ","
                + GenericDataTypeImpl.FRACTION_TOKEN, Types.NUMERIC, Types.DECIMAL));

        // Patch [ 2124875 ] Add Postgres data types
        registerType(createDataType("char", GenericDataTypeImpl.SIZE_TOKEN, Types.CHAR));
        registerType(createDataType("character", GenericDataTypeImpl.SIZE_TOKEN, Types.CHAR));
        registerType(createDataType("character varying", "[" + GenericDataTypeImpl.SIZE_TOKEN + "]", Types.VARCHAR));
        registerType(createDataType("boolean", "", Types.BOOLEAN));
        registerType(createDataType("interval", "", Types.TIMESTAMP));
        registerType(createDataType("smallint", "", Types.SMALLINT));
        registerType(createDataType("integer", "", Types.INTEGER));

        registerType(createDataType("bigint", "", Types.BIGINT));
        registerType(createDataType("real", "", Types.REAL));
        registerType(createDataType("double precision", "", Types.DOUBLE));
        registerType(createDataType("xml", "", Types.SQLXML));
        registerType(createDataType("blob", "", Types.BLOB, Types.LONGVARBINARY));
        registerType(createDataType("clob", "", Types.CLOB, Types.LONGVARCHAR));

        // Patch [ 2874576 ] Reverse-Engineering unterstuetzt INET Datentyp
        // nicht
        // @see
        // http://www.postgresql.org/docs/8.4/interactive/datatype-net-types.html#DATATYPE-INET
        registerType(createDataType("inet", "", Types.VARCHAR, Types.NVARCHAR));

        // TODO [dr-death] add all missing datatypes according to
        // @see
        // http://www.postgresql.org/docs/8.4/interactive/datatype.html#DATATYPE-TABLE
        // registerType(createDataType("_point", "", Types.OTHER));
        // registerType(createDataType("point", "", Types.OTHER));
        // registerType(createDataType("abstime", "", Types.OTHER));
        // registerType(createDataType("_xml", "", Types.OTHER));

        seal();
    }

    @Override
    public PostgresReverseEngineeringStrategy getReverseEngineeringStrategy() {
        return new PostgresReverseEngineeringStrategy(this);
    }

    @Override
    public String getUniqueName() {
        return "PostgresDialect";
    }

    @Override
    public String getDriverClassName() {
        return "org.postgresql.Driver";
    }

    @Override
    public String getDriverURLTemplate() {
        return "jdbc:postgresql://<host>:<port>/<db>";
    }

    @Override
    public PostgresSQLGenerator createSQLGenerator() {
        return new PostgresSQLGenerator(this);
    }

    @Override
    public Class getHibernateDialectClass() {
        return org.hibernate.dialect.PostgreSQLDialect.class;
    }

    @Override
    public DataType createDataType(String aName, String aDefinition, int... aJdbcType) {
        return new PostgresDataType(aName, aDefinition, aJdbcType);
    }

    @Override
    public DataType createDataType(String aName, String aDefinition, boolean aIdentity, int... aJdbcType) {
        return new PostgresDataType(aName, aDefinition, aIdentity, aJdbcType);
    }
}
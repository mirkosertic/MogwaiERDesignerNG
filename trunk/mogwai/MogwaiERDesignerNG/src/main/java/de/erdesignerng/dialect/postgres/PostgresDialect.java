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

import de.erdesignerng.dialect.NameCastType;
import de.erdesignerng.dialect.sql92.SQL92Dialect;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-11-15 17:04:23 $
 */
public class PostgresDialect extends SQL92Dialect {

    public PostgresDialect() {
        setSpacesAllowedInObjectNames(false);
        setCaseSensitive(true);
        setMaxObjectNameLength(255);
        setNullablePrimaryKeyAllowed(false);
        setCastType(NameCastType.NOTHING);

        registerType(new PostgresDataType("bool", "", Types.BIT));
        registerType(new PostgresDataType("bytea", "", Types.BINARY, Types.VARBINARY));
        registerType(new PostgresDataType("name", "", Types.VARCHAR));
        registerType(new PostgresDataType("int8", "", Types.BIGINT));
        registerType(new PostgresDataType("bigserial", "", true, Types.BIGINT));
        registerType(new PostgresDataType("int2", "", Types.SMALLINT, Types.TINYINT));
        registerType(new PostgresDataType("int4", "", Types.INTEGER));
        registerType(new PostgresDataType("serial", "", true, Types.INTEGER));
        registerType(new PostgresDataType("text", "", Types.VARCHAR));
        registerType(new PostgresDataType("oid", "", Types.INTEGER));
        registerType(new PostgresDataType("float4", "", Types.REAL));
        registerType(new PostgresDataType("float8", "", Types.DOUBLE, Types.FLOAT));
        registerType(new PostgresDataType("money", "", Types.DOUBLE));
        registerType(new PostgresDataType("bpchar", "", Types.CHAR));
        registerType(new PostgresDataType("varchar", "$size", Types.VARCHAR));
        registerType(new PostgresDataType("date", "", Types.DATE));
        registerType(new PostgresDataType("time", "", Types.TIME));
        registerType(new PostgresDataType("timestamp", "", Types.TIMESTAMP));
        registerType(new PostgresDataType("timestamptz", "", Types.TIMESTAMP));
        registerType(new PostgresDataType("timetz", "", Types.TIME));
        registerType(new PostgresDataType("bit", "", Types.BIT));
        registerType(new PostgresDataType("numeric", "$size,$fraction", Types.NUMERIC, Types.DECIMAL));

        // Patch [ 2124875 ] Add Postgres data types
        registerType(new PostgresDataType("char", "$size", Types.CHAR));
        registerType(new PostgresDataType("character", "$size", Types.CHAR));
        registerType(new PostgresDataType("boolean", "", Types.BOOLEAN));
        registerType(new PostgresDataType("interval", "", Types.TIMESTAMP));
        registerType(new PostgresDataType("smallint", "", Types.SMALLINT));
        registerType(new PostgresDataType("integer", "", Types.INTEGER));

        registerType(new PostgresDataType("bigint", "", Types.BIGINT));
        registerType(new PostgresDataType("real", "", Types.REAL));
        registerType(new PostgresDataType("double precision", "", Types.DOUBLE));
        registerType(new PostgresDataType("xml", "", Types.SQLXML));
        registerType(new PostgresDataType("blob", "", Types.BLOB, Types.LONGVARBINARY));
        registerType(new PostgresDataType("clob", "", Types.CLOB, Types.LONGVARCHAR));

        seal();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PostgresReverseEngineeringStrategy getReverseEngineeringStrategy() {
        return new PostgresReverseEngineeringStrategy(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUniqueName() {
        return "PostgresDialect";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDriverClassName() {
        return "org.postgresql.Driver";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDriverURLTemplate() {
        return "jdbc:postgresql://<host>:<port>/<db>";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PostgresSQLGenerator createSQLGenerator() {
        return new PostgresSQLGenerator(this);
    }

    @Override
    public Class getHibernateDialectClass() {
        return org.hibernate.dialect.PostgreSQLDialect.class;
    }
}
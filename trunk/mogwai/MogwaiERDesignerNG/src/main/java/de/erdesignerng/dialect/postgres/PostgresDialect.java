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

import de.erdesignerng.dialect.NameCastType;
import de.erdesignerng.dialect.sql92.SQL92Dialect;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-06-13 16:49:00 $
 */
public class PostgresDialect extends SQL92Dialect {

    public PostgresDialect() {
        setSpacesAllowedInObjectNames(false);
        setCaseSensitive(true);
        setMaxObjectNameLength(255);
        setNullablePrimaryKeyAllowed(false);
        setCastType(NameCastType.NOTHING);

        registerType(new PostgresDataType("bool", "", java.sql.Types.BIT));
        registerType(new PostgresDataType("bytea", "", java.sql.Types.BINARY));
        registerType(new PostgresDataType("name", "", java.sql.Types.VARCHAR));
        registerType(new PostgresDataType("int8", "", java.sql.Types.BIGINT));
        registerType(new PostgresDataType("bigserial", "", java.sql.Types.BIGINT, true));
        registerType(new PostgresDataType("int2", "", java.sql.Types.SMALLINT));
        registerType(new PostgresDataType("int4", "", java.sql.Types.INTEGER));
        registerType(new PostgresDataType("serial", "", java.sql.Types.INTEGER, true));
        registerType(new PostgresDataType("text", "", java.sql.Types.VARCHAR));
        registerType(new PostgresDataType("oid", "", java.sql.Types.INTEGER));
        registerType(new PostgresDataType("float4", "", java.sql.Types.REAL));
        registerType(new PostgresDataType("float8", "", java.sql.Types.DOUBLE));
        registerType(new PostgresDataType("money", "", java.sql.Types.DOUBLE));
        registerType(new PostgresDataType("bpchar", "", java.sql.Types.CHAR));
        registerType(new PostgresDataType("varchar", "$size", java.sql.Types.VARCHAR));
        registerType(new PostgresDataType("date", "", java.sql.Types.DATE));
        registerType(new PostgresDataType("time", "", java.sql.Types.TIME));
        registerType(new PostgresDataType("timestamp", "", java.sql.Types.TIMESTAMP));
        registerType(new PostgresDataType("timestamptz", "", java.sql.Types.TIMESTAMP));
        registerType(new PostgresDataType("timetz", "", java.sql.Types.TIME));
        registerType(new PostgresDataType("bit", "", java.sql.Types.BIT));
        registerType(new PostgresDataType("numeric", "$size,$fraction", java.sql.Types.NUMERIC));
        
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
}
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
package de.erdesignerng.dialect.h2;

import java.sql.Types;

import de.erdesignerng.dialect.DataType;
import de.erdesignerng.dialect.NameCastType;
import de.erdesignerng.dialect.sql92.SQL92Dialect;

/**
 * @author $Author: gniddelgesicht $
 * @version $Date: 2008/11/15 17:04:23 $
 */
public class H2Dialect extends SQL92Dialect {

    public H2Dialect() {
        setSpacesAllowedInObjectNames(false);
        setCaseSensitive(true);
        setMaxObjectNameLength(255);
        setNullablePrimaryKeyAllowed(false);
        setCastType(NameCastType.UPPERCASE);
        setSupportsColumnExtra(true);

        // registerType(new H2DataType("array", "", Types.ARRAY));
        registerType(new H2DataType("bigint", "", Types.BIGINT));
        registerType(new H2DataType("binay", "$size", Types.BINARY));
        registerType(new H2DataType("blob", "$size", Types.BLOB));
        registerType(new H2DataType("boolean", "", Types.BOOLEAN, Types.BIT));
        registerType(new H2DataType("char", "$size", Types.CHAR));
        registerType(new H2DataType("clob", "$size", Types.CLOB));
        registerType(new H2DataType("date", "", Types.DATE));
        registerType(new H2DataType("decimal", "$size,$fraction", Types.NUMERIC));
        registerType(new H2DataType("double", "", Types.DOUBLE));
        registerType(new H2DataType("float", "", Types.FLOAT));
        registerType(new H2DataType("identity", "", true, Types.BIGINT));
        registerType(new H2DataType("integer", "", Types.INTEGER));
        registerType(new H2DataType("longvarbinary", "$size", Types.LONGVARBINARY, Types.SQLXML));
        registerType(new H2DataType("longvarchar", "$size", Types.LONGVARCHAR));
        registerType(new H2DataType("numeric", "$size,$fraction", Types.NUMERIC, Types.DECIMAL));
        registerType(new H2DataType("real", "", Types.REAL));
        registerType(new H2DataType("smallint", "", Types.SMALLINT));
        registerType(new H2DataType("time", "", Types.TIME));
        registerType(new H2DataType("timestamp", "", Types.TIMESTAMP));
        registerType(new H2DataType("tinyint", "", Types.TINYINT));
        registerType(new H2DataType("uuid", "$size", true, Types.BINARY));
        registerType(new H2DataType("varbinary", "$size", Types.VARBINARY));
        registerType(new H2DataType("varchar", "$size", Types.VARCHAR));
        registerType(new H2DataType("varchar_ignorecase", "$size", Types.VARCHAR));

        seal();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public H2ReverseEngineeringStrategy getReverseEngineeringStrategy() {
        return new H2ReverseEngineeringStrategy(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUniqueName() {
        return "H2Dialect";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDriverClassName() {
        return "org.h2.Driver";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDriverURLTemplate() {
        return "jdbc:h2:<Path to database directory>";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public H2SQLGenerator createSQLGenerator() {
        return new H2SQLGenerator(this);
    }

    @Override
    public Class getHibernateDialectClass() {
        return org.hibernate.dialect.H2Dialect.class;
    }
    
    @Override
    public DataType createDataType(String aName, String aDefinition, int... aJdbcType) {
        return new H2DataType(aName, aDefinition, aJdbcType);
    }

    @Override
    public DataType createDataType(String aName, String aDefinition, boolean anIdentity, int... aJdbcType) {
        return new H2DataType(aName, aDefinition, anIdentity, aJdbcType);
    }    
}
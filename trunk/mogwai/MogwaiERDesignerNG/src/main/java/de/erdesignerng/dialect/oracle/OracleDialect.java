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
package de.erdesignerng.dialect.oracle;

import java.sql.Types;

import de.erdesignerng.dialect.NameCastType;
import de.erdesignerng.dialect.sql92.SQL92Dialect;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-09 19:07:31 $
 */
public class OracleDialect extends SQL92Dialect {

    public OracleDialect() {
        setSpacesAllowedInObjectNames(false);
        setCaseSensitive(false);
        setMaxObjectNameLength(32);
        setNullablePrimaryKeyAllowed(false);
        setCastType(NameCastType.UPPERCASE);
        setSupportsOnUpdate(false);

        registerType(new OracleDataType("LONG RAW", "", true, Types.LONGVARBINARY));
        registerType(new OracleDataType("RAW", "$size", true, Types.VARBINARY, Types.BINARY));
        registerType(new OracleDataType("LONG", "", true, Types.LONGVARCHAR));
        registerType(new OracleDataType("CHAR", "$size", Types.CHAR));
        registerType(new OracleDataType("NUMBER", "$size,$fraction", Types.NUMERIC, Types.INTEGER, Types.BIGINT,
                Types.DECIMAL, Types.DOUBLE, Types.SMALLINT, Types.BIT, Types.TINYINT, Types.BOOLEAN));
        registerType(new OracleDataType("FLOAT", "", Types.FLOAT));
        registerType(new OracleDataType("REAL", "", Types.REAL));
        registerType(new OracleDataType("VARCHAR2", "$size", Types.VARCHAR));
        registerType(new OracleDataType("NVARCHAR2", "$size", Types.VARCHAR));
        registerType(new OracleDataType("DATE", "", Types.DATE));
        registerType(new OracleDataType("TIMESTAMP", "", Types.TIMESTAMP, Types.TIME));
        registerType(new OracleDataType("BLOB", "", Types.BLOB));
        registerType(new OracleDataType("CLOB", "", Types.CLOB));
        registerType(new OracleDataType("NCLOB", "", Types.CLOB));
        registerType(new OracleDataType("XMLTYPE", "", Types.SQLXML));
        registerType(new OracleDataType("ROWID", "", Types.OTHER));
        registerType(new OracleDataType("SDO_GEOMETRY", "", Types.OTHER));
        registerType(new OracleDataType("SDO_GTYPE", "", Types.OTHER));
        registerType(new OracleDataType("SDO_SRID", "", Types.OTHER));
        registerType(new OracleDataType("SDO_POINT", "", Types.OTHER));
        registerType(new OracleDataType("SDO_ELEM_INFO", "", Types.OTHER));
        registerType(new OracleDataType("SDO_ORDINATES", "", Types.OTHER));

        seal();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OracleReverseEngineeringStrategy getReverseEngineeringStrategy() {
        return new OracleReverseEngineeringStrategy(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUniqueName() {
        return "OracleDialect";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDriverClassName() {
        return "oracle.jdbc.driver.OracleDriver";
    }

    @Override
    public String getDriverURLTemplate() {
        return "jdbc:oracle:thin:@//<host>:<port>/<db>";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OracleSQLGenerator createSQLGenerator() {
        return new OracleSQLGenerator(this);
    }

    @Override
    public Class getHibernateDialectClass() {
        return org.hibernate.dialect.Oracle8iDialect.class;
    }
}
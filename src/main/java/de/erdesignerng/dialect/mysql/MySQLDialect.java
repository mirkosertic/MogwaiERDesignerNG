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

import java.sql.Types;

import de.erdesignerng.dialect.NameCastType;
import de.erdesignerng.dialect.sql92.SQL92Dialect;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-11-02 14:20:18 $
 */
public class MySQLDialect extends SQL92Dialect {

    public MySQLDialect() {
        setSpacesAllowedInObjectNames(false);
        setCaseSensitive(true);
        setMaxObjectNameLength(64);
        setNullablePrimaryKeyAllowed(false);
        setCastType(NameCastType.NOTHING);
        setSupportsColumnExtra(true);

        // Other
        registerType(new MySQLDataType("BIT", "$size", Types.BIT));
        registerType(new MySQLDataType("BOOL", "", Types.BIT, Types.BOOLEAN));
        
        // Numeric
        registerType(new MySQLDataType("TINYINT", "", Types.TINYINT));
        registerType(new MySQLDataType("TINYINT UNSIGNED", "", Types.TINYINT));
        registerType(new MySQLDataType("BIGINT", "", Types.BIGINT));
        registerType(new MySQLDataType("BIGINT UNSIGNED", "", Types.BIGINT));
        registerType(new MySQLDataType("NUMERIC", "$size,$fraction", Types.NUMERIC));
        registerType(new MySQLDataType("DECIMAL", "$size,$fraction", Types.DECIMAL));
        registerType(new MySQLDataType("DECIMAL UNSIGNED", "$size,$fraction", Types.DECIMAL));
        registerType(new MySQLDataType("INTEGER", "", Types.INTEGER));
        registerType(new MySQLDataType("INTEGER UNSIGNED", "", Types.INTEGER));
        registerType(new MySQLDataType("INT", "", Types.INTEGER));
        registerType(new MySQLDataType("INT UNSIGNED", "", Types.INTEGER));
        registerType(new MySQLDataType("MEDIUMINT", "", Types.INTEGER));
        registerType(new MySQLDataType("MEDIUMINT UNSIGNED", "", Types.INTEGER));
        registerType(new MySQLDataType("SMALLINT", "", Types.SMALLINT));
        registerType(new MySQLDataType("SMALLINT UNSIGNED", "", Types.SMALLINT));
        registerType(new MySQLDataType("FLOAT", "$size,$fraction", Types.REAL, Types.FLOAT));
        registerType(new MySQLDataType("DOUBLE", "$size,$fraction", Types.DOUBLE));
        registerType(new MySQLDataType("DOUBLE PRECISION", "$size,$fraction", Types.DOUBLE));
        registerType(new MySQLDataType("REAL", "$size,$fraction", Types.DOUBLE));
        
        // Blob
        registerType(new MySQLDataType("LONG VARBINARY", "", Types.LONGVARBINARY));
        registerType(new MySQLDataType("MEDIUMBLOB", "", Types.LONGVARBINARY));
        registerType(new MySQLDataType("LONGBLOB", "", Types.LONGVARBINARY));
        registerType(new MySQLDataType("BLOB", "", Types.BLOB));
        registerType(new MySQLDataType("CLOB", "", Types.CLOB, Types.SQLXML));
        registerType(new MySQLDataType("TINYBLOB", "", Types.LONGVARBINARY));
        registerType(new MySQLDataType("VARBINARY", "$size", Types.VARBINARY));
        registerType(new MySQLDataType("BINARY", "$size", Types.BINARY));
        
        // Text
        registerType(new MySQLDataType("LONG VARCHAR", "", Types.LONGVARCHAR));
        registerType(new MySQLDataType("MEDIUMTEXT", "", Types.LONGVARCHAR));
        registerType(new MySQLDataType("LONGTEXT", "", Types.LONGVARCHAR));
        registerType(new MySQLDataType("TEXT", "", Types.LONGVARCHAR));
        registerType(new MySQLDataType("TINYTEXT", "", Types.LONGVARCHAR));
        registerType(new MySQLDataType("CHAR", "$size", Types.CHAR));
        registerType(new MySQLDataType("VARCHAR", "$size", Types.VARCHAR));
        
        // Date and time
        registerType(new MySQLDataType("DATE", "", Types.DATE));
        registerType(new MySQLDataType("TIME", "", Types.TIME));
        registerType(new MySQLDataType("DATETIME", "", Types.TIMESTAMP));
        registerType(new MySQLDataType("TIMESTAMP", "", Types.TIMESTAMP));
        
        seal();        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MySQLReverseEngineeringStrategy getReverseEngineeringStrategy() {
        return new MySQLReverseEngineeringStrategy(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUniqueName() {
        return "MySQLDialect";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDriverClassName() {
        return "com.mysql.jdbc.Driver";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDriverURLTemplate() {
        return "jdbc:mysql://<host>/<db>";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supportsSchemaInformation() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MySQLSQLGenerator createSQLGenerator() {
        return new MySQLSQLGenerator(this);
    }
    
    @Override
    public Class getHibernateDialectClass() {
        return org.hibernate.dialect.MySQLDialect.class;
    }
}
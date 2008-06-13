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

import de.erdesignerng.dialect.NameCastType;
import de.erdesignerng.dialect.sql92.SQL92Dialect;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-06-13 16:48:59 $
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
        registerType(new MySQLDataType("BIT", "$size", java.sql.Types.BIT));
        registerType(new MySQLDataType("BOOL", "", java.sql.Types.BIT));
        
        // Numeric
        registerType(new MySQLDataType("TINYINT", "", java.sql.Types.TINYINT));
        registerType(new MySQLDataType("TINYINT UNSIGNED", "", java.sql.Types.TINYINT));
        registerType(new MySQLDataType("BIGINT", "", java.sql.Types.BIGINT));
        registerType(new MySQLDataType("BIGINT UNSIGNED", "", java.sql.Types.BIGINT));
        registerType(new MySQLDataType("NUMERIC", "$size,$fraction", java.sql.Types.NUMERIC));
        registerType(new MySQLDataType("DECIMAL", "$size,$fraction", java.sql.Types.DECIMAL));
        registerType(new MySQLDataType("DECIMAL UNSIGNED", "$size,$fraction", java.sql.Types.DECIMAL));
        registerType(new MySQLDataType("INTEGER", "", java.sql.Types.INTEGER));
        registerType(new MySQLDataType("INTEGER UNSIGNED", "", java.sql.Types.INTEGER));
        registerType(new MySQLDataType("INT", "", java.sql.Types.INTEGER));
        registerType(new MySQLDataType("INT UNSIGNED", "", java.sql.Types.INTEGER));
        registerType(new MySQLDataType("MEDIUMINT", "", java.sql.Types.INTEGER));
        registerType(new MySQLDataType("MEDIUMINT UNSIGNED", "", java.sql.Types.INTEGER));
        registerType(new MySQLDataType("SMALLINT", "", java.sql.Types.SMALLINT));
        registerType(new MySQLDataType("SMALLINT UNSIGNED", "", java.sql.Types.SMALLINT));
        registerType(new MySQLDataType("FLOAT", "$size,$fraction", java.sql.Types.REAL));
        registerType(new MySQLDataType("DOUBLE", "$size,$fraction", java.sql.Types.DOUBLE));
        registerType(new MySQLDataType("DOUBLE PRECISION", "$size,$fraction", java.sql.Types.DOUBLE));
        registerType(new MySQLDataType("REAL", "$size,$fraction", java.sql.Types.DOUBLE));
        
        // Blob
        registerType(new MySQLDataType("LONG VARBINARY", "", java.sql.Types.LONGVARBINARY));
        registerType(new MySQLDataType("MEDIUMBLOB", "", java.sql.Types.LONGVARBINARY));
        registerType(new MySQLDataType("LONGBLOB", "", java.sql.Types.LONGVARBINARY));
        registerType(new MySQLDataType("BLOB", "", java.sql.Types.LONGVARBINARY));
        registerType(new MySQLDataType("TINYBLOB", "", java.sql.Types.LONGVARBINARY));
        registerType(new MySQLDataType("VARBINARY", "$size", java.sql.Types.VARBINARY));
        registerType(new MySQLDataType("BINARY", "$size", java.sql.Types.BINARY));
        
        // Text
        registerType(new MySQLDataType("LONG VARCHAR", "", java.sql.Types.LONGVARCHAR));
        registerType(new MySQLDataType("MEDIUMTEXT", "", java.sql.Types.LONGVARCHAR));
        registerType(new MySQLDataType("LONGTEXT", "", java.sql.Types.LONGVARCHAR));
        registerType(new MySQLDataType("TEXT", "", java.sql.Types.LONGVARCHAR));
        registerType(new MySQLDataType("TINYTEXT", "", java.sql.Types.LONGVARCHAR));
        registerType(new MySQLDataType("CHAR", "$size", java.sql.Types.CHAR));
        registerType(new MySQLDataType("VARCHAR", "$size", java.sql.Types.VARCHAR));
        
        // Date and time
        registerType(new MySQLDataType("DATE", "", java.sql.Types.DATE));
        registerType(new MySQLDataType("TIME", "", java.sql.Types.TIME));
        registerType(new MySQLDataType("DATETIME", "", java.sql.Types.TIMESTAMP));
        registerType(new MySQLDataType("TIMESTAMP", "", java.sql.Types.TIMESTAMP));
        
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
}
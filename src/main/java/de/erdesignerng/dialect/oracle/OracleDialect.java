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

import de.erdesignerng.dialect.NameCastType;
import de.erdesignerng.dialect.sql92.SQL92Dialect;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-11-02 14:20:18 $
 */
public class OracleDialect extends SQL92Dialect {

    public OracleDialect() {
        setSpacesAllowedInObjectNames(false);
        setCaseSensitive(false);
        setMaxObjectNameLength(32);
        setNullablePrimaryKeyAllowed(false);
        setCastType(NameCastType.UPPERCASE);
        setSupportsOnUpdate(false);

        registerType(new OracleDataType("LONG RAW", "", java.sql.Types.LONGVARBINARY, 1));
        registerType(new OracleDataType("RAW", "$size", java.sql.Types.VARBINARY, 1));
        registerType(new OracleDataType("LONG", "", java.sql.Types.LONGVARCHAR, 1));
        registerType(new OracleDataType("CHAR", "$size", java.sql.Types.CHAR));
        registerType(new OracleDataType("NUMBER", "$size,$fraction", java.sql.Types.NUMERIC));
        registerType(new OracleDataType("FLOAT", "", java.sql.Types.FLOAT));
        registerType(new OracleDataType("REAL", "", java.sql.Types.REAL));
        registerType(new OracleDataType("VARCHAR2", "$size", java.sql.Types.VARCHAR));
        registerType(new OracleDataType("NVARCHAR2", "$size", java.sql.Types.VARCHAR));
        registerType(new OracleDataType("DATE", "", java.sql.Types.DATE));
        registerType(new OracleDataType("TIMESTAMP", "", java.sql.Types.TIMESTAMP));
        registerType(new OracleDataType("BLOB", "", java.sql.Types.BLOB));
        registerType(new OracleDataType("CLOB", "", java.sql.Types.CLOB));
        registerType(new OracleDataType("NCLOB", "", java.sql.Types.CLOB));
        registerType(new OracleDataType("XMLTYPE", "", java.sql.Types.OTHER));
        registerType(new OracleDataType("ROWID", "", java.sql.Types.OTHER));
        
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
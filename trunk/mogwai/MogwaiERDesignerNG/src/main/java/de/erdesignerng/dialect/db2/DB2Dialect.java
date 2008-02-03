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
package de.erdesignerng.dialect.db2;

import de.erdesignerng.dialect.NameCastType;
import de.erdesignerng.dialect.sql92.SQL92Dialect;

/**
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2008-02-03 13:53:49 $
 */
public class DB2Dialect extends SQL92Dialect {

    public DB2Dialect() {
        setSpacesAllowedInObjectNames(false);
        setCaseSensitive(false);
        setMaxObjectNameLength(128);
        setNullablePrimaryKeyAllowed(false);
        setCastType(NameCastType.UPPERCASE);

        registerType(new DB2DataType("VARCHAR() FOR BIT DATA", "$size", java.sql.Types.VARBINARY));
        registerType(new DB2DataType("CHAR() FOR BIT DATA", "$size", java.sql.Types.BINARY));
        registerType(new DB2DataType("CHAR", "$size", java.sql.Types.CHAR));
        registerType(new DB2DataType("NUMERIC", "$size,$fraction", java.sql.Types.NUMERIC));
        registerType(new DB2DataType("DECIMAL", "$size,$fraction", java.sql.Types.DECIMAL));
        registerType(new DB2DataType("BIGINT", "", java.sql.Types.BIGINT));
        registerType(new DB2DataType("INTEGER", "", java.sql.Types.INTEGER));
        registerType(new DB2DataType("SMALLINT", "", java.sql.Types.SMALLINT));
        registerType(new DB2DataType("REAL", "", java.sql.Types.FLOAT));
        registerType(new DB2DataType("FLOAT", "$size", java.sql.Types.DOUBLE));
        registerType(new DB2DataType("VARCHAR", "$size", java.sql.Types.VARCHAR));
        registerType(new DB2DataType("DATE", "", java.sql.Types.DATE));
        registerType(new DB2DataType("TIME", "", java.sql.Types.TIME));
        registerType(new DB2DataType("TIMESTAMP", "", java.sql.Types.TIMESTAMP));
        
        seal();
    }

    @Override
    public DB2ReverseEngineeringStrategy getReverseEngineeringStrategy() {
        return new DB2ReverseEngineeringStrategy(this);
    }

    @Override
    public String getUniqueName() {
        return "DB2";
    }

    @Override
    public String getDriverClassName() {
        return "hit.db2.Db2Driver";
    }

    @Override
    public String getDriverURLTemplate() {
        return "jdbc:db2://<host>/<db>";
    }

    @Override
    public DB2SQLGenerator createSQLGenerator() {
        return new DB2SQLGenerator(this);
    }
}

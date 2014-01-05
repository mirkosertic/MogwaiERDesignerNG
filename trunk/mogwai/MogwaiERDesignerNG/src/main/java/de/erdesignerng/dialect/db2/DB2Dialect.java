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

import de.erdesignerng.dialect.DataType;
import de.erdesignerng.dialect.NameCastType;
import de.erdesignerng.dialect.Dialect;

import java.sql.Types;

/**
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2008-11-02 14:20:18 $
 */
public final class DB2Dialect extends Dialect {

	public DB2Dialect() {
		setSpacesAllowedInObjectNames(false);
		setCaseSensitive(false);
		setMaxObjectNameLength(128);
		setNullablePrimaryKeyAllowed(false);
		setCastType(NameCastType.UPPERCASE);

		registerType(createDataType("VARCHAR() FOR BIT DATA", "$size", Types.VARBINARY));
		registerType(createDataType("CHAR() FOR BIT DATA", "$size", Types.BINARY, Types.BIT));
		registerType(createDataType("CHAR", "$size", Types.CHAR));
		registerType(createDataType("NUMERIC", "$size,$fraction", Types.NUMERIC));
		registerType(createDataType("DECIMAL", "$size,$fraction", Types.DECIMAL));
		registerType(createDataType("BIGINT", "", Types.BIGINT));
		registerType(createDataType("BLOB", "", Types.BLOB, Types.LONGVARBINARY));
		registerType(createDataType("CLOB", "", Types.CLOB, Types.SQLXML, Types.LONGVARCHAR));
		registerType(createDataType("INTEGER", "", Types.INTEGER));
		registerType(createDataType("SMALLINT", "", Types.SMALLINT, Types.TINYINT, Types.BOOLEAN));
		registerType(createDataType("REAL", "", Types.FLOAT, Types.REAL));
		registerType(createDataType("FLOAT", "$size", Types.DOUBLE));
		registerType(createDataType("VARCHAR", "$size", Types.VARCHAR));
		registerType(createDataType("DATE", "", Types.DATE));
		registerType(createDataType("TIME", "", Types.TIME));
		registerType(createDataType("TIMESTAMP", "", Types.TIMESTAMP));

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

	@Override
	public Class getHibernateDialectClass() {
		return org.hibernate.dialect.DB2Dialect.class;
	}

	@Override
	public DataType createDataType(String aName, String aDefinition, int... aJdbcType) {
		return new DB2DataType(aName, aDefinition, aJdbcType);
	}

	@Override
	public DataType createDataType(String aName, String aDefinition, boolean anIdentity, int... aJdbcType) {
		return new DB2DataType(aName, aDefinition, anIdentity, aJdbcType);
	}

	@Override
	public DataType createDataType(String aName, String aDefinition, boolean anIdentity, boolean anArray, int... aJdbcType) {
		return new DB2DataType(aName, aDefinition, anIdentity, anArray, aJdbcType);
	}

}

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

import de.erdesignerng.dialect.DataType;
import de.erdesignerng.dialect.NameCastType;
import de.erdesignerng.dialect.sql92.SQL92Dialect;

import java.sql.Types;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-09 19:07:31 $
 */
public final class OracleDialect extends SQL92Dialect {

	public OracleDialect() {
		setSpacesAllowedInObjectNames(false);
		setCaseSensitive(false);
		setMaxObjectNameLength(64);
		setNullablePrimaryKeyAllowed(false);
		setCastType(NameCastType.UPPERCASE);
		setSupportsOnUpdate(false);
		setSuppressONALLIfNOACTION(true);

		registerType(createDataType("LONG RAW", "", true, Types.LONGVARBINARY));
		registerType(createDataType("RAW", "$size", true, Types.VARBINARY, Types.BINARY));
		registerType(createDataType("LONG", "", true, Types.LONGVARCHAR));
		registerType(createDataType("CHAR", "$size", Types.CHAR));
		registerType(createDataType("NUMBER", "$size,$fraction", Types.NUMERIC, Types.INTEGER, Types.BIGINT,
				Types.DECIMAL, Types.DOUBLE, Types.SMALLINT, Types.BIT, Types.TINYINT, Types.BOOLEAN));
		registerType(createDataType("FLOAT", "", Types.FLOAT));
		registerType(createDataType("REAL", "", Types.REAL));
		registerType(createDataType("VARCHAR2", "$size", Types.VARCHAR));
		registerType(createDataType("NVARCHAR2", "$size", Types.VARCHAR));
		registerType(createDataType("DATE", "", Types.DATE));
		registerType(createDataType("TIMESTAMP", "", Types.TIMESTAMP, Types.TIME));
		registerType(createDataType("BLOB", "", Types.BLOB));
		registerType(createDataType("CLOB", "", Types.CLOB));
		registerType(createDataType("NCLOB", "", Types.CLOB));
		registerType(createDataType("XMLTYPE", "", Types.SQLXML));
		registerType(createDataType("ROWID", "", Types.OTHER));
		registerType(createDataType("SDO_GEOMETRY", "", Types.OTHER));
		registerType(createDataType("SDO_GTYPE", "", Types.OTHER));
		registerType(createDataType("SDO_SRID", "", Types.OTHER));
		registerType(createDataType("SDO_POINT", "", Types.OTHER));
		registerType(createDataType("SDO_ELEM_INFO", "", Types.OTHER));
		registerType(createDataType("SDO_ORDINATES", "", Types.OTHER));

		seal();
	}

	@Override
	public OracleReverseEngineeringStrategy getReverseEngineeringStrategy() {
		return new OracleReverseEngineeringStrategy(this);
	}

	@Override
	public String getUniqueName() {
		return "OracleDialect";
	}

	@Override
	public String getDriverClassName() {
		return "oracle.jdbc.driver.OracleDriver";
	}

	@Override
	public String getDriverURLTemplate() {
		return "jdbc:oracle:thin:@//<host>:<port>/<db>";
	}

	@Override
	public OracleSQLGenerator createSQLGenerator() {
		return new OracleSQLGenerator(this);
	}

	@Override
	public Class getHibernateDialectClass() {
		return org.hibernate.dialect.Oracle8iDialect.class;
	}

	@Override
	public DataType createDataType(String aName, String aDefinition, int... aJdbcType) {
		return new OracleDataType(aName, aDefinition, aJdbcType);
	}

	@Override
	public DataType createDataType(String aName, String aDefinition, boolean aIdentity, int... aJdbcType) {
		return new OracleDataType(aName, aDefinition, aIdentity, aJdbcType);
	}
}
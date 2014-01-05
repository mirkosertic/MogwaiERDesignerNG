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

import de.erdesignerng.dialect.DataType;
import de.erdesignerng.dialect.NameCastType;
import de.erdesignerng.dialect.TableProperties;
import de.erdesignerng.dialect.Dialect;
import de.erdesignerng.model.Table;

import java.sql.Types;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-11-02 14:20:18 $
 */
public class MySQLDialect extends Dialect {

	public MySQLDialect() {
		setSpacesAllowedInObjectNames(false);
		setCaseSensitive(true);
		setMaxObjectNameLength(64);
		setNullablePrimaryKeyAllowed(false);
		setCastType(NameCastType.NOTHING);
		setSupportsColumnExtra(true);

		// Other
		registerType(createDataType("BIT", "$size", Types.BIT));
		registerType(createDataType("BOOL", "", Types.BIT, Types.BOOLEAN));

		// Numeric
		registerType(createDataType("TINYINT", "", Types.TINYINT));
		registerType(createDataType("TINYINT UNSIGNED", "", Types.TINYINT));
		registerType(createDataType("BIGINT", "", Types.BIGINT));
		registerType(createDataType("BIGINT UNSIGNED", "", Types.BIGINT));
		registerType(createDataType("NUMERIC", "$size,$fraction", Types.NUMERIC));
		registerType(createDataType("DECIMAL", "$size,$fraction", Types.DECIMAL));
		registerType(createDataType("DECIMAL UNSIGNED", "$size,$fraction", Types.DECIMAL));
		registerType(createDataType("INTEGER", "", Types.INTEGER));
		registerType(createDataType("INTEGER UNSIGNED", "", Types.INTEGER));
		registerType(createDataType("INT", "", Types.INTEGER));
		registerType(createDataType("INT UNSIGNED", "", Types.INTEGER));
		registerType(createDataType("MEDIUMINT", "", Types.INTEGER));
		registerType(createDataType("MEDIUMINT UNSIGNED", "", Types.INTEGER));
		registerType(createDataType("SMALLINT", "", Types.SMALLINT));
		registerType(createDataType("SMALLINT UNSIGNED", "", Types.SMALLINT));
		registerType(createDataType("FLOAT", "$size,$fraction", Types.REAL, Types.FLOAT));
		registerType(createDataType("DOUBLE", "$size,$fraction", Types.DOUBLE));
		registerType(createDataType("DOUBLE PRECISION", "$size,$fraction", Types.DOUBLE));
		registerType(createDataType("REAL", "$size,$fraction", Types.DOUBLE));

		// Blob
		registerType(createDataType("LONG VARBINARY", "", Types.LONGVARBINARY));
		registerType(createDataType("MEDIUMBLOB", "", Types.LONGVARBINARY));
		registerType(createDataType("LONGBLOB", "", Types.LONGVARBINARY));
		registerType(createDataType("BLOB", "", Types.BLOB));
		registerType(createDataType("CLOB", "", Types.CLOB, Types.SQLXML));
		registerType(createDataType("TINYBLOB", "", Types.LONGVARBINARY));
		registerType(createDataType("VARBINARY", "$size", Types.VARBINARY));
		registerType(createDataType("BINARY", "$size", Types.BINARY));

		// Text
		registerType(createDataType("LONG VARCHAR", "", Types.LONGVARCHAR));
		registerType(createDataType("MEDIUMTEXT", "", Types.LONGVARCHAR));
		registerType(createDataType("LONGTEXT", "", Types.LONGVARCHAR));
		registerType(createDataType("TEXT", "", Types.LONGVARCHAR));
		registerType(createDataType("TINYTEXT", "", Types.LONGVARCHAR));
		registerType(createDataType("CHAR", "$size", Types.CHAR));
		registerType(createDataType("VARCHAR", "$size", Types.VARCHAR));

		// Date and time
		registerType(createDataType("DATE", "", Types.DATE));
		registerType(createDataType("TIME", "", Types.TIME));
		registerType(createDataType("DATETIME", "", Types.TIMESTAMP));
		registerType(createDataType("TIMESTAMP", "", Types.TIMESTAMP));

		// Enums and sets
		registerType(createDataType("ENUM", "$extra", Types.VARCHAR));
		registerType(createDataType("SET", "$extra", Types.VARCHAR));

		// Spatial
		registerType(createDataType("GEOMETRY", "", SPATIAL_COLUMN_TYPE));
		registerType(createDataType("POINT", "", SPATIAL_COLUMN_TYPE));
		registerType(createDataType("LINESTRING", "", SPATIAL_COLUMN_TYPE));
		registerType(createDataType("POLYGON", "", SPATIAL_COLUMN_TYPE));
		registerType(createDataType("MULTIPOINT", "", SPATIAL_COLUMN_TYPE));
		registerType(createDataType("MULTILINESTRING", "", SPATIAL_COLUMN_TYPE));
		registerType(createDataType("MULTIPOLYGON", "", SPATIAL_COLUMN_TYPE));
		registerType(createDataType("GEOMETRYCOLLECTION", "", SPATIAL_COLUMN_TYPE));

		seal();
	}

	@Override
	public MySQLReverseEngineeringStrategy getReverseEngineeringStrategy() {
		return new MySQLReverseEngineeringStrategy(this);
	}

	@Override
	public String getUniqueName() {
		return "MySQLDialect";
	}

	@Override
	public String getDriverClassName() {
		return "com.mysql.jdbc.Driver";
	}

	@Override
	public String getDriverURLTemplate() {
		return "jdbc:mysql://<host>/<db>";
	}

	@Override
	public boolean isSupportsSchemaInformation() {
		return false;
	}

	@Override
	public MySQLSQLGenerator createSQLGenerator() {
		return new MySQLSQLGenerator(this);
	}

	@Override
	public Class getHibernateDialectClass() {
		return org.hibernate.dialect.MySQLDialect.class;
	}

	@Override
	public DataType createDataType(String aName, String aDefinition, int... aJdbcType) {
		return new MySQLDataType(aName, aDefinition, aJdbcType);
	}

	@Override
	public DataType createDataType(String aName, String aDefinition, boolean anIdentity, int... aJdbcType) {
		return new MySQLDataType(aName, aDefinition, anIdentity, aJdbcType);
	}

	@Override
	public DataType createDataType(String aName, String aDefinition, boolean anIdentity, boolean anArray, int... aJdbcType) {
		return new MySQLDataType(aName, aDefinition, anIdentity, anArray, aJdbcType);
	}

	@Override
	public TableProperties createTablePropertiesFor(Table aTable) {
		MySQLTableProperties theProperties = new MySQLTableProperties();
		theProperties.initializeFrom(aTable);
		return theProperties;
	}

	@Override
	public boolean supportsSpatialIndexes() {
		return true;
	}

	@Override
	public boolean supportsFulltextIndexes() {
		return true;
	}

}
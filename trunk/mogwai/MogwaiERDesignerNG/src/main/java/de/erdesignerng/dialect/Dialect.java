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
package de.erdesignerng.dialect;

import de.erdesignerng.DialogUtils;
import de.erdesignerng.exception.ElementInvalidNameException;
import de.erdesignerng.model.*;
import org.apache.commons.lang.StringUtils;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-11-15 19:12:36 $
 */
public abstract class Dialect {

	public static final int SPATIAL_COLUMN_TYPE = 9001;

	private boolean caseSensitive;

	private boolean spacesAllowedInObjectNames;

	private int maxObjectNameLength;

	private boolean nullablePrimaryKeyAllowed;

	private boolean supportsOnUpdate = true;

	private boolean supportsOnDelete = true;

	private boolean generatesManagedConnection;

	private boolean supportsColumnExtra;

	private boolean suppressONALLIfNOACTION = false;

	private boolean supportsDomains = false;

	private boolean supportsCustomTypes = false;

	private boolean supportsSchemaInformation = true;

	private NameCastType castType;

	private final DataTypeList dataTypes = new DataTypeList();

	private ArrayList<String> systemSchemas = null;

	private String defaultSchemaName = null;

	private final HashMap<String, String> dataTypeAliases = new HashMap<>();

	protected void addDataTypeAlias(String aDataTypeAlias, String aBaseDataTypeName) {
		dataTypeAliases.put(aDataTypeAlias, aBaseDataTypeName);
	}

	public HashMap<String, String> getDataTypeAliases() {
		return dataTypeAliases;
	}

	/**
	 * @return the caseSensitive
	 */
	public boolean isCaseSensitive() {
		return caseSensitive;
	}

	/**
	 * @param aCaseSensitive the caseSensitive to set
	 */
	public void setCaseSensitive(boolean aCaseSensitive) {
		caseSensitive = aCaseSensitive;
	}

	/**
	 * @return the maxObjectNameLength
	 */
	public int getMaxObjectNameLength() {
		return maxObjectNameLength;
	}

	/**
	 * @param aMaxObjectNameLength the maxObjectNameLength to set
	 */
	public void setMaxObjectNameLength(int aMaxObjectNameLength) {
		maxObjectNameLength = aMaxObjectNameLength;
	}

	/**
	 * @return the spacesAllowedInObjectNames
	 */
	public boolean isSpacesAllowedInObjectNames() {
		return spacesAllowedInObjectNames;
	}

	/**
	 * @param aSpacesAllowedInObjectNames the spacesAllowedInObjectNames to set
	 */
	public void setSpacesAllowedInObjectNames(boolean aSpacesAllowedInObjectNames) {
		spacesAllowedInObjectNames = aSpacesAllowedInObjectNames;
	}

	/**
	 * Check the name of an element and return the converted name.
	 *
	 * @param aName the name
	 * @return the converted name
	 * @throws ElementInvalidNameException will be thrown if the name is invalid
	 */
	public String checkName(String aName) throws ElementInvalidNameException {
		if (StringUtils.isEmpty(aName)) {
			throw new ElementInvalidNameException("Element must have a name");
		}

		if (!spacesAllowedInObjectNames) {
			if (aName.indexOf(' ') > 0) {
				throw new ElementInvalidNameException("No spaces are allowed in an object name");
			}
		}

		if (aName.length() > maxObjectNameLength) {
			throw new ElementInvalidNameException("Object name to long : " + aName + " length is " + aName.length() + " maximum is " + maxObjectNameLength);
		}

		return castType.cast(aName);
	}

	/**
	 * @return the nullablePrimaryKeyAllowed
	 */
	public boolean isNullablePrimaryKeyAllowed() {
		return nullablePrimaryKeyAllowed;
	}

	/**
	 * @param aNullablePrimaryKeyAllowed the nullablePrimaryKeyAllowed to set
	 */
	public void setNullablePrimaryKeyAllowed(boolean aNullablePrimaryKeyAllowed) {
		nullablePrimaryKeyAllowed = aNullablePrimaryKeyAllowed;
	}

	/**
	 * @return the castType
	 */
	public NameCastType getCastType() {
		return castType;
	}

	/**
	 * @param aCastType the castType to set
	 */
	public void setCastType(NameCastType aCastType) {
		castType = aCastType;
	}

	/**
	 * Get the reverse engineering strategy.
	 *
	 * @return the reverse engineering strategy
	 */
	public abstract JDBCReverseEngineeringStrategy getReverseEngineeringStrategy();

	public abstract String getUniqueName();

	public String getDefaultUserName() {
		return "";
	}

	@Override
	public String toString() {
		return getUniqueName();
	}

	public abstract String getDriverClassName();

	public abstract String getDriverURLTemplate();

	/**
	 * Create a connection to a database.
	 *
	 * @param aClassLoader	   the classloader
	 * @param aDriver			the name of the driver
	 * @param aUrl			   the url
	 * @param aUser			  the user
	 * @param aPassword		  the password
	 * @param aPromptForPassword shall be prompted for the password
	 * @return the connection
	 * @throws ClassNotFoundException is thrown in case of an error
	 * @throws InstantiationException is thrown in case of an error
	 * @throws IllegalAccessException is thrown in case of an error
	 * @throws SQLException		   is thrown in case of an error
	 */
	public Connection createConnection(ClassLoader aClassLoader, String aDriver, String aUrl, String aUser, String aPassword, boolean aPromptForPassword) throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {
		Class<Driver> theDriverClass = (Class<Driver>) aClassLoader.loadClass(aDriver);
		Driver theDriver = theDriverClass.newInstance();

		if (aPromptForPassword) {
			aPassword = DialogUtils.promptForPassword();
			if (aPassword == null) {
				return null;
			}
		}

		Properties theProperties = new Properties();
		theProperties.put("user", aUser);
		theProperties.put("password", aPassword);

		return theDriver.connect(aUrl, theProperties);
	}

	public boolean isSupportsSchemaInformation() {
		return supportsSchemaInformation;
	}

	protected void registerType(DataType aType) {
		dataTypes.add(aType);
	}

	public abstract SQLGenerator createSQLGenerator();

	/**
	 * @return the supportsOnDelete
	 */
	public boolean isSupportsOnDelete() {
		return supportsOnDelete;
	}

	/**
	 * @param supportsOnDelete the supportsOnDelete to set
	 */
	public void setSupportsOnDelete(boolean supportsOnDelete) {
		this.supportsOnDelete = supportsOnDelete;
	}

	/**
	 * @return the supportsOnUpdate
	 */
	public boolean isSupportsOnUpdate() {
		return supportsOnUpdate;
	}

	/**
	 * @param supportsOnUpdate the supportsOnUpdate to set
	 */
	public void setSupportsOnUpdate(boolean supportsOnUpdate) {
		this.supportsOnUpdate = supportsOnUpdate;
	}

	/**
	 * Get the supported data types.
	 *
	 * @return the list of datatypes
	 */
	public DataTypeList getDataTypes() {
		return dataTypes;
	}

	protected void seal() {
	}

	public boolean generatesManagedConnection() {
		return generatesManagedConnection;
	}

	public void setGeneratesManagedConnection(boolean generatesStaticConnection) {
		generatesManagedConnection = generatesStaticConnection;
	}

	/**
	 * @return the supportsColumnExtra
	 */
	public boolean isSupportsColumnExtra() {
		return supportsColumnExtra;
	}

	/**
	 * @param supportsColumnExtra the supportsColumnExtra to set
	 */
	public void setSupportsColumnExtra(boolean supportsColumnExtra) {
		this.supportsColumnExtra = supportsColumnExtra;
	}

	/**
	 * @return the suppressONALLIfNOACTION
	 */
	public boolean isSuppressONALLIfNOACTION() {
		return suppressONALLIfNOACTION;
	}

	/**
	 * @param suppressONALLIfNOACTION the suppressONALLIfNOACTION to set
	 */
	public void setSuppressONALLIfNOACTION(boolean suppressONALLIfNOACTION) {
		this.suppressONALLIfNOACTION = suppressONALLIfNOACTION;
	}

	/**
	 * Get the separator chars for strings ( how they are escaped in SQL ).
	 *
	 * @return the separator chars
	 */
	public String getStringSeparatorChars() {
		return "'";
	}

	/**
	 * Get the hibernate dialect class for this dialect.
	 *
	 * @return the class
	 */
	public abstract Class getHibernateDialectClass();

	/**
	 * Find the closest matching type for a given foreign datatype.
	 * <p/>
	 * The matching is done by JDBC Type.
	 *
	 * @param aDataType the datatype
	 * @return the matching datatype or null if no match was found
	 */
	public DataType findClosestMatchingTypeFor(DataType aDataType) {
		for (int theCurrentJDBCType : aDataType.getJDBCType()) {
			for (DataType theType : dataTypes) {
				for (int theJDBCType : theType.getJDBCType()) {
					if (theJDBCType == theCurrentJDBCType) {
						return theType;
					}
				}
			}
		}
		return null;
	}

	public boolean isSupportsDomains() {
		return supportsDomains;
	}

	public void setSupportsDomains(boolean supportsDomains) {
		this.supportsDomains = supportsDomains;
	}

	public boolean isSupportsCustomTypes() {
		return supportsCustomTypes;
	}

	public void setSupportsCustomTypes(boolean supportsCustomTypes) {
		this.supportsCustomTypes = supportsCustomTypes;
	}

	public void setSupportsSchemaInformation(boolean supportsSchemaInformation) {
		this.supportsSchemaInformation = supportsSchemaInformation;
	}

	public List<String> getSystemSchemas() {
		if (supportsSchemaInformation) {
			return systemSchemas;
		}
		return new ArrayList<>();
	}

	public void addSystemSchema(String aSystemSchemaName) {
		if (systemSchemas == null) {
			systemSchemas = new ArrayList<>();
		}

		systemSchemas.add(aSystemSchemaName);
	}

	public abstract DataType createDataType(String aName, String aDefinition, int... aJdbcType);

	public abstract DataType createDataType(String aName, String aDefinition, boolean anIdentity, int... aJdbcType);

	public abstract DataType createDataType(String aName, String aDefinition, boolean anIdentity, boolean anArray, int... aJdbcType);

	/**
	 * Create a new table properties object for a given table.
	 *
	 * @param aTable the table
	 * @return the created table properties object
	 */
	public TableProperties createTablePropertiesFor(Table aTable) {
		TableProperties theProps = new TableProperties();
		theProps.initializeFrom(aTable);
		return theProps;
	}

	/**
	 * Create a new view properties object for a given view.
	 *
	 * @param aView the view
	 * @return the created view properties object
	 */
	public ViewProperties createViewPropertiesFor(View aView) {
		ViewProperties theProps = new ViewProperties();
		theProps.initializeFrom(aView);
		return theProps;
	}

	/**
	 * Create a new domain properties object for a given domain.
	 *
	 * @param aDomain the domain
	 * @return the created domain properties object
	 */
	public DomainProperties createDomainPropertiesFor(Domain aDomain) {
		DomainProperties theProps = new DomainProperties();
		theProps.initializeFrom(aDomain);
		return theProps;
	}

	/**
	 * Create a new relation properties object for a given relation.
	 *
	 * @param aRelation the relation
	 * @return the created relation properties object
	 */
	public RelationProperties createRelationPropertiesFor(Relation aRelation) {
		RelationProperties theProps = new RelationProperties();
		theProps.initializeFrom(aRelation);
		return theProps;
	}

	/**
	 * Create a new index properties object for a given index.
	 *
	 * @param aIndex the index
	 * @return the created index properties object
	 */
	public IndexProperties createIndexPropertiesFor(Index aIndex) {
		IndexProperties theProps = new IndexProperties();
		theProps.initializeFrom(aIndex);
		return theProps;
	}

	/**
	 * Does this dialect support spatial indexes?
	 *
	 * @return true, if spatial indexes are supported, false else
	 */
	public boolean supportsSpatialIndexes() {
		return false;
	}

	/**
	 * Does this dialect support fulltext indexes?
	 *
	 * @return true, if fulltext indexes are supported, false else
	 */
	public boolean supportsFulltextIndexes() {
		return false;
	}

	/**
	 * Returns the default schema name
	 *
	 * @return default schema name
	 */
	public String getDefaultSchemaName() {
		return defaultSchemaName;
	}

	/**
	 * @param defaultSchemaName the name of the default schema
	 */
	public void setDefaultSchemaName(String defaultSchemaName) {
		this.defaultSchemaName = defaultSchemaName;
	}

	/**
	 * Returns the name of the physically defined data type.
	 * 
	 * @param aTypeName the name of a real data type OR alias
	 * @return the name of the real data type
	 */
	protected String convertTypeNameToRealTypeName(String aTypeName) {
		return aTypeName;
	}
	}

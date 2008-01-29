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

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.digester.Digester;

import de.erdesignerng.dialect.config.ConfigDataType;
import de.erdesignerng.dialect.config.ConfigDataTypes;
import de.erdesignerng.exception.ElementInvalidNameException;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-01-29 22:04:11 $
 */
public abstract class Dialect {

    private boolean caseSensitive;

    private boolean spacesAllowedInObjectNames;

    private int maxObjectNameLength;

    private boolean nullablePrimaryKeyAllowed;

    private boolean supportsOnUpdate = true;

    private boolean supportsOnDelete = true;

    private NameCastType castType;

    private Map<String, DataType> dataTypes = new HashMap<String, DataType>();

    protected Dialect() {
    }

    protected void loadDatatypeKonfiguration(String aKonfigfilename) {

        System.out.println("Loading " + aKonfigfilename);

        Digester theDigester = new Digester();

        theDigester.addObjectCreate("datatypes", ConfigDataTypes.class);
        theDigester.addSetProperties("datatypes");
        theDigester.addObjectCreate("datatypes/datatype", ConfigDataType.class);
        theDigester.addSetProperties("datatypes/datatype");
        theDigester.addSetNext("datatypes/datatype", "addType");

        ConfigDataTypes theConfiguration = null;
        try {
            theConfiguration = (ConfigDataTypes) theDigester.parse(getClass().getResourceAsStream(
                    "/de/erdesignerng/" + aKonfigfilename));
        } catch (Exception e) {
            throw new RuntimeException("Cannot load configuration", e);
        }

        int counter = 0;

        for (ConfigDataType theType : theConfiguration.getTypes()) {
            registerType("TYP_" + counter++, theType.getName(), theType.getPattern());
        }
    }

    /**
     * @return the caseSensitive
     */
    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    /**
     * @param aCaseSensitive
     *            the caseSensitive to set
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
     * @param aMaxObjectNameLength
     *            the maxObjectNameLength to set
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
     * @param aSpacesAllowedInObjectNames
     *            the spacesAllowedInObjectNames to set
     */
    public void setSpacesAllowedInObjectNames(boolean aSpacesAllowedInObjectNames) {
        spacesAllowedInObjectNames = aSpacesAllowedInObjectNames;
    }

    /**
     * Check the name of an element and return the converted name.
     * 
     * @param aName
     *            the name
     * @return the converted name
     * @throws ElementInvalidNameException
     *             will be thrown if the name is invalid
     */
    public String checkName(String aName) throws ElementInvalidNameException {
        if ((aName == null) || ("".equals(aName))) {
            throw new ElementInvalidNameException("Element must have a name");
        }

        if (!spacesAllowedInObjectNames) {
            if (aName.indexOf(' ') > 0) {
                throw new ElementInvalidNameException("No spaces are allowed in an object name");
            }
        }

        if (aName.length() > maxObjectNameLength) {
            throw new ElementInvalidNameException("Object name to long : " + aName + " length is " + aName.length()
                    + " maximum is " + maxObjectNameLength);
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
     * @param aNullablePrimaryKeyAllowed
     *            the nullablePrimaryKeyAllowed to set
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
     * @param aCastType
     *            the castType to set
     */
    public void setCastType(NameCastType aCastType) {
        castType = aCastType;
    }

    /**
     * Get the reverse engineering strategy.
     * 
     * @return the reverse engineering strategy
     */
    public abstract ReverseEngineeringStrategy getReverseEngineeringStrategy();

    public abstract String getUniqueName();

    @Override
    public String toString() {
        return getUniqueName();
    }

    public abstract String getDriverClassName();

    public abstract String getDriverURLTemplate();

    /**
     * Create a connection to a database.
     * 
     * @param aClassLoader
     *            the classloader
     * @param aDriver
     *            the name of the driver
     * @param aUrl
     *            the url
     * @param aUser
     *            the user
     * @param aPassword
     *            the password
     * @return the connection
     * @throws ClassNotFoundException
     *             is thrown in case of an error
     * @throws InstantiationException
     *             is thrown in case of an error
     * @throws IllegalAccessException
     *             is thrown in case of an error
     * @throws SQLException
     *             is thrown in case of an error
     */
    public Connection createConnection(ClassLoader aClassLoader, String aDriver, String aUrl, String aUser,
            String aPassword) throws ClassNotFoundException, InstantiationException, IllegalAccessException,
            SQLException {
        Class theDriverClass = aClassLoader.loadClass(aDriver);
        Driver theDriver = (Driver) theDriverClass.newInstance();

        Properties theProperties = new Properties();
        theProperties.put("user", aUser);
        theProperties.put("password", aPassword);
        Connection theConnection = theDriver.connect(aUrl, theProperties);

        System.out.println("Product : " + theConnection.getMetaData().getDatabaseProductName());
        System.out.println("Version : " + theConnection.getMetaData().getDatabaseProductVersion());

        return theConnection;
    }

    public boolean supportsSchemaInformation() {
        return true;
    }

    /**
     * Get a defined data type.
     * 
     * @param aTypeName
     *            the type name
     * @return the data type or null if its not existant
     */
    public DataType getDataType(String aTypeName) {
        return dataTypes.get(aTypeName);
    }

    protected abstract DataType createDataTypeFor(String aId, String aTypeName, String aCreateParams);

    protected void registerType(String aId, String aTypename, String aPattern) {

        System.out.println(getClass().getSimpleName() + " -> Registering type " + aTypename + " with pattern "
                + aPattern);
        dataTypes.put(aTypename, createDataTypeFor(aId, aTypename, aPattern));
    }

    public abstract SQLGenerator createSQLGenerator();

    /**
     * @return the supportsOnDelete
     */
    public boolean isSupportsOnDelete() {
        return supportsOnDelete;
    }

    /**
     * @param supportsOnDelete
     *            the supportsOnDelete to set
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
     * @param supportsOnUpdate
     *            the supportsOnUpdate to set
     */
    public void setSupportsOnUpdate(boolean supportsOnUpdate) {
        this.supportsOnUpdate = supportsOnUpdate;
    }

    /**
     * Get a datatype by id.
     * 
     * @param aId the id of the datatype
     * @return the datatype or null if nothing was found
     */
    public DataType getDataTypeById(String aId) {
        for (DataType aType : dataTypes.values()) {
            if (aId.equals(aType.getId())) {
                return aType;
            }
        }
        return null;
    }
}

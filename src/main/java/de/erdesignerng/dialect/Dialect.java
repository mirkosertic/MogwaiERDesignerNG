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
import java.util.List;
import java.util.Properties;

import de.erdesignerng.exception.ElementInvalidNameException;
import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.Domain;
import de.erdesignerng.model.Index;
import de.erdesignerng.model.Relation;
import de.erdesignerng.model.Table;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-01-03 13:11:06 $
 */
public abstract class Dialect {

	private boolean caseSensitive;

	private boolean spacesAllowedInObjectNames;

	private int maxObjectNameLength;

	private boolean nullablePrimaryKeyAllowed;

	private NameCastType castType;

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
	public void setSpacesAllowedInObjectNames(
			boolean aSpacesAllowedInObjectNames) {
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
			throw new ElementInvalidNameException();
		}

		if (!spacesAllowedInObjectNames) {
			if (aName.indexOf(' ') > 0) {
				throw new ElementInvalidNameException();
			}
		}

		if (aName.length() > maxObjectNameLength) {
			throw new ElementInvalidNameException();
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
	 * @return
	 */
	public abstract JDBCReverseEngineeringStrategy getReverseEngineeringStrategy();

	public abstract List<String> createAlterAttributeSQL(Table aTable,
			String attributeName, Domain aDomain, boolean aNullable);

	public abstract List<String> createRenameAttributeSQL(Table aTable,
			Attribute aAttribute, String aNewName);

	public abstract List<String> createRenameRelationSQL(Relation aRelation,
			String aNewName);

	public abstract List<String> createRenameTableSQL(Table aTable,
			String aNewName);

	public abstract List<String> createRenameIndexSQL(Table aTable,
			Index index, String aNewName);

	public abstract List<String> createDropAttributeSQL(Attribute aAttribute);

	public abstract List<String> createDropRelationSQL(Relation aRelation);

	public abstract List<String> createDropIndexSQL(Index aIndex);

	public abstract List<String> createDropTableSQL(Table aTable);

	public abstract List<String> createAddTableSQL(Table aTable);

	public abstract List<String> createAddAttributeSQL(Attribute aAttribute);

	public abstract List<String> createAddIndexSQL(Index aAttribute);

	public abstract List<String> createAddRelationSQL(Relation aRelation);

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
	 * @param aDriver
	 * @param aUrl
	 * @param aUser
	 * @param aPassword
	 * @return
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws SQLException
	 */
	public Connection createConnection(ClassLoader aClassLoader,String aDriver,String aUrl,String aUser,String aPassword) throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {
		Class theDriverClass = aClassLoader.loadClass(aDriver);
		Driver theDriver = (Driver)theDriverClass.newInstance();
		
		Properties theProperties = new Properties();
		theProperties.put("user", aUser);
		theProperties.put("password", aPassword);
		return theDriver.connect(aUrl, theProperties);
	}
}

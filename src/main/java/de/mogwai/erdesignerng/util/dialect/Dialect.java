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
package de.mogwai.erdesignerng.util.dialect;

import de.mogwai.erdesignerng.exception.ElementInvalidNameException;
import de.mogwai.erdesignerng.model.NameCastType;
import de.mogwai.erdesignerng.reverseengineering.JDBCReverseEngineeringStrategy;

/**
 * Base class for model properties.
 * 
 * @author Mirko Sertic <mail@mirkosertic.de>
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
	 * @param caseSensitive
	 *            the caseSensitive to set
	 */
	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}

	/**
	 * @return the maxObjectNameLength
	 */
	public int getMaxObjectNameLength() {
		return maxObjectNameLength;
	}

	/**
	 * @param maxObjectNameLength
	 *            the maxObjectNameLength to set
	 */
	public void setMaxObjectNameLength(int maxObjectNameLength) {
		this.maxObjectNameLength = maxObjectNameLength;
	}

	/**
	 * @return the spacesAllowedInObjectNames
	 */
	public boolean isSpacesAllowedInObjectNames() {
		return spacesAllowedInObjectNames;
	}

	/**
	 * @param spacesAllowedInObjectNames
	 *            the spacesAllowedInObjectNames to set
	 */
	public void setSpacesAllowedInObjectNames(boolean spacesAllowedInObjectNames) {
		this.spacesAllowedInObjectNames = spacesAllowedInObjectNames;
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
	 * @param nullablePrimaryKeyAllowed
	 *            the nullablePrimaryKeyAllowed to set
	 */
	public void setNullablePrimaryKeyAllowed(boolean nullablePrimaryKeyAllowed) {
		this.nullablePrimaryKeyAllowed = nullablePrimaryKeyAllowed;
	}

	/**
	 * @return the castType
	 */
	public NameCastType getCastType() {
		return castType;
	}

	/**
	 * @param castType
	 *            the castType to set
	 */
	public void setCastType(NameCastType castType) {
		this.castType = castType;
	}

	/**
	 * Get the reverse engineering strategy.
	 * 
	 * @return
	 */
	public abstract JDBCReverseEngineeringStrategy getReverseEngineeringStrategy();
}

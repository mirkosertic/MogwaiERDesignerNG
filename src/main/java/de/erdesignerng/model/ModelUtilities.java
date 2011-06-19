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
package de.erdesignerng.model;

import de.erdesignerng.dialect.Dialect;
import de.erdesignerng.exception.ElementAlreadyExistsException;
import de.erdesignerng.exception.ElementInvalidNameException;

import java.util.UUID;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-06-15 16:59:31 $
 */
public final class ModelUtilities {

	private ModelUtilities() {
	}

	/**
	 * Check existence in a list.
	 *
	 * @param aVector  the list
	 * @param aName	the name
	 * @param aDialect the dialect
	 * @throws ElementAlreadyExistsException is thrown if name exists
	 */
	public static void checkExistence(ModelItemVector aVector, String aName, Dialect aDialect)
			throws ElementAlreadyExistsException {

		boolean theCaseSensitive = false;

		if (aDialect != null) {
			theCaseSensitive = aDialect.isCaseSensitive();
		}

		if (aVector.elementExists(aName, theCaseSensitive)) {
			throw new ElementAlreadyExistsException("Element '" + aName + "' already exists!");
		}

	}

	/**
	 * Check the name of an item and its existence in a list.
	 *
	 * @param aVector  the list
	 * @param aItem	the item
	 * @param aDialect the dialect
	 * @throws ElementInvalidNameException   is thrown in case of an error
	 * @throws ElementAlreadyExistsException is thrown in case of an error
	 */
	public static void checkNameAndExistence(ModelItemVector aVector, OwnedModelItem aItem, Dialect aDialect)
			throws ElementInvalidNameException, ElementAlreadyExistsException {

		if (aDialect != null) {
			aItem.setName(aDialect.checkName(aItem.getName()));
		}

		checkExistence(aVector, aItem.getUniqueName(), aDialect);
	}

	/**
	 * Create a unique system id.
	 *
	 * @return the newly created id
	 */
	public static String createSystemIdFor() {
		return UUID.randomUUID().toString();
	}
}

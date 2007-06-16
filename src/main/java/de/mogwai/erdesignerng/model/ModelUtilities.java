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
package de.mogwai.erdesignerng.model;

import de.mogwai.erdesignerng.exception.ElementAlreadyExistsException;
import de.mogwai.erdesignerng.exception.ElementInvalidNameException;
import de.mogwai.erdesignerng.util.dialect.Dialect;

/**
 * Helper functions for the model.
 * 
 * @author Mirko Sertic <mail@mirkosertic.de>
 */
public final class ModelUtilities {

	private ModelUtilities() {
	}

	/**
	 * Check existance in a list.
	 * 
	 * @param aVector
	 * @param aName
	 * @param aProperties
	 * @throws ElementAlreadyExistsException
	 */
	public static void checkExistance(ModelItemVector aVector, String aName,
			Dialect aProperties) throws ElementAlreadyExistsException {

		if (aVector.elementExists(aName, aProperties.isCaseSensitive())) {
			throw new ElementAlreadyExistsException(aName);
		}

	}

	/**
	 * Check the name of an item and its existance in a list.
	 * 
	 * @param aVector
	 * @param aItem
	 * @param aProperties
	 * @throws ElementInvalidNameException
	 * @throws ElementAlreadyExistsException
	 */
	public static void checkNameAndExistance(ModelItemVector aVector,
			OwnedModelItem aItem, Dialect aProperties)
			throws ElementInvalidNameException, ElementAlreadyExistsException {

		aItem.setName(aProperties.checkName(aItem.getName()));

		checkExistance(aVector, aItem.getName(), aProperties);
	}

	/**
	 * Find the model properties.
	 * 
	 * @param aItem
	 *            the item to start searching at
	 * @return the found model history.
	 */
	public static Dialect getModelProperties(OwnedModelItem aItem) {

		Object theOwner = aItem.getOwner();
		if (theOwner instanceof Model) {
			return ((Model) theOwner).getModelProperties();
		}

		if (theOwner instanceof OwnedModelItem) {
			return getModelProperties((OwnedModelItem) theOwner);
		}

		return null;
	}

	/**
	 * Find the model history.
	 * 
	 * @param aItem
	 *            the item to start searching at
	 * @return the found model history.
	 */
	public static ModelHistory getModelHistory(OwnedModelItem aItem) {

		Object theOwner = aItem.getOwner();
		if (theOwner instanceof Model) {
			return ((Model) theOwner).getModelHistory();
		}

		if (theOwner instanceof OwnedModelItem) {
			return getModelHistory((OwnedModelItem) theOwner);
		}

		return null;
	}

	/**
	 * Create a unique system id.
	 * 
	 * @param aClass
	 * @return
	 */
	public static String createSystemIdFor(Class aClass) {
		return aClass.getName() + "_" + System.currentTimeMillis() + "_"
				+ (int) (Math.random() * 10000000);
	}
}

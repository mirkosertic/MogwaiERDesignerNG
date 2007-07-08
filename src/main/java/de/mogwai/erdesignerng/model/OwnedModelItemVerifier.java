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

import de.mogwai.erdesignerng.exception.CannotDeleteException;
import de.mogwai.erdesignerng.exception.ElementAlreadyExistsException;
import de.mogwai.erdesignerng.exception.ElementInvalidNameException;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2007-07-08 10:06:31 $
 */
public interface OwnedModelItemVerifier {

	/**
	 * Check if a name is already existant.
	 * 
	 * @param aSender
	 * @param aName
	 */
	void checkNameAlreadyExists(ModelItem aSender, String aName)
			throws ElementAlreadyExistsException;

	/**
	 * Delete an element.
	 * 
	 * @param aSender
	 *            the element to delete
	 * @throws CannotDeleteException
	 */
	void delete(ModelItem aSender) throws CannotDeleteException;

	/**
	 * Check the name.
	 * 
	 * @param aName
	 * @return
	 * @throws ElementInvalidNameException
	 */
	String checkName(String aName) throws ElementInvalidNameException;
}

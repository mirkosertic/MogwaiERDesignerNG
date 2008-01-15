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

import de.erdesignerng.exception.CannotDeleteException;
import de.erdesignerng.exception.ElementAlreadyExistsException;
import de.erdesignerng.exception.ElementInvalidNameException;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-01-15 19:22:43 $
 */
public interface OwnedModelItemVerifier {

    /**
     * Check if a name is already existant.
     * 
     * @param aSender the sender
     * @param aName dhe name
     * @throws ElementAlreadyExistsException in case of an error
     */
    void checkNameAlreadyExists(ModelItem aSender, String aName) throws ElementAlreadyExistsException;

    /**
     * Delete an element.
     * 
     * @param aSender
     *            the element to delete
     * @throws CannotDeleteException in case of an error
     */
    void delete(ModelItem aSender) throws CannotDeleteException;

    /**
     * Check the name.
     * 
     * @param aName the name
     * @return the checked name
     * @throws ElementInvalidNameException in case of an error
     */
    String checkName(String aName) throws ElementInvalidNameException;
}

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

import java.util.Vector;

/**
 * A List of ModelItems.
 * 
 * @author Mirko Sertic <mail@mirkosertic.de>
 */
public class ModelItemVector<T extends ModelItem> extends Vector<T> {

	private static final long serialVersionUID = 5030067810497396582L;

	/**
	 * Check if a named element already exists in this list.
	 * 
	 * @param aName
	 *            the name of the element
	 * @param aCaseSensitive
	 * 
	 * @return true if it exists, else false.
	 */
	public boolean elementExists(String aName, boolean aCaseSensitive) {
		for (T theElement : this) {
			if (aCaseSensitive) {
				if (aName.equals(theElement.getName())) {
					return true;
				}
			} else {
				if (aName.toLowerCase().equals(
						theElement.getName().toLowerCase())) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Find a attribute by a given name.
	 * 
	 * @param aName
	 *            the name
	 * @return the found element
	 */
	public T findByName(String aName) {
		for (T theElement : this) {
			if (aName.equals(theElement.getName())) {
				return theElement;
			}
		}
		return null;
	}

}

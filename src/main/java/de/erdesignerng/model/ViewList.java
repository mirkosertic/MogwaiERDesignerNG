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

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-09 19:07:29 $
 */
public class ViewList extends ModelItemVector<View> {

	private static final long serialVersionUID = 7291908371933857720L;

	/**
	 * Find a table by name and schema.
	 * 
	 * @param aName
	 *			the name of the table
	 * @param aSchemaName
	 *			the schema of the table
	 * @return the table or null if nothing was found
	 */
	public View findByNameAndSchema(String aName, String aSchemaName) {
		for (View theElement : this) {
			if (aName.equals(theElement.getName()) && aSchemaName.equals(theElement.getSchema())) {
				return theElement;
			}
		}
		return null;
	}
}
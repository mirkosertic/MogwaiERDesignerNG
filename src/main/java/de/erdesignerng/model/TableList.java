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
 * @version $Date: 2008-02-01 21:05:35 $
 */
public class TableList extends ModelItemVector<Table> {

	private static final long serialVersionUID = 7291908371933857720L;

	/**
	 * Find an attribute by a given system id.
	 * 
	 * @param aSystemId
	 *			the system id
	 * @return the attribute or null if nothing was found
	 */
	public Attribute<Table> findAttributeBySystemId(String aSystemId) {
		for (Table theTable : this) {
			Attribute<Table> theAttribute = theTable.getAttributes().findBySystemId(aSystemId);
			if (theAttribute != null) {
				return theAttribute;
			}
		}
		return null;
	}

	/**
	 * Test if the domain is in use by a table.
	 * 
	 * @param aDomain
	 *			the domain
	 * @return the using table or null if the domain is not in use
	 */
	public Table checkIfUsedByTable(Domain aDomain) {
		for (Table theTable : this) {
			if (theTable.getAttributes().isDomainInUse(aDomain)) {
				return theTable;
			}
		}
		return null;
	}

	/**
	 * Find a table by name and schema.
	 * 
	 * @param aName
	 *			the name of the table
	 * @param aSchemaName
	 *			the schema of the table
	 * @return the table or null if nothing was found
	 */
	public Table findByNameAndSchema(String aName, String aSchemaName) {
		for (Table theElement : this) {
			if (aName.equals(theElement.getName()) && aSchemaName.equals(theElement.getSchema())) {
				return theElement;
			}
		}
		return null;
	}

	/**
	 * Check if a type is used somewhere.
	 * 
	 * @param aType the type
	 * @return the table where it is used or null of it is nowhere used
	 */
	public Table checkIfUsedByTable(CustomType aType) {
		return null;
	}
}
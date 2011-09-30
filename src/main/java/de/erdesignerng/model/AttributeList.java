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

import de.erdesignerng.dialect.DataType;

/**
 * A list of attributes.
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2008-01-15 19:22:43 $
 */
public class AttributeList<T extends ModelItem> extends ModelItemVector<Attribute<T>> {

	private static final long serialVersionUID = 890361971577085178L;

	/**
	 * Test if a domain is in use.
	 * 
	 * @param aDomain
	 *			the domain
	 * @return true if yes, else false
	 */
	public boolean isDomainInUse(Domain aDomain) {
		for (Attribute<T> theAttribute : this) {
			DataType theType = theAttribute.getDatatype();
			if (theType.isDomain()) {
				if (aDomain.getSystemId().equals(((Domain) theType).getSystemId())) {
					return true;
				}
			}
		}
		return false;
	}
}
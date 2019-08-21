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

import java.util.Map;

/**
 * A list of datatypes.
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2008-06-15 17:53:55 $
 */
public class DataTypeListWithAlias extends DataTypeList {

	private final Dialect dialect;

	public DataTypeListWithAlias(final Dialect dialect) {
		this.dialect = dialect;
	}

	/**
	 * Find a datatype by its name.
	 * 
	 * @param aName
	 *			the name of the datatype
	 * @return the datatype or null if nothing was found
	 */
	public DataType findByName(final String aName) {
		for (final DataType theType : this) {
			if (theType.getName().equalsIgnoreCase(aName)) {
				return theType;
			}
		}
		if (dialect != null) {
			for (final Map.Entry<String, String> entry : dialect.getDataTypeAliases().entrySet()) {
				if (entry.getKey().equalsIgnoreCase(aName)) {
					return findByName(entry.getValue());
				}
			}
		}
		return null;
	}
}
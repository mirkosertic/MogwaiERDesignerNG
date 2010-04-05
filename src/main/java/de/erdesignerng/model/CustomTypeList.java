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

import java.util.Vector;

/**
 * @author $Author: dr-death $
 * @version $Date: 2010-03-30 20:00:00 $
 */
public class CustomTypeList extends Vector<CustomType> implements ModelList<CustomType> {

    /**
     * Find a custom datatype by system id.
     *
     * @param aSystemId
     *            the system id
     * @return the custom datatype or null if custom datatype does not exist
     */
    public CustomType findBySystemId(String aSystemId) {
        for (CustomType theCustomType : this) {
            if (aSystemId.equals(theCustomType.getSystemId())) {
                return theCustomType;
            }
        }

        return null;
    }

    /**
     * Find a custom datatype by a given name.
     *
     * @param aName
     *            the name
     * @return the custom datatype or null if custom datatype does not exist
     */
    public CustomType findByName(String aName) {
        for (CustomType theCustomType : this) {
            if (aName.equals(theCustomType.getName())) {
                return theCustomType;
            }
        }

        return null;
    }

}

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
package de.erdesignerng.model.serializer;

import de.erdesignerng.model.Domain;

/**
 * @author $Author: dr-death2 $
 * @version $Date: 2009-10-25 02:50:00 $
 */
public abstract class AbstractXMLDomainSerializer extends CommonAbstractXMLSerializer<Domain> {

	protected static final String DOMAIN = "Domain";

	protected static final String FRACTION = "fraction";

	protected static final String SCALE = "scale";

	protected static final String SIZE = "size";

	protected static final String NULLABLE = "nullable";

    protected String safeString(Object aValue) {
        if (aValue == null) {
            return "";
        }
        return aValue.toString();
    }

    protected Integer safeInteger(String aValue) {
        if (aValue == null) {
            return null;
        }
        if (aValue.length() == 0) {
            return null;
        }
        // In older models this might be the case as there was a bug in data serialization
        if ("null".equals(aValue)) {
            return null;
        }
        return Integer.parseInt(aValue);
    }
}
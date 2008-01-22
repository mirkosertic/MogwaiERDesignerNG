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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.Domain;

public class AttributeSerializer extends Serializer {

    public static final String ATTRIBUTE = "Attribute";

    public static final String DOMAINREFID = "domainrefid";

    public static final String NULLABLE = "nullable";

    public static final String PRIMARYKEY = "primarykey";

    public static final String DEFAULTVALUEREFID = "defaultvaluerefid";

    public void serialize(Attribute aAttribute, Document aDocument, Element aRootElement) {
        
        Element theAttributeElement = addElement(aDocument, aRootElement, ATTRIBUTE);

        // Basisdaten des Modelelementes speichern
        serializeProperties(aDocument, theAttributeElement, aAttribute);

        // Domain usw
        Domain theDomain = aAttribute.getDomain();
        theAttributeElement.setAttribute(DOMAINREFID, theDomain.getSystemId());

        setBooleanAttribute(theAttributeElement, NULLABLE, aAttribute.isNullable());

        setBooleanAttribute(theAttributeElement, PRIMARYKEY, aAttribute.isPrimaryKey());

        if (aAttribute.getDefaultValue() != null) {
            theAttributeElement.setAttribute(DEFAULTVALUEREFID, aAttribute.getDefaultValue().getSystemId());
        }

        serializeCommentElement(aDocument, theAttributeElement, aAttribute);
    }
}

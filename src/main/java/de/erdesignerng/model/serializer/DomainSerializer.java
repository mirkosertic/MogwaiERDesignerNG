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

import de.erdesignerng.model.Domain;

public class DomainSerializer extends Serializer {

    public static final String DOMAIN = "Domain";

    public static final String SIZE = "size";

    public static final String FRACTION = "fraction";

    public static final String RADIX = "radix";

    public static final String SEQUENCED = "sequenced";

    public static final String JAVA_CLASS_NAME = "javaclassname";

    public void serialize(Domain aDomain, Document aDocument, Element aRootElement) {
        Element theDomainElement = addElement(aDocument, aRootElement, DOMAIN);

        // Basisdaten des Modelelementes speichern
        serializeProperties(aDocument, theDomainElement, aDomain);

        // Zusatzdaten
        theDomainElement.setAttribute(DATATYPE, aDomain.getDatatype());
        theDomainElement.setAttribute(JAVA_CLASS_NAME, aDomain.getJavaClassName());
        theDomainElement.setAttribute(SIZE, "" + aDomain.getDomainSize());
        theDomainElement.setAttribute(FRACTION, "" + aDomain.getFraction());
        theDomainElement.setAttribute(RADIX, "" + aDomain.getRadix());
        setBooleanAttribute(theDomainElement, SEQUENCED, aDomain.isSequenced());

    }
}

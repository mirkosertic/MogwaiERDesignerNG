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
package de.erdesignerng.io.serializer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.erdesignerng.model.Domain;
import de.erdesignerng.model.Model;

public class DomainSerializer extends Serializer {
    
    public static final DomainSerializer SERIALIZER = new DomainSerializer();

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
        theDomainElement.setAttribute(DATATYPE, aDomain.getDatatype().getId());
        theDomainElement.setAttribute(JAVA_CLASS_NAME, aDomain.getJavaClassName());
        theDomainElement.setAttribute(SIZE, "" + aDomain.getDomainSize());
        theDomainElement.setAttribute(FRACTION, "" + aDomain.getFraction());
        theDomainElement.setAttribute(RADIX, "" + aDomain.getRadix());
        setBooleanAttribute(theDomainElement, SEQUENCED, aDomain.isSequenced());

    }

    public void deserializeFrom(Model aModel, Document aDocument) {
        
        // First of all, parse the domains
        NodeList theElements = aDocument.getElementsByTagName(DOMAIN);
        for (int i = 0; i < theElements.getLength(); i++) {
            Element theElement = (Element) theElements.item(i);

            Domain theDomain = new Domain();
            theDomain.setOwner(aModel);
            deserializeProperties(theElement, theDomain);

            theDomain.setDatatype(aModel.getDialect().getDataTypeById(theElement.getAttribute(DATATYPE)));
            theDomain.setJavaClassName(theElement.getAttribute(JAVA_CLASS_NAME));
            theDomain.setSequenced(TRUE.equals(theElement.getAttribute(SEQUENCED)));
            theDomain.setDomainSize(Integer.parseInt(theElement.getAttribute(SIZE)));
            theDomain.setFraction(Integer.parseInt(theElement.getAttribute(FRACTION)));
            theDomain.setRadix(Integer.parseInt(theElement.getAttribute(RADIX)));

            aModel.getDomains().add(theDomain);
        }        
    }
}

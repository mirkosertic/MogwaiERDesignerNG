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
package de.erdesignerng.model.serializer.xml10;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.erdesignerng.model.Domain;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.serializer.AbstractXMLDomainSerializer;

public class XMLDomainSerializer extends AbstractXMLDomainSerializer {

    @Override
    public void serialize(Domain aDomain, Document aDocument, Element aRootElement) {
        Element theDomainElement = addElement(aDocument, aRootElement, DOMAIN);

        // Basisdaten des Modelelementes speichern
        theDomainElement.setAttribute(ID, aDomain.getSystemId());
        theDomainElement.setAttribute(NAME, aDomain.getName());

        theDomainElement.setAttribute(DATATYPE, aDomain.getConcreteType().getName());
        theDomainElement.setAttribute(SIZE, "" + aDomain.getSize());
        theDomainElement.setAttribute(FRACTION, "" + aDomain.getFraction());
        theDomainElement.setAttribute(SCALE, "" + aDomain.getScale());
    }

    @Override
    public void deserialize(Model aModel, Document aDocument) {
        NodeList theElements = aDocument.getElementsByTagName(DOMAIN);
        for (int i = 0; i < theElements.getLength(); i++) {
            Element theElement = (Element) theElements.item(i);

            Domain theDomain = new Domain();
            theDomain.setSystemId(theElement.getAttribute(ID));
            theDomain.setName(theElement.getAttribute(NAME));
            theDomain.setConcreteType(aModel.getDomainDataTypes().findByName(theElement.getAttribute(DATATYPE)));
            theDomain.setSize(Integer.parseInt(theElement.getAttribute(SIZE)));
            theDomain.setFraction(Integer.parseInt(theElement.getAttribute(FRACTION)));
            theDomain.setScale(Integer.parseInt(theElement.getAttribute(SCALE)));

            aModel.getDomains().add(theDomain);
        }

    }
}
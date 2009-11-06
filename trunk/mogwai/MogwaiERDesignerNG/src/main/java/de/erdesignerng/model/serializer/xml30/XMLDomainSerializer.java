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
package de.erdesignerng.model.serializer.xml30;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.erdesignerng.model.Domain;
import de.erdesignerng.model.Model;

/**
 * @author $Author: dr-death $
 * @version $Date: 2009-11-06 17:15:00 $
 */
public class XMLDomainSerializer extends de.erdesignerng.model.serializer.xml10.XMLDomainSerializer {

	protected static final String NULLABLE = "nullable";

    @Override
    public void serialize(Domain aDomain, Document aDocument, Element aRootElement) {
        Element theDomainElement = addElement(aDocument, aRootElement, DOMAIN);

        // Basisdaten des Modelelementes speichern
        theDomainElement.setAttribute(ID, aDomain.getSystemId());
        theDomainElement.setAttribute(NAME, aDomain.getName());

        theDomainElement.setAttribute(DATATYPE, aDomain.getConcreteType().getName());

        //Bug Fixing 2876916 [ERDesignerNG] Reverse-Eng. PgSQL VARCHAR max-length wrong
        theDomainElement.setAttribute(SIZE, "" + ((aDomain.getSize() != null)?aDomain.getSize():""));

        theDomainElement.setAttribute(FRACTION, "" + aDomain.getFraction());
        theDomainElement.setAttribute(SCALE, "" + aDomain.getScale());
		theDomainElement.setAttribute(NULLABLE, "" + aDomain.isNullable());
    }

    @Override
    public void deserialize(Model aModel, Document aDocument) {
        // Now, parse tables
        NodeList theElements = aDocument.getElementsByTagName(DOMAIN);
        for (int i = 0; i < theElements.getLength(); i++) {
            Element theDomainElement = (Element) theElements.item(i);

            Domain theDomain = new Domain();
            theDomain.setSystemId(theDomainElement.getAttribute(ID));
            theDomain.setName(theDomainElement.getAttribute(NAME));
            theDomain.setConcreteType(aModel.getDomainDataTypes().findByName(theDomainElement.getAttribute(DATATYPE)));

            // Bug Fixing 2876916 [ERDesignerNG] Reverse-Eng. PgSQL VARCHAR max-length wrong
            String theAttributeString = theDomainElement.getAttribute(SIZE);
            theDomain.setSize((StringUtils.isEmpty(theAttributeString) || ("null".equals(theAttributeString)))?null:Integer.parseInt(theAttributeString));

            theDomain.setFraction(Integer.parseInt(theDomainElement.getAttribute(FRACTION)));
            theDomain.setScale(Integer.parseInt(theDomainElement.getAttribute(SCALE)));

            String theNullable = theDomainElement.getAttribute(NULLABLE);
            if (!StringUtils.isEmpty(theNullable)) {
                theDomain.setNullable(Boolean.parseBoolean(theNullable));
            }

            aModel.getDomains().add(theDomain);
        }
    }

}

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
package de.erdesignerng.model.serializer.xml50;

import de.erdesignerng.model.Domain;
import de.erdesignerng.model.Model;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author $Author: dr-death2 $
 * @version $Date: 2009-11-06 17:15:00 $
 */
public class XMLDomainSerializer extends de.erdesignerng.model.serializer.xml30.XMLDomainSerializer {

	@Override
	public void serialize(Domain aDomain, Document aDocument, Element aRootElement) {
		Element theDomainElement = addElement(aDocument, aRootElement, DOMAIN);

		// Basisdaten des Modelelementes speichern
		serializeProperties(aDocument, theDomainElement, aDomain);
		serializeCommentElement(aDocument, theDomainElement, aDomain);

		theDomainElement.setAttribute(DATATYPE, aDomain.getConcreteType().getName());

		// Bug Fixing 2876916 [ERDesignerNG] Reverse-Eng. PgSQL VARCHAR max-length wrong
		theDomainElement.setAttribute(SIZE, safeString(aDomain.getSize()));
		theDomainElement.setAttribute(FRACTION, safeString(aDomain.getFraction()));
		theDomainElement.setAttribute(SCALE, safeString(aDomain.getScale()));
		theDomainElement.setAttribute(NULLABLE, safeString(aDomain.isNullable()));
	}

	@Override
	public void deserialize(Model aModel, Document aDocument) {
		NodeList theElements = aDocument.getElementsByTagName(DOMAIN);
		for (int i = 0; i < theElements.getLength(); i++) {
			Element theDomainElement = (Element) theElements.item(i);

			Domain theDomain = new Domain();

			deserializeProperties(theDomainElement, theDomain);
			deserializeCommentElement(theDomainElement, theDomain);

			theDomain.setConcreteType(aModel.getDomainDataTypes().findByName(theDomainElement.getAttribute(DATATYPE)));

			// Bug Fixing 2876916 [ERDesignerNG] Reverse-Eng. PgSQL VARCHAR max-length wrong
			theDomain.setSize(safeInteger(theDomainElement.getAttribute(SIZE)));
			theDomain.setFraction(safeInteger(theDomainElement.getAttribute(FRACTION)));
			theDomain.setScale(safeInteger(theDomainElement.getAttribute(SCALE)));

			String theNullable = theDomainElement.getAttribute(NULLABLE);
			if (!StringUtils.isEmpty(theNullable)) {
				theDomain.setNullable(Boolean.parseBoolean(theNullable));
			}

			aModel.getDomains().add(theDomain);
		}
	}

}

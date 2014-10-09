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

import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.CustomType;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.ModelItem;
import de.erdesignerng.model.Table;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XMLAttributeSerializer extends de.erdesignerng.model.serializer.xml10.XMLAttributeSerializer {

	@Override
	public void serialize(Attribute aAttribute, Document aDocument, Element aRootElement) {

		Element theAttributeElement = addElement(aDocument, aRootElement, ATTRIBUTE);

		// Basisdaten des Modelelementes speichern
		serializeProperties(aDocument, theAttributeElement, aAttribute);

		theAttributeElement.setAttribute(DATATYPE, "" + ((aAttribute.getDatatype() != null) ? aAttribute.getDatatype().getName() : ""));

		// Bug Fixing 2876916 [ERDesignerNG] Reverse-Eng. PgSQL VARCHAR max-length wrong
		theAttributeElement.setAttribute(SIZE, "" + ((aAttribute.getSize() != null) ? aAttribute.getSize() : ""));

		theAttributeElement.setAttribute(FRACTION, "" + aAttribute.getFraction());
		theAttributeElement.setAttribute(SCALE, "" + aAttribute.getScale());
		theAttributeElement.setAttribute(DEFAULTVALUE, aAttribute.getDefaultValue());
		theAttributeElement.setAttribute(EXTRA, aAttribute.getExtra());

		setBooleanAttribute(theAttributeElement, NULLABLE, aAttribute.isNullable());

		serializeCommentElement(aDocument, theAttributeElement, aAttribute);
	}

	@Override
	public void deserialize(Model aModel, ModelItem aTableOrCustomType, Element aElement) {
		// Parse the Attributes
		NodeList theAttributes = aElement.getElementsByTagName(ATTRIBUTE);
		for (int j = 0; j < theAttributes.getLength(); j++) {
			Element theAttributeElement = (Element) theAttributes.item(j);
			boolean isCustomType = "CustomType".equalsIgnoreCase(theAttributeElement.getParentNode().getNodeName());
			boolean isTable = "Table".equalsIgnoreCase(theAttributeElement.getParentNode().getNodeName());

			Attribute theAttribute = null;

			if (isTable) {
				theAttribute = new Attribute<Table>();
				theAttribute.setOwner(aTableOrCustomType);
			} else if(isCustomType) {
				theAttribute = new Attribute<CustomType>();
				theAttribute.setOwner(aTableOrCustomType);
			}

			deserializeProperties(theAttributeElement, theAttribute);
			deserializeCommentElement(theAttributeElement, theAttribute);

			String theDatatypeName = theAttributeElement.getAttribute(DATATYPE);
			theAttribute.setDatatype((StringUtils.isEmpty(theDatatypeName)? null : aModel.getAvailableDataTypes().findByName(theDatatypeName)));
			theAttribute.setDefaultValue(theAttributeElement.getAttribute(DEFAULTVALUE));

			// Bug Fixing 2876916 [ERDesignerNG] Reverse-Eng. PgSQL VARCHAR max-length wrong
			String theAttributeString = theAttributeElement.getAttribute(SIZE);
			theAttribute.setSize((StringUtils.isEmpty(theAttributeString) || ("null".equals(theAttributeString))) ? null : Integer.parseInt(theAttributeString));

			String theFraction = theAttributeElement.getAttribute(FRACTION);
			if (!StringUtils.isEmpty(theFraction) && !"null".equals(theFraction)) {
				theAttribute.setFraction(Integer.parseInt(theFraction));
			}
			theAttribute.setScale(Integer.parseInt(theAttributeElement.getAttribute(SCALE)));
			theAttribute.setNullable(TRUE.equals(theAttributeElement.getAttribute(NULLABLE)));
			theAttribute.setExtra(theAttributeElement.getAttribute(EXTRA));

			if (isTable) {
				((Table)aTableOrCustomType).getAttributes().add(theAttribute);
			} else if(isCustomType) {
				((CustomType)aTableOrCustomType).getAttributes().add(theAttribute);
			}
		}
	}
}
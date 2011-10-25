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

import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.CustomType;
import de.erdesignerng.model.CustomTypeType;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.serializer.AbstractXMLModelSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author $Author: dr-death2 $
 * @version $Date: 2011-10-08 20:00:00 $
 */
public class XMLCustomTypeSerializer extends de.erdesignerng.model.serializer.xml40.XMLCustomTypeSerializer {

	public XMLCustomTypeSerializer(AbstractXMLModelSerializer xmlModelSerializer) {
		super(xmlModelSerializer);
	}

	@Override
	public void serialize(CustomType aCustomType, Document aDocument, Element aRootElement) {
		Element theCustomTypeElement = addElement(aDocument, aRootElement, CUSTOMTYPE);

		// Basisdaten (ID, NAME) des Modelelementes speichern
		serializeProperties(aDocument, theCustomTypeElement, aCustomType);
		serializeCommentElement(aDocument, theCustomTypeElement, aCustomType);

		theCustomTypeElement.setAttribute(SCHEMA, aCustomType.getSchema());
		theCustomTypeElement.setAttribute(TYPE, aCustomType.getType().toString());
		theCustomTypeElement.setAttribute(ALIAS, aCustomType.getAlias());

		// Attribute serialisieren
		for (Attribute<CustomType> theAttribute : aCustomType.getAttributes()) {
			getXMLModelSerializer().getXMLAttributeSerializer().serialize(theAttribute, aDocument, theCustomTypeElement);
		}
	}

	@Override
	public void deserialize(Model aModel, Document aDocument) {
		NodeList theElements = aDocument.getElementsByTagName(CUSTOMTYPE);

		for (int i = 0; i < theElements.getLength(); i++) {

			Element theElement = (Element) theElements.item(i);

			CustomType theCustomType = new CustomType();
			theCustomType.setOwner(aModel);

			deserializeProperties(theElement, theCustomType);
			deserializeCommentElement(theElement, theCustomType);

			theCustomType.setSchema(theElement.getAttribute(SCHEMA));
			theCustomType.setType(CustomTypeType.fromString(theElement.getAttribute(TYPE)));
			theCustomType.setSchema(theElement.getAttribute(ALIAS));

			getXMLModelSerializer().getXMLAttributeSerializer().deserialize(aModel, theCustomType, theElement);

			aModel.getCustomTypes().add(theCustomType);
		}
	}
}
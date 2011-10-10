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
package de.erdesignerng.model.serializer.xml40;

import de.erdesignerng.model.CustomType;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.serializer.AbstractXMLCustomTypeSerializer;
import de.erdesignerng.model.serializer.AbstractXMLModelSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author $Author: dr-death2 $
 * @version $Date: 2010-04-04 01:15:00 $
 */
public class XMLCustomTypeSerializer extends AbstractXMLCustomTypeSerializer {

	public XMLCustomTypeSerializer(AbstractXMLModelSerializer xmlModelSerializer) {
		super(xmlModelSerializer);
	}

	@Override
	public void serialize(CustomType aCustomType, Document aDocument, Element aRootElement) {
		Element theCustomTypeElement = addElement(aDocument, aRootElement, CUSTOMTYPE);

		serializeProperties(aDocument, theCustomTypeElement, aCustomType);
		serializeCommentElement(aDocument, theCustomTypeElement, aCustomType);

		theCustomTypeElement.setAttribute(SCHEMA, aCustomType.getSchema());
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

			aModel.getCustomTypes().add(theCustomType);
		}
	}
}
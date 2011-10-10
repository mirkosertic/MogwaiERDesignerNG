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
package de.erdesignerng.model.serializer.xml20;

import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.Index;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.Table;
import de.erdesignerng.model.serializer.AbstractXMLModelSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XMLTableSerializer extends de.erdesignerng.model.serializer.xml10.XMLTableSerializer {

	public XMLTableSerializer(AbstractXMLModelSerializer xmlModelSerializer) {
		super(xmlModelSerializer);
	}

	@Override
	public void serialize(Table aTable, Document aDocument, Element aRootElement) {
		Element theTableElement = addElement(aDocument, aRootElement, TABLE);

		// Basisdaten des Modelelementes speichern
		serializeProperties(aDocument, theTableElement, aTable);
		serializeCommentElement(aDocument, theTableElement, aTable);

		theTableElement.setAttribute(SCHEMA, aTable.getSchema());

		// Attribute serialisieren
		for (Attribute<Table> theAttribute : aTable.getAttributes()) {
			getXMLModelSerializer().getXMLAttributeSerializer().serialize(theAttribute, aDocument, theTableElement);
		}

		// Indexes serialisieren
		for (Index theIndex : aTable.getIndexes()) {
			getXMLModelSerializer().getXMLIndexSerializer().serialize(theIndex, aDocument, theTableElement);
		}
	}

	@Override
	public void deserialize(Model aModel, Document aDocument) {
		// Now, parse tables
		NodeList theElements = aDocument.getElementsByTagName(TABLE);
		for (int i = 0; i < theElements.getLength(); i++) {
			Element theElement = (Element) theElements.item(i);

			Table theTable = new Table();
			theTable.setOwner(aModel);

			deserializeProperties(theElement, theTable);
			deserializeCommentElement(theElement, theTable);

			theTable.setSchema(theElement.getAttribute(SCHEMA));

			getXMLModelSerializer().getXMLAttributeSerializer().deserialize(aModel, theTable, theElement);
			getXMLModelSerializer().getXMLIndexSerializer().deserialize(theTable, theElement);

			aModel.getTables().add(theTable);
		}

	}

}
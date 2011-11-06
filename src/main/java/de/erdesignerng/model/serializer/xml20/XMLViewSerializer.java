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

import de.erdesignerng.model.Model;
import de.erdesignerng.model.View;
import de.erdesignerng.model.serializer.AbstractXMLViewSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XMLViewSerializer extends AbstractXMLViewSerializer {

	@Override
	public void serialize(View aView, Document aDocument, Element aRootElement) {
		Element theRelationElement = addElement(aDocument, aRootElement, VIEW);

		serializeProperties(aDocument, theRelationElement, aView);
		serializeCommentElement(aDocument, theRelationElement, aView);

		Element theSQLElement = addElement(aDocument, theRelationElement, SQL);
		theSQLElement.appendChild(aDocument.createTextNode(aView.getSql()));
	}

	@Override
	public void deserialize(Model aModel, Document aDocument) {

		NodeList theElements = aDocument.getElementsByTagName(VIEW);
		for (int i = 0; i < theElements.getLength(); i++) {
			Element theElement = (Element) theElements.item(i);

			View theView = new View();
			theView.setOwner(aModel);
			deserializeProperties(theElement, theView);
			deserializeCommentElement(theElement, theView);

			NodeList theSQL = theElement.getElementsByTagName(SQL);
			for (int j = 0; j < theSQL.getLength(); j++) {
				Element theSQLElement = (Element) theSQL.item(j);
				theView.setSql(theSQLElement.getChildNodes().item(0).getNodeValue());
			}

			aModel.getViews().add(theView);
		}
	}
}
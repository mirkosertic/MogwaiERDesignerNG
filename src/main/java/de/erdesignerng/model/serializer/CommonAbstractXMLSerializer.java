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

import de.erdesignerng.model.Model;
import de.erdesignerng.model.ModelItem;
import de.erdesignerng.model.OwnedModelItem;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class CommonAbstractXMLSerializer<T extends OwnedModelItem> implements CommonXMLElementsAndAttributes {

	protected Element addElement(final Document aDocument, final Node aNode, final String aElementName) {
		final Element theElement = aDocument.createElement(aElementName);
		aNode.appendChild(theElement);
		return theElement;
	}

	protected void setBooleanAttribute(final Element aElement, final String aAttributeName, final boolean aValue) {
		aElement.setAttribute(aAttributeName, aValue ? TRUE : FALSE);
	}

	protected void serializeProperties(final Document aDocument, final Element aNode, final ModelItem aItem) {

		aNode.setAttribute(ID, aItem.getSystemId());
		aNode.setAttribute(NAME, aItem.getName());

		for (final String theKey : aItem.getProperties().getProperties().keySet()) {
			final String theValue = aItem.getProperties().getProperties().get(theKey);
			if (theValue != null) {
				final Element theProperty = addElement(aDocument, aNode, PROPERTY);
				theProperty.setAttribute(NAME, theKey);
				theProperty.setAttribute(VALUE, theValue);
			}
		}
	}

	protected void serializeCommentElement(final Document aDocument, final Element aElement, final ModelItem aItem) {
		if (!StringUtils.isEmpty(aItem.getComment())) {
			final Element theCommentElement = aDocument.createElement(COMMENT);
			theCommentElement.appendChild(aDocument.createTextNode(aItem.getComment()));

			aElement.appendChild(theCommentElement);
		}
	}

	protected void deserializeCommentElement(final Element aElement, final ModelItem aItem) {
		final NodeList theChildren = aElement.getChildNodes();
		for (int i = 0; i < theChildren.getLength(); i++) {
			final Node theChild = theChildren.item(i);
			if (COMMENT.equals(theChild.getNodeName())) {
				final Element theElement = (Element) theChild;
				if (theElement.getChildNodes().getLength() > 0) {
					aItem.setComment(theElement.getChildNodes().item(0).getNodeValue());
				}
			}
		}
	}

	protected void deserializeProperties(final Element aElement, final ModelItem aModelItem) {

		aModelItem.setSystemId(aElement.getAttribute(ID));
		aModelItem.setName(aElement.getAttribute(NAME));

		final NodeList theProperties = aElement.getElementsByTagName(PROPERTY);
		for (int i = 0; i < theProperties.getLength(); i++) {
			final Element theElement = (Element) theProperties.item(i);

			aModelItem.getProperties().setProperty(theElement.getAttribute(NAME), theElement.getAttribute(VALUE));
		}
	}

	public abstract void serialize(T aModelItem, Document aDocument, Element aRootElement);

	public abstract void deserialize(Model aModel, Document aDocument);
}

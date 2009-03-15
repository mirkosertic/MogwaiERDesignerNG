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
package de.erdesignerng.model.serializer.xml17;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.erdesignerng.model.ModelItem;

public class XMLSerializer {

    public static final String ID = "id";

    public static final String NAME = "name";

    public static final String VALUE = "value";

    public static final String PROPERTY = "Property";

    public static final String COMMENT = "Comment";

    public static final String TRUE = "true";

    public static final String FALSE = "false";
    
    public static final String ATTRIBUTEREFID = "attributerefid";
    
    public static final String ATTRIBUTEEXPRESSION = "expression";
    
    public static final String DATATYPE = "datatype";

    protected Element addElement(Document aDocument, Node aNode, String aElementName) {
        Element theElement = aDocument.createElement(aElementName);
        aNode.appendChild(theElement);
        return theElement;
    }

    protected void setBooleanAttribute(Element aElement, String aAttributeName, boolean aValue) {
        aElement.setAttribute(aAttributeName, aValue ? TRUE : FALSE);
    }

    protected void serializeProperties(Document aDocument, Element aNode, ModelItem aItem) {

        aNode.setAttribute(ID, aItem.getSystemId());
        aNode.setAttribute(NAME, aItem.getName());

        for (String theKey : aItem.getProperties().getProperties().keySet()) {
            String theValue = aItem.getProperties().getProperties().get(theKey);
            if (theValue != null) {
                Element theProperty = addElement(aDocument, aNode, PROPERTY);
                theProperty.setAttribute(NAME, theKey);
                theProperty.setAttribute(VALUE, theValue);
            }
        }
    }

    protected void serializeCommentElement(Document aDocument, Element aElement, ModelItem aItem) {
        Element theCommentElement = aDocument.createElement(COMMENT);
        if (aItem.getComment() != null) {
            theCommentElement.appendChild(aDocument.createTextNode(aItem.getComment()));
        }
        aElement.appendChild(theCommentElement);
    }

    protected void deserializeCommentElement(Element aElement, ModelItem aItem) {
        NodeList theChilds = aElement.getChildNodes();
        for (int i = 0; i < theChilds.getLength(); i++) {
            Node theChild = theChilds.item(i);
            if (COMMENT.equals(theChild.getNodeName())) {
                Element theElement = (Element) theChild;
                if (theElement.getChildNodes().getLength() > 0) {
                    aItem.setComment(theElement.getChildNodes().item(0).getNodeValue());
                }
            }
        }
    }

    protected void deserializeProperties(Element aElement, ModelItem aModelItem) {

        aModelItem.setSystemId(aElement.getAttribute(ID));
        aModelItem.setName(aElement.getAttribute(NAME));

        NodeList theProperties = aElement.getElementsByTagName(PROPERTY);
        for (int i = 0; i < theProperties.getLength(); i++) {
            Element theElement = (Element) theProperties.item(i);

            aModelItem.getProperties().setProperty(theElement.getAttribute(NAME), theElement.getAttribute(VALUE));
        }
    }    
}

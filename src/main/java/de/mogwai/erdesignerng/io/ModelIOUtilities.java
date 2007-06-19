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
package de.mogwai.erdesignerng.io;

import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.mogwai.erdesignerng.model.CascadeType;
import de.mogwai.erdesignerng.model.Model;
import de.mogwai.erdesignerng.model.Domain;
import de.mogwai.erdesignerng.model.Table;
import de.mogwai.erdesignerng.model.Attribute;
import de.mogwai.erdesignerng.model.Relation;

import de.mogwai.erdesignerng.model.ModelItem;

public class ModelIOUtilities {

	private static ModelIOUtilities me;

	private DocumentBuilderFactory documentBuilderFactory;

	private DocumentBuilder documentBuilder;

	private TransformerFactory transformerFactory;

	private ModelIOUtilities() throws ParserConfigurationException {
		documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilder = documentBuilderFactory.newDocumentBuilder();
		transformerFactory = TransformerFactory.newInstance();
	}

	public static ModelIOUtilities getInstance()
			throws ParserConfigurationException {

		if (me == null) {
			me = new ModelIOUtilities();
		}
		return me;
	}

	protected Element addElement(Document aDocument, Node aNode,
			String aElementName) {
		Element theElement = aDocument.createElement(aElementName);
		aNode.appendChild(theElement);
		return theElement;
	}

	protected void serializeProperties(Document aDocument, Element aNode,
			ModelItem aItem) {

		aNode.setAttribute("id", aItem.getSystemId());
		aNode.setAttribute("name", aItem.getName());

		for (String theKey : aItem.getProperties().keySet()) {
			String theValue = aItem.getProperties().get(theKey);
			if (theValue != null) {
				Element theProperty = addElement(aDocument, aNode, "Property");
				theProperty.setAttribute("name", theKey);
				theProperty.setAttribute("value", theValue);
			}
		}
	}
	
	protected void setBooleanAttribute(Element aElement,String aAttributeName,boolean aValue) {
		aElement.setAttribute(aAttributeName, aValue?"true":"false");
	}

	protected void setCascadeTypeAttribute(Element aElement,String aAttributeName,CascadeType aValue) {
		String theValue = "";
		if (aValue.equals(CascadeType.CASCADE)) {
			theValue="cascade";
		}
		if (aValue.equals(CascadeType.SET_NULL)) {
			theValue="setnull";
		}
		if (aValue.equals(CascadeType.NOTHING)) {
			theValue="nothing";
		}
		aElement.setAttribute(aAttributeName, theValue);
	}
	
	public void serializeModelToXML(Model aModel, OutputStream aStream)
			throws TransformerException {
		Document theDocument = documentBuilder.newDocument();

		Element theRootElement = addElement(theDocument, theDocument, "Model");
		theRootElement.setAttribute("version", "1.0");

		// Domains serialisieren
		Element theDomainsElement = addElement(theDocument, theRootElement,
				"Domains");
		for (Domain aDomain : aModel.getDomains()) {
			Element theDomainElement = addElement(theDocument,
					theDomainsElement, "Domain");

			// Basisdaten des Modelelementes speichern
			serializeProperties(theDocument, theDomainElement, aDomain);
			
			// Zusatzdaten
		}

		Element theTablesElement = addElement(theDocument, theRootElement,
				"Tables");
		for (Table aTable : aModel.getTables()) {
			Element theTableElement = addElement(theDocument, theTablesElement,
					"Table");

			// Basisdaten des Modelelementes speichern
			serializeProperties(theDocument, theTableElement, aTable);

			// Attribute serialisieren
			for (Attribute theAttribute : aTable.getAttributes()) {

				Element theAttributeElement = addElement(theDocument,
						theTableElement, "Attribute");

				// Basisdaten des Modelelementes speichern
				serializeProperties(theDocument, theAttributeElement,
						theAttribute);

				// Domain usw
				Domain theDomain = theAttribute.getDomain();
				theAttributeElement.setAttribute("domainrefid", theDomain
						.getSystemId());
				
				setBooleanAttribute(theAttributeElement, "nullable", theAttribute.isNullable());
				setBooleanAttribute(theAttributeElement, "primarykey", theAttribute.isPrimaryKey());				
			}
		}

		Element theRelationsElement = addElement(theDocument, theRootElement,
				"Relations");
		for (Relation aRelation : aModel.getRelations()) {
			Element theRelationElement = addElement(theDocument,
					theRelationsElement, "Relation");

			// Basisdaten des Modelelementes speichern
			serializeProperties(theDocument, theRelationElement, aRelation);
			
			// Zusatzdaten
			theRelationElement.setAttribute("starttablerefid",aRelation.getStart().getSystemId());
			theRelationElement.setAttribute("endtableredid",aRelation.getStart().getSystemId());
			
			setCascadeTypeAttribute(theRelationElement, "ondelete", aRelation.getOnDelete());
			setCascadeTypeAttribute(theRelationElement, "onupdate", aRelation.getOnDelete());			
		}

		Transformer theTransformer = transformerFactory.newTransformer();
		theTransformer.transform(new DOMSource(theDocument), new StreamResult(
				aStream));
	}
}

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

import java.io.IOException;
import java.io.InputStream;
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
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.mogwai.erdesignerng.model.Attribute;
import de.mogwai.erdesignerng.model.CascadeType;
import de.mogwai.erdesignerng.model.Domain;
import de.mogwai.erdesignerng.model.Index;
import de.mogwai.erdesignerng.model.IndexType;
import de.mogwai.erdesignerng.model.Model;
import de.mogwai.erdesignerng.model.ModelItem;
import de.mogwai.erdesignerng.model.Relation;
import de.mogwai.erdesignerng.model.Table;

public final class ModelIOUtilities {

	protected static final String ID = "id";

	protected static final String NAME = "name";

	protected static final String VALUE = "value";

	protected static final String PROPERTY = "Property";

	protected static final String TRUE = "true";

	protected static final String FALSE = "false";

	protected static final String CASCADE = "cascade";

	protected static final String SET_NULL = "setnull";

	protected static final String NOTHING = "nothing";

	protected static final String DOMAIN = "Domain";

	protected static final String DOMAINS = "Domains";

	protected static final String TABLE = "Table";

	protected static final String TABLES = "Tables";

	protected static final String ATTRIBUTE = "Attribute";

	protected static final String INDEXATTRIBUTE = "Indexattribute";

	protected static final String ATTRIBUTEREFID = "attributerefid";

	protected static final String RELATION = "Relation";

	protected static final String RELATIONS = "Relations";

	protected static final String MAPPING = "Mapping";

	protected static final String MODEL = "Model";

	protected static final String DOMAINREFID = "domainrefid";

	protected static final String ONDELETE = "ondelete";

	protected static final String ONUPDATE = "onupdate";

	protected static final String IMPORTINGTABLEREFID = "importingtablerefid";

	protected static final String EXPORTINGTABLEREFID = "exportingtablerefid";

	protected static final String IMPORTINGATTRIBUTEREFID = "importingattributerefid";

	protected static final String EXPORTINGATTRIBUTEREFID = "exportingattributerefid";

	protected static final String NULLABLE = "nullable";

	protected static final String PRIMARYKEY = "primarykey";

	protected static final String VERSION = "version";

	protected static final String INDEX = "Index";

	protected static final String INDEXTYPE = "indextype";

	protected static final String DEFAULT = "default";

	protected static final String DATATYPE = "datatype";
	
	protected static final String SEQUENCED = "sequenced";

	protected static final String JAVA_CLASS_NAME = "javaclassname";

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

		aNode.setAttribute(ID, aItem.getSystemId());
		aNode.setAttribute(NAME, aItem.getName());

		for (String theKey : aItem.getProperties().keySet()) {
			String theValue = aItem.getProperties().get(theKey);
			if (theValue != null) {
				Element theProperty = addElement(aDocument, aNode, PROPERTY);
				theProperty.setAttribute(NAME, theKey);
				theProperty.setAttribute(VALUE, theValue);
			}
		}
	}

	protected void deserializeProperties(Element aElement, ModelItem aModelItem) {

		aModelItem.setSystemId(aElement.getAttribute(ID));
		aModelItem.setName(aElement.getAttribute(NAME));

		NodeList theProperties = aElement.getElementsByTagName(PROPERTY);
		for (int i = 0; i < theProperties.getLength(); i++) {
			Element theElement = (Element) theProperties.item(i);

			aModelItem.setProperty(theElement.getAttribute(NAME), theElement
					.getAttribute(VALUE));
		}
	}

	protected void setBooleanAttribute(Element aElement, String aAttributeName,
			boolean aValue) {
		aElement.setAttribute(aAttributeName, aValue ? TRUE : FALSE);
	}

	public Model deserializeModelFromXML(InputStream aInputStream)
			throws SAXException, IOException {
		Document theDocument = documentBuilder.parse(aInputStream);
		aInputStream.close();
		Model theModel = new Model();

		// First of all, parse the domains
		NodeList theElements = theDocument.getElementsByTagName(DOMAIN);
		for (int i = 0; i < theElements.getLength(); i++) {
			Element theElement = (Element) theElements.item(i);

			Domain theDomain = new Domain();
			deserializeProperties(theElement, theDomain);

			theDomain.setDatatype(theElement.getAttribute(DATATYPE));
			theDomain.setJavaClassName(theElement.getAttribute(JAVA_CLASS_NAME));
			theDomain.setSequenced(TRUE.equals(theElement.getAttribute(SEQUENCED)));
			
			theModel.getDomains().add(theDomain);
		}

		// Now, parse tables
		theElements = theDocument.getElementsByTagName(TABLE);
		for (int i = 0; i < theElements.getLength(); i++) {
			Element theElement = (Element) theElements.item(i);

			Table theTable = new Table();
			deserializeProperties(theElement, theTable);

			// Parse the Attributes
			NodeList theAttributes = theElement.getElementsByTagName(ATTRIBUTE);
			for (int j = 0; j < theAttributes.getLength(); j++) {
				Element theAttributeElement = (Element) theAttributes.item(j);

				Attribute theAttribute = new Attribute();
				deserializeProperties(theAttributeElement, theAttribute);

				String theDomainId = theAttributeElement
						.getAttribute(DOMAINREFID);

				Domain theDomain = theModel.getDomains().findBySystemId(
						theDomainId);

				if (theDomain == null) {
					throw new IllegalArgumentException(
							"Cannot find domain with id " + theDomainId);
				}

				theAttribute.setDefinition(theDomain, TRUE
						.equals(theAttributeElement.getAttribute(NULLABLE)),
						theAttributeElement.getAttribute(DEFAULT));

				theTable.getAttributes().add(theAttribute);
			}

			// Parse the indexes
			NodeList theIndexes = theElement.getElementsByTagName(INDEX);
			for (int j = 0; j < theIndexes.getLength(); j++) {

				Element theIndexElement = (Element) theIndexes.item(j);
				Index theIndex = new Index();

				deserializeProperties(theIndexElement, theIndex);

				theIndex.setIndexType(IndexType.fromType(theIndexElement
						.getAttribute(INDEXTYPE)));

				theAttributes = theIndexElement
						.getElementsByTagName(INDEXATTRIBUTE);
				for (int k = 0; k < theAttributes.getLength(); k++) {

					Element theAttributeElement = (Element) theAttributes
							.item(k);

					String theAttributeRefId = theAttributeElement
							.getAttribute(ATTRIBUTEREFID);
					theIndex.getAttributes().add(
							theTable.getAttributes().findBySystemId(
									theAttributeRefId));
				}

				theTable.getIndexes().add(theIndex);

			}

			theModel.getTables().add(theTable);
		}

		// And finally, parse the relations
		theElements = theDocument.getElementsByTagName(RELATION);
		for (int i = 0; i < theElements.getLength(); i++) {
			Element theElement = (Element) theElements.item(i);

			Relation theRelation = new Relation();
			deserializeProperties(theElement, theRelation);

			theRelation.setOnDelete(CascadeType.fromType(theElement
					.getAttribute(ONDELETE)));
			theRelation.setOnUpdate(CascadeType.fromType(theElement
					.getAttribute(ONUPDATE)));

			String theStartTableID = theElement
					.getAttribute(IMPORTINGTABLEREFID);
			String theEndTableID = theElement.getAttribute(EXPORTINGTABLEREFID);

			Table theTempTable = theModel.getTables().findTableBySystemId(
					theStartTableID);
			if (theTempTable == null) {
				throw new IllegalArgumentException("Cannot find table with id "
						+ theStartTableID);
			}
			theRelation.setImportingTable(theTempTable);
			theTempTable = theModel.getTables().findTableBySystemId(
					theEndTableID);
			if (theTempTable == null) {
				throw new IllegalArgumentException("Cannot find table with id "
						+ theEndTableID);
			}

			theRelation.setExportingTable(theTempTable);

			// Parse the mapping
			NodeList theMappings = theElement.getElementsByTagName(MAPPING);
			for (int j = 0; j < theMappings.getLength(); j++) {
				Element theAttributeElement = (Element) theMappings.item(j);

				String theStartId = theAttributeElement
						.getAttribute(IMPORTINGATTRIBUTEREFID);
				String theEndId = theAttributeElement
						.getAttribute(EXPORTINGATTRIBUTEREFID);

				Attribute theStartAttribute = theModel.getTables()
						.findAttributeBySystemId(theStartId);
				if (theStartAttribute == null) {
					throw new IllegalArgumentException(
							"Cannot find attribute with id " + theStartId);
				}

				Attribute theEndAttribute = theModel.getTables()
						.findAttributeBySystemId(theEndId);
				if (theEndAttribute == null) {
					throw new IllegalArgumentException(
							"Cannot find attribute with id " + theEndId);
				}

				theRelation.getMapping()
						.put(theStartAttribute, theEndAttribute);
			}

			theModel.getRelations().add(theRelation);
		}

		return theModel;
	}
	
	public void serializeModelToXML(Model aModel, OutputStream aStream)
			throws TransformerException, IOException {
		Document theDocument = documentBuilder.newDocument();

		Element theRootElement = addElement(theDocument, theDocument, MODEL);
		theRootElement.setAttribute(VERSION, "1.0");

		// Domains serialisieren
		Element theDomainsElement = addElement(theDocument, theRootElement,
				DOMAINS);
		for (Domain theDomain : aModel.getDomains()) {
			Element theDomainElement = addElement(theDocument,
					theDomainsElement, DOMAIN);

			// Basisdaten des Modelelementes speichern
			serializeProperties(theDocument, theDomainElement, theDomain);

			// Zusatzdaten
			theDomainElement.setAttribute(DATATYPE, theDomain.getDatatype());
			theDomainElement.setAttribute(JAVA_CLASS_NAME, theDomain.getJavaClassName());
			setBooleanAttribute(theDomainElement, SEQUENCED, theDomain.isSequenced());
		}

		Element theTablesElement = addElement(theDocument, theRootElement,
				TABLES);
		for (Table theTable : aModel.getTables()) {
			Element theTableElement = addElement(theDocument, theTablesElement,
					TABLE);

			// Basisdaten des Modelelementes speichern
			serializeProperties(theDocument, theTableElement, theTable);

			// Attribute serialisieren
			for (Attribute theAttribute : theTable.getAttributes()) {

				Element theAttributeElement = addElement(theDocument,
						theTableElement, ATTRIBUTE);

				// Basisdaten des Modelelementes speichern
				serializeProperties(theDocument, theAttributeElement,
						theAttribute);

				// Domain usw
				Domain theDomain = theAttribute.getDomain();
				theAttributeElement.setAttribute(DOMAINREFID, theDomain
						.getSystemId());

				setBooleanAttribute(theAttributeElement, NULLABLE, theAttribute
						.isNullable());

				if (theAttribute.getDefaultValue() != null) {
					theAttributeElement.setAttribute(DEFAULT, theAttribute
							.getDefaultValue());
				}
			}

			// Indexes serialisieren
			for (Index theIndex : theTable.getIndexes()) {

				Element theIndexElement = addElement(theDocument,
						theTableElement, INDEX);

				theIndexElement.setAttribute(INDEXTYPE, theIndex.getIndexType()
						.getType());

				serializeProperties(theDocument, theIndexElement, theIndex);

				// Attribute
				for (Attribute theAttribute : theIndex.getAttributes()) {
					Element theAttributeElement = addElement(theDocument,
							theIndexElement, INDEXATTRIBUTE);
					theAttributeElement.setAttribute(ATTRIBUTEREFID,
							theAttribute.getSystemId());
				}

			}
		}

		Element theRelationsElement = addElement(theDocument, theRootElement,
				RELATIONS);
		for (Relation theRelation : aModel.getRelations()) {
			Element theRelationElement = addElement(theDocument,
					theRelationsElement, RELATION);

			// Basisdaten des Modelelementes speichern
			serializeProperties(theDocument, theRelationElement, theRelation);

			// Zusatzdaten
			theRelationElement.setAttribute(IMPORTINGTABLEREFID, theRelation
					.getImportingTable().getSystemId());
			theRelationElement.setAttribute(EXPORTINGTABLEREFID, theRelation
					.getExportingTable().getSystemId());

			theRelationElement.setAttribute(ONDELETE, theRelation.getOnDelete()
					.getType());
			theRelationElement.setAttribute(ONUPDATE, theRelation.getOnDelete()
					.getType());

			// Mapping
			for (Attribute theKey : theRelation.getMapping().keySet()) {
				Attribute theValue = theRelation.getMapping().get(theKey);

				Element theMapping = addElement(theDocument,
						theRelationElement, MAPPING);
				theMapping.setAttribute(IMPORTINGATTRIBUTEREFID, theKey
						.getSystemId());
				theMapping.setAttribute(EXPORTINGATTRIBUTEREFID, theValue
						.getSystemId());
			}
		}

		Transformer theTransformer = transformerFactory.newTransformer();
		theTransformer.transform(new DOMSource(theDocument), new StreamResult(
				aStream));

		aStream.close();
	}
}

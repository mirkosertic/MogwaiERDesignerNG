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
package de.erdesignerng.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

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

import de.erdesignerng.dialect.DialectFactory;
import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.CascadeType;
import de.erdesignerng.model.DefaultValue;
import de.erdesignerng.model.Domain;
import de.erdesignerng.model.Index;
import de.erdesignerng.model.IndexType;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.ModelItem;
import de.erdesignerng.model.Relation;
import de.erdesignerng.model.Table;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-01-12 17:10:01 $
 */
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

	protected static final String CONFIGURATION = "Configuration";

	protected static final String DIALECT = "dialect";

	protected static final String DEFAULTVALUES = "Defaultvalues";

	protected static final String DEFAULTVALUE = "Defaultvalue";

	protected static final String DEFAULTVALUEREFID = "defaultvaluerefid";

	protected static final String COMMENT = "Comment";

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

	private Element addElement(Document aDocument, Node aNode,
			String aElementName) {
		Element theElement = aDocument.createElement(aElementName);
		aNode.appendChild(theElement);
		return theElement;
	}

	private void serializeProperties(Document aDocument, Element aNode,
			ModelItem aItem) {

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

	private void deserializeProperties(Element aElement, ModelItem aModelItem) {

		aModelItem.setSystemId(aElement.getAttribute(ID));
		aModelItem.setName(aElement.getAttribute(NAME));

		NodeList theProperties = aElement.getElementsByTagName(PROPERTY);
		for (int i = 0; i < theProperties.getLength(); i++) {
			Element theElement = (Element) theProperties.item(i);

			aModelItem.getProperties().setProperty(
					theElement.getAttribute(NAME),
					theElement.getAttribute(VALUE));
		}
	}

	private void setBooleanAttribute(Element aElement, String aAttributeName,
			boolean aValue) {
		aElement.setAttribute(aAttributeName, aValue ? TRUE : FALSE);
	}

	public Model deserializeModelFromXML(InputStream aInputStream)
			throws SAXException, IOException {
		Document theDocument = documentBuilder.parse(aInputStream);
		aInputStream.close();
		Model theModel = new Model();

		NodeList theElements = theDocument.getElementsByTagName(CONFIGURATION);
		for (int i = 0; i < theElements.getLength(); i++) {
			Element theElement = (Element) theElements.item(i);

			NodeList theProperties = theElement.getElementsByTagName(PROPERTY);
			for (int j = 0; j < theProperties.getLength(); j++) {
				Element theProperty = (Element) theProperties.item(j);

				String theName = theProperty.getAttribute(NAME);
				String theValue = theProperty.getAttribute(VALUE);

				if (DIALECT.equals(theName)) {
					theModel.setDialect(DialectFactory.getInstance()
							.getDialect(theValue));
				} else {
					theModel.getProperties().setProperty(theName, theValue);
				}
			}
		}

		// Default values
		theElements = theDocument.getElementsByTagName(DEFAULTVALUE);
		for (int i = 0; i < theElements.getLength(); i++) {
			Element theElement = (Element) theElements.item(i);

			DefaultValue theDefaultValue = new DefaultValue();
			theDefaultValue.setOwner(theModel);
			deserializeProperties(theElement, theDefaultValue);

			theDefaultValue.setDatatype(theElement.getAttribute(DATATYPE));

			theModel.getDefaultValues().add(theDefaultValue);
		}

		// First of all, parse the domains
		theElements = theDocument.getElementsByTagName(DOMAIN);
		for (int i = 0; i < theElements.getLength(); i++) {
			Element theElement = (Element) theElements.item(i);

			Domain theDomain = new Domain();
			theDomain.setOwner(theModel);
			deserializeProperties(theElement, theDomain);

			theDomain.setDatatype(theElement.getAttribute(DATATYPE));
			theDomain
					.setJavaClassName(theElement.getAttribute(JAVA_CLASS_NAME));
			theDomain.setSequenced(TRUE.equals(theElement
					.getAttribute(SEQUENCED)));

			theModel.getDomains().add(theDomain);
		}

		// Now, parse tables
		theElements = theDocument.getElementsByTagName(TABLE);
		for (int i = 0; i < theElements.getLength(); i++) {
			Element theElement = (Element) theElements.item(i);

			Table theTable = new Table();
			theTable.setOwner(theModel);
			deserializeProperties(theElement, theTable);

			deserializeCommentElement(theElement, theTable);

			// Parse the Attributes
			NodeList theAttributes = theElement.getElementsByTagName(ATTRIBUTE);
			for (int j = 0; j < theAttributes.getLength(); j++) {
				Element theAttributeElement = (Element) theAttributes.item(j);

				Attribute theAttribute = new Attribute();
				theAttribute.setOwner(theTable);
				deserializeProperties(theAttributeElement, theAttribute);

				deserializeCommentElement(theAttributeElement, theAttribute);

				String theDomainId = theAttributeElement
						.getAttribute(DOMAINREFID);

				Domain theDomain = theModel.getDomains().findBySystemId(
						theDomainId);

				if (theDomain == null) {
					throw new IllegalArgumentException(
							"Cannot find domain with id " + theDomainId);
				}

				DefaultValue theDefault = null;
				String theDefaultRefId = theAttributeElement
						.getAttribute(DEFAULTVALUEREFID);
				if ((theDefaultRefId != null) && (!"".equals(theDefaultRefId))) {
					theDefault = theModel.getDefaultValues().findBySystemId(
							theDefaultRefId);
					if (theDefault == null) {
						throw new IllegalArgumentException(
								"Cannot find default value with id "
										+ theDefaultRefId);
					}
				}

				theAttribute.setDefinition(theDomain, TRUE
						.equals(theAttributeElement.getAttribute(NULLABLE)),
						theDefault);

				theAttribute.setPrimaryKey(TRUE.equals(theAttributeElement
						.getAttribute(PRIMARYKEY)));

				theTable.getAttributes().add(theAttribute);
			}

			// Parse the indexes
			NodeList theIndexes = theElement.getElementsByTagName(INDEX);
			for (int j = 0; j < theIndexes.getLength(); j++) {

				Element theIndexElement = (Element) theIndexes.item(j);
				Index theIndex = new Index();
				theIndex.setOwner(theTable);
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
			theRelation.setOwner(theModel);
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

	private void serializeCommentElement(Document aDocument, Element aElement,
			ModelItem aItem) {
		Element theCommentElement = aDocument.createElement(COMMENT);
		if (aItem.getComment() != null) {
			theCommentElement.appendChild(aDocument.createTextNode(aItem
					.getComment()));
		}
		aElement.appendChild(theCommentElement);
	}

	private void deserializeCommentElement(Element aElement, ModelItem aItem) {
		NodeList theChilds = aElement.getChildNodes();
		for (int i = 0; i < theChilds.getLength(); i++) {
			Node theChild = theChilds.item(i);
			if (COMMENT.equals(theChild.getNodeName())) {
				Element theElement = (Element) theChild;
				if (theElement.getChildNodes().getLength() > 0) {
					aItem.setComment(theElement.getChildNodes().item(0)
							.getNodeValue());
				}
			}
		}
	}

	public void serializeModelToXML(Model aModel, OutputStream aStream)
			throws TransformerException, IOException {
		Document theDocument = documentBuilder.newDocument();

		Element theRootElement = addElement(theDocument, theDocument, MODEL);
		theRootElement.setAttribute(VERSION, "1.0");

		Element theConfigurationElement = addElement(theDocument,
				theRootElement, CONFIGURATION);

		Element theDialectElement = addElement(theDocument,
				theConfigurationElement, PROPERTY);
		theDialectElement.setAttribute(NAME, DIALECT);
		theDialectElement.setAttribute(VALUE, aModel.getDialect()
				.getUniqueName());

		Map<String, String> theProperties = aModel.getProperties()
				.getProperties();
		for (String theKey : theProperties.keySet()) {
			String theValue = theProperties.get(theKey);

			Element thePropertyElement = addElement(theDocument,
					theConfigurationElement, PROPERTY);
			thePropertyElement.setAttribute(NAME, theKey);
			thePropertyElement.setAttribute(VALUE, theValue);
		}

		// Default values
		Element theDefaultValuesElement = addElement(theDocument,
				theRootElement, DEFAULTVALUES);
		for (DefaultValue theDefaultValue : aModel.getDefaultValues()) {
			Element theDefaultValueElement = addElement(theDocument,
					theDefaultValuesElement, DEFAULTVALUE);

			// Basisdaten des Modelelementes speichern
			serializeProperties(theDocument, theDefaultValueElement,
					theDefaultValue);

			// Zusatzdaten
			theDefaultValueElement.setAttribute(DATATYPE, theDefaultValue
					.getDatatype());
		}

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
			theDomainElement.setAttribute(JAVA_CLASS_NAME, theDomain
					.getJavaClassName());
			setBooleanAttribute(theDomainElement, SEQUENCED, theDomain
					.isSequenced());
		}

		Element theTablesElement = addElement(theDocument, theRootElement,
				TABLES);
		for (Table theTable : aModel.getTables()) {
			Element theTableElement = addElement(theDocument, theTablesElement,
					TABLE);

			// Basisdaten des Modelelementes speichern
			serializeProperties(theDocument, theTableElement, theTable);

			serializeCommentElement(theDocument, theTableElement, theTable);

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

				setBooleanAttribute(theAttributeElement, PRIMARYKEY,
						theAttribute.isPrimaryKey());

				if (theAttribute.getDefaultValue() != null) {
					theAttributeElement.setAttribute(DEFAULTVALUEREFID,
							theAttribute.getDefaultValue().getSystemId());
				}

				serializeCommentElement(theDocument, theAttributeElement,
						theAttribute);
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

			serializeCommentElement(theDocument, theRelationElement,
					theRelation);

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

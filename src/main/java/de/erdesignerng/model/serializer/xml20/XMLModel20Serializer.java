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

import de.erdesignerng.dialect.DialectFactory;
import de.erdesignerng.model.Comment;
import de.erdesignerng.model.Domain;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.Relation;
import de.erdesignerng.model.SubjectArea;
import de.erdesignerng.model.Table;
import de.erdesignerng.model.View;
import de.erdesignerng.model.serializer.AbstractXMLIndexSerializer;
import de.erdesignerng.model.serializer.AbstractXMLModelSerializer;
import de.erdesignerng.model.serializer.AbstractXMLRelationSerializer;
import de.erdesignerng.model.serializer.AbstractXMLSubjectAreaSerializer;
import de.erdesignerng.model.serializer.AbstractXMLTableSerializer;
import de.erdesignerng.model.serializer.AbstractXMLViewSerializer;
import de.erdesignerng.model.serializer.xml10.XMLModel10Serializer;
import de.erdesignerng.util.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.Map;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-13 15:40:34 $
 */
public class XMLModel20Serializer extends XMLModel10Serializer {

	private static final String CURRENT_VERSION = "2.0";

	private static final String XML_SCHEMA_DEFINITION = "/erdesignerschema_2.0.xsd";

	private AbstractXMLViewSerializer xmlViewSerializer = null;

	public XMLModel20Serializer(XMLUtils utils) {
		super(utils);
		setXMLIndexSerializer(new XMLIndexSerializer());
		setXMLRelationSerializer(new XMLRelationSerializer());
		setXMLSubjectAreaSerializer(new XMLSubjectAreaSerializer());
		setXMLTableSerializer(new XMLTableSerializer(this));
		setXMLViewSerializer(new XMLViewSerializer());
	}

	@Override
	protected void serialize(Model aModel, Document aDocument) {

		Element theRootElement = addElement(aDocument, aDocument, MODEL);
		theRootElement.setAttribute(VERSION, getVersion());

		Element theConfigurationElement = addElement(aDocument, theRootElement, CONFIGURATION);

		Element theDialectElement = addElement(aDocument, theConfigurationElement, PROPERTY);
		theDialectElement.setAttribute(NAME, DIALECT);
		theDialectElement.setAttribute(VALUE, aModel.getDialect().getUniqueName());

		for (Map.Entry<String, String> theEntry : aModel.getProperties().getProperties().entrySet()) {
			Element thePropertyElement = addElement(aDocument, theConfigurationElement, PROPERTY);
			thePropertyElement.setAttribute(NAME, theEntry.getKey());
			thePropertyElement.setAttribute(VALUE, theEntry.getValue());
		}

		Element theDomainsElement = addElement(aDocument, theRootElement, DOMAINS);
		for (Domain theTable : aModel.getDomains()) {
			getXMLDomainSerializer().serialize(theTable, aDocument, theDomainsElement);
		}

		Element theTablesElement = addElement(aDocument, theRootElement, TABLES);
		for (Table theTable : aModel.getTables()) {
			getXMLTableSerializer(this).serialize(theTable, aDocument, theTablesElement);
		}

		Element theRelationsElement = addElement(aDocument, theRootElement, RELATIONS);
		for (Relation theRelation : aModel.getRelations()) {
			getXMLRelationSerializer().serialize(theRelation, aDocument, theRelationsElement);
		}

		Element theViewsElement = addElement(aDocument, theRootElement, VIEWS);
		for (View theView : aModel.getViews()) {
			getXMLViewSerializer().serialize(theView, aDocument, theViewsElement);
		}

		Element theSubjectAreasElement = addElement(aDocument, theRootElement, SUBJECTAREAS);
		for (SubjectArea theSubjectArea : aModel.getSubjectAreas()) {
			getXMLSubjectAreaSerializer().serialize(theSubjectArea, aDocument, theSubjectAreasElement);
		}

		Element theCommentsElement = addElement(aDocument, theRootElement, COMMENTS);
		for (Comment theComment : aModel.getComments()) {
			getXMLCommentSerializer().serialize(theComment, aDocument, theCommentsElement);
		}
	}

	@Override
	protected Model deserialize(Document aDocument) {
		Model theModel = new Model();

		NodeList theElements = aDocument.getElementsByTagName(MODEL);
		for (int i = 0; i < theElements.getLength(); i++) {
			Element theElement = (Element) theElements.item(i);
			String theVersion = theElement.getAttribute(VERSION);
			if (!getVersion().equals(theVersion)) {
				throw new RuntimeException("Unsupported version of model : " + theVersion);
			}
		}

		theElements = aDocument.getElementsByTagName(CONFIGURATION);
		for (int i = 0; i < theElements.getLength(); i++) {
			Element theElement = (Element) theElements.item(i);

			NodeList theProperties = theElement.getElementsByTagName(PROPERTY);
			for (int j = 0; j < theProperties.getLength(); j++) {
				Element theProperty = (Element) theProperties.item(j);

				String theName = theProperty.getAttribute(NAME);
				String theValue = theProperty.getAttribute(VALUE);

				if (DIALECT.equals(theName)) {
					theModel.setDialect(DialectFactory.getInstance().getDialect(theValue));
				} else {
					theModel.getProperties().setProperty(theName, theValue);
				}
			}
		}

		getXMLDomainSerializer().deserialize(theModel, aDocument);
		getXMLTableSerializer(this).deserialize(theModel, aDocument);
		getXMLRelationSerializer().deserialize(theModel, aDocument);
		getXMLViewSerializer().deserialize(theModel, aDocument);
		getXMLCommentSerializer().deserialize(theModel, aDocument);
		getXMLSubjectAreaSerializer().deserialize(theModel, aDocument);

		return theModel;
	}

	@Override
	public String getSchemaResource() {
		return XML_SCHEMA_DEFINITION;
	}

	@Override
	public String getVersion() {
		return CURRENT_VERSION;
	}
	
		@Override
	public AbstractXMLIndexSerializer getXMLIndexSerializer() {
		if (super.getXMLIndexSerializer() == null) {
			setXMLIndexSerializer(new XMLIndexSerializer());
		}

		return super.getXMLIndexSerializer();
	}

	@Override
	protected AbstractXMLRelationSerializer getXMLRelationSerializer() {
		if (super.getXMLRelationSerializer() == null) {
			setXMLRelationSerializer(new XMLRelationSerializer());
		}

		return super.getXMLRelationSerializer();
	}

	@Override
	protected AbstractXMLSubjectAreaSerializer getXMLSubjectAreaSerializer() {
		if (super.getXMLSubjectAreaSerializer() == null) {
			setXMLSubjectAreaSerializer(new XMLSubjectAreaSerializer());
		}

		return super.getXMLSubjectAreaSerializer();
	}

	@Override
	protected AbstractXMLTableSerializer getXMLTableSerializer(AbstractXMLModelSerializer xmlModelSerializer) {
		if (super.getXMLTableSerializer(xmlModelSerializer) == null) {
			setXMLTableSerializer(new XMLTableSerializer(xmlModelSerializer));
		}

		return super.getXMLTableSerializer(xmlModelSerializer);
	}

	@Override
	protected AbstractXMLViewSerializer getXMLViewSerializer() {
		if (super.getXMLViewSerializer() == null) {
			setXMLViewSerializer(new XMLViewSerializer());
		}

		return super.getXMLViewSerializer();
	}
}
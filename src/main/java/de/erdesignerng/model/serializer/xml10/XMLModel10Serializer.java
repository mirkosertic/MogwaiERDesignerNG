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
package de.erdesignerng.model.serializer.xml10;

import de.erdesignerng.dialect.DialectFactory;
import de.erdesignerng.model.Comment;
import de.erdesignerng.model.Domain;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.Relation;
import de.erdesignerng.model.SubjectArea;
import de.erdesignerng.model.Table;
import de.erdesignerng.model.serializer.AbstractXMLAttributeSerializer;
import de.erdesignerng.model.serializer.AbstractXMLCommentSerializer;
import de.erdesignerng.model.serializer.AbstractXMLDomainSerializer;
import de.erdesignerng.model.serializer.AbstractXMLIndexSerializer;
import de.erdesignerng.model.serializer.AbstractXMLModelSerializer;
import de.erdesignerng.model.serializer.AbstractXMLRelationSerializer;
import de.erdesignerng.model.serializer.AbstractXMLSubjectAreaSerializer;
import de.erdesignerng.model.serializer.AbstractXMLTableSerializer;
import de.erdesignerng.util.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.Map;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-11-15 14:36:54 $
 */
public class XMLModel10Serializer extends AbstractXMLModelSerializer {

	private static final String CURRENT_VERSION = "1.0";

	private static final String XML_SCHEMA_DEFINITION = "/erdesignerschema_1.0.xsd";

	public XMLModel10Serializer(final XMLUtils utils) {
		super(utils);
	}

	@Override
	protected void serialize(final Model aModel, final Document aDocument) {

		final Element theRootElement = addElement(aDocument, aDocument, MODEL);
		theRootElement.setAttribute(VERSION, getVersion());

		final Element theConfigurationElement = addElement(aDocument, theRootElement, CONFIGURATION);

		final Element theDialectElement = addElement(aDocument, theConfigurationElement, PROPERTY);
		theDialectElement.setAttribute(NAME, DIALECT);
		theDialectElement.setAttribute(VALUE, aModel.getDialect().getUniqueName());

		for (final Map.Entry<String, String> theEntry : aModel.getProperties().getProperties().entrySet()) {
			final Element thePropertyElement = addElement(aDocument, theConfigurationElement, PROPERTY);
			thePropertyElement.setAttribute(NAME, theEntry.getKey());
			thePropertyElement.setAttribute(VALUE, theEntry.getValue());
		}

		final Element theDomainsElement = addElement(aDocument, theRootElement, DOMAINS);
		for (final Domain theTable : aModel.getDomains()) {
			getXMLDomainSerializer().serialize(theTable, aDocument, theDomainsElement);
		}

		final Element theTablesElement = addElement(aDocument, theRootElement, TABLES);
		for (final Table theTable : aModel.getTables()) {
			getXMLTableSerializer(this).serialize(theTable, aDocument, theTablesElement);
		}

		final Element theRelationsElement = addElement(aDocument, theRootElement, RELATIONS);
		for (final Relation theRelation : aModel.getRelations()) {
			getXMLRelationSerializer().serialize(theRelation, aDocument, theRelationsElement);
		}

		final Element theSubjectAreasElement = addElement(aDocument, theRootElement, SUBJECTAREAS);
		for (final SubjectArea theSubjectArea : aModel.getSubjectAreas()) {
			getXMLSubjectAreaSerializer().serialize(theSubjectArea, aDocument, theSubjectAreasElement);
		}

		final Element theCommentsElement = addElement(aDocument, theRootElement, COMMENTS);
		for (final Comment theComment : aModel.getComments()) {
			getXMLCommentSerializer().serialize(theComment, aDocument, theCommentsElement);
		}
	}

	@Override
	protected Model deserialize(final Document aDocument) {
		final Model theModel = new Model();

		final NodeList theElements = aDocument.getElementsByTagName(CONFIGURATION);
		for (int i = 0; i < theElements.getLength(); i++) {
			final Element theElement = (Element) theElements.item(i);

			final NodeList theProperties = theElement.getElementsByTagName(PROPERTY);
			for (int j = 0; j < theProperties.getLength(); j++) {
				final Element theProperty = (Element) theProperties.item(j);

				final String theName = theProperty.getAttribute(NAME);
				final String theValue = theProperty.getAttribute(VALUE);

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
		getXMLCommentSerializer().deserialize(theModel, aDocument);
		getXMLSubjectAreaSerializer().deserialize(theModel, aDocument);

		return theModel;
	}

	@Override
	protected String getSchemaResource() {
		return XML_SCHEMA_DEFINITION;
	}

	@Override
	protected String getVersion() {
		return CURRENT_VERSION;
	}

	@Override
	public AbstractXMLAttributeSerializer getXMLAttributeSerializer() {
		if (super.getXMLAttributeSerializer() == null) {
			setXMLAttributeSerializer(new XMLAttributeSerializer());
		}

		return super.getXMLAttributeSerializer();
	}

	@Override
	protected AbstractXMLCommentSerializer getXMLCommentSerializer() {
		if (super.getXMLCommentSerializer() == null) {
			setXMLCommentSerializer(new XMLCommentSerializer());
		}

		return super.getXMLCommentSerializer();
	}

	@Override
	protected AbstractXMLDomainSerializer getXMLDomainSerializer() {
		if (super.getXMLDomainSerializer() == null) {
			setXMLDomainSerializer(new XMLDomainSerializer());
		}

		return super.getXMLDomainSerializer();
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
	protected AbstractXMLTableSerializer getXMLTableSerializer(final AbstractXMLModelSerializer xmlModelSerializer) {
		if (super.getXMLTableSerializer(xmlModelSerializer) == null) {
			setXMLTableSerializer(new XMLTableSerializer(xmlModelSerializer));
		}

		return super.getXMLTableSerializer(xmlModelSerializer);
	}

}
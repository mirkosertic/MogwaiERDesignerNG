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
package de.erdesignerng.model.serializer.xml30;

import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.erdesignerng.dialect.DialectFactory;
import de.erdesignerng.model.Comment;
import de.erdesignerng.model.Domain;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.Relation;
import de.erdesignerng.model.SubjectArea;
import de.erdesignerng.model.Table;
import de.erdesignerng.model.View;
import de.erdesignerng.model.serializer.CommonXMLElementsAndAttributes;
import de.erdesignerng.model.serializer.XMLSerializer;

/**
 * @author $Author: dr-death $
 * @version $Date: 2009-10-21 10:00:00 $
 */
public class XMLModelSerializer extends XMLSerializer implements CommonXMLElementsAndAttributes {

    public static final XMLModelSerializer SERIALIZER = new XMLModelSerializer();

    protected static final String CONFIGURATION = "Configuration";

    protected static final String DIALECT = "dialect";

    protected static final String DEFAULTVALUES = "Defaultvalues";

    protected static final String DOMAINS = "Domains";

    protected static final String TABLES = "Tables";

    protected static final String RELATIONS = "Relations";

    protected static final String VIEWS = "Views";

    protected static final String SUBJECTAREAS = "Subjectareas";

    protected static final String COMMENTS = "Comments";

    protected static final String CURRENT_VERSION = "3.0";

    public void serialize(Model aModel, Document aDocument) {

        Element theRootElement = addElement(aDocument, aDocument, MODEL);
        theRootElement.setAttribute(VERSION, CURRENT_VERSION);

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
            XMLDomainSerializer.SERIALIZER.serialize(theTable, aDocument, theDomainsElement);
        }

        Element theTablesElement = addElement(aDocument, theRootElement, TABLES);
        for (Table theTable : aModel.getTables()) {
            XMLTableSerializer.SERIALIZER.serialize(theTable, aDocument, theTablesElement);
        }

        Element theRelationsElement = addElement(aDocument, theRootElement, RELATIONS);
        for (Relation theRelation : aModel.getRelations()) {
            XMLRelationSerializer.SERIALIZER.serialize(theRelation, aDocument, theRelationsElement);
        }

        Element theViewsElement = addElement(aDocument, theRootElement, VIEWS);
        for (View theView : aModel.getViews()) {
            XMLViewSerializer.SERIALIZER.serialize(theView, aDocument, theViewsElement);
        }

        Element theSubjectAreasElement = addElement(aDocument, theRootElement, SUBJECTAREAS);
        for (SubjectArea theSubjectArea : aModel.getSubjectAreas()) {
            XMLSubjectAreaSerializer.SERIALIZER.serialize(theSubjectArea, aDocument, theSubjectAreasElement);
        }

        Element theCommentsElement = addElement(aDocument, theRootElement, COMMENTS);
        for (Comment theComment : aModel.getComments()) {
            XMLCommentSerializer.SERIALIZER.serialize(theComment, aDocument, theCommentsElement);
        }
    }

    public Model deserializeFrom(Document aDocument) {
        Model theModel = new Model();

        NodeList theElements = aDocument.getElementsByTagName(MODEL);
        for (int i = 0; i < theElements.getLength(); i++) {
            Element theElement = (Element) theElements.item(i);
            String theVersion = theElement.getAttribute(VERSION);
            if (!CURRENT_VERSION.equals(theVersion)) {
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

        XMLDomainSerializer.SERIALIZER.deserializeFrom(theModel, aDocument);
        XMLTableSerializer.SERIALIZER.deserializeFrom(theModel, aDocument);
        XMLRelationSerializer.SERIALIZER.deserializeFrom(theModel, aDocument);
        XMLViewSerializer.SERIALIZER.deserializeFrom(theModel, aDocument);
        XMLCommentSerializer.SERIALIZER.deserializeFrom(theModel, aDocument);
        XMLSubjectAreaSerializer.SERIALIZER.deserializeFrom(theModel, aDocument);

        return theModel;
    }
}
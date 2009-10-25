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

import de.erdesignerng.model.serializer.AbstractXMLRelationSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.CascadeType;
import de.erdesignerng.model.IndexExpression;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.Relation;
import de.erdesignerng.model.Table;

public class XMLRelationSerializer extends AbstractXMLRelationSerializer {

    public void serialize(Relation aRelation, Document aDocument, Element aRootElement) {
        Element theRelationElement = addElement(aDocument, aRootElement, RELATION);

        // Basisdaten des Modelelementes speichern
        serializeProperties(aDocument, theRelationElement, aRelation);

        // Zusatzdaten
        theRelationElement.setAttribute(IMPORTINGTABLEREFID, aRelation.getImportingTable().getSystemId());
        theRelationElement.setAttribute(EXPORTINGTABLEREFID, aRelation.getExportingTable().getSystemId());

        theRelationElement.setAttribute(ONDELETE, aRelation.getOnDelete().getType());
        theRelationElement.setAttribute(ONUPDATE, aRelation.getOnDelete().getType());

        serializeCommentElement(aDocument, theRelationElement, aRelation);

        // Mapping
        for (IndexExpression theKey : aRelation.getMapping().keySet()) {
            Attribute theValue = aRelation.getMapping().get(theKey);

            Element theMapping = addElement(aDocument, theRelationElement, MAPPING);
            theMapping.setAttribute(IMPORTINGATTRIBUTEREFID, theKey.getAttributeRef().getSystemId());
            theMapping.setAttribute(EXPORTINGATTRIBUTEREFID, theValue.getSystemId());
        }
    }

    public void deserialize(Model aModel, Document aDocument) {

        // And finally, parse the relations
        NodeList theElements = aDocument.getElementsByTagName(RELATION);
        for (int i = 0; i < theElements.getLength(); i++) {
            Element theElement = (Element) theElements.item(i);

            Relation theRelation = new Relation();
            theRelation.setOwner(aModel);
            deserializeProperties(theElement, theRelation);

            theRelation.setOnDelete(CascadeType.fromType(theElement.getAttribute(ONDELETE)));
            theRelation.setOnUpdate(CascadeType.fromType(theElement.getAttribute(ONUPDATE)));

            String theStartTableID = theElement.getAttribute(IMPORTINGTABLEREFID);
            String theEndTableID = theElement.getAttribute(EXPORTINGTABLEREFID);

            Table theTempTable = aModel.getTables().findBySystemId(theStartTableID);
            if (theTempTable == null) {
                throw new IllegalArgumentException("Cannot find table with id " + theStartTableID);
            }
            theRelation.setImportingTable(theTempTable);
            theTempTable = aModel.getTables().findBySystemId(theEndTableID);
            if (theTempTable == null) {
                throw new IllegalArgumentException("Cannot find table with id " + theEndTableID);
            }

            theRelation.setExportingTable(theTempTable);

            // Parse the mapping
            NodeList theMappings = theElement.getElementsByTagName(MAPPING);
            for (int j = 0; j < theMappings.getLength(); j++) {
                Element theAttributeElement = (Element) theMappings.item(j);

                String theStartId = theAttributeElement.getAttribute(IMPORTINGATTRIBUTEREFID);
                String theEndId = theAttributeElement.getAttribute(EXPORTINGATTRIBUTEREFID);

                Attribute theStartAttribute = aModel.getTables().findAttributeBySystemId(theStartId);
                if (theStartAttribute == null) {
                    throw new IllegalArgumentException("Cannot find attribute with id " + theStartId);
                }

                Attribute theEndAttribute = aModel.getTables().findAttributeBySystemId(theEndId);
                if (theEndAttribute == null) {
                    throw new IllegalArgumentException("Cannot find attribute with id " + theEndId);
                }

                IndexExpression theExpression = theRelation.getExportingTable().getPrimarykey().getExpressions()
                        .findByAttribute(theStartAttribute);

                theRelation.getMapping().put(theExpression, theEndAttribute);
            }

            aModel.getRelations().add(theRelation);
        }
    }
}
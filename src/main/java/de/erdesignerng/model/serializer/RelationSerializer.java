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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.Relation;

public class RelationSerializer extends Serializer {
    
    public static final String RELATION = "Relation";

    public static final String MAPPING = "Mapping";
    
    public static final String IMPORTINGTABLEREFID = "importingtablerefid";

    public static final String EXPORTINGTABLEREFID = "exportingtablerefid";

    public static final String IMPORTINGATTRIBUTEREFID = "importingattributerefid";

    public static final String EXPORTINGATTRIBUTEREFID = "exportingattributerefid";

    public static final String ONDELETE = "ondelete";

    public static final String ONUPDATE = "onupdate";

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
        for (Attribute theKey : aRelation.getMapping().keySet()) {
            Attribute theValue = aRelation.getMapping().get(theKey);

            Element theMapping = addElement(aDocument, theRelationElement, MAPPING);
            theMapping.setAttribute(IMPORTINGATTRIBUTEREFID, theKey.getSystemId());
            theMapping.setAttribute(EXPORTINGATTRIBUTEREFID, theValue.getSystemId());
        }
        
    }
}

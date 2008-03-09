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

import java.awt.Color;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.erdesignerng.model.Model;
import de.erdesignerng.model.SubjectArea;
import de.erdesignerng.model.Table;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-03-09 18:20:28 $
 */
public class SubjectAreaSerializer extends Serializer {

    public static final SubjectAreaSerializer SERIALIZER = new SubjectAreaSerializer();

    public static final String SUBJECTAREA = "Subjectarea";

    public static final String ITEM = "Item";

    public static final String TABLEREFID = "tablerefid";

    public static final String COLOR = "color";

    public void serialize(SubjectArea aArea, Document aDocument, Element aRootElement) {

        Element theSubjectAreaElement = addElement(aDocument, aRootElement, SUBJECTAREA);

        // Basisdaten des Modelelementes speichern
        serializeProperties(aDocument, theSubjectAreaElement, aArea);
        theSubjectAreaElement.setAttribute(COLOR, "" + aArea.getColor().getRGB());

        for (Table theTable : aArea.getTables()) {

            Element theTableElement = addElement(aDocument, theSubjectAreaElement, ITEM);
            theTableElement.setAttribute(TABLEREFID, theTable.getSystemId());

        }

    }

    public void deserializeFrom(Model aModel, Document aDocument) {

        NodeList theElements = aDocument.getElementsByTagName(SUBJECTAREA);
        for (int i = 0; i < theElements.getLength(); i++) {
            Element theElement = (Element) theElements.item(i);

            SubjectArea theSubjectArea = new SubjectArea();
            deserializeProperties(theElement, theSubjectArea);

            theSubjectArea.setColor(new Color(Integer.parseInt(theElement.getAttribute(COLOR))));

            NodeList theTables = theElement.getElementsByTagName(ITEM);
            for (int j = 0; j < theTables.getLength(); j++) {

                Element theItemElement = (Element) theTables.item(j);
                String theTableId = theItemElement.getAttribute(TABLEREFID);

                Table theTable = aModel.getTables().findBySystemId(theTableId);
                if (theTable == null) {
                    throw new IllegalArgumentException("Cannot find table with id " + theTableId);
                }

                theSubjectArea.getTables().add(theTable);
            }

            aModel.getSubjectAreas().add(theSubjectArea);
        }

    }
}

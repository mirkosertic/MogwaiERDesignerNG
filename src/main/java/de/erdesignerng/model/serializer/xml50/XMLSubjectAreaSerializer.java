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
package de.erdesignerng.model.serializer.xml50;

import de.erdesignerng.model.*;
import java.awt.Color;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author $Author: dr-death2 $
 * @version $Date: 2011-10-08 20:00:00 $
 */
public class XMLSubjectAreaSerializer extends de.erdesignerng.model.serializer.xml20.XMLSubjectAreaSerializer {

    @Override
    public void serialize(SubjectArea aArea, Document aDocument, Element aRootElement) {

        Element theSubjectAreaElement = addElement(aDocument, aRootElement, SUBJECTAREA);

        // Basisdaten des Modelelementes speichern
        serializeProperties(aDocument, theSubjectAreaElement, aArea);
        theSubjectAreaElement.setAttribute(COLOR, "" + aArea.getColor().getRGB());
        setBooleanAttribute(theSubjectAreaElement, VISIBLE, aArea.isVisible());
        setBooleanAttribute(theSubjectAreaElement, EXPANDED, aArea.isExpanded());

        for (Table theTable : aArea.getTables()) {
            Element theTableElement = addElement(aDocument, theSubjectAreaElement, ITEM);
            theTableElement.setAttribute(TABLEREFID, theTable.getSystemId());
        }

        for (View theView : aArea.getViews()) {
            Element theViewElement = addElement(aDocument, theSubjectAreaElement, ITEM);
            theViewElement.setAttribute(VIEWREFID, theView.getSystemId());
        }

        for (Comment theComment : aArea.getComments()) {
            Element theCommentElement = addElement(aDocument, theSubjectAreaElement, ITEM);
            theCommentElement.setAttribute(COMMENTREFID, theComment.getSystemId());
        }
    }

    @Override
    public void deserialize(Model aModel, Document aDocument) {

        NodeList theElements = aDocument.getElementsByTagName(SUBJECTAREA);
        for (int i = 0; i < theElements.getLength(); i++) {
            Element theElement = (Element) theElements.item(i);

            SubjectArea theSubjectArea = new SubjectArea();
            deserializeProperties(theElement, theSubjectArea);

            theSubjectArea.setColor(new Color(Integer.parseInt(theElement.getAttribute(COLOR))));
            if (theElement.hasAttribute(VISIBLE)) {
                theSubjectArea.setVisible(TRUE.equals(theElement.getAttribute(VISIBLE)));
            }

            if (theElement.hasAttribute(EXPANDED)) {
                theSubjectArea.setExpanded(TRUE.equals(theElement.getAttribute(EXPANDED)));
            }

            NodeList theTables = theElement.getElementsByTagName(ITEM);
            for (int j = 0; j < theTables.getLength(); j++) {

                Element theItemElement = (Element) theTables.item(j);
                String theTableId = theItemElement.getAttribute(TABLEREFID);
                String theViewId = theItemElement.getAttribute(VIEWREFID);
                String theCommentId = theItemElement.getAttribute(COMMENTREFID);

                if (!StringUtils.isEmpty(theTableId)) {
                    Table theTable = aModel.getTables().findBySystemId(theTableId);
                    if (theTable == null) {
                        throw new IllegalArgumentException("Cannot find table with id " + theTableId);
                    }

                    theSubjectArea.getTables().add(theTable);
                }

                if (!StringUtils.isEmpty(theViewId)) {
                    View theView = aModel.getViews().findBySystemId(theViewId);
                    if (theView == null) {
                        throw new IllegalArgumentException("Cannot find view with id " + theViewId);
                    }

                    theSubjectArea.getViews().add(theView);
                }

                if (!StringUtils.isEmpty(theCommentId)) {
                    Comment theComment = aModel.getComments().findBySystemId(theCommentId);
                    if (theComment == null) {
                        throw new IllegalArgumentException("Cannot find comment with id " + theCommentId);
                    }

                    theSubjectArea.getComments().add(theComment);
                }
            }

            aModel.getSubjectAreas().add(theSubjectArea);
        }
    }
}
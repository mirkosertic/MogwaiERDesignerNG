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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.erdesignerng.model.Comment;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.serializer.XMLSerializer;

/**
 * @author $Author: dr-death $
 * @version $Date: 2009-10-21 10:00:00 $
 */
public class XMLCommentSerializer extends XMLSerializer {

    public static final XMLCommentSerializer SERIALIZER = new XMLCommentSerializer();

    public static final String COMMENT = "ModelComment";

    public void serialize(Comment aComment, Document aDocument, Element aRootElement) {

        Element theSubjectAreaElement = addElement(aDocument, aRootElement, COMMENT);

        // Basisdaten des Modelelementes speichern
        serializeProperties(aDocument, theSubjectAreaElement, aComment);
        serializeCommentElement(aDocument, theSubjectAreaElement, aComment);
    }

    public void deserializeFrom(Model aModel, Document aDocument) {

        NodeList theElements = aDocument.getElementsByTagName(COMMENT);
        for (int i = 0; i < theElements.getLength(); i++) {
            Element theElement = (Element) theElements.item(i);

            Comment theComment = new Comment();
            theComment.setOwner(aModel);
            deserializeProperties(theElement, theComment);
            deserializeCommentElement(theElement, theComment);

            aModel.getComments().add(theComment);
        }
    }
}

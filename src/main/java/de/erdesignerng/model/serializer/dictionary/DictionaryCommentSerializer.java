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
package de.erdesignerng.model.serializer.dictionary;

import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;

import de.erdesignerng.model.Comment;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.serializer.dictionary.entities.CommentEntity;
import de.erdesignerng.model.serializer.dictionary.entities.DictionaryEntity;
import de.erdesignerng.model.serializer.dictionary.entities.ModelEntity;

/**
 * Serializer for comments.
 * 
 * @author msertic
 */
public class DictionaryCommentSerializer extends DictionaryBaseSerializer {

    public static final DictionaryCommentSerializer SERIALIZER = new DictionaryCommentSerializer();

    public void serialize(Model aModel, Session aSession, DictionaryEntity aDictionaryEntity) {

        Map<String, ModelEntity> theComments = deletedRemovedInstances(aModel.getComments(), aDictionaryEntity.getComments());
        
        for (Comment theComment : aModel.getComments()) {
            boolean existing = true;
            CommentEntity theExisting = (CommentEntity) theComments.get(theComment.getSystemId());
            if (theExisting == null) {
                theExisting = new CommentEntity();
                existing = false;
            }

            copyBaseAttributes(theComment, theExisting);

            if (!existing) {
                aDictionaryEntity.getComments().add(theExisting);
            }
        }
    }

    public void deserialize(Model aModel, Session aSession) {
        Criteria theCriteria = aSession.createCriteria(CommentEntity.class);
        for (Object theObject : theCriteria.list()) {
            CommentEntity theCommentEntity = (CommentEntity) theObject;

            Comment theComment = new Comment();
            theComment.setOwner(aModel);
            
            copyBaseAttributes(theCommentEntity, theComment);

            aModel.getComments().add(theComment);
        }
    }
}
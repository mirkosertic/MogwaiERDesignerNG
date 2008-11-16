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
package de.erdesignerng.model.serializer.repository;

import java.awt.Color;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;

import de.erdesignerng.model.Comment;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.SubjectArea;
import de.erdesignerng.model.Table;
import de.erdesignerng.model.serializer.repository.entities.RepositoryEntity;
import de.erdesignerng.model.serializer.repository.entities.ModelEntity;
import de.erdesignerng.model.serializer.repository.entities.SubjectAreaEntity;

/**
 * Serializer for comments.
 * 
 * @author msertic
 */
public class DictionarySubjectAreaSerializer extends DictionaryBaseSerializer {

    public static final DictionarySubjectAreaSerializer SERIALIZER = new DictionarySubjectAreaSerializer();

    protected void copyExtendedAttributes(SubjectArea aSource, SubjectAreaEntity aDestination) {

        aDestination.setColor(aSource.getColor().getRGB());

        aDestination.getTables().clear();
        for (Table theTable : aSource.getTables()) {
            aDestination.getTables().add(theTable.getSystemId());
        }

        aDestination.getComments().clear();
        for (Comment theComment : aSource.getComments()) {
            aDestination.getComments().add(theComment.getSystemId());
        }

    }

    protected void copyExtendedAttributes(SubjectAreaEntity aSource, SubjectArea aDestination, Model aModel) {

        aDestination.setColor(new Color(aSource.getColor()));

        aDestination.getTables().clear();
        for (String theTable : aSource.getTables()) {
            aDestination.getTables().add(aModel.getTables().findBySystemId(theTable));
        }

        aDestination.getComments().clear();
        for (String theComment : aSource.getComments()) {
            aDestination.getComments().add(aModel.getComments().findBySystemId(theComment));
        }
    }

    public void serialize(Model aModel, Session aSession, RepositoryEntity aDictionaryEntity) {

        Map<String, ModelEntity> theComments = deletedRemovedInstances(aModel.getSubjectAreas(), aDictionaryEntity
                .getSubjectareas());

        for (SubjectArea theSubjectArea : aModel.getSubjectAreas()) {
            boolean existing = true;
            SubjectAreaEntity theExisting = (SubjectAreaEntity) theComments.get(theSubjectArea.getSystemId());
            if (theExisting == null) {
                theExisting = new SubjectAreaEntity();
                existing = false;
            }

            copyBaseAttributes(theSubjectArea, theExisting);
            copyExtendedAttributes(theSubjectArea, theExisting);

            if (!existing) {
                aDictionaryEntity.getSubjectareas().add(theExisting);
            }
        }
    }

    public void deserialize(Model aModel, Session aSession) {
        Criteria theCriteria = aSession.createCriteria(SubjectAreaEntity.class);

        for (Object theObject : theCriteria.list()) {
            SubjectAreaEntity theEntity = (SubjectAreaEntity) theObject;

            SubjectArea theSubjectArea = new SubjectArea();

            copyBaseAttributes(theEntity, theSubjectArea);
            copyExtendedAttributes(theEntity, theSubjectArea, aModel);

            aModel.getSubjectAreas().add(theSubjectArea);
        }
    }
}
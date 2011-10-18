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

import de.erdesignerng.model.*;
import de.erdesignerng.model.serializer.repository.entities.ModelEntity;
import de.erdesignerng.model.serializer.repository.entities.RepositoryEntity;
import de.erdesignerng.model.serializer.repository.entities.SubjectAreaEntity;
import java.awt.Color;
import java.util.Map;

/**
 * Serializer for comments.
 *
 * @author mirkosertic
 */
public class DictionarySubjectAreaSerializer extends DictionaryBaseSerializer {

    public static final DictionarySubjectAreaSerializer SERIALIZER = new DictionarySubjectAreaSerializer();

    private void copyExtendedAttributes(SubjectArea aSource, SubjectAreaEntity aDestination) {

        aDestination.setColor(aSource.getColor().getRGB());
        aDestination.setVisible(aSource.isVisible());
        aDestination.setExpanded(aSource.isExpanded());

        aDestination.getTables().clear();
        for (Table theTable : aSource.getTables()) {
            aDestination.getTables().add(theTable.getSystemId());
        }

        aDestination.getComments().clear();
        for (Comment theComment : aSource.getComments()) {
            aDestination.getComments().add(theComment.getSystemId());
        }

        aDestination.getViews().clear();
        for (View theView : aSource.getViews()) {
            aDestination.getViews().add(theView.getSystemId());
        }

    }

    private void copyExtendedAttributes(SubjectAreaEntity aSource, SubjectArea aDestination, Model aModel) {

        aDestination.setColor(new Color(aSource.getColor()));
        if (aSource.getVisible() != null) {
            aDestination.setVisible(aSource.getVisible());
        }

        if (aSource.getExpanded() != null) {
            aDestination.setExpanded(aSource.getExpanded());
        }

        aDestination.getTables().clear();
        for (String theTable : aSource.getTables()) {
            aDestination.getTables().add(aModel.getTables().findBySystemId(theTable));
        }

        aDestination.getComments().clear();
        for (String theComment : aSource.getComments()) {
            aDestination.getComments().add(aModel.getComments().findBySystemId(theComment));
        }

        aDestination.getViews().clear();
        for (String theView : aSource.getViews()) {
            aDestination.getViews().add(aModel.getViews().findBySystemId(theView));
        }
    }

    public void serialize(Model aModel, RepositoryEntity aDictionaryEntity) {

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

    public void deserialize(Model aModel, RepositoryEntity aRepositoryEntity) {
        for (SubjectAreaEntity theEntity : aRepositoryEntity.getSubjectareas()) {

            SubjectArea theSubjectArea = new SubjectArea();

            copyBaseAttributes(theEntity, theSubjectArea);
            copyExtendedAttributes(theEntity, theSubjectArea, aModel);

            aModel.getSubjectAreas().add(theSubjectArea);
        }
    }
}
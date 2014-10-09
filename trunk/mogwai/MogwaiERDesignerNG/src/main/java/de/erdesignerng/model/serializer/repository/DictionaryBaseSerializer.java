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

import de.erdesignerng.model.ModelItem;
import de.erdesignerng.model.ModelList;
import de.erdesignerng.model.serializer.repository.entities.ModelEntity;
import de.erdesignerng.model.serializer.repository.entities.StringKeyValuePair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Base class for all serializers.
 *
 * @author mirkosertic
 */
public abstract class DictionaryBaseSerializer {

    protected void copyBaseAttributes(ModelItem aSource, ModelEntity aDestination) {
        aDestination.setSystemId(aSource.getSystemId());
        aDestination.setName(aSource.getName());
        aDestination.setComment(aSource.getComment());

        aDestination.getProperties().clear();
        for (Map.Entry<String, String> theEntry : aSource.getProperties().getProperties().entrySet()) {
            StringKeyValuePair theElement = new StringKeyValuePair();
            theElement.setKey(theEntry.getKey());
            theElement.setValue(theEntry.getValue());
            aDestination.getProperties().add(theElement);
        }
    }

    protected void copyBaseAttributes(ModelEntity aSource, ModelItem aDestination) {
        aDestination.setSystemId(aSource.getSystemId());
        aDestination.setName(aSource.getName());
        aDestination.setComment(aSource.getComment());

        aDestination.getProperties().getProperties().clear();
        for (StringKeyValuePair theElement : aSource.getProperties()) {
            aDestination.getProperties().getProperties().put(theElement.getKey(), theElement.getValue());
        }
    }

    protected Map<String, ModelEntity> deletedRemovedInstances(ModelList aItems, List aModelEntities) {

        Set<ModelEntity> theRemovedModelInstances = new HashSet<>();

        Map<String, ModelEntity> theModelInstances = new HashMap<>();
        for (Object theObject : aModelEntities) {
            ModelEntity theCommentEntity = (ModelEntity) theObject;
            Object theExistingEntity = aItems.findBySystemId(theCommentEntity.getSystemId());
            if (theExistingEntity == null) {
                theRemovedModelInstances.add(theCommentEntity);
            } else {
                theModelInstances.put(theCommentEntity.getSystemId(), theCommentEntity);
            }
        }

        aModelEntities.removeAll(theRemovedModelInstances);

        return theModelInstances;
    }
}
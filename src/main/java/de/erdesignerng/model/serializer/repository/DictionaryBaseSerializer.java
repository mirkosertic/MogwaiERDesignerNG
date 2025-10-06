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

    protected void copyBaseAttributes(final ModelItem aSource, final ModelEntity aDestination) {
        aDestination.setSystemId(aSource.getSystemId());
        aDestination.setName(aSource.getName());
        aDestination.setComment(aSource.getComment());

        aDestination.getProperties().clear();
        for (final Map.Entry<String, String> theEntry : aSource.getProperties().getProperties().entrySet()) {
            final StringKeyValuePair theElement = new StringKeyValuePair();
            theElement.setKey(theEntry.getKey());
            theElement.setValue(theEntry.getValue());
            aDestination.getProperties().add(theElement);
        }
    }

    protected void copyBaseAttributes(final ModelEntity aSource, final ModelItem aDestination) {
        aDestination.setSystemId(aSource.getSystemId());
        aDestination.setName(aSource.getName());
        aDestination.setComment(aSource.getComment());

        aDestination.getProperties().getProperties().clear();
        for (final StringKeyValuePair theElement : aSource.getProperties()) {
            aDestination.getProperties().getProperties().put(theElement.getKey(), theElement.getValue());
        }
    }

    protected Map<String, ModelEntity> deletedRemovedInstances(final ModelList aItems, final List aModelEntities) {

        final Set<ModelEntity> theRemovedModelInstances = new HashSet<>();

        final Map<String, ModelEntity> theModelInstances = new HashMap<>();
        for (final Object theObject : aModelEntities) {
            final ModelEntity theCommentEntity = (ModelEntity) theObject;
            final Object theExistingEntity = aItems.findBySystemId(theCommentEntity.getSystemId());
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
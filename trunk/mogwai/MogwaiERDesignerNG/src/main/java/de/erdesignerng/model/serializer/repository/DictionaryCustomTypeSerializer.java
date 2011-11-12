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

import de.erdesignerng.model.CustomType;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.serializer.repository.entities.CustomTypeEntity;
import de.erdesignerng.model.serializer.repository.entities.ModelEntity;
import de.erdesignerng.model.serializer.repository.entities.RepositoryEntity;
import java.util.Map;
import org.hibernate.Session;

/**
 * Serializer for custom types.
 *
 * @author mirkosertic
 */
public class DictionaryCustomTypeSerializer extends DictionaryBaseSerializer {

    public static final DictionaryCustomTypeSerializer SERIALIZER = new DictionaryCustomTypeSerializer();

    private void copyExtendedAttributes(CustomType aSource, CustomTypeEntity aDestination) {
        aDestination.setSystemId(aSource.getSystemId());
        aDestination.setName(aSource.getName());
        aDestination.setSchema(aSource.getSchema());
        aDestination.setType(aSource.getType());
        aDestination.setAlias(aSource.getAlias());

        DictionaryAttributeSerializer.SERIALIZER.serialize(aSource, aDestination);
    }

    private void copyExtendedAttributes(Model aModel, CustomTypeEntity aSource, CustomType aDestination) {
        aDestination.setSystemId(aSource.getSystemId());
        aDestination.setName(aSource.getName());
        aDestination.setSchema(aSource.getSchema());
        aDestination.setAlias(aSource.getAlias());
        aDestination.setType(aSource.getType());

        DictionaryAttributeSerializer.SERIALIZER.deserialize(aModel, aDestination, aSource);
    }

    public void serialize(Model aModel, Session aSession, RepositoryEntity aDictionaryEntity) {

        Map<String, ModelEntity> theCustomTypes = deletedRemovedInstances(aModel.getDomains(), aDictionaryEntity.getCustomType());

        for (CustomType theType : aModel.getCustomTypes()) {
            boolean existing = true;
            CustomTypeEntity theExisting = (CustomTypeEntity) theCustomTypes.get(theType.getSystemId());
            if (theExisting == null) {
                theExisting = new CustomTypeEntity();
                existing = false;
            }

            copyBaseAttributes(theType, theExisting);
            copyExtendedAttributes(theType, theExisting);

            if (!existing) {
                aDictionaryEntity.getCustomType().add(theExisting);
            }
        }
    }

    public void deserialize(Model aModel, RepositoryEntity aRepositoryEntity) {
        for (CustomTypeEntity theEntity : aRepositoryEntity.getCustomType()) {

            CustomType theType = new CustomType();
            copyBaseAttributes(theEntity, theType);
            copyExtendedAttributes(aModel, theEntity, theType);

            aModel.getCustomTypes().add(theType);
        }
    }
}
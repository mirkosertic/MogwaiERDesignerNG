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
import de.erdesignerng.model.serializer.repository.entities.AttributeEntity;
import de.erdesignerng.model.serializer.repository.entities.AttributeEntityProvider;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.StringUtils;

/**
 * Serializer for attributes.
 *
 * @author mirkosertic
 */
public class DictionaryAttributeSerializer extends DictionaryBaseSerializer {

    public static final DictionaryAttributeSerializer SERIALIZER = new DictionaryAttributeSerializer();

    private <T extends ModelItem> void copyExtendedAttributes(Attribute<T> aSource, AttributeEntity aDestination) {
        aDestination.setDatatype(null);
        aDestination.setDomainId(null);
        if (aSource.getDatatype() != null) {
            if (!(aSource.getDatatype().isDomain())) {
                aDestination.setDatatype(aSource.getDatatype().getName());
            } else {
                Domain theDomain = (Domain) aSource.getDatatype();
                aDestination.setDomainId(theDomain.getSystemId());
            }
        } else {
            // in this case we are deserializing a custom type attribute
        }
        aDestination.setSize(aSource.getSize());
        aDestination.setFraction(aSource.getFraction());
        aDestination.setScale(aSource.getScale());
        aDestination.setNullable(aSource.isNullable());
        aDestination.setDefaultValue(aSource.getDefaultValue());
        aDestination.setExtra(aSource.getExtra());
    }

    private <T extends ModelItem> void copyExtendedAttributes(AttributeEntity aSource, Attribute<T> aDestination, Model aModel) {
        if (!StringUtils.isEmpty(aSource.getDomainId())) {
            aDestination.setDatatype(aModel.getDomains().findBySystemId(aSource.getDomainId()));
        } else {
            if (!StringUtils.isEmpty(aSource.getDatatype())) {
                aDestination.setDatatype(aModel.getAvailableDataTypes().findByName(aSource.getDatatype()));
            } else {
                // In this case we are deserializing a custom type attribute
                aDestination.setDatatype(null);
            }
        }
        aDestination.setSize(aSource.getSize());
        aDestination.setFraction(aSource.getFraction());
        aDestination.setScale(aSource.getScale());
        aDestination.setNullable(aSource.isNullable());
        aDestination.setDefaultValue(aSource.getDefaultValue());
        aDestination.setExtra(aSource.getExtra());
    }

    public <T extends ModelItem> void serialize(AttributeProvider<T> aTable, AttributeEntityProvider aTableEntity) {
        Set<AttributeEntity> theRemovedAttributes = new HashSet<>();

        Map<String, AttributeEntity> theAttributes = new HashMap<>();
        for (AttributeEntity theAttributeEntity : aTableEntity.getAttributes()) {
            Attribute<T> theAttribute = aTable.getAttributes().findBySystemId(theAttributeEntity.getSystemId());
            if (theAttribute == null) {
                theRemovedAttributes.add(theAttributeEntity);
            } else {
                theAttributes.put(theAttributeEntity.getSystemId(), theAttributeEntity);
            }
        }

        aTableEntity.getAttributes().removeAll(theRemovedAttributes);

        for (Attribute<T> theAttribute : aTable.getAttributes()) {
            boolean existing = true;
            AttributeEntity theEntity = theAttributes.get(theAttribute.getSystemId());
            if (theEntity == null) {
                theEntity = new AttributeEntity();
                existing = false;
            }

            copyBaseAttributes(theAttribute, theEntity);
            copyExtendedAttributes(theAttribute, theEntity);

            if (!existing) {
                aTableEntity.getAttributes().add(theEntity);
            }
        }

    }

    public <T extends ModelItem> void deserialize(Model aModel, AttributeProvider<T> aTable, AttributeEntityProvider aTableEntity) {
        for (AttributeEntity theAttributeEntity : aTableEntity.getAttributes()) {
            Attribute<T> theAttribute = aTable.createNewAttribute();

            copyBaseAttributes(theAttributeEntity, theAttribute);
            copyExtendedAttributes(theAttributeEntity, theAttribute, aModel);
        }
    }
}
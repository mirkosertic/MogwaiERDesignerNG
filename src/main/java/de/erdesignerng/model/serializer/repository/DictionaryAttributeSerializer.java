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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;

import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.Domain;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.Table;
import de.erdesignerng.model.serializer.repository.entities.AttributeEntity;
import de.erdesignerng.model.serializer.repository.entities.TableEntity;

/**
 * Serializer for attributes.
 * 
 * @author msertic
 */
public class DictionaryAttributeSerializer extends DictionaryBaseSerializer {

    public static final DictionaryAttributeSerializer SERIALIZER = new DictionaryAttributeSerializer();

    protected void copyExtendedAttributes(Attribute aSource, AttributeEntity aDestination) {
        aDestination.setDatatype(null);
        aDestination.setDomain(null);
        if (!(aSource.getDatatype().isDomain())) {
            aDestination.setDatatype(aSource.getDatatype().getName());
        } else {
            Domain theDomain = (Domain) aSource.getDatatype();
            aDestination.setDomain(theDomain.getSystemId());
        }
        aDestination.setSize(aSource.getSize());
        aDestination.setFraction(aSource.getFraction());
        aDestination.setScale(aSource.getScale());
        aDestination.setNullable(aSource.isNullable());
        aDestination.setDefaultValue(aSource.getDefaultValue());
        aDestination.setExtra(aSource.getExtra());
    }

    protected void copyExtendedAttributes(AttributeEntity aSource, Attribute aDestination, Model aModel) {

        if (!StringUtils.isEmpty(aSource.getDomain())) {
            aDestination.setDatatype(aModel.getDomains().findBySystemId(aSource.getDomain()));
        } else {
            aDestination.setDatatype(aModel.getDialect().getDataTypes().findByName(aSource.getDatatype()));
        }
        aDestination.setSize(aSource.getSize());
        aDestination.setFraction(aSource.getFraction());
        aDestination.setScale(aSource.getScale());
        aDestination.setNullable(aSource.isNullable());
        aDestination.setDefaultValue(aSource.getDefaultValue());
        aDestination.setExtra(aSource.getExtra());
    }

    public void serialize(Table aTable, TableEntity aTableEntity, Session aSession) {

        Set<AttributeEntity> theRemovedAttributes = new HashSet<AttributeEntity>();

        Map<String, AttributeEntity> theAttributes = new HashMap<String, AttributeEntity>();
        for (AttributeEntity theAttributeEntity : aTableEntity.getAttributes()) {
            Attribute theAttribute = aTable.getAttributes().findBySystemId(theAttributeEntity.getSystemId());
            if (theAttribute == null) {
                theRemovedAttributes.add(theAttributeEntity);
            } else {
                theAttributes.put(theAttributeEntity.getSystemId(), theAttributeEntity);
            }
        }

        aTableEntity.getAttributes().removeAll(theRemovedAttributes);

        for (Attribute theAttribute : aTable.getAttributes()) {
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

    public void deserialize(Model aModel, Table aTable, TableEntity aTableEntity) {
        for (AttributeEntity theAttributeEntity : aTableEntity.getAttributes()) {

            Attribute theAttribute = new Attribute();

            copyBaseAttributes(theAttributeEntity, theAttribute);
            copyExtendedAttributes(theAttributeEntity, theAttribute, aModel);

            theAttribute.setOwner(aTable);
            aTable.getAttributes().add(theAttribute);
        }
    }
}

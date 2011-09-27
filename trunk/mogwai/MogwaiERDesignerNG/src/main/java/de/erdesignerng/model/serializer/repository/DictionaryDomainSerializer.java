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

import de.erdesignerng.model.Domain;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.serializer.repository.entities.DomainEntity;
import de.erdesignerng.model.serializer.repository.entities.ModelEntity;
import de.erdesignerng.model.serializer.repository.entities.RepositoryEntity;

import java.util.Map;

/**
 * Serializer for domains.
 *
 * @author mirkosertic
 */
public class DictionaryDomainSerializer extends DictionaryBaseSerializer {

    public static final DictionaryDomainSerializer SERIALIZER = new DictionaryDomainSerializer();

    private void copyExtendedAttributes(Domain aSource, DomainEntity aDestination) {
        aDestination.setSystemId(aSource.getSystemId());
        aDestination.setName(aSource.getName());
        aDestination.setDatatype(aSource.getConcreteType().getName());
        aDestination.setSize(aSource.getSize());
        aDestination.setFraction(aSource.getFraction());
        aDestination.setScale(aSource.getScale());
        aDestination.setNullable(aSource.isNullable());
    }

    private void copyExtendedAttributes(DomainEntity aSource, Domain aDestination, Model aModel) {
        aDestination.setSystemId(aSource.getSystemId());
        aDestination.setName(aSource.getName());
        aDestination.setConcreteType(aModel.getDialect().getDataTypes().findByName(aSource.getDatatype()));
        aDestination.setSize(aSource.getSize());
        aDestination.setFraction(aSource.getFraction());
        aDestination.setScale(aSource.getScale());
        aDestination.setNullable(aSource.isNullable());
    }

    public void serialize(Model aModel, RepositoryEntity aDictionaryEntity) {

        Map<String, ModelEntity> theDomains = deletedRemovedInstances(aModel.getDomains(), aDictionaryEntity
                .getDomains());

        for (Domain theDomain : aModel.getDomains()) {
            boolean existing = true;
            DomainEntity theExisting = (DomainEntity) theDomains.get(theDomain.getSystemId());
            if (theExisting == null) {
                theExisting = new DomainEntity();
                existing = false;
            }

            copyBaseAttributes(theDomain, theExisting);
            copyExtendedAttributes(theDomain, theExisting);

            if (!existing) {
                aDictionaryEntity.getDomains().add(theExisting);
            }
        }
    }

    public void deserialize(Model aModel, RepositoryEntity aRepositoryEntity) {
        for (DomainEntity theEntity : aRepositoryEntity.getDomains()) {

            Domain theDomain = new Domain();
            copyBaseAttributes(theEntity, theDomain);
            copyExtendedAttributes(theEntity, theDomain, aModel);

            aModel.getDomains().add(theDomain);
        }
    }
}

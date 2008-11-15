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

import de.erdesignerng.model.Domain;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.serializer.dictionary.entities.DictionaryEntity;
import de.erdesignerng.model.serializer.dictionary.entities.DomainEntity;
import de.erdesignerng.model.serializer.dictionary.entities.ModelEntity;

/**
 * Serializer for domains.
 * 
 * @author msertic
 */
public class DictionaryDomainSerializer extends DictionaryBaseSerializer {

    public static final DictionaryDomainSerializer SERIALIZER = new DictionaryDomainSerializer();

    protected void copyBaseAttributes(Domain aSource, DomainEntity aDestination) {
        aDestination.setSystemId(aSource.getSystemId());
        aDestination.setName(aSource.getName());
        aDestination.setDatatype(aSource.getAttribute().getDatatype().getName());
        aDestination.setSize(aSource.getAttribute().getSize());
        aDestination.setFraction(aSource.getAttribute().getFraction());
        aDestination.setScale(aSource.getAttribute().getScale());
    }
    
    protected void copyBaseAttributes(DomainEntity aSource, Domain aDestination, Model aModel) {
        aDestination.setSystemId(aSource.getSystemId());
        aDestination.setName(aSource.getName());
        aDestination.getAttribute().setDatatype(aModel.getDialect().getDataTypes().findByName(aSource.getDatatype()));
        aDestination.getAttribute().setSize(aSource.getSize());
        aDestination.getAttribute().setFraction(aSource.getFraction());
        aDestination.getAttribute().setScale(aSource.getScale());
    }
    
    public void serialize(Model aModel, Session aSession, DictionaryEntity aDictionaryEntity) {

        Map<String, ModelEntity> theDomains = deletedRemovedInstances(aModel.getDomains(), aDictionaryEntity.getDomains());

        for (Domain theDomain : aModel.getDomains()) {
            boolean existing = true;
            DomainEntity theExisting = (DomainEntity) theDomains.get(theDomain.getSystemId());
            if (theExisting == null) {
                theExisting = new DomainEntity();
                existing = false;
            }

            copyBaseAttributes(theDomain, theExisting);
            
            if (!existing) {
                aDictionaryEntity.getDomains().add(theExisting);
            }
        }
    }

    public void deserialize(Model aModel, Session aSession) {
        Criteria theCriteria = aSession.createCriteria(DomainEntity.class);
        for (Object theObject : theCriteria.list()) {
            DomainEntity theEntity = (DomainEntity) theObject;
            
            Domain theDomain = new Domain();
            copyBaseAttributes(theEntity, theDomain, aModel);
            
            aModel.getDomains().add(theDomain);
        }
    }
}

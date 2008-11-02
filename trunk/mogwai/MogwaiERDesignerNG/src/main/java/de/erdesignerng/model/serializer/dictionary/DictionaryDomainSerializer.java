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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Session;

import de.erdesignerng.model.Domain;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.serializer.dictionary.entities.DomainEntity;

/**
 * Serializer for domains.
 * 
 * @author msertic
 */
public class DictionaryDomainSerializer extends DictionarySerializer {

    public static final DictionaryDomainSerializer SERIALIZER = new DictionaryDomainSerializer();

    protected void copyBaseAttributes(Domain aSource, DomainEntity aDestination) {
        aDestination.setSystemId(aSource.getSystemId());
        aDestination.setName(aSource.getName());
        aDestination.setDatatype(aSource.getAttribute().getDatatype().getName());
        aDestination.setSize(aSource.getAttribute().getSize());
        aDestination.setFraction(aSource.getAttribute().getFraction());
        aDestination.setScale(aSource.getAttribute().getScale());
    }
    
    public void serialize(Model aModel, Session aSession) {

        Set<DomainEntity> theRemovedDomains = new HashSet<DomainEntity>();
        
        Map<String, DomainEntity> theDomains = new HashMap<String, DomainEntity>();
        Criteria theCriteria = aSession.createCriteria(DomainEntity.class);
        for (Object theObject : theCriteria.list()) {
            DomainEntity theTableEntity = (DomainEntity) theObject;
            Domain theTable = aModel.getDomains().findBySystemId(theTableEntity.getSystemId());
            if (theTable == null) {
                theRemovedDomains.add(theTableEntity);
            } else {
                theDomains.put(theTableEntity.getSystemId(), theTableEntity);
            }
        }
        
        for (DomainEntity theRemovedTable : theRemovedDomains) {
            aSession.delete(theRemovedTable);
        }

        for (Domain theDomain : aModel.getDomains()) {
            boolean existing = true;
            DomainEntity theExisting = theDomains.get(theDomain.getSystemId());
            if (theExisting == null) {
                theExisting = new DomainEntity();
                existing = false;
            }

            copyBaseAttributes(theDomain, theExisting);
            
            if (existing) {
                aSession.update(theExisting);
            } else {
                aSession.save(theExisting);
            }
        }
    }
}

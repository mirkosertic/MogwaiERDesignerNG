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

import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.CascadeType;
import de.erdesignerng.model.Relation;
import de.erdesignerng.model.serializer.dictionary.entities.RelationEntity;

/**
 * Serializer for relations.
 * 
 * @author msertic
 */
public class DictionaryRelationSerializer extends DictionarySerializer {

    public static final DictionaryRelationSerializer SERIALIZER = new DictionaryRelationSerializer();

    protected int cascadeTypeToInt(CascadeType aType) {
        switch (aType) {
        case CASCADE:
            return 0;
        case SET_NULL:
            return 1;
        case NOTHING:
            return 2;
        default:
            return -1;
        }
    }

    public void serialize(Model aModel, Session aSession) {

        Set<RelationEntity> theRemovedRelations = new HashSet<RelationEntity>();

        Map<String, RelationEntity> theRelations = new HashMap<String, RelationEntity>();
        Criteria theCriteria = aSession.createCriteria(RelationEntity.class);
        for (Object theObject : theCriteria.list()) {
            RelationEntity theTableEntity = (RelationEntity) theObject;
            Relation theTable = aModel.getRelations().findBySystemId(theTableEntity.getSystemId());
            if (theTable == null) {
                theRemovedRelations.add(theTableEntity);
            } else {
                theRelations.put(theTableEntity.getSystemId(), theTableEntity);
            }
        }

        for (RelationEntity theEntry : theRemovedRelations) {
            aSession.delete(theEntry);
        }

        for (Relation theRelation : aModel.getRelations()) {
            boolean existing = true;
            RelationEntity theExisting = theRelations.get(theRelation.getSystemId());
            if (theExisting == null) {
                theExisting = new RelationEntity();
                existing = false;
            }

            copyBaseAttributes(theRelation, theExisting);

            theExisting.setImportingTable(theRelation.getImportingTable().getSystemId());
            theExisting.setExportingTable(theRelation.getExportingTable().getSystemId());
            theExisting.setOnUpdate(cascadeTypeToInt(theRelation.getOnUpdate()));
            theExisting.setOnDelete(cascadeTypeToInt(theRelation.getOnDelete()));

            theExisting.getMapping().clear();
            for (Map.Entry<Attribute, Attribute> theEntry : theRelation.getMapping().entrySet()) {
                theExisting.getMapping().put(theEntry.getKey().getSystemId(), theEntry.getValue().getSystemId());
            }

            if (existing) {
                aSession.update(theExisting);
            } else {
                aSession.save(theExisting);
            }
        }
    }
}

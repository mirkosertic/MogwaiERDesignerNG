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

import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.CascadeType;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.Relation;
import de.erdesignerng.model.serializer.dictionary.entities.ModelEntity;
import de.erdesignerng.model.serializer.dictionary.entities.RelationEntity;

/**
 * Serializer for relations.
 * 
 * @author msertic
 */
public class DictionaryRelationSerializer extends DictionarySerializer {

    public static final DictionaryRelationSerializer SERIALIZER = new DictionaryRelationSerializer();

    private static final int CASCADE_INT = 0;

    private static final int CASCADE_SETNULL = 1;

    private static final int CASCADE_NOTHING = 2;

    protected int cascadeTypeToInt(CascadeType aType) {
        switch (aType) {
        case CASCADE:
            return CASCADE_INT;
        case SET_NULL:
            return CASCADE_SETNULL;
        case NOTHING:
            return CASCADE_NOTHING;
        default:
            throw new RuntimeException("Unknown cascade type : " + aType);
        }
    }

    protected CascadeType intToCascadeType(int aType) {
        switch (aType) {
        case CASCADE_INT:
            return CascadeType.CASCADE;
        case CASCADE_SETNULL:
            return CascadeType.SET_NULL;
        case CASCADE_NOTHING:
            return CascadeType.NOTHING;
        default:
            throw new RuntimeException("Invalid cascade type : " + aType);
        }
    }

    private void copyExtendedAttributes(Relation aSource, RelationEntity aDestination) {
        aDestination.setImportingTable(aSource.getImportingTable().getSystemId());
        aDestination.setExportingTable(aSource.getExportingTable().getSystemId());
        aDestination.setOnUpdate(cascadeTypeToInt(aSource.getOnUpdate()));
        aDestination.setOnDelete(cascadeTypeToInt(aSource.getOnDelete()));

        aDestination.getMapping().clear();
        for (Map.Entry<Attribute, Attribute> theEntry : aSource.getMapping().entrySet()) {
            aDestination.getMapping().put(theEntry.getKey().getSystemId(), theEntry.getValue().getSystemId());
        }
    }

    private void copyExtendedAttributes(RelationEntity aSource, Relation aDestination, Model aModel) {
        aDestination.setImportingTable(aModel.getTables().findBySystemId(aSource.getImportingTable()));
        aDestination.setExportingTable(aModel.getTables().findBySystemId(aSource.getExportingTable()));
        aDestination.setOnUpdate(intToCascadeType(aSource.getOnUpdate()));
        aDestination.setOnDelete(intToCascadeType(aSource.getOnDelete()));

        aDestination.getMapping().clear();
        for (Map.Entry<String, String> theEntry : aSource.getMapping().entrySet()) {
            Attribute theAt1 = aDestination.getExportingTable().getAttributes().findBySystemId(theEntry.getKey());
            Attribute theAt2 = aDestination.getImportingTable().getAttributes().findBySystemId(theEntry.getValue());
            if (theAt1 == null) {
                throw new RuntimeException("Cannot find attribute" + theEntry.getKey());
            }

            if (theAt2 == null) {
                throw new RuntimeException("Cannot find attribute" + theEntry.getValue());
            }

            aDestination.getMapping().put(theAt1, theAt2);
        }
    }

    public void serialize(Model aModel, Session aSession) {

        Map<String, ModelEntity> theRelations = deletedRemovedInstances(aModel.getRelations(), RelationEntity.class, aSession); 

        for (Relation theRelation : aModel.getRelations()) {
            boolean existing = true;
            RelationEntity theExisting = (RelationEntity) theRelations.get(theRelation.getSystemId());
            if (theExisting == null) {
                theExisting = new RelationEntity();
                existing = false;
            }

            copyBaseAttributes(theRelation, theExisting);

            copyExtendedAttributes(theRelation, theExisting);

            if (existing) {
                aSession.update(theExisting);
            } else {
                aSession.save(theExisting);
            }
        }
    }

    public void deserialize(Model aModel, Session aSession) {
        Criteria theCriteria = aSession.createCriteria(RelationEntity.class);
        for (Object theObject : theCriteria.list()) {
            RelationEntity theRelationEntity = (RelationEntity) theObject;

            Relation theRelation = new Relation();
            theRelation.setOwner(aModel);

            copyBaseAttributes(theRelationEntity, theRelation);

            copyExtendedAttributes(theRelationEntity, theRelation, aModel);

            aModel.getRelations().add(theRelation);
        }
    }
}

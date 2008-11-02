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

import org.hibernate.Session;

import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.Index;
import de.erdesignerng.model.Table;
import de.erdesignerng.model.serializer.dictionary.entities.IndexEntity;
import de.erdesignerng.model.serializer.dictionary.entities.TableEntity;

/**
 * Serializer for indexes.
 * 
 * @author msertic
 */
public class DictionaryIndexSerializer extends DictionarySerializer {

    public static final DictionaryIndexSerializer SERIALIZER = new DictionaryIndexSerializer();

    protected void copyExtendedAttributes(Index aSource, IndexEntity aDestination) {
        switch (aSource.getIndexType()) {
        case UNIQUE:
            aDestination.setType(0);
            break;
        case NONUNIQUE:
            aDestination.setType(1);
            break;
        case PRIMARYKEY:
            aDestination.setType(2);
            break;
        default:
            aDestination.setType(-1);
        }

        aDestination.getAttributes().clear();
        for (Attribute theAttribute : aSource.getAttributes()) {
            aDestination.getAttributes().add(theAttribute.getSystemId());
        }
    }

    public void serialize(Table aTable, TableEntity aTableEntity, Session aSession) {

        Set<IndexEntity> theRemovedIndexes = new HashSet<IndexEntity>();
        Map<String, IndexEntity> theIndexes = new HashMap<String, IndexEntity>();

        for (IndexEntity theIndexEntity : aTableEntity.getIndexes()) {
            Index theAttribute = aTable.getIndexes().findBySystemId(theIndexEntity.getSystemId());
            if (theAttribute == null) {
                theRemovedIndexes.add(theIndexEntity);
            } else {
                theIndexes.put(theIndexEntity.getSystemId(), theIndexEntity);
            }
        }

        aTableEntity.getIndexes().remove(theRemovedIndexes);

        for (Index theIndex : aTable.getIndexes()) {
            boolean existing = true;
            IndexEntity theEntity = theIndexes.get(theIndex.getSystemId());
            if (theEntity == null) {
                theEntity = new IndexEntity();
                existing = false;
            }

            copyBaseAttributes(theIndex, theEntity);
            copyExtendedAttributes(theIndex, theEntity);

            if (existing) {
                aSession.update(theEntity);
            } else {
                aTableEntity.getIndexes().add(theEntity);
                aSession.save(theEntity);
            }
        }

    }
}

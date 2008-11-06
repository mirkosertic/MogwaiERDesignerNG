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

import de.erdesignerng.model.Model;
import de.erdesignerng.model.Table;
import de.erdesignerng.model.serializer.dictionary.entities.TableEntity;

/**
 * Serializer for tables.
 * 
 * @author msertic
 */
public class DictionaryTableSerializer extends DictionarySerializer {

    public static final DictionaryTableSerializer SERIALIZER = new DictionaryTableSerializer();

    public void serialize(Model aModel, Session aSession) {

        Set<TableEntity> theRemovedTables = new HashSet<TableEntity>();

        Map<String, TableEntity> theTables = new HashMap<String, TableEntity>();
        Criteria theCriteria = aSession.createCriteria(TableEntity.class);
        for (Object theObject : theCriteria.list()) {
            TableEntity theTableEntity = (TableEntity) theObject;
            Table theTable = aModel.getTables().findBySystemId(theTableEntity.getSystemId());
            if (theTable == null) {
                theRemovedTables.add(theTableEntity);
            } else {
                theTables.put(theTableEntity.getSystemId(), theTableEntity);
            }
        }

        for (TableEntity theRemovedTable : theRemovedTables) {
            aSession.delete(theRemovedTable);
        }

        for (Table theTable : aModel.getTables()) {
            boolean existing = true;
            TableEntity theExisting = theTables.get(theTable.getSystemId());
            if (theExisting == null) {
                theExisting = new TableEntity();
                existing = false;
            }

            copyBaseAttributes(theTable, theExisting);

            DictionaryAttributeSerializer.SERIALIZER.serialize(theTable, theExisting, aSession);

            DictionaryIndexSerializer.SERIALIZER.serialize(theTable, theExisting, aSession);

            if (existing) {
                aSession.update(theExisting);
            } else {
                aSession.save(theExisting);
            }
        }
    }

    public void deserialize(Model aModel, Session aSession) {
        Criteria theCriteria = aSession.createCriteria(TableEntity.class);
        for (Object theObject : theCriteria.list()) {
            TableEntity theTableEntity = (TableEntity) theObject;

            Table theTable = new Table();

            copyBaseAttributes(theTableEntity, theTable);

            DictionaryAttributeSerializer.SERIALIZER.deserialize(aModel, theTable, theTableEntity, aSession);

            // DictionaryIndexSerializer.SERIALIZER.serialize(theTable,
            // theExisting, aSession);

            theTable.setOwner(aModel);
            aModel.getTables().add(theTable);
        }
    }
}

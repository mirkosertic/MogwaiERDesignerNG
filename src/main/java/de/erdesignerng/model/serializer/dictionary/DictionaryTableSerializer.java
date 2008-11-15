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

import de.erdesignerng.model.Model;
import de.erdesignerng.model.Table;
import de.erdesignerng.model.serializer.dictionary.entities.DictionaryEntity;
import de.erdesignerng.model.serializer.dictionary.entities.ModelEntity;
import de.erdesignerng.model.serializer.dictionary.entities.TableEntity;

/**
 * Serializer for tables.
 * 
 * @author msertic
 */
public class DictionaryTableSerializer extends DictionaryBaseSerializer {

    public static final DictionaryTableSerializer SERIALIZER = new DictionaryTableSerializer();

    public void serialize(Model aModel, Session aSession, DictionaryEntity aDictionary) {

        Map<String, ModelEntity> theTables = deletedRemovedInstances(aModel.getTables(), aDictionary.getTables());
        
        for (Table theTable : aModel.getTables()) {
            boolean existing = true;
            TableEntity theExisting = (TableEntity) theTables.get(theTable.getSystemId());
            if (theExisting == null) {
                theExisting = new TableEntity();
                existing = false;
            }

            copyBaseAttributes(theTable, theExisting);

            DictionaryAttributeSerializer.SERIALIZER.serialize(theTable, theExisting, aSession);

            DictionaryIndexSerializer.SERIALIZER.serialize(theTable, theExisting, aSession);

            if (!existing) {
                aDictionary.getTables().add(theExisting);
            }
        }
    }

    public void deserialize(Model aModel, Session aSession) {
        Criteria theCriteria = aSession.createCriteria(TableEntity.class);
        for (Object theObject : theCriteria.list()) {
            TableEntity theTableEntity = (TableEntity) theObject;

            Table theTable = new Table();
            theTable.setOwner(aModel);
            
            copyBaseAttributes(theTableEntity, theTable);

            DictionaryAttributeSerializer.SERIALIZER.deserialize(aModel, theTable, theTableEntity, aSession);

            DictionaryIndexSerializer.SERIALIZER.deserialize(aModel, theTable, theTableEntity, aSession);

            aModel.getTables().add(theTable);
        }
    }
}

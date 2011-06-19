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

import de.erdesignerng.model.Model;
import de.erdesignerng.model.Table;
import de.erdesignerng.model.serializer.repository.entities.ModelEntity;
import de.erdesignerng.model.serializer.repository.entities.RepositoryEntity;
import de.erdesignerng.model.serializer.repository.entities.TableEntity;
import org.hibernate.Session;

import java.util.Map;

/**
 * Serializer for tables.
 *
 * @author mirkosertic
 */
public class DictionaryTableSerializer extends DictionaryBaseSerializer {

	public static final DictionaryTableSerializer SERIALIZER = new DictionaryTableSerializer();

	public void serialize(Model aModel, Session aSession, RepositoryEntity aDictionary) {

		Map<String, ModelEntity> theTables = deletedRemovedInstances(aModel.getTables(), aDictionary.getTables());

		for (Table theTable : aModel.getTables()) {
			boolean existing = true;
			TableEntity theExisting = (TableEntity) theTables.get(theTable.getSystemId());
			if (theExisting == null) {
				theExisting = new TableEntity();
				existing = false;
			}

			copyBaseAttributes(theTable, theExisting);

			theExisting.setSchema(theTable.getSchema());

			DictionaryAttributeSerializer.SERIALIZER.serialize(theTable, theExisting);

			DictionaryIndexSerializer.SERIALIZER.serialize(theTable, theExisting);

			if (!existing) {
				aDictionary.getTables().add(theExisting);
			}
		}
	}

	public void deserialize(Model aModel, RepositoryEntity aRepositoryEntity) {
		for (TableEntity theTableEntity : aRepositoryEntity.getTables()) {

			Table theTable = new Table();
			theTable.setOwner(aModel);

			copyBaseAttributes(theTableEntity, theTable);

			theTable.setSchema(theTableEntity.getSchema());

			DictionaryAttributeSerializer.SERIALIZER.deserialize(aModel, theTable, theTableEntity);

			DictionaryIndexSerializer.SERIALIZER.deserialize(aModel, theTable, theTableEntity);

			aModel.getTables().add(theTable);
		}
	}
}

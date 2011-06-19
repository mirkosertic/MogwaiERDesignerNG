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
import de.erdesignerng.model.View;
import de.erdesignerng.model.serializer.repository.entities.ModelEntity;
import de.erdesignerng.model.serializer.repository.entities.RepositoryEntity;
import de.erdesignerng.model.serializer.repository.entities.ViewEntity;

import java.util.Map;

/**
 * Serializer for views.
 *
 * @author mirkosertic
 */
public class DictionaryViewSerializer extends DictionaryBaseSerializer {

	public static final DictionaryViewSerializer SERIALIZER = new DictionaryViewSerializer();

	public void serialize(Model aModel, RepositoryEntity aDictionary) {

		Map<String, ModelEntity> theViews = deletedRemovedInstances(aModel.getViews(), aDictionary.getViews());

		for (View theView : aModel.getViews()) {
			boolean existing = true;
			ViewEntity theExisting = (ViewEntity) theViews.get(theView.getSystemId());
			if (theExisting == null) {
				theExisting = new ViewEntity();
				existing = false;
			}

			copyBaseAttributes(theView, theExisting);

			theExisting.setSqlstatement(theView.getSql());
			theExisting.setSchema(theView.getSchema());

			if (!existing) {
				aDictionary.getViews().add(theExisting);
			}
		}
	}

	public void deserialize(Model aModel, RepositoryEntity aRepositoryEntity) {
		for (ViewEntity theViewEntity : aRepositoryEntity.getViews()) {

			View theView = new View();
			theView.setOwner(aModel);

			copyBaseAttributes(theViewEntity, theView);

			theView.setSql(theViewEntity.getSqlstatement());
			theView.setSchema(theViewEntity.getSchema());

			aModel.getViews().add(theView);
		}
	}
}

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

import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.CascadeType;
import de.erdesignerng.model.Index;
import de.erdesignerng.model.IndexExpression;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.Relation;
import de.erdesignerng.model.Table;
import de.erdesignerng.model.serializer.repository.entities.ModelEntity;
import de.erdesignerng.model.serializer.repository.entities.RelationEntity;
import de.erdesignerng.model.serializer.repository.entities.RepositoryEntity;
import de.erdesignerng.model.serializer.repository.entities.StringKeyValuePair;

import java.util.Map;

/**
 * Serializer for relations.
 *
 * @author mirkosertic
 */
public class DictionaryRelationSerializer extends DictionaryBaseSerializer {

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
		for (Map.Entry<IndexExpression, Attribute<Table>> theEntry : aSource.getMapping().entrySet()) {
			StringKeyValuePair theMappingEntry = new StringKeyValuePair();
			theMappingEntry.setKey(theEntry.getKey().getSystemId());
			theMappingEntry.setValue(theEntry.getValue().getSystemId());
			aDestination.getMapping().add(theMappingEntry);
		}
	}

	private void copyExtendedAttributes(RelationEntity aSource, Relation aDestination, Model aModel) {
		aDestination.setImportingTable(aModel.getTables().findBySystemId(aSource.getImportingTable()));
		aDestination.setExportingTable(aModel.getTables().findBySystemId(aSource.getExportingTable()));
		aDestination.setOnUpdate(intToCascadeType(aSource.getOnUpdate()));
		aDestination.setOnDelete(intToCascadeType(aSource.getOnDelete()));

		aDestination.getMapping().clear();
		Index thePrimaryKey = aDestination.getExportingTable().getPrimarykey();

		for (StringKeyValuePair theEntry : aSource.getMapping()) {
			IndexExpression theExpression = thePrimaryKey.getExpressions().findBySystemId(theEntry.getKey());
			if (theExpression == null) {
				throw new RuntimeException("Cannot find index expression" + theEntry.getKey());
			}

			Attribute<Table> theImportingAttribute = aDestination.getImportingTable().getAttributes().findBySystemId(
					theEntry.getValue());

			if (theImportingAttribute == null) {
				throw new RuntimeException("Cannot find attribute" + theEntry.getValue());
			}

			aDestination.getMapping().put(theExpression, theImportingAttribute);
		}
	}

	public void serialize(Model aModel, RepositoryEntity aDictionary) {

		Map<String, ModelEntity> theRelations = deletedRemovedInstances(aModel.getRelations(), aDictionary
				.getRelations());

		for (Relation theRelation : aModel.getRelations()) {
			boolean existing = true;
			RelationEntity theExisting = (RelationEntity) theRelations.get(theRelation.getSystemId());
			if (theExisting == null) {
				theExisting = new RelationEntity();
				existing = false;
			}

			copyBaseAttributes(theRelation, theExisting);
			copyExtendedAttributes(theRelation, theExisting);

			if (!existing) {
				aDictionary.getRelations().add(theExisting);
			}
		}
	}

	public void deserialize(Model aModel, RepositoryEntity aRepositoryEntity) {
		for (RelationEntity theRelationEntity : aRepositoryEntity.getRelations()) {

			Relation theRelation = new Relation();
			theRelation.setOwner(aModel);

			copyBaseAttributes(theRelationEntity, theRelation);
			copyExtendedAttributes(theRelationEntity, theRelation, aModel);

			aModel.getRelations().add(theRelation);
		}
	}
}

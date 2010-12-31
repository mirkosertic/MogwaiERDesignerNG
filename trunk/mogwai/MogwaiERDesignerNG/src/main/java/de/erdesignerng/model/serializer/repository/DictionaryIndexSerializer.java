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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;

import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.Index;
import de.erdesignerng.model.IndexExpression;
import de.erdesignerng.model.IndexType;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.Table;
import de.erdesignerng.model.serializer.repository.entities.IndexEntity;
import de.erdesignerng.model.serializer.repository.entities.IndexExpressionEntity;
import de.erdesignerng.model.serializer.repository.entities.TableEntity;

/**
 * Serializer for indexes.
 * 
 * @author mirkosertic
 */
public class DictionaryIndexSerializer extends DictionaryBaseSerializer {

	private static final int TYPE_UNIQUE = 0;

	private static final int TYPE_NONUNIQUE = 1;

	private static final int TYPE_PK = 2;

	private static final int TYPE_UNDEFINED = -1;

	public static final DictionaryIndexSerializer SERIALIZER = new DictionaryIndexSerializer();

	protected void copyExtendedAttributes(Index aSource, IndexEntity aDestination) {
		switch (aSource.getIndexType()) {
		case UNIQUE:
			aDestination.setType(TYPE_UNIQUE);
			break;
		case NONUNIQUE:
			aDestination.setType(TYPE_NONUNIQUE);
			break;
		case PRIMARYKEY:
			aDestination.setType(TYPE_PK);
			break;
		default:
			aDestination.setType(TYPE_UNDEFINED);
		}

		aDestination.getExpressions().clear();
		for (IndexExpression theIndexExpression : aSource.getExpressions()) {
			IndexExpressionEntity theEntity = new IndexExpressionEntity();
			theEntity.setExpression(theIndexExpression.getExpression());
			theEntity.setSystemId(theIndexExpression.getSystemId());
			Attribute theRefAttribute = theIndexExpression.getAttributeRef();
			if (theRefAttribute != null) {
				theEntity.setAttributeId(theRefAttribute.getSystemId());
			}
			aDestination.getExpressions().add(theEntity);
		}
	}

	protected void copyExtendedAttributes(IndexEntity aSource, Index aDestination, Table aTable) {
		switch (aSource.getType()) {
		case TYPE_UNIQUE:
			aDestination.setIndexType(IndexType.UNIQUE);
			break;
		case TYPE_NONUNIQUE:
			aDestination.setIndexType(IndexType.NONUNIQUE);
			break;
		case TYPE_PK:
			aDestination.setIndexType(IndexType.PRIMARYKEY);
			break;
		default:
			throw new RuntimeException("Invalid index type : " + aSource.getType());
		}

		aDestination.getExpressions().clear();
		for (IndexExpressionEntity theExpressionEntity : aSource.getExpressions()) {
			IndexExpression theExpression = new IndexExpression();
			theExpression.setExpression(theExpressionEntity.getExpression());
			theExpression.setSystemId(theExpressionEntity.getSystemId());
			String theAttributeId = theExpressionEntity.getAttributeId();
			if (!StringUtils.isEmpty(theAttributeId)) {
				theExpression.setAttributeRef(aTable.getAttributes().findBySystemId(theAttributeId));
			}
			aDestination.getExpressions().add(theExpression);
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

			if (!existing) {
				aTableEntity.getIndexes().add(theEntity);
			}
		}
	}

	public void deserialize(Model aModel, Table aTable, TableEntity aTableEntity) {
		for (IndexEntity theIndexEntity : aTableEntity.getIndexes()) {
			Index theIndex = new Index();

			copyBaseAttributes(theIndexEntity, theIndex);
			copyExtendedAttributes(theIndexEntity, theIndex, aTable);

			theIndex.setOwner(aTable);
			aTable.getIndexes().add(theIndex);
		}
	}
}

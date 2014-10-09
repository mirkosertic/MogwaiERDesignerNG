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

import de.erdesignerng.dialect.DialectFactory;
import de.erdesignerng.dialect.Statement;
import de.erdesignerng.dialect.StatementList;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.ModelUtilities;
import de.erdesignerng.model.serializer.repository.entities.ChangeEntity;
import de.erdesignerng.model.serializer.repository.entities.RepositoryEntity;
import de.erdesignerng.model.serializer.repository.entities.StringKeyValuePair;
import de.erdesignerng.modificationtracker.HistoryModificationTracker;
import de.erdesignerng.modificationtracker.ModelModificationTracker;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Serializer to store the model in a data dictionary.
 *
 * @author mirkosertic
 */
public class DictionaryModelSerializer extends DictionaryBaseSerializer {

	public static final DictionaryModelSerializer SERIALIZER = new DictionaryModelSerializer();

	public RepositoryEntryDescriptor serialize(final RepositoryEntryDescriptor aDesc, final Model aModel,
											   Connection aConnection, Class aHibernateDialectClass) throws Exception {

		return (RepositoryEntryDescriptor) new HibernateTemplate(aHibernateDialectClass, aConnection) {

			@Override
			public Object doInSession(Session aSession) {
				RepositoryEntity theEntity;
				if (aDesc.getId() == null) {
					theEntity = new RepositoryEntity();
					theEntity.setSystemId(ModelUtilities.createSystemIdFor());
				} else {
					theEntity = (RepositoryEntity) aSession.get(RepositoryEntity.class, aDesc.getId());
				}

				theEntity.setName(aDesc.getName());
				theEntity.setDialect(aModel.getDialect().getUniqueName());

				// Serialize properties
				theEntity.getProperties().clear();
				for (Map.Entry<String, String> theEntry : aModel.getProperties().getProperties().entrySet()) {
					StringKeyValuePair theElement = new StringKeyValuePair();
					theElement.setKey(theEntry.getKey());
					theElement.setValue(theEntry.getValue());
					theEntity.getProperties().add(theElement);
				}

				// Serialize the rest
				DictionaryCustomTypeSerializer.SERIALIZER.serialize(aModel, aSession, theEntity);

				DictionaryDomainSerializer.SERIALIZER.serialize(aModel, theEntity);

				DictionaryTableSerializer.SERIALIZER.serialize(aModel, aSession, theEntity);

				DictionaryViewSerializer.SERIALIZER.serialize(aModel, theEntity);

				DictionaryRelationSerializer.SERIALIZER.serialize(aModel, theEntity);

				DictionaryCommentSerializer.SERIALIZER.serialize(aModel, theEntity);

				DictionarySubjectAreaSerializer.SERIALIZER.serialize(aModel, theEntity);

				// Serialize changes
				ModelModificationTracker theTracker = aModel.getModificationTracker();
				if (theTracker instanceof HistoryModificationTracker) {
					HistoryModificationTracker theHistTracker = (HistoryModificationTracker) theTracker;
					StatementList theList = theHistTracker.getNotSavedStatements();
					if (theList.size() > 0) {
						ChangeEntity theChange = new ChangeEntity();
						theChange.setSystemId(ModelUtilities.createSystemIdFor());
						for (Statement theStatement : theList) {
							theChange.getStatements().add(theStatement.getSql());
							theStatement.setSaved(true);
						}

						theEntity.getChanges().add(theChange);
					}
				}

				aSession.saveOrUpdate(theEntity);

				aDesc.setName(theEntity.getName());
				aDesc.setId(theEntity.getId());
				return aDesc;
			}

		}.execute();
	}

	public Model deserialize(final RepositoryEntryDescriptor aDescriptor, Connection aConnection,
							 Class aHibernateDialectClass) throws Exception {

		return (Model) new HibernateTemplate(aHibernateDialectClass, aConnection) {

			@Override
			public Object doInSession(Session aSession) {
				RepositoryEntity theRepositoryEntity = (RepositoryEntity) aSession.get(RepositoryEntity.class,
						aDescriptor.getId());

				Model theNewModel = new Model();

				// Deserialize the properties
				theNewModel.getProperties().getProperties().clear();
				for (StringKeyValuePair theElement : theRepositoryEntity.getProperties()) {
					theNewModel.getProperties().getProperties().put(theElement.getKey(), theElement.getValue());
				}
				theNewModel.setDialect(DialectFactory.getInstance().getDialect(theRepositoryEntity.getDialect()));

				// Deserialize the rest
				DictionaryCustomTypeSerializer.SERIALIZER.deserialize(theNewModel, theRepositoryEntity);

				DictionaryDomainSerializer.SERIALIZER.deserialize(theNewModel, theRepositoryEntity);

				DictionaryTableSerializer.SERIALIZER.deserialize(theNewModel, theRepositoryEntity);

				DictionaryViewSerializer.SERIALIZER.deserialize(theNewModel, theRepositoryEntity);

				DictionaryRelationSerializer.SERIALIZER.deserialize(theNewModel, theRepositoryEntity);

				DictionaryCommentSerializer.SERIALIZER.deserialize(theNewModel, theRepositoryEntity);

				DictionarySubjectAreaSerializer.SERIALIZER.deserialize(theNewModel, theRepositoryEntity);

				return theNewModel;
			}

		}.execute();
	}

	/**
	 * Get the available repository entries.
	 *
	 * @param aDialectClass the hibernate dialect class
	 * @param aConnection   the jdbc connection
	 * @return list of entries
	 * @throws Exception will be thrown in case of an exception
	 */
	public List<RepositoryEntryDescriptor> getRepositoryEntries(Class aDialectClass, Connection aConnection)
			throws Exception {
		return (List<RepositoryEntryDescriptor>) new HibernateTemplate(aDialectClass, aConnection) {
			@Override
			public Object doInSession(Session aSession) {
				List<RepositoryEntryDescriptor> theResult = new ArrayList<>();

				Criteria theCriteria = aSession.createCriteria(RepositoryEntity.class);
				theCriteria.setProjection(Projections.projectionList().add(Projections.property("id")).add(
						Projections.property("name")));
				theCriteria.addOrder(Order.asc("name"));

				for (Object theObject : theCriteria.list()) {
					Object[] theArray = (Object[]) theObject;

					RepositoryEntryDescriptor theEntry = new RepositoryEntryDescriptor();
					theEntry.setId((Long) theArray[0]);
					theEntry.setName((String) theArray[1]);
					theResult.add(theEntry);
				}
				return theResult;
			}
		}.execute();
	}

	/**
	 * Read a specific repository entity.
	 *
	 * @param aHibernateDialectClass  the hibernate dialect class
	 * @param aConnection			 the connection
	 * @param aCurrentRepositoryEntry the repository descriptor
	 * @return the entity
	 * @throws Exception will be thrown in case of an error
	 */
	public RepositoryEntity getRepositoryEntity(Class aHibernateDialectClass, Connection aConnection,
												final RepositoryEntryDescriptor aCurrentRepositoryEntry) throws Exception {
		return (RepositoryEntity) new HibernateTemplate(aHibernateDialectClass, aConnection) {

			@Override
			public Object doInSession(Session aSession) {
				return aSession.get(RepositoryEntity.class, aCurrentRepositoryEntry.getId());
			}

		}.execute();
	}
}
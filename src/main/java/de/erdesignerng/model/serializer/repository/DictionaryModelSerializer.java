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

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.criterion.Projections;

import de.erdesignerng.dialect.DialectFactory;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.ModelUtilities;
import de.erdesignerng.model.serializer.repository.entities.AttributeEntity;
import de.erdesignerng.model.serializer.repository.entities.ChangeEntity;
import de.erdesignerng.model.serializer.repository.entities.CommentEntity;
import de.erdesignerng.model.serializer.repository.entities.DomainEntity;
import de.erdesignerng.model.serializer.repository.entities.IndexEntity;
import de.erdesignerng.model.serializer.repository.entities.RelationEntity;
import de.erdesignerng.model.serializer.repository.entities.RepositoryEntity;
import de.erdesignerng.model.serializer.repository.entities.StringKeyValuePair;
import de.erdesignerng.model.serializer.repository.entities.SubjectAreaEntity;
import de.erdesignerng.model.serializer.repository.entities.TableEntity;

/**
 * Serializer to store the model in a data dictionary.
 * 
 * @author msertic
 */
public class DictionaryModelSerializer extends DictionaryBaseSerializer {

    public static final DictionaryModelSerializer SERIALIZER = new DictionaryModelSerializer();

    protected Configuration createConfiguration(Class aHibernateDialectClass) {
        Configuration theConfiguration = new Configuration();
        theConfiguration.addClass(DomainEntity.class);
        theConfiguration.addClass(TableEntity.class);
        theConfiguration.addClass(AttributeEntity.class);
        theConfiguration.addClass(IndexEntity.class);
        theConfiguration.addClass(RelationEntity.class);
        theConfiguration.addClass(CommentEntity.class);
        theConfiguration.addClass(SubjectAreaEntity.class);
        theConfiguration.addClass(RepositoryEntity.class);
        theConfiguration.addClass(ChangeEntity.class);
        theConfiguration.setProperty(Environment.DIALECT, aHibernateDialectClass.getName());
        theConfiguration.setProperty(Environment.HBM2DDL_AUTO, "update");
        theConfiguration.setProperty(Environment.CONNECTION_PROVIDER, ThreadbasedConnectionProvider.class.getName());
        return theConfiguration;
    }

    protected Session createSession(Connection aConnection, Class aHibernateDialectClass) {

        Configuration theConfiguration = createConfiguration(aHibernateDialectClass);
        SessionFactory theSessionFactory = theConfiguration.buildSessionFactory();

        return theSessionFactory.openSession(aConnection, AuditInterceptor.INSTANCE);
    }

    public RepositoryEntryDesciptor serialize(RepositoryEntryDesciptor aDesc, Model aModel, Connection aConnection, Class aHibernateDialectClass) throws Exception {

        ThreadbasedConnectionProvider.initializeForThread(aConnection);
        Session theSession = null;
        Transaction theTx = null;
        try {

            theSession = createSession(aConnection, aHibernateDialectClass);
            theTx = theSession.beginTransaction();

            RepositoryEntity theEntity = null;
            if (aDesc.getId() == null) {
                theEntity = new RepositoryEntity();
                theEntity.setSystemId(ModelUtilities.createSystemIdFor(theEntity));                
            } else {
                theEntity = (RepositoryEntity) theSession.get(RepositoryEntity.class, aDesc.getId());
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
            DictionaryDomainSerializer.SERIALIZER.serialize(aModel, theSession, theEntity);

            DictionaryTableSerializer.SERIALIZER.serialize(aModel, theSession, theEntity);

            DictionaryRelationSerializer.SERIALIZER.serialize(aModel, theSession, theEntity);

            DictionaryCommentSerializer.SERIALIZER.serialize(aModel, theSession, theEntity);

            DictionarySubjectAreaSerializer.SERIALIZER.serialize(aModel, theSession, theEntity);

            theSession.saveOrUpdate(theEntity);

            theTx.commit();
            
            aDesc.setName(theEntity.getName());
            aDesc.setId(theEntity.getId());
            return aDesc;

        } catch (Exception e) {
            if (theTx != null) {
                theTx.rollback();
            }

            throw e;
        } finally {

            if (theSession != null) {
                theSession.close();
            }

            ThreadbasedConnectionProvider.cleanup();
        }

    }

    public Model deserialize(RepositoryEntryDesciptor aDescriptor, Connection aConnection, Class aHibernateDialectClass)
            throws Exception {

        ThreadbasedConnectionProvider.initializeForThread(aConnection);
        Session theSession = null;
        Transaction theTx = null;
        try {

            Model theNewModel = new Model();

            theSession = createSession(aConnection, aHibernateDialectClass);
            theTx = theSession.beginTransaction();

            RepositoryEntity theRepositoryEntity = (RepositoryEntity) theSession.get(RepositoryEntity.class,
                    aDescriptor.getId());
            
            // Deserialize the properties
            theNewModel.getProperties().getProperties().clear();
            for (StringKeyValuePair theElement : theRepositoryEntity.getProperties()) {
                theNewModel.getProperties().getProperties().put(theElement.getKey(), theElement.getValue());
            }
            theNewModel.setDialect(DialectFactory.getInstance().getDialect(theRepositoryEntity.getDialect()));
            
            // Deserialize the rest
            DictionaryDomainSerializer.SERIALIZER.deserialize(theNewModel, theRepositoryEntity);

            DictionaryTableSerializer.SERIALIZER.deserialize(theNewModel, theRepositoryEntity);

            DictionaryRelationSerializer.SERIALIZER.deserialize(theNewModel, theRepositoryEntity);

            DictionaryCommentSerializer.SERIALIZER.deserialize(theNewModel, theRepositoryEntity);

            DictionarySubjectAreaSerializer.SERIALIZER.deserialize(theNewModel, theRepositoryEntity);

            theTx.rollback();

            return theNewModel;

        } catch (Exception e) {
            if (theTx != null) {
                theTx.rollback();
            }

            throw e;
        } finally {

            if (theSession != null) {
                theSession.close();
            }

            ThreadbasedConnectionProvider.cleanup();
        }
    }

    /**
     * Get the available repository entries.
     * 
     * @param aDialectClass the hibernate dialect class
     * @param aConnection the jdbc connection
     * @return list of entries
     * @throws Exception will be thrown in case of an exception
     */
    public List<RepositoryEntryDesciptor> getRepositoryEntries(Class aDialectClass, Connection aConnection)
            throws Exception {
        ThreadbasedConnectionProvider.initializeForThread(aConnection);
        Session theSession = null;
        Transaction theTx = null;
        try {

            theSession = createSession(aConnection, aDialectClass);
            theTx = theSession.beginTransaction();

            List<RepositoryEntryDesciptor> theResult = new ArrayList<RepositoryEntryDesciptor>();

            Criteria theCriteria = theSession.createCriteria(RepositoryEntity.class);
            theCriteria.setProjection(Projections.projectionList().add(Projections.property("id")).add(
                    Projections.property("name")));
            for (Object theObject : theCriteria.list()) {
                Object[] theArray = (Object[]) theObject;
                
                RepositoryEntryDesciptor theEntry = new RepositoryEntryDesciptor();
                theEntry.setId((Long) theArray[0]);
                theEntry.setName((String) theArray[1]);
                theResult.add(theEntry);
            }

            theTx.rollback();

            return theResult;

        } catch (Exception e) {
            if (theTx != null) {
                theTx.rollback();
            }

            throw e;
        } finally {

            if (theSession != null) {
                theSession.close();
            }

            ThreadbasedConnectionProvider.cleanup();
        }
    }
}
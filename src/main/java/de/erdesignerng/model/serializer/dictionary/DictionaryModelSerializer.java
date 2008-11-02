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

import java.sql.Connection;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

import de.erdesignerng.model.Model;
import de.erdesignerng.model.serializer.dictionary.entities.AttributeEntity;
import de.erdesignerng.model.serializer.dictionary.entities.DomainEntity;
import de.erdesignerng.model.serializer.dictionary.entities.IndexEntity;
import de.erdesignerng.model.serializer.dictionary.entities.TableEntity;

/**
 * Serializer to store the model in a data dictionary.
 * 
 * @author msertic
 */
public class DictionaryModelSerializer extends DictionarySerializer {

    public static final DictionaryModelSerializer SERIALIZER = new DictionaryModelSerializer();

    protected Configuration createConfiguration(Model aModel) {
        Configuration theConfiguration = new Configuration();
        theConfiguration.addClass(DomainEntity.class);
        theConfiguration.addClass(TableEntity.class);
        theConfiguration.addClass(AttributeEntity.class);
        theConfiguration.addClass(IndexEntity.class);
        theConfiguration.setProperty(Environment.DIALECT, aModel.getDialect().getHibernateDialectClass().getName());
        theConfiguration.setProperty(Environment.HBM2DDL_AUTO, "update");
        theConfiguration.setProperty(Environment.CONNECTION_PROVIDER, ThreadbasedConnectionProvider.class.getName());
        return theConfiguration;
    }

    protected Session createSession(Model aModel, Connection aConnection) {

        Configuration theConfiguration = createConfiguration(aModel);
        SessionFactory theSessionFactory = theConfiguration.buildSessionFactory();

        return theSessionFactory.openSession(aConnection, AuditInterceptor.INSTANCE);
    }

    public void serialize(Model aModel, Connection aConnection) throws Exception {

        ThreadbasedConnectionProvider.initializeForThread(aConnection);
        Session theSession = null;
        Transaction theTx = null;
        try {

            theSession = createSession(aModel, aConnection);    
            theTx = theSession.beginTransaction();
            
            DictionaryTableSerializer.SERIALIZER.serialize(aModel, theSession);
            
            theTx.commit();
            
        } catch (Exception e) {
            theTx.rollback();
            
            throw e;
        } finally {

            if (theSession != null) {
                theSession.close();
            }
            
            ThreadbasedConnectionProvider.cleanup();
        }

    }
}

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

import de.erdesignerng.model.serializer.repository.entities.AttributeEntity;
import de.erdesignerng.model.serializer.repository.entities.ChangeEntity;
import de.erdesignerng.model.serializer.repository.entities.CommentEntity;
import de.erdesignerng.model.serializer.repository.entities.CustomTypeEntity;
import de.erdesignerng.model.serializer.repository.entities.DomainEntity;
import de.erdesignerng.model.serializer.repository.entities.IndexEntity;
import de.erdesignerng.model.serializer.repository.entities.RelationEntity;
import de.erdesignerng.model.serializer.repository.entities.RepositoryEntity;
import de.erdesignerng.model.serializer.repository.entities.SubjectAreaEntity;
import de.erdesignerng.model.serializer.repository.entities.TableEntity;
import de.erdesignerng.model.serializer.repository.entities.ViewEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

import java.sql.Connection;

/**
 * Template class for hibernate operations.
 *
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-09 19:07:30 $
 */
public abstract class HibernateTemplate {

    private final Class dialectClass;

    private final Connection connection;

    public HibernateTemplate(Class aDialectClass, Connection aConnection) {
        dialectClass = aDialectClass;
        connection = aConnection;
    }

    protected Configuration createConfiguration(Class aHibernateDialectClass) {
        Configuration theConfiguration = new Configuration();
        theConfiguration.addClass(DomainEntity.class);
        theConfiguration.addClass(CustomTypeEntity.class);
        theConfiguration.addClass(TableEntity.class);
        theConfiguration.addClass(AttributeEntity.class);
        theConfiguration.addClass(IndexEntity.class);
        theConfiguration.addClass(RelationEntity.class);
        theConfiguration.addClass(CommentEntity.class);
        theConfiguration.addClass(SubjectAreaEntity.class);
        theConfiguration.addClass(RepositoryEntity.class);
        theConfiguration.addClass(ChangeEntity.class);
        theConfiguration.addClass(ViewEntity.class);
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

    public abstract Object doInSession(Session aSession);

    public Object execute() throws Exception {
        ThreadbasedConnectionProvider.initializeForThread(connection);
        Session theSession = null;
        Transaction theTx = null;

        Thread theCurrentThread = Thread.currentThread();

        ClassLoader theLoader = HibernateTemplate.class.getClassLoader();
        theCurrentThread.setContextClassLoader(theLoader);

        try {

            theSession = createSession(connection, dialectClass);

            theTx = theSession.beginTransaction();

            Object theResult = doInSession(theSession);

            theTx.commit();

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
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
package de.erdesignerng.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import de.erdesignerng.dialect.Dialect;
import de.erdesignerng.model.serializer.repository.DictionaryModelSerializer;
import de.erdesignerng.model.serializer.repository.RepositoryEntryDesciptor;
import de.erdesignerng.model.serializer.xml10.Model10XMLPersister;
import de.erdesignerng.model.serializer.xml20.Model20XMLPersister;
import de.erdesignerng.util.ApplicationPreferences;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-09 19:07:29 $
 */
public final class ModelIOUtilities {

    private static ModelIOUtilities me;

    private DocumentBuilderFactory documentBuilderFactory;

    private DocumentBuilder documentBuilder;

    private TransformerFactory transformerFactory;

    private ModelIOUtilities() throws ParserConfigurationException {
        documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilder = documentBuilderFactory.newDocumentBuilder();
        transformerFactory = TransformerFactory.newInstance();
    }

    public static ModelIOUtilities getInstance() throws ParserConfigurationException {

        if (me == null) {
            me = new ModelIOUtilities();
        }
        return me;
    }

    public DocumentBuilderFactory getDocumentBuilderFactory() {
        return documentBuilderFactory;
    }

    public DocumentBuilder getDocumentBuilder() {
        return documentBuilder;
    }

    public TransformerFactory getTransformerFactory() {
        return transformerFactory;
    }

    public Model deserializeModelFromXML(InputStream aInputStream) throws SAXException, IOException {

        try {
            Document theDocument = documentBuilder.parse(aInputStream);
            if (Model20XMLPersister.supportsDocument(theDocument)) {
                Model20XMLPersister thePersister = new Model20XMLPersister(this);
                return thePersister.deserializeModelFromXML(theDocument);
            }
            if (Model10XMLPersister.supportsDocument(theDocument)) {
                Model10XMLPersister thePersister = new Model10XMLPersister(this);
                return thePersister.deserializeModelFromXML(theDocument);
            }

            // This should never happen
            throw new IOException("Invalid document version");
        } finally {
            if (aInputStream != null) {
                aInputStream.close();
            }
        }
    }

    /**
     * Serialize a model to XML output.
     * 
     * @param aModel the model
     * @param aStream the output stream
     * @throws TransformerException will be thrown in case of an error
     * @throws IOException will be thrown in case of an error
     */
    public void serializeModelToXML(Model aModel, OutputStream aStream) throws TransformerException, IOException {

        Model20XMLPersister thePersister = new Model20XMLPersister(this);
        thePersister.serializeModelToXML(aModel, aStream);
    }

    /**
     * Save a model to a repository.
     * 
     * @param aDesc
     *            the element descriptor
     * @param aDialect
     *            the dialect used to communicate with the repository
     * @param aConnection
     *            the connection
     * @param aModel
     *            the model
     * @param aPreferences
     *            the preferences
     * @return the descriptor
     * @throws Exception
     *             will be thrown in case of an exception
     */
    public RepositoryEntryDesciptor serializeModelToDB(RepositoryEntryDesciptor aDesc, Dialect aDialect,
            Connection aConnection, Model aModel, ApplicationPreferences aPreferences) throws Exception {

        Class theDialectClass = aDialect.getHibernateDialectClass();
        aDesc = DictionaryModelSerializer.SERIALIZER.serialize(aDesc, aModel, aConnection, theDialectClass);

        return aDesc;
    }

    /**
     * Deserialize a model from a repository.
     * 
     * @param aDescriptor
     *            the descriptor for the repository entity
     * @param aDialect
     *            the repository dialect
     * @param aConnection
     *            the repository connection
     * @param aPreferences
     *            the preferences
     * @return the loaded model
     * @throws Exception
     *             will be thrown in case of an exception
     */
    public Model deserializeModelfromRepository(RepositoryEntryDesciptor aDescriptor, Dialect aDialect,
            Connection aConnection, ApplicationPreferences aPreferences) throws Exception {

        return DictionaryModelSerializer.SERIALIZER.deserialize(aDescriptor, aConnection, aDialect
                .getHibernateDialectClass());
    }

    /**
     * Get the available repository descriptors.
     * 
     * @param aDialect
     *            the dialect
     * @param aConnection
     *            the connection
     * @return the list of descriptors
     * @throws Exception
     *             will be thrown in case of an exception
     */
    public List<RepositoryEntryDesciptor> getRepositoryEntries(Dialect aDialect, Connection aConnection)
            throws Exception {
        return DictionaryModelSerializer.SERIALIZER.getRepositoryEntries(aDialect.getHibernateDialectClass(),
                aConnection);
    }
}
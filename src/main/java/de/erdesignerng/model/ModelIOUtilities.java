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
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import de.erdesignerng.dialect.Dialect;
import de.erdesignerng.model.serializer.repository.DictionaryModelSerializer;
import de.erdesignerng.model.serializer.xml.XMLModelSerializer;
import de.erdesignerng.util.ApplicationPreferences;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-11-16 15:22:55 $
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

    public Model deserializeModelFromXML(InputStream aInputStream) throws SAXException, IOException {
        Document theDocument = documentBuilder.parse(aInputStream);
        aInputStream.close();

        final List<SAXParseException> theExceptions = new ArrayList<SAXParseException>();

        // Validate the document
        SchemaFactory theSchemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        // hook up org.xml.sax.ErrorHandler implementation.
        theSchemaFactory.setErrorHandler(new ErrorHandler() {

            public void error(SAXParseException aException) throws SAXException {
                theExceptions.add(aException);
            }

            public void fatalError(SAXParseException aException) throws SAXException {
                theExceptions.add(aException);
            }

            public void warning(SAXParseException aException) throws SAXException {
                theExceptions.add(aException);
            }
        });

        // get the custom xsd schema describing the required format for my XML
        // files.
        Schema theSchema = theSchemaFactory.newSchema(getClass().getResource("/erdesignerschema.xsd"));

        // Create a Validator capable of validating XML files according to my
        // custom schema.
        Validator validator = theSchema.newValidator();

        // parse the XML DOM tree againts the stricter XSD schema
        validator.validate(new DOMSource(theDocument));

        if (theExceptions.size() > 0) {
            for (SAXParseException theException : theExceptions) {
                System.out.println(theException.getMessage());
            }
            throw new IOException("Failed to validate document against schema");
        }

        return XMLModelSerializer.SERIALIZER.deserializeFrom(theDocument);
    }

    public void serializeModelToXML(Model aModel, OutputStream aStream) throws TransformerException, IOException {
        Document theDocument = documentBuilder.newDocument();

        XMLModelSerializer.SERIALIZER.serialize(aModel, theDocument);

        Transformer theTransformer = transformerFactory.newTransformer();
        theTransformer.transform(new DOMSource(theDocument), new StreamResult(aStream));

        aStream.close();
    }

    public void serializeModelToDB(Model aModel, ApplicationPreferences aPreferences) throws Exception {

        Connection theConnection = null;
        try {
            Class theDialectClass = aModel.getDialect().getHibernateDialectClass();
            theConnection = aModel.createConnection(aPreferences);
            DictionaryModelSerializer.SERIALIZER.serialize(aModel, theConnection, theDialectClass);
        } finally {
            if (theConnection != null) {
                if (!aModel.getDialect().generatesManagedConnection()) {
                    try {
                        theConnection.close();
                    } catch (Exception e) {
                        // Ignore this
                    }
                }
            }
        }
    }

    public Model deserializeModelfromRepository(Dialect aDialect, Connection aConnection, ApplicationPreferences aPreferences)
            throws Exception {

        return DictionaryModelSerializer.SERIALIZER.deserialize(aConnection, aDialect.getHibernateDialectClass());
    }
}
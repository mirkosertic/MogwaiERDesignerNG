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
package de.erdesignerng.model.serializer.xml10;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import de.erdesignerng.model.Model;
import de.erdesignerng.model.ModelIOUtilities;
import de.erdesignerng.model.serializer.CommonXMLElementsAndAttributes;

/**
 * Persister for model version 1.0.
 * 
 * @author mirkosertic
 */
public class Model10XMLPersister implements CommonXMLElementsAndAttributes {

    private ModelIOUtilities utils;

    /**
     * Test if the persister supports a document version.
     * 
     * @param aDocument
     *            the document
     * @return true if yes, else false
     */
    public static boolean supportsDocument(Document aDocument) {
        NodeList theNodes = aDocument.getElementsByTagName(MODEL);
        if (theNodes.getLength() != 1) {
            return false;
        }
        Element theDocumentElement = (Element) theNodes.item(0);
        return XMLModelSerializer.CURRENT_VERSION.equals(theDocumentElement.getAttribute(VERSION));
    }

    public Model10XMLPersister(ModelIOUtilities aUtils) {
        utils = aUtils;
    }

    public void serializeModelToXML(Model aModel, OutputStream aStream) throws IOException, TransformerException {
        Document theDocument = utils.getXmlUtils().getDocumentBuilder().newDocument();

        XMLModelSerializer.SERIALIZER.serialize(aModel, theDocument);

        Transformer theTransformer = utils.getXmlUtils().getTransformerFactory().newTransformer();
        theTransformer.transform(new DOMSource(theDocument), new StreamResult(aStream));

        aStream.close();

    }

    public Model deserializeModelFromXML(Document aDocument) throws SAXException, IOException {

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
        Schema theSchema = theSchemaFactory.newSchema(getClass().getResource("/erdesignerschema_1.0.xsd"));

        // Create a Validator capable of validating XML files according to my
        // custom schema.
        Validator validator = theSchema.newValidator();

        // parse the XML DOM tree againts the stricter XSD schema
        validator.validate(new DOMSource(aDocument));

        if (theExceptions.size() > 0) {
            for (SAXParseException theException : theExceptions) {
                throw new IOException("Failed to validate document against schema", theException);
            }
        }

        return XMLModelSerializer.SERIALIZER.deserializeFrom(aDocument);
    }
}

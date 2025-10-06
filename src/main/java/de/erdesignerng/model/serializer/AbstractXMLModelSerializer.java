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
package de.erdesignerng.model.serializer;

import de.erdesignerng.model.Model;
import de.erdesignerng.model.OwnedModelItem;
import de.erdesignerng.util.XMLUtils;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public abstract class AbstractXMLModelSerializer extends CommonAbstractXMLSerializer {

    protected static final String CONFIGURATION = "Configuration";

    protected static final String DIALECT = "dialect";

    protected static final String CUSTOMTYPES = "CustomTypes";

    protected static final String DOMAINS = "Domains";

    protected static final String TABLES = "Tables";

    protected static final String RELATIONS = "Relations";

    protected static final String VIEWS = "Views";

    protected static final String SUBJECTAREAS = "Subjectareas";

    protected static final String COMMENTS = "Comments";

    private AbstractXMLAttributeSerializer xmlAttributeSerializer = null;

    private AbstractXMLCommentSerializer xmlCommentSerializer = null;

    private AbstractXMLDomainSerializer xmlDomainSerializer = null;

    private AbstractXMLCustomTypeSerializer xmlCustomTypeSerializer = null;

    private AbstractXMLIndexSerializer xmlIndexSerializer = null;

    private AbstractXMLRelationSerializer xmlRelationSerializer = null;

    private AbstractXMLSubjectAreaSerializer xmlSubjectAreaSerializer = null;

    private AbstractXMLTableSerializer xmlTableSerializer = null;

	private AbstractXMLViewSerializer xmlViewSerializer = null;

    private XMLUtils utils = null;

    protected AbstractXMLModelSerializer(final XMLUtils aUtils) {
        utils = aUtils;
    }

    /**
     * Test if the persister supports a document version.
     *
     * @param aDocument the document
     * @return true if yes, else false
     */
    public boolean supportsDocument(final Document aDocument) {
        final NodeList theNodes = aDocument.getElementsByTagName(MODEL);
        if (theNodes.getLength() != 1) {
            return false;
        }
        final Element theDocumentElement = (Element) theNodes.item(0);
        return getVersion().equals(theDocumentElement.getAttribute(VERSION));
    }

    public Model deserializeModelFromXML(final Document aDocument) throws SAXException, IOException {

        if (!supportsDocument(aDocument)) {
            throw new IOException("Unsupported model version");
        }

        final List<SAXParseException> theExceptions = new ArrayList<>();

        // Validate the document
        final SchemaFactory theSchemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        // hook up org.XML.sax.ErrorHandler implementation.
        final ErrorHandler theHandler = new ErrorHandler() {

            @Override
            public void error(final SAXParseException aException) {
                theExceptions.add(aException);
            }

            @Override
            public void fatalError(final SAXParseException aException) {
                theExceptions.add(aException);
            }

            @Override
            public void warning(final SAXParseException aException) {
                theExceptions.add(aException);
            }
        };
        theSchemaFactory.setErrorHandler(theHandler);

        // get the custom xsd schema describing the required format for my XML
        // files.
        final Schema theSchema = theSchemaFactory.newSchema(getClass().getResource(getSchemaResource()));

        // Create a Validator capable of validating XML files according to my
        // custom schema.
        final Validator validator = theSchema.newValidator();
        validator.setErrorHandler(theHandler);

        // parse the XML DOM tree against the stricter XSD schema
        validator.validate(new DOMSource(aDocument));

        if (!theExceptions.isEmpty()) {
            throw new IOException("Failed to validate document against schema", theExceptions.getFirst());
        }

        return deserialize(aDocument);
    }

    public void serializeModelToXML(final Model aModel, final Writer aWriter) throws IOException, TransformerException {

        final Document theDocument = utils.newDocument();

        serialize(aModel, theDocument);

        utils.transform(theDocument, aWriter);

        aWriter.close();
    }

    protected abstract Model deserialize(Document aDocument);

    protected abstract void serialize(Model aModel, Document aDocument);

    @Override
    @Deprecated
    public void serialize(final OwnedModelItem aModelItem, final Document aDocument, final Element aRootElement) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    @Deprecated
    public void deserialize(final Model aModel, final Document aDocument) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    protected abstract String getVersion();

    protected abstract String getSchemaResource();

    public AbstractXMLAttributeSerializer getXMLAttributeSerializer() {
        return xmlAttributeSerializer;
    }

    protected AbstractXMLCommentSerializer getXMLCommentSerializer() {
        return xmlCommentSerializer;
    }

    protected AbstractXMLCustomTypeSerializer getXMLCustomTypeSerializer(final AbstractXMLModelSerializer xmlModelSerializer) {
        return xmlCustomTypeSerializer;
    }

    protected AbstractXMLDomainSerializer getXMLDomainSerializer() {
        return xmlDomainSerializer;
    }

    public AbstractXMLIndexSerializer getXMLIndexSerializer() {
        return xmlIndexSerializer;
    }

    protected AbstractXMLRelationSerializer getXMLRelationSerializer() {
        return xmlRelationSerializer;
    }

    protected AbstractXMLSubjectAreaSerializer getXMLSubjectAreaSerializer() {
        return xmlSubjectAreaSerializer;
    }

    protected AbstractXMLTableSerializer getXMLTableSerializer(final AbstractXMLModelSerializer xmlModelSerializer) {
        return xmlTableSerializer;
    }

	protected AbstractXMLViewSerializer getXMLViewSerializer() {
        return xmlViewSerializer;
    }

    protected void setXMLAttributeSerializer(final AbstractXMLAttributeSerializer xmlAttributeSerializer) {
        this.xmlAttributeSerializer = xmlAttributeSerializer;
    }

    protected void setXMLCommentSerializer(final AbstractXMLCommentSerializer xmlCommentSerializer) {
        this.xmlCommentSerializer = xmlCommentSerializer;
    }

    protected void setXMLCustomTypeSerializer(final AbstractXMLCustomTypeSerializer xmlCustomTypeSerializer) {
        this.xmlCustomTypeSerializer = xmlCustomTypeSerializer;
    }

    protected void setXMLDomainSerializer(final AbstractXMLDomainSerializer xmlDomainSerializer) {
        this.xmlDomainSerializer = xmlDomainSerializer;
    }

    protected void setXMLIndexSerializer(final AbstractXMLIndexSerializer xmlIndexSerializer) {
        this.xmlIndexSerializer = xmlIndexSerializer;
    }

    protected void setXMLRelationSerializer(final AbstractXMLRelationSerializer xmlRelationSerializer) {
        this.xmlRelationSerializer = xmlRelationSerializer;
    }

    protected void setXMLSubjectAreaSerializer(final AbstractXMLSubjectAreaSerializer xmlSubjectAreaSerializer) {
        this.xmlSubjectAreaSerializer = xmlSubjectAreaSerializer;
    }

    protected void setXMLTableSerializer(final AbstractXMLTableSerializer xmlTableSerializer) {
        this.xmlTableSerializer = xmlTableSerializer;
    }

	protected void setXMLViewSerializer(final AbstractXMLViewSerializer xmlViewSerializer) {
        this.xmlViewSerializer = xmlViewSerializer;
    }
}
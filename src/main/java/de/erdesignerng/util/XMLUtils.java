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
package de.erdesignerng.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Utility class to deal with XML io.
 * 
 * @author mirkosertic
 */
public final class XMLUtils {

    private static XMLUtils me;

    private DocumentBuilderFactory documentBuilderFactory;

    private DocumentBuilder documentBuilder;

    private TransformerFactory transformerFactory;

    private XMLUtils() throws ParserConfigurationException {
        documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilder = documentBuilderFactory.newDocumentBuilder();
        transformerFactory = TransformerFactory.newInstance();
    }

    public static XMLUtils getInstance() throws ParserConfigurationException {

        if (me == null) {
            me = new XMLUtils();
        }
        return me;
    }

    public Document parse(InputStream aStream) throws SAXException, IOException {
        return documentBuilder.parse(aStream);
    }

    public Document newDocument() {
        return documentBuilder.newDocument();
    }

    public void transform(Document aDocument, Writer aWriter) throws TransformerException {
        Transformer theTransformer = transformerFactory.newTransformer();
        theTransformer.setOutputProperty(OutputKeys.INDENT, "yes");
        theTransformer.transform(new DOMSource(aDocument), new StreamResult(aWriter));
    }
}
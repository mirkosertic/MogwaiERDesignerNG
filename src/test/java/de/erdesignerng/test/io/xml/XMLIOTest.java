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
package de.erdesignerng.test.io.xml;

import java.io.IOException;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import de.erdesignerng.model.Model;
import de.erdesignerng.model.serializer.xml17.XMLModelSerializer;
import de.erdesignerng.test.BaseERDesignerTestCaseImpl;

/**
 * Test for XML based model io. 
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2008-11-14 21:10:04 $
 */
public class XMLIOTest extends BaseERDesignerTestCaseImpl {

    public void testLoadXML17Model() throws ParserConfigurationException, SAXException, IOException, TransformerException {
        
        DocumentBuilderFactory theFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder theBuilder = theFactory.newDocumentBuilder();
        Document theDoc = theBuilder.parse(getClass().getResourceAsStream("examplemodel.mxm"));
        Model theModel = XMLModelSerializer.SERIALIZER.deserializeFrom(theDoc);
        
        Document theEmptyDoc = theBuilder.newDocument();
        XMLModelSerializer.SERIALIZER.serialize(theModel, theEmptyDoc);
        
        StringWriter theStringWriter = new StringWriter();
        
        TransformerFactory theTransformerFactory = TransformerFactory.newInstance();
        Transformer theTransformer = theTransformerFactory.newTransformer();
        theTransformer.transform(new DOMSource(theEmptyDoc), new StreamResult(theStringWriter));

        String theOriginalFile = readResourceFile("examplemodel.mxm");
        String theNewFile = theStringWriter.toString();
        
        assertTrue(theOriginalFile.equals(theNewFile));
    }
}
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
package de.erdesignerng.test.generator;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import de.erdesignerng.generator.openxava.OpenXavaGenerator;
import de.erdesignerng.generator.openxava.OpenXavaOptions;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.serializer.xml10.XMLModelSerializer;
import junit.framework.TestCase;

public class OpenXavaGeneratorTest extends TestCase {

    public void testGenerator() throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory theFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder theBuilder = theFactory.newDocumentBuilder();
        Document theDoc = theBuilder.parse(getClass().getResourceAsStream("examplemodel.mxm"));
        Model theModel = XMLModelSerializer.SERIALIZER.deserializeFrom(theDoc);
        
        OpenXavaOptions theOptions = new OpenXavaOptions();
        File theTargetFile = new File("C:\\temp\\ox\\openxava-3.1.2\\workspace\\Management\\src");
        
        OpenXavaGenerator theGenerator = new OpenXavaGenerator();
        theGenerator.generate(theModel, "de.powerstaff", theOptions, theTargetFile);
        
    }
}

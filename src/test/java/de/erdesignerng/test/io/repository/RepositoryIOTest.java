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
package de.erdesignerng.test.io.repository;

import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import de.erdesignerng.model.Model;
import de.erdesignerng.model.serializer.repository.DictionaryModelSerializer;
import de.erdesignerng.model.serializer.repository.RepositoryEntryDesciptor;
import de.erdesignerng.model.serializer.xml17.XMLModelSerializer;
import de.erdesignerng.test.BaseERDesignerTestCaseImpl;

/**
 * Test for XML based model io.
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2008-11-16 17:48:26 $
 */
public class RepositoryIOTest extends BaseERDesignerTestCaseImpl {

    public void testLoadSaveDictionary() throws Exception {
        DocumentBuilderFactory theFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder theBuilder = theFactory.newDocumentBuilder();
        Document theDoc = theBuilder.parse(getClass().getResourceAsStream("examplemodel.mxm"));

        Model theModel = XMLModelSerializer.SERIALIZER.deserializeFrom(theDoc);

        Class.forName("org.hsqldb.jdbcDriver").newInstance();
        Connection theConnection = null;
        try {
            theConnection = DriverManager.getConnection("jdbc:hsqldb:mem:aname", "sa", "");
            
            Class theHibernateDialect = org.hibernate.dialect.HSQLDialect.class;
            
            RepositoryEntryDesciptor theDesc = new RepositoryEntryDesciptor();
            theDesc.setName("Dummy");
            
            theDesc = DictionaryModelSerializer.SERIALIZER.serialize(theDesc, theModel, theConnection, theHibernateDialect);
            
            Model theNewModel = DictionaryModelSerializer.SERIALIZER.deserialize(theDesc, theConnection, theHibernateDialect);

            Document theEmptyDoc = theBuilder.newDocument();
            XMLModelSerializer.SERIALIZER.serialize(theNewModel, theEmptyDoc);
            
            StringWriter theStringWriter = new StringWriter();
            
            TransformerFactory theTransformerFactory = TransformerFactory.newInstance();
            Transformer theTransformer = theTransformerFactory.newTransformer();
            theTransformer.transform(new DOMSource(theEmptyDoc), new StreamResult(theStringWriter));
            
            String theOriginalFile = readResourceFile("examplemodel.mxm");
            String theNewFile = theStringWriter.toString();
            
            assertTrue(theOriginalFile.equals(theNewFile));
            
        } finally {
            if (theConnection != null) {
                theConnection.close();
            }
        }
    }
}
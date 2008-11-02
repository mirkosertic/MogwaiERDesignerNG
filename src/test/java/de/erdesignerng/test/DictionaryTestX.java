package de.erdesignerng.test;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import de.erdesignerng.model.Model;
import de.erdesignerng.model.serializer.dictionary.DictionaryModelSerializer;
import de.erdesignerng.model.serializer.xml.XMLModelSerializer;
import de.erdesignerng.util.ApplicationPreferences;
import junit.framework.TestCase;

public class DictionaryTestX extends TestCase {
    
    public void testLoadAndSave() throws ParserConfigurationException, SAXException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {
        XMLModelSerializer theXMLSerializer = XMLModelSerializer.SERIALIZER;
        
        DocumentBuilderFactory theFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder theBuilder = theFactory.newDocumentBuilder();
        Document theDocument = theBuilder.parse(new File("C:\\Users\\msertic\\Documents\\powerstaff.mxm"));
        
        Model theModel = theXMLSerializer.deserializeFrom(theDocument);
        Connection theConnection = theModel.createConnection(ApplicationPreferences.getInstance());
        
        DictionaryModelSerializer theDictSerializer = DictionaryModelSerializer.SERIALIZER;
        
        theDictSerializer.serialize(theModel, theConnection);
        
        theConnection.close();
    }

}

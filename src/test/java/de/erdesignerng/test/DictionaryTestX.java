package de.erdesignerng.test;

import java.io.File;
import java.sql.Connection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.w3c.dom.Document;

import de.erdesignerng.model.Model;
import de.erdesignerng.model.serializer.dictionary.DictionaryModelSerializer;
import de.erdesignerng.model.serializer.xml.XMLModelSerializer;
import de.erdesignerng.util.ApplicationPreferences;

public class DictionaryTestX extends TestCase {
    
    public void testLoadAndSave() throws Exception {
        XMLModelSerializer theXMLSerializer = XMLModelSerializer.SERIALIZER;
        
        DocumentBuilderFactory theFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder theBuilder = theFactory.newDocumentBuilder();
        Document theDocument = theBuilder.parse(new File("C:\\Users\\msertic\\Documents\\powerstaff.mxm"));
        
        Model theModel = theXMLSerializer.deserializeFrom(theDocument);
        Connection theConnection = theModel.createConnection(ApplicationPreferences.getInstance());
        
        DictionaryModelSerializer theDictSerializer = DictionaryModelSerializer.SERIALIZER;
        
        theDictSerializer.serialize(theModel, theConnection);
        
        Model theNewModel = theDictSerializer.deserialize(theModel, theConnection);
        System.out.println(theNewModel.getDomains().size());
        System.out.println(theNewModel.getTables().size());
        
        theConnection.close();
    }

}

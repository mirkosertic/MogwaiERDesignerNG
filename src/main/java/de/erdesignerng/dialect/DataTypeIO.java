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
package de.erdesignerng.dialect;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.erdesignerng.util.ApplicationPreferences;
import de.erdesignerng.util.XMLUtils;

public final class DataTypeIO {

    private static final Logger LOGGER = Logger.getLogger(DataTypeIO.class);

    private static final String JDBCTYPE = "Jdbctype";

    private static final String DATATYPE = "Datatype";

    private static final String DATATYPES = "Datatypes";

    private static final String VALUE = "value";

    private static final String DEFINITION = "definition";

    private static final String IDENTITY = "identity";

    private static final String NAME = "name";

    private static final String DIALECT = "dialect";

    private static final String OTHER = "OTHER";

    private static DataTypeIO me;

    private XMLUtils xmlUtils;

    public static DataTypeIO getInstance() throws ParserConfigurationException {
        if (me == null) {
            me = new DataTypeIO();
        }
        return me;
    }

    private DataTypeIO() throws ParserConfigurationException {
        xmlUtils = XMLUtils.getInstance();
    }

    private static String jdbcTypeToString(int aType) throws IllegalAccessException {
        for (Field theField : Types.class.getFields()) {
            int theValue = theField.getInt(Types.class);
            if (theValue == aType) {
                return theField.getName();
            }
        }
        return OTHER;
    }

    private static int stringToJdbcType(String aType) throws IllegalAccessException {
        for (Field theField : Types.class.getFields()) {
            int theValue = theField.getInt(Types.class);
            if (aType.equals(theField.getName())) {
                return theValue;
            }
        }
        return Types.OTHER;
    }

    public void loadUserTypes(ApplicationPreferences aPreferences) throws TransformerException, IOException,
            SAXException, IllegalAccessException {
        DialectFactory theFactory = DialectFactory.getInstance();

        File theDataTypesDirectory = aPreferences.getDatatypeConfigDirectory();
        theDataTypesDirectory.mkdirs();

        for (Dialect theDialect : theFactory.getSupportedDialects()) {
            File theDatatypeFile = new File(theDataTypesDirectory, theDialect.getUniqueName() + ".xml");
            if (!theDatatypeFile.exists() || theDatatypeFile.getTotalSpace() == 0) {
                LOGGER.info("Writing new datatype file " + theDatatypeFile);
                FileOutputStream theStream = null;
                try {
                    theStream = new FileOutputStream(theDatatypeFile);
                    serializeDataTypesFor(theDialect, theStream);
                } finally {
                    if (theStream != null) {
                        theStream.close();
                    }
                }
            } else {
                LOGGER.info("Loading types from " + theDatatypeFile);
                InputStream theStream = null;
                try {
                    theStream = new FileInputStream(theDatatypeFile);
                    deserializeDataTypesFrom(theDialect, theStream);
                } finally {
                    if (theStream != null) {
                        theStream.close();
                    }
                }
            }
        }
    }

    private void deserializeDataTypesFrom(Dialect aDialect, InputStream aStream) throws TransformerException,
            IOException, SAXException, IllegalArgumentException, IllegalAccessException {
        Document theDocument = xmlUtils.parse(aStream);
        NodeList theNodes = theDocument.getElementsByTagName(DATATYPE);
        for (int i = 0; i < theNodes.getLength(); i++) {
            Element theDataType = (Element) theNodes.item(i);
            String theTypeName = theDataType.getAttribute(NAME);
            boolean theIdentity = Boolean.parseBoolean(theDataType.getAttribute(IDENTITY));
            String theDefinition = theDataType.getAttribute(DEFINITION);

            List<Integer> theJDBCTypes = new ArrayList<Integer>();
            NodeList theTypesNode = theDataType.getElementsByTagName(JDBCTYPE);
            for (int j = 0; j < theTypesNode.getLength(); j++) {
                Element theJdbcElement = (Element) theTypesNode.item(j);
                theJDBCTypes.add(stringToJdbcType(theJdbcElement.getAttribute(VALUE)));
            }

            int[] theTypes = new int[theJDBCTypes.size()];
            for (int j = 0; j < theJDBCTypes.size(); j++) {
                theTypes[j] = theJDBCTypes.get(j);
            }
            DataType theNewDataType = aDialect.createDataType(theTypeName, theDefinition, theIdentity, theTypes);

            DataType theExistingDataType = aDialect.getDataTypes().findByName(theTypeName);
            if (theExistingDataType == null) {
                LOGGER.info("Adding new datatype " + theTypeName);
                aDialect.getDataTypes().add(theNewDataType);
            } else {
                LOGGER.info("Replacing datatype " + theTypeName);
                aDialect.getDataTypes().remove(theExistingDataType);
                aDialect.getDataTypes().add(theNewDataType);
            }
        }
    }

    private void serializeDataTypesFor(Dialect aDialect, OutputStream aStream) throws TransformerException,
            IOException, IllegalAccessException {
        Document theDocument = xmlUtils.newDocument();
        Element theRootElement = theDocument.createElement(DATATYPES);
        theRootElement.setAttribute(DIALECT, aDialect.getUniqueName());
        theDocument.appendChild(theRootElement);
        for (DataType theDataType : aDialect.getDataTypes()) {
            Element theTypeElement = theDocument.createElement(DATATYPE);
            theTypeElement.setAttribute(NAME, theDataType.getName());
            theTypeElement.setAttribute(IDENTITY, Boolean.toString(theDataType.isIdentity()));
            theTypeElement.setAttribute(DEFINITION, theDataType.getDefinition());
            for (int theJdbcType : theDataType.getJDBCType()) {
                Element theJdbcElement = theDocument.createElement(JDBCTYPE);
                theJdbcElement.setAttribute(VALUE, jdbcTypeToString(theJdbcType));
                theTypeElement.appendChild(theJdbcElement);
            }
            theRootElement.appendChild(theTypeElement);
        }

        xmlUtils.transform(theDocument, new OutputStreamWriter(aStream, "UTF-8"));
    }
}
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

import de.erdesignerng.dialect.Dialect;
import de.erdesignerng.model.serializer.AbstractXMLModelSerializer;
import de.erdesignerng.model.serializer.repository.DictionaryModelSerializer;
import de.erdesignerng.model.serializer.repository.RepositoryEntryDescriptor;
import de.erdesignerng.model.serializer.xml10.XMLModel10Serializer;
import de.erdesignerng.model.serializer.xml20.XMLModel20Serializer;
import de.erdesignerng.model.serializer.xml30.XMLModel30Serializer;
import de.erdesignerng.model.serializer.xml40.XMLModel40Serializer;
import de.erdesignerng.model.serializer.xml50.XMLModel50Serializer;
import de.erdesignerng.util.XMLUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-09 19:07:29 $
 */
public final class ModelIOUtilities {

	private static ModelIOUtilities me;

	private XMLUtils xmlUtils;

	private final List<AbstractXMLModelSerializer> knownSerializers = new ArrayList<>();

	private ModelIOUtilities() throws ParserConfigurationException {
		xmlUtils = XMLUtils.getInstance();
		knownSerializers.add(new XMLModel10Serializer(xmlUtils));
		knownSerializers.add(new XMLModel20Serializer(xmlUtils));
		knownSerializers.add(new XMLModel30Serializer(xmlUtils));
		knownSerializers.add(new XMLModel40Serializer(xmlUtils));
		knownSerializers.add(new XMLModel50Serializer(xmlUtils));
	}

	public static ModelIOUtilities getInstance() throws ParserConfigurationException {

		if (me == null) {
			me = new ModelIOUtilities();
		}
		return me;
	}

	public Model deserializeModelFromXML(InputStream aInputStream) throws SAXException, IOException {
		try {
			Document theDocument = xmlUtils.parse(aInputStream);
			for (AbstractXMLModelSerializer theSerializer : knownSerializers) {
				if (theSerializer.supportsDocument(theDocument)) {
					return theSerializer.deserializeModelFromXML(theDocument);
				}
			}

			// This should never happen
			throw new IOException("Invalid document version");
		} finally {
			if (aInputStream != null) {
				aInputStream.close();
			}
		}
	}

	/**
	 * Serialize a model to XML output.
	 *
	 * @param aModel  the model
	 * @param aWriter the output writer
	 * @throws TransformerException will be thrown in case of an error
	 * @throws IOException		  will be thrown in case of an error
	 */
	public void serializeModelToXML(Model aModel, Writer aWriter) throws TransformerException, IOException {
		new XMLModel50Serializer(xmlUtils).serializeModelToXML(aModel, aWriter);
	}

	/**
	 * Save a model to a repository.
	 *
	 * @param aDesc	   the element descriptor
	 * @param aDialect	the dialect used to communicate with the repository
	 * @param aConnection the connection
	 * @param aModel	  the model
	 * @return the descriptor
	 * @throws Exception will be thrown in case of an exception
	 */
	public RepositoryEntryDescriptor serializeModelToDB(RepositoryEntryDescriptor aDesc, Dialect aDialect, Connection aConnection, Model aModel) throws Exception {
		Class theDialectClass = aDialect.getHibernateDialectClass();
		aDesc = DictionaryModelSerializer.SERIALIZER.serialize(aDesc, aModel, aConnection, theDialectClass);

		return aDesc;
	}

	/**
	 * Deserialize a model from a repository.
	 *
	 * @param aDescriptor the descriptor for the repository entity
	 * @param aDialect	the repository dialect
	 * @param aConnection the repository connection
	 * @return the loaded model
	 * @throws Exception will be thrown in case of an exception
	 */
	public Model deserializeModelFromRepository(RepositoryEntryDescriptor aDescriptor, Dialect aDialect, Connection aConnection) throws Exception {
		return DictionaryModelSerializer.SERIALIZER.deserialize(aDescriptor, aConnection, aDialect.getHibernateDialectClass());
	}

	/**
	 * Get the available repository descriptors.
	 *
	 * @param aDialect	the dialect
	 * @param aConnection the connection
	 * @return the list of descriptors
	 * @throws Exception will be thrown in case of an exception
	 */
	public List<RepositoryEntryDescriptor> getRepositoryEntries( Dialect aDialect, Connection aConnection) throws Exception {
		return DictionaryModelSerializer.SERIALIZER.getRepositoryEntries(aDialect.getHibernateDialectClass(), aConnection);
	}
}
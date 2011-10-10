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
package de.erdesignerng.model.serializer.xml30;

import de.erdesignerng.model.serializer.AbstractXMLAttributeSerializer;
import de.erdesignerng.model.serializer.AbstractXMLDomainSerializer;
import de.erdesignerng.model.serializer.xml20.XMLModel20Serializer;
import de.erdesignerng.util.XMLUtils;

/**
 * @author $Author: dr-death2 $
 * @version $Date: 2009-10-21 10:00:00 $
 */
public class XMLModel30Serializer extends XMLModel20Serializer {

	private static final String CURRENT_VERSION = "3.0";

	private static final String XML_SCHEMA_DEFINITION = "/erdesignerschema_3.0.xsd";

	public XMLModel30Serializer(XMLUtils utils) {
		super(utils);
		setXMLAttributeSerializer(new XMLAttributeSerializer());
		setXMLDomainSerializer(new XMLDomainSerializer());
	}

	@Override
	public String getSchemaResource() {
		return XML_SCHEMA_DEFINITION;
	}

	@Override
	public String getVersion() {
		return CURRENT_VERSION;
	}

	@Override
	public AbstractXMLAttributeSerializer getXMLAttributeSerializer() {
		if (super.getXMLAttributeSerializer() == null) {
			setXMLAttributeSerializer(new XMLAttributeSerializer());
		}

		return super.getXMLAttributeSerializer();
	}

	@Override
	public AbstractXMLDomainSerializer getXMLDomainSerializer() {
		if (super.getXMLDomainSerializer() == null) {
			setXMLDomainSerializer(new XMLDomainSerializer());
		}

		return super.getXMLDomainSerializer();
	}
}
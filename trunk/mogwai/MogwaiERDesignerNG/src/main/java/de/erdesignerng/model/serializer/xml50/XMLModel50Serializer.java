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
package de.erdesignerng.model.serializer.xml50;

import de.erdesignerng.model.serializer.AbstractXMLCustomTypeSerializer;
import de.erdesignerng.model.serializer.AbstractXMLDomainSerializer;
import de.erdesignerng.model.serializer.AbstractXMLModelSerializer;
import de.erdesignerng.model.serializer.AbstractXMLSubjectAreaSerializer;
import de.erdesignerng.model.serializer.xml40.XMLModel40Serializer;
import de.erdesignerng.util.XMLUtils;

/**
 * @author $Author: dr-death2 $
 * @version $Date: 2011-10-08 20:00:00 $
 */
public class XMLModel50Serializer extends XMLModel40Serializer {

	private static final String CURRENT_VERSION = "5.0";

	private static final String XML_SCHEMA_DEFINITION = "/erdesignerschema_5.0.xsd";

	public XMLModel50Serializer(XMLUtils utils) {
		super(utils);
		setXMLCustomTypeSerializer(new XMLCustomTypeSerializer(this));
		setXMLDomainSerializer(new XMLDomainSerializer());
		setXMLSubjectAreaSerializer(new XMLSubjectAreaSerializer());
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
	public AbstractXMLCustomTypeSerializer getXMLCustomTypeSerializer(AbstractXMLModelSerializer xmlModelSerializer) {
		if (super.getXMLCustomTypeSerializer(xmlModelSerializer) == null) {
			setXMLCustomTypeSerializer(new XMLCustomTypeSerializer(xmlModelSerializer));
		}

		return super.getXMLCustomTypeSerializer(xmlModelSerializer);
	}

	@Override
	public AbstractXMLDomainSerializer getXMLDomainSerializer() {
		if (super.getXMLDomainSerializer() == null) {
			setXMLDomainSerializer(new XMLDomainSerializer());
		}

		return super.getXMLDomainSerializer();
	}

	@Override
	protected AbstractXMLSubjectAreaSerializer getXMLSubjectAreaSerializer() {
		if (super.getXMLSubjectAreaSerializer() == null) {
			setXMLSubjectAreaSerializer(new XMLSubjectAreaSerializer());
		}

		return super.getXMLSubjectAreaSerializer();
	}
}
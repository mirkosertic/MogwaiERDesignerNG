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
package de.erdesignerng.model.serializer;

import de.erdesignerng.model.CustomType;

/**
 * @author $Author: dr-death2 $
 * @version $Date: 2010-04-04 01:15:00 $
 */
public abstract class AbstractXMLCustomTypeSerializer extends CommonAbstractXMLSerializer<CustomType> {

	protected static final String CUSTOMTYPE = "CustomType";

	protected static final String SCHEMA = "schema";

	protected static final String TYPE = "type";

	protected static final String ALIAS = "alias";

	private AbstractXMLModelSerializer xmlModelSerializer = null;

	public AbstractXMLCustomTypeSerializer(AbstractXMLModelSerializer xmlModelSerializer) {
		this.xmlModelSerializer = xmlModelSerializer;
	}

	protected AbstractXMLModelSerializer getXMLModelSerializer() {
		return xmlModelSerializer;
	}
}
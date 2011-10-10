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

import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.ModelItem;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author $Author: dr-death2 $
 * @version $Date: 2009-10-25 02:50:00 $
 */
public abstract class AbstractXMLAttributeSerializer extends CommonAbstractXMLSerializer<Attribute> {

	protected static final String ATTRIBUTE = "Attribute";

	protected static final String DEFAULTVALUE = "defaultvalue";

	protected static final String EXTRA = "extra";

	protected static final String FRACTION = "fraction";

	protected static final String NULLABLE = "nullable";

	protected static final String SCALE = "scale";

	protected static final String SIZE = "size";

	public abstract void deserialize(Model aModel, ModelItem aTableOrCustomType, Element aElement);

	@Override
	@Deprecated
	public void deserialize(Model aModel, Document aDocument) {
		throw new UnsupportedOperationException("Not supported.");
	}
}

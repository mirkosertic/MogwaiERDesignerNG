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

import de.erdesignerng.model.Relation;

/**
 * @author $Author: dr-death2 $
 * @version $Date: 2009-10-25 02:50:00 $
 */
public abstract class AbstractXMLRelationSerializer extends CommonAbstractXMLSerializer<Relation> {
	protected static final String EXPORTINGATTRIBUTEREFID = "exportingattributerefid";

	protected static final String EXPORTINGTABLEREFID = "exportingtablerefid";

	protected static final String IMPORTINGATTRIBUTEREFID = "importingattributerefid";

	protected static final String IMPORTINGTABLEREFID = "importingtablerefid";

	protected static final String MAPPING = "Mapping";

	protected static final String ONDELETE = "ondelete";

	protected static final String ONUPDATE = "onupdate";

	protected static final String RELATION = "Relation";

	protected static final String EXPORTINGEXPRESSIONREFID = "exportingexpressionrefid";

}

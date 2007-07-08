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
package de.mogwai.erdesignerng.model;

import java.util.Map;

/**
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2007-07-08 17:55:41 $
 */
public class RelationList extends ModelItemVector<Relation> {

	private static final long serialVersionUID = 330168987165235683L;

	/**
	 * Check if a table is used by any of the defined relations.
	 * 
	 * @param aTable
	 *            the table
	 * @return true it its used, else false
	 */
	public boolean isTableInUse(Table aTable) {
		for (Relation theRelation : this) {
			if (theRelation.getImportingTable().equals(aTable)) {
				return true;
			}
			if (theRelation.getExportingTable().equals(aTable)) {
				return true;
			}
		}
		return false;

	}

	/**
	 * Check if an attribute is used by any of the defined relations.
	 * 
	 * @param aAttribute
	 *            the attribute
	 * @return
	 */
	public boolean isAttributeInUse(Attribute aAttribute) {
		for (Relation theRelation : this) {
			Map theMap = theRelation.getMapping();
			if (theMap.containsKey(aAttribute)) {
				return true;
			}
			if (theMap.containsValue(aAttribute)) {
				return true;
			}
		}
		return false;
	}

	public boolean isForeignKeyAttribute(Attribute aAttribute) {
		for (Relation theRelation : this) {
			Map theMap = theRelation.getMapping();
			if (theMap.containsValue(aAttribute)) {
				return true;
			}
		}
		return false;
	}

}

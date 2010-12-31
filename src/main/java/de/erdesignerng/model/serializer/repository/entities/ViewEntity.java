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
package de.erdesignerng.model.serializer.repository.entities;

/**
 * Entity for a view.
 * 
 * @author mirkosertic
 */
public class ViewEntity extends ModelEntity {

	private String sqlstatement;

	private String schema;

	public String getSqlstatement() {
		return sqlstatement;
	}

	public void setSqlstatement(String sqlstatement) {
		this.sqlstatement = sqlstatement;
	}

	/**
	 * @return the schema
	 */
	public String getSchema() {
		return schema;
	}

	/**
	 * @param schema
	 *			the schema to set
	 */
	public void setSchema(String schema) {
		this.schema = schema;
	}
}
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

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-01-03 13:11:14 $
 */
public abstract class ModelItem {

	public static final String PROPERTY_LOCATION = "LOCATION";

	public static final String PROPERTY_REMARKS = "REMARKS";

	private String systemId = ModelUtilities.createSystemIdFor(this);

	private String name;

	private String comment;

	private ModelProperties properties = new ModelProperties();

	/**
	 * Get the name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name.
	 * 
	 * @param name
	 *            the name to set
	 */
	public void setName(String aName) {
		name = aName;
	}

	/**
	 * @return the systemId
	 */
	public String getSystemId() {
		return systemId;
	}

	/**
	 * @param systemId
	 *            the systemId to set
	 */
	public void setSystemId(String aSystemId) {
		systemId = aSystemId;
	}

	public ModelProperties getProperties() {
		return properties;
	}

	public void setProperties(ModelProperties properties) {
		this.properties = properties;
	}

	@Override
	public String toString() {
		return name;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
}

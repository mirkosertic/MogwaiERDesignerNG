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

import java.util.HashMap;

/**
 * Base class for all model elements.
 * 
 * @author Mirko Sertic <mail@mirkosertic.de>
 */
public abstract class ModelItem {

	public static final String PROPERTY_LOCATION = "LOCATION";

	public static final String PROPERTY_REMARKS = "REMARKS";

	protected String systemId = ModelUtilities.createSystemIdFor(this);

	protected String name;

	protected HashMap<String, String> properties = new HashMap<String, String>();

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
	public void setName(String name) {
		this.name = name;
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
	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	public HashMap<String, String> getProperties() {
		return properties;
	}

	public void setProperties(HashMap<String, String> properties) {
		this.properties = properties;
	}

	public void setProperty(String aName, String aValue) {
		properties.put(aName, aValue);
	}

	public String getProperty(String aName) {
		return properties.get(aName);
	}

	public int getIntProperty(String aName, int aDefault) {
		if (!properties.containsKey(aName)) {
			return aDefault;
		}

		return Integer.parseInt(properties.get(aName));
	}

	public void setIntProperty(String aName, int aValue) {
		properties.put(aName, "" + aValue);
	}
}

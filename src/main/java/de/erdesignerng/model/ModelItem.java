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

import java.io.Serializable;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-09 19:07:29 $
 */
public abstract class ModelItem implements Serializable {

	public static final String PROPERTY_LOCATION = "LOCATION";

	private String systemId = ModelUtilities.createSystemIdFor();

	private String name;

	private String originalName;

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
	 * @param aName the name to set
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
	 * @param aSystemId the systemId to set
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

	public boolean isRenamed(String aName) {
		if (name == null) {
			return aName != null;
		}
		return !name.equals(aName);
	}

	public boolean isCommentChanged(String aComment) {
		return isStringModified(comment, aComment);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ModelItem)) return false;

		ModelItem modelItem = (ModelItem) o;

		if (systemId != null ? !systemId.equals(modelItem.systemId) : modelItem.systemId != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return systemId != null ? systemId.hashCode() : 0;
	}

	protected boolean isStringModified(String aValue1, String aValue2) {
		if (aValue1 == null) {
			aValue1 = "";
		}
		if (aValue2 == null) {
			aValue2 = "";
		}
		return !aValue1.equals(aValue2);
	}

	public String getOriginalName() {
		return originalName;
	}

	public void setOriginalName(String originalName) {
		this.originalName = originalName;
	}

	/**
	 * Get the unique name of the model item.
	 *
	 * @return the name
	 */
	public String getUniqueName() {
		return getName();
	}
}
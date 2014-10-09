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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class for all model entities.
 * 
 * @author mirkosertic
 */
public abstract class ModelEntity {

	private Long id;

	private String systemId;

	private String name;

	private String comment;

	private Integer version;

	private String creationUser;

	private Timestamp creationDate;

	private String lastModificationUser;

	private Timestamp lastModificationDate;

	private List<StringKeyValuePair> properties = new ArrayList<>();

	/**
	 * @return the systemId
	 */
	public String getSystemId() {
		return systemId;
	}

	/**
	 * @param systemId
	 *			the systemId to set
	 */
	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *			the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment
	 *			the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * @return the version
	 */
	public Integer getVersion() {
		return version;
	}

	/**
	 * @param version
	 *			the version to set
	 */
	public void setVersion(Integer version) {
		this.version = version;
	}

	/**
	 * @return the creationUser
	 */
	public String getCreationUser() {
		return creationUser;
	}

	/**
	 * @param creationUser
	 *			the creationUser to set
	 */
	public void setCreationUser(String creationUser) {
		this.creationUser = creationUser;
	}

	/**
	 * @return the creationDate
	 */
	public Timestamp getCreationDate() {
		return creationDate;
	}

	/**
	 * @param creationDate
	 *			the creationDate to set
	 */
	public void setCreationDate(Timestamp creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * @return the lastModificationUser
	 */
	public String getLastModificationUser() {
		return lastModificationUser;
	}

	/**
	 * @param lastModificationUser
	 *			the lastModificationUser to set
	 */
	public void setLastModificationUser(String lastModificationUser) {
		this.lastModificationUser = lastModificationUser;
	}

	/**
	 * @return the lastModificationDate
	 */
	public Timestamp getLastModificationDate() {
		return lastModificationDate;
	}

	/**
	 * @param lastModificationDate
	 *			the lastModificationDate to set
	 */
	public void setLastModificationDate(Timestamp lastModificationDate) {
		this.lastModificationDate = lastModificationDate;
	}

	/**
	 * @return the properties
	 */
	public List<StringKeyValuePair> getProperties() {
		return properties;
	}

	/**
	 * @param properties
	 *			the properties to set
	 */
	public void setProperties(List<StringKeyValuePair> properties) {
		this.properties = properties;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id
	 *			the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
}
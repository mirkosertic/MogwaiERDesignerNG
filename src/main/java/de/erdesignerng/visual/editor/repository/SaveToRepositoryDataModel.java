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
package de.erdesignerng.visual.editor.repository;

import de.erdesignerng.model.serializer.repository.RepositoryEntryDescriptor;

/**
 * Datamodel for the save to repository dialog.
 * 
 * @author mirkosertic
 */
public class SaveToRepositoryDataModel {
	private String nameForNewEntry;

	private RepositoryEntryDescriptor existingEntry;

	private String nameForExistingEntry;

	/**
	 * @return the nameForNewEntry
	 */
	public String getNameForNewEntry() {
		return nameForNewEntry;
	}

	/**
	 * @param nameForNewEntry
	 *			the nameForNewEntry to set
	 */
	public void setNameForNewEntry(String nameForNewEntry) {
		this.nameForNewEntry = nameForNewEntry;
	}

	/**
	 * @return the existingEntry
	 */
	public RepositoryEntryDescriptor getExistingEntry() {
		return existingEntry;
	}

	/**
	 * @param existingEntry
	 *			the existingEntry to set
	 */
	public void setExistingEntry(RepositoryEntryDescriptor existingEntry) {
		this.existingEntry = existingEntry;
	}

	/**
	 * @return the nameForExistingEntry
	 */
	public String getNameForExistingEntry() {
		return nameForExistingEntry;
	}

	/**
	 * @param nameForExistingEntry
	 *			the nameForExistingEntry to set
	 */
	public void setNameForExistingEntry(String nameForExistingEntry) {
		this.nameForExistingEntry = nameForExistingEntry;
	}
}
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
 * Datamodel for the load from repository dialog.
 * 
 * @author mirkosertic
 */
public class LoadFromRepositoryDataModel {

	private RepositoryEntryDescriptor entry;

	/**
	 * @return the entry
	 */
	public RepositoryEntryDescriptor getEntry() {
		return entry;
	}

	/**
	 * @param entry
	 *			the entry to set
	 */
	public void setEntry(RepositoryEntryDescriptor entry) {
		this.entry = entry;
	}
}

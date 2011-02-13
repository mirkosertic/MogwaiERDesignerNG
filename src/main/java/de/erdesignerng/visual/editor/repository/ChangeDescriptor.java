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

import de.erdesignerng.model.serializer.repository.entities.ChangeEntity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * A change descriptor.
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-09 19:07:30 $
 */
public class ChangeDescriptor {

	private final ChangeEntity change;

	private final int index;

	public ChangeDescriptor(ChangeEntity aChange, int aIndex) {
		change = aChange;
		index = aIndex;
	}

	@Override
	public String toString() {
		DateFormat theFormat = new SimpleDateFormat();
		return "#" + index + " " + change.getCreationUser() + "@" + theFormat.format(change.getCreationDate());
	}

	public ChangeEntity getChange() {
		return change;
	}

	public int getIndex() {
		return index;
	}
}
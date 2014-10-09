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

import java.util.ArrayList;
import java.util.List;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-09 19:07:29 $
 */
public class SubjectAreaList extends ModelItemVector<SubjectArea> {

	/**
	 * Remove a table from the subject areas.
	 * 
	 * If a subject area has no tables, it is removed from the model.
	 * 
	 * @param aTable
	 *			the table
	 */
	public void removeTable(Table aTable) {

		List<SubjectArea> theRemovedAreas = new ArrayList<>();

		for (SubjectArea theArea : this) {
			theArea.getTables().remove(aTable);
			if (theArea.isEmpty()) {
				theRemovedAreas.add(theArea);
			}
		}

		removeAll(theRemovedAreas);
	}

	/**
	 * Remove a comment from all subject areas.
	 * 
	 * If a subject area is empty, it will be removed completely
	 * 
	 * @param aComment
	 *			the comment
	 */
	public void removeComment(Comment aComment) {
		List<SubjectArea> theRemovedAreas = new ArrayList<>();

		for (SubjectArea theArea : this) {
			theArea.getComments().remove(aComment);
			if (theArea.isEmpty()) {
				theRemovedAreas.add(theArea);
			}
		}

		removeAll(theRemovedAreas);
	}

	/**
	 * Remove a view from the subject areas.
	 * 
	 * If a subject area has no tables, it is removed from the model.
	 * 
	 * @param aView
	 *			the view
	 */
	public void removeView(View aView) {
		List<SubjectArea> theRemovedAreas = new ArrayList<>();

		for (SubjectArea theArea : this) {
			theArea.getViews().remove(aView);
			if (theArea.isEmpty()) {
				theRemovedAreas.add(theArea);
			}
		}

		removeAll(theRemovedAreas);
	}
}
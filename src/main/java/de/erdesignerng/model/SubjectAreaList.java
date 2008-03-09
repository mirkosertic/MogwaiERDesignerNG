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
 * @version $Date: 2008-03-09 18:20:28 $
 */
public class SubjectAreaList extends ModelItemVector<SubjectArea> {

    /**
     * Remove a table from the subject areas.
     * 
     * If a subject area has no tables, it is removed from the model.
     * 
     * @param aTable the table
     */
    public void removeTable(Table aTable) {
        
        List<SubjectArea> theRemovedAreas = new ArrayList<SubjectArea>();
        
        for (SubjectArea theArea : this) {
            theArea.getTables().remove(aTable);
            if (theArea.getTables().size() == 0) {
                theRemovedAreas.add(theArea);
            }
        }
        
        removeAll(theRemovedAreas);
    }
}
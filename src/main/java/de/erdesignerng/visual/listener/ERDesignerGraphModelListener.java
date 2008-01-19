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
package de.erdesignerng.visual.listener;

import java.util.Map;

import org.jgraph.event.GraphModelEvent;
import org.jgraph.event.GraphModelListener;
import org.jgraph.event.GraphLayoutCacheEvent.GraphLayoutCacheChange;

import de.erdesignerng.visual.cells.ModelCell;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-01-19 15:25:31 $
 */
final class ERDesignerGraphModelListener implements GraphModelListener {
    public void graphChanged(GraphModelEvent aEvent) {
        GraphLayoutCacheChange theChange = aEvent.getChange();

        Object[] theChangedObjects = theChange.getChanged();
        Map theChangedAttributes = theChange.getPreviousAttributes();
        if (theChangedAttributes != null) {
            for (Object theChangedObject : theChangedObjects) {
                Map theAttributes = (Map) theChangedAttributes.get(theChangedObject);

                if (theChangedObject instanceof ModelCell) {

                    ModelCell theCell = (ModelCell) theChangedObject;
                    theCell.transferAttributesToProperties(theAttributes);
                }
            }
        }

    }
}
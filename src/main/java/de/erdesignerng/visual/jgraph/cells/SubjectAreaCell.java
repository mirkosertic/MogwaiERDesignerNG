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
package de.erdesignerng.visual.jgraph.cells;

import de.erdesignerng.model.SubjectArea;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;

import java.util.Map;

/**
 * A subject area.
 *
 * @author $Author: mirkosertic $
 * @version $Date: 2008-06-13 16:48:59 $
 */
public class SubjectAreaCell extends DefaultGraphCell implements ModelCell<SubjectArea>, HideableCell {

    public SubjectAreaCell(SubjectArea aArea) {
        super(aArea);

        GraphConstants.setAutoSize(getAttributes(), true);
        GraphConstants.setEditable(getAttributes(), true);
        GraphConstants.setInset(getAttributes(), 20);
        GraphConstants.setGroupOpaque(getAttributes(), true);
    }

    @Override
    public void transferAttributesToProperties(Map aAttributes) {
    }

    @Override
    public void transferPropertiesToAttributes(SubjectArea aObject) {
    }

    public boolean isExpanded() {
        SubjectArea theArea = (SubjectArea) getUserObject();
        return theArea.isExpanded();
    }

    public void setExpanded(boolean aExpanded) {
        SubjectArea theArea = (SubjectArea) getUserObject();
        theArea.setExpanded(aExpanded);
    }
}
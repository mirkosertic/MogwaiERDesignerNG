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
package de.erdesignerng.visual.jgraph.cells.views;

import org.jgraph.JGraph;
import org.jgraph.graph.GraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.PortView;

import java.awt.geom.Rectangle2D;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-06-13 16:48:58 $
 */
public class DefaultPortView extends PortView {

    public DefaultPortView(Object aObject) {
        super(aObject);
    }

    @Override
    public boolean intersects(JGraph aGraph, Rectangle2D aRect) {
        GraphCell theCell = (GraphCell) getParentView().getCell();
        Rectangle2D theBounds = GraphConstants.getBounds(theCell.getAttributes());

        return theBounds.contains(aRect.getX(), aRect.getY());
    }

}

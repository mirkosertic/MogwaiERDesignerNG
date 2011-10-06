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
package de.erdesignerng.visual.jgraph.tools;

import de.erdesignerng.visual.common.GenericModelEditor;
import de.erdesignerng.visual.jgraph.ERDesignerGraph;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-09 19:07:30 $
 */
public class ViewTool extends BaseTool {

    public ViewTool(GenericModelEditor aEditor, ERDesignerGraph aGraph) {
        super(aEditor, aGraph);
    }

    @Override
    public boolean isForceMarqueeEvent(MouseEvent event) {
        return true;
    }

    @Override
    public boolean startCreateNew(MouseEvent e) {
        graph.commandNewView(graph.fromScreen(new Point2D.Double(e.getX(), e.getY())));
        return true;
    }
}

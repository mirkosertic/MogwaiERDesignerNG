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
package de.erdesignerng.visual.tools;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import de.erdesignerng.visual.ERDesignerGraph;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-01-15 19:22:44 $
 */
public class EntityTool extends BaseTool {

    public EntityTool(ERDesignerGraph aGraph) {
        super(aGraph);
    }

    @Override
    public boolean isForceMarqueeEvent(MouseEvent event) {
        return true;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        graph.commandNewTable(new Point2D.Double(e.getX(), e.getY()));
    }
}
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
package de.mogwai.erdesignerng.visual;

import java.awt.geom.Point2D;

import org.jgraph.JGraph;
import org.jgraph.graph.GraphCell;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;

import de.mogwai.erdesignerng.visual.tools.BaseTool;

/**
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2007-07-08 10:06:43 $
 */
public class ERDesignerGraph extends JGraph {

	private BaseTool tool;

	public ERDesignerGraph(GraphModel aModel, GraphLayoutCache aLayoutCache) {
		super(aModel, aLayoutCache);
		//setPortsVisible(true);
	}

	public void setTool(BaseTool aTool) {
		setMarqueeHandler(aTool);
		tool = aTool;
	}

	public void commandDeleteCell(GraphCell aCell) {
	}
	
	public void commandNewTable(Point2D aPoint) {
	}
}

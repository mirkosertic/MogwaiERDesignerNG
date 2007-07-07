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

import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import org.jgraph.JGraph;
import org.jgraph.graph.BasicMarqueeHandler;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;


public class ERDesignerGraph extends JGraph {
	
	public class MyMarqueeHandler extends BasicMarqueeHandler {

		@Override
		public boolean isForceMarqueeEvent(MouseEvent e) { 
		       if (SwingUtilities.isRightMouseButton(e)) { 
		             return true; 
		       }
		       return super.isForceMarqueeEvent(e); 
		 } 
		
		@Override
		 public void mousePressed(final MouseEvent e) { 
		       if (SwingUtilities.isRightMouseButton(e)) { 
		             Object cell = getFirstCellForLocation(e.getX(), e.getY()); 
		             JPopupMenu menu = createPopupMenu(e.getPoint(), cell); 
		             menu.show(ERDesignerGraph.this, e.getX(), e.getY()); 
		       } else { 
		             super.mousePressed(e); 
		       } 
		 }		
	}

	public ERDesignerGraph(GraphModel aModel, GraphLayoutCache aLayoutCache) {
		super(aModel, aLayoutCache);
		setMarqueeHandler(new MyMarqueeHandler());
	}
	
	protected JPopupMenu createPopupMenu(Point aPoint,Object aCell) {
		
		if (aCell !=null) {
			System.out.println(aCell.getClass());
		} else {
			System.out.println("No cell");
		}
		
		JPopupMenu theMenu = new JPopupMenu();
		theMenu.add("Lala");
		return theMenu;
	}
}

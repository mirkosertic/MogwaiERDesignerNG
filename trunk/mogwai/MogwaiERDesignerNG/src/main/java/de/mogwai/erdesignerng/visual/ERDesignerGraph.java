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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import javax.swing.Action;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import org.jgraph.JGraph;
import org.jgraph.graph.BasicMarqueeHandler;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;

public class ERDesignerGraph extends JGraph {

	private Action deleteAction = new GenericAction("Delete", new ActionListener() {

		public void actionPerformed(ActionEvent e) {
			commandDelete(clickedCell);
		}
		
	});
	private Action newTableAction = new GenericAction("New Table",new ActionListener() {

		public void actionPerformed(ActionEvent e) {
			commandNewTable(clickedPoint);
		}
	});
	
	private Object clickedCell;
	private Point2D clickedPoint;
	
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
				
				clickedPoint = new Point2D.Double(e.getX(),e.getY());
				clickedCell = getFirstCellForLocation(e.getX(), e.getY());
				JPopupMenu menu = createPopupMenu(e.getPoint(), clickedCell);
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

	protected JPopupMenu createPopupMenu(Point aPoint, Object aCell) {

		JPopupMenu theMenu = new JPopupMenu();
		
		if (aCell != null) {
			theMenu.add(deleteAction);
			theMenu.addSeparator();
		} 
			
		theMenu.add(newTableAction);

		return theMenu;
	}
	
	protected void commandNewTable(Point2D aLocation) {
		
	}
	
	protected void commandDelete(Object aCell) {
	}
}

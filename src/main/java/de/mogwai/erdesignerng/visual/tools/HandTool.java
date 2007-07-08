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
package de.mogwai.erdesignerng.visual.tools;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import org.jgraph.graph.GraphCell;

import de.mogwai.erdesignerng.visual.ERDesignerGraph;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2007-07-08 10:06:38 $
 */
public class HandTool extends BaseTool {
	
	public HandTool(ERDesignerGraph aGraph) {
		super(aGraph);
	}

	@Override
	public boolean isForceMarqueeEvent(MouseEvent e) {
		if (SwingUtilities.isRightMouseButton(e)) {
			return true;
		}
		return super.isForceMarqueeEvent(e);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (SwingUtilities.isRightMouseButton(e)) {

			GraphCell theClickedCell = (GraphCell) graph.getFirstCellForLocation(e.getX(), e
					.getY());

			if (theClickedCell != null) {
				JPopupMenu menu = createPopupMenu(e.getPoint(), theClickedCell);
				menu.show(graph, e.getX(), e.getY());
			}
		} else {
			super.mousePressed(e);
		}
	}
	
	public JPopupMenu createPopupMenu(Point aPoint,final GraphCell aCell) {

		JPopupMenu theMenu = new JPopupMenu();

		JMenuItem theItem = theMenu.add("Delete");
		theItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				graph.commandDeleteCell(aCell);
			}
		});

		return theMenu;
	}
}

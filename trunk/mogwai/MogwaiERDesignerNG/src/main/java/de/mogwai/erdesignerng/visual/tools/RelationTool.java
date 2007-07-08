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

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.Port;
import org.jgraph.graph.PortView;

import de.mogwai.erdesignerng.model.Relation;
import de.mogwai.erdesignerng.model.Table;
import de.mogwai.erdesignerng.visual.ERDesignerGraph;
import de.mogwai.erdesignerng.visual.cells.RelationEdge;
import de.mogwai.erdesignerng.visual.cells.TableCell;
import de.mogwai.erdesignerng.visual.editor.relation.RelationEditor;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2007-07-08 17:55:42 $
 */
public class RelationTool extends BaseTool {

	protected Point2D start, current;

	protected PortView port, firstPort;

	public RelationTool(ERDesignerGraph aGraph) {
		super(aGraph);
	}

	public boolean isForceMarqueeEvent(MouseEvent e) {
		if (e.isShiftDown())
			return false;

		if (SwingUtilities.isRightMouseButton(e))
			return true;

		port = getSourcePortAt(e.getPoint());
		if (port != null)
			return true;

		return super.isForceMarqueeEvent(e);
	}

	public void mousePressed(final MouseEvent e) {

		if (SwingUtilities.isRightMouseButton(e)) {
		} else if (port != null) {
			start = graph.toScreen(port.getLocation());
			firstPort = port;
		} else {
			super.mousePressed(e);
		}
	}

	public void mouseDragged(MouseEvent e) {
		if (start != null) {
			Graphics g = graph.getGraphics();
			PortView newPort = getTargetPortAt(e.getPoint());
			if (newPort == null || newPort != port) {
				paintConnector(Color.black, graph.getBackground(), g);
				port = newPort;
				if (port != null)
					current = graph.toScreen(port.getLocation());
				else
					current = graph.snap(e.getPoint());
				paintConnector(graph.getBackground(), Color.black, g);
			}
		}
		super.mouseDragged(e);
	}

	public PortView getSourcePortAt(Point2D point) {
		graph.setJumpToDefaultPort(false);
		PortView result;
		try {
			result = graph.getPortViewAt(point.getX(), point.getY());
		} finally {
			graph.setJumpToDefaultPort(true);
		}
		return result;
	}

	protected PortView getTargetPortAt(Point2D point) {
		return graph.getPortViewAt(point.getX(), point.getY());
	}

	public void mouseReleased(MouseEvent e) {
		if (e != null && port != null && firstPort != null) {
			connect((Port) firstPort.getCell(), (Port) port.getCell());
			e.consume();
		} else
			graph.repaint();
		firstPort = port = null;
		start = current = null;
		super.mouseReleased(e);
	}

	public void mouseMoved(MouseEvent e) {
		if (e != null && getSourcePortAt(e.getPoint()) != null) {
			graph.setCursor(new Cursor(Cursor.HAND_CURSOR));
			e.consume();
		} else
			super.mouseMoved(e);
	}

	protected void paintConnector(Color fg, Color bg, Graphics g) {
		g.setColor(fg);
		g.setXORMode(bg);
		paintPort(graph.getGraphics());
		if (firstPort != null && start != null && current != null)
			g.drawLine((int) start.getX(), (int) start.getY(), (int) current
					.getX(), (int) current.getY());
	}

	protected void paintPort(Graphics g) {
		if (port != null) {
			boolean o = (GraphConstants.getOffset(port.getAllAttributes()) != null);
			Rectangle2D r = (o) ? port.getBounds() : port.getParentView()
					.getBounds();
			r = graph.toScreen((Rectangle2D) r.clone());
			r.setFrame(r.getX() - 3, r.getY() - 3, r.getWidth() + 6, r
					.getHeight() + 6);
			graph.getUI().paintCell(g, port, r, true);
		}
	}

	public void connect(Port aSource, Port aTarget) {
		// Construct Edge with no label
		GraphCell theSourceCell = (GraphCell) ((DefaultPort) aSource)
				.getParent();
		GraphCell theTargetCell = (GraphCell) ((DefaultPort) aTarget)
				.getParent();
		if ((theSourceCell instanceof TableCell)
				&& (theTargetCell instanceof TableCell)) {
			Table theSourceTable = (Table) ((TableCell) theSourceCell)
					.getUserObject();
			Table theTargetTable = (Table) ((TableCell) theTargetCell)
					.getUserObject();

			Relation theRelation = new Relation();
			theRelation.setImportingTable(theSourceTable);
			theRelation.setExportingTable(theTargetTable);

			RelationEditor theEditor = new RelationEditor(theSourceTable
					.getOwner(), (JFrame) SwingUtilities.getRoot(graph));
			theEditor.initializeFor(theRelation);
			if (theEditor.showModal() == RelationEditor.MODAL_RESULT_OK) {

				RelationEdge theEdge = new RelationEdge(theRelation,
						(TableCell) theSourceCell, (TableCell) theTargetCell);

				try {
					theEditor.applyValues();
					graph.getGraphLayoutCache().insert(theEdge);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}

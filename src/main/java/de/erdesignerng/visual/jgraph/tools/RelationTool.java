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

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.model.Table;
import de.erdesignerng.visual.MessagesHelper;
import de.erdesignerng.visual.common.GenericModelEditor;
import de.erdesignerng.visual.jgraph.ERDesignerGraph;
import de.erdesignerng.visual.jgraph.cells.TableCell;
import de.mogwai.common.client.looks.UIInitializer;
import de.mogwai.common.client.looks.components.DefaultPopupMenu;
import de.mogwai.common.client.looks.components.action.DefaultAction;
import de.mogwai.common.client.looks.components.menu.DefaultMenuItem;
import de.mogwai.common.i18n.ResourceHelper;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.Port;
import org.jgraph.graph.PortView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-09-23 18:13:28 $
 */
public class RelationTool extends BaseTool {

    protected Point2D start;

    protected Point2D current;

    protected PortView port;

    protected PortView firstPort;

    public RelationTool(GenericModelEditor aEditor, ERDesignerGraph aGraph) {
        super(aEditor, aGraph);
    }

    @Override
    public boolean isForceMarqueeEvent(MouseEvent e) {
        if (e.isShiftDown()) {
            return false;
        }

        if (SwingUtilities.isRightMouseButton(e)) {
            return true;
        }

        port = getSourcePortAt(e.getPoint());
        if (port != null) {
            return true;
        }

        return super.isForceMarqueeEvent(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {

        if (SwingUtilities.isRightMouseButton(e)) {
            // Do nothing here
        } else if (port != null) {
            start = graph.toScreen(port.getLocation());
            firstPort = port;
        } else {
            super.mousePressed(e);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (start != null) {
            Graphics g = graph.getGraphics();
            PortView newPort = getTargetPortAt(e.getPoint());
            if (newPort == null || newPort != port) {
                paintConnector(Color.black, graph.getBackground(), g);
                port = newPort;
                if (port != null) {
                    current = graph.toScreen(port.getLocation());
                } else {
                    current = graph.snap(e.getPoint());
                }
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

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e != null && port != null && firstPort != null) {
            connect((Port) firstPort.getCell(), (Port) port.getCell());
            e.consume();
        } else {
            if (firstPort != null) {
                DefaultPort thePort = (DefaultPort) firstPort.getCell();
                GraphCell theCell = (GraphCell) thePort.getParent();
                if (theCell instanceof TableCell) {
                    DefaultPopupMenu menu = createPopupMenu(graph.fromScreen(new Point2D.Double(e.getX(), e.getY())),
                            (TableCell) theCell);
                    menu.show(graph, e.getX(), e.getY());
                }
            }
        }
        firstPort = null;
        port = null;
        start = null;
        current = null;
        super.mouseReleased(e);

        graph.repaint();
    }

    private DefaultPopupMenu createPopupMenu(final Point2D aLocation, final TableCell aParentCell) {

        DefaultPopupMenu theMenu = new DefaultPopupMenu(ResourceHelper.getResourceHelper(ERDesignerBundle.BUNDLE_NAME));

        DefaultAction theAddChildTableAction = new DefaultAction(ERDesignerBundle.BUNDLE_NAME,
                ERDesignerBundle.CREATECHILDTABLEHERE);
        DefaultMenuItem theAddChildTableMenu = new DefaultMenuItem(theAddChildTableAction);
        theAddChildTableAction.addActionListener(e -> graph.commandNewTableAndRelation(aLocation, aParentCell, true));

        theMenu.add(theAddChildTableMenu);

        DefaultAction theAddParentTableAction = new DefaultAction(ERDesignerBundle.BUNDLE_NAME,
                ERDesignerBundle.CREATEPARENTTABLEHERE);
        DefaultMenuItem theAddParentTableMenu = new DefaultMenuItem(theAddParentTableAction);
        theAddParentTableMenu.addActionListener(e -> graph.commandNewTableAndRelation(aLocation, aParentCell, false));

        theMenu.add(theAddParentTableMenu);

        UIInitializer.getInstance().initialize(theMenu);

        return theMenu;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (e != null && getSourcePortAt(e.getPoint()) != null) {
            graph.setCursor(new Cursor(Cursor.HAND_CURSOR));
            e.consume();
        } else {
            super.mouseMoved(e);
        }
    }

    protected void paintConnector(Color fg, Color bg, Graphics g) {
        g.setColor(fg);
        g.setXORMode(bg);
        paintPort(graph.getGraphics());
        if (firstPort != null && start != null && current != null) {
            g.drawLine((int) start.getX(), (int) start.getY(), (int) current.getX(), (int) current.getY());
        }
    }

    protected void paintPort(Graphics g) {
        if (port != null) {
            boolean o = (GraphConstants.getOffset(port.getAllAttributes()) != null);
            Rectangle2D r = (o) ? port.getBounds() : port.getParentView().getBounds();
            r = graph.toScreen((Rectangle2D) r.clone());
            r.setFrame(r.getX() - 3, r.getY() - 3, r.getWidth() + 6, r.getHeight() + 6);
            graph.getUI().paintCell(g, port, r, true);
        }
    }

    public void connect(Port aSource, Port aTarget) {
        // Construct Edge with no label
        GraphCell theSourceCell = (GraphCell) ((DefaultPort) aSource).getParent();
        GraphCell theTargetCell = (GraphCell) ((DefaultPort) aTarget).getParent();
        if ((theSourceCell instanceof TableCell) && (theTargetCell instanceof TableCell)) {

            Table theTargetTable = (Table) ((TableCell) theTargetCell).getUserObject();
            if (theTargetTable.hasPrimaryKey()) {
                graph.commandNewRelation((TableCell) theSourceCell, (TableCell) theTargetCell);
                graph.repaint();
            } else {
                MessagesHelper.displayErrorMessage(graph, getResourceHelper().getText(
                        ERDesignerBundle.EXPORTINGTABLENEEDSPRIMARYKEY));
            }
        }
    }
}
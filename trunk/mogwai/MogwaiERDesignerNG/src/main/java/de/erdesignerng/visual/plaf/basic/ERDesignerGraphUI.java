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
package de.erdesignerng.visual.plaf.basic;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.jgraph.JGraph;
import org.jgraph.graph.AbstractCellView;
import org.jgraph.graph.CellView;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.plaf.basic.BasicGraphUI;

import de.erdesignerng.visual.ERDesignerGraph;
import de.erdesignerng.visual.cells.ModelCell;
import de.erdesignerng.visual.cells.views.TableCellView;
import de.erdesignerng.visual.common.ERDesignerComponent;
import de.erdesignerng.visual.editor.BaseEditor;
import de.erdesignerng.visual.editor.DialogConstants;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-13 15:40:33 $
 */
public class ERDesignerGraphUI extends BasicGraphUI {

    private static final Logger LOGGER = Logger.getLogger(ERDesignerGraphUI.class);

    private ERDesignerComponent erdesigner;

    public ERDesignerGraphUI(ERDesignerComponent aComponent) {
        erdesigner = aComponent;
    }

    public class MyMouseHandler extends MouseHandler {

        @Override
        public void mousePressed(MouseEvent e) {
            handler = null;
            if (!e.isConsumed() && graph.isEnabled()) {
                graph.requestFocus();
                int s = graph.getTolerance();
                Rectangle2D theRectangle = graph.fromScreen(new Rectangle2D.Double(e.getX() - s, e.getY() - s, 2 * s,
                        2 * s));
                lastFocus = focus;
                focus = (focus != null && focus.intersects(graph, theRectangle)) ? focus : null;
                cell = graph.getNextSelectableViewAt(focus, e.getX(), e.getY());
                if ((cell != null) && (cell.getChildViews() != null) && (cell.getChildViews().length > 0)) {
                    CellView theTemp = graph.getNextViewAt(cell.getChildViews(), focus, e.getX(), e.getY());
                    if (theTemp != null) {
                        cell = theTemp;
                    }
                }
                // if (focus == null) {
                focus = cell;
                // }
                completeEditing();
                boolean isForceMarquee = isForceMarqueeEvent(e);
                boolean isEditable = graph.isGroupsEditable() || (focus != null && focus.isLeaf());
                if (!isForceMarquee) {
                    if (e.getClickCount() == graph.getEditClickCount() && focus != null && isEditable
                            && graph.isCellEditable(focus.getCell()) && handleEditTrigger(cell.getCell(), e)) {
                        e.consume();
                        cell = null;
                    } else if (!isToggleSelectionEvent(e)) {
                        if (handle != null) {
                            handle.mousePressed(e);
                            handler = handle;
                        }
                        // Immediate Selection
                        if (!e.isConsumed() && cell != null && !graph.isCellSelected(cell.getCell())) {
                            selectCellForEvent(cell.getCell(), e);
                            focus = cell;
                            if (handle != null) {
                                handle.mousePressed(e);
                                handler = handle;
                            }
                            e.consume();
                            cell = null;
                        }
                    }
                }
                // Marquee Selection
                if (!e.isConsumed() && marquee != null
                        && (!isToggleSelectionEvent(e) || focus == null || isForceMarquee)) {
                    marquee.mousePressed(e);
                    handler = marquee;
                }
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            super.mouseDragged(e);

            ERDesignerGraph theGraph = (ERDesignerGraph) graph;
            theGraph.setDragging(true);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            super.mouseReleased(e);

            ERDesignerGraph theGraph = (ERDesignerGraph) graph;
            theGraph.setDragging(false);

            if (focus.getCell() instanceof ModelCell) {
                ((ModelCell)focus.getCell()).transferAttributesToProperties(focus.getAllAttributes());
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == graph.getEditClickCount()) {

                int s = graph.getTolerance();

                Rectangle2D theRectangle = graph.fromScreen(new Rectangle2D.Double(e.getX() - s, e.getY() - s, 2 * s,
                        2 * s));
                lastFocus = focus;
                focus = (focus != null && focus.intersects(graph, theRectangle)) ? focus : null;
                cell = graph.getNextSelectableViewAt(focus, e.getX(), e.getY());
                if ((cell != null) && (cell.getChildViews() != null) && (cell.getChildViews().length > 0)) {
                    CellView theTemp = graph.getNextViewAt(cell.getChildViews(), focus, e.getX(), e.getY());
                    if (theTemp != null) {
                        cell = theTemp;
                    }
                }
                if (focus == null) {
                    focus = cell;
                }

                if (cell != null) {
                    if (handleEditTrigger(cell.getCell(), e)) {
                        e.consume();
                    }
                }
            }
        }

        @Override
        protected void postProcessSelection(MouseEvent e, Object cell, boolean wasSelected) {
        }
    };

    @Override
    protected MouseListener createMouseListener() {
        return new MyMouseHandler();
    }

    @Override
    protected boolean startEditing(Object cell, MouseEvent event) {
        completeEditing();

        if (graph.isCellEditable(cell)) {
            CellView tmp = graphLayoutCache.getMapping(cell, false);
            cellEditor = tmp.getEditor();
            editingComponent = cellEditor.getGraphCellEditorComponent(graph, cell, graph.isCellSelected(cell));
            if (cellEditor.isCellEditable(event)) {

                editingCell = cell;
                editingComponent.validate();

                if (cellEditor.shouldSelectCell(event) && graph.isSelectionEnabled()) {
                    stopEditingInCompleteEditing = false;
                    try {
                        graph.setSelectionCell(cell);
                    } catch (Exception e) {
                        LOGGER.error("Error setting selection cell", e);
                    }
                    stopEditingInCompleteEditing = true;
                }

                /*
                 * Find the component that will get forwarded all the mouse
                 * events until mouseReleased.
                 */
                Point componentPoint = SwingUtilities.convertPoint(graph, new Point(event.getX(), event.getY()),
                        editingComponent);

                /*
                 * Create an instance of BasicTreeMouseListener to handle
                 * passing the mouse/motion events to the necessary component.
                 */
                // We really want similiar behavior to getMouseEventTarget,
                // but it is package private.
                Component activeComponent = SwingUtilities.getDeepestComponentAt(editingComponent, componentPoint.x,
                        componentPoint.y);
                if (activeComponent != null) {
                    new MouseInputHandler(graph, activeComponent, event);
                }

                BaseEditor theDialog = (BaseEditor) editingComponent;
                if (theDialog.showModal() == DialogConstants.MODAL_RESULT_OK) {
                    try {
                        theDialog.applyValues();

                        erdesigner.commandNotifyAboutEdit();
                    } catch (Exception e1) {
                        erdesigner.getWorldConnector().notifyAboutException(e1);
                    }
                }

                editingComponent = null;

                // Mark the cell as edited, so it is resized during
                // redisplay to its new preferred size
                DefaultGraphCell theCell = (DefaultGraphCell) editingCell;
                graph.getGraphLayoutCache().editCell(theCell, theCell.getAttributes());

                graph.invalidate();
                graph.repaint();
            } else {
                editingComponent = null;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEditing(JGraph graph) {
        return false;
    }
}

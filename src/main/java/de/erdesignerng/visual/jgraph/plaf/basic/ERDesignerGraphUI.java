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
package de.erdesignerng.visual.jgraph.plaf.basic;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.model.ModelItem;
import de.erdesignerng.visual.common.ContextMenuFactory;
import de.erdesignerng.visual.common.ERDesignerComponent;
import de.erdesignerng.visual.common.OutlineComponent;
import de.erdesignerng.visual.editor.BaseEditor;
import de.erdesignerng.visual.editor.DialogConstants;
import de.erdesignerng.visual.jgraph.ERDesignerGraph;
import de.erdesignerng.visual.jgraph.JGraphEditor;
import de.erdesignerng.visual.jgraph.cells.SubjectAreaCell;
import de.erdesignerng.visual.jgraph.cells.views.SubjectAreaCellView;
import de.erdesignerng.visual.jgraph.tools.BaseTool;
import de.mogwai.common.client.looks.UIInitializer;
import de.mogwai.common.client.looks.components.DefaultPopupMenu;
import de.mogwai.common.i18n.ResourceHelper;
import org.apache.log4j.Logger;
import org.jgraph.JGraph;
import org.jgraph.graph.CellView;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.EdgeView;
import org.jgraph.plaf.basic.BasicGraphUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-13 15:40:33 $
 */
public class ERDesignerGraphUI extends BasicGraphUI {

    private static final Logger LOGGER = Logger.getLogger(ERDesignerGraphUI.class);

    private final JGraphEditor erdesigner;

    public ERDesignerGraphUI(JGraphEditor aComponent) {
        erdesigner = aComponent;
    }

    @Override
    protected void installListeners() {
        super.installListeners();
        if (mouseListener instanceof MouseWheelListener) {
            graph.addMouseWheelListener((MouseWheelListener) mouseListener);
        }
    }

    @Override
    protected void uninstallListeners() {
        super.uninstallListeners();
        if (mouseListener instanceof MouseWheelListener) {
            graph.removeMouseWheelListener((MouseWheelListener) mouseListener);
        }
    }

    public class MyMouseHandler extends MouseHandler {

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            if (e.isControlDown()) {
                if (e.getWheelRotation() < 0) {
                    erdesigner.commandZoomOneLevelIn();
                } else {
                    erdesigner.commandZoomOneLevelOut();
                }
            }
        }

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
        }

        @Override
        public void mouseClicked(MouseEvent e) {

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

            // Check for single click on a cell
            if (e.getClickCount() == 1 && !SwingUtilities.isRightMouseButton(e)) {
                // Check if the user clicked a subject area
                if (cell instanceof SubjectAreaCellView) {
                    SubjectAreaCellView theView = (SubjectAreaCellView) cell;
                    SubjectAreaCell theCell = (SubjectAreaCell) theView.getCell();

                    Rectangle2D theBounds = theView.getBounds();
                    Point2D theClickPoint = graph.fromScreen(e.getPoint());

                    // Check if user clicked on top left corner
                    if ((theClickPoint.getX() > theBounds.getX() + 5 && theClickPoint.getX() < theBounds.getX() + 20)
                            && (theClickPoint.getY() > theBounds.getY() + 5 && theClickPoint.getY() < theBounds.getY() + 20)) {

                        ERDesignerGraph theGraph = (ERDesignerGraph) graph;

                        // Yes, so toggle collapsed / expanded state
                        if (!theCell.isExpanded()) {
                            theGraph.setSubjectAreaCellExpanded(theCell);
                        } else {
                            theGraph.setSubjectAreaCellCollapsed(theCell);
                        }
                    }
                }
            }

            if (e.getClickCount() == graph.getEditClickCount() && !SwingUtilities.isRightMouseButton(e)) {

                if (cell != null) {
                    if (handleEditTrigger(cell.getCell(), e)) {
                        e.consume();
                    }
                }
            }

            if (cell != null) {
                graph.scrollCellToVisible(cell.getCell());

                if (SwingUtilities.isRightMouseButton(e)) {
                    List<DefaultGraphCell> theSelectionList = new ArrayList<>();
                    for (Object theSelection : graph.getSelectionCells()) {
                        if (theSelection instanceof DefaultGraphCell) {
                            theSelectionList.add((DefaultGraphCell) theSelection);
                        }
                    }

                    DefaultPopupMenu theMenu = createPopupMenu(theSelectionList);
                    theMenu.show(graph, e.getX(), e.getY());
                }
                e.consume();
            } else {
                if (marquee instanceof BaseTool) {
                    BaseTool theTool = (BaseTool) marquee;
                    if (theTool.startCreateNew(e)) {
                        e.consume();
                    }
                }
            }
        }

        @Override
        protected void postProcessSelection(MouseEvent aEvent, Object aCell, boolean aWasSelected) {
        }
    }

    private DefaultPopupMenu createPopupMenu(List<DefaultGraphCell> aCells) {

        DefaultPopupMenu theMenu = new DefaultPopupMenu(ResourceHelper
                .getResourceHelper(ERDesignerBundle.BUNDLE_NAME));

        List<ModelItem> theItems = aCells.stream().map(theCell -> (ModelItem) theCell.getUserObject()).collect(Collectors.toList());

        ContextMenuFactory.addActionsToMenu(erdesigner, theMenu, theItems);

        UIInitializer.getInstance().initialize(theMenu);

        return theMenu;
    }

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
                // We really want similar behavior to getMouseEventTarget,
                // but it is package private.
                Component activeComponent = SwingUtilities.getDeepestComponentAt(editingComponent, componentPoint.x,
                        componentPoint.y);
                if (activeComponent != null) {
                    new MouseInputHandler(graph, activeComponent, event);
                }

                BaseEditor theDialog = (BaseEditor) editingComponent;
                event.consume();
                if (theDialog.showModal() == DialogConstants.MODAL_RESULT_OK) {
                    try {
                        theDialog.applyValues();

                        erdesigner.commandNotifyAboutEdit();

                        OutlineComponent.getDefault().refresh(ERDesignerComponent.getDefault().getModel());
                    } catch (Exception e1) {
                        ERDesignerComponent.getDefault().getWorldConnector().notifyAboutException(e1);
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

    @Override
    public boolean isEditing(JGraph aGraph) {
        return false;
    }

    /**
     * We draw the edges on top of the other elements.
     *
     * @param g
     * @param realClipBounds
     */
    @Override
    protected void paintCells(Graphics g, Rectangle2D realClipBounds) {
        CellView[] views = graphLayoutCache.getRoots();
        List<CellView> edges = new ArrayList<>();

        // Draw everything except of edges
        for (CellView theView : views) {
            if (theView instanceof EdgeView) {
                edges.add(theView);
            } else {
                Rectangle2D bounds = theView.getBounds();
                if (bounds != null) {
                    if (realClipBounds == null) {
                        paintCell(g, theView, bounds, false);
                    } else if (bounds.intersects(realClipBounds)) {
                        paintCell(g, theView, bounds, false);
                    }
                }
            }
        }

        // Finally draw the edges
        for (CellView theView : edges) {
            Rectangle2D bounds = theView.getBounds();
            if (bounds != null) {
                if (realClipBounds == null) {
                    paintCell(g, theView, bounds, false);
                } else if (bounds.intersects(realClipBounds)) {
                    paintCell(g, theView, bounds, false);
                }
            }
        }
    }
}
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
package de.erdesignerng.visual.jgraph;

import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.JGraphLayout;
import com.jgraph.layout.hierarchical.JGraphHierarchicalLayout;
import com.jgraph.layout.organic.JGraphFastOrganicLayout;
import com.jgraph.layout.organic.JGraphOrganicLayout;
import com.jgraph.layout.organic.JGraphSelfOrganizingOrganicLayout;
import com.jgraph.layout.simple.SimpleGridLayout;
import com.jgraph.layout.tree.JGraphRadialTreeLayout;
import com.jgraph.layout.tree.JGraphTreeLayout;
import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.model.*;
import de.erdesignerng.modificationtracker.VetoException;
import de.erdesignerng.util.ApplicationPreferences;
import de.erdesignerng.util.SQLUtils;
import de.erdesignerng.visual.*;
import de.erdesignerng.visual.common.*;
import de.erdesignerng.visual.jgraph.cells.*;
import de.erdesignerng.visual.jgraph.cells.views.CellViewFactory;
import de.erdesignerng.visual.jgraph.cells.views.RelationEdgeView;
import de.erdesignerng.visual.jgraph.cells.views.TableCellView;
import de.erdesignerng.visual.jgraph.export.Exporter;
import de.erdesignerng.visual.jgraph.export.ImageExporter;
import de.erdesignerng.visual.jgraph.export.SVGExporter;
import de.erdesignerng.visual.jgraph.plaf.basic.ERDesignerGraphUI;
import de.erdesignerng.visual.jgraph.tools.*;
import de.mogwai.common.client.looks.components.DefaultScrollPane;
import de.mogwai.common.client.looks.components.action.DefaultAction;
import de.mogwai.common.client.looks.components.menu.DefaultMenu;
import de.mogwai.common.client.looks.components.menu.DefaultMenuItem;
import de.mogwai.common.i18n.ResourceHelperProvider;
import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.jgraph.event.*;
import org.jgraph.graph.*;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class JGraphEditor extends DefaultScrollPane implements GenericModelEditor {

    private static final Logger LOGGER = Logger.getLogger(JGraphEditor.class);

    private static final class ERDesignerGraphSelectionListener implements
            GraphSelectionListener {
        @Override
        public void valueChanged(GraphSelectionEvent aEvent) {
            Object[] theCells = aEvent.getCells();
            if (!ArrayUtils.isEmpty(theCells)) {
                List<ModelItem> theItems = new ArrayList<>();
                for (Object theCell : theCells) {
                    if (theCell instanceof DefaultGraphCell
                            && aEvent.isAddedCell(theCell)) {
                        DefaultGraphCell theGraphCell = (DefaultGraphCell) theCell;
                        Object theUserObject = theGraphCell.getUserObject();
                        if (theUserObject instanceof ModelItem) {
                            theItems.add((ModelItem) theUserObject);
                        }
                    }
                }

                SQLComponent.getDefault().displaySQLFor(
                        theItems.toArray(new ModelItem[theItems.size()]));
                if (theItems.size() == 1) {
                    OutlineComponent.getDefault().setSelectedItem(
                            theItems.get(0));
                }

            } else {
                SQLComponent.getDefault().resetDisplay();
            }
        }
    }

    private class ERDesignerGraphModelListener implements GraphModelListener {

        @Override
        public void graphChanged(GraphModelEvent aEvent) {
            if (!filling) {
                GraphLayoutCacheEvent.GraphLayoutCacheChange theChange = aEvent.getChange();

                Object[] theChangedObjects = theChange.getChanged();
                Map theChangedAttributes = theChange.getPreviousAttributes();

                if (theChangedAttributes != null) {
                    for (Object theChangedObject : theChangedObjects) {
                        Map theAttributes = (Map) theChangedAttributes
                                .get(theChangedObject);

                        if (theChangedObject instanceof ModelCell) {

                            ModelCell theCell = (ModelCell) theChangedObject;
                            if (theAttributes != null) {
                                theCell
                                        .transferAttributesToProperties(theAttributes);
                            }
                        }

                        if (theChangedObject instanceof SubjectAreaCell) {

                            SubjectAreaCell theCell = (SubjectAreaCell) theChangedObject;
                            if (theCell.getChildCount() == 0) {
                                commandRemoveSubjectArea(theCell);
                            } else {
                                commandUpdateSubjectArea(theCell);
                            }
                        }
                    }
                    graph.setSelectionCells(graph.getSelectionCells());
                }
            }
        }
    }

    private final class LayoutThread extends Thread {
        @Override
        public void run() {
            while (!isInterrupted()) {
                try {
                    long theDuration = System.currentTimeMillis();

                    if (layout.preEvolveLayout()) {
                        layout.evolveLayout();

                        SwingUtilities.invokeAndWait(() -> layout.postEvolveLayout());
                    }
                    theDuration = System.currentTimeMillis() - theDuration;

                    // Assume 30 Frames / Second animation speed
                    long theDifference = (1000 - (theDuration * 30)) / 30;
                    if (theDifference > 0) {
                        sleep(theDifference);
                    } else {
                        sleep(40);
                    }
                } catch (InterruptedException e) {
                    return;
                } catch (Exception e) {
                    ERDesignerComponent.getDefault().getWorldConnector().notifyAboutException(e);
                }
            }
        }
    }

    private static final class GraphModelMappingInfo {
        final Map<Table, TableCell> modelTableCells = new HashMap<>();

        final Map<View, ViewCell> modelViewCells = new HashMap<>();

        final Map<Comment, CommentCell> modelCommentCells = new HashMap<>();
    }


    private ERDesignerGraph graph;
    private final ERDesignerGraphLayout layout;
    private LayoutThread layoutThread;
    private boolean filling;

    public JGraphEditor() {

        layout = new ERDesignerGraphLayout(this);
        setWheelScrollingEnabled(false);
        // We have to initialize the graph object.
        setModel(null);
    }

    public ERDesignerGraph getGraph() {
        return graph;
    }

    protected void commandRemoveSubjectArea(SubjectAreaCell aCell) {

        graph.getGraphLayoutCache().remove(new Object[]{aCell});

        ERDesignerComponent theComponent = ERDesignerComponent.getDefault();
        theComponent.getModel().removeSubjectArea((SubjectArea) aCell.getUserObject());
        theComponent.updateSubjectAreasMenu();
    }

    protected void commandUpdateSubjectArea(SubjectAreaCell aCell) {

        SubjectArea theArea = (SubjectArea) aCell.getUserObject();
        theArea.getTables().clear();
        theArea.getViews().clear();
        theArea.getComments().clear();
        for (Object theObject : aCell.getChildren()) {
            if (theObject instanceof TableCell) {
                theArea.getTables().add(
                        (Table) ((TableCell) theObject).getUserObject());
            }
            if (theObject instanceof ViewCell) {
                theArea.getViews().add(
                        (View) ((ViewCell) theObject).getUserObject());
            }
            if (theObject instanceof CommentCell) {
                theArea.getComments().add(
                        (Comment) ((CommentCell) theObject).getUserObject());
            }
        }

        ERDesignerComponent.getDefault().updateSubjectAreasMenu();
    }

    @Override
    public void repaintGraph() {
        for (CellView theView : graph.getGraphLayoutCache().getCellViews()) {
            graph.updateAutoSize(theView);
        }
        graph.getGraphLayoutCache().reload();
        graph.getGraphLayoutCache().update(
                graph.getGraphLayoutCache().getAllViews());

        graph.addOffscreenDirty(new Rectangle2D.Double(0, 0, getWidth(), getHeight()));
        graph.repaint();
    }

    @Override
    public void commandSetDisplayLevel(DisplayLevel aLevel) {

        graph.setDisplayLevel(aLevel);
        repaintGraph();
    }

    @Override
    public void commandSetDisplayOrder(DisplayOrder aOrder) {

        graph.setDisplayOrder(aOrder);
        repaintGraph();
    }

    @Override
    public void commandHideSubjectArea(SubjectArea aArea) {

        graph.getGraphLayoutCache().getVisibleSet().stream().filter(theItem -> theItem instanceof SubjectAreaCell).forEach(theItem -> {
            SubjectAreaCell theCell = (SubjectAreaCell) theItem;
            if (theCell.getUserObject().equals(aArea)) {
                aArea.setVisible(false);

                Object[] theCellObjects = new Object[]{theCell};
                graph.getGraphLayoutCache().hideCells(theCellObjects, true);
            }
        });
        ERDesignerComponent.getDefault().updateSubjectAreasMenu();
    }

    @Override
    public void commandShowSubjectArea(SubjectArea aArea) {

        for (CellView theCellView : graph.getGraphLayoutCache()
                .getHiddenCellViews()) {
            Object theItem = theCellView.getCell();
            if (theItem instanceof SubjectAreaCell) {
                SubjectAreaCell theCell = (SubjectAreaCell) theItem;
                if (theCell.getUserObject().equals(aArea)) {
                    aArea.setVisible(true);

                    Object[] theCellObjects = DefaultGraphModel.getDescendants(
                            graph.getModel(), new Object[]{theCell})
                            .toArray();

                    graph.getGraphLayoutCache().showCells(theCellObjects, true);
                    for (Object theSingleCell : theCellObjects) {
                        if (theSingleCell instanceof TableCell) {
                            TableCell theTableCell = (TableCell) theSingleCell;
                            Table theTable = (Table) theTableCell
                                    .getUserObject();

                            theTableCell
                                    .transferPropertiesToAttributes(theTable);
                            graph.getGraphLayoutCache().edit(
                                    new Object[]{theTableCell},
                                    theTableCell.getAttributes());
                        }
                        if (theSingleCell instanceof ViewCell) {
                            ViewCell theViewCell = (ViewCell) theSingleCell;
                            View theView = (View) theViewCell.getUserObject();

                            theViewCell.transferPropertiesToAttributes(theView);
                            graph.getGraphLayoutCache().edit(
                                    new Object[]{theViewCell},
                                    theViewCell.getAttributes());
                        }
                        if (theSingleCell instanceof CommentCell) {
                            CommentCell theCommentCell = (CommentCell) theSingleCell;
                            Comment theComment = (Comment) theCommentCell
                                    .getUserObject();

                            theCommentCell
                                    .transferPropertiesToAttributes(theComment);
                            graph.getGraphLayoutCache().edit(
                                    new Object[]{theCommentCell},
                                    theCommentCell.getAttributes());
                        }
                    }
                }
            }
        }
        ERDesignerComponent.getDefault().updateSubjectAreasMenu();
    }

    @Override
    public void commandSetTool(ToolEnum aTool) {
        switch (aTool) {
            case HAND:
                graph.setTool(new HandTool(this, graph));
                break;
            case ENTITY:
                graph.setTool(new EntityTool(this, graph));
                break;
            case RELATION:
                graph.setTool(new RelationTool(this, graph));
                break;
            case COMMENT:
                graph.setTool(new CommentTool(this, graph));
                break;
            case VIEW:
                graph.setTool(new ViewTool(this, graph));
                break;
        }
    }

    @Override
    public void commandSetZoom(ZoomInfo aZoomInfo) {

        graph.setScale(aZoomInfo.getValue());

        repaintGraph();
    }

    /**
     * Factory Method to create a new graph model and initialize it with the
     * required listener.
     *
     * @return the newly created graphmodel
     */
    private GraphModel createNewGraphModel() {
        GraphModel theModel = new DefaultGraphModel();
        theModel.addGraphModelListener(new ERDesignerGraphModelListener());
        return theModel;
    }

    /**
     * Factory Method for the graph layout cache.
     *
     * @param aModel the graph model
     * @return the newly created graph layout cache
     */
    private GraphLayoutCache createNewGraphlayoutCache(GraphModel aModel) {
        GraphLayoutCache theCache = new GraphLayoutCache(aModel,
                new CellViewFactory(), true);
        theCache.setAutoSizeOnValueChange(true);
        return theCache;
    }

    @Override
    public void setModel(final Model model) {
        GraphModel theGraphModel = createNewGraphModel();

        graph = new ERDesignerGraph(model, theGraphModel,
                createNewGraphlayoutCache(theGraphModel)) {

            @Override
            public void commandNewTable(Point2D aLocation) {
                new AddTableCommand(aLocation,
                        null, false).execute();
            }

            @Override
            public void commandNewComment(Point2D aLocation) {
                new AddCommentCommand(aLocation)
                        .execute();
            }

            @Override
            public void commandNewView(Point2D aLocation) {
                new AddViewCommand(aLocation)
                        .execute();
            }

            @Override
            public void commandHideCells(List<HideableCell> cellsToHide) {
                JGraphEditor.this.commandHideCells(cellsToHide);
            }

            @Override
            public void commandAddToNewSubjectArea(
                    List<DefaultGraphCell> aCells) {

                super.commandAddToNewSubjectArea(aCells);
                ERDesignerComponent.getDefault().updateSubjectAreasMenu();
            }

            @Override
            public void commandNewTableAndRelation(Point2D aLocation,
                                                   TableCell aExportingTableCell, boolean aNewTableIsChild) {
                new AddTableCommand(aLocation,
                        (Table) aExportingTableCell.getUserObject(), aNewTableIsChild).execute();
            }

            @Override
            public void commandNewRelation(TableCell aImportingCell,
                                           TableCell aExportingCell) {
                new AddRelationCommand(
                        (Table) aImportingCell.getUserObject(), (Table) aExportingCell.getUserObject()).execute();
            }

            @Override
            public void refreshOutline() {
                OutlineComponent.getDefault().refresh(model);
            }
        };

        graph.setUI(new ERDesignerGraphUI(this));
        graph
                .addGraphSelectionListener(new ERDesignerGraphSelectionListener());

        commandSetDisplayLevel(DisplayLevel.ALL);
        commandSetDisplayOrder(DisplayOrder.NATURAL);

        refreshPreferences();

        if (model != null) {
            fillGraph(model);
        }

        getViewport().removeAll();
        getViewport().add(graph);

        invalidate();
        repaintGraph();
    }

    private GraphModelMappingInfo fillGraph(Model aModel) {

        filling = true;
        try {

            GraphModel theGraphModel = createNewGraphModel();

            graph.setModel(theGraphModel);
            graph.setGraphLayoutCache(createNewGraphlayoutCache(theGraphModel));

            GraphModelMappingInfo theInfo = new GraphModelMappingInfo();

            List<Object> theCellsToInsert = new ArrayList<>();

            for (Table theTable : aModel.getTables()) {
                TableCell theCell = new TableCell(theTable);
                theCell.transferPropertiesToAttributes(theTable);

                theCellsToInsert.add(theCell);

                theInfo.modelTableCells.put(theTable, theCell);
            }

            for (View theView : aModel.getViews()) {

                try {
                    SQLUtils.updateViewAttributesFromSQL(theView, theView.getSql());
                } catch (Exception e) {
                    LOGGER.error("Error inspecting sql : " + theView.getSql(), e);
                }

                ViewCell theCell = new ViewCell(theView);
                theCell.transferPropertiesToAttributes(theView);

                theCellsToInsert.add(theCell);

                theInfo.modelViewCells.put(theView, theCell);
            }

            for (Comment theComment : aModel.getComments()) {
                CommentCell theCell = new CommentCell(theComment);
                theCell.transferPropertiesToAttributes(theComment);

                theCellsToInsert.add(theCell);

                theInfo.modelCommentCells.put(theComment, theCell);
            }

            for (Relation theRelation : aModel.getRelations()) {

                TableCell theImportingCell = theInfo.modelTableCells
                        .get(theRelation.getImportingTable());
                TableCell theExportingCell = theInfo.modelTableCells
                        .get(theRelation.getExportingTable());

                RelationEdge theCell = new RelationEdge(theRelation,
                        theImportingCell, theExportingCell);
                theCell.transferPropertiesToAttributes(theRelation);

                theCellsToInsert.add(theCell);
            }

            graph.getGraphLayoutCache().insert(theCellsToInsert.toArray());

            List<SubjectAreaCell> theSACells = new ArrayList<>();

            for (SubjectArea theSubjectArea : aModel.getSubjectAreas()) {

                SubjectAreaCell theSubjectAreaCell = new SubjectAreaCell(
                        theSubjectArea);
                List<ModelCell> theTableCells = theSubjectArea.getTables().stream().map(theInfo.modelTableCells::get).collect(Collectors.toList());

                theTableCells.addAll(theSubjectArea.getViews().stream().map(theInfo.modelViewCells::get).collect(Collectors.toList()));

                theTableCells.addAll(theSubjectArea.getComments().stream().map(theInfo.modelCommentCells::get).collect(Collectors.toList()));

                graph.getGraphLayoutCache().insertGroup(theSubjectAreaCell,
                        theTableCells.toArray());

                theSACells.add(theSubjectAreaCell);

                if (!theSubjectArea.isVisible()) {
                    commandHideSubjectArea(theSubjectArea);
                }

                if (!theSubjectArea.isExpanded()) {
                    graph.setSubjectAreaCellCollapsed(theSubjectAreaCell);
                }
            }

            if (theSACells.size() > 0) {
                graph.getGraphLayoutCache().toBack(
                        theSACells.toArray(new Object[theSACells.size()]));
            }

            ERDesignerComponent.getDefault().updateSubjectAreasMenu();

            return theInfo;

        } finally {
            filling = false;
        }
    }

    /**
     * Hide a list of specific cells.
     *
     * @param aCellsToHide the cells to hide
     */
    protected void commandHideCells(List<HideableCell> aCellsToHide) {
        aCellsToHide.stream().filter(theCell -> theCell instanceof SubjectAreaCell).forEach(theCell -> {
            SubjectAreaCell theSA = (SubjectAreaCell) theCell;
            SubjectArea theArea = (SubjectArea) theSA.getUserObject();

            commandHideSubjectArea(theArea);
        });

        ERDesignerComponent.getDefault().updateSubjectAreasMenu();
    }

    @Override
    public void commandSetDisplayCommentsState(boolean aState) {

        graph.setDisplayComments(aState);
        repaintGraph();
    }

    @Override
    public void commandSetDisplayGridState(boolean aState) {

        graph.setGridEnabled(aState);
        graph.setGridVisible(aState);
        repaintGraph();
    }

    @Override
    public void refreshPreferences() {
        graph.setGridSize(ApplicationPreferences.getInstance().getGridSize());
        repaintGraph();
    }

    @Override
    public void commandNotifyAboutEdit() {
        ERDesignerComponent.getDefault().updateSubjectAreasMenu();
    }

    @Override
    public final void setIntelligentLayoutEnabled(boolean aStatus) {
        if (!aStatus) {
            if (layoutThread != null) {
                layoutThread.interrupt();
                while (layoutThread.getState() != Thread.State.TERMINATED) {
                }
            }
        } else {
            layoutThread = new LayoutThread();
            layoutThread.start();
        }
        ApplicationPreferences.getInstance().setIntelligentLayout(aStatus);
    }

    @Override
    public void setSelectedObject(ModelItem aItem) {
        DefaultGraphCell theCell = findCellforObject(aItem);
        if (theCell != null) {
            graph.setSelectionCell(theCell);
            graph.scrollCellToVisible(theCell);
        }
    }

    public DefaultGraphCell findCellforObject(ModelItem aItem) {
        for (CellView theView : graph.getGraphLayoutCache().getCellViews()) {
            DefaultGraphCell theCell = (DefaultGraphCell) theView.getCell();
            if (aItem.equals(theCell.getUserObject())) {
                return theCell;
            }
        }
        return null;
    }

    private List<DefaultGraphCell> getCellsFor(List<ModelItem> aItems) {
        List<DefaultGraphCell> theCells = new ArrayList<>();
        for (ModelItem theItem : aItems) {
            DefaultGraphCell theCell = findCellforObject(theItem);
            if (theCell != null) {
                theCells.add(theCell);
            }
        }
        return theCells;
    }

    @Override
    public void commandAddToNewSubjectArea(List<ModelItem> aItems) {

        List<DefaultGraphCell> theCells = getCellsFor(aItems);

        if (theCells.size() > 0) {
            graph.commandAddToNewSubjectArea(theCells);
            ERDesignerComponent.getDefault().updateSubjectAreasMenu();
        }
    }

    @Override
    public void commandDelete(List<ModelItem> aItems) {

        List<DefaultGraphCell> theCells = getCellsFor(aItems);

        if (theCells.size() > 0) {

            if (MessagesHelper.displayQuestionMessage(graph,
                    ERDesignerBundle.DOYOUREALLYWANTTODELETE)) {
                try {
                    graph.commandDeleteCells(theCells);
                } catch (VetoException ex) {
                    MessagesHelper.displayErrorMessage(graph,
                            ERDesignerComponent.getDefault().getResourceHelper().getFormattedText(
                                    ERDesignerBundle.CANNOTDELETEMODELITEM,
                                    ex.getMessage()));
                }
            }
        }
    }

    @Override
    public void commandCreateComment(Comment aComment, Point2D aLocation) {
        CommentCell theCell = new CommentCell(aComment);
        theCell.transferPropertiesToAttributes(aComment);

        Object theTargetCell = graph.getFirstCellForLocation(aLocation.getX(), aLocation.getY());
        if (theTargetCell instanceof SubjectAreaCell) {
            SubjectAreaCell theSACell = (SubjectAreaCell) theTargetCell;
            SubjectArea theArea = (SubjectArea) theSACell.getUserObject();
            theArea.getComments().add(aComment);

            theSACell.add(theCell);
        }

        theCell.setBounds(new Rectangle2D.Double(aLocation.getX(), aLocation.getY(), -1, -1));

        graph.getGraphLayoutCache().insert(theCell);

        theCell.transferAttributesToProperties(theCell.getAttributes());

        graph.doLayout();
    }

    @Override
    public void commandCreateRelation(Relation aRelation) {

        TableCell theImportingCell = (TableCell) findCellforObject(aRelation.getImportingTable());
        TableCell theExportingCell = (TableCell) findCellforObject(aRelation.getExportingTable());

        RelationEdge theEdge = new RelationEdge(aRelation,
                theImportingCell, theExportingCell);

        graph.getGraphLayoutCache().insert(theEdge);
    }

    @Override
    public void commandCreateTable(Table aTable, Point2D aLocation) {
        TableCell theImportingCell = new TableCell(aTable);
        theImportingCell.transferPropertiesToAttributes(aTable);

        Object theTargetCell = graph.getFirstCellForLocation(aLocation.getX(), aLocation.getY());
        if (theTargetCell instanceof SubjectAreaCell) {
            SubjectAreaCell theSACell = (SubjectAreaCell) theTargetCell;
            SubjectArea theArea = (SubjectArea) theSACell.getUserObject();
            theArea.getTables().add(aTable);

            theSACell.add(theImportingCell);
        }

        theImportingCell.setBounds(new Rectangle2D.Double(aLocation.getX(), aLocation.getY(), -1, -1));

        graph.getGraphLayoutCache().insert(theImportingCell);

        theImportingCell.transferAttributesToProperties(theImportingCell.getAttributes());

        graph.doLayout();
    }

    @Override
    public void commandCreateView(View aView, Point2D aLocation) {
        ViewCell theCell = new ViewCell(aView);
        theCell.transferPropertiesToAttributes(aView);

        Object theTargetCell = graph.getFirstCellForLocation(aLocation.getX(), aLocation.getY());
        if (theTargetCell instanceof SubjectAreaCell) {
            SubjectAreaCell theSACell = (SubjectAreaCell) theTargetCell;
            SubjectArea theArea = (SubjectArea) theSACell.getUserObject();
            theArea.getViews().add(aView);

            theSACell.add(theCell);
        }

        theCell.setBounds(new Rectangle2D.Double(aLocation.getX(), aLocation.getY(), -1, -1));

        graph.getGraphLayoutCache().insert(theCell);

        theCell.transferAttributesToProperties(theCell.getAttributes());

        graph.doLayout();

    }

    @Override
    public void commandShowOrHideRelationsFor(Table aTable, boolean aShow) {
        Set<RelationEdge> theCellsToHide = new HashSet<>();
        for (Object theItem : graph.getGraphLayoutCache().getCellViews()) {
            if (theItem instanceof RelationEdgeView) {
                RelationEdgeView theView = (RelationEdgeView) theItem;
                RelationEdge theCell = (RelationEdge) theView.getCell();
                Relation theRelation = (Relation) theCell.getUserObject();
                if (theRelation.getExportingTable() == aTable || theRelation.getImportingTable() == aTable) {
                    theCellsToHide.add(theCell);
                }
            }
        }
        if (aShow) {
            graph.getGraphLayoutCache().showCells(theCellsToHide.toArray(new RelationEdge[theCellsToHide.size()]), true);
        } else {
            graph.getGraphLayoutCache().hideCells(theCellsToHide.toArray(new RelationEdge[theCellsToHide.size()]), true);
        }

    }

    @Override
    public JComponent getDetailComponent() {
        return this;
    }

    @Override
    public void addExportEntries(DefaultMenu aMenu, Exporter aExporter) {
        DefaultAction theAllInOneAction = new DefaultAction(
                ERDesignerBundle.BUNDLE_NAME, ERDesignerBundle.ALLINONEFILE);
        DefaultMenuItem theAllInOneItem = new DefaultMenuItem(theAllInOneAction);
        theAllInOneAction.addActionListener(new ExportGraphicsCommand(this,
                aExporter, ExportType.ALL_IN_ONE));
        aMenu.add(theAllInOneItem);

        DefaultAction theOnePerTableAction = new DefaultAction(
                ERDesignerBundle.BUNDLE_NAME, ERDesignerBundle.ONEFILEPERTABLE);
        DefaultMenuItem theOnePerTable = new DefaultMenuItem(
                theOnePerTableAction);
        theOnePerTableAction.addActionListener(new ExportGraphicsCommand(this,
                aExporter, ExportType.ONE_PER_FILE));

        aMenu.add(theOnePerTable);
    }

    @Override
    public boolean supportsZoom() {
        return true;
    }

    @Override
    public boolean supportsHandAction() {
        return true;
    }

    @Override
    public boolean supportsRelationAction() {
        return true;
    }

    @Override
    public boolean supportsCommentAction() {
        return true;
    }

    @Override
    public boolean supportsViewAction() {
        return true;
    }

    @Override
    public boolean supportsIntelligentLayout() {
        return true;
    }

    @Override
    public void initExportEntries(ResourceHelperProvider aProvider, DefaultMenu aExportMenu) {
        aExportMenu.setEnabled(true);
        List<String> theSupportedFormats = ImageExporter.getSupportedFormats();
        if (theSupportedFormats.contains("IMAGE/PNG")) {
            DefaultMenu theSingleExportMenu = new DefaultMenu(aProvider,
                    ERDesignerBundle.ASPNG);
            aExportMenu.add(theSingleExportMenu);

            addExportEntries(theSingleExportMenu, new ImageExporter("png"));
        }
        if (theSupportedFormats.contains("IMAGE/JPEG")) {
            DefaultMenu theSingleExportMenu = new DefaultMenu(aProvider,
                    ERDesignerBundle.ASJPEG);
            aExportMenu.add(theSingleExportMenu);

            addExportEntries(theSingleExportMenu, new ImageExporter("jpg"));
        }
        if (theSupportedFormats.contains("IMAGE/BMP")) {
            DefaultMenu theSingleExportMenu = new DefaultMenu(aProvider,
                    ERDesignerBundle.ASBMP);
            aExportMenu.add(theSingleExportMenu);

            addExportEntries(theSingleExportMenu, new ImageExporter("bmp"));
        }

        DefaultAction theExportSVGAction = new DefaultAction(aProvider,
                ERDesignerBundle.ASSVG);

        DefaultMenu theSVGExportMenu = new DefaultMenu(theExportSVGAction);

        aExportMenu.add(theSVGExportMenu);
        addExportEntries(theSVGExportMenu, new SVGExporter());

    }

    @Override
    public boolean supportsEntityAction() {
        return true;
    }

    @Override
    public boolean supportsGrid() {
        return true;
    }

    @Override
    public boolean supportsDisplayLevel() {
        return true;
    }

    @Override
    public boolean supportsSubjectAreas() {
        return true;
    }

    @Override
    public boolean supportsAttributeOrder() {
        return true;
    }

    @Override
    public boolean supportsDeletionOfObjects() {
        return true;
    }

    @Override
    public boolean supportShowingAndHidingOfRelations() {
        return true;
    }

    @Override
    public void initLayoutMenu(ERDesignerComponent aComponent, DefaultMenu aLayoutMenu) {
        aLayoutMenu.setEnabled(true);

        DefaultAction layoutCluster = new DefaultAction(
                e -> performClusterLayout(), aComponent, ERDesignerBundle.LAYOUTCLUSTER);

        DefaultAction layoutTreeAction = new DefaultAction(
                e -> performTreeLayout(), aComponent, ERDesignerBundle.LAYOUTTREE);

        DefaultAction layoutRadialAction = new DefaultAction(
                e -> performRadialLayout(), aComponent, ERDesignerBundle.LAYOUTRADIAL);

        aLayoutMenu.add(layoutCluster);
        aLayoutMenu.add(layoutTreeAction);
        aLayoutMenu.add(layoutRadialAction);
        aLayoutMenu.addSeparator();

        DefaultAction layoutGrid = new DefaultAction(
                e -> performJGraphSimpleGridLayout(), aComponent, ERDesignerBundle.LAYOUTGRID);

        DefaultAction layoutSelfOrganizing = new DefaultAction(
                e -> performJGraphSelfOrganizingLayout(), aComponent, ERDesignerBundle.LAYOUTSELFORGANIZING);

        DefaultAction layoutOrganic = new DefaultAction(
                e -> performJGraphOrganicLayout(), aComponent, ERDesignerBundle.LAYOUTORGANIC);

        DefaultAction layoutFastOrganic = new DefaultAction(
                e -> performJGraphFastOrganicLayout(), aComponent, ERDesignerBundle.LAYOUTFASTORGANIC);

        DefaultAction layoutRadialTree = new DefaultAction(
                e -> performJGraphRadialTreeLayout(), aComponent, ERDesignerBundle.LAYOUTRADIALTREE);

        DefaultAction layoutTree2 = new DefaultAction(
                e -> performJGraphTreeLayout2(), aComponent, ERDesignerBundle.LAYOUTTREE2);

        DefaultAction layoutHierarchical = new DefaultAction(
                e -> performJGraphHierarchicalLayout(), aComponent, ERDesignerBundle.LAYOUTHIERARCHICAL);


        aLayoutMenu.add(layoutGrid);
        aLayoutMenu.add(layoutSelfOrganizing);
        aLayoutMenu.add(layoutOrganic);
        aLayoutMenu.add(layoutFastOrganic);
        aLayoutMenu.add(layoutRadialTree);
        aLayoutMenu.add(layoutTree2);
        aLayoutMenu.add(layoutHierarchical);
    }

    private List<Set<Table>> buildHierarchy(Model aModel) {
        // Try to build a hierarchy
        List<Set<Table>> theLayers = new ArrayList<>();
        Set<Table> theCurrentLayer = new HashSet<>();
        Set<Table> theAlreadyKnown = new HashSet<>();
        for (Table theTable : aModel.getTables()) {
            boolean isTopLevel = true;
            List<Relation> theRelations = aModel.getRelations().getExportedKeysFor(theTable);
            if (theRelations.size() == 0) {
                isTopLevel = true;
            } else {
                for (Relation theRelation : theRelations) {
                    if (theRelation.getImportingTable() != theTable) {
                        isTopLevel = false;
                    }
                }
            }
            if (isTopLevel) {
                theCurrentLayer.add(theTable);
                theAlreadyKnown.add(theTable);
            }
        }

        // Top Level components
        theLayers.add(theCurrentLayer);

        Set<Table> theTablesToSearch = new HashSet<>();
        theTablesToSearch.addAll(theCurrentLayer);
        while (theTablesToSearch.size() > 0) {
            theCurrentLayer = new HashSet<>();
            for (Table theTable : theTablesToSearch) {
                for (Relation theRelation : aModel.getRelations().getForeignKeysFor(theTable)) {
                    if (theRelation.getExportingTable() != theTable && !theAlreadyKnown.contains(theRelation.getExportingTable())) {
                        theCurrentLayer.add(theRelation.getExportingTable());
                        theAlreadyKnown.add(theRelation.getExportingTable());
                    }
                }
            }
            if (theCurrentLayer.size() > 0) {

                Set<Table> theTablesToRemove = new HashSet<>();

                for (Table theTable : theCurrentLayer) {
                    boolean isUsedInSameLayer = false;
                    for (Relation theRelation : aModel.getRelations().getExportedKeysFor(theTable)) {
                        if (theRelation.getImportingTable() != theTable && theCurrentLayer.contains(theRelation.getImportingTable())) {
                            isUsedInSameLayer = true;
                        }
                    }
                    if (isUsedInSameLayer) {
                        theTablesToRemove.add(theTable);
                    }
                }

                theCurrentLayer.removeAll(theTablesToRemove);
                theAlreadyKnown.removeAll(theTablesToRemove);

                theLayers.add(theCurrentLayer);
                theTablesToSearch = theCurrentLayer;
            } else {
                theTablesToSearch.clear();
            }
        }
        return theLayers;
    }

    private void updatePositions() {
        Map<Object, Map> theModificatios = new HashMap<>();
        for (CellView theView : graph.getGraphLayoutCache().getAllViews()) {

            if (theView.getCell() instanceof ModelCellWithPosition) {

                ModelCellWithPosition theCell = (ModelCellWithPosition) theView.getCell();
                theCell.transferPropertiesToAttributes(theCell.getUserObject());

                theModificatios.put(theCell, theCell.getAttributes());
            }
        }

        if (theModificatios.size() > 0) {
            graph.getGraphLayoutCache().edit(theModificatios);
        }
    }

    private void performJGraphLayout(JGraphLayout aLayout) {
        JGraphFacade facade = new JGraphFacade(graph);
        facade.run(aLayout, true);

        Map nested = facade.createNestedMap(true, true); // Obtain a mapof the resulting attribute changes from the facade
        graph.getGraphLayoutCache().edit(nested); // Apply the results tothe actual graph
    }

    private void performJGraphSelfOrganizingLayout() {
        JGraphSelfOrganizingOrganicLayout theLayout = new JGraphSelfOrganizingOrganicLayout();
        theLayout.setMinRadius(150);
        theLayout.setStartRadius(300);
        performJGraphLayout(theLayout);
    }

    private void performJGraphSimpleGridLayout() {
        SimpleGridLayout theLayout = new SimpleGridLayout();
        theLayout.setNumCellsPerRow(10);
        theLayout.setHeightSpacing(20);
        theLayout.setWidthSpacing(20);
        theLayout.setActOnUnconnectedVerticesOnly(false);
        theLayout.setOffsetX(10);
        theLayout.setOffsetY(10);
        theLayout.setOrdered(true);
        performJGraphLayout(theLayout);
    }

    private void performJGraphOrganicLayout() {
        JGraphOrganicLayout theLayout = new JGraphOrganicLayout();
        theLayout.setOptimizeEdgeCrossing(true);
        theLayout.setOptimizeNodeDistribution(true);
        theLayout.setNodeDistributionCostFactor(500000);
        performJGraphLayout(theLayout);
    }

    private void performJGraphRadialTreeLayout() {
        JGraphRadialTreeLayout theLayout = new JGraphRadialTreeLayout();
        theLayout.setAutoRadius(true);
        performJGraphLayout(theLayout);
    }

    private void performJGraphHierarchicalLayout() {
        JGraphHierarchicalLayout theLayout = new JGraphHierarchicalLayout();
        performJGraphLayout(theLayout);
    }

    private void performJGraphTreeLayout2() {
        JGraphTreeLayout theLayout = new JGraphTreeLayout();
        theLayout.setAlignment(SwingConstants.TOP);
        theLayout.setPositionMultipleTrees(true);
        performJGraphLayout(theLayout);
    }

    private void performJGraphFastOrganicLayout() {
        JGraphFastOrganicLayout theLayout = new JGraphFastOrganicLayout();
        performJGraphLayout(theLayout);
    }

    private void performClusterLayout() {
        Model theModel = graph.getDBModel();

        LayoutHelper theHelper = new LayoutHelper();
        theHelper.performClusterLayout(theModel);

        ERDesignerComponent.getDefault().setModel(theModel);
    }

    private void performTreeLayout() {

        Model theModel = graph.getDBModel();

        List<Set<Table>> theLayers = buildHierarchy(theModel);

        LayoutHelper theHelper = new LayoutHelper();
        theHelper.performTreeLayout(new Point(20, 20), theLayers, theModel.getViews());

        updatePositions();

        repaintGraph();
    }

    private void performRadialLayout() {

        Model theModel = graph.getDBModel();

        List<Set<Table>> theLayers = buildHierarchy(theModel);

        int centerx = 500 * (theLayers.size() + 1);
        int centery = 500 * (theLayers.size() + 1);
        int theRadius = 0;
        for (int theLayer = theLayers.size() - 1; theLayer >= 0; theLayer--) {
            Set<Table> theLayerTables = theLayers.get(theLayer);
            if (theLayerTables.size() > 0) {

                TableCellView.MyRenderer theRenderer = new TableCellView.MyRenderer();
                double thePerimeter = 0;
                double theMinRadius = 0;
                for (Table theTable : theLayerTables) {
                    JComponent theRendererComponent = theRenderer.getRendererComponent(theTable);
                    Dimension theSize = theRendererComponent.getPreferredSize();
                    double theR = Math.sqrt(theSize.width * theSize.width + theSize.height * theSize.height);
                    thePerimeter += theR;

                    theMinRadius = Math.max(theMinRadius, theR);
                }
                thePerimeter += theLayerTables.size() * 40;

                double theRadiusIncrement = (thePerimeter / (Math.PI * 2)) - theRadius;
                theRadius += Math.max(theRadiusIncrement, theMinRadius);

                double theIncrement = Math.toDegrees(360 / theLayerTables.size());
                double theAngle = 0;

                for (Table theTable : theLayerTables) {
                    int theXP = centerx + (int) (Math.cos(theAngle) * theRadius);
                    int theYP = centery + (int) (Math.sin(theAngle) * theRadius);
                    theTable.getProperties().setPointProperty(Table.PROPERTY_LOCATION, theXP, theYP);
                    theAngle += theIncrement;
                }
                theRadius += 500;
            }
        }

        if (theModel.getViews().size() > 0) {
            double theIncrement = Math.toDegrees(360 / theModel.getViews().size());
            double theAngle = 0;
            for (View theView : theModel.getViews()) {
                int theXP = centerx + (int) (Math.cos(theAngle) * theRadius);
                int theYP = centery + (int) (Math.sin(theAngle) * theRadius);
                theView.getProperties().setPointProperty(View.PROPERTY_LOCATION, theXP, theYP);
                theAngle += theIncrement;
            }
        }

        updatePositions();

        repaintGraph();
    }

    public void commandZoomOneLevelIn() {
    }

    public void commandZoomOneLevelOut() {
    }
}
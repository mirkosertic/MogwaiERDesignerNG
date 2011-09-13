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

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.model.*;
import de.erdesignerng.modificationtracker.VetoException;
import de.erdesignerng.util.ApplicationPreferences;
import de.erdesignerng.util.SQLUtils;
import de.erdesignerng.visual.DisplayLevel;
import de.erdesignerng.visual.DisplayOrder;
import de.erdesignerng.visual.ExportType;
import de.erdesignerng.visual.MessagesHelper;
import de.erdesignerng.visual.common.*;
import de.erdesignerng.visual.jgraph.cells.*;
import de.erdesignerng.visual.jgraph.cells.views.CellViewFactory;
import de.erdesignerng.visual.jgraph.cells.views.RelationEdgeView;
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
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.*;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import org.apache.commons.lang.ArrayUtils;
import org.jgraph.event.*;
import org.jgraph.graph.*;

public class JGraphEditor extends DefaultScrollPane implements GenericModelEditor {

    private static final class ERDesignerGraphSelectionListener implements
            GraphSelectionListener {
        @Override
        public void valueChanged(GraphSelectionEvent aEvent) {
            Object[] theCells = aEvent.getCells();
            if (!ArrayUtils.isEmpty(theCells)) {
                List<ModelItem> theItems = new ArrayList<ModelItem>();
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

    private final class LayoutThread extends Thread {
        @Override
        public void run() {
            while (!isInterrupted()) {
                try {
                    long theDuration = System.currentTimeMillis();

                    if (layout.preEvolveLayout()) {
                        layout.evolveLayout();

                        SwingUtilities.invokeAndWait(new Runnable() {
                            @Override
                            public void run() {
                                layout.postEvolveLayout();
                            }
                        });
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
        final Map<Table, TableCell> modelTableCells = new HashMap<Table, TableCell>();

        final Map<View, ViewCell> modelViewCells = new HashMap<View, ViewCell>();

        final Map<Comment, CommentCell> modelCommentCells = new HashMap<Comment, CommentCell>();
    }


    private ERDesignerGraph graph;
    private ERDesignerGraphLayout layout;
    private LayoutThread layoutThread;

    public JGraphEditor() {
        layout = new ERDesignerGraphLayout(this);
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
        for (Object theItem : graph.getGraphLayoutCache().getVisibleSet()) {
            if (theItem instanceof SubjectAreaCell) {
                SubjectAreaCell theCell = (SubjectAreaCell) theItem;
                if (theCell.getUserObject().equals(aArea)) {
                    aArea.setVisible(false);

                    Object[] theCellObjects = new Object[]{theCell};
                    graph.getGraphLayoutCache().hideCells(theCellObjects, true);
                }
            }
        }
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
        if (aTool.equals(ToolEnum.HAND)) {
            graph.setTool(new HandTool(this, graph));
        }
        if (aTool.equals(ToolEnum.ENTITY)) {
            graph.setTool(new EntityTool(graph));
        }
        if (aTool.equals(ToolEnum.RELATION)) {
            graph.setTool(new RelationTool(graph));
        }
        if (aTool.equals(ToolEnum.COMMENT)) {
            graph.setTool(new CommentTool(graph));
        }
        if (aTool.equals(ToolEnum.VIEW)) {
            graph.setTool(new ViewTool(graph));
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

        getViewport().removeAll();
        getViewport().add(graph);

        refreshPreferences();

        fillGraph(model);
    }

    private GraphModelMappingInfo fillGraph(Model aModel) {

        GraphModel theGraphModel = createNewGraphModel();

        graph.setModel(theGraphModel);
        graph.setGraphLayoutCache(createNewGraphlayoutCache(theGraphModel));

        GraphModelMappingInfo theInfo = new GraphModelMappingInfo();

        List<Object> theCellsToInsert = new ArrayList<Object>();

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
                e.printStackTrace();
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

        for (SubjectArea theSubjectArea : aModel.getSubjectAreas()) {

            SubjectAreaCell theSubjectAreaCell = new SubjectAreaCell(
                    theSubjectArea);
            List<ModelCell> theTableCells = new ArrayList<ModelCell>();

            for (Table theTable : theSubjectArea.getTables()) {
                theTableCells.add(theInfo.modelTableCells.get(theTable));
            }

            for (View theView : theSubjectArea.getViews()) {
                theTableCells.add(theInfo.modelViewCells.get(theView));
            }

            for (Comment theComment : theSubjectArea.getComments()) {
                theTableCells.add(theInfo.modelCommentCells.get(theComment));
            }

            graph.getGraphLayoutCache().insertGroup(theSubjectAreaCell,
                    theTableCells.toArray());
            graph.getGraphLayoutCache().toBack(
                    new Object[]{theSubjectAreaCell});

            if (!theSubjectArea.isVisible()) {
                commandHideSubjectArea(theSubjectArea);
            }

        }

        return theInfo;
    }

    /**
     * Hide a list of specific cells.
     *
     * @param aCellsToHide the cells to hide
     */
    protected void commandHideCells(List<HideableCell> aCellsToHide) {
        for (HideableCell theCell : aCellsToHide) {
            if (theCell instanceof SubjectAreaCell) {
                SubjectAreaCell theSA = (SubjectAreaCell) theCell;
                SubjectArea theArea = (SubjectArea) theSA.getUserObject();

                commandHideSubjectArea(theArea);
            }
        }

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
        List<DefaultGraphCell> theCells = new ArrayList<DefaultGraphCell>();
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
        TableCell theExportingCell = (TableCell) findCellforObject(aRelation.getImportingTable());

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
        Set<RelationEdge> theCellsToHide = new HashSet<RelationEdge>();
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
}
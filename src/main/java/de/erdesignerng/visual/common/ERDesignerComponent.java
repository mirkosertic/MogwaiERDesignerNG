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
package de.erdesignerng.visual.common;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.jgraph.event.GraphModelEvent;
import org.jgraph.event.GraphModelListener;
import org.jgraph.event.GraphLayoutCacheEvent.GraphLayoutCacheChange;
import org.jgraph.graph.CellView;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.model.Comment;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.ModelItem;
import de.erdesignerng.model.Relation;
import de.erdesignerng.model.SubjectArea;
import de.erdesignerng.model.Table;
import de.erdesignerng.model.View;
import de.erdesignerng.model.serializer.repository.RepositoryEntryDesciptor;
import de.erdesignerng.util.ApplicationPreferences;
import de.erdesignerng.util.ConnectionDescriptor;
import de.erdesignerng.util.JasperUtils;
import de.erdesignerng.util.SQLUtils;
import de.erdesignerng.visual.DisplayLevel;
import de.erdesignerng.visual.DisplayOrder;
import de.erdesignerng.visual.ERDesignerGraph;
import de.erdesignerng.visual.ExportType;
import de.erdesignerng.visual.MessagesHelper;
import de.erdesignerng.visual.cells.CommentCell;
import de.erdesignerng.visual.cells.HideableCell;
import de.erdesignerng.visual.cells.ModelCell;
import de.erdesignerng.visual.cells.RelationEdge;
import de.erdesignerng.visual.cells.SubjectAreaCell;
import de.erdesignerng.visual.cells.TableCell;
import de.erdesignerng.visual.cells.ViewCell;
import de.erdesignerng.visual.cells.views.CellViewFactory;
import de.erdesignerng.visual.export.Exporter;
import de.erdesignerng.visual.export.ImageExporter;
import de.erdesignerng.visual.export.SVGExporter;
import de.erdesignerng.visual.help.PDFViewer;
import de.erdesignerng.visual.plaf.basic.ERDesignerGraphUI;
import de.erdesignerng.visual.tools.CommentTool;
import de.erdesignerng.visual.tools.EntityTool;
import de.erdesignerng.visual.tools.HandTool;
import de.erdesignerng.visual.tools.RelationTool;
import de.erdesignerng.visual.tools.ToolEnum;
import de.erdesignerng.visual.tools.ViewTool;
import de.mogwai.common.client.looks.UIInitializer;
import de.mogwai.common.client.looks.components.DefaultCheckBox;
import de.mogwai.common.client.looks.components.DefaultCheckboxMenuItem;
import de.mogwai.common.client.looks.components.DefaultComboBox;
import de.mogwai.common.client.looks.components.DefaultScrollPane;
import de.mogwai.common.client.looks.components.DefaultToggleButton;
import de.mogwai.common.client.looks.components.DefaultToolbar;
import de.mogwai.common.client.looks.components.action.ActionEventProcessor;
import de.mogwai.common.client.looks.components.action.DefaultAction;
import de.mogwai.common.client.looks.components.menu.DefaultMenu;
import de.mogwai.common.client.looks.components.menu.DefaultMenuItem;
import de.mogwai.common.client.looks.components.menu.DefaultRadioButtonMenuItem;
import de.mogwai.common.i18n.ResourceHelper;
import de.mogwai.common.i18n.ResourceHelperProvider;

/**
 * The ERDesigner Editing Component.
 * 
 * This is the heart of the system.
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-13 15:40:33 $
 */
public class ERDesignerComponent implements ResourceHelperProvider {

    private final class LayoutThread extends Thread {
        @Override
        public void run() {
            while (!isInterrupted()) {
                try {
                    long theDuration = System.currentTimeMillis();

                    if (layout.preEvolveLayout()) {
                        layout.evolveLayout();

                        SwingUtilities.invokeAndWait(new Runnable() {
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
                    worldConnector.notifyAboutException(e);
                }
            }
        }
    }

    private class ERDesignerGraphModelListener implements GraphModelListener {

        public void graphChanged(GraphModelEvent aEvent) {
            GraphLayoutCacheChange theChange = aEvent.getChange();

            Object[] theChangedObjects = theChange.getChanged();
            Map theChangedAttributes = theChange.getPreviousAttributes();

            if (theChangedAttributes != null) {
                for (Object theChangedObject : theChangedObjects) {
                    Map theAttributes = (Map) theChangedAttributes.get(theChangedObject);

                    if (theChangedObject instanceof ModelCell) {

                        ModelCell theCell = (ModelCell) theChangedObject;
                        if (theAttributes != null) {
                            theCell.transferAttributesToProperties(theAttributes);
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
            }
        }
    }

    private final ApplicationPreferences preferences;

    File currentEditingFile;

    RepositoryEntryDesciptor currentRepositoryEntry;

    private volatile Model model;

    volatile ERDesignerGraph graph;

    private JToggleButton handButton;

    private JToggleButton commentButton;

    private JToggleButton relationButton;

    private JToggleButton viewButton;

    private JToggleButton entityButton;

    private DefaultMenu lruMenu;

    private DefaultMenu storedConnections;

    private DefaultMenu subjectAreas;

    private DefaultMenu documentationMenu;

    private DefaultCheckboxMenuItem displayCommentsMenuItem;

    private DefaultCheckboxMenuItem displayGridMenuItem;

    private DefaultRadioButtonMenuItem displayAllMenuItem;

    private DefaultRadioButtonMenuItem displayNaturalOrderMenuItem;

    private DefaultMenu repositoryUtilsMenu;

    private final DefaultScrollPane scrollPane = new DefaultScrollPane();

    private final ERDesignerWorldConnector worldConnector;

    private final DefaultComboBox zoomBox = new DefaultComboBox();

    private static final ZoomInfo ZOOMSCALE_HUNDREDPERCENT = new ZoomInfo("100%", 1);

    private final ERDesignerGraphLayout layout;

    private Thread layoutThread;

    public ERDesignerComponent(ApplicationPreferences aPreferences, final ERDesignerWorldConnector aConnector) {
        worldConnector = aConnector;
        preferences = aPreferences;
        layout = new ERDesignerGraphLayout(this);

        initActions();

        if (preferences.isIntelligentLayout()) {
            setIntelligentLayoutEnabled(true);
        }
    }

    protected void initActions() {

        DefaultAction theReverseEngineerAction = new DefaultAction(new ReverseEngineerCommand(this), this,
                ERDesignerBundle.REVERSEENGINEER);

        DefaultAction thePreferencesAction = new DefaultAction(new PreferencesCommand(this), this,
                ERDesignerBundle.PREFERENCES);

        DefaultAction theSaveAction = new DefaultAction(new SaveToFileCommand(this), this, ERDesignerBundle.SAVEMODEL);
        theSaveAction
                .putValue(DefaultAction.HOTKEY_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));

        DefaultAction theSaveAsAction = new DefaultAction(new ActionEventProcessor() {

            public void processActionEvent(ActionEvent aEvent) {
                new SaveToFileCommand(ERDesignerComponent.this).executeSaveFileAs();
            }

        }, this, ERDesignerBundle.SAVEMODELAS);

        DefaultAction theSaveToRepository = new DefaultAction(new SaveToRepositoryCommand(this), this,
                ERDesignerBundle.SAVEMODELTODB);

        DefaultAction theRelationAction = new DefaultAction(new ActionEventProcessor() {

            public void processActionEvent(ActionEvent e) {
                commandSetTool(ToolEnum.RELATION);
            }

        }, this, ERDesignerBundle.RELATION);

        DefaultAction theNewAction = new DefaultAction(new ActionEventProcessor() {

            public void processActionEvent(ActionEvent e) {
                commandNew();
            }
        }, this, ERDesignerBundle.NEWMODEL);

        DefaultAction theLruAction = new DefaultAction(this, ERDesignerBundle.RECENTLYUSEDFILES);

        DefaultAction theLoadAction = new DefaultAction(new OpenFromFileCommand(this), this, ERDesignerBundle.LOADMODEL);

        DefaultAction theHandAction = new DefaultAction(new ActionEventProcessor() {

            public void processActionEvent(ActionEvent e) {
                commandSetTool(ToolEnum.HAND);
            }

        }, this, ERDesignerBundle.HAND);

        DefaultAction theCommentAction = new DefaultAction(new ActionEventProcessor() {

            public void processActionEvent(ActionEvent e) {
                commandSetTool(ToolEnum.COMMENT);
            }

        }, this, ERDesignerBundle.COMMENT);

        DefaultAction theExportSVGAction = new DefaultAction(this, ERDesignerBundle.ASSVG);

        DefaultAction theEntityAction = new DefaultAction(new ActionEventProcessor() {

            public void processActionEvent(ActionEvent e) {
                commandSetTool(ToolEnum.ENTITY);
            }

        }, this, ERDesignerBundle.ENTITY);

        DefaultAction theViewAction = new DefaultAction(new ActionEventProcessor() {

            public void processActionEvent(ActionEvent e) {
                commandSetTool(ToolEnum.VIEW);
            }

        }, this, ERDesignerBundle.VIEWTOOL);

        DefaultAction theExportAction = new DefaultAction(this, ERDesignerBundle.EXPORT);

        DefaultAction theExitAction = new DefaultAction(new ActionEventProcessor() {

            public void processActionEvent(ActionEvent e) {
                worldConnector.exitApplication();
            }

        }, this, ERDesignerBundle.EXITPROGRAM);

        DefaultAction theClasspathAction = new DefaultAction(new ClasspathCommand(this), this,
                ERDesignerBundle.CLASSPATH);

        DefaultAction theDBConnectionAction = new DefaultAction(new DBConnectionCommand(this), this,
                ERDesignerBundle.DBCONNECTION);

        DefaultAction theRepositoryConnectionAction = new DefaultAction(new RepositoryConnectionCommand(this), this,
                ERDesignerBundle.REPOSITORYCONNECTION);

        DefaultAction theDomainsAction = new DefaultAction(new EditDomainCommand(this), this,
                ERDesignerBundle.DOMAINEDITOR);

        DefaultAction theZoomAction = new DefaultAction(new ActionEventProcessor() {

            public void processActionEvent(ActionEvent aEvent) {
                commandSetZoom((ZoomInfo) ((JComboBox) aEvent.getSource()).getSelectedItem());
            }
        }, this, ERDesignerBundle.ZOOM);

        DefaultAction theZoomInAction = new DefaultAction(new ActionEventProcessor() {

            public void processActionEvent(ActionEvent e) {
                commandZoomIn();
            }

        }, this, ERDesignerBundle.ZOOMIN);

        DefaultAction theZoomOutAction = new DefaultAction(new ActionEventProcessor() {

            public void processActionEvent(ActionEvent e) {
                commandZoomOut();
            }

        }, this, ERDesignerBundle.ZOOMOUT);

        DefaultAction theGenerateSQL = new DefaultAction(new GenerateSQLCommand(this), this,
                ERDesignerBundle.GENERATECREATEDBDDL);

        DefaultAction theGenerateChangelog = new DefaultAction(new GenerateChangeLogSQLCommand(this), this,
                ERDesignerBundle.GENERATECHANGELOG);

        DefaultAction theCompleteCompareAction = new DefaultAction(new CompleteCompareCommand(this), this,
                ERDesignerBundle.COMPLETECOMPARE);

        DefaultAction theConvertModelAction = new DefaultAction(new ConvertModelCommand(this), this,
                ERDesignerBundle.CONVERTMODEL);

        DefaultAction theCreateMigrationScriptAction = new DefaultAction(new GenerateMigrationScriptCommand(this),
                this, ERDesignerBundle.CREATEMIGRATIONSCRIPT);

        DefaultAction theHelpAction = new DefaultAction(new ActionEventProcessor() {

            public void processActionEvent(ActionEvent aEvent) {
                commandShowHelp();
            }

        }, this, ERDesignerBundle.HELP);

        DefaultAction theExportOpenXavaAction = new DefaultAction(new OpenXavaExportExportCommand(this), this,
                ERDesignerBundle.OPENXAVAEXPORT);

        lruMenu = new DefaultMenu(theLruAction);

        DefaultAction theStoredConnectionsAction = new DefaultAction(this, ERDesignerBundle.STOREDDBCONNECTION);
        storedConnections = new DefaultMenu(theStoredConnectionsAction);

        ERDesignerToolbarEntry theFileMenu = new ERDesignerToolbarEntry(ERDesignerBundle.FILE);
        if (worldConnector.supportsPreferences()) {
            theFileMenu.add(new DefaultMenuItem(thePreferencesAction));
            theFileMenu.addSeparator();
        }

        theFileMenu.add(new DefaultMenuItem(theNewAction));
        theFileMenu.addSeparator();
        DefaultMenuItem theSaveItem = new DefaultMenuItem(theSaveAction);
        theFileMenu.add(theSaveItem);
        KeyStroke theStroke = (KeyStroke) theSaveAction.getValue(DefaultAction.HOTKEY_KEY);
        if (theStroke != null) {
            theSaveItem.setAccelerator(theStroke);
            scrollPane.registerKeyboardAction(theSaveAction, theStroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
        }

        theFileMenu.add(new DefaultMenuItem(theSaveAsAction));
        theFileMenu.add(new DefaultMenuItem(theLoadAction));

        if (worldConnector.supportsRepositories()) {
            theFileMenu.addSeparator();
            theFileMenu.add(new DefaultMenuItem(theRepositoryConnectionAction));
            theFileMenu.add(new DefaultMenuItem(theSaveToRepository));

            DefaultMenuItem theLoadFromDBMenu = new DefaultMenuItem(new DefaultAction(new OpenFromRepositoryCommand(
                    this), this, ERDesignerBundle.LOADMODELFROMDB));

            theFileMenu.add(theLoadFromDBMenu);

            repositoryUtilsMenu = new DefaultMenu(this, ERDesignerBundle.REPOSITORYUTILS);
            repositoryUtilsMenu.add(new DefaultMenuItem(theCreateMigrationScriptAction));

            UIInitializer.getInstance().initialize(repositoryUtilsMenu);

            theFileMenu.add(repositoryUtilsMenu);

            theFileMenu.addSeparator();
        }

        DefaultMenu theExportMenu = new DefaultMenu(theExportAction);

        List<String> theSupportedFormats = ImageExporter.getSupportedFormats();
        if (theSupportedFormats.contains("IMAGE/PNG")) {
            DefaultMenu theSingleExportMenu = new DefaultMenu(this, ERDesignerBundle.ASPNG);
            theExportMenu.add(theSingleExportMenu);

            addExportEntries(theSingleExportMenu, new ImageExporter("png"));
        }
        if (theSupportedFormats.contains("IMAGE/JPEG")) {
            DefaultMenu theSingleExportMenu = new DefaultMenu(this, ERDesignerBundle.ASJPEG);
            theExportMenu.add(theSingleExportMenu);

            addExportEntries(theSingleExportMenu, new ImageExporter("jpg"));
        }
        if (theSupportedFormats.contains("IMAGE/BMP")) {
            DefaultMenu theSingleExportMenu = new DefaultMenu(this, ERDesignerBundle.ASBMP);
            theExportMenu.add(theSingleExportMenu);

            addExportEntries(theSingleExportMenu, new ImageExporter("bmp"));
        }

        DefaultMenu theSVGExportMenu = new DefaultMenu(theExportSVGAction);

        theExportMenu.add(theSVGExportMenu);
        addExportEntries(theSVGExportMenu, new SVGExporter());

        theExportMenu.add(new DefaultMenuItem(theExportOpenXavaAction));

        UIInitializer.getInstance().initialize(theExportMenu);

        theFileMenu.add(theExportMenu);

        theFileMenu.addSeparator();
        theFileMenu.add(lruMenu);

        if (worldConnector.supportsExitApplication()) {
            theFileMenu.addSeparator();
            theFileMenu.add(new DefaultMenuItem(theExitAction));
        }

        ERDesignerToolbarEntry theDBMenu = new ERDesignerToolbarEntry(ERDesignerBundle.DATABASE);

        boolean addSeparator = false;
        if (worldConnector.supportsClasspathEditor()) {
            theDBMenu.add(new DefaultMenuItem(theClasspathAction));
            addSeparator = true;
        }

        if (worldConnector.supportsConnectionEditor()) {
            theDBMenu.add(new DefaultMenuItem(theDBConnectionAction));
            theDBMenu.add(storedConnections);
            addSeparator = true;
        }

        if (addSeparator) {
            theDBMenu.addSeparator();
        }

        theDBMenu.add(new DefaultMenuItem(theDomainsAction));
        theDBMenu.addSeparator();

        theDBMenu.add(new DefaultMenuItem(theReverseEngineerAction));
        theDBMenu.addSeparator();
        theDBMenu.add(new DefaultMenuItem(theGenerateSQL));
        theDBMenu.addSeparator();
        theDBMenu.add(new DefaultMenuItem(theGenerateChangelog));
        theDBMenu.addSeparator();
        theDBMenu.add(new DefaultMenuItem(theCompleteCompareAction));
        theDBMenu.addSeparator();
        theDBMenu.add(new DefaultMenuItem(theConvertModelAction));

        if (worldConnector.supportsReporting()) {
            documentationMenu = new DefaultMenu(this, ERDesignerBundle.CREATEDBDOCUMENTATION);
            theDBMenu.addSeparator();
            theDBMenu.add(documentationMenu);

            updateDocumentationMenu();
        }

        ERDesignerToolbarEntry theViewMenu = new ERDesignerToolbarEntry(ERDesignerBundle.VIEW);

        DefaultAction theDisplayCommentsAction = new DefaultAction(new ActionEventProcessor() {

            public void processActionEvent(ActionEvent e) {
                DefaultCheckboxMenuItem theItem = (DefaultCheckboxMenuItem) e.getSource();
                commandSetDisplayCommentsState(theItem.isSelected());
            }

        }, this, ERDesignerBundle.DISPLAYCOMMENTS);

        displayCommentsMenuItem = new DefaultCheckboxMenuItem(theDisplayCommentsAction);
        displayCommentsMenuItem.setSelected(true);
        theViewMenu.add(displayCommentsMenuItem);

        DefaultAction theDisplayGridAction = new DefaultAction(new ActionEventProcessor() {

            public void processActionEvent(ActionEvent e) {
                DefaultCheckboxMenuItem theItem = (DefaultCheckboxMenuItem) e.getSource();
                commandSetDisplayGridState(theItem.isSelected());
            }

        }, this, ERDesignerBundle.DISPLAYGRID);

        displayGridMenuItem = new DefaultCheckboxMenuItem(theDisplayGridAction);
        theViewMenu.add(displayGridMenuItem);

        DefaultMenu theDisplayLevelMenu = new DefaultMenu(this, ERDesignerBundle.DISPLAYLEVEL);
        theViewMenu.add(theDisplayLevelMenu);

        DefaultAction theDisplayAllAction = new DefaultAction(new ActionEventProcessor() {

            public void processActionEvent(ActionEvent e) {
                commandSetDisplayLevel(DisplayLevel.ALL);
            }

        }, this, ERDesignerBundle.DISPLAYALL);

        DefaultAction theDisplayPKOnlyAction = new DefaultAction(new ActionEventProcessor() {

            public void processActionEvent(ActionEvent e) {
                commandSetDisplayLevel(DisplayLevel.PRIMARYKEYONLY);
            }

        }, this, ERDesignerBundle.DISPLAYPRIMARYKEY);

        DefaultAction theDisplayPKAndFK = new DefaultAction(new ActionEventProcessor() {

            public void processActionEvent(ActionEvent e) {
                commandSetDisplayLevel(DisplayLevel.PRIMARYKEYSANDFOREIGNKEYS);
            }

        }, this, ERDesignerBundle.DISPLAYPRIMARYKEYANDFOREIGNKEY);

        displayAllMenuItem = new DefaultRadioButtonMenuItem(theDisplayAllAction);
        DefaultRadioButtonMenuItem thePKOnlyItem = new DefaultRadioButtonMenuItem(theDisplayPKOnlyAction);
        DefaultRadioButtonMenuItem thePKAndFKItem = new DefaultRadioButtonMenuItem(theDisplayPKAndFK);

        ButtonGroup theDisplayLevelGroup = new ButtonGroup();
        theDisplayLevelGroup.add(displayAllMenuItem);
        theDisplayLevelGroup.add(thePKOnlyItem);
        theDisplayLevelGroup.add(thePKAndFKItem);

        theDisplayLevelMenu.add(displayAllMenuItem);
        theDisplayLevelMenu.add(thePKOnlyItem);
        theDisplayLevelMenu.add(thePKAndFKItem);

        UIInitializer.getInstance().initialize(theDisplayLevelMenu);

        DefaultMenu theDisplayOrderMenu = new DefaultMenu(this, ERDesignerBundle.DISPLAYORDER);
        theViewMenu.add(theDisplayOrderMenu);

        DefaultAction theDisplayNaturalOrderAction = new DefaultAction(new ActionEventProcessor() {

            public void processActionEvent(ActionEvent e) {
                commandSetDisplayOrder(DisplayOrder.NATURAL);
            }

        }, this, ERDesignerBundle.DISPLAYNATURALORDER);

        DefaultAction theDisplayAscendingOrderAction = new DefaultAction(new ActionEventProcessor() {

            public void processActionEvent(ActionEvent e) {
                commandSetDisplayOrder(DisplayOrder.ASCENDING);
            }

        }, this, ERDesignerBundle.DISPLAYASCENDING);

        DefaultAction theDisplayDescendingOrderAction = new DefaultAction(new ActionEventProcessor() {

            public void processActionEvent(ActionEvent e) {
                commandSetDisplayOrder(DisplayOrder.DESCENDING);
            }

        }, this, ERDesignerBundle.DISPLAYDESCENDING);

        displayNaturalOrderMenuItem = new DefaultRadioButtonMenuItem(theDisplayNaturalOrderAction);
        DefaultRadioButtonMenuItem theAscendingItem = new DefaultRadioButtonMenuItem(theDisplayAscendingOrderAction);
        DefaultRadioButtonMenuItem theDescendingItem = new DefaultRadioButtonMenuItem(theDisplayDescendingOrderAction);

        ButtonGroup theDisplayOrderGroup = new ButtonGroup();
        theDisplayOrderGroup.add(displayNaturalOrderMenuItem);
        theDisplayOrderGroup.add(theAscendingItem);
        theDisplayOrderGroup.add(theDescendingItem);

        theDisplayOrderMenu.add(displayNaturalOrderMenuItem);
        theDisplayOrderMenu.add(theAscendingItem);
        theDisplayOrderMenu.add(theDescendingItem);

        UIInitializer.getInstance().initialize(theDisplayOrderMenu);

        subjectAreas = new DefaultMenu(this, ERDesignerBundle.MENUSUBJECTAREAS);

        UIInitializer.getInstance().initialize(subjectAreas);
        theViewMenu.add(subjectAreas);

        theViewMenu.addSeparator();

        theViewMenu.add(new DefaultMenuItem(theZoomInAction));
        theViewMenu.add(new DefaultMenuItem(theZoomOutAction));

        if (worldConnector.supportsHelp()) {
            theViewMenu.addSeparator();
            theViewMenu.add(new DefaultMenuItem(theHelpAction));
        }

        DefaultComboBoxModel theZoomModel = new DefaultComboBoxModel();
        theZoomModel.addElement(ZOOMSCALE_HUNDREDPERCENT);
        for (int i = 9; i > 0; i--) {
            theZoomModel.addElement(new ZoomInfo(i * 10 + " %", ((double) i) / (double) 10));
        }
        zoomBox.setPreferredSize(new Dimension(100, 21));
        zoomBox.setMaximumSize(new Dimension(100, 21));
        zoomBox.setAction(theZoomAction);
        zoomBox.setModel(theZoomModel);

        DefaultToolbar theToolBar = worldConnector.getToolBar();

        theToolBar.add(theFileMenu);
        theToolBar.add(theDBMenu);
        theToolBar.add(theViewMenu);
        theToolBar.addSeparator();

        theToolBar.add(theNewAction);
        theToolBar.addSeparator();
        theToolBar.add(theLoadAction);
        theToolBar.add(theSaveAsAction);
        theToolBar.addSeparator();
        theToolBar.add(zoomBox);
        theToolBar.addSeparator();
        theToolBar.add(theZoomInAction);
        theToolBar.add(theZoomOutAction);
        theToolBar.addSeparator();

        handButton = new DefaultToggleButton(theHandAction);
        relationButton = new DefaultToggleButton(theRelationAction);
        entityButton = new DefaultToggleButton(theEntityAction);
        commentButton = new DefaultToggleButton(theCommentAction);
        viewButton = new DefaultToggleButton(theViewAction);

        ButtonGroup theGroup = new ButtonGroup();
        theGroup.add(handButton);
        theGroup.add(relationButton);
        theGroup.add(entityButton);
        theGroup.add(commentButton);
        theGroup.add(viewButton);

        theToolBar.add(handButton);
        theToolBar.add(entityButton);
        theToolBar.add(relationButton);
        theToolBar.add(commentButton);
        theToolBar.add(viewButton);

        final DefaultCheckBox theCheckbox = new DefaultCheckBox(ERDesignerBundle.INTELLIGENTLAYOUT);
        theCheckbox.setSelected(preferences.isIntelligentLayout());
        theCheckbox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                setIntelligentLayoutEnabled(theCheckbox.isSelected());
            }

        });

        theToolBar.addSeparator();
        theToolBar.add(theCheckbox);

        worldConnector.initTitle();

        updateRecentlyUsedMenuEntries();

        setupViewForNothing();

        UIInitializer.getInstance().initialize(scrollPane);
    }

    protected boolean checkForValidConnection() {

        if (model.getDialect() == null) {
            MessagesHelper.displayErrorMessage(graph, getResourceHelper().getText(
                    ERDesignerBundle.PLEASEDEFINEADATABASECONNECTIONFIRST));
            return false;
        }

        return true;
    }

    /**
     * Show all subject areas.
     */
    protected void commandShowAllSubjectAreas() {
        for (SubjectArea theArea : model.getSubjectAreas()) {
            commandShowSubjectArea(theArea);
        }
    }

    /**
     * Hide all subject areas.
     */
    protected void commandHideAllSubjectAreas() {
        for (SubjectArea theArea : model.getSubjectAreas()) {
            commandHideSubjectArea(theArea);
        }
    }

    /**
     * Hide a specific subject area.
     * 
     * @param aArea
     *            the area
     */
    protected void commandHideSubjectArea(SubjectArea aArea) {
        for (Object theItem : graph.getGraphLayoutCache().getVisibleSet()) {
            if (theItem instanceof SubjectAreaCell) {
                SubjectAreaCell theCell = (SubjectAreaCell) theItem;
                if (theCell.getUserObject().equals(aArea)) {
                    aArea.setVisible(false);

                    Object[] theCellObjects = new Object[] { theCell };
                    graph.getGraphLayoutCache().hideCells(theCellObjects, true);
                }
            }
        }
        updateSubjectAreasMenu();
    }

    /**
     * Show a specific subject area.
     * 
     * @param aArea
     *            the subject area to show
     */
    protected void commandShowSubjectArea(SubjectArea aArea) {
        for (CellView theCellView : graph.getGraphLayoutCache().getHiddenCellViews()) {
            Object theItem = theCellView.getCell();
            if (theItem instanceof SubjectAreaCell) {
                SubjectAreaCell theCell = (SubjectAreaCell) theItem;
                if (theCell.getUserObject().equals(aArea)) {
                    aArea.setVisible(true);

                    Object[] theCellObjects = DefaultGraphModel.getDescendants(graph.getModel(), new Object[] { theCell })
                            .toArray();

                    graph.getGraphLayoutCache().showCells(theCellObjects, true);
                    for (Object theSingleCell : theCellObjects) {
                        if (theSingleCell instanceof TableCell) {
                            TableCell theTableCell = (TableCell) theSingleCell;
                            Table theTable = (Table) theTableCell.getUserObject();

                            theTableCell.transferPropertiesToAttributes(theTable);
                            graph.getGraphLayoutCache().edit(new Object[] { theTableCell },
                                    theTableCell.getAttributes());
                        }
                        if (theSingleCell instanceof ViewCell) {
                            ViewCell theViewCell = (ViewCell) theSingleCell;
                            View theView = (View) theViewCell.getUserObject();

                            theViewCell.transferPropertiesToAttributes(theView);
                            graph.getGraphLayoutCache().edit(new Object[] { theViewCell }, theViewCell.getAttributes());
                        }
                        if (theSingleCell instanceof CommentCell) {
                            CommentCell theCommentCell = (CommentCell) theSingleCell;
                            Comment theComment = (Comment) theCommentCell.getUserObject();

                            theCommentCell.transferPropertiesToAttributes(theComment);
                            graph.getGraphLayoutCache().edit(new Object[] { theCommentCell },
                                    theCommentCell.getAttributes());
                        }
                    }
                }
            }
        }
        updateSubjectAreasMenu();
    }

    /**
     * Update the create documentation menu.
     */
    protected void updateDocumentationMenu() {
        documentationMenu.removeAll();

        File theReportsFile = preferences.getReportsDirectory();
        try {
            Map<File, String> theReports = JasperUtils.findReportsInDirectory(theReportsFile);
            for (Map.Entry<File, String> theEntry : theReports.entrySet()) {

                final File theJRXMLFile = theEntry.getKey();
                JMenuItem theItem = new JMenuItem();
                theItem.setText(theEntry.getValue());
                theItem.addActionListener(new GenerateDocumentationCommand(this, theJRXMLFile));

                documentationMenu.add(theItem);
            }
        } catch (Exception e) {
            worldConnector.notifyAboutException(e);
        }
        UIInitializer.getInstance().initialize(documentationMenu);
    }

    /**
     * Update the subject area menu.
     */
    protected void updateSubjectAreasMenu() {
        subjectAreas.removeAll();
        subjectAreas.add(new DefaultMenuItem(new DefaultAction(new ActionEventProcessor() {

            public void processActionEvent(ActionEvent aEvent) {
                commandShowAllSubjectAreas();
            }

        }, this, ERDesignerBundle.SHOWALL)));
        subjectAreas.add(new DefaultMenuItem(new DefaultAction(new ActionEventProcessor() {

            public void processActionEvent(ActionEvent aEvent) {
                commandHideAllSubjectAreas();
            }

        }, this, ERDesignerBundle.HIDEALL)));

        if (model.getSubjectAreas().size() > 0) {
            subjectAreas.addSeparator();

            for (SubjectArea theArea : model.getSubjectAreas()) {
                final JCheckBoxMenuItem theItem = new JCheckBoxMenuItem();
                theItem.setText(theArea.getName());
                theItem.setState(theArea.isVisible());
                final SubjectArea theFinalArea = theArea;
                theItem.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (theItem.getState()) {
                            commandShowSubjectArea(theFinalArea);
                        } else {
                            commandHideSubjectArea(theFinalArea);
                        }
                    }

                });

                subjectAreas.add(theItem);
                UIInitializer.getInstance().initialize(theItem);
            }
        }

        UIInitializer.getInstance().initialize(subjectAreas);
    }

    protected void updateRecentlyUsedMenuEntries() {

        lruMenu.removeAll();
        storedConnections.removeAll();

        if (preferences != null) {

            List<File> theFiles = preferences.getRecentlyUsedFiles();
            for (final File theFile : theFiles) {
                JMenuItem theItem = new JMenuItem(theFile.toString());
                theItem.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        new OpenFromFileCommand(ERDesignerComponent.this).execute(theFile);
                    }
                });

                lruMenu.add(theItem);
                UIInitializer.getInstance().initialize(theItem);
            }

            for (final ConnectionDescriptor theConnectionInfo : preferences.getRecentlyUsedConnections()) {
                JMenuItem theItem1 = new JMenuItem(theConnectionInfo.toString());
                theItem1.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        new DBConnectionCommand(ERDesignerComponent.this).execute(theConnectionInfo);
                    }
                });

                storedConnections.add(theItem1);
                UIInitializer.getInstance().initialize(theItem1);
            }
        }
    }

    protected void addCurrentConnectionToConnectionHistory() {

        ConnectionDescriptor theConnection = model.createConnectionHistoryEntry();
        addConnectionToConnectionHistory(theConnection);
    }

    protected void addConnectionToConnectionHistory(ConnectionDescriptor aConnection) {

        preferences.addRecentlyUsedConnection(aConnection);

        updateRecentlyUsedMenuEntries();
    }

    protected void commandNew() {

        Model theModel = worldConnector.createNewModel();
        setModel(theModel);

        setupViewForNothing();

        worldConnector.setStatusText(getResourceHelper().getText(ERDesignerBundle.NEWMODELCREATED));
    }

    /**
     * Setup the view for a model loaded from repository.
     * 
     * @param aDescriptor
     *            the entry descriptor
     */
    protected void setupViewFor(RepositoryEntryDesciptor aDescriptor) {

        currentEditingFile = null;
        currentRepositoryEntry = aDescriptor;
        worldConnector.initTitle(aDescriptor.getName());
        if (worldConnector.supportsRepositories()) {
            repositoryUtilsMenu.setEnabled(true);
        }
    }

    /**
     * Setup the view for a model loaded from file.
     * 
     * @param aFile
     *            the file
     */
    protected void setupViewFor(File aFile) {

        currentEditingFile = aFile;
        currentRepositoryEntry = null;
        worldConnector.initTitle(aFile.toString());
        if (worldConnector.supportsRepositories()) {
            repositoryUtilsMenu.setEnabled(false);
        }
    }

    /**
     * Setup the view for an empty model.
     */
    protected void setupViewForNothing() {

        currentEditingFile = null;
        currentRepositoryEntry = null;
        if (worldConnector.supportsRepositories()) {
            repositoryUtilsMenu.setEnabled(false);
        }
        worldConnector.initTitle();
    }

    /**
     * Set the current editing tool.
     * 
     * @param aTool
     *            the tool
     */
    protected void commandSetTool(ToolEnum aTool) {
        if (aTool.equals(ToolEnum.HAND)) {

            if (!handButton.isSelected()) {
                handButton.setSelected(true);
            }

            graph.setTool(new HandTool(graph));
        }
        if (aTool.equals(ToolEnum.ENTITY)) {

            if (!entityButton.isSelected()) {
                entityButton.setSelected(true);
            }

            graph.setTool(new EntityTool(graph));
        }
        if (aTool.equals(ToolEnum.RELATION)) {

            if (!relationButton.isSelected()) {
                relationButton.setSelected(true);
            }

            graph.setTool(new RelationTool(graph));
        }
        if (aTool.equals(ToolEnum.COMMENT)) {

            if (!commentButton.isSelected()) {
                commentButton.setSelected(true);
            }

            graph.setTool(new CommentTool(graph));
        }
        if (aTool.equals(ToolEnum.VIEW)) {

            if (!viewButton.isSelected()) {
                viewButton.setSelected(true);
            }

            graph.setTool(new ViewTool(graph));
        }
    }

    protected void commandSetZoom(ZoomInfo aZoomInfo) {
        graph.setScale(aZoomInfo.getValue());
        zoomBox.setSelectedItem(aZoomInfo);

        repaintGraph();
    }

    protected void commandZoomIn() {
        int theIndex = zoomBox.getSelectedIndex();
        if (theIndex > 0) {
            theIndex--;
            zoomBox.setSelectedIndex(theIndex);
            commandSetZoom((ZoomInfo) zoomBox.getSelectedItem());
        }
    }

    protected void commandZoomOut() {
        int theIndex = zoomBox.getSelectedIndex();
        if (theIndex < zoomBox.getItemCount() - 1) {
            theIndex++;
            zoomBox.setSelectedIndex(theIndex);
            commandSetZoom((ZoomInfo) zoomBox.getSelectedItem());
        }
    }

    /**
     * Display the application help screen.
     */
    protected void commandShowHelp() {
        PDFViewer theViewer = new PDFViewer(scrollPane, true, getResourceHelper().getText(ERDesignerBundle.ONLINEHELP));
        try {
            theViewer.setMinimumSize(new Dimension(640, 480));
            theViewer.openFile(preferences.getOnlineHelpPDFFile());
        } catch (Exception e) {
            worldConnector.notifyAboutException(e);
        }
    }

    public ResourceHelper getResourceHelper() {
        return ResourceHelper.getResourceHelper(ERDesignerBundle.BUNDLE_NAME);
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
     * Factory Method for the graph layout cacne.
     *
     * @param aModel the graph model
     * @return the newly created graph layout cache
     */
    private GraphLayoutCache createNewGraphlayoutCache(GraphModel aModel) {
        GraphLayoutCache theCache = new GraphLayoutCache(aModel, new CellViewFactory(), true);
        theCache.setAutoSizeOnValueChange(true);
        return theCache;
    }

    /**
     * Set the current editing model.
     * 
     * @param aModel
     *            the model
     */
    public void setModel(Model aModel) {

        try {
            setIntelligentLayoutEnabled(false);

            model = aModel;

            GraphModel theGraphModel = createNewGraphModel();

            graph = new ERDesignerGraph(model, theGraphModel, createNewGraphlayoutCache(theGraphModel)) {

                @Override
                public void commandNewTable(Point2D aLocation) {
                    new AddTableCommand(ERDesignerComponent.this, aLocation, null, false).execute();
                }

                @Override
                public void commandNewComment(Point2D aLocation) {
                    new AddCommentCommand(ERDesignerComponent.this, aLocation).execute();
                }

                @Override
                public void commandNewView(Point2D aLocation) {
                    new AddViewCommand(ERDesignerComponent.this, aLocation).execute();
                }

                @Override
                public void commandHideCells(List<HideableCell> cellsToHide) {
                    ERDesignerComponent.this.commandHideCells(cellsToHide);
                }

                @Override
                public void commandAddToNewSubjectArea(List<ModelCell> aCells) {
                    super.commandAddToNewSubjectArea(aCells);
                    updateSubjectAreasMenu();
                }

                @Override
                public void commandNewTableAndRelation(Point2D aLocation, TableCell aExportingTableCell,
                        boolean aNewTableIsChild) {
                    new AddTableCommand(ERDesignerComponent.this, aLocation, aExportingTableCell, aNewTableIsChild)
                            .execute();
                }

                @Override
                public void commandNewRelation(TableCell aImportingCell, TableCell aExportingCell) {
                    new AddRelationCommand(ERDesignerComponent.this, aImportingCell, aExportingCell).execute();
                }

                @Override
                public void commandLocateInOutline(Object object) {
                    worldConnector.getOutlineComponent().locateObject(object);
                }

                @Override
                public void refreshOutline() {
                    worldConnector.getOutlineComponent().refresh(model, null);
                }
            };

            graph.setUI(new ERDesignerGraphUI(this));

            displayAllMenuItem.setSelected(true);
            displayNaturalOrderMenuItem.setSelected(true);
            displayCommentsMenuItem.setSelected(true);

            commandSetDisplayGridState(displayGridMenuItem.isSelected());
            commandSetDisplayCommentsState(true);
            commandSetDisplayLevel(DisplayLevel.ALL);
            commandSetDisplayOrder(DisplayOrder.NATURAL);

            scrollPane.getViewport().removeAll();
            scrollPane.getViewport().add(graph);

            refreshPreferences(preferences);

            fillGraph(aModel);
            
            worldConnector.getOutlineComponent().setModel(aModel, true);

        } finally {

            commandSetZoom(ZOOMSCALE_HUNDREDPERCENT);
            commandSetTool(ToolEnum.HAND);

            updateSubjectAreasMenu();

            setIntelligentLayoutEnabled(preferences.isIntelligentLayout());
        }
    }

    public Model getModel() {
        return model;
    }

    private class GraphModelMappingInfo {
        Map<Table, TableCell> modelTableCells = new HashMap<Table, TableCell>();

        Map<View, ViewCell> modelViewCells = new HashMap<View, ViewCell>();

        Map<Comment, CommentCell> modelCommentCells = new HashMap<Comment, CommentCell>();
    }

    private GraphModelMappingInfo fillGraph(Model aModel) {

        GraphModel theGraphModel = createNewGraphModel();

        graph.setModel(theGraphModel);
        graph.setGraphLayoutCache(createNewGraphlayoutCache(theGraphModel));

        GraphModelMappingInfo theInfo = new GraphModelMappingInfo();

        for (Table theTable : aModel.getTables()) {
            TableCell theCell = new TableCell(theTable);
            theCell.transferPropertiesToAttributes(theTable);

            graph.getGraphLayoutCache().insert(theCell);

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

            graph.getGraphLayoutCache().insert(theCell);

            theInfo.modelViewCells.put(theView, theCell);
        }

        for (Comment theComment : aModel.getComments()) {
            CommentCell theCell = new CommentCell(theComment);
            theCell.transferPropertiesToAttributes(theComment);

            graph.getGraphLayoutCache().insert(theCell);

            theInfo.modelCommentCells.put(theComment, theCell);
        }

        for (Relation theRelation : aModel.getRelations()) {

            TableCell theImportingCell = theInfo.modelTableCells.get(theRelation.getImportingTable());
            TableCell theExportingCell = theInfo.modelTableCells.get(theRelation.getExportingTable());

            RelationEdge theCell = new RelationEdge(theRelation, theImportingCell, theExportingCell);
            theCell.transferPropertiesToAttributes(theRelation);

            graph.getGraphLayoutCache().insert(theCell);
        }

        for (SubjectArea theSubjectArea : aModel.getSubjectAreas()) {

            SubjectAreaCell theSubjectAreaCell = new SubjectAreaCell(theSubjectArea);
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

            graph.getGraphLayoutCache().insertGroup(theSubjectAreaCell, theTableCells.toArray());
            graph.getGraphLayoutCache().toBack(new Object[] { theSubjectAreaCell });

            if (!theSubjectArea.isVisible()) {
                commandHideSubjectArea(theSubjectArea);
            }

        }

        return theInfo;
    }

    /**
     * Hide a list of specific cells.
     * 
     * @param aCellsToHide
     *            the cells to hide
     */
    protected void commandHideCells(List<HideableCell> aCellsToHide) {
        for (HideableCell theCell : aCellsToHide) {
            if (theCell instanceof SubjectAreaCell) {
                SubjectAreaCell theSA = (SubjectAreaCell) theCell;
                SubjectArea theArea = (SubjectArea) theSA.getUserObject();

                commandHideSubjectArea(theArea);
            }
        }

        updateSubjectAreasMenu();
    }

    protected void addExportEntries(DefaultMenu aMenu, final Exporter aExporter) {

        DefaultAction theAllInOneAction = new DefaultAction(ERDesignerBundle.BUNDLE_NAME, ERDesignerBundle.ALLINONEFILE);
        DefaultMenuItem theAllInOneItem = new DefaultMenuItem(theAllInOneAction);
        theAllInOneAction.addActionListener(new ExportGraphicsCommand(this, aExporter, ExportType.ALL_IN_ONE));
        aMenu.add(theAllInOneItem);

        DefaultAction theOnePerTableAction = new DefaultAction(ERDesignerBundle.BUNDLE_NAME,
                ERDesignerBundle.ONEFILEPERTABLE);
        DefaultMenuItem theOnePerTable = new DefaultMenuItem(theOnePerTableAction);
        theOnePerTableAction.addActionListener(new ExportGraphicsCommand(this, aExporter, ExportType.ONE_PER_FILE));

        aMenu.add(theOnePerTable);
    }

    public JComponent getDetailComponent() {
        return scrollPane;
    }

    public File getCurrentFile() {
        return currentEditingFile;
    }

    /**
     * Save the preferences.
     */
    public void savePreferences() {
        try {
            preferences.store();
        } catch (Exception e) {
            worldConnector.notifyAboutException(e);
        }
    }

    public ERDesignerWorldConnector getWorldConnector() {
        return worldConnector;
    }

    protected void commandRemoveSubjectArea(SubjectAreaCell aCell) {
        graph.getGraphLayoutCache().remove(new Object[] { aCell });
        model.removeSubjectArea((SubjectArea) aCell.getUserObject());

        updateSubjectAreasMenu();
    }

    protected void commandUpdateSubjectArea(SubjectAreaCell aCell) {

        SubjectArea theArea = (SubjectArea) aCell.getUserObject();
        theArea.getTables().clear();
        theArea.getViews().clear();
        theArea.getComments().clear();
        for (Object theObject : aCell.getChildren()) {
            if (theObject instanceof TableCell) {
                theArea.getTables().add((Table) ((TableCell) theObject).getUserObject());
            }
            if (theObject instanceof ViewCell) {
                theArea.getViews().add((View) ((ViewCell) theObject).getUserObject());
            }
            if (theObject instanceof CommentCell) {
                theArea.getComments().add((Comment) ((CommentCell) theObject).getUserObject());
            }
        }

        updateSubjectAreasMenu();
    }

    /**
     * Toggle the include comments view state.
     * 
     * @param aState
     *            true if comments shall be displayed, else false
     */
    protected void commandSetDisplayCommentsState(boolean aState) {
        graph.setDisplayComments(aState);
        repaintGraph();
    }

    /**
     * Toggle the include comments view state.
     * 
     * @param aState
     *            true if comments shall be displayed, else false
     */
    protected void commandSetDisplayGridState(boolean aState) {
        graph.setGridEnabled(aState);
        graph.setGridVisible(aState);
        repaintGraph();
    }

    /**
     * The preferences where changed, so they need to be reloaded.
     * 
     * @param aPreferences
     *            the preferences
     */
    public void refreshPreferences(ApplicationPreferences aPreferences) {
        graph.setGridSize(aPreferences.getGridSize());
        repaintGraph();
    }

    /**
     * Set the current display level.
     * 
     * @param aLevel
     *            the level
     */
    protected void commandSetDisplayLevel(DisplayLevel aLevel) {
        graph.setDisplayLevel(aLevel);
        repaintGraph();
    }

    /**
     * Set the current display order.
     * 
     * @param aOrder
     *            the display order
     */
    protected void commandSetDisplayOrder(DisplayOrder aOrder) {
        graph.setDisplayOrder(aOrder);
        repaintGraph();
    }

    /**
     * Repaint the current graph.
     */
    public void repaintGraph() {
        for (CellView theView : graph.getGraphLayoutCache().getCellViews()) {
            graph.updateAutoSize(theView);
        }
        graph.getGraphLayoutCache().reload();
        graph.getGraphLayoutCache().update(graph.getGraphLayoutCache().getAllViews());

        graph.addOffscreenDirty(new Rectangle2D.Double(0, 0, scrollPane.getWidth(), scrollPane.getHeight()));
        graph.repaint();
    }

    /**
     * Hook method. Will be called if a cell was successfully edited.
     */
    public void commandNotifyAboutEdit() {
        updateSubjectAreasMenu();
    }

    /**
     * Set the status of the intelligent layout functionality.
     * 
     * @param aStatus
     *            true if enabled, else false
     */
    protected void setIntelligentLayoutEnabled(boolean aStatus) {
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
        preferences.setIntelligentLayout(aStatus);
    }

    /**
     * Set the currently selected cell depending on its user object.
     * 
     * @param aItem the user object.
     */
    public void setSelectedObject(ModelItem aItem) {
        for (CellView theView : graph.getGraphLayoutCache().getCellViews()) {
           DefaultGraphCell theCell = (DefaultGraphCell) theView.getCell();
           if (aItem.equals(theCell.getUserObject())) {
               // We do have a match
               graph.setSelectionCell(theCell);
               graph.scrollCellToVisible(theCell);
               return;
           }
        }
    }
}
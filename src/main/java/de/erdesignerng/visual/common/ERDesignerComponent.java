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

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.model.*;
import de.erdesignerng.model.serializer.repository.RepositoryEntryDescriptor;
import de.erdesignerng.util.ApplicationPreferences;
import de.erdesignerng.util.ConnectionDescriptor;
import de.erdesignerng.util.JasperUtils;
import de.erdesignerng.visual.*;
import de.erdesignerng.visual.editor.BaseEditor;
import de.erdesignerng.visual.editor.DialogConstants;
import de.erdesignerng.visual.java2d.EditorPanel;
import de.erdesignerng.visual.java2d.Java2DEditor;
import de.erdesignerng.visual.java3d.Java3DEditor;
import de.erdesignerng.visual.jgraph.JGraphEditor;
import de.mogwai.common.client.looks.UIInitializer;
import de.mogwai.common.client.looks.components.*;
import de.mogwai.common.client.looks.components.action.DefaultAction;
import de.mogwai.common.client.looks.components.menu.DefaultMenu;
import de.mogwai.common.client.looks.components.menu.DefaultMenuItem;
import de.mogwai.common.client.looks.components.menu.DefaultRadioButtonMenuItem;
import de.mogwai.common.i18n.ResourceHelper;
import de.mogwai.common.i18n.ResourceHelperProvider;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The ERDesigner Editing Component.
 * <p/>
 * This is the heart of the system.
 *
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-13 15:40:33 $
 */
public final class ERDesignerComponent implements ResourceHelperProvider {

    protected File currentEditingFile;

    protected RepositoryEntryDescriptor currentRepositoryEntry;

    private volatile Model model;

    private JToggleButton handButton;

    private JToggleButton commentButton;

    private JToggleButton relationButton;

    private JToggleButton viewButton;

    private JToggleButton entityButton;

    private DefaultMenu lruMenu;

    private DefaultMenu storedConnections;

    private DefaultMenu subjectAreas;

    private DefaultMenu documentationMenu;

    private DefaultMenu layoutMenu;

    private DefaultCheckboxMenuItem displayCommentsMenuItem;

    private DefaultCheckboxMenuItem displayGridMenuItem;

    private DefaultRadioButtonMenuItem displayAllMenuItem;

    private DefaultRadioButtonMenuItem displayNaturalOrderMenuItem;

    private DefaultMenu repositoryUtilsMenu;

    private final ERDesignerWorldConnector worldConnector;

    private final DefaultComboBox zoomBox = new DefaultComboBox();

    private DefaultAction zoomInAction;

    private DefaultAction zoomOutAction;

    private static final ZoomInfo ZOOMSCALE_HUNDREDPERCENT = new ZoomInfo(
            "100%", 1);

    private DefaultAction handAction;

    private DefaultAction entityAction;

    private DefaultAction relationAction;

    private DefaultAction commentAction;

    private DefaultAction viewAction;

    private DefaultCheckBox intelligentLayoutCheckbox;

    private DefaultMenu exportMenu;

    private DefaultAction exportOpenXavaAction;

    private DefaultAction displayCommentsAction;

    private DefaultAction displayGridAction;

    private DefaultMenu displayLevelMenu;

    private DefaultMenu displayOrderMenu;

    private DefaultCheckboxMenuItem viewMode2DDiagramMenuItem;
    private DefaultCheckboxMenuItem viewMode2DInteractiveMenuItem;
    private DefaultCheckboxMenuItem viewMode3DInteractiveMenuItem;

    private ToolEnum currentTool = ToolEnum.HAND;

    private final DefaultAction editCustomTypes = new DefaultAction(
            new EditCustomTypesCommand(), this,
            ERDesignerBundle.CUSTOMTYPEEDITOR);

    private GenericModelEditor editor;
    private ModelItem selectedObject;

    private final JPanel editorPanel;

    private static ERDesignerComponent DEFAULT;

    public static ERDesignerComponent initializeComponent(
            ERDesignerWorldConnector aConnector) {
        DEFAULT = new ERDesignerComponent(aConnector);
        return DEFAULT;
    }

    public static ERDesignerComponent getDefault() {
        if (DEFAULT == null) {
            throw new RuntimeException("Component is not initialized");
        }
        return DEFAULT;
    }

    private ERDesignerComponent(ERDesignerWorldConnector aConnector) {
        worldConnector = aConnector;

        editorPanel = new JPanel(new BorderLayout());

        initActions();

        boolean theSuccess = false;

        switch (ApplicationPreferences.getInstance().getEditorMode()) {
            case CLASSIC:
                theSuccess = setEditor2DDiagram();
                break;
            case INTERACTIVE_2D:
                theSuccess = setEditor2DInteractive();
                break;
            case INTERACTIVE_3D:
                theSuccess = setEditor3DInteractive();
                break;
        }

        if (!theSuccess) {
            // Perhaps OGL or Java3D init failed
            // Fallback to 2DDiagram mode
            setEditor2DDiagram();
        }

        if (ApplicationPreferences.getInstance().isIntelligentLayout()) {
            editor.setIntelligentLayoutEnabled(true);
        }

        handAction.actionPerformed(new ActionEvent(editorPanel, MouseEvent.MOUSE_CLICKED, null));
    }

    protected boolean setEditor2DDiagram() {
        setEditor(new JGraphEditor() {
            @Override
            public void commandZoomOneLevelIn() {
                zoomIn();
            }

            @Override
            public void commandZoomOneLevelOut() {
                zoomOut();
            }
        });
        viewMode2DDiagramMenuItem.setSelected(true);
        ApplicationPreferences.getInstance().setEditorMode(EditorMode.CLASSIC);

        return true;
    }

    protected boolean setEditor2DInteractive() {
        setEditor(new Java2DEditor() {
            @Override
            protected void componentClicked(EditorPanel.EditorComponent aComponent, MouseEvent aEvent) {
                if (!SwingUtilities.isRightMouseButton(aEvent)) {
                    if (aEvent.getClickCount() == 1) {
                        OutlineComponent.getDefault().setSelectedItem((ModelItem) aComponent.userObject);
                    } else {
                        ModelItem theItem = (ModelItem) aComponent.userObject;
                        BaseEditor theEditor = EditorFactory.createEditorFor(theItem, editorPanel);
                        if (theEditor.showModal() == DialogConstants.MODAL_RESULT_OK) {
                            try {
                                theEditor.applyValues();

                                ERDesignerComponent.getDefault().updateSubjectAreasMenu();

                                OutlineComponent.getDefault().refresh(ERDesignerComponent.getDefault().getModel());

                                setSelectedObject(theItem);
                            } catch (Exception e1) {
                                ERDesignerComponent.getDefault().getWorldConnector().notifyAboutException(e1);
                            }
                        }
                    }
                } else {
                    DefaultPopupMenu theMenu = new DefaultPopupMenu(ResourceHelper
                            .getResourceHelper(ERDesignerBundle.BUNDLE_NAME));

                    List<ModelItem> theItems = new ArrayList<>();
                    theItems.add((ModelItem) aComponent.userObject);
                    ContextMenuFactory.addActionsToMenu(this, theMenu, theItems);

                    UIInitializer.getInstance().initialize(theMenu);

                    theMenu.show(editorPanel, aEvent.getX(), aEvent.getY());
                }
            }
        });
        viewMode2DInteractiveMenuItem.setSelected(true);
        ApplicationPreferences.getInstance().setEditorMode(EditorMode.INTERACTIVE_2D);

        return true;
    }

    protected boolean setEditor3DInteractive() {
        try {
            setEditor(new Java3DEditor());

            viewMode3DInteractiveMenuItem.setSelected(true);
            ApplicationPreferences.getInstance().setEditorMode(EditorMode.INTERACTIVE_3D);

            return true;
        } catch (Throwable t) {
            MessagesHelper.displayErrorMessage(getDetailComponent(), getResourceHelper().getText(ERDesignerBundle.NOSUPPORTEDOPENGLVENDOR), getResourceHelper().getText(ERDesignerBundle.ERRORINITIALIZING3DMODE));
            return false;
        }
    }

    protected void setEditor(GenericModelEditor aEditor) {

        JComponent theDetail = aEditor.getDetailComponent();

        editorPanel.removeAll();
        editorPanel.add(theDetail, BorderLayout.CENTER);
        UIInitializer.getInstance().initialize(theDetail);

        theDetail.invalidate();
        theDetail.doLayout();

        editorPanel.invalidate();
        editorPanel.doLayout();
        editorPanel.repaint();

        editor = aEditor;
        if (model != null) {
            editor.setModel(model);
            if (selectedObject != null) {
                editor.setSelectedObject(selectedObject);
            }
        }

        // Enable / Disable buttons
        zoomBox.setEnabled(aEditor.supportsZoom());
        zoomInAction.setEnabled(aEditor.supportsZoom());
        zoomOutAction.setEnabled(aEditor.supportsZoom());
        handAction.setEnabled(aEditor.supportsHandAction());
        entityAction.setEnabled(aEditor.supportsEntityAction());
        relationAction.setEnabled(aEditor.supportsRelationAction());
        commentAction.setEnabled(aEditor.supportsCommentAction());
        viewAction.setEnabled(aEditor.supportsViewAction());
        intelligentLayoutCheckbox.setEnabled(aEditor.supportsIntelligentLayout());
        displayCommentsAction.setEnabled(aEditor.supportsCommentAction());
        displayGridAction.setEnabled(aEditor.supportsGrid());
        displayLevelMenu.setEnabled(aEditor.supportsDisplayLevel());
        subjectAreas.setEnabled(aEditor.supportsSubjectAreas());
        displayOrderMenu.setEnabled(aEditor.supportsAttributeOrder());

        exportMenu.removeAll();
        aEditor.initExportEntries(this, exportMenu);
        exportMenu.add(new DefaultMenuItem(exportOpenXavaAction));
        UIInitializer.getInstance().initialize(exportMenu);

        layoutMenu.removeAll();
        aEditor.initLayoutMenu(this, layoutMenu);
        UIInitializer.getInstance().initialize(layoutMenu);

        editor.commandSetDisplayCommentsState(displayCommentsMenuItem.isSelected());
        editor.commandSetDisplayGridState(displayGridMenuItem.isSelected());

        editor.commandSetTool(currentTool);

        aEditor.repaintGraph();
    }

    private void commandSetTool(ToolEnum aTool) {
        currentTool = aTool;
        editor.commandSetTool(aTool);
    }

    protected final void initActions() {

        // Required by Java3D
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);

        DefaultAction theReverseEngineerAction = new DefaultAction(
                new ReverseEngineerCommand(), this,
                ERDesignerBundle.REVERSEENGINEER);

        DefaultAction thePreferencesAction = new DefaultAction(
                new PreferencesCommand(), this,
                ERDesignerBundle.PREFERENCES);

        DefaultAction theSaveAction = new DefaultAction(new SaveToFileCommand(
        ), this, ERDesignerBundle.SAVEMODEL);
        theSaveAction.putValue(DefaultAction.HOTKEY_KEY, KeyStroke
                .getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));

        DefaultAction theSaveAsAction = new DefaultAction(
                aEvent -> new SaveToFileCommand()
                        .executeSaveFileAs(), this, ERDesignerBundle.SAVEMODELAS);

        DefaultAction theSaveToRepository = new DefaultAction(
                new SaveToRepositoryCommand(), this,
                ERDesignerBundle.SAVEMODELTODB);

        relationAction = new DefaultAction(
                e -> {
                    commandSetTool(ToolEnum.RELATION);
                    if (!relationButton.isSelected()) {
                        relationButton.setSelected(true);
                    }
                }, this, ERDesignerBundle.RELATION);

        DefaultAction theNewAction = new DefaultAction(
                e -> commandNew(), this, ERDesignerBundle.NEWMODEL);

        DefaultAction theLruAction = new DefaultAction(this,
                ERDesignerBundle.RECENTLYUSEDFILES);

        DefaultAction theLoadAction = new DefaultAction(
                new OpenFromFileCommand(), this, ERDesignerBundle.LOADMODEL);

        handAction = new DefaultAction(
                e -> {
                    commandSetTool(ToolEnum.HAND);

                    if (!handButton.isSelected()) {
                        handButton.setSelected(true);
                    }
                }, this, ERDesignerBundle.HAND);

        commentAction = new DefaultAction(
                e -> {
                    commandSetTool(ToolEnum.COMMENT);
                    if (!commentButton.isSelected()) {
                        commentButton.setSelected(true);
                    }
                }, this, ERDesignerBundle.COMMENT);

        entityAction = new DefaultAction(
                e -> {
                    commandSetTool(ToolEnum.ENTITY);
                    if (!entityButton.isSelected()) {
                        entityButton.setSelected(true);
                    }
                }, this, ERDesignerBundle.ENTITY);

        viewAction = new DefaultAction(
                e -> {
                    commandSetTool(ToolEnum.VIEW);
                    if (!viewButton.isSelected()) {
                        viewButton.setSelected(true);
                    }
                }, this, ERDesignerBundle.VIEWTOOL);

        DefaultAction theExportAction = new DefaultAction(this,
                ERDesignerBundle.EXPORT);

        DefaultAction theExitAction = new DefaultAction(
                e -> worldConnector.exitApplication(), this, ERDesignerBundle.EXITPROGRAM);

        DefaultAction theClasspathAction = new DefaultAction(
                new ClasspathCommand(), this, ERDesignerBundle.CLASSPATH);

        DefaultAction theDBConnectionAction = new DefaultAction(
                new DBConnectionCommand(), this,
                ERDesignerBundle.DBCONNECTION);

        DefaultAction theRepositoryConnectionAction = new DefaultAction(
                new RepositoryConnectionCommand(), this,
                ERDesignerBundle.REPOSITORYCONNECTION);

        DefaultAction theDomainsAction = new DefaultAction(
                new EditDomainCommand(), this,
                ERDesignerBundle.DOMAINEDITOR);

        DefaultAction theZoomAction = new DefaultAction(
                aEvent -> editor.commandSetZoom((ZoomInfo) ((JComboBox) aEvent
                        .getSource()).getSelectedItem()), this, ERDesignerBundle.ZOOM);

        zoomInAction = new DefaultAction(
                e -> zoomIn(), this, ERDesignerBundle.ZOOMIN);

        zoomOutAction = new DefaultAction(
                e -> zoomOut(), this, ERDesignerBundle.ZOOMOUT);

        DefaultAction theGenerateSQL = new DefaultAction(
                new GenerateSQLCommand(), this,
                ERDesignerBundle.GENERATECREATEDBDDL);

        DefaultAction theGenerateChangelog = new DefaultAction(
                new GenerateChangeLogSQLCommand(), this,
                ERDesignerBundle.GENERATECHANGELOG);

        DefaultAction theCompleteCompareWithDatabaseAction = new DefaultAction(
                new CompleteCompareWithDatabaseCommand(), this,
                ERDesignerBundle.COMPLETECOMPAREWITHDATABASE);

        DefaultAction theCompleteCompareWithModelAction = new DefaultAction(
                new CompleteCompareWithOtherModelCommand(), this,
                ERDesignerBundle.COMPLETECOMPAREWITHOTHERMODEL);

        DefaultAction theConvertModelAction = new DefaultAction(
                new ConvertModelCommand(), this,
                ERDesignerBundle.CONVERTMODEL);

        DefaultAction theCreateMigrationScriptAction = new DefaultAction(
                new GenerateMigrationScriptCommand(), this,
                ERDesignerBundle.CREATEMIGRATIONSCRIPT);

        DefaultAction theCkeckModelAction = new DefaultAction(
                new ModelCheckCommand(), this,
                ERDesignerBundle.CHECKMODELFORERRORS);

        DefaultAction theHelpAction = new DefaultAction(
                aEvent -> commandShowHelp(), this, ERDesignerBundle.HELP);

        exportOpenXavaAction = new DefaultAction(
                new OpenXavaExportExportCommand(), this,
                ERDesignerBundle.OPENXAVAEXPORT);

        lruMenu = new DefaultMenu(theLruAction);

        DefaultAction theStoredConnectionsAction = new DefaultAction(this,
                ERDesignerBundle.STOREDDBCONNECTION);
        storedConnections = new DefaultMenu(theStoredConnectionsAction);

        ERDesignerToolbarEntry theFileMenu = new ERDesignerToolbarEntry(
                ERDesignerBundle.FILE);
        if (worldConnector.supportsPreferences()) {
            theFileMenu.add(new DefaultMenuItem(thePreferencesAction));
            theFileMenu.addSeparator();
        }

        theFileMenu.add(new DefaultMenuItem(theNewAction));
        theFileMenu.addSeparator();
        DefaultMenuItem theSaveItem = new DefaultMenuItem(theSaveAction);
        theFileMenu.add(theSaveItem);
        KeyStroke theStroke = (KeyStroke) theSaveAction
                .getValue(DefaultAction.HOTKEY_KEY);
        if (theStroke != null) {
            theSaveItem.setAccelerator(theStroke);
            getDetailComponent().registerKeyboardAction(theSaveAction, theStroke,
                    JComponent.WHEN_IN_FOCUSED_WINDOW);
        }

        theFileMenu.add(new DefaultMenuItem(theSaveAsAction));
        theFileMenu.add(new DefaultMenuItem(theLoadAction));

        if (worldConnector.supportsRepositories()) {
            theFileMenu.addSeparator();
            theFileMenu.add(new DefaultMenuItem(theRepositoryConnectionAction));
            theFileMenu.add(new DefaultMenuItem(theSaveToRepository));

            DefaultMenuItem theLoadFromDBMenu = new DefaultMenuItem(
                    new DefaultAction(new OpenFromRepositoryCommand(),
                            this, ERDesignerBundle.LOADMODELFROMDB));

            theFileMenu.add(theLoadFromDBMenu);

            repositoryUtilsMenu = new DefaultMenu(this,
                    ERDesignerBundle.REPOSITORYUTILS);
            repositoryUtilsMenu.add(new DefaultMenuItem(
                    theCreateMigrationScriptAction));

            UIInitializer.getInstance().initialize(repositoryUtilsMenu);

            theFileMenu.add(repositoryUtilsMenu);

            theFileMenu.addSeparator();
        }

        exportMenu = new DefaultMenu(theExportAction);
        theFileMenu.add(exportMenu);

        theFileMenu.addSeparator();
        theFileMenu.add(lruMenu);

        if (worldConnector.supportsExitApplication()) {
            theFileMenu.addSeparator();
            theFileMenu.add(new DefaultMenuItem(theExitAction));
        }

        ERDesignerToolbarEntry theDBMenu = new ERDesignerToolbarEntry(
                ERDesignerBundle.DATABASE);

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

        theDBMenu.add(new DefaultMenuItem(editCustomTypes));
        theDBMenu.add(new DefaultMenuItem(theDomainsAction));
        theDBMenu.addSeparator();

        theDBMenu.add(new DefaultMenuItem(theReverseEngineerAction));
        theDBMenu.addSeparator();
        theDBMenu.add(new DefaultMenuItem(theGenerateSQL));
        theDBMenu.addSeparator();
        theDBMenu.add(new DefaultMenuItem(theGenerateChangelog));
        theDBMenu.addSeparator();
        theDBMenu.add(new DefaultMenuItem(theCkeckModelAction));
        theDBMenu.addSeparator();
        theDBMenu
                .add(new DefaultMenuItem(theCompleteCompareWithDatabaseAction));
        theDBMenu.add(new DefaultMenuItem(theCompleteCompareWithModelAction));
        theDBMenu.addSeparator();
        theDBMenu.add(new DefaultMenuItem(theConvertModelAction));

        if (worldConnector.supportsReporting()) {
            documentationMenu = new DefaultMenu(this,
                    ERDesignerBundle.CREATEDBDOCUMENTATION);
            theDBMenu.addSeparator();
            theDBMenu.add(documentationMenu);

            updateDocumentationMenu();
        }

        ERDesignerToolbarEntry theViewMenu = new ERDesignerToolbarEntry(
                ERDesignerBundle.VIEW);

        DefaultMenu theViewModeMenu = new DefaultMenu(this,
                ERDesignerBundle.VIEWMODE);

        DefaultAction theViewMode2DDiagramAction = new DefaultAction(
                e -> setEditor2DDiagram(), this, ERDesignerBundle.VIEWMODE2DDIAGRAM);
        DefaultAction theViewMode2DInteractiveAction = new DefaultAction(
                e -> setEditor2DInteractive(), this, ERDesignerBundle.VIEWMODE2DINTERACTIVE);
        DefaultAction theViewMode3DInteractiveAction = new DefaultAction(
                e -> setEditor3DInteractive(), this, ERDesignerBundle.VIEWMODE3DINTERACTIVE);


        viewMode2DDiagramMenuItem = new DefaultCheckboxMenuItem(
                theViewMode2DDiagramAction);
        viewMode2DInteractiveMenuItem = new DefaultCheckboxMenuItem(
                theViewMode2DInteractiveAction);
        viewMode3DInteractiveMenuItem = new DefaultCheckboxMenuItem(
                theViewMode3DInteractiveAction);

        theViewModeMenu.add(viewMode2DDiagramMenuItem);
        theViewModeMenu.add(viewMode2DInteractiveMenuItem);
        theViewModeMenu.add(viewMode3DInteractiveMenuItem);

        ButtonGroup theDisplayModeGroup = new ButtonGroup();
        theDisplayModeGroup.add(viewMode2DDiagramMenuItem);
        theDisplayModeGroup.add(viewMode2DInteractiveMenuItem);
        theDisplayModeGroup.add(viewMode3DInteractiveMenuItem);

        viewMode2DInteractiveMenuItem.setSelected(true);

        theViewMenu.add(theViewModeMenu);
        UIInitializer.getInstance().initialize(theViewModeMenu);

        displayCommentsAction = new DefaultAction(
                e -> {
                    DefaultCheckboxMenuItem theItem = (DefaultCheckboxMenuItem) e
                            .getSource();
                    editor.commandSetDisplayCommentsState(theItem.isSelected());
                }, this, ERDesignerBundle.DISPLAYCOMMENTS);

        displayCommentsMenuItem = new DefaultCheckboxMenuItem(
                displayCommentsAction);
        displayCommentsMenuItem.setSelected(false);
        theViewMenu.add(displayCommentsMenuItem);

        displayGridAction = new DefaultAction(
                e -> {
                    DefaultCheckboxMenuItem theItem = (DefaultCheckboxMenuItem) e
                            .getSource();
                    editor.commandSetDisplayGridState(theItem.isSelected());
                }, this, ERDesignerBundle.DISPLAYGRID);

        displayGridMenuItem = new DefaultCheckboxMenuItem(displayGridAction);
        theViewMenu.add(displayGridMenuItem);

        displayLevelMenu = new DefaultMenu(this,
                ERDesignerBundle.DISPLAYLEVEL);
        theViewMenu.add(displayLevelMenu);

        DefaultAction theDisplayAllAction = new DefaultAction(
                e -> editor.commandSetDisplayLevel(DisplayLevel.ALL), this, ERDesignerBundle.DISPLAYALL);

        DefaultAction theDisplayPKOnlyAction = new DefaultAction(
                e -> editor.commandSetDisplayLevel(DisplayLevel.PRIMARYKEYONLY), this, ERDesignerBundle.DISPLAYPRIMARYKEY);

        DefaultAction theDisplayPKAndFK = new DefaultAction(
                e -> editor.commandSetDisplayLevel(DisplayLevel.PRIMARYKEYSANDFOREIGNKEYS), this, ERDesignerBundle.DISPLAYPRIMARYKEYANDFOREIGNKEY);

        displayAllMenuItem = new DefaultRadioButtonMenuItem(theDisplayAllAction);
        DefaultRadioButtonMenuItem thePKOnlyItem = new DefaultRadioButtonMenuItem(
                theDisplayPKOnlyAction);
        DefaultRadioButtonMenuItem thePKAndFKItem = new DefaultRadioButtonMenuItem(
                theDisplayPKAndFK);

        ButtonGroup theDisplayLevelGroup = new ButtonGroup();
        theDisplayLevelGroup.add(displayAllMenuItem);
        theDisplayLevelGroup.add(thePKOnlyItem);
        theDisplayLevelGroup.add(thePKAndFKItem);

        displayLevelMenu.add(displayAllMenuItem);
        displayLevelMenu.add(thePKOnlyItem);
        displayLevelMenu.add(thePKAndFKItem);

        UIInitializer.getInstance().initialize(displayLevelMenu);

        displayOrderMenu = new DefaultMenu(this,
                ERDesignerBundle.DISPLAYORDER);
        theViewMenu.add(displayOrderMenu);

        DefaultAction theDisplayNaturalOrderAction = new DefaultAction(
                e -> editor.commandSetDisplayOrder(DisplayOrder.NATURAL), this, ERDesignerBundle.DISPLAYNATURALORDER);

        DefaultAction theDisplayAscendingOrderAction = new DefaultAction(
                e -> editor.commandSetDisplayOrder(DisplayOrder.ASCENDING), this, ERDesignerBundle.DISPLAYASCENDING);

        DefaultAction theDisplayDescendingOrderAction = new DefaultAction(
                e -> editor.commandSetDisplayOrder(DisplayOrder.DESCENDING), this, ERDesignerBundle.DISPLAYDESCENDING);

        displayNaturalOrderMenuItem = new DefaultRadioButtonMenuItem(
                theDisplayNaturalOrderAction);
        DefaultRadioButtonMenuItem theAscendingItem = new DefaultRadioButtonMenuItem(
                theDisplayAscendingOrderAction);
        DefaultRadioButtonMenuItem theDescendingItem = new DefaultRadioButtonMenuItem(
                theDisplayDescendingOrderAction);

        ButtonGroup theDisplayOrderGroup = new ButtonGroup();
        theDisplayOrderGroup.add(displayNaturalOrderMenuItem);
        theDisplayOrderGroup.add(theAscendingItem);
        theDisplayOrderGroup.add(theDescendingItem);

        displayOrderMenu.add(displayNaturalOrderMenuItem);
        displayOrderMenu.add(theAscendingItem);
        displayOrderMenu.add(theDescendingItem);

        UIInitializer.getInstance().initialize(displayOrderMenu);

        subjectAreas = new DefaultMenu(this, ERDesignerBundle.MENUSUBJECTAREAS);
        UIInitializer.getInstance().initialize(subjectAreas);
        theViewMenu.add(subjectAreas);

        theViewMenu.addSeparator();

        layoutMenu = new DefaultMenu(this, ERDesignerBundle.LAYOUT);
        UIInitializer.getInstance().initialize(layoutMenu);
        theViewMenu.add(layoutMenu);

        theViewMenu.addSeparator();

        theViewMenu.add(new DefaultMenuItem(zoomInAction));
        theViewMenu.add(new DefaultMenuItem(zoomOutAction));

        if (worldConnector.supportsHelp()) {
            theViewMenu.addSeparator();
            theViewMenu.add(new DefaultMenuItem(theHelpAction));
        }

        DefaultComboBoxModel theZoomModel = new DefaultComboBoxModel();
        theZoomModel.addElement(ZOOMSCALE_HUNDREDPERCENT);
        for (int i = 95; i > 0; i -= 5) {
            theZoomModel.addElement(new ZoomInfo(i + " %", ((double) i)
                    / (double) 100));
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
        theToolBar.add(zoomInAction);
        theToolBar.add(zoomOutAction);
        theToolBar.addSeparator();

        handButton = new DefaultToggleButton(handAction);
        relationButton = new DefaultToggleButton(relationAction);
        entityButton = new DefaultToggleButton(entityAction);
        commentButton = new DefaultToggleButton(commentAction);
        viewButton = new DefaultToggleButton(viewAction);

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

        intelligentLayoutCheckbox = new DefaultCheckBox(
                ERDesignerBundle.INTELLIGENTLAYOUT);
        intelligentLayoutCheckbox.setSelected(ApplicationPreferences.getInstance()
                .isIntelligentLayout());
        intelligentLayoutCheckbox.addActionListener(e -> editor.setIntelligentLayoutEnabled(intelligentLayoutCheckbox.isSelected()));

        theToolBar.addSeparator();
        theToolBar.add(intelligentLayoutCheckbox);

        worldConnector.initTitle();

        updateRecentlyUsedMenuEntries();

        setupViewForNothing();
    }

    private void zoomOut() {
        int theIndex = zoomBox.getSelectedIndex();
        if (theIndex < zoomBox.getItemCount() - 1) {
            theIndex++;
            zoomBox.setSelectedIndex(theIndex);
            editor.commandSetZoom((ZoomInfo) zoomBox.getSelectedItem());
        }
    }

    private void zoomIn() {
        int theIndex = zoomBox.getSelectedIndex();
        if (theIndex > 0) {
            theIndex--;
            zoomBox.setSelectedIndex(theIndex);
            editor.commandSetZoom((ZoomInfo) zoomBox.getSelectedItem());
        }
    }

    protected boolean checkForValidConnection() {

        if (model.getDialect() == null) {
            MessagesHelper
                    .displayErrorMessage(
                            getDetailComponent(),
                            getResourceHelper()
                                    .getText(
                                            ERDesignerBundle.PLEASEDEFINEADATABASECONNECTIONFIRST));
            return false;
        }

        return true;
    }

    /**
     * Show all subject areas.
     */
    protected void commandShowAllSubjectAreas() {
        model.getSubjectAreas().forEach(editor::commandShowSubjectArea);
    }

    /**
     * Hide all subject areas.
     */
    protected void commandHideAllSubjectAreas() {
        model.getSubjectAreas().forEach(editor::commandHideSubjectArea);
    }

    /**
     * Update the create documentation menu.
     */
    protected void updateDocumentationMenu() {
        documentationMenu.removeAll();

        File theReportsFile = ApplicationPreferences.getInstance()
                .getReportsDirectory();
        try {
            Map<File, String> theReports = JasperUtils
                    .findReportsInDirectory(theReportsFile);
            for (Map.Entry<File, String> theEntry : theReports.entrySet()) {

                File theJRXMLFile = theEntry.getKey();
                JMenuItem theItem = new JMenuItem();
                theItem.setText(theEntry.getValue());
                theItem.addActionListener(new GenerateDocumentationCommand(
                        theJRXMLFile));

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
    public void updateSubjectAreasMenu() {
        subjectAreas.removeAll();
        subjectAreas.add(new DefaultMenuItem(new DefaultAction(
                aEvent -> commandShowAllSubjectAreas(), this, ERDesignerBundle.SHOWALL)));
        subjectAreas.add(new DefaultMenuItem(new DefaultAction(
                aEvent -> commandHideAllSubjectAreas(), this, ERDesignerBundle.HIDEALL)));

        if (model.getSubjectAreas().size() > 0) {
            subjectAreas.addSeparator();

            for (SubjectArea theArea : model.getSubjectAreas()) {
                final JCheckBoxMenuItem theItem = new JCheckBoxMenuItem();
                theItem.setText(theArea.getName());
                theItem.setState(theArea.isVisible());
                final SubjectArea theFinalArea = theArea;
                theItem.addActionListener(e -> {
                    if (theItem.getState()) {
                        editor.commandShowSubjectArea(theFinalArea);
                    } else {
                        editor.commandHideSubjectArea(theFinalArea);
                    }
                });

                subjectAreas.add(theItem);
                UIInitializer.getInstance().initialize(theItem);
            }
        }

        UIInitializer.getInstance().initialize(subjectAreas);

        OutlineComponent.getDefault().refresh(model);
    }

    protected void updateRecentlyUsedMenuEntries() {

        lruMenu.removeAll();
        storedConnections.removeAll();

        List<File> theFiles = ApplicationPreferences.getInstance()
                .getRecentlyUsedFiles();
        for (final File theFile : theFiles) {
            JMenuItem theItem = new JMenuItem(theFile.toString());
            theItem.addActionListener(e -> commandOpenFile(theFile));

            lruMenu.add(theItem);
            UIInitializer.getInstance().initialize(theItem);
        }

        for (final ConnectionDescriptor theConnectionInfo : ApplicationPreferences
                .getInstance().getRecentlyUsedConnections()) {
            JMenuItem theItem1 = new JMenuItem(theConnectionInfo.toString());
            theItem1.addActionListener(e -> new DBConnectionCommand()
                    .execute(theConnectionInfo));

            storedConnections.add(theItem1);
            UIInitializer.getInstance().initialize(theItem1);
        }
    }

    protected void addCurrentConnectionToConnectionHistory() {

        ConnectionDescriptor theConnection = model
                .createConnectionHistoryEntry();
        addConnectionToConnectionHistory(theConnection);

        editCustomTypes.setEnabled(model.getDialect().isSupportsCustomTypes());
    }

    protected void addConnectionToConnectionHistory(
            ConnectionDescriptor aConnection) {

        ApplicationPreferences.getInstance().addRecentlyUsedConnection(
                aConnection);

        updateRecentlyUsedMenuEntries();
    }

    protected void commandNew() {

        Model theModel = worldConnector.createNewModel();
        setModel(theModel);

        setupViewForNothing();

        worldConnector.setStatusText(getResourceHelper().getText(
                ERDesignerBundle.NEWMODELCREATED));
    }

    /**
     * Setup the view for a model loaded from repository.
     *
     * @param aDescriptor the entry descriptor
     */
    protected void setupViewFor(RepositoryEntryDescriptor aDescriptor) {

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
     * @param aFile the file
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
     * Display the application help screen.
     */
    protected void commandShowHelp() {
        try {
            URI theFile = ApplicationPreferences.getInstance()
                    .getOnlineHelpPDFFile();
            Desktop.getDesktop().browse(theFile);
        } catch (Exception e) {
            worldConnector.notifyAboutException(e);
        }
    }

    @Override
    public ResourceHelper getResourceHelper() {
        return ResourceHelper.getResourceHelper(ERDesignerBundle.BUNDLE_NAME);
    }

    /**
     * Set the current editing model.
     *
     * @param aModel the model
     */
    public void setModel(Model aModel) {

        //SubjectAreaHelper theHelper = new SubjectAreaHelper();
        //theHelper.computeCluster(aModel);

        try {
            editor.setIntelligentLayoutEnabled(false);

            model = aModel;
            selectedObject = null;

            editor.setModel(model);

            SQLComponent.getDefault().resetDisplay();

            displayAllMenuItem.setSelected(true);
            displayNaturalOrderMenuItem.setSelected(true);
            displayCommentsMenuItem.setSelected(false);
            if (aModel != null && aModel.getDialect() != null) {
                editCustomTypes.setEnabled(aModel.getDialect()
                        .isSupportsCustomTypes());
            } else {
                editCustomTypes.setEnabled(false);
            }

            editor.commandSetDisplayGridState(displayGridMenuItem.isSelected());
            editor.commandSetDisplayCommentsState(false);

            OutlineComponent.getDefault().setModel(aModel);

        } finally {

            zoomBox.setSelectedItem(ZOOMSCALE_HUNDREDPERCENT);
            editor.commandSetZoom(ZOOMSCALE_HUNDREDPERCENT);

            handAction.actionPerformed(new ActionEvent(editorPanel, MouseEvent.MOUSE_CLICKED, null));

            updateSubjectAreasMenu();

            editor.setIntelligentLayoutEnabled(ApplicationPreferences.getInstance()
                    .isIntelligentLayout());
        }
    }

    public Model getModel() {
        return model;
    }

    public JComponent getDetailComponent() {
        return editorPanel;
    }

    /**
     * Save the preferences.
     */
    public void savePreferences() {
        try {
            ApplicationPreferences.getInstance().store();
        } catch (Exception e) {
            worldConnector.notifyAboutException(e);
        }
    }

    public ERDesignerWorldConnector getWorldConnector() {
        return worldConnector;
    }

    public void repaintGraph() {
        editor.repaintGraph();
    }

    public void setIntelligentLayoutEnabled(boolean b) {
        editor.setIntelligentLayoutEnabled(b);
    }

    public void setSelectedObject(ModelItem aObject) {
        selectedObject = aObject;
        editor.setSelectedObject(aObject);
    }

    public void refreshPreferences() {
        editor.refreshPreferences();
    }

    public void commandCreateComment(Comment aComment, Point2D aLocation) {
        editor.commandCreateComment(aComment, aLocation);
    }

    public void commandCreateRelation(Relation aRelation) {
        editor.commandCreateRelation(aRelation);
    }

    public void commandCreateTable(Table aTable, Point2D aLocation) {
        editor.commandCreateTable(aTable, aLocation);
    }

    public void commandCreateView(View aView, Point2D aLocation) {
        editor.commandCreateView(aView, aLocation);
    }

    public void commandHideSubjectArea(SubjectArea aSubjectArea) {
        editor.commandHideSubjectArea(aSubjectArea);
    }

    public void commandAddToNewSubjectArea(List<ModelItem> aNewSubjectAreaItems) {
        editor.commandAddToNewSubjectArea(aNewSubjectAreaItems);
    }

    public void commandDelete(List<ModelItem> aItemsToBeDeleted) {
        editor.commandDelete(aItemsToBeDeleted);
    }

    public void commandShowOrHideRelationsFor(Table aTable, boolean aShow) {
        editor.commandShowOrHideRelationsFor(aTable, aShow);
    }

    public GenericModelEditor getEditor() {
        return editor;
    }

    public void commandOpenFile(File aFile) {
        FileInputStream theStream = null;

        try {
            theStream = new FileInputStream(aFile);

            Model theModel = ModelIOUtilities.getInstance()
                    .deserializeModelFromXML(theStream);
            getWorldConnector().initializeLoadedModel(theModel);

            setModel(theModel);

            ApplicationPreferences.getInstance().addRecentlyUsedFile(aFile);

            addCurrentConnectionToConnectionHistory();

            setupViewFor(aFile);
            getWorldConnector().setStatusText(
                    getResourceHelper().getText(
                            ERDesignerBundle.FILELOADED));

        } catch (Exception e) {

            MessagesHelper.displayErrorMessage(getDetailComponent(), getResourceHelper().getText(
                    ERDesignerBundle.ERRORLOADINGFILE));

            getWorldConnector().notifyAboutException(e);
        } finally {
            if (theStream != null) {
                try {
                    theStream.close();
                } catch (IOException e) {
                    // Ignore this exception
                }
            }
        }
    }
}
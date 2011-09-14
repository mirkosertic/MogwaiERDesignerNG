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
import de.erdesignerng.model.Comment;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.ModelItem;
import de.erdesignerng.model.Relation;
import de.erdesignerng.model.SubjectArea;
import de.erdesignerng.model.Table;
import de.erdesignerng.model.View;
import de.erdesignerng.model.check.ModelChecker;
import de.erdesignerng.model.serializer.repository.RepositoryEntryDescriptor;
import de.erdesignerng.util.ApplicationPreferences;
import de.erdesignerng.util.ConnectionDescriptor;
import de.erdesignerng.util.JasperUtils;
import de.erdesignerng.visual.DisplayLevel;
import de.erdesignerng.visual.DisplayOrder;
import de.erdesignerng.visual.EditorFactory;
import de.erdesignerng.visual.MessagesHelper;
import de.erdesignerng.visual.editor.BaseEditor;
import de.erdesignerng.visual.editor.DialogConstants;
import de.erdesignerng.visual.java2d.EditorPanel;
import de.erdesignerng.visual.java2d.Java2DEditor;
import de.erdesignerng.visual.java3d.Java3DEditor;
import de.erdesignerng.visual.jgraph.JGraphEditor;
import de.mogwai.common.client.looks.UIInitializer;
import de.mogwai.common.client.looks.components.DefaultCheckBox;
import de.mogwai.common.client.looks.components.DefaultCheckboxMenuItem;
import de.mogwai.common.client.looks.components.DefaultComboBox;
import de.mogwai.common.client.looks.components.DefaultPopupMenu;
import de.mogwai.common.client.looks.components.DefaultToggleButton;
import de.mogwai.common.client.looks.components.DefaultToolbar;
import de.mogwai.common.client.looks.components.action.ActionEventProcessor;
import de.mogwai.common.client.looks.components.action.DefaultAction;
import de.mogwai.common.client.looks.components.menu.DefaultMenu;
import de.mogwai.common.client.looks.components.menu.DefaultMenuItem;
import de.mogwai.common.client.looks.components.menu.DefaultRadioButtonMenuItem;
import de.mogwai.common.i18n.ResourceHelper;
import de.mogwai.common.i18n.ResourceHelperProvider;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.io.File;
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
public class ERDesignerComponent implements ResourceHelperProvider {

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

    private DefaultCheckboxMenuItem displayCommentsMenuItem;

    private DefaultCheckboxMenuItem displayGridMenuItem;

    private DefaultRadioButtonMenuItem displayAllMenuItem;

    private DefaultRadioButtonMenuItem displayNaturalOrderMenuItem;

    private DefaultMenu repositoryUtilsMenu;

    private final ERDesignerWorldConnector worldConnector;

    private final DefaultComboBox zoomBox = new DefaultComboBox();

    private DefaultAction theZoomInAction;

    private DefaultAction theZoomOutAction;

    private static final ZoomInfo ZOOMSCALE_HUNDREDPERCENT = new ZoomInfo(
            "100%", 1);

    private DefaultAction theHandAction;

    private DefaultAction theEntityAction;

    private DefaultAction theRelationAction;

    private DefaultAction theCommentAction;

    private DefaultAction theViewAction;

    private DefaultCheckBox theIntelligentLayoutCheckbox;

    private DefaultMenu theExportMenu;

    private DefaultAction theExportOpenXavaAction;

    private DefaultAction theDisplayCommentsAction;

    private DefaultAction theDisplayGridAction;

    private DefaultMenu theDisplayLevelMenu;

    private DefaultMenu theDisplayOrderMenu;

    private ToolEnum currentTool = ToolEnum.HAND;

    private final DefaultAction editCustomTypes = new DefaultAction(
            new EditCustomTypesCommand(), this,
            ERDesignerBundle.CUSTOMTYPEEDITOR);

    private GenericModelEditor editor;
    private ModelItem selectedObject;

    private JPanel editorPanel;

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

        setEditor2DInteractive();

        if (ApplicationPreferences.getInstance().isIntelligentLayout()) {
            editor.setIntelligentLayoutEnabled(true);
        }
    }

    protected void setEditor2DDiagram() {
        setEditor(new JGraphEditor());
    }

    protected void setEditor2DInteractive() {
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

                    List<ModelItem> theItems = new ArrayList<ModelItem>();
                    theItems.add((ModelItem) aComponent.userObject);
                    ContextMenuFactory.addActionsToMenu(this, theMenu, theItems);

                    UIInitializer.getInstance().initialize(theMenu);

                    theMenu.show(editorPanel, aEvent.getX(), aEvent.getY());
                }
            }
        });
    }

    protected void setEditor3DInteractive() {
        setEditor(new Java3DEditor());
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
        theZoomInAction.setEnabled(aEditor.supportsZoom());
        theZoomOutAction.setEnabled(aEditor.supportsZoom());
        theHandAction.setEnabled(aEditor.supportsHandAction());
        theEntityAction.setEnabled(aEditor.supportsEntityAction());
        theRelationAction.setEnabled(aEditor.supportsRelationAction());
        theCommentAction.setEnabled(aEditor.supportsCommentAction());
        theViewAction.setEnabled(aEditor.supportsViewAction());
        theIntelligentLayoutCheckbox.setEnabled(aEditor.supportsIntelligentLayout());
        theDisplayCommentsAction.setEnabled(aEditor.supportsCommentAction());
        theDisplayGridAction.setEnabled(aEditor.supportsGrid());
        theDisplayLevelMenu.setEnabled(aEditor.supportsDisplayLevel());
        subjectAreas.setEnabled(aEditor.supportsSubjectAreas());
        theDisplayOrderMenu.setEnabled(aEditor.supportsAttributeOrder());

        theExportMenu.removeAll();
        aEditor.initExportEntries(this, theExportMenu);
        theExportMenu.add(new DefaultMenuItem(theExportOpenXavaAction));
        UIInitializer.getInstance().initialize(theExportMenu);

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
                new ActionEventProcessor() {

                    @Override
                    public void processActionEvent(ActionEvent aEvent) {
                        new SaveToFileCommand()
                                .executeSaveFileAs();
                    }

                }, this, ERDesignerBundle.SAVEMODELAS);

        DefaultAction theSaveToRepository = new DefaultAction(
                new SaveToRepositoryCommand(), this,
                ERDesignerBundle.SAVEMODELTODB);

        theRelationAction = new DefaultAction(
                new ActionEventProcessor() {

                    @Override
                    public void processActionEvent(ActionEvent e) {
                        commandSetTool(ToolEnum.RELATION);
                        if (!relationButton.isSelected()) {
                            relationButton.setSelected(true);
                        }
                    }

                }, this, ERDesignerBundle.RELATION);

        DefaultAction theNewAction = new DefaultAction(
                new ActionEventProcessor() {

                    @Override
                    public void processActionEvent(ActionEvent e) {
                        commandNew();
                    }
                }, this, ERDesignerBundle.NEWMODEL);

        DefaultAction theLruAction = new DefaultAction(this,
                ERDesignerBundle.RECENTLYUSEDFILES);

        DefaultAction theLoadAction = new DefaultAction(
                new OpenFromFileCommand(), this, ERDesignerBundle.LOADMODEL);

        theHandAction = new DefaultAction(
                new ActionEventProcessor() {

                    @Override
                    public void processActionEvent(ActionEvent e) {
                        commandSetTool(ToolEnum.HAND);

                        if (!handButton.isSelected()) {
                            handButton.setSelected(true);
                        }
                    }

                }, this, ERDesignerBundle.HAND);

        theCommentAction = new DefaultAction(
                new ActionEventProcessor() {

                    @Override
                    public void processActionEvent(ActionEvent e) {
                        commandSetTool(ToolEnum.COMMENT);
                        if (!commentButton.isSelected()) {
                            commentButton.setSelected(true);
                        }
                    }

                }, this, ERDesignerBundle.COMMENT);

        theEntityAction = new DefaultAction(
                new ActionEventProcessor() {

                    @Override
                    public void processActionEvent(ActionEvent e) {
                        commandSetTool(ToolEnum.ENTITY);
                        if (!entityButton.isSelected()) {
                            entityButton.setSelected(true);
                        }
                    }

                }, this, ERDesignerBundle.ENTITY);

        theViewAction = new DefaultAction(
                new ActionEventProcessor() {

                    @Override
                    public void processActionEvent(ActionEvent e) {
                        commandSetTool(ToolEnum.VIEW);
                        if (!viewButton.isSelected()) {
                            viewButton.setSelected(true);
                        }
                    }

                }, this, ERDesignerBundle.VIEWTOOL);

        DefaultAction theExportAction = new DefaultAction(this,
                ERDesignerBundle.EXPORT);

        DefaultAction theExitAction = new DefaultAction(
                new ActionEventProcessor() {

                    @Override
                    public void processActionEvent(ActionEvent e) {
                        worldConnector.exitApplication();
                    }

                }, this, ERDesignerBundle.EXITPROGRAM);

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
                new ActionEventProcessor() {

                    @Override
                    public void processActionEvent(ActionEvent aEvent) {
                        editor.commandSetZoom((ZoomInfo) ((JComboBox) aEvent
                                .getSource()).getSelectedItem());
                    }
                }, this, ERDesignerBundle.ZOOM);

        theZoomInAction = new DefaultAction(
                new ActionEventProcessor() {

                    @Override
                    public void processActionEvent(ActionEvent e) {
                        int theIndex = zoomBox.getSelectedIndex();
                        if (theIndex > 0) {
                            theIndex--;
                            zoomBox.setSelectedIndex(theIndex);
                            editor.commandSetZoom((ZoomInfo) zoomBox.getSelectedItem());
                        }
                    }

                }, this, ERDesignerBundle.ZOOMIN);

        theZoomOutAction = new DefaultAction(
                new ActionEventProcessor() {

                    @Override
                    public void processActionEvent(ActionEvent e) {
                        int theIndex = zoomBox.getSelectedIndex();
                        if (theIndex < zoomBox.getItemCount() - 1) {
                            theIndex++;
                            zoomBox.setSelectedIndex(theIndex);
                            editor.commandSetZoom((ZoomInfo) zoomBox.getSelectedItem());
                        }
                    }

                }, this, ERDesignerBundle.ZOOMOUT);

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
                new ActionEventProcessor() {

                    @Override
                    public void processActionEvent(ActionEvent aEvent) {
                        commandShowHelp();
                    }

                }, this, ERDesignerBundle.HELP);

        theExportOpenXavaAction = new DefaultAction(
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

        theExportMenu = new DefaultMenu(theExportAction);
        theFileMenu.add(theExportMenu);

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
                new ActionEventProcessor() {

                    @Override
                    public void processActionEvent(ActionEvent e) {
                        setEditor2DDiagram();
                    }

                }, this, ERDesignerBundle.VIEWMODE2DDIAGRAM);
        DefaultAction theViewMode2DInteractiveAction = new DefaultAction(
                new ActionEventProcessor() {

                    @Override
                    public void processActionEvent(ActionEvent e) {
                        setEditor2DInteractive();
                    }

                }, this, ERDesignerBundle.VIEWMODE2DINTERACTIVE);
        DefaultAction theViewMode3DInteractiveAction = new DefaultAction(
                new ActionEventProcessor() {

                    @Override
                    public void processActionEvent(ActionEvent e) {
                        setEditor3DInteractive();
                    }

                }, this, ERDesignerBundle.VIEWMODE3DINTERACTIVE);


        DefaultCheckboxMenuItem theViewMode2DDiagram = new DefaultCheckboxMenuItem(
                theViewMode2DDiagramAction);
        DefaultCheckboxMenuItem theViewMode2DInteractive = new DefaultCheckboxMenuItem(
                theViewMode2DInteractiveAction);
        DefaultCheckboxMenuItem theViewMode3DInteractive = new DefaultCheckboxMenuItem(
                theViewMode3DInteractiveAction);
        theViewModeMenu.add(theViewMode2DDiagram);
        theViewModeMenu.add(theViewMode2DInteractive);
        theViewModeMenu.add(theViewMode3DInteractive);

        ButtonGroup theDisplayModeGroup = new ButtonGroup();
        theDisplayModeGroup.add(theViewMode2DDiagram);
        theDisplayModeGroup.add(theViewMode2DInteractive);
        theDisplayModeGroup.add(theViewMode3DInteractive);

        theViewMode2DInteractive.setSelected(true);

        theViewMenu.add(theViewModeMenu);
        UIInitializer.getInstance().initialize(theViewModeMenu);

        theDisplayCommentsAction = new DefaultAction(
                new ActionEventProcessor() {

                    @Override
                    public void processActionEvent(ActionEvent e) {
                        DefaultCheckboxMenuItem theItem = (DefaultCheckboxMenuItem) e
                                .getSource();
                        editor.commandSetDisplayCommentsState(theItem.isSelected());
                    }

                }, this, ERDesignerBundle.DISPLAYCOMMENTS);

        displayCommentsMenuItem = new DefaultCheckboxMenuItem(
                theDisplayCommentsAction);
        displayCommentsMenuItem.setSelected(false);
        theViewMenu.add(displayCommentsMenuItem);

        theDisplayGridAction = new DefaultAction(
                new ActionEventProcessor() {

                    @Override
                    public void processActionEvent(ActionEvent e) {
                        DefaultCheckboxMenuItem theItem = (DefaultCheckboxMenuItem) e
                                .getSource();
                        editor.commandSetDisplayGridState(theItem.isSelected());
                    }

                }, this, ERDesignerBundle.DISPLAYGRID);

        displayGridMenuItem = new DefaultCheckboxMenuItem(theDisplayGridAction);
        theViewMenu.add(displayGridMenuItem);

        theDisplayLevelMenu = new DefaultMenu(this,
                ERDesignerBundle.DISPLAYLEVEL);
        theViewMenu.add(theDisplayLevelMenu);

        DefaultAction theDisplayAllAction = new DefaultAction(
                new ActionEventProcessor() {

                    @Override
                    public void processActionEvent(ActionEvent e) {
                        editor.commandSetDisplayLevel(DisplayLevel.ALL);
                    }

                }, this, ERDesignerBundle.DISPLAYALL);

        DefaultAction theDisplayPKOnlyAction = new DefaultAction(
                new ActionEventProcessor() {

                    @Override
                    public void processActionEvent(ActionEvent e) {
                        editor.commandSetDisplayLevel(DisplayLevel.PRIMARYKEYONLY);
                    }

                }, this, ERDesignerBundle.DISPLAYPRIMARYKEY);

        DefaultAction theDisplayPKAndFK = new DefaultAction(
                new ActionEventProcessor() {

                    @Override
                    public void processActionEvent(ActionEvent e) {
                        editor.commandSetDisplayLevel(DisplayLevel.PRIMARYKEYSANDFOREIGNKEYS);
                    }

                }, this, ERDesignerBundle.DISPLAYPRIMARYKEYANDFOREIGNKEY);

        displayAllMenuItem = new DefaultRadioButtonMenuItem(theDisplayAllAction);
        DefaultRadioButtonMenuItem thePKOnlyItem = new DefaultRadioButtonMenuItem(
                theDisplayPKOnlyAction);
        DefaultRadioButtonMenuItem thePKAndFKItem = new DefaultRadioButtonMenuItem(
                theDisplayPKAndFK);

        ButtonGroup theDisplayLevelGroup = new ButtonGroup();
        theDisplayLevelGroup.add(displayAllMenuItem);
        theDisplayLevelGroup.add(thePKOnlyItem);
        theDisplayLevelGroup.add(thePKAndFKItem);

        theDisplayLevelMenu.add(displayAllMenuItem);
        theDisplayLevelMenu.add(thePKOnlyItem);
        theDisplayLevelMenu.add(thePKAndFKItem);

        UIInitializer.getInstance().initialize(theDisplayLevelMenu);

        theDisplayOrderMenu = new DefaultMenu(this,
                ERDesignerBundle.DISPLAYORDER);
        theViewMenu.add(theDisplayOrderMenu);

        DefaultAction theDisplayNaturalOrderAction = new DefaultAction(
                new ActionEventProcessor() {

                    @Override
                    public void processActionEvent(ActionEvent e) {
                        editor.commandSetDisplayOrder(DisplayOrder.NATURAL);
                    }

                }, this, ERDesignerBundle.DISPLAYNATURALORDER);

        DefaultAction theDisplayAscendingOrderAction = new DefaultAction(
                new ActionEventProcessor() {

                    @Override
                    public void processActionEvent(ActionEvent e) {
                        editor.commandSetDisplayOrder(DisplayOrder.ASCENDING);
                    }

                }, this, ERDesignerBundle.DISPLAYASCENDING);

        DefaultAction theDisplayDescendingOrderAction = new DefaultAction(
                new ActionEventProcessor() {

                    @Override
                    public void processActionEvent(ActionEvent e) {
                        editor.commandSetDisplayOrder(DisplayOrder.DESCENDING);
                    }

                }, this, ERDesignerBundle.DISPLAYDESCENDING);

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

        theIntelligentLayoutCheckbox = new DefaultCheckBox(
                ERDesignerBundle.INTELLIGENTLAYOUT);
        theIntelligentLayoutCheckbox.setSelected(ApplicationPreferences.getInstance()
                .isIntelligentLayout());
        theIntelligentLayoutCheckbox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                editor.setIntelligentLayoutEnabled(theIntelligentLayoutCheckbox.isSelected());
            }
        });

        theToolBar.addSeparator();
        theToolBar.add(theIntelligentLayoutCheckbox);

        worldConnector.initTitle();

        updateRecentlyUsedMenuEntries();

        setupViewForNothing();
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
        for (SubjectArea theArea : model.getSubjectAreas()) {
            editor.commandShowSubjectArea(theArea);
        }
    }

    /**
     * Hide all subject areas.
     */
    protected void commandHideAllSubjectAreas() {
        for (SubjectArea theArea : model.getSubjectAreas()) {
            editor.commandHideSubjectArea(theArea);
        }
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

                final File theJRXMLFile = theEntry.getKey();
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
                new ActionEventProcessor() {

                    @Override
                    public void processActionEvent(ActionEvent aEvent) {
                        commandShowAllSubjectAreas();
                    }

                }, this, ERDesignerBundle.SHOWALL)));
        subjectAreas.add(new DefaultMenuItem(new DefaultAction(
                new ActionEventProcessor() {

                    @Override
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
                            editor.commandShowSubjectArea(theFinalArea);
                        } else {
                            editor.commandHideSubjectArea(theFinalArea);
                        }
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
            theItem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    new OpenFromFileCommand()
                            .execute(theFile);
                }
            });

            lruMenu.add(theItem);
            UIInitializer.getInstance().initialize(theItem);
        }

        for (final ConnectionDescriptor theConnectionInfo : ApplicationPreferences
                .getInstance().getRecentlyUsedConnections()) {
            JMenuItem theItem1 = new JMenuItem(theConnectionInfo.toString());
            theItem1.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    new DBConnectionCommand()
                            .execute(theConnectionInfo);
                }
            });

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
            File theFile = ApplicationPreferences.getInstance()
                    .getOnlineHelpPDFFile();
            Desktop.getDesktop().open(theFile);
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
            editor.setModel(model);

            ModelChecker theChecker = new ModelChecker();
            theChecker.check(aModel);

        } finally {

            zoomBox.setSelectedItem(ZOOMSCALE_HUNDREDPERCENT);
            editor.commandSetZoom(ZOOMSCALE_HUNDREDPERCENT);
            commandSetTool(ToolEnum.HAND);

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
}
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.swing.JRViewer;

import org.jgraph.event.GraphModelEvent;
import org.jgraph.event.GraphModelListener;
import org.jgraph.event.GraphLayoutCacheEvent.GraphLayoutCacheChange;
import org.jgraph.graph.CellView;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;
import org.jgraph.graph.VertexView;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.dialect.Dialect;
import de.erdesignerng.dialect.DialectFactory;
import de.erdesignerng.dialect.GenericConnectionProvider;
import de.erdesignerng.dialect.ReverseEngineeringNotifier;
import de.erdesignerng.dialect.ReverseEngineeringOptions;
import de.erdesignerng.dialect.ReverseEngineeringStrategy;
import de.erdesignerng.dialect.SQLGenerator;
import de.erdesignerng.dialect.Statement;
import de.erdesignerng.dialect.StatementList;
import de.erdesignerng.io.GenericFileFilter;
import de.erdesignerng.io.ModelFileFilter;
import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.Comment;
import de.erdesignerng.model.Index;
import de.erdesignerng.model.IndexExpression;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.ModelBasedConnectionProvider;
import de.erdesignerng.model.ModelIOUtilities;
import de.erdesignerng.model.ModelItem;
import de.erdesignerng.model.ModelUtilities;
import de.erdesignerng.model.Relation;
import de.erdesignerng.model.SubjectArea;
import de.erdesignerng.model.Table;
import de.erdesignerng.model.View;
import de.erdesignerng.model.serializer.repository.DictionaryModelSerializer;
import de.erdesignerng.model.serializer.repository.RepositoryEntryDesciptor;
import de.erdesignerng.model.serializer.repository.entities.RepositoryEntity;
import de.erdesignerng.modificationtracker.HistoryModificationTracker;
import de.erdesignerng.modificationtracker.VetoException;
import de.erdesignerng.util.ApplicationPreferences;
import de.erdesignerng.util.ConnectionDescriptor;
import de.erdesignerng.util.JasperUtils;
import de.erdesignerng.util.SQLUtils;
import de.erdesignerng.visual.DisplayLevel;
import de.erdesignerng.visual.DisplayOrder;
import de.erdesignerng.visual.ERDesignerGraph;
import de.erdesignerng.visual.ExportType;
import de.erdesignerng.visual.LongRunningTask;
import de.erdesignerng.visual.MessagesHelper;
import de.erdesignerng.visual.cells.CommentCell;
import de.erdesignerng.visual.cells.HideableCell;
import de.erdesignerng.visual.cells.ModelCell;
import de.erdesignerng.visual.cells.RelationEdge;
import de.erdesignerng.visual.cells.SubjectAreaCell;
import de.erdesignerng.visual.cells.TableCell;
import de.erdesignerng.visual.cells.ViewCell;
import de.erdesignerng.visual.cells.views.CellViewFactory;
import de.erdesignerng.visual.cells.views.TableCellView;
import de.erdesignerng.visual.editor.DialogConstants;
import de.erdesignerng.visual.editor.classpath.ClasspathEditor;
import de.erdesignerng.visual.editor.comment.CommentEditor;
import de.erdesignerng.visual.editor.completecompare.CompleteCompareEditor;
import de.erdesignerng.visual.editor.connection.DatabaseConnectionEditor;
import de.erdesignerng.visual.editor.connection.RepositoryConnectionEditor;
import de.erdesignerng.visual.editor.convertmodel.ConvertModelEditor;
import de.erdesignerng.visual.editor.domain.DomainEditor;
import de.erdesignerng.visual.editor.openxavaexport.OpenXavaExportEditor;
import de.erdesignerng.visual.editor.preferences.PreferencesEditor;
import de.erdesignerng.visual.editor.relation.RelationEditor;
import de.erdesignerng.visual.editor.repository.LoadFromRepositoryEditor;
import de.erdesignerng.visual.editor.repository.MigrationScriptEditor;
import de.erdesignerng.visual.editor.repository.SaveToRepositoryEditor;
import de.erdesignerng.visual.editor.reverseengineer.ReverseEngineerEditor;
import de.erdesignerng.visual.editor.reverseengineer.TablesSelectEditor;
import de.erdesignerng.visual.editor.sql.SQLEditor;
import de.erdesignerng.visual.editor.table.TableEditor;
import de.erdesignerng.visual.editor.view.ViewEditor;
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
import de.mogwai.common.client.looks.components.DefaultDialog;
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

    private DefaultAction classpathAction;

    private File currentEditingFile;

    private RepositoryEntryDesciptor currentRepositoryEntry;

    private DefaultAction dbConnectionAction;

    private DefaultAction repositoryConnectionAction;

    private DefaultAction domainsAction;

    private DefaultAction entityAction;

    private DefaultAction viewAction;

    private JToggleButton entityButton;

    private DefaultAction exitAction;

    private DefaultAction exportAction;

    private DefaultAction exportSVGAction;

    volatile ERDesignerGraph graph;

    private GraphModel graphModel;

    private DefaultAction handAction;

    private JToggleButton handButton;

    private DefaultAction commentAction;

    private JToggleButton commentButton;

    private JToggleButton viewButton;

    private GraphLayoutCache layoutCache;

    private DefaultAction loadAction;

    private DefaultAction lruAction;

    private DefaultMenu lruMenu;

    private DefaultMenu storedConnections;

    private DefaultMenu subjectAreas;

    volatile Model model;

    private DefaultAction newAction;

    private ApplicationPreferences preferences;

    private DefaultAction relationAction;

    private JToggleButton relationButton;

    private DefaultAction reverseEngineerAction;

    private DefaultAction completeCompareAction;

    private DefaultAction convertModelAction;

    private DefaultAction saveAsAction;

    private DefaultAction saveAction;

    private DefaultAction saveToRepository;

    private DefaultScrollPane scrollPane = new DefaultScrollPane();

    private ERDesignerWorldConnector worldConnector;

    private DefaultAction zoomAction;

    private DefaultComboBox zoomBox = new DefaultComboBox();

    private DefaultAction zoomInAction;

    private DefaultAction zoomOutAction;

    private DefaultAction preferencesAction;

    private DefaultMenu documentationMenu;

    private DefaultAction generateSQL;

    private DefaultAction generateChangelog;

    private DefaultAction displayCommentsAction;

    private DefaultCheckboxMenuItem displayCommentsMenuItem;

    private DefaultAction displayGridAction;

    private DefaultCheckboxMenuItem displayGridMenuItem;

    private DefaultRadioButtonMenuItem displayAllMenuItem;

    private DefaultAction displayAllAction;

    private DefaultAction displayPKOnlyAction;

    private DefaultAction displayPKAndFK;

    private DefaultRadioButtonMenuItem displayNaturalOrderMenuItem;

    private DefaultAction displayNaturalOrderAction;

    private DefaultAction displayAscendingOrderAction;

    private DefaultAction displayDescendingOrderAction;

    private DefaultAction createMigrationScriptAction;

    private DefaultAction helpAction;

    private DefaultMenu repositoryUtilsMenu;

    private DefaultAction exportOpenXavaAction;

    private static final ZoomInfo ZOOMSCALE_HUNDREDPERCENT = new ZoomInfo("100%", 1);

    private ERDesignerGraphLayout layout;

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

        reverseEngineerAction = new DefaultAction(new ActionEventProcessor() {

            public void processActionEvent(ActionEvent aEvent) {
                commandReverseEngineer();
            }

        }, this, ERDesignerBundle.REVERSEENGINEER);

        preferencesAction = new DefaultAction(new ActionEventProcessor() {

            public void processActionEvent(ActionEvent aEvent) {
                commandPreferences();
            }

        }, this, ERDesignerBundle.PREFERENCES);

        saveAction = new DefaultAction(new ActionEventProcessor() {

            public void processActionEvent(ActionEvent aEvent) {
                commandSaveFile();
            }

        }, this, ERDesignerBundle.SAVEMODEL);
        saveAction.putValue(DefaultAction.HOTKEY_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));

        saveAsAction = new DefaultAction(new ActionEventProcessor() {

            public void processActionEvent(ActionEvent aEvent) {
                commandSaveFileAs();
            }

        }, this, ERDesignerBundle.SAVEMODELAS);

        saveToRepository = new DefaultAction(new ActionEventProcessor() {

            public void processActionEvent(ActionEvent aEvent) {
                commandSaveToRepository();
            }

        }, this, ERDesignerBundle.SAVEMODELTODB);

        relationAction = new DefaultAction(new ActionEventProcessor() {

            public void processActionEvent(ActionEvent e) {
                commandSetTool(ToolEnum.RELATION);
            }

        }, this, ERDesignerBundle.RELATION);

        newAction = new DefaultAction(new ActionEventProcessor() {

            public void processActionEvent(ActionEvent e) {
                commandNew();
            }
        }, this, ERDesignerBundle.NEWMODEL);

        lruAction = new DefaultAction(this, ERDesignerBundle.RECENTLYUSEDFILES);

        loadAction = new DefaultAction(new ActionEventProcessor() {

            public void processActionEvent(ActionEvent aEvent) {
                commandOpenFromFile();
            }

        }, this, ERDesignerBundle.LOADMODEL);

        handAction = new DefaultAction(new ActionEventProcessor() {

            public void processActionEvent(ActionEvent e) {
                commandSetTool(ToolEnum.HAND);
            }

        }, this, ERDesignerBundle.HAND);

        commentAction = new DefaultAction(new ActionEventProcessor() {

            public void processActionEvent(ActionEvent e) {
                commandSetTool(ToolEnum.COMMENT);
            }

        }, this, ERDesignerBundle.COMMENT);

        exportSVGAction = new DefaultAction(this, ERDesignerBundle.ASSVG);

        entityAction = new DefaultAction(new ActionEventProcessor() {

            public void processActionEvent(ActionEvent e) {
                commandSetTool(ToolEnum.ENTITY);
            }

        }, this, ERDesignerBundle.ENTITY);

        viewAction = new DefaultAction(new ActionEventProcessor() {

            public void processActionEvent(ActionEvent e) {
                commandSetTool(ToolEnum.VIEW);
            }

        }, this, ERDesignerBundle.VIEWTOOL);

        exportAction = new DefaultAction(this, ERDesignerBundle.EXPORT);

        exitAction = new DefaultAction(new ActionEventProcessor() {

            public void processActionEvent(ActionEvent e) {
                worldConnector.exitApplication();
            }

        }, this, ERDesignerBundle.EXITPROGRAM);

        classpathAction = new DefaultAction(new ActionEventProcessor() {

            public void processActionEvent(ActionEvent e) {
                commandClasspath();
            }

        }, this, ERDesignerBundle.CLASSPATH);

        dbConnectionAction = new DefaultAction(new ActionEventProcessor() {

            public void processActionEvent(ActionEvent e) {
                commandDBConnection();
            }

        }, this, ERDesignerBundle.DBCONNECTION);

        repositoryConnectionAction = new DefaultAction(new ActionEventProcessor() {

            public void processActionEvent(ActionEvent e) {
                commandRepositoryConnection();
            }

        }, this, ERDesignerBundle.REPOSITORYCONNECTION);

        domainsAction = new DefaultAction(new ActionEventProcessor() {

            public void processActionEvent(ActionEvent e) {
                commandEditDomains();
            }

        }, this, ERDesignerBundle.DOMAINEDITOR);

        zoomAction = new DefaultAction(new ActionEventProcessor() {

            public void processActionEvent(ActionEvent aEvent) {
                commandSetZoom((ZoomInfo) ((JComboBox) aEvent.getSource()).getSelectedItem());
            }
        }, this, ERDesignerBundle.ZOOM);

        zoomInAction = new DefaultAction(new ActionEventProcessor() {

            public void processActionEvent(ActionEvent e) {
                commandZoomIn();
            }

        }, this, ERDesignerBundle.ZOOMIN);

        zoomOutAction = new DefaultAction(new ActionEventProcessor() {

            public void processActionEvent(ActionEvent e) {
                commandZoomOut();
            }

        }, this, ERDesignerBundle.ZOOMOUT);

        generateSQL = new DefaultAction(new ActionEventProcessor() {

            public void processActionEvent(ActionEvent e) {
                commandGenerateSQL();
            }

        }, this, ERDesignerBundle.GENERATECREATEDBDDL);

        generateChangelog = new DefaultAction(new ActionEventProcessor() {

            public void processActionEvent(ActionEvent e) {
                commandGenerateChangelogSQL();
            }

        }, this, ERDesignerBundle.GENERATECHANGELOG);

        completeCompareAction = new DefaultAction(new ActionEventProcessor() {

            public void processActionEvent(ActionEvent e) {
                commandCompleteCompare();
            }

        }, this, ERDesignerBundle.COMPLETECOMPARE);

        convertModelAction = new DefaultAction(new ActionEventProcessor() {

            public void processActionEvent(ActionEvent e) {
                commandConvertModel();
            }

        }, this, ERDesignerBundle.CONVERTMODEL);

        createMigrationScriptAction = new DefaultAction(new ActionEventProcessor() {

            public void processActionEvent(ActionEvent aEvent) {
                commandCreateMigrationScript();
            }

        }, this, ERDesignerBundle.CREATEMIGRATIONSCRIPT);

        helpAction = new DefaultAction(new ActionEventProcessor() {

            public void processActionEvent(ActionEvent aEvent) {
                commandShowHelp();
            }

        }, this, ERDesignerBundle.HELP);

        exportOpenXavaAction = new DefaultAction(new ActionEventProcessor() {

            public void processActionEvent(ActionEvent aEvent) {
                commandExportToOpenXava();
            }

        }, this, ERDesignerBundle.OPENXAVAEXPORT);

        lruMenu = new DefaultMenu(lruAction);

        DefaultAction theStoredConnectionsAction = new DefaultAction(this, ERDesignerBundle.STOREDDBCONNECTION);
        storedConnections = new DefaultMenu(theStoredConnectionsAction);

        ERDesignerToolbarEntry theFileMenu = new ERDesignerToolbarEntry(ERDesignerBundle.FILE);
        if (worldConnector.supportsPreferences()) {
            theFileMenu.add(new DefaultMenuItem(preferencesAction));
            theFileMenu.addSeparator();
        }

        theFileMenu.add(new DefaultMenuItem(newAction));
        theFileMenu.addSeparator();
        DefaultMenuItem theSaveItem = new DefaultMenuItem(saveAction);
        theFileMenu.add(theSaveItem);
        KeyStroke theStroke = (KeyStroke) saveAction.getValue(DefaultAction.HOTKEY_KEY);
        if (theStroke != null) {
            theSaveItem.setAccelerator(theStroke);
            scrollPane.registerKeyboardAction(saveAction, theStroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
        }

        theFileMenu.add(new DefaultMenuItem(saveAsAction));
        theFileMenu.add(new DefaultMenuItem(loadAction));

        if (worldConnector.supportsRepositories()) {
            theFileMenu.addSeparator();
            theFileMenu.add(new DefaultMenuItem(repositoryConnectionAction));
            theFileMenu.add(new DefaultMenuItem(saveToRepository));

            DefaultMenuItem theLoadFromDBMenu = new DefaultMenuItem(new DefaultAction(new ActionEventProcessor() {

                public void processActionEvent(ActionEvent e) {
                    commandOpenFromRepository();
                }

            }, this, ERDesignerBundle.LOADMODELFROMDB));

            theFileMenu.add(theLoadFromDBMenu);

            repositoryUtilsMenu = new DefaultMenu(this, ERDesignerBundle.REPOSITORYUTILS);
            repositoryUtilsMenu.add(new DefaultMenuItem(createMigrationScriptAction));

            UIInitializer.getInstance().initialize(repositoryUtilsMenu);

            theFileMenu.add(repositoryUtilsMenu);

            theFileMenu.addSeparator();
        }

        DefaultMenu theExportMenu = new DefaultMenu(exportAction);

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

        DefaultMenu theSVGExportMenu = new DefaultMenu(exportSVGAction);

        theExportMenu.add(theSVGExportMenu);
        addExportEntries(theSVGExportMenu, new SVGExporter());

        theExportMenu.add(new DefaultMenuItem(exportOpenXavaAction));

        UIInitializer.getInstance().initialize(theExportMenu);

        theFileMenu.add(theExportMenu);

        theFileMenu.addSeparator();
        theFileMenu.add(lruMenu);

        if (worldConnector.supportsExitApplication()) {
            theFileMenu.addSeparator();
            theFileMenu.add(new DefaultMenuItem(exitAction));
        }

        ERDesignerToolbarEntry theDBMenu = new ERDesignerToolbarEntry(ERDesignerBundle.DATABASE);

        boolean addSeparator = false;
        if (worldConnector.supportsClasspathEditor()) {
            theDBMenu.add(new DefaultMenuItem(classpathAction));
            addSeparator = true;
        }

        if (worldConnector.supportsConnectionEditor()) {
            theDBMenu.add(new DefaultMenuItem(dbConnectionAction));
            theDBMenu.add(storedConnections);
            addSeparator = true;
        }

        if (addSeparator) {
            theDBMenu.addSeparator();
        }

        theDBMenu.add(new DefaultMenuItem(domainsAction));
        theDBMenu.addSeparator();

        theDBMenu.add(new DefaultMenuItem(reverseEngineerAction));
        theDBMenu.addSeparator();
        theDBMenu.add(new DefaultMenuItem(generateSQL));
        theDBMenu.addSeparator();
        theDBMenu.add(new DefaultMenuItem(generateChangelog));
        theDBMenu.addSeparator();
        theDBMenu.add(new DefaultMenuItem(completeCompareAction));
        theDBMenu.addSeparator();
        theDBMenu.add(new DefaultMenuItem(convertModelAction));

        if (worldConnector.supportsReporting()) {
            documentationMenu = new DefaultMenu(this, ERDesignerBundle.CREATEDBDOCUMENTATION);
            theDBMenu.addSeparator();
            theDBMenu.add(documentationMenu);

            updateDocumentationMenu();
        }

        ERDesignerToolbarEntry theViewMenu = new ERDesignerToolbarEntry(ERDesignerBundle.VIEW);

        displayCommentsAction = new DefaultAction(new ActionEventProcessor() {

            public void processActionEvent(ActionEvent e) {
                DefaultCheckboxMenuItem theItem = (DefaultCheckboxMenuItem) e.getSource();
                commandSetDisplayCommentsState(theItem.isSelected());
            }

        }, this, ERDesignerBundle.DISPLAYCOMMENTS);

        displayCommentsMenuItem = new DefaultCheckboxMenuItem(displayCommentsAction);
        displayCommentsMenuItem.setSelected(true);
        theViewMenu.add(displayCommentsMenuItem);

        displayGridAction = new DefaultAction(new ActionEventProcessor() {

            public void processActionEvent(ActionEvent e) {
                DefaultCheckboxMenuItem theItem = (DefaultCheckboxMenuItem) e.getSource();
                commandSetDisplayGridState(theItem.isSelected());
            }

        }, this, ERDesignerBundle.DISPLAYGRID);

        displayGridMenuItem = new DefaultCheckboxMenuItem(displayGridAction);
        theViewMenu.add(displayGridMenuItem);

        DefaultMenu theDisplayLevelMenu = new DefaultMenu(this, ERDesignerBundle.DISPLAYLEVEL);
        theViewMenu.add(theDisplayLevelMenu);

        displayAllAction = new DefaultAction(new ActionEventProcessor() {

            public void processActionEvent(ActionEvent e) {
                commandSetDisplayLevel(DisplayLevel.ALL);
            }

        }, this, ERDesignerBundle.DISPLAYALL);

        displayPKOnlyAction = new DefaultAction(new ActionEventProcessor() {

            public void processActionEvent(ActionEvent e) {
                commandSetDisplayLevel(DisplayLevel.PRIMARYKEYONLY);
            }

        }, this, ERDesignerBundle.DISPLAYPRIMARYKEY);

        displayPKAndFK = new DefaultAction(new ActionEventProcessor() {

            public void processActionEvent(ActionEvent e) {
                commandSetDisplayLevel(DisplayLevel.PRIMARYKEYSANDFOREIGNKEYS);
            }

        }, this, ERDesignerBundle.DISPLAYPRIMARYKEYANDFOREIGNKEY);

        displayAllMenuItem = new DefaultRadioButtonMenuItem(displayAllAction);
        DefaultRadioButtonMenuItem thePKOnlyItem = new DefaultRadioButtonMenuItem(displayPKOnlyAction);
        DefaultRadioButtonMenuItem thePKAndFKItem = new DefaultRadioButtonMenuItem(displayPKAndFK);

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

        displayNaturalOrderAction = new DefaultAction(new ActionEventProcessor() {

            public void processActionEvent(ActionEvent e) {
                commandSetDisplayOrder(DisplayOrder.NATURAL);
            }

        }, this, ERDesignerBundle.DISPLAYNATURALORDER);

        displayAscendingOrderAction = new DefaultAction(new ActionEventProcessor() {

            public void processActionEvent(ActionEvent e) {
                commandSetDisplayOrder(DisplayOrder.ASCENDING);
            }

        }, this, ERDesignerBundle.DISPLAYASCENDING);

        displayDescendingOrderAction = new DefaultAction(new ActionEventProcessor() {

            public void processActionEvent(ActionEvent e) {
                commandSetDisplayOrder(DisplayOrder.DESCENDING);
            }

        }, this, ERDesignerBundle.DISPLAYDESCENDING);

        displayNaturalOrderMenuItem = new DefaultRadioButtonMenuItem(displayNaturalOrderAction);
        DefaultRadioButtonMenuItem theAscendingItem = new DefaultRadioButtonMenuItem(displayAscendingOrderAction);
        DefaultRadioButtonMenuItem theDescendingItem = new DefaultRadioButtonMenuItem(displayDescendingOrderAction);

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

        theViewMenu.add(new DefaultMenuItem(zoomInAction));
        theViewMenu.add(new DefaultMenuItem(zoomOutAction));

        if (worldConnector.supportsHelp()) {
            theViewMenu.addSeparator();
            theViewMenu.add(new DefaultMenuItem(helpAction));
        }

        DefaultComboBoxModel theZoomModel = new DefaultComboBoxModel();
        theZoomModel.addElement(ZOOMSCALE_HUNDREDPERCENT);
        for (int i = 9; i > 0; i--) {
            theZoomModel.addElement(new ZoomInfo(i * 10 + " %", ((double) i) / (double) 10));
        }
        zoomBox.setPreferredSize(new Dimension(100, 21));
        zoomBox.setMaximumSize(new Dimension(100, 21));
        zoomBox.setAction(zoomAction);
        zoomBox.setModel(theZoomModel);

        DefaultToolbar theToolBar = worldConnector.getToolBar();

        theToolBar.add(theFileMenu);
        theToolBar.add(theDBMenu);
        theToolBar.add(theViewMenu);
        theToolBar.addSeparator();

        theToolBar.add(newAction);
        theToolBar.addSeparator();
        theToolBar.add(loadAction);
        theToolBar.add(saveAsAction);
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

    protected void commandAddTableAndOptionalConnector(Point2D aPoint, TableCell aExportingCell) {

        if (model.getDialect() == null) {
            MessagesHelper.displayErrorMessage(graph, getResourceHelper().getText(
                    ERDesignerBundle.PLEASEDEFINEADATABASECONNECTIONFIRST));
            return;
        }

        Table theTable = new Table();
        TableEditor theTableEditor = new TableEditor(model, scrollPane);
        theTableEditor.initializeFor(theTable);
        if (theTableEditor.showModal() == DialogConstants.MODAL_RESULT_OK) {
            try {

                TableCell theCell = new TableCell(theTable);
                layoutCache.insert(theCell);
                
                //TODO: [rarf] Handle addition of new relation here

                try {
                    theTableEditor.applyValues();
                } catch (VetoException e) {
                    worldConnector.notifyAboutException(e);
                }

                theCell.transferPropertiesToAttributes(theTable);

                Object theTargetCell = graph.getFirstCellForLocation(aPoint.getX(), aPoint.getY());
                if (theTargetCell instanceof SubjectAreaCell) {
                    SubjectAreaCell theSACell = (SubjectAreaCell) theTargetCell;
                    SubjectArea theArea = (SubjectArea) theSACell.getUserObject();
                    theArea.getTables().add(theTable);

                    theSACell.add(theCell);
                }

                GraphConstants.setBounds(theCell.getAttributes(), new Rectangle2D.Double(aPoint.getX(), aPoint.getY(),
                        -1, -1));

                layoutCache.insert(theCell);

                theCell.transferAttributesToProperties(theCell.getAttributes());

            } catch (Exception e) {
                worldConnector.notifyAboutException(e);
            }

            graph.doLayout();
        }
    }

    protected void commandAddView(Point2D aPoint) {

        if (model.getDialect() == null) {
            MessagesHelper.displayErrorMessage(graph, getResourceHelper().getText(
                    ERDesignerBundle.PLEASEDEFINEADATABASECONNECTIONFIRST));
            return;
        }

        View theView = new View();
        ViewEditor theEditor = new ViewEditor(model, scrollPane);
        theEditor.initializeFor(theView);
        if (theEditor.showModal() == DialogConstants.MODAL_RESULT_OK) {
            try {

                try {
                    theEditor.applyValues();
                } catch (VetoException e) {
                    worldConnector.notifyAboutException(e);
                }

                ViewCell theCell = new ViewCell(theView);
                theCell.transferPropertiesToAttributes(theView);

                Object theTargetCell = graph.getFirstCellForLocation(aPoint.getX(), aPoint.getY());
                if (theTargetCell instanceof SubjectAreaCell) {
                    SubjectAreaCell theSACell = (SubjectAreaCell) theTargetCell;
                    SubjectArea theArea = (SubjectArea) theSACell.getUserObject();
                    theArea.getViews().add(theView);

                    theSACell.add(theCell);
                }

                GraphConstants.setBounds(theCell.getAttributes(), new Rectangle2D.Double(aPoint.getX(), aPoint.getY(),
                        -1, -1));

                layoutCache.insert(theCell);

                theCell.transferAttributesToProperties(theCell.getAttributes());

            } catch (Exception e) {
                worldConnector.notifyAboutException(e);
            }

            graph.doLayout();
        }
    }

    protected void commandClasspath() {
        ClasspathEditor theEditor = new ClasspathEditor(scrollPane, preferences);
        if (theEditor.showModal() == DialogConstants.MODAL_RESULT_OK) {
            try {
                theEditor.applyValues();
            } catch (Exception e) {
                worldConnector.notifyAboutException(e);
            }
        }

    }

    protected void commandPreferences() {
        PreferencesEditor theEditor = new PreferencesEditor(graph, preferences, this);
        if (theEditor.showModal() == DialogConstants.MODAL_RESULT_OK) {
            try {
                theEditor.applyValues();
            } catch (Exception e) {
                worldConnector.notifyAboutException(e);
            }
        }

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
        for (Object theItem : layoutCache.getVisibleSet()) {
            if (theItem instanceof SubjectAreaCell) {
                SubjectAreaCell theCell = (SubjectAreaCell) theItem;
                if (theCell.getUserObject().equals(aArea)) {
                    aArea.setVisible(false);

                    Object[] theCellObjects = new Object[] { theCell };
                    layoutCache.hideCells(theCellObjects, true);
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
        for (CellView theCellView : layoutCache.getHiddenCellViews()) {
            Object theItem = theCellView.getCell();
            if (theItem instanceof SubjectAreaCell) {
                SubjectAreaCell theCell = (SubjectAreaCell) theItem;
                if (theCell.getUserObject().equals(aArea)) {
                    aArea.setVisible(true);

                    Object[] theCellObjects = DefaultGraphModel.getDescendants(graphModel, new Object[] { theCell })
                            .toArray();

                    layoutCache.showCells(theCellObjects, true);
                    for (Object theSingleCell : theCellObjects) {
                        if (theSingleCell instanceof TableCell) {
                            TableCell theTableCell = (TableCell) theSingleCell;
                            Table theTable = (Table) theTableCell.getUserObject();

                            theTableCell.transferPropertiesToAttributes(theTable);
                            layoutCache.edit(new Object[] { theTableCell }, theTableCell.getAttributes());
                        }
                        if (theSingleCell instanceof ViewCell) {
                            ViewCell theViewCell = (ViewCell) theSingleCell;
                            View theView = (View) theViewCell.getUserObject();

                            theViewCell.transferPropertiesToAttributes(theView);
                            layoutCache.edit(new Object[] { theViewCell }, theViewCell.getAttributes());
                        }
                        if (theSingleCell instanceof CommentCell) {
                            CommentCell theCommentCell = (CommentCell) theSingleCell;
                            Comment theComment = (Comment) theCommentCell.getUserObject();

                            theCommentCell.transferPropertiesToAttributes(theComment);
                            layoutCache.edit(new Object[] { theCommentCell }, theCommentCell.getAttributes());
                        }
                    }
                }
            }
        }
        updateSubjectAreasMenu();
    }

    /**
     * Generate a documentation for the current model from a JasperReports
     * template.
     * 
     * @param aJRXMLFile
     *            the name of the template
     */
    protected void commandGenerateDocumentation(final File aJRXMLFile) {

        if (model.getDialect() == null) {
            MessagesHelper.displayErrorMessage(graph, getResourceHelper().getText(
                    ERDesignerBundle.PLEASEDEFINEADATABASECONNECTIONFIRST));
            return;
        }

        LongRunningTask<JasperPrint> theTask = new LongRunningTask<JasperPrint>(worldConnector) {

            @Override
            public JasperPrint doWork(MessagePublisher aMessagePublisher) throws Exception {

                aMessagePublisher.publishMessage(getResourceHelper().getText(ERDesignerBundle.DOCSTEP1));

                ModelIOUtilities theUtils = ModelIOUtilities.getInstance();
                File theTempFile = File.createTempFile("mogwai", ".mxm");
                FileOutputStream theOutputStream = new FileOutputStream(theTempFile);
                theUtils.serializeModelToXML(model, theOutputStream);
                theOutputStream.close();

                aMessagePublisher.publishMessage(getResourceHelper().getText(ERDesignerBundle.DOCSTEP2));

                JasperPrint thePrint = JasperUtils.runJasperReport(theTempFile, aJRXMLFile);

                aMessagePublisher.publishMessage(getResourceHelper().getText(ERDesignerBundle.DOCSTEP3));

                return thePrint;
            }

            @Override
            public void handleResult(JasperPrint aResult) {

                JRViewer theViewer = new JRViewer(aResult);

                DefaultDialog theResult = new DefaultDialog(scrollPane, getResourceHelper(),
                        ERDesignerBundle.CREATEDBDOCUMENTATION);
                theResult.setContentPane(theViewer);
                theResult.setMinimumSize(new Dimension(640, 480));
                theResult.pack();
                theViewer.setFitPageZoomRatio();

                theResult.setVisible(true);
            }

        };
        theTask.start();
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
                theItem.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        commandGenerateDocumentation(theJRXMLFile);
                    }

                });

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
                        commandOpenFromFile(theFile);
                    }

                });

                lruMenu.add(theItem);
                UIInitializer.getInstance().initialize(theItem);
            }

            for (final ConnectionDescriptor theConnectionInfo : preferences.getRecentlyUsedConnections()) {
                JMenuItem theItem1 = new JMenuItem(theConnectionInfo.toString());
                theItem1.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        commandDBConnection(theConnectionInfo);
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

    /**
     * Edit the database connection.
     */
    protected void commandDBConnection() {
        commandDBConnection(model.createConnectionHistoryEntry());
    }

    protected void commandDBConnection(ConnectionDescriptor aConnection) {
        DatabaseConnectionEditor theEditor = new DatabaseConnectionEditor(scrollPane, model, preferences, aConnection);
        if (theEditor.showModal() == DialogConstants.MODAL_RESULT_OK) {
            try {
                theEditor.applyValues();
                addCurrentConnectionToConnectionHistory();
            } catch (Exception e) {
                worldConnector.notifyAboutException(e);
            }
        }
    }

    /**
     * Edit the repository connection.
     */
    protected void commandRepositoryConnection() {
        RepositoryConnectionEditor theEditor = new RepositoryConnectionEditor(scrollPane, preferences);
        if (theEditor.showModal() == DialogConstants.MODAL_RESULT_OK) {
            try {
                theEditor.applyValues();
            } catch (Exception e) {
                worldConnector.notifyAboutException(e);
            }
        }
    }

    /**
     * Edit the domains.
     */
    protected void commandEditDomains() {
        DomainEditor theEditor = new DomainEditor(model, scrollPane);
        if (theEditor.showModal() == DialogConstants.MODAL_RESULT_OK) {
            try {
                theEditor.applyValues();
            } catch (Exception e) {
                worldConnector.notifyAboutException(e);
            }
        }
    }

    protected void commandExport(Exporter aExporter, ExportType aExportType) {

        if (aExportType.equals(ExportType.ONE_PER_FILE)) {

            JFileChooser theChooser = new JFileChooser();
            theChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (theChooser.showSaveDialog(scrollPane) == JFileChooser.APPROVE_OPTION) {
                File theBaseDirectory = theChooser.getSelectedFile();

                CellView[] theViews = layoutCache.getAllViews();
                for (CellView theView : theViews) {
                    if (theView instanceof TableCellView) {
                        VertexView theItemCellView = (VertexView) theView;
                        DefaultGraphCell theItemCell = (DefaultGraphCell) theItemCellView.getCell();
                        ModelItem theItem = (ModelItem) theItemCell.getUserObject();

                        File theOutputFile = new File(theBaseDirectory, theItem.getName()
                                + aExporter.getFileExtension());
                        try {
                            aExporter.exportToStream(theItemCellView.getRendererComponent(graph, false, false, false),
                                    new FileOutputStream(theOutputFile));
                        } catch (Exception e) {
                            worldConnector.notifyAboutException(e);
                        }
                    }
                }
            }

        } else {

            JFileChooser theChooser = new JFileChooser();
            GenericFileFilter theFilter = new GenericFileFilter(aExporter.getFileExtension(), aExporter
                    .getFileExtension()
                    + " File");
            theChooser.setFileFilter(theFilter);
            if (theChooser.showSaveDialog(scrollPane) == JFileChooser.APPROVE_OPTION) {

                File theFile = theFilter.getCompletedFile(theChooser.getSelectedFile());
                try {
                    aExporter.fullExportToStream(graph, new FileOutputStream(theFile));
                } catch (Exception e) {
                    worldConnector.notifyAboutException(e);
                }
            }

        }
    }

    protected void commandNew() {

        Model theModel = worldConnector.createNewModel();
        setModel(theModel);

        setupViewForNothing();

        worldConnector.setStatusText(getResourceHelper().getText(ERDesignerBundle.NEWMODELCREATED));
    }

    protected void commandOpenFromFile() {

        ModelFileFilter theFiler = new ModelFileFilter();

        JFileChooser theChooser = new JFileChooser();
        theChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        theChooser.setFileFilter(theFiler);
        if (theChooser.showOpenDialog(scrollPane) == JFileChooser.APPROVE_OPTION) {

            File theFile = theFiler.getCompletedFile(theChooser.getSelectedFile());

            commandOpenFromFile(theFile);
        }
    }

    protected void commandOpenFromFile(File aFile) {

        FileInputStream theStream = null;

        try {
            setIntelligentLayoutEnabled(false);

            theStream = new FileInputStream(aFile);

            Model theModel = ModelIOUtilities.getInstance().deserializeModelFromXML(theStream);
            worldConnector.initializeLoadedModel(theModel);

            setModel(theModel);

            preferences.addRecentlyUsedFile(aFile);

            addCurrentConnectionToConnectionHistory();

            setupViewFor(aFile);
            worldConnector.setStatusText(getResourceHelper().getText(ERDesignerBundle.FILELOADED));

        } catch (Exception e) {

            MessagesHelper.displayErrorMessage(graph, getResourceHelper().getText(ERDesignerBundle.ERRORLOADINGFILE));

            worldConnector.notifyAboutException(e);
        } finally {
            if (theStream != null) {
                try {
                    theStream.close();
                } catch (IOException e) {
                    // Ignore this exception
                }
            }

            setIntelligentLayoutEnabled(preferences.isIntelligentLayout());
        }
    }

    /**
     * Reverse engineer a model from a database connection.
     */
    public void commandReverseEngineer() {

        if (model.getDialect() == null) {
            MessagesHelper.displayErrorMessage(graph, getResourceHelper().getText(
                    ERDesignerBundle.PLEASEDEFINEADATABASECONNECTIONFIRST));
            return;
        }

        final ReverseEngineerEditor theEditor = new ReverseEngineerEditor(model, scrollPane, preferences);
        if (theEditor.showModal() == DialogConstants.MODAL_RESULT_OK) {

            setIntelligentLayoutEnabled(false);

            try {

                final Connection theConnection = model.createConnection(preferences);
                if (theConnection == null) {
                    return;
                }
                final ReverseEngineeringStrategy theStrategy = model.getDialect().getReverseEngineeringStrategy();
                final Model theTempModel = model;

                LongRunningTask<ReverseEngineeringOptions> theRETask = new LongRunningTask<ReverseEngineeringOptions>(
                        worldConnector) {

                    @Override
                    public ReverseEngineeringOptions doWork(MessagePublisher aMessagePublisher) throws Exception {
                        ReverseEngineeringOptions theOptions = theEditor.createREOptions();
                        theOptions.getTableEntries().addAll(
                                theStrategy.getTablesForSchemas(theConnection, theOptions.getSchemaEntries()));
                        return theOptions;
                    }

                    @Override
                    public void handleResult(final ReverseEngineeringOptions aResult) {
                        TablesSelectEditor theTablesEditor = new TablesSelectEditor(aResult, scrollPane);
                        if (theTablesEditor.showModal() == DialogConstants.MODAL_RESULT_OK) {

                            LongRunningTask<Model> theTask = new LongRunningTask<Model>(worldConnector) {

                                @Override
                                public Model doWork(final MessagePublisher aPublisher) throws Exception {
                                    ReverseEngineeringNotifier theNotifier = new ReverseEngineeringNotifier() {

                                        public void notifyMessage(String aResourceKey, String... aValues) {
                                            String theMessage = MessageFormat.format(getResourceHelper().getText(
                                                    aResourceKey), (Object[]) aValues);
                                            aPublisher.publishMessage(theMessage);
                                        }

                                    };

                                    theStrategy.updateModelFromConnection(theTempModel, worldConnector, theConnection,
                                            aResult, theNotifier);

                                    return theTempModel;
                                }

                                @Override
                                public void handleResult(Model aResultModel) {
                                    setModel(aResultModel);
                                }

                                @Override
                                public void cleanup() throws SQLException {
                                    if (!model.getDialect().generatesManagedConnection()) {
                                        theConnection.close();
                                    }
                                }

                            };
                            theTask.start();
                        }
                    }

                };
                theRETask.start();

            } catch (Exception e) {
                worldConnector.notifyAboutException(e);
            } finally {
                setIntelligentLayoutEnabled(preferences.isIntelligentLayout());
            }
        }
    }

    /**
     * Save the current model to file.
     */
    protected void commandSaveFileAs() {

        ModelFileFilter theFiler = new ModelFileFilter();

        JFileChooser theChooser = new JFileChooser();
        theChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        theChooser.setFileFilter(theFiler);
        theChooser.setSelectedFile(currentEditingFile);
        if (theChooser.showSaveDialog(scrollPane) == JFileChooser.APPROVE_OPTION) {

            File theFile = theFiler.getCompletedFile(theChooser.getSelectedFile());
            commandSaveModelToFile(theFile);

        }
    }

    /**
     * Save the current model to the current file. If the current file is
     * unknown, a saveas action is performed.
     */
    protected void commandSaveFile() {
        if (currentEditingFile != null) {
            commandSaveModelToFile(currentEditingFile);
        } else {
            commandSaveFileAs();
        }
    }

    private void commandSaveModelToFile(File aFile) {

        DateFormat theFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        Date theNow = new Date();

        FileOutputStream theStream = null;
        PrintWriter theWriter = null;
        try {

            setIntelligentLayoutEnabled(false);

            if (aFile.exists()) {
                File theBakFile = new File(aFile.toString() + "_" + theFormat.format(theNow));
                aFile.renameTo(theBakFile);
            }

            theStream = new FileOutputStream(aFile);

            ModelIOUtilities.getInstance().serializeModelToXML(model, theStream);

            worldConnector.initTitle();

            preferences.addRecentlyUsedFile(aFile);

            updateRecentlyUsedMenuEntries();

            if (model.getModificationTracker() instanceof HistoryModificationTracker) {
                HistoryModificationTracker theTracker = (HistoryModificationTracker) model.getModificationTracker();
                StatementList theStatements = theTracker.getNotSavedStatements();
                if (theStatements.size() > 0) {
                    StringBuilder theFileName = new StringBuilder(aFile.toString());
                    int p = theFileName.lastIndexOf(".");
                    if (p > 0) {

                        SQLGenerator theGenerator = model.getDialect().createSQLGenerator();

                        theFileName = new StringBuilder(theFileName.substring(0, p));

                        theFileName.insert(p, "_" + theFormat.format(theNow));
                        theFileName.append(".sql");

                        theWriter = new PrintWriter(new File(theFileName.toString()));
                        for (Statement theStatement : theStatements) {
                            theWriter.print(theStatement.getSql());
                            theWriter.println(theGenerator.createScriptStatementSeparator());
                            theStatement.setSaved(true);

                        }
                    }
                }
            }

            setupViewFor(aFile);
            worldConnector.setStatusText(getResourceHelper().getText(ERDesignerBundle.FILESAVED));

        } catch (Exception e) {
            worldConnector.notifyAboutException(e);
        } finally {
            if (theStream != null) {
                try {
                    theStream.close();
                } catch (IOException e) {
                    // Ignore this exception
                }
            }
            if (theWriter != null) {
                theWriter.close();
            }

            setIntelligentLayoutEnabled(preferences.isIntelligentLayout());
        }
    }

    /**
     * Save the current model to a repository.
     */
    protected void commandSaveToRepository() {

        ConnectionDescriptor theRepositoryConnection = preferences.getRepositoryConnection();
        if (theRepositoryConnection == null) {
            MessagesHelper.displayErrorMessage(scrollPane, getResourceHelper().getText(
                    ERDesignerBundle.ERRORINREPOSITORYCONNECTION));
            return;
        }
        Connection theConnection = null;
        Dialect theDialect = DialectFactory.getInstance().getDialect(theRepositoryConnection.getDialect());
        try {

            setIntelligentLayoutEnabled(false);

            theConnection = theDialect.createConnection(preferences.createDriverClassLoader(), theRepositoryConnection
                    .getDriver(), theRepositoryConnection.getUrl(), theRepositoryConnection.getUsername(),
                    theRepositoryConnection.getPassword(), false);

            List<RepositoryEntryDesciptor> theEntries = ModelIOUtilities.getInstance().getRepositoryEntries(theDialect,
                    theConnection);

            SaveToRepositoryEditor theEditor = new SaveToRepositoryEditor(scrollPane, theEntries,
                    currentRepositoryEntry);
            if (theEditor.showModal() == DialogConstants.MODAL_RESULT_OK) {
                try {

                    RepositoryEntryDesciptor theDesc = theEditor.getRepositoryDescriptor();

                    theDesc = ModelIOUtilities.getInstance().serializeModelToDB(theDesc, theDialect, theConnection,
                            model, preferences);

                    setupViewFor(theDesc);
                    worldConnector.setStatusText(getResourceHelper().getText(ERDesignerBundle.FILESAVED));

                } catch (Exception e) {
                    worldConnector.notifyAboutException(e);
                }
            }
        } catch (Exception e) {
            worldConnector.notifyAboutException(e);
        } finally {
            if (theConnection != null && !theDialect.generatesManagedConnection()) {
                try {
                    theConnection.close();
                } catch (SQLException e) {
                    // Do nothing here
                }
            }

            setIntelligentLayoutEnabled(preferences.isIntelligentLayout());
        }
    }

    /**
     * Create a migration script from repository.
     */
    protected void commandCreateMigrationScript() {

        ConnectionDescriptor theRepositoryConnection = preferences.getRepositoryConnection();
        if (theRepositoryConnection == null) {
            MessagesHelper.displayErrorMessage(scrollPane, getResourceHelper().getText(
                    ERDesignerBundle.ERRORINREPOSITORYCONNECTION));
            return;
        }
        Connection theConnection = null;
        Dialect theDialect = DialectFactory.getInstance().getDialect(theRepositoryConnection.getDialect());
        try {

            theConnection = theDialect.createConnection(preferences.createDriverClassLoader(), theRepositoryConnection
                    .getDriver(), theRepositoryConnection.getUrl(), theRepositoryConnection.getUsername(),
                    theRepositoryConnection.getPassword(), false);

            RepositoryEntity theEntity = DictionaryModelSerializer.SERIALIZER.getRepositoryEntity(theDialect
                    .getHibernateDialectClass(), theConnection, currentRepositoryEntry);

            MigrationScriptEditor theEditor = new MigrationScriptEditor(scrollPane, theEntity,
                    new GenericConnectionProvider(theConnection, theDialect.createSQLGenerator()
                            .createScriptStatementSeparator()), preferences, worldConnector);

            theEditor.showModal();

        } catch (Exception e) {
            worldConnector.notifyAboutException(e);
        } finally {
            if (theConnection != null && !theDialect.generatesManagedConnection()) {
                try {
                    theConnection.close();
                } catch (SQLException e) {
                    // Do nothing here
                }
            }
        }
    }

    /**
     * Open the database model from an existing connection.
     */
    protected void commandOpenFromRepository() {

        ConnectionDescriptor theRepositoryConnection = preferences.getRepositoryConnection();
        if (theRepositoryConnection == null) {
            MessagesHelper.displayErrorMessage(scrollPane, getResourceHelper().getText(
                    ERDesignerBundle.ERRORINREPOSITORYCONNECTION));
            return;
        }
        Connection theConnection = null;
        Dialect theDialect = DialectFactory.getInstance().getDialect(theRepositoryConnection.getDialect());
        try {

            setIntelligentLayoutEnabled(false);

            theConnection = theDialect.createConnection(preferences.createDriverClassLoader(), theRepositoryConnection
                    .getDriver(), theRepositoryConnection.getUrl(), theRepositoryConnection.getUsername(),
                    theRepositoryConnection.getPassword(), false);

            List<RepositoryEntryDesciptor> theEntries = ModelIOUtilities.getInstance().getRepositoryEntries(theDialect,
                    theConnection);

            LoadFromRepositoryEditor theEditor = new LoadFromRepositoryEditor(scrollPane, preferences, theConnection,
                    theEntries);
            if (theEditor.showModal() == DialogConstants.MODAL_RESULT_OK) {

                RepositoryEntryDesciptor theDescriptor = theEditor.getModel().getEntry();

                Model theModel = ModelIOUtilities.getInstance().deserializeModelfromRepository(theDescriptor,
                        theDialect, theConnection, preferences);
                worldConnector.initializeLoadedModel(theModel);

                setupViewFor(theDescriptor);
                worldConnector.setStatusText(getResourceHelper().getText(ERDesignerBundle.FILELOADED));

                currentRepositoryEntry = theDescriptor;
                currentEditingFile = null;

                setModel(theModel);
            }

        } catch (Exception e) {
            worldConnector.notifyAboutException(e);
        } finally {
            if (theConnection != null && !theDialect.generatesManagedConnection()) {
                try {
                    theConnection.close();
                } catch (SQLException e) {
                    // Do nothing here
                }
            }
            setIntelligentLayoutEnabled(preferences.isIntelligentLayout());
        }
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

    protected void commandGenerateSQL() {

        if (model.getDialect() == null) {
            MessagesHelper.displayErrorMessage(graph, getResourceHelper().getText(
                    ERDesignerBundle.PLEASEDEFINEADATABASECONNECTIONFIRST));
            return;
        }

        try {
            SQLGenerator theGenerator = model.getDialect().createSQLGenerator();
            StatementList theStatements = theGenerator.createCreateAllObjects(model);
            SQLEditor theEditor = new SQLEditor(scrollPane, new ModelBasedConnectionProvider(model), theStatements,
                    currentEditingFile, "schema.sql", preferences, worldConnector);
            theEditor.showModal();
        } catch (VetoException e) {
            worldConnector.notifyAboutException(e);
        }
    }

    protected String generateChangelogSQLFileName() {
        return "changelog.sql";
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

    /**
     * Run the OpenXava Exporter.
     */
    protected void commandExportToOpenXava() {
        OpenXavaExportEditor theEditor = new OpenXavaExportEditor(model, scrollPane);
        if (theEditor.showModal() == DialogConstants.MODAL_RESULT_OK) {
            try {
                theEditor.applyValues();

                worldConnector.setStatusText(getResourceHelper().getText(ERDesignerBundle.OPENXAVAEXPORTOK));
            } catch (Exception e) {
                worldConnector.notifyAboutException(e);
            }
        }
    }

    /**
     * Show the DDL changelog.
     */
    protected void commandGenerateChangelogSQL() {

        if (model.getDialect() == null) {
            MessagesHelper.displayErrorMessage(graph, getResourceHelper().getText(
                    ERDesignerBundle.PLEASEDEFINEADATABASECONNECTIONFIRST));
            return;
        }

        StatementList theStatements = ((HistoryModificationTracker) model.getModificationTracker()).getStatements();
        SQLEditor theEditor = new SQLEditor(scrollPane, new ModelBasedConnectionProvider(model), theStatements,
                currentEditingFile, generateChangelogSQLFileName(), preferences, worldConnector);
        theEditor.showModal();
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
    protected GraphModel createNewGraphModel() {
        GraphModel theModel = new DefaultGraphModel();
        theModel.addGraphModelListener(new ERDesignerGraphModelListener());
        return theModel;
    }

    /**
     * Factiry Method for the graph layout cacne.
     * 
     * @return the newly created graph layout cache
     */
    protected GraphLayoutCache createNewGraphlayoutCache() {
        GraphLayoutCache theCache = new GraphLayoutCache(graphModel, new CellViewFactory(), true);
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

            graphModel = createNewGraphModel();
            layoutCache = createNewGraphlayoutCache();

            graph = new ERDesignerGraph(preferences, model, graphModel, layoutCache) {

                @Override
                public void commandNewTable(Point2D aLocation) {
                    ERDesignerComponent.this.commandAddTableAndOptionalConnector(aLocation, null);
                }

                @Override
                public void commandNewComment(Point2D aLocation) {
                    ERDesignerComponent.this.commandAddComment(aLocation);
                }

                @Override
                public void commandNewView(Point2D aLocation) {
                    ERDesignerComponent.this.commandAddView(aLocation);
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
                public void commandNewTableAndRelation(Point2D aLocation, TableCell aExportingTableCell) {
                    ERDesignerComponent.this.commandAddTableAndOptionalConnector(aLocation, aExportingTableCell);
                }

                @Override
                public void commandNewRelation(TableCell aImportingCell, TableCell aExportingCell) {
                    ERDesignerComponent.this.commandAddRelation(aImportingCell, aExportingCell);
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

        } finally {

            commandSetZoom(ZOOMSCALE_HUNDREDPERCENT);
            commandSetTool(ToolEnum.HAND);

            updateSubjectAreasMenu();

            setIntelligentLayoutEnabled(preferences.isIntelligentLayout());
        }
    }

    private class GraphModelMappingInfo {
        Map<Table, TableCell> modelTableCells = new HashMap<Table, TableCell>();

        Map<View, ViewCell> modelViewCells = new HashMap<View, ViewCell>();

        Map<Comment, CommentCell> modelCommentCells = new HashMap<Comment, CommentCell>();
    }

    private GraphModelMappingInfo fillGraph(Model aModel) {

        graphModel = createNewGraphModel();
        layoutCache = createNewGraphlayoutCache();

        graph.setModel(graphModel);
        graph.setGraphLayoutCache(layoutCache);

        GraphModelMappingInfo theInfo = new GraphModelMappingInfo();

        for (Table theTable : aModel.getTables()) {
            TableCell theCell = new TableCell(theTable);
            theCell.transferPropertiesToAttributes(theTable);

            layoutCache.insert(theCell);

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

            layoutCache.insert(theCell);

            theInfo.modelViewCells.put(theView, theCell);
        }

        for (Comment theComment : aModel.getComments()) {
            CommentCell theCell = new CommentCell(theComment);
            theCell.transferPropertiesToAttributes(theComment);

            layoutCache.insert(theCell);

            theInfo.modelCommentCells.put(theComment, theCell);
        }

        for (Relation theRelation : aModel.getRelations()) {

            TableCell theImportingCell = theInfo.modelTableCells.get(theRelation.getImportingTable());
            TableCell theExportingCell = theInfo.modelTableCells.get(theRelation.getExportingTable());

            RelationEdge theCell = new RelationEdge(theRelation, theImportingCell, theExportingCell);
            theCell.transferPropertiesToAttributes(theRelation);

            layoutCache.insert(theCell);
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

            layoutCache.insertGroup(theSubjectAreaCell, theTableCells.toArray());
            layoutCache.toBack(new Object[] { theSubjectAreaCell });

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

    /**
     * Add a relation to the model.
     * 
     * @param aImportingCell
     * @param aExportingCell
     */
    protected boolean commandAddRelation(TableCell aImportingCell, TableCell aExportingCell) {
        Table theImportingTable = (Table) aImportingCell.getUserObject();
        Table theExportingTable = (Table) aExportingCell.getUserObject();

        Relation theRelation = createPreparedRelationFor(theImportingTable, theExportingTable);

        RelationEditor theEditor = new RelationEditor(theImportingTable.getOwner(), scrollPane);
        theEditor.initializeFor(theRelation);

        if (theEditor.showModal() == DialogConstants.MODAL_RESULT_OK) {

            RelationEdge theEdge = new RelationEdge(theRelation, aImportingCell, aExportingCell);

            try {
                theEditor.applyValues();
                layoutCache.insert(theEdge);

                return true;
            } catch (Exception e) {
                worldConnector.notifyAboutException(e);
            }
        }
        return false;
    }

    private Relation createPreparedRelationFor(Table aSourceTable, Table aTargetTable) {
        Relation theRelation = new Relation();
        theRelation.setImportingTable(aSourceTable);
        theRelation.setExportingTable(aTargetTable);
        theRelation.setOnUpdate(preferences.getOnUpdateDefault());
        theRelation.setOnDelete(preferences.getOnDeleteDefault());

        String thePattern = preferences.getAutomaticRelationAttributePattern();
        String theTargetTableName = model.getDialect().getCastType().cast(aTargetTable.getName());

        // Create the foreign key suggestions
        Index thePrimaryKey = aTargetTable.getPrimarykey();
        for (IndexExpression theExpression : thePrimaryKey.getExpressions()) {
            Attribute theAttribute = theExpression.getAttributeRef();
            if (theAttribute != null) {
                String theNewname = MessageFormat.format(thePattern, theTargetTableName, theAttribute.getName());
                Attribute theNewAttribute = aSourceTable.getAttributes().findByName(theNewname);
                if (theNewAttribute == null) {
                    theNewAttribute = theAttribute.clone();
                    theNewAttribute.setSystemId(ModelUtilities.createSystemIdFor(theNewAttribute));
                    theNewAttribute.setOwner(null);
                    theNewAttribute.setName(theNewname);
                }
                theRelation.getMapping().put(theExpression, theNewAttribute);
            }
        }
        return theRelation;
    }

    /**
     * Add a new comment to the model.
     * 
     * @param aLocation
     *            the location
     */
    protected void commandAddComment(Point2D aLocation) {
        Comment theComment = new Comment();
        CommentEditor theEditor = new CommentEditor(model, scrollPane);
        theEditor.initializeFor(theComment);
        if (theEditor.showModal() == DialogConstants.MODAL_RESULT_OK) {
            try {

                try {
                    theEditor.applyValues();
                } catch (VetoException e) {
                    worldConnector.notifyAboutException(e);
                }

                CommentCell theCell = new CommentCell(theComment);
                theCell.transferPropertiesToAttributes(theComment);

                Object theTargetCell = graph.getFirstCellForLocation(aLocation.getX(), aLocation.getY());
                if (theTargetCell instanceof SubjectAreaCell) {
                    SubjectAreaCell theSACell = (SubjectAreaCell) theTargetCell;
                    SubjectArea theArea = (SubjectArea) theSACell.getUserObject();
                    theArea.getComments().add(theComment);

                    theSACell.add(theCell);
                }

                GraphConstants.setBounds(theCell.getAttributes(), new Rectangle2D.Double(aLocation.getX(), aLocation
                        .getY(), -1, -1));

                layoutCache.insert(theCell);

                theCell.transferAttributesToProperties(theCell.getAttributes());

            } catch (Exception e) {
                worldConnector.notifyAboutException(e);
            }

            graph.doLayout();
        }
    }

    protected void addExportEntries(DefaultMenu aMenu, final Exporter aExporter) {

        DefaultAction theAllInOneAction = new DefaultAction(ERDesignerBundle.BUNDLE_NAME, ERDesignerBundle.ALLINONEFILE);
        DefaultMenuItem theAllInOneItem = new DefaultMenuItem(theAllInOneAction);
        theAllInOneAction.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                commandExport(aExporter, ExportType.ALL_IN_ONE);
            }
        });
        aMenu.add(theAllInOneItem);

        DefaultAction theOnePerTableAction = new DefaultAction(ERDesignerBundle.BUNDLE_NAME,
                ERDesignerBundle.ONEFILEPERTABLE);
        DefaultMenuItem theOnePerTable = new DefaultMenuItem(theOnePerTableAction);
        theOnePerTableAction.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                commandExport(aExporter, ExportType.ONE_PER_FILE);
            }
        });
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

    protected void commandConvertModel() {
        if (model.getDialect() == null) {
            MessagesHelper.displayErrorMessage(graph, getResourceHelper().getText(
                    ERDesignerBundle.PLEASEDEFINEADATABASECONNECTIONFIRST));
            return;
        }

        ConvertModelEditor theEditor = new ConvertModelEditor(model, scrollPane);
        if (theEditor.showModal() == DialogConstants.MODAL_RESULT_OK) {
            try {
                theEditor.applyValues();

                setModel(model);

                worldConnector.setStatusText(getResourceHelper().getText(ERDesignerBundle.MODELCONVERTED));
            } catch (Exception e) {
                worldConnector.notifyAboutException(e);
            }
        }
    }

    protected void commandCompleteCompare() {
        if (model.getDialect() == null) {
            MessagesHelper.displayErrorMessage(graph, getResourceHelper().getText(
                    ERDesignerBundle.PLEASEDEFINEADATABASECONNECTIONFIRST));
            return;
        }

        final ReverseEngineerEditor theEditor = new ReverseEngineerEditor(model, scrollPane, preferences);
        if (theEditor.showModal() == DialogConstants.MODAL_RESULT_OK) {

            try {
                final Connection theConnection = model.createConnection(preferences);
                if (theConnection == null) {
                    return;
                }
                final ReverseEngineeringStrategy theStrategy = model.getDialect().getReverseEngineeringStrategy();
                final ReverseEngineeringOptions theOptions = theEditor.createREOptions();

                final Model theDatabaseModel = worldConnector.createNewModel();
                theDatabaseModel.setDialect(model.getDialect());
                theDatabaseModel.getProperties().copyFrom(model);

                LongRunningTask<Model> theTask = new LongRunningTask<Model>(worldConnector) {

                    @Override
                    public Model doWork(final MessagePublisher aPublisher) throws Exception {
                        theOptions.getTableEntries().addAll(
                                theStrategy.getTablesForSchemas(theConnection, theOptions.getSchemaEntries()));

                        ReverseEngineeringNotifier theNotifier = new ReverseEngineeringNotifier() {

                            public void notifyMessage(String aResourceKey, String... aValues) {
                                String theMessage = MessageFormat.format(getResourceHelper().getText(aResourceKey),
                                        (Object[]) aValues);
                                aPublisher.publishMessage(theMessage);
                            }

                        };

                        theStrategy.updateModelFromConnection(theDatabaseModel, worldConnector, theConnection,
                                theOptions, theNotifier);

                        return theDatabaseModel;

                    }

                    @Override
                    public void handleResult(Model aResultModel) {
                        addConnectionToConnectionHistory(theDatabaseModel.createConnectionHistoryEntry());

                        CompleteCompareEditor theCompare = new CompleteCompareEditor(scrollPane, model, aResultModel,
                                preferences);
                        theCompare.showModal();
                    }

                    @Override
                    public void cleanup() throws SQLException {
                        if (!model.getDialect().generatesManagedConnection()) {
                            theConnection.close();
                        }
                    }
                };
                theTask.start();

            } catch (Exception e) {
                worldConnector.notifyAboutException(e);
            }
        }
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
    protected void repaintGraph() {
        for (CellView theView : layoutCache.getCellViews()) {
            graph.updateAutoSize(theView);
        }
        layoutCache.reload();
        layoutCache.update(layoutCache.getAllViews());

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
}
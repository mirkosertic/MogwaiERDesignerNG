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
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.BackingStoreException;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JToggleButton;

import org.jdesktop.swingworker.SwingWorker;
import org.jgraph.event.GraphModelEvent;
import org.jgraph.event.GraphModelListener;
import org.jgraph.event.GraphLayoutCacheEvent.GraphLayoutCacheChange;
import org.jgraph.graph.CellView;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.dialect.ReverseEngineeringNotifier;
import de.erdesignerng.dialect.ReverseEngineeringOptions;
import de.erdesignerng.dialect.ReverseEngineeringStrategy;
import de.erdesignerng.dialect.StatementList;
import de.erdesignerng.io.GenericFileFilter;
import de.erdesignerng.io.ModelFileFilter;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.ModelIOUtilities;
import de.erdesignerng.model.Relation;
import de.erdesignerng.model.Table;
import de.erdesignerng.modificationtracker.VetoException;
import de.erdesignerng.util.ApplicationPreferences;
import de.erdesignerng.visual.ERDesignerGraph;
import de.erdesignerng.visual.ExportType;
import de.erdesignerng.visual.MessagesHelper;
import de.erdesignerng.visual.cells.ModelCell;
import de.erdesignerng.visual.cells.RelationEdge;
import de.erdesignerng.visual.cells.TableCell;
import de.erdesignerng.visual.cells.views.CellViewFactory;
import de.erdesignerng.visual.cells.views.TableCellView;
import de.erdesignerng.visual.editor.DialogConstants;
import de.erdesignerng.visual.editor.classpath.ClasspathEditor;
import de.erdesignerng.visual.editor.connection.DatabaseConnectionEditor;
import de.erdesignerng.visual.editor.preferences.PreferencesEditor;
import de.erdesignerng.visual.editor.reverseengineer.ReverseEngineerEditor;
import de.erdesignerng.visual.editor.sql.SQLEditor;
import de.erdesignerng.visual.editor.table.TableEditor;
import de.erdesignerng.visual.export.Exporter;
import de.erdesignerng.visual.export.ImageExporter;
import de.erdesignerng.visual.export.SVGExporter;
import de.erdesignerng.visual.layout.Layouter;
import de.erdesignerng.visual.layout.LayouterFactory;
import de.erdesignerng.visual.plaf.basic.ERDesignerGraphUI;
import de.erdesignerng.visual.tools.EntityTool;
import de.erdesignerng.visual.tools.HandTool;
import de.erdesignerng.visual.tools.RelationTool;
import de.erdesignerng.visual.tools.ToolEnum;
import de.mogwai.common.client.looks.UIInitializer;
import de.mogwai.common.client.looks.components.DefaultComboBox;
import de.mogwai.common.client.looks.components.DefaultScrollPane;
import de.mogwai.common.client.looks.components.DefaultToggleButton;
import de.mogwai.common.client.looks.components.DefaultToolbar;
import de.mogwai.common.client.looks.components.action.ActionEventProcessor;
import de.mogwai.common.client.looks.components.action.DefaultAction;
import de.mogwai.common.client.looks.components.menu.DefaultMenu;
import de.mogwai.common.client.looks.components.menu.DefaultMenuItem;
import de.mogwai.common.i18n.ResourceHelper;
import de.mogwai.common.i18n.ResourceHelperProvider;

public class ERDesignerComponent implements ResourceHelperProvider {

    private static final class ERDesignerGrapgModelListener implements GraphModelListener {
        public void graphChanged(GraphModelEvent aEvent) {
            GraphLayoutCacheChange theChange = aEvent.getChange();
        
            Object[] theChangedObjects = theChange.getChanged();
            Map theChangedAttributes = theChange.getPreviousAttributes();
            if (theChangedAttributes != null) {
                for (Object theChangedObject : theChangedObjects) {
                    Map theAttributes = (Map) theChangedAttributes.get(theChangedObject);
        
                    if (theChangedObject instanceof ModelCell) {
        
                        ModelCell theCell = (ModelCell) theChangedObject;
                        theCell.transferAttributesToProperties(theAttributes);
                    }
                }
            }
        
        }
    }
    
    private final class ReverseEngineerSwingWorker extends SwingWorker<Model, String> {
        private final Connection connection;

        private final ReverseEngineeringOptions options;

        private final ReverseEngineeringStrategy strategy;

        private ReverseEngineerSwingWorker(ReverseEngineeringOptions options, ReverseEngineeringStrategy strategy, Connection connection) {
            this.options = options;
            this.strategy = strategy;
            this.connection = connection;
        }

        @Override
        protected Model doInBackground() throws Exception {
            try {
                ReverseEngineeringNotifier theNotifier = new ReverseEngineeringNotifier() {
        
                    public void notifyMessage(String aResourceKey, String... aValues) {
                        String theMessage = MessageFormat.format(getResourceHelper().getText(aResourceKey),
                                aValues);
                        publish(new String[] { theMessage });
                    }
        
                };
                return strategy.createModelFromConnection(connection, options, theNotifier);
            } catch (Exception e) {
                worldConnector.notifyAboutException(e);
            }
            return null;
        }

        @Override
        protected void process(List<String> aString) {
            for (String theMessage : aString) {
                worldConnector.setStatusText(theMessage);
            }
        }
    }
    
    private static GraphModelListener graphModelListener = new ERDesignerGrapgModelListener();

    private DefaultAction classpathAction;
    
    private File currentEditingFile;

    private DefaultAction dbConnectionAction;
    
    private DefaultAction domainsAction;
    
    private DefaultAction entityAction;

    private JToggleButton entityButton;

    private DefaultAction exitAction;

    private DefaultAction exportAction;

    private DefaultAction exportSVGAction;

    private DefaultAction fileAction;

    private ERDesignerGraph graph;

    private GraphModel graphModel;

    private DefaultAction handAction;

    private JToggleButton handButton;

    private DefaultAction layoutAction;

    private GraphLayoutCache layoutCache;

    private DefaultAction layoutgraphvizAction;

    private DefaultAction loadAction;

    private DefaultAction lruAction;

    private DefaultMenu lruMenu;

    private Model model;

    private DefaultAction newAction;

    private ApplicationPreferences preferences;

    private DefaultAction relationAction;

    private JToggleButton relationButton;

    private DefaultAction reverseEngineerAction;

    private DefaultAction saveAction;

    private DefaultScrollPane scrollPane = new DefaultScrollPane();

    private ERDesignerWorldConnector worldConnector;

    private DefaultAction zoomAction;
    
    private DefaultComboBox zoomBox = new DefaultComboBox();    

    private DefaultAction zoomInAction;

    private DefaultAction zoomOutAction;
    
    private DefaultAction preferencesAction;
    
    private DefaultAction generateSQL;

    private static final ZoomInfo ZOOMSCALE_HUNDREDPERCENT = new ZoomInfo("100%", 1);

    public ERDesignerComponent(ApplicationPreferences aPreferences, ERDesignerWorldConnector aConnector) {
        worldConnector = aConnector;
        preferences = aPreferences;
        initActions();
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
        
        layoutgraphvizAction = new DefaultAction(new ActionEventProcessor() {

            public void processActionEvent(ActionEvent e) {
                commandLayoutGraphviz();
            }

        }, this, ERDesignerBundle.LAYOUTBYGRAPHVIZ);        
        
        loadAction = new DefaultAction(new ActionEventProcessor() {

            public void processActionEvent(ActionEvent aEvent) {
                commandOpenFile();
            }

        }, this, ERDesignerBundle.LOADMODEL);        
        
        handAction = new DefaultAction(new ActionEventProcessor() {

            public void processActionEvent(ActionEvent e) {
                commandSetTool(ToolEnum.HAND);
            }

        }, this, ERDesignerBundle.HAND);
        
        layoutAction = new DefaultAction(this, ERDesignerBundle.LAYOUT);        
        
        exportSVGAction = new DefaultAction(this, ERDesignerBundle.ASSVG);        
        
        entityAction = new DefaultAction(new ActionEventProcessor() {

            public void processActionEvent(ActionEvent e) {
                commandSetTool(ToolEnum.ENTITY);
            }

        }, this, ERDesignerBundle.ENTITY);        
        
        fileAction = new DefaultAction(this, ERDesignerBundle.FILE);        
        
        exportAction = new DefaultAction(this, ERDesignerBundle.EXPORT);        
        
        exitAction = new DefaultAction(new ActionEventProcessor() {

            public void processActionEvent(ActionEvent e) {
                commandExit();
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
        
        lruMenu = new DefaultMenu(lruAction);        
        
        ERDesignerToolbarEntry theFileMenu = new ERDesignerToolbarEntry(ERDesignerBundle.FILE);
        if (worldConnector.supportsPreferences()) {
            theFileMenu.add(preferencesAction);
            theFileMenu.addSeparator();
        }
        
        theFileMenu.add(newAction);
        theFileMenu.addSeparator();
        theFileMenu.add(saveAction);
        theFileMenu.add(loadAction);
        theFileMenu.addSeparator();

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
            addSeparator = true;
        }

        if (addSeparator) {
            theDBMenu.addSeparator();
        }
        
        theDBMenu.add(new DefaultMenuItem(reverseEngineerAction));
        theDBMenu.addSeparator();
        theDBMenu.add(new DefaultMenuItem(generateSQL));
        
        ERDesignerToolbarEntry theViewMenu = new ERDesignerToolbarEntry(ERDesignerBundle.VIEW);

        DefaultMenu theLayoutMenu = new DefaultMenu(layoutAction);
        theLayoutMenu.add(new DefaultMenuItem(layoutgraphvizAction));

        theViewMenu.add(theLayoutMenu);
        theViewMenu.addSeparator();
        theViewMenu.add(new DefaultMenuItem(zoomInAction));
        theViewMenu.add(new DefaultMenuItem(zoomOutAction));

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
        theToolBar.add(saveAction);
        theToolBar.addSeparator();
        theToolBar.add(zoomBox);
        theToolBar.addSeparator();
        theToolBar.add(zoomInAction);
        theToolBar.add(zoomOutAction);
        theToolBar.addSeparator();
        
        handButton = new DefaultToggleButton(handAction);
        relationButton = new DefaultToggleButton(relationAction);
        entityButton = new DefaultToggleButton(entityAction);

        ButtonGroup theGroup = new ButtonGroup();
        theGroup.add(handButton);
        theGroup.add(relationButton);
        theGroup.add(entityButton);

        theToolBar.add(handButton);
        theToolBar.add(entityButton);
        theToolBar.add(relationButton);
        
        worldConnector.initTitle();

        initLRUMenu();

        UIInitializer.getInstance().initialize(scrollPane);
    }
    
    protected void commandAddTable(Point2D aPoint) {
        Table theTable = new Table();
        TableEditor theEditor = new TableEditor(model, scrollPane);
        theEditor.initializeFor(theTable);
        if (theEditor.showModal() == DialogConstants.MODAL_RESULT_OK) {
            try {
                
                try {
                    theEditor.applyValues();
                } catch (VetoException e) {
                    e.printStackTrace();
                    return;
                }

                TableCell theCell = new TableCell(theTable);
                theCell.transferPropertiesToAttributes(theTable);

                GraphConstants.setBounds(theCell.getAttributes(), new Rectangle2D.Double(aPoint.getX(), aPoint.getY(),
                        -1, -1));

                layoutCache.insert(theCell);

                theCell.transferAttributesToProperties(theCell.getAttributes());

            } catch (Exception e) {
                worldConnector.notifyAboutException(e);
            }
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
        PreferencesEditor theEditor = new PreferencesEditor(scrollPane, preferences);
        if (theEditor.showModal() == DialogConstants.MODAL_RESULT_OK) {
            try {
                theEditor.applyValues();
            } catch (Exception e) {
                worldConnector.notifyAboutException(e);
            }
        }

    }    

    protected void commandDBConnection() {
        DatabaseConnectionEditor theEditor = new DatabaseConnectionEditor(scrollPane, model, preferences);
        if (theEditor.showModal() == DialogConstants.MODAL_RESULT_OK) {
            try {
                theEditor.applyValues();
            } catch (Exception e) {
                worldConnector.notifyAboutException(e);
            }
        }
    }

    protected void commandDelete(Object aCell) {

    }

    protected void commandExit() {
        try {
            preferences.store();
        } catch (BackingStoreException e) {
            worldConnector.notifyAboutException(e);
        }

        System.exit(0);
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
                        TableCellView theTableCellView = (TableCellView) theView;
                        TableCell theTableCell = (TableCell) theTableCellView.getCell();
                        Table theTable = (Table) theTableCell.getUserObject();

                        File theOutputFile = new File(theBaseDirectory, theTable.getName()
                                + aExporter.getFileExtension());
                        try {
                            aExporter.exportToStream(theTableCellView.getRendererComponent(graph, false, false, false),
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

    protected void commandLayoutGraphviz() {
        try {
            Layouter theLayout = LayouterFactory.getInstance().createGraphvizLayouter();
            theLayout.applyLayout(preferences, graph, graph.getRoots());
        } catch (Exception e) {
            worldConnector.notifyAboutException(e);
        }
    }

    protected void commandLogicalView() {
        graph.setDomainDisplayMode(true);
        graph.invalidate();
        graph.repaint();
    }

    protected void commandNew() {
        currentEditingFile = null;

        Model theModel = worldConnector.createNewModel();
        setModel(theModel);

        worldConnector.initTitle();
        
        worldConnector.setStatusText(getResourceHelper().getText(ERDesignerBundle.NEWMODELCREATED));        
    }

    protected void commandOpenFile() {

        ModelFileFilter theFiler = new ModelFileFilter();

        JFileChooser theChooser = new JFileChooser();
        theChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        theChooser.setFileFilter(theFiler);
        if (theChooser.showOpenDialog(scrollPane) == JFileChooser.APPROVE_OPTION) {

            File theFile = theFiler.getCompletedFile(theChooser.getSelectedFile());

            commandOpenFile(theFile);
        }
    }

    protected void commandOpenFile(File aFile) {

        try {
            Model theModel = ModelIOUtilities.getInstance().deserializeModelFromXML(new FileInputStream(aFile));
            worldConnector.initializeLoadedModel(theModel);
            
            setModel(theModel);

            currentEditingFile = aFile;
            worldConnector.initTitle(currentEditingFile);

            preferences.addLRUFile(aFile);

            initLRUMenu();
            
            worldConnector.setStatusText(getResourceHelper().getText(ERDesignerBundle.FILELOADED));

        } catch (Exception e) {
            
            MessagesHelper.displayErrorMessage(graph, getResourceHelper().getText(ERDesignerBundle.ERRORLOADINGFILE));
            
            worldConnector.notifyAboutException(e);
        }
    }

    protected void commandPhysicalView() {
        graph.setDomainDisplayMode(false);
        graph.invalidate();
        graph.repaint();
    }

    public void commandReverseEngineer() {
        
        if (model.getDialect() == null) {
            MessagesHelper.displayErrorMessage(graph, getResourceHelper().getText(ERDesignerBundle.PLEASEDEFINEADATABASECONNECTIONFIRST));
            return;
        }

        ReverseEngineerEditor theEditor = new ReverseEngineerEditor(model, scrollPane, preferences);
        if (theEditor.showModal() == DialogConstants.MODAL_RESULT_OK) {

            try {

                final Connection theConnection = model.createConnection(preferences);
                final ReverseEngineeringStrategy theStrategy = model.getDialect().getReverseEngineeringStrategy();
                final ReverseEngineeringOptions theOptions = theEditor.createREOptions();

                SwingWorker<Model, String> theWorker = new ReverseEngineerSwingWorker(theOptions, theStrategy, theConnection);
                theWorker.execute();

                Model theModel = theWorker.get();
                theModel.getProperties().copyFrom(model);
                if (theModel != null) {
                    setModel(theModel);
                }

            } catch (Exception e) {
                worldConnector.notifyAboutException(e);
            }

        }
    }

    protected void commandSaveFile() {

        ModelFileFilter theFiler = new ModelFileFilter();

        JFileChooser theChooser = new JFileChooser();
        theChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        theChooser.setFileFilter(theFiler);
        theChooser.setSelectedFile(currentEditingFile);
        if (theChooser.showSaveDialog(scrollPane) == JFileChooser.APPROVE_OPTION) {

            File theFile = theFiler.getCompletedFile(theChooser.getSelectedFile());
            try {

                ModelIOUtilities.getInstance().serializeModelToXML(model, new FileOutputStream(theFile));

                currentEditingFile = theFile;
                worldConnector.initTitle();

                preferences.addLRUFile(theFile);

                initLRUMenu();
                
                worldConnector.setStatusText(getResourceHelper().getText(ERDesignerBundle.FILESAVED));                

            } catch (Exception e) {
                worldConnector.notifyAboutException(e);
            }

        }
    }

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
    }

    protected void commandSetZoom(ZoomInfo aZoomInfo) {
        graph.setScale(aZoomInfo.getValue());
        zoomBox.setSelectedItem(aZoomInfo);
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
        try {
            StatementList theStatements = model.getDialect().createSQLGenerator().createCreateAllObjects(model);
            SQLEditor theEditor = new SQLEditor(scrollPane, model, theStatements);
            theEditor.showModal();
        } catch (VetoException e) {
            worldConnector.notifyAboutException(e);
        }
    }

    public ResourceHelper getResourceHelper() {
        return ResourceHelper.getResourceHelper(ERDesignerBundle.BUNDLE_NAME);
    }

    private void initLRUMenu() {

        lruMenu.removeAll();
        if (preferences != null) {

            List<File> theFiles = preferences.getLrufiles();
            for (final File theFile : theFiles) {
                JMenuItem theItem = new JMenuItem(theFile.toString());
                theItem.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        commandOpenFile(theFile);
                    }

                });

                UIInitializer.getInstance().initializeFontAndColors(theItem);

                lruMenu.add(theItem);
            }
        }
    }
    
    public void setModel(Model aModel) {
        model = aModel;

        graphModel = new DefaultGraphModel();
        layoutCache = new GraphLayoutCache(graphModel, new CellViewFactory());

        graphModel.addGraphModelListener(graphModelListener);

        graph = new ERDesignerGraph(model, graphModel, layoutCache) {

            @Override
            public void commandNewTable(Point2D aLocation) {
                ERDesignerComponent.this.commandAddTable(aLocation);
            }

        };
        graph.setUI(new ERDesignerGraphUI(this));

        scrollPane.getViewport().removeAll();
        scrollPane.getViewport().add(graph);

        Map<Table, TableCell> theCells = new HashMap<Table, TableCell>();

        for (Table theTable : model.getTables()) {
            TableCell theCell = new TableCell(theTable);
            theCell.transferPropertiesToAttributes(theTable);

            layoutCache.insert(theCell);

            theCells.put(theTable, theCell);
        }

        for (Relation theRelation : model.getRelations()) {

            TableCell theImportingCell = theCells.get(theRelation.getImportingTable());
            TableCell theExportingCell = theCells.get(theRelation.getExportingTable());

            RelationEdge theCell = new RelationEdge(theRelation, theImportingCell, theExportingCell);
            theCell.transferPropertiesToAttributes(theRelation);

            layoutCache.insert(theCell);
        }

        commandSetZoom(ZOOMSCALE_HUNDREDPERCENT);
        commandSetTool(ToolEnum.HAND);
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

    public void savePreferences() {
        try {
            preferences.store();
        } catch (Exception e) {
            worldConnector.notifyAboutException(e);
        }
    }
}

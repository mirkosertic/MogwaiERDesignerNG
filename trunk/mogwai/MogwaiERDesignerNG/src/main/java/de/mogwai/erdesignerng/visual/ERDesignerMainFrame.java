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
package de.mogwai.erdesignerng.visual;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;

import org.jgraph.event.GraphModelEvent;
import org.jgraph.event.GraphModelListener;
import org.jgraph.event.GraphLayoutCacheEvent.GraphLayoutCacheChange;
import org.jgraph.graph.CellView;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;

import de.mogwai.erdesignerng.io.GenericFileFilter;
import de.mogwai.erdesignerng.io.ModelFileFilter;
import de.mogwai.erdesignerng.io.ModelIOUtilities;
import de.mogwai.erdesignerng.model.Model;
import de.mogwai.erdesignerng.model.Relation;
import de.mogwai.erdesignerng.model.Table;
import de.mogwai.erdesignerng.visual.cells.ModelCell;
import de.mogwai.erdesignerng.visual.cells.RelationEdge;
import de.mogwai.erdesignerng.visual.cells.TableCell;
import de.mogwai.erdesignerng.visual.cells.views.CellViewFactory;
import de.mogwai.erdesignerng.visual.cells.views.TableCellView;
import de.mogwai.erdesignerng.visual.components.StatusBar;
import de.mogwai.erdesignerng.visual.components.ToolBar;
import de.mogwai.erdesignerng.visual.editor.connection.DatabaseConnectionEditor;
import de.mogwai.erdesignerng.visual.editor.defaultvalue.DefaultValueEditor;
import de.mogwai.erdesignerng.visual.editor.domain.DomainEditor;
import de.mogwai.erdesignerng.visual.editor.table.TableEditor;
import de.mogwai.erdesignerng.visual.export.Exporter;
import de.mogwai.erdesignerng.visual.export.ImageExporter;
import de.mogwai.erdesignerng.visual.export.SVGExporter;
import de.mogwai.erdesignerng.visual.plaf.basic.ERDesignerGraphUI;
import de.mogwai.erdesignerng.visual.tools.EntityTool;
import de.mogwai.erdesignerng.visual.tools.HandTool;
import de.mogwai.erdesignerng.visual.tools.RelationTool;
import de.mogwai.erdesignerng.visual.tools.ToolEnum;

/**
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2007-07-27 18:23:37 $
 */
public class ERDesignerMainFrame extends JFrame {

	private class ZoomInfo {

		private String description;

		private double value;

		public ZoomInfo(String aDescription, double aValue) {
			description = aDescription;
			value = aValue;
		}

		public double getValue() {
			return value;
		}

		@Override
		public String toString() {
			return description;
		}
	}

	private final ZoomInfo ZOOMSCALE_HUNDREDPERCENT = new ZoomInfo("100%", 1);

	private GraphModel graphModel;

	private GraphLayoutCache layoutCache;

	private ERDesignerGraph graph;

	private Model model;

	private JMenuBar mainMenu = new JMenuBar();

	private ToolBar toolBar = new ToolBar();

	private StatusBar statusBar = new StatusBar();

	private JScrollPane scrollPane = new JScrollPane();

	private JComboBox zoomBox = new JComboBox();

	private Action fileAction = new GenericAction("File");

	private Action newAction = new GenericAction("New model", IconFactory
			.getNewIcon(), new ActionListener() {

		public void actionPerformed(ActionEvent e) {
			commandNew();
		}
	});

	private Action saveAction = new GenericAction("Save model...", IconFactory
			.getSaveIcon(), new ActionListener() {

		public void actionPerformed(ActionEvent aEvent) {
			commandSaveFile();
		}

	});

	private Action loadAction = new GenericAction("Load model...", IconFactory
			.getFolderIcon(), new ActionListener() {

		public void actionPerformed(ActionEvent aEvent) {
			commandOpenFile();
		}

	});

	private Action exitAction = new GenericAction("Exit", IconFactory
			.getExitIcon(), new ActionListener() {

		public void actionPerformed(ActionEvent e) {
			commandExit();
		}

	});

	private Action exportAction = new GenericAction("Export", IconFactory
			.getPageAddIcon(), null);

	private Action exportSVGAction = new GenericAction("As SVG");

	private Action databaseAction = new GenericAction("Database");

	private Action dbConnectionAction = new GenericAction("DB Connection...",
			IconFactory.getDBIcon(), new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					commandDBConnection();
				}

			});

	private Action reverseEngineerAction = new GenericAction(
			"Reverse engineer...");

	private Action domainsAction = new GenericAction("Domains...",
			new ActionListener() {

				public void actionPerformed(ActionEvent aEvent) {
					commandShowDomainEditor();
				}

			});

	private Action defaultValuesAction = new GenericAction("Default values...",
			new ActionListener() {

				public void actionPerformed(ActionEvent aEvent) {
					commandShowDefaultValuesEditor();
				}

			});

	private Action viewAction = new GenericAction("View");

	private Action zoomAction = new GenericAction("Zoom", new ActionListener() {

		public void actionPerformed(ActionEvent aEvent) {
			commandSetZoom((ZoomInfo) ((JComboBox) aEvent.getSource())
					.getSelectedItem());
		}
	});

	private Action zoomInAction = new GenericAction(
			IconFactory.getZoomInIcon(), new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					commandZoomIn();
				}

			});

	private Action zoomOutAction = new GenericAction(IconFactory
			.getZoomOutIcon(), new ActionListener() {

		public void actionPerformed(ActionEvent e) {
			commandZoomOut();
		}

	});

	private Action handAction = new GenericAction(IconFactory.getHandIcon(),
			new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					commandSetTool(ToolEnum.HAND);
				}

			});

	private Action entityAction = new GenericAction(
			IconFactory.getEntityIcon(), new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					commandSetTool(ToolEnum.ENTITY);
				}

			});

	private Action relationAction = new GenericAction(IconFactory
			.getRelationIcon(), new ActionListener() {

		public void actionPerformed(ActionEvent e) {
			commandSetTool(ToolEnum.RELATION);
		}

	});

	private JToggleButton handButton;

	private JToggleButton relationButton;

	private JToggleButton entityButton;

	private File currentEditingFile;

	public ERDesignerMainFrame() {
		initialize();
	}

	protected void addExportEntries(JMenu aMenu, final Exporter aExporter) {
		JMenuItem theAllInOneItem = aMenu.add("All in one");
		theAllInOneItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				commandExport(aExporter, ExportType.ALL_IN_ONE);
			}
		});

		JMenuItem theOnePerTable = aMenu.add("One file per table");
		theOnePerTable.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				commandExport(aExporter, ExportType.ONE_PER_FILE);
			}
		});
	}

	protected void initialize() {

		setJMenuBar(mainMenu);

		JMenu theFileMenu = new JMenu(fileAction);
		theFileMenu.add(new JMenuItem(newAction));
		theFileMenu.addSeparator();
		theFileMenu.add(new JMenuItem(saveAction));
		theFileMenu.add(new JMenuItem(loadAction));
		theFileMenu.addSeparator();

		JMenu theExportMenu = new JMenu(exportAction);

		List<String> theSupportedFormats = ImageExporter.getSupportedFormats();
		if (theSupportedFormats.contains("IMAGE/PNG")) {
			JMenu theSingleExportMenu = new JMenu("As PNG");
			theExportMenu.add(theSingleExportMenu);

			addExportEntries(theSingleExportMenu, new ImageExporter("png"));
		}
		if (theSupportedFormats.contains("IMAGE/JPEG")) {
			JMenu theSingleExportMenu = new JMenu("As JPEG");
			theExportMenu.add(theSingleExportMenu);

			addExportEntries(theSingleExportMenu, new ImageExporter("jpg"));
		}
		if (theSupportedFormats.contains("IMAGE/BMP")) {
			JMenu theSingleExportMenu = new JMenu("As BMP");
			theExportMenu.add(theSingleExportMenu);

			addExportEntries(theSingleExportMenu, new ImageExporter("bmp"));
		}

		JMenu theSVGExportMenu = new JMenu(exportSVGAction);

		theExportMenu.add(theSVGExportMenu);
		addExportEntries(theSVGExportMenu, new SVGExporter());

		theFileMenu.add(theExportMenu);

		theFileMenu.addSeparator();
		theFileMenu.add(new JMenuItem(exitAction));

		JMenu theDBMenu = new JMenu(databaseAction);
		theDBMenu.add(new JMenuItem(dbConnectionAction));
		theDBMenu.addSeparator();
		theDBMenu.add(new JMenuItem(reverseEngineerAction));
		theDBMenu.addSeparator();
		theDBMenu.add(new JMenuItem(domainsAction));
		theDBMenu.add(new JMenuItem(defaultValuesAction));

		mainMenu.add(theFileMenu);
		mainMenu.add(theDBMenu);

		DefaultComboBoxModel theZoomModel = new DefaultComboBoxModel();
		theZoomModel.addElement(ZOOMSCALE_HUNDREDPERCENT);
		for (int i = 9; i > 0; i--) {
			theZoomModel.addElement(new ZoomInfo(i * 10 + " %", ((double) i)
					/ (double) 10));
		}
		zoomBox.setPreferredSize(new Dimension(100, 21));
		zoomBox.setMaximumSize(new Dimension(100, 21));
		zoomBox.setAction(zoomAction);
		zoomBox.setModel(theZoomModel);

		toolBar.add(newAction);
		toolBar.addSeparator();
		toolBar.add(loadAction);
		toolBar.add(saveAction);
		toolBar.addSeparator();
		toolBar.add(zoomBox);
		toolBar.addSeparator();
		toolBar.add(zoomInAction);
		toolBar.add(zoomOutAction);
		toolBar.addSeparator();

		handButton = new JToggleButton(handAction);
		relationButton = new JToggleButton(relationAction);
		entityButton = new JToggleButton(entityAction);

		ButtonGroup theGroup = new ButtonGroup();
		theGroup.add(handButton);
		theGroup.add(relationButton);
		theGroup.add(entityButton);

		toolBar.add(handButton);
		toolBar.add(entityButton);
		toolBar.add(relationButton);

		Container theContentPane = getContentPane();
		theContentPane.setLayout(new BorderLayout());
		theContentPane.add(toolBar, BorderLayout.NORTH);
		theContentPane.add(scrollPane, BorderLayout.CENTER);
		theContentPane.add(statusBar, BorderLayout.SOUTH);

		setSize(800, 600);

		initTitle();
	}

	private void initTitle() {

		StringBuffer theTitle = new StringBuffer("Mogwai ERDesignerNG");
		if (currentEditingFile != null) {
			theTitle.append(" - ").append(currentEditingFile.toString());
		}

		setTitle(theTitle.toString());
	}

	public void setModel(Model aModel) {
		model = aModel;

		graphModel = new DefaultGraphModel();
		layoutCache = new GraphLayoutCache(graphModel, new CellViewFactory());

		graphModel.addGraphModelListener(graphModelListener);

		graph = new ERDesignerGraph(graphModel, layoutCache) {

			@Override
			public void commandNewTable(Point2D aLocation) {
				ERDesignerMainFrame.this.commandAddTable(aLocation);
			}

		};
		graph.setUI(new ERDesignerGraphUI());

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

			TableCell theImportingCell = theCells.get(theRelation
					.getImportingTable());
			TableCell theExportingCell = theCells.get(theRelation
					.getExportingTable());

			RelationEdge theCell = new RelationEdge(theRelation,
					theImportingCell, theExportingCell);
			theCell.transferPropertiesToAttributes(theRelation);

			layoutCache.insert(theCell);
		}

		commandSetZoom(ZOOMSCALE_HUNDREDPERCENT);
		commandSetTool(ToolEnum.HAND);
	}

	protected void commandShowDomainEditor() {
		DomainEditor theEditor = new DomainEditor(model, this);
		if (theEditor.showModal() == DomainEditor.MODAL_RESULT_OK) {
			try {
				theEditor.applyValues();
			} catch (Exception e) {
				logException(e);
			}
		}
	}

	protected void commandShowDefaultValuesEditor() {
		DefaultValueEditor theEditor = new DefaultValueEditor(model, this);
		if (theEditor.showModal() == DomainEditor.MODAL_RESULT_OK) {
			try {
				theEditor.applyValues();
			} catch (Exception e) {
				logException(e);
			}
		}
	}

	protected void commandOpenFile() {

		ModelFileFilter theFiler = new ModelFileFilter();

		JFileChooser theChooser = new JFileChooser();
		theChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		theChooser.setFileFilter(theFiler);
		if (theChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {

			File theFile = theFiler.getCompletedFile(theChooser
					.getSelectedFile());
			try {
				Model theModel = ModelIOUtilities.getInstance()
						.deserializeModelFromXML(new FileInputStream(theFile));
				setModel(theModel);

				currentEditingFile = theFile;
				initTitle();

			} catch (Exception e) {
				logException(e);
			}
		}
	}

	protected void commandSaveFile() {

		ModelFileFilter theFiler = new ModelFileFilter();

		JFileChooser theChooser = new JFileChooser();
		theChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		theChooser.setFileFilter(theFiler);
		theChooser.setSelectedFile(currentEditingFile);
		if (theChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {

			File theFile = theFiler.getCompletedFile(theChooser
					.getSelectedFile());
			try {

				ModelIOUtilities.getInstance().serializeModelToXML(model,
						new FileOutputStream(theFile));

				currentEditingFile = theFile;
				initTitle();
			} catch (Exception e) {
				logException(e);
			}

		}
	}

	protected void commandNew() {
		currentEditingFile = null;

		Model theModel = new Model();
		setModel(theModel);

		initTitle();
	}

	protected void commandSetZoom(ZoomInfo aZoomInfo) {
		graph.setScale(aZoomInfo.getValue());
		zoomBox.setSelectedItem(aZoomInfo);
	}

	/**
	 * Log an exception.
	 * 
	 * @param aException
	 */
	protected void logException(Exception aException) {
		aException.printStackTrace();
	}

	protected void commandAddTable(Point2D aPoint) {
		Table theTable = new Table();
		TableEditor theEditor = new TableEditor(model, this);
		theEditor.initializeFor(theTable);
		if (theEditor.showModal() == TableEditor.MODAL_RESULT_OK) {
			try {
				theEditor.applyValues();

				TableCell theCell = new TableCell(theTable);
				theCell.transferPropertiesToAttributes(theTable);

				GraphConstants.setBounds(theCell.getAttributes(),
						new Rectangle2D.Double(aPoint.getX(), aPoint.getY(),
								-1, -1));

				layoutCache.insert(theCell);

				theCell.transferAttributesToProperties(theCell.getAttributes());

			} catch (Exception e) {
				logException(e);
			}
		}
	}

	protected void commandDelete(Object aCell) {

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

	protected void commandZoomIn() {
		int theIndex = zoomBox.getSelectedIndex();
		if (theIndex < zoomBox.getItemCount() - 1) {
			theIndex++;
			zoomBox.setSelectedIndex(theIndex);
			commandSetZoom((ZoomInfo) zoomBox.getSelectedItem());
		}
	}

	protected void commandZoomOut() {
		int theIndex = zoomBox.getSelectedIndex();
		if (theIndex > 0) {
			theIndex--;
			zoomBox.setSelectedIndex(theIndex);
			commandSetZoom((ZoomInfo) zoomBox.getSelectedItem());
		}
	}

	protected void commandExit() {
		System.exit(0);
	}

	protected void commandDBConnection() {
		DatabaseConnectionEditor theEditor = new DatabaseConnectionEditor(this,
				model);
		if (theEditor.showModal() == DatabaseConnectionEditor.MODAL_RESULT_OK) {
			try {
				theEditor.applyValues();
			} catch (Exception e) {
				logException(e);
			}
		}
	}

	protected void commandExport(Exporter aExporter, ExportType aExportType) {

		if (aExportType.equals(ExportType.ONE_PER_FILE)) {

			JFileChooser theChooser = new JFileChooser();
			theChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			if (theChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
				File theBaseDirectory = theChooser.getSelectedFile();

				CellView[] theViews = layoutCache.getAllViews();
				for (CellView theView : theViews) {
					if (theView instanceof TableCellView) {
						TableCellView theTableCellView = (TableCellView) theView;
						TableCell theTableCell = (TableCell) theTableCellView
								.getCell();
						Table theTable = (Table) theTableCell.getUserObject();

						File theOutputFile = new File(theBaseDirectory,
								theTable.getName()
										+ aExporter.getFileExtension());
						try {
							aExporter.exportToStream(theTableCellView
									.getRendererComponent(graph, false, false,
											false), new FileOutputStream(
									theOutputFile));
						} catch (Exception e) {
							logException(e);
						}
					}
				}
			}

		} else {

			JFileChooser theChooser = new JFileChooser();
			GenericFileFilter theFilter = new GenericFileFilter(aExporter
					.getFileExtension(), aExporter.getFileExtension() + " File");
			theChooser.setFileFilter(theFilter);
			if (theChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {

				File theFile = theFilter.getCompletedFile(theChooser
						.getSelectedFile());
				try {
					aExporter.fullExportToStream(graph, new FileOutputStream(
							theFile));
				} catch (Exception e) {
					logException(e);
				}
			}

		}
	}

	private static GraphModelListener graphModelListener = new GraphModelListener() {

		public void graphChanged(GraphModelEvent aEvent) {
			GraphLayoutCacheChange theChange = aEvent.getChange();

			Object[] theChangedObjects = theChange.getChanged();
			Map theChangedAttributes = theChange.getPreviousAttributes();
			for (Object theChangedObject : theChangedObjects) {
				Map theAttributes = (Map) theChangedAttributes
						.get(theChangedObject);

				if (theChangedObject instanceof ModelCell) {

					ModelCell theCell = (ModelCell) theChangedObject;
					theCell.transferAttributesToProperties(theAttributes);
				}
			}

		}
	};
}

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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;

import org.jgraph.event.GraphModelEvent;
import org.jgraph.event.GraphModelListener;
import org.jgraph.event.GraphLayoutCacheEvent.GraphLayoutCacheChange;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;

import de.mogwai.erdesignerng.io.ModelIOUtilities;
import de.mogwai.erdesignerng.model.Model;
import de.mogwai.erdesignerng.model.Relation;
import de.mogwai.erdesignerng.model.Table;
import de.mogwai.erdesignerng.visual.cells.ModelCell;
import de.mogwai.erdesignerng.visual.cells.RelationCell;
import de.mogwai.erdesignerng.visual.cells.TableCell;
import de.mogwai.erdesignerng.visual.cells.views.CellViewFactory;
import de.mogwai.erdesignerng.visual.components.StatusBar;
import de.mogwai.erdesignerng.visual.components.ToolBar;
import de.mogwai.erdesignerng.visual.editor.defaultvalue.DefaultValueEditor;
import de.mogwai.erdesignerng.visual.editor.domain.DomainEditor;
import de.mogwai.erdesignerng.visual.paf.basic.ERDesignerGraphUI;

public class ERDesignerMainFrame extends JFrame {
	
	private class ZoomInfo {
		
		private String description;
		private double value;
		
		public ZoomInfo(String aDescription,double aValue) {
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
	
	private final ZoomInfo ZOOMSCALE_HUNDREDPERCENT = new ZoomInfo("100%",1);

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

	private Action newAction = new GenericAction("New model");

	private Action saveAction = new GenericAction("Save model...",
			new ActionListener() {

				public void actionPerformed(ActionEvent aEvent) {
					commandSaveFile();
				}

			});

	private Action loadAction = new GenericAction("Load model...",
			new ActionListener() {

				public void actionPerformed(ActionEvent aEvent) {
					commandOpenFile();
				}

			});

	private Action exitAction = new GenericAction("Exit");

	private Action databaseAction = new GenericAction("Database");

	private Action dbConnectionAction = new GenericAction("DB Connection...");

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

	private Action zoomAction = new GenericAction("Zoom",new ActionListener() {
		public void actionPerformed(ActionEvent aEvent) {
			commandSetZoom((ZoomInfo) ((JComboBox)aEvent.getSource()).getSelectedItem());
		}
	});

	public ERDesignerMainFrame() {
		initialize();
	}

	protected void initialize() {

		setJMenuBar(mainMenu);

		JMenu theFileMenu = new JMenu(fileAction);
		theFileMenu.add(new JMenuItem(newAction));
		theFileMenu.addSeparator();
		theFileMenu.add(new JMenuItem(saveAction));
		theFileMenu.add(new JMenuItem(loadAction));
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
		for (int i=9;i>0;i--) {
			theZoomModel.addElement(new ZoomInfo(i*10+" %",((double)i)/(double)10));
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

		Container theContentPane = getContentPane();
		theContentPane.setLayout(new BorderLayout());
		theContentPane.add(toolBar, BorderLayout.NORTH);
		theContentPane.add(scrollPane, BorderLayout.CENTER);
		theContentPane.add(statusBar, BorderLayout.SOUTH);

		setSize(800, 600);
	}

	public void setModel(Model aModel) {
		model = aModel;

		graphModel = new DefaultGraphModel();
		layoutCache = new GraphLayoutCache(graphModel, new CellViewFactory());

		graphModel.addGraphModelListener(graphModelListener);

		graph = new ERDesignerGraph(graphModel, layoutCache);
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

			RelationCell theCell = new RelationCell(theRelation,
					theImportingCell, theExportingCell);
			theCell.transferPropertiesToAttributes(theRelation);

			layoutCache.insert(theCell);
		}
		
		commandSetZoom(ZOOMSCALE_HUNDREDPERCENT);
	}

	protected void commandShowDomainEditor() {
		DomainEditor theEditor = new DomainEditor(model, this);
		if (theEditor.showModal() == DomainEditor.MODAL_RESULT_OK) {
		}
	}

	protected void commandShowDefaultValuesEditor() {
		DefaultValueEditor theEditor = new DefaultValueEditor(model, this);
		if (theEditor.showModal() == DomainEditor.MODAL_RESULT_OK) {
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
		if (theChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {

			File theFile = theFiler.getCompletedFile(theChooser
					.getSelectedFile());
			try {

				ModelIOUtilities.getInstance().serializeModelToXML(model,
						new FileOutputStream(theFile));

			} catch (Exception e) {
				logException(e);
			}

		}
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

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
package de.erdesignerng.visual.editor.completecompare;

import de.erdesignerng.model.Model;
import de.erdesignerng.model.utils.ModelComparator;
import de.erdesignerng.model.utils.ModelCompareResult;
import de.erdesignerng.util.ApplicationPreferences;
import de.erdesignerng.visual.editor.BaseEditor;
import de.mogwai.common.client.looks.UIInitializer;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-13 15:40:33 $
 */
public class CompleteCompareEditor extends BaseEditor {

	private CompleteCompareEditorView editingView;

	private final Model currentModel;

	private final Model databaseModel;

	public CompleteCompareEditor(Component aParent, Model aCurrentModel,
			Model aDatabaseModel, String aCaption) {
		super(aParent, aCaption);

		currentModel = aCurrentModel;
		databaseModel = aDatabaseModel;

		initialize();

		TreeCellRenderer theRenderer = new CompareTreeCellRenderer();

		editingView.getCurrentModelView().setCellRenderer(theRenderer);
		editingView.getDatabaseView().setCellRenderer(theRenderer);
		editingView.getOkButton().setAction(okAction);

		refreshView();

		UIInitializer.getInstance().initialize(this);
	}

	private void refreshView() {

		ModelComparator theComparator = new ModelComparator();
		ModelCompareResult theResult = theComparator.compareModels(
				currentModel, databaseModel);

		editingView.getCurrentModelView().setModel(
				new DefaultTreeModel(theResult.getModelRootNode()));
		editingView.getDatabaseView().setModel(
				new DefaultTreeModel(theResult.getDbRootNode()));

		int theRow = 0;
		while (theRow < editingView.getCurrentModelView().getRowCount()) {
			editingView.getCurrentModelView().expandRow(theRow++);
		}

		theRow = 0;
		while (theRow < editingView.getDatabaseView().getRowCount()) {
			editingView.getDatabaseView().expandRow(theRow++);
		}
	}

	private void initialize() {

		editingView = new CompleteCompareEditorView();
		editingView.getOkButton().setAction(okAction);

		JScrollPane modelScroll = editingView.getCurrentModelView()
				.getScrollPane();
		JScrollPane dbScroll = editingView.getDatabaseView().getScrollPane();

		modelScroll.getVerticalScrollBar().setModel(
				dbScroll.getVerticalScrollBar().getModel());
		modelScroll.getHorizontalScrollBar().setModel(
				dbScroll.getHorizontalScrollBar().getModel());

		setContentPane(editingView);
		setResizable(true);

		pack();

		setMinimumSize(getSize());
		ApplicationPreferences.getInstance().setWindowSize(
				getClass().getSimpleName(), this);
	}

	@Override
	protected void commandOk() {
		ApplicationPreferences.getInstance().updateWindowSize(
				getClass().getSimpleName(), this);
		super.commandOk();
	}

	@Override
	public void applyValues() throws Exception {
	}
}
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
package de.mogwai.erdesignerng.visual.editor.defaultvalue;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;

import de.mogwai.erdesignerng.model.DefaultValue;
import de.mogwai.erdesignerng.model.Model;
import de.mogwai.erdesignerng.visual.editor.BaseEditor;
import de.mogwai.erdesignerng.visual.editor.DialogConstants;

/**
 * Editor for default value declarations.
 * 
 * @author Mirko Sertic
 */
public class DefaultValueEditor extends BaseEditor {

	private DefaultListModel defaultValuesListModel;

	private DefaultValue currentEditingDefaultValue;

	private DefaultValueEditorView editingView;

	/**
	 * @param parent
	 */
	public DefaultValueEditor(Model aModel, JFrame aParent) {
		super(aParent);
		initialize();

		defaultValuesListModel = new DefaultListModel();
		for (DefaultValue theValue : aModel.getDefaultValues()) {
			defaultValuesListModel.addElement(theValue);
		}

		editingView.getDefaultValueList().setModel(defaultValuesListModel);

		updateEditFields();

		editingView.getDeleteButton().setEnabled(false);
		editingView.getRenameButton().setEnabled(false);
	}

	private void updateEditFields() {

		if (currentEditingDefaultValue != null) {
			editingView.getDefaultValueName().setText(
					currentEditingDefaultValue.getName());
			editingView.getDeclaration().setEnabled(true);
			editingView.getDeclaration().setText(
					currentEditingDefaultValue.getDatatype());
			editingView.getUpdateButton().setEnabled(true);
		} else {
			editingView.getDefaultValueName().setText(null);
			editingView.getDeclaration().setText(null);
			editingView.getUpdateButton().setEnabled(false);
			editingView.getDefaultValueName().setEnabled(false);
			editingView.getDeclaration().setEnabled(false);
		}

	}

	/**
	 * This method initializes this.
	 */
	private void initialize() {

		editingView = new DefaultValueEditorView();

		editingView.getUpdateButton().addActionListener(
				new java.awt.event.ActionListener() {

					public void actionPerformed(java.awt.event.ActionEvent e) {
						handleUpdate();
					}
				});
		editingView.getOkButton().addActionListener(
				new java.awt.event.ActionListener() {

					public void actionPerformed(java.awt.event.ActionEvent e) {
						handleClose();
					}
				});
		editingView.getCancelButton().addActionListener(
				new java.awt.event.ActionListener() {

					public void actionPerformed(java.awt.event.ActionEvent e) {
						setModalResult(MODAL_RESULT_CANCEL);
					}
				});
		editingView.getDefaultValueList().addListSelectionListener(
				new javax.swing.event.ListSelectionListener() {

					public void valueChanged(
							javax.swing.event.ListSelectionEvent e) {
						handleItemChanged(e);
					}
				});
		editingView.getNewButton().addActionListener(
				new java.awt.event.ActionListener() {

					public void actionPerformed(java.awt.event.ActionEvent e) {
						handleNew();
					}
				});
		editingView.getRenameButton().addActionListener(
				new java.awt.event.ActionListener() {

					public void actionPerformed(java.awt.event.ActionEvent e) {
						handleRename();
					}
				});
		editingView.getDeleteButton().addActionListener(
				new java.awt.event.ActionListener() {

					public void actionPerformed(java.awt.event.ActionEvent e) {
						handleDelete();
					}
				});

		setContentPane(editingView);
		setTitle("Default value dictionary");
		setResizable(false);
		pack();

	}

	private void handleClose() {

		setModalResult(DialogConstants.MODAL_RESULT_OK);
	}

	private void handleItemChanged(ListSelectionEvent e) {
		int index = editingView.getDefaultValueList().getSelectedIndex();
		if (index >= 0) {
			currentEditingDefaultValue = (DefaultValue) defaultValuesListModel
					.get(index);
			updateEditFields();

			editingView.getDefaultValueName().setEnabled(false);
			editingView.getDeclaration().setEnabled(true);

			editingView.getRenameButton().setEnabled(true);
			editingView.getDeleteButton().setEnabled(true);

		} else {
			editingView.getDefaultValueName().setEnabled(false);
			editingView.getDeclaration().setEnabled(false);

			editingView.getRenameButton().setEnabled(false);
			editingView.getDeleteButton().setEnabled(false);

			currentEditingDefaultValue = null;
			updateEditFields();
		}

	}

	private void handleNew() {
		currentEditingDefaultValue = new DefaultValue();
		updateEditFields();

		editingView.getDefaultValueName().setEnabled(true);
		editingView.getDeclaration().setEnabled(true);
		editingView.getRenameButton().setEnabled(false);
		editingView.getDeleteButton().setEnabled(false);
		editingView.getUpdateButton().setEnabled(true);
	}

	private void handleUpdate() {
		String name = editingView.getDefaultValueName().getText();
		String definition = editingView.getDeclaration().getText();

		if (name.trim().length() == 0) {
			JOptionPane.showMessageDialog(this,
					"Please enter a default value name", "Invalid name",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (name.indexOf(' ') >= 0) {
			JOptionPane.showMessageDialog(this,
					"Please enter a default value name without blanks",
					"Invalid default value name", JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (definition.trim().length() == 0) {
			JOptionPane.showMessageDialog(this, "Please enter a definition",
					"Invalid definition", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// Commented due to BUG 784958
		// if (definition.indexOf(' ')>=0) {
		// JOptionPane.showMessageDialog(this,"Please enter a definition without
		// blanks","Invalid definition",JOptionPane.ERROR_MESSAGE);
		// return;
		// }

		currentEditingDefaultValue.setName(name);
		currentEditingDefaultValue.setDatatype(definition);

		if (!defaultValuesListModel.contains(currentEditingDefaultValue)) {
			defaultValuesListModel.addElement(currentEditingDefaultValue);
			editingView.getDefaultValueName().setEnabled(false);
		}

	}

	private void handleRename() {
	}

	private void handleDelete() {
	}
}

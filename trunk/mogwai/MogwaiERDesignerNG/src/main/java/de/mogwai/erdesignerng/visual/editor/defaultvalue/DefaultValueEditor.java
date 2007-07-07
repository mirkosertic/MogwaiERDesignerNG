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

import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.event.ListSelectionEvent;

import de.mogwai.binding.BindingInfo;
import de.mogwai.erdesignerng.exception.ElementAlreadyExistsException;
import de.mogwai.erdesignerng.exception.ElementInvalidNameException;
import de.mogwai.erdesignerng.model.DefaultValue;
import de.mogwai.erdesignerng.model.DefaultValueList;
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

	private BindingInfo<DefaultValue> bindingInfo = new BindingInfo<DefaultValue>();

	private DefaultValueEditorView editingView;
	
	private Model model;
	
	private Map<String,DefaultValue> knownValues = new HashMap<String,DefaultValue>();

	/**
	 * @param parent
	 */
	public DefaultValueEditor(Model aModel, JFrame aParent) {
		super(aParent);

		model = aModel;
		
		initialize();

		defaultValuesListModel = new DefaultListModel();
		for (DefaultValue theValue : aModel.getDefaultValues()) {
			
			DefaultValue theClone = theValue.clone();
			
			knownValues.put(theClone.getName(), theClone);
			defaultValuesListModel.addElement(theValue);
		}

		editingView.getDefaultValueList().setModel(defaultValuesListModel);

		bindingInfo.addBinding("name", editingView.getDefaultValueName(), true);
		bindingInfo.addBinding("datatype", editingView.getDeclaration(), true);
		bindingInfo.configure();

		updateEditFields();
		setResizable(false);
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
		setTitle("Default values dictionary");
		setResizable(false);
		pack();

	}

	private void updateEditFields() {

		DefaultValue theValue = bindingInfo.getDefaultModel();

		if (theValue != null) {

			boolean isNew = !defaultValuesListModel.contains(theValue);

			editingView.getNewButton().setEnabled(true);
			editingView.getDeleteButton().setEnabled(!isNew);
			editingView.getRenameButton().setEnabled(!isNew);
			editingView.getDefaultValueName().setEnabled(isNew);
			editingView.getDeclaration().setEnabled(true);
			editingView.getUpdateButton().setEnabled(true);

		} else {
			editingView.getNewButton().setEnabled(true);
			editingView.getDeleteButton().setEnabled(false);
			editingView.getRenameButton().setEnabled(false);
			editingView.getDefaultValueName().setEnabled(false);
			editingView.getDeclaration().setEnabled(false);
			editingView.getUpdateButton().setEnabled(false);
		}

		bindingInfo.model2view();
		
		editingView.getDefaultValueList().invalidate();
		editingView.getDefaultValueList().setSelectedValue(bindingInfo.getDefaultModel(), true);
	}

	private void handleClose() {

		setModalResult(DialogConstants.MODAL_RESULT_OK);
	}

	private void handleItemChanged(ListSelectionEvent e) {

		int index = editingView.getDefaultValueList().getSelectedIndex();
		if (index >= 0) {
			bindingInfo.setDefaultModel((DefaultValue) defaultValuesListModel
					.get(index));
		}

		updateEditFields();
	}

	private void handleNew() {

		bindingInfo.setDefaultModel(new DefaultValue());
		updateEditFields();
	}
	
	private void handleUpdate() {

		DefaultValue theModel = bindingInfo.getDefaultModel();
		if (bindingInfo.validate().size() == 0) {
			bindingInfo.view2model();
			
			if (!defaultValuesListModel.contains(theModel)) {
				
				if (model.getDefaultValues().findByName(theModel.getName())!= null) {
					displayErrorMessage("Name already in use!");
					return;
				}
				defaultValuesListModel.addElement(theModel);
				knownValues.put(theModel.getName(), theModel);
			}

			updateEditFields();
		}
	}
	
	private void handleRename() {
	}

	private void handleDelete() {
	}
	
	public void applyValues(Model aModel) throws ElementAlreadyExistsException, ElementInvalidNameException {
		
		DefaultValueList theList = aModel.getDefaultValues();
		
		for(String theKey : knownValues.keySet()) {
			DefaultValue theValue = knownValues.get(theKey);
			
			DefaultValue theInModel = theList.findByName(theKey);
			if (theInModel != null) {
				theInModel.restoreFrom(theValue);
			} else {
				aModel.addDefaultValue(theValue);
			}
		}
	}
}

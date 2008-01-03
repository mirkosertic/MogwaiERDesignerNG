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
package de.erdesignerng.visual.editor.defaultvalue;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.event.ListSelectionEvent;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.exception.ElementAlreadyExistsException;
import de.erdesignerng.exception.ElementInvalidNameException;
import de.erdesignerng.model.DefaultValue;
import de.erdesignerng.model.DefaultValueList;
import de.erdesignerng.model.Model;
import de.erdesignerng.visual.editor.BaseEditor;
import de.erdesignerng.visual.editor.DialogConstants;
import de.mogwai.binding.BindingInfo;
import de.mogwai.common.client.looks.UIInitializer;
import de.mogwai.common.client.looks.components.action.ActionEventProcessor;
import de.mogwai.common.client.looks.components.action.DefaultAction;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-01-03 18:28:10 $
 */
public class DefaultValueEditor extends BaseEditor {

	private DefaultListModel defaultValuesListModel;

	private BindingInfo<DefaultValue> bindingInfo = new BindingInfo<DefaultValue>();

	private DefaultValueEditorView editingView;

	private Model model;

	private Map<String, DefaultValue> knownValues = new HashMap<String, DefaultValue>();
	
	private DefaultAction updateAction = new DefaultAction(
			new ActionEventProcessor() {

				public void processActionEvent(ActionEvent e) {
					commandUpdate();
				}
			}, this, ERDesignerBundle.UPDATE);

	private DefaultAction closeAction = new DefaultAction(
			new ActionEventProcessor() {

				public void processActionEvent(ActionEvent e) {
					commandClose();
				}
			}, this, ERDesignerBundle.OK);

	private DefaultAction cancelAction = new DefaultAction(
			new ActionEventProcessor() {

				public void processActionEvent(ActionEvent e) {
					commandCancel();
				}
			}, this, ERDesignerBundle.CANCEL);

	private DefaultAction newAction = new DefaultAction(
			new ActionEventProcessor() {

				public void processActionEvent(ActionEvent e) {
					commandNew();
				}
			}, this, ERDesignerBundle.NEW);

	private DefaultAction deleteAction = new DefaultAction(
			new ActionEventProcessor() {

				public void processActionEvent(ActionEvent e) {
					commandDelete();
				}
			}, this, ERDesignerBundle.DELETE);
	

	/**
	 * @param parent
	 */
	public DefaultValueEditor(Model aModel, Component aParent) {
		super(aParent, ERDesignerBundle.DEFAULTVALUES);

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

		editingView.getUpdateButton().setAction(updateAction);
		editingView.getOkButton().setAction(closeAction);
		editingView.getCancelButton().setAction(cancelAction);
		editingView.getDefaultValueList().addListSelectionListener(
				new javax.swing.event.ListSelectionListener() {

					public void valueChanged(
							javax.swing.event.ListSelectionEvent e) {
						handleItemChanged(e);
					}
				});
		editingView.getNewButton().setAction(newAction);
		editingView.getDeleteButton().setAction(deleteAction);

		setContentPane(editingView);
		setResizable(false);
		pack();

		UIInitializer.getInstance().initialize(this);		
	}

	private void updateEditFields() {

		DefaultValue theValue = bindingInfo.getDefaultModel();

		if (theValue != null) {

			boolean isNew = !defaultValuesListModel.contains(theValue);

			editingView.getNewButton().setEnabled(true);
			editingView.getDeleteButton().setEnabled(!isNew);
			editingView.getDefaultValueName().setEnabled(true);
			editingView.getDeclaration().setEnabled(true);
			editingView.getUpdateButton().setEnabled(true);

		} else {
			editingView.getNewButton().setEnabled(true);
			editingView.getDeleteButton().setEnabled(false);
			editingView.getDefaultValueName().setEnabled(false);
			editingView.getDeclaration().setEnabled(false);
			editingView.getUpdateButton().setEnabled(false);
		}

		bindingInfo.model2view();

		editingView.getDefaultValueList().invalidate();
		editingView.getDefaultValueList().setSelectedValue(
				bindingInfo.getDefaultModel(), true);
	}

	private void commandClose() {

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

	private void commandNew() {

		bindingInfo.setDefaultModel(new DefaultValue());
		updateEditFields();
	}

	private void commandUpdate() {

		DefaultValue theModel = bindingInfo.getDefaultModel();
		if (bindingInfo.validate().size() == 0) {
			bindingInfo.view2model();

			if (!defaultValuesListModel.contains(theModel)) {

				if (model.getDefaultValues().findByName(theModel.getName()) != null) {
					displayErrorMessage("Name already in use!");
					return;
				}
				defaultValuesListModel.addElement(theModel);
				knownValues.put(theModel.getName(), theModel);
			}

			updateEditFields();
		}
	}

	private void commandDelete() {
	}

	@Override
	public void applyValues() throws ElementAlreadyExistsException,
			ElementInvalidNameException {

		DefaultValueList theList = model.getDefaultValues();

		for (String theKey : knownValues.keySet()) {
			DefaultValue theValue = knownValues.get(theKey);

			DefaultValue theInModel = theList.findByName(theKey);
			if (theInModel != null) {
				theInModel.restoreFrom(theValue);
			} else {
				model.addDefaultValue(theValue);
			}
		}
	}
}

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
package de.erdesignerng.visual.editor.domain;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.event.ListSelectionEvent;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.exception.ElementAlreadyExistsException;
import de.erdesignerng.exception.ElementInvalidNameException;
import de.erdesignerng.model.Domain;
import de.erdesignerng.model.DomainList;
import de.erdesignerng.model.Model;
import de.erdesignerng.visual.editor.BaseEditor;
import de.erdesignerng.visual.editor.DialogConstants;
import de.mogwai.binding.BindingInfo;
import de.mogwai.common.client.looks.UIInitializer;
import de.mogwai.common.client.looks.components.action.ActionEventProcessor;
import de.mogwai.common.client.looks.components.action.DefaultAction;
import de.mogwai.common.client.looks.components.list.DefaultListModel;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-01-03 20:21:15 $
 */
public class DomainEditor extends BaseEditor {

	private DefaultListModel domainListModel;

	private BindingInfo<Domain> bindingInfo = new BindingInfo<Domain>();

	private DomainEditorView editingView = new DomainEditorView();

	private Model model;

	private Map<String, Domain> knownValues = new HashMap<String, Domain>();
	
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
	 * @param aParent
	 */
	public DomainEditor(Model aModel, Component aParent) {
		super(aParent, ERDesignerBundle.DOMAINS);

		model = aModel;

		initialize();

		domainListModel = editingView.getDomainList().getModel();
		for (Domain theDomain : aModel.getDomains()) {

			Domain theClone = theDomain.clone();

			domainListModel.add(theClone);

			knownValues.put(theClone.getName(), theClone);
		}

		editingView.getDomainList().setModel(domainListModel);

		// And finally update the corresponding editor fields..
		// Setup the java class list
		Vector javaTypes = new Vector();
		javaTypes.add("String");
		javaTypes.add("Integer");
		javaTypes.add("Double");
		javaTypes.add("Long");
		javaTypes.add("Float");
		javaTypes.add("Byte");
		javaTypes.add("Character");
		javaTypes.add("java.util.Date");
		Collections.sort(javaTypes);

		editingView.getJavatype().setModel(new DefaultComboBoxModel(javaTypes));

		bindingInfo.addBinding("name", editingView.getDomainName(), true);
		bindingInfo.addBinding("datatype", editingView.getDeclaration(), true);
		bindingInfo.addBinding("sequenced", editingView.getSequenced());
		bindingInfo
				.addBinding("javaClassName", editingView.getJavatype(), true);
		bindingInfo.configure();

		updateEditFields();
		setResizable(false);
	}
	
	/**
	 * This method initializes this.
	 */
	private void initialize() {

		editingView.getUpdateButton().setAction(updateAction);
		editingView.getOkButton().setAction(closeAction);
		editingView.getCancelButton().setAction(cancelAction);
		
		editingView.getDomainList().addListSelectionListener(
				new javax.swing.event.ListSelectionListener() {

					public void valueChanged(
							javax.swing.event.ListSelectionEvent e) {
						commandItemChanged(e);
					}
				});
		
		editingView.getNewButton().setAction(newAction);
		editingView.getDeleteButton().setAction(deleteAction);

		setContentPane(editingView);
		pack();

		UIInitializer.getInstance().initialize(this);		
	}

	private void updateEditFields() {

		Domain theValue = bindingInfo.getDefaultModel();

		if (theValue != null) {

			boolean isNew = !domainListModel.contains(theValue);

			editingView.getNewButton().setEnabled(true);
			editingView.getDeleteButton().setEnabled(!isNew);
			editingView.getDomainName().setEnabled(true);
			editingView.getDeclaration().setEnabled(true);
			editingView.getUpdateButton().setEnabled(true);
			editingView.getSequenced().setEnabled(true);
			editingView.getJavatype().setEnabled(true);

		} else {
			editingView.getNewButton().setEnabled(true);
			editingView.getDeleteButton().setEnabled(false);
			editingView.getDomainName().setEnabled(false);
			editingView.getDeclaration().setEnabled(false);
			editingView.getUpdateButton().setEnabled(false);
			editingView.getSequenced().setEnabled(false);
			editingView.getJavatype().setEnabled(false);
		}

		bindingInfo.model2view();

		editingView.getDomainList().invalidate();
		editingView.getDomainList().setSelectedValue(
				bindingInfo.getDefaultModel(), true);
	}

	private void commandClose() {

		setModalResult(DialogConstants.MODAL_RESULT_OK);
	}

	private void commandItemChanged(ListSelectionEvent e) {

		int index = editingView.getDomainList().getSelectedIndex();
		if (index >= 0) {
			bindingInfo.setDefaultModel((Domain) domainListModel.get(index));
		}

		updateEditFields();
	}

	private void commandNew() {
		bindingInfo.setDefaultModel(new Domain());
		updateEditFields();
	}

	private void commandUpdate() {

		Domain theModel = bindingInfo.getDefaultModel();
		if (bindingInfo.validate().size() == 0) {
			bindingInfo.view2model();

			if (!domainListModel.contains(theModel)) {

				if (model.getDefaultValues().findByName(theModel.getName()) != null) {
					displayErrorMessage("Name already in use!");
					return;
				}
				domainListModel.add(theModel);
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

		DomainList theList = model.getDomains();

		for (String theKey : knownValues.keySet()) {
			Domain theValue = knownValues.get(theKey);

			Domain theInModel = theList.findByName(theKey);
			if (theInModel != null) {
				theInModel.restoreFrom(theValue);
			} else {
				model.addDomain(theValue);
			}
		}
	}
}

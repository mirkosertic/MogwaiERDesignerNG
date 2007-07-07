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
package de.mogwai.erdesignerng.visual.editor.domain;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.event.ListSelectionEvent;

import de.mogwai.binding.BindingInfo;
import de.mogwai.erdesignerng.exception.ElementAlreadyExistsException;
import de.mogwai.erdesignerng.exception.ElementInvalidNameException;
import de.mogwai.erdesignerng.model.Domain;
import de.mogwai.erdesignerng.model.DomainList;
import de.mogwai.erdesignerng.model.Model;
import de.mogwai.erdesignerng.visual.editor.BaseEditor;
import de.mogwai.erdesignerng.visual.editor.DialogConstants;

/**
 * Editor for domain declarations.
 * 
 * @author Mirko Sertic
 */
public class DomainEditor extends BaseEditor {

	private DefaultListModel domainListModel;

	private BindingInfo<Domain> bindingInfo = new BindingInfo<Domain>();

	private DomainEditorView editingView = new DomainEditorView();

	private Model model;

	private Map<String, Domain> knownValues = new HashMap<String, Domain>();

	/**
	 * @param aParent
	 */
	public DomainEditor(Model aModel, JFrame aParent) {
		super(aParent);

		model = aModel;

		initialize();

		domainListModel = new DefaultListModel();
		for (Domain theDomain : aModel.getDomains()) {
			
			Domain theClone = theDomain.clone();
			
			domainListModel.addElement(theClone);

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

		editingView.getUpdateButton().addActionListener(
				new java.awt.event.ActionListener() {

					public void actionPerformed(java.awt.event.ActionEvent e) {
						commandUpdate();
					}
				});
		editingView.getOkButton().addActionListener(
				new java.awt.event.ActionListener() {

					public void actionPerformed(java.awt.event.ActionEvent e) {
						commandClose();
					}
				});
		editingView.getCancelButton().addActionListener(
				new java.awt.event.ActionListener() {

					public void actionPerformed(java.awt.event.ActionEvent e) {
						setModalResult(MODAL_RESULT_CANCEL);
					}
				});
		editingView.getDomainList().addListSelectionListener(
				new javax.swing.event.ListSelectionListener() {

					public void valueChanged(
							javax.swing.event.ListSelectionEvent e) {
						commandItemChanged(e);
					}
				});
		editingView.getNewButton().addActionListener(
				new java.awt.event.ActionListener() {

					public void actionPerformed(java.awt.event.ActionEvent e) {
						commandNew();
					}
				});
		editingView.getRenameButton().addActionListener(
				new java.awt.event.ActionListener() {

					public void actionPerformed(java.awt.event.ActionEvent e) {
						commandRename();
					}
				});
		editingView.getDeleteButton().addActionListener(
				new java.awt.event.ActionListener() {

					public void actionPerformed(java.awt.event.ActionEvent e) {
						commandDelete();
					}
				});

		setContentPane(editingView);
		setTitle("Domain dictionary");
		pack();

	}

	private void updateEditFields() {

		Domain theValue = bindingInfo.getDefaultModel();

		if (theValue != null) {

			boolean isNew = !domainListModel.contains(theValue);

			editingView.getNewButton().setEnabled(true);
			editingView.getDeleteButton().setEnabled(!isNew);
			editingView.getRenameButton().setEnabled(!isNew);
			editingView.getDomainName().setEnabled(isNew);
			editingView.getDeclaration().setEnabled(true);
			editingView.getUpdateButton().setEnabled(true);
			editingView.getSequenced().setEnabled(true);
			editingView.getJavatype().setEnabled(true);

		} else {
			editingView.getNewButton().setEnabled(true);
			editingView.getDeleteButton().setEnabled(false);
			editingView.getRenameButton().setEnabled(false);
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
				domainListModel.addElement(theModel);
				knownValues.put(theModel.getName(), theModel);
			}

			updateEditFields();
		}

	}

	private void commandRename() {
	}

	private void commandDelete() {
	}

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

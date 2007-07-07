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
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;

import de.mogwai.erdesignerng.model.Domain;
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

	private Domain currentEditingDomain;

	private DomainEditorView editingView = new DomainEditorView();

	/**
	 * @param aParent
	 */
	public DomainEditor(Model aModel, JFrame aParent) {
		super(aParent);

		initialize();

		domainListModel = new DefaultListModel();
		for (Domain theDomain : aModel.getDomains()) {
			domainListModel.addElement(theDomain);
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

		updateEditFields();
		editingView.getDeleteButton().setEnabled(false);
		editingView.getRenameButton().setEnabled(false);

		setResizable(false);
	}

	private void updateEditFields() {

		if (currentEditingDomain != null) {
			editingView.getDomainName().setText(currentEditingDomain.getName());
			editingView.getDeclaration().setEnabled(true);
			editingView.getDeclaration().setText(
					currentEditingDomain.getDatatype());
			editingView.getUpdateButton().setEnabled(true);
			editingView.getSequenced().setSelected(
					currentEditingDomain.isSequenced());
			editingView.getSequenced().setEnabled(true);
			editingView.getJavatype().setEnabled(true);
			editingView.getSql92type().setEnabled(true);
			editingView.getJavatype().setSelectedItem(
					currentEditingDomain.getJavaClassName());
		} else {
			editingView.getDomainName().setText(null);
			editingView.getDeclaration().setText(null);
			editingView.getUpdateButton().setEnabled(false);
			editingView.getDomainName().setEnabled(false);
			editingView.getDeclaration().setEnabled(false);
			editingView.getSequenced().setEnabled(false);
			editingView.getJavatype().setSelectedItem(null);
			editingView.getJavatype().setEnabled(false);
			editingView.getSql92type().setEnabled(false);
			editingView.getSql92type().setSelectedItem(null);
		}

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

		editingView.getSql92type().addItemListener(
				new java.awt.event.ItemListener() {

					public void itemStateChanged(java.awt.event.ItemEvent evt) {
						commandSQL92DatatypesItemStateChanged(evt);
					}
				});

		setContentPane(editingView);
		setTitle("Domain dictionary");
		pack();

	}

	private void commandClose() {

		setModalResult(DialogConstants.MODAL_RESULT_OK);
	}

	private void commandItemChanged(ListSelectionEvent e) {

		int index = editingView.getDomainList().getSelectedIndex();
		if (index >= 0) {
			currentEditingDomain = (Domain) domainListModel.get(index);
			updateEditFields();

			editingView.getDomainName().setEnabled(false);
			editingView.getDeclaration().setEnabled(true);
			editingView.getSql92type().setEnabled(true);
			editingView.getJavatype().setEnabled(true);

			editingView.getRenameButton().setEnabled(true);
			editingView.getDeleteButton().setEnabled(true);

		} else {
			editingView.getDomainName().setEnabled(false);
			editingView.getDeclaration().setEnabled(false);

			editingView.getRenameButton().setEnabled(false);
			editingView.getDeleteButton().setEnabled(false);
			editingView.getSql92type().setEnabled(false);

			currentEditingDomain = null;
			updateEditFields();
		}

	}

	private void commandNew() {
		currentEditingDomain = new Domain();
		updateEditFields();

		editingView.getDomainName().setEnabled(true);
		editingView.getDeclaration().setEnabled(true);
		editingView.getSql92type().setEnabled(true);
		editingView.getJavatype().setEnabled(true);
		editingView.getRenameButton().setEnabled(false);
		editingView.getDeleteButton().setEnabled(false);

		editingView.getUpdateButton().setEnabled(true);
	}

	private void commandUpdate() {
		String name = editingView.getDomainName().getText();
		String definition = editingView.getDeclaration().getText();

		if (name.trim().length() == 0) {
			JOptionPane.showMessageDialog(this, "Please enter a domain name",
					"Invalid domain name", JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (name.indexOf(' ') >= 0) {
			JOptionPane.showMessageDialog(this,
					"Please enter a domain name without blanks",
					"Invalid domain name", JOptionPane.ERROR_MESSAGE);
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

		if (!definition.equals(currentEditingDomain.getDatatype())) {

		}

		currentEditingDomain.setName(name);
		currentEditingDomain.setDatatype(definition);
		currentEditingDomain.setSequenced(editingView.getSequenced()
				.isSelected());
		currentEditingDomain.setJavaClassName(editingView.getJavatype()
				.getSelectedItem().toString());

		if (!domainListModel.contains(currentEditingDomain)) {
			domainListModel.addElement(currentEditingDomain);
			editingView.getDomainName().setEnabled(false);
		}

	}

	private void commandRename() {
	}

	private void commandDelete() {
	}

	/*
	 * DRH Pull down of what is (hopefully) generic SQL92 datatypes understood
	 * by ERDesigner supported DBs.
	 */
	private void commandSQL92DatatypesItemStateChanged(
			java.awt.event.ItemEvent evt) {
		String SQL92type = (String) editingView.getSql92type()
				.getSelectedItem();
		editingView.getDeclaration().setText(SQL92type);
	}
}

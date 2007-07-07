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
package de.mogwai.erdesignerng.visual.editor.table;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.event.ChangeEvent;

import de.mogwai.binding.BindingInfo;
import de.mogwai.erdesignerng.exception.ElementAlreadyExistsException;
import de.mogwai.erdesignerng.exception.ElementInvalidNameException;
import de.mogwai.erdesignerng.model.Attribute;
import de.mogwai.erdesignerng.model.DefaultValue;
import de.mogwai.erdesignerng.model.Domain;
import de.mogwai.erdesignerng.model.Model;
import de.mogwai.erdesignerng.model.Table;
import de.mogwai.erdesignerng.visual.editor.BaseEditor;

/**
 * @author Mirko Sertic
 */
public class TableEditor extends BaseEditor {

	private Model model;

	private TableEditorView editingView;

	private BindingInfo<Table> tableBindingInfo = new BindingInfo<Table>();

	private BindingInfo<Attribute> attributeBindingInfo = new BindingInfo<Attribute>();

	private DefaultListModel attributeListModel = new DefaultListModel();

	private DefaultListModel domainListModel = new DefaultListModel();

	private DefaultComboBoxModel defaultValuesListModel = new DefaultComboBoxModel();

	private Map<String, Attribute> knownValues = new HashMap<String, Attribute>();

	/**
	 * @param parent
	 */
	public TableEditor(Model aModel, JFrame aParent) {
		super(aParent);
		initialize();

		model = aModel;

		for (Domain theDomain : aModel.getDomains()) {
			domainListModel.addElement(theDomain);
		}
		editingView.getDomainList().setModel(domainListModel);

		for (DefaultValue theValue : aModel.getDefaultValues()) {
			defaultValuesListModel.addElement(theValue);
		}
		editingView.getDefault().setModel(defaultValuesListModel);

		tableBindingInfo.addBinding("name", editingView.getEntity_name(), true);
		tableBindingInfo.configure();

		attributeBindingInfo.addBinding("name", editingView.getAttributeName(),
				true);
		attributeBindingInfo.addBinding("domain", editingView.getDomainList(),
				true);
		attributeBindingInfo.addBinding("nullable", editingView.getNullable());
		attributeBindingInfo.addBinding("primaryKey", editingView
				.getPrimaryKey());
		attributeBindingInfo.addBinding("defaultValue", editingView
				.getDefault());
		attributeBindingInfo.configure();
	}

	/**
	 * This method initializes this.
	 */
	private void initialize() {

		editingView = new TableEditorView();
		editingView.getOkButton().addActionListener(
				new java.awt.event.ActionListener() {

					public void actionPerformed(java.awt.event.ActionEvent e) {
						commandOk();
					}
				});
		editingView.getCancelButton().addActionListener(
				new java.awt.event.ActionListener() {

					public void actionPerformed(java.awt.event.ActionEvent e) {
						setModalResult(MODAL_RESULT_CANCEL);
					}
				});
		editingView.getMainTabbedPane().addChangeListener(
				new javax.swing.event.ChangeListener() {

					public void stateChanged(javax.swing.event.ChangeEvent e) {
						commandTabStateChange(e);
					}
				});
		editingView.getAttributeList().addListSelectionListener(
				new javax.swing.event.ListSelectionListener() {

					public void valueChanged(
							javax.swing.event.ListSelectionEvent e) {
						commandAttributeListValueChanged(e);
					}
				});
		editingView.getUpButton().addActionListener(
				new java.awt.event.ActionListener() {

					public void actionPerformed(java.awt.event.ActionEvent e) {
						commandMoveAttributeUp(e);
					}
				});
		editingView.getDownButton().addActionListener(
				new java.awt.event.ActionListener() {

					public void actionPerformed(java.awt.event.ActionEvent e) {
						commandMoveAttributeDown(e);
					}
				});
		editingView.getNewButton().addActionListener(
				new java.awt.event.ActionListener() {

					public void actionPerformed(java.awt.event.ActionEvent e) {
						commandNewAttribute(e);
					}
				});
		editingView.getRenameButton().addActionListener(
				new java.awt.event.ActionListener() {

					public void actionPerformed(java.awt.event.ActionEvent e) {
						commandRenameAttribute(e);
					}
				});
		editingView.getDeleteButton().addActionListener(
				new java.awt.event.ActionListener() {

					public void actionPerformed(java.awt.event.ActionEvent e) {
						commandDeleteAttribute(e);
					}
				});
		editingView.getUpdateAttributeButton().addActionListener(
				new java.awt.event.ActionListener() {

					public void actionPerformed(java.awt.event.ActionEvent e) {
						commandUpdateAttribute(e);
					}
				});
		editingView.getPrimaryKey().addItemListener(
				new java.awt.event.ItemListener() {

					public void itemStateChanged(java.awt.event.ItemEvent e) {
						commandPrimaryKeyItemStateChanged(e);
					}
				});
		editingView.getUpdateIndexButton().addActionListener(
				new java.awt.event.ActionListener() {

					public void actionPerformed(java.awt.event.ActionEvent e) {
						commandUpdateIndex();
					}
				});
		editingView.getNewIndexButton().addActionListener(
				new java.awt.event.ActionListener() {

					public void actionPerformed(java.awt.event.ActionEvent e) {
						commandNewIndex();
					}
				});
		editingView.getRenameIndexButton().addActionListener(
				new java.awt.event.ActionListener() {

					public void actionPerformed(java.awt.event.ActionEvent e) {
						commandRenameIndex();
					}
				});
		editingView.getDeleteIndexButton().addActionListener(
				new java.awt.event.ActionListener() {

					public void actionPerformed(java.awt.event.ActionEvent e) {
						commandDeleteIndex();
					}
				});
		editingView.getIndexList().addListSelectionListener(
				new javax.swing.event.ListSelectionListener() {

					public void valueChanged(
							javax.swing.event.ListSelectionEvent e) {
						updateEditFields();
					}
				});
		editingView.getDomainDictionary().addActionListener(
				new java.awt.event.ActionListener() {

					public void actionPerformed(java.awt.event.ActionEvent e) {
						commandStartDomainEditor(e);
					}
				});

		setContentPane(editingView);

		setTitle("Entity editor");
		pack();

		editingView.getUpdateIndexButton().setEnabled(false);

	}

	public void initializeFor(Table aTable) {

		tableBindingInfo.setDefaultModel(aTable);
		tableBindingInfo.model2view();

		editingView.getEntity_name().setName(aTable.getName());
		for (Attribute theAttribute : aTable.getAttributes()) {

			Attribute theClone = theAttribute.clone();

			attributeListModel.addElement(theClone);
			knownValues.put(theClone.getName(), theClone);
		}
		editingView.getAttributeList().setModel(attributeListModel);

		updateEditFields();
	}

	private void commandOk() {
		if (tableBindingInfo.validate().size() == 0) {
			setModalResult(MODAL_RESULT_OK);
		}
	}

	private void commandTabStateChange(ChangeEvent e) {
	}

	private void commandUpdateAttribute(java.awt.event.ActionEvent evt) {
		Attribute theModel = attributeBindingInfo.getDefaultModel();
		if (attributeBindingInfo.validate().size() == 0) {
			attributeBindingInfo.view2model();

			if (!attributeListModel.contains(theModel)) {

				attributeListModel.addElement(theModel);
				knownValues.put(theModel.getName(), theModel);
			}

			updateEditFields();
		}
	}

	private void updateEditFields() {

		Attribute theValue = attributeBindingInfo.getDefaultModel();

		if (theValue != null) {

			boolean isNew = !attributeListModel.contains(theValue);

			editingView.getNewButton().setEnabled(true);
			editingView.getDeleteButton().setEnabled(!isNew);
			editingView.getRenameButton().setEnabled(!isNew);
			editingView.getDomainList().setEnabled(true);
			editingView.getAttributeName().setEnabled(isNew);
			editingView.getNullable().setEnabled(true);
			editingView.getDefault().setEnabled(true);
			editingView.getPrimaryKey().setEnabled(true);

		} else {
			editingView.getNewButton().setEnabled(true);
			editingView.getDeleteButton().setEnabled(false);
			editingView.getRenameButton().setEnabled(false);
			editingView.getDomainList().setEnabled(false);
			editingView.getAttributeName().setEnabled(false);
			editingView.getNullable().setEnabled(false);
			editingView.getDefault().setEnabled(false);
			editingView.getPrimaryKey().setEnabled(false);
		}

		attributeBindingInfo.model2view();

		editingView.getAttributeList().invalidate();
		editingView.getAttributeList().setSelectedValue(
				attributeBindingInfo.getDefaultModel(), true);

	}

	private void commandAttributeListValueChanged(
			javax.swing.event.ListSelectionEvent evt) {

		int index = editingView.getAttributeList().getSelectedIndex();
		if (index >= 0) {
			attributeBindingInfo.setDefaultModel((Attribute) attributeListModel
					.get(index));
		}

		updateEditFields();

	}

	private void commandRenameAttribute(java.awt.event.ActionEvent evt) {
	}

	private void commandDeleteAttribute(java.awt.event.ActionEvent evt) {
	}

	private void commandNewAttribute(java.awt.event.ActionEvent evt) {
		attributeBindingInfo.setDefaultModel(new Attribute());
		updateEditFields();
	}

	private void commandPrimaryKeyActionPerformed(java.awt.event.ActionEvent evt) {
	}

	private void commandNullableActionPerformed(java.awt.event.ActionEvent evt) {
	}

	private void commandMoveAttributeDown(java.awt.event.ActionEvent evt) {
	}

	private void commandMoveAttributeUp(java.awt.event.ActionEvent evt) {
	}

	private void commandPrimaryKeyItemStateChanged(java.awt.event.ItemEvent evt) {
	}

	private void commandDeleteIndex() {
	}

	private void commandRenameIndex() {
	}

	private void commandNewIndex() {
	}

	private void commandUpdateIndex() {
	}

	private void commandStartDomainEditor(ActionEvent e) {
	}

	public void applyValues() throws ElementAlreadyExistsException,
			ElementInvalidNameException {

		Table theTable = tableBindingInfo.getDefaultModel();
		tableBindingInfo.view2model();

		if (!model.getTables().contains(theTable)) {
			model.addTable(theTable);
		}

		for (String theKey : knownValues.keySet()) {
			Attribute theAttribute = knownValues.get(theKey);

			Attribute theExistantAttribute = theTable.getAttributes()
					.findByName(theKey);
			if (theExistantAttribute == null) {
				theTable.addAttribute(model, theAttribute);
			} else {
				theExistantAttribute.setName(theAttribute.getName());
				theExistantAttribute.setDefaultValue(theAttribute
						.getDefaultValue());
				theExistantAttribute.setPrimaryKey(theAttribute.isPrimaryKey());
				theExistantAttribute.setNullable(theAttribute.isNullable());
			}
		}

	}
}

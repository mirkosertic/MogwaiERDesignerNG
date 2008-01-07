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
package de.erdesignerng.visual.editor.table;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.event.ChangeEvent;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.exception.ElementAlreadyExistsException;
import de.erdesignerng.exception.ElementInvalidNameException;
import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.DefaultValue;
import de.erdesignerng.model.Domain;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.Table;
import de.erdesignerng.visual.editor.BaseEditor;
import de.mogwai.binding.BindingInfo;
import de.mogwai.common.client.looks.UIInitializer;
import de.mogwai.common.client.looks.components.action.ActionEventProcessor;
import de.mogwai.common.client.looks.components.action.DefaultAction;

/**
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2008-01-07 19:09:37 $
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
	
	private List<Attribute> removedAttributes = new ArrayList<Attribute>();

	private DefaultAction okAction = new DefaultAction(
			new ActionEventProcessor() {

				public void processActionEvent(ActionEvent e) {
					commandOk();
				}
			}, this, ERDesignerBundle.OK);

	private DefaultAction cancelAction = new DefaultAction(
			new ActionEventProcessor() {

				public void processActionEvent(ActionEvent e) {
					commandCancel();
				}
			}, this, ERDesignerBundle.CANCEL);

	private DefaultAction newAttributeAction = new DefaultAction(
			new ActionEventProcessor() {

				public void processActionEvent(ActionEvent e) {
					commandNewAttribute(e);
				}
			}, this, ERDesignerBundle.NEW);

	private DefaultAction deleteAttributeAction = new DefaultAction(
			new ActionEventProcessor() {

				public void processActionEvent(ActionEvent e) {
					commandDeleteAttribute(e);
				}
			}, this, ERDesignerBundle.DELETE);

	private DefaultAction updateAttribute = new DefaultAction(
			new ActionEventProcessor() {

				public void processActionEvent(ActionEvent e) {
					commandUpdateAttribute(e);
				}
			}, this, ERDesignerBundle.UPDATE);

	private DefaultAction newIndexAction = new DefaultAction(
			new ActionEventProcessor() {

				public void processActionEvent(ActionEvent e) {
					commandNewIndex();
				}
			}, this, ERDesignerBundle.NEW);

	private DefaultAction deleteIndexAction = new DefaultAction(
			new ActionEventProcessor() {

				public void processActionEvent(ActionEvent e) {
					commandDeleteIndex();
				}
			}, this, ERDesignerBundle.DELETE);

	private DefaultAction updateIndex = new DefaultAction(
			new ActionEventProcessor() {

				public void processActionEvent(ActionEvent e) {
					commandUpdateIndex();
				}
			}, this, ERDesignerBundle.UPDATE);
	
	/**
	 * @param parent
	 */
	public TableEditor(Model aModel, Component aParent) {
		super(aParent, ERDesignerBundle.ENTITYEDITOR);
		initialize();

		model = aModel;
		editingView.getAttributeList().setCellRenderer(
				new AttributeListCellRenderer());

		for (Domain theDomain : aModel.getDomains()) {
			domainListModel.addElement(theDomain);
		}
		editingView.getDomainList().setModel(domainListModel);

		for (DefaultValue theValue : aModel.getDefaultValues()) {
			defaultValuesListModel.addElement(theValue);
		}
		editingView.getDefault().setModel(defaultValuesListModel);

		tableBindingInfo.addBinding("name", editingView.getEntity_name(), true);
		tableBindingInfo.addBinding("comment", editingView.getEntityComment());
		tableBindingInfo.configure();

		attributeBindingInfo.addBinding("name", editingView.getAttributeName(),
				true);
		attributeBindingInfo.addBinding("comment", editingView
				.getAttributeComment());
		attributeBindingInfo.addBinding("domain", editingView.getDomainList(),
				true);
		attributeBindingInfo.addBinding("nullable", editingView.getNullable());
		attributeBindingInfo.addBinding("primaryKey", editingView
				.getPrimaryKey());
		attributeBindingInfo.addBinding("defaultValue", editingView
				.getDefault());
		attributeBindingInfo.configure();
		
		UIInitializer.getInstance().initialize(this);		
	}
	
	/**
	 * This method initializes this.
	 */
	private void initialize() {

		editingView = new TableEditorView();
		editingView.getOkButton().setAction(okAction);
		editingView.getCancelButton().setAction(cancelAction);
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
		editingView.getNewButton().setAction(newAttributeAction);
		editingView.getDeleteButton().setAction(deleteAttributeAction);
		editingView.getUpdateAttributeButton().setAction(updateAttribute);
		editingView.getPrimaryKey().addItemListener(
				new java.awt.event.ItemListener() {

					public void itemStateChanged(java.awt.event.ItemEvent e) {
						commandPrimaryKeyItemStateChanged(e);
					}
				});
		editingView.getUpdateIndexButton().setAction(updateIndex);
		editingView.getNewIndexButton().setAction(newIndexAction);
		editingView.getDeleteIndexButton().setAction(deleteIndexAction);
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
			knownValues.put(theClone.getSystemId(), theClone);
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
		Vector theValidationResult = attributeBindingInfo.validate();
		if (theValidationResult.size() == 0) {
			attributeBindingInfo.view2model();

			if (!attributeListModel.contains(theModel)) {

				attributeListModel.addElement(theModel);
				knownValues.put(theModel.getSystemId(), theModel);
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
			editingView.getDomainList().setEnabled(true);
			editingView.getAttributeName().setEnabled(true);
			editingView.getNullable().setEnabled(true);
			editingView.getDefault().setEnabled(true);
			editingView.getPrimaryKey().setEnabled(true);

		} else {
			editingView.getNewButton().setEnabled(true);
			editingView.getDeleteButton().setEnabled(false);
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
		//TODO: Implement functionality here
	}

	private void commandDeleteAttribute(java.awt.event.ActionEvent aEvent) {

		Attribute theAttribute = attributeBindingInfo.getDefaultModel();
		
		if (!model.checkIfUsedAsForeignKey(tableBindingInfo.getDefaultModel(), theAttribute)) {

			if (displayQuestionMessage(ERDesignerBundle.DOYOUREALLYWANTTODELETE)) {
				knownValues.remove(theAttribute.getSystemId());
				attributeListModel.removeElement(theAttribute);
				
				removedAttributes.add(theAttribute);
			}
		} else {
			displayErrorMessage(getResourceHelper().getText(ERDesignerBundle.ATTRIBUTEISUSEDINFOREIGNKEYS));
		}
	}

	private void commandNewAttribute(java.awt.event.ActionEvent evt) {
		attributeBindingInfo.setDefaultModel(new Attribute());
		updateEditFields();
	}

	private void commandPrimaryKeyActionPerformed(java.awt.event.ActionEvent evt) {
		//TODO: Implement functionality here
	}

	private void commandNullableActionPerformed(java.awt.event.ActionEvent evt) {
		//TODO: Implement functionality here
	}

	private void commandMoveAttributeDown(java.awt.event.ActionEvent evt) {
		//TODO: Implement functionality here
	}

	private void commandMoveAttributeUp(java.awt.event.ActionEvent evt) {
		//TODO: Implement functionality here
	}

	private void commandPrimaryKeyItemStateChanged(java.awt.event.ItemEvent evt) {
		//TODO: Implement functionality here
	}

	private void commandDeleteIndex() {
		//TODO: Implement functionality here
	}

	private void commandRenameIndex() {
		//TODO: Implement functionality here
	}

	private void commandNewIndex() {
		//TODO: Implement functionality here
	}

	private void commandUpdateIndex() {
		//TODO: Implement functionality here
	}

	private void commandStartDomainEditor(ActionEvent e) {
		//TODO: Implement functionality here
	}

	@Override
	public void applyValues() throws ElementAlreadyExistsException,
			ElementInvalidNameException {

		Table theTable = tableBindingInfo.getDefaultModel();
		tableBindingInfo.view2model();

		if (!model.getTables().contains(theTable)) {
			model.addTable(theTable);
		} else {
			for(Attribute theAttribute : removedAttributes) {
				theTable.getAttributes().removeById(theAttribute.getSystemId());
			}
		}

		for (String theKey : knownValues.keySet()) {
			Attribute theAttribute = knownValues.get(theKey);

			Attribute theExistantAttribute = theTable.getAttributes()
					.findBySystemId(theKey);
			if (theExistantAttribute == null) {
				theTable.addAttribute(model, theAttribute);
			} else {
				try {
					theExistantAttribute.restoreFrom(theAttribute);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}
}

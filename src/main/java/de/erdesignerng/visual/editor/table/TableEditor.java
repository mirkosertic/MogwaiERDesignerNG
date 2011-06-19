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

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.dialect.DataType;
import de.erdesignerng.dialect.Dialect;
import de.erdesignerng.dialect.IndexProperties;
import de.erdesignerng.dialect.TableProperties;
import de.erdesignerng.exception.ElementAlreadyExistsException;
import de.erdesignerng.exception.ElementInvalidNameException;
import de.erdesignerng.model.*;
import de.erdesignerng.modificationtracker.VetoException;
import de.erdesignerng.visual.MessagesHelper;
import de.erdesignerng.visual.editor.BaseEditor;
import de.erdesignerng.visual.editor.NullsafeSpinnerEditor;
import de.erdesignerng.visual.editor.NullsafeSpinnerModel;
import de.erdesignerng.visual.scaffolding.ScaffoldingUtils;
import de.erdesignerng.visual.scaffolding.ScaffoldingWrapper;
import de.mogwai.common.client.binding.BindingInfo;
import de.mogwai.common.client.binding.adapter.RadioButtonAdapter;
import de.mogwai.common.client.binding.validator.ValidationError;
import de.mogwai.common.client.looks.UIInitializer;
import de.mogwai.common.client.looks.components.DefaultTabbedPaneTab;
import de.mogwai.common.client.looks.components.action.ActionEventProcessor;
import de.mogwai.common.client.looks.components.action.DefaultAction;
import de.mogwai.common.client.looks.components.list.DefaultListModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-13 15:40:33 $
 */
public class TableEditor extends BaseEditor {

	private final Model model;

	private TableEditorView editingView;

	private final BindingInfo<Table> tableBindingInfo = new BindingInfo<Table>();

	private final BindingInfo<Attribute> attributeBindingInfo = new BindingInfo<Attribute>();

	private final BindingInfo<Index> indexBindingInfo = new BindingInfo<Index>();

	private final BindingInfo<IndexValueModel> indexExpressionBindingInfo = new BindingInfo<IndexValueModel>();

	private final BindingInfo<IndexValueModel> indexExpressionBindingInfo2 = new BindingInfo<IndexValueModel>();

	private final DefaultListModel attributeListModel;

	private final DefaultListModel indexListModel;

	private final Map<String, Attribute> knownAttributeValues = new HashMap<String, Attribute>();

	private final Map<String, Index> knownIndexValues = new HashMap<String, Index>();

	private final List<Attribute> removedAttributes = new ArrayList<Attribute>();

	private final List<Index> removedIndexes = new ArrayList<Index>();

	private final DefaultAction newAttributeAction = new DefaultAction(new ActionEventProcessor() {

		@Override
		public void processActionEvent(ActionEvent e) {
			commandNewAttribute();
		}
	}, this, ERDesignerBundle.NEW);

	private final DefaultAction deleteAttributeAction = new DefaultAction(new ActionEventProcessor() {

		@Override
		public void processActionEvent(ActionEvent e) {
			commandDeleteAttribute(e);
		}
	}, this, ERDesignerBundle.DELETE);

	private final DefaultAction updateAttribute = new DefaultAction(new ActionEventProcessor() {

		@Override
		public void processActionEvent(ActionEvent e) {
			commandUpdateAttribute();
		}
	}, this, ERDesignerBundle.UPDATE);

	private final DefaultAction newIndexAction = new DefaultAction(new ActionEventProcessor() {

		@Override
		public void processActionEvent(ActionEvent e) {
			commandNewIndex();
		}
	}, this, ERDesignerBundle.NEW);

	private final DefaultAction deleteIndexAction = new DefaultAction(new ActionEventProcessor() {

		@Override
		public void processActionEvent(ActionEvent e) {
			commandDeleteIndex();
		}
	}, this, ERDesignerBundle.DELETE);

	private final DefaultAction updateIndex = new DefaultAction(new ActionEventProcessor() {

		@Override
		public void processActionEvent(ActionEvent e) {
			commandUpdateIndex();
		}
	}, this, ERDesignerBundle.UPDATE);

	private final DefaultAction addIndexAttribute = new DefaultAction(new ActionEventProcessor() {

		@Override
		public void processActionEvent(ActionEvent e) {
			commandAddIndexAttribute();
		}
	}, this, ERDesignerBundle.NEWONLYICON);

	private final DefaultAction addIndexExpression = new DefaultAction(new ActionEventProcessor() {

		@Override
		public void processActionEvent(ActionEvent e) {
			commandAddIndexExpression();
		}
	}, this, ERDesignerBundle.NEWONLYICON);

	private final DefaultAction removeIndexElement = new DefaultAction(new ActionEventProcessor() {

		@Override
		public void processActionEvent(ActionEvent e) {
			commandRemoveIndexElement();
		}
	}, this, ERDesignerBundle.DELETEONLYICON);

	private TableProperties tableProperties;

	private ScaffoldingWrapper tablePropertiesWrapper;

	private IndexProperties indexProperties;

	private ScaffoldingWrapper indexPropertiesWrapper;

	public TableEditor(Model aModel, Component aParent) {
		super(aParent, ERDesignerBundle.ENTITYEDITOR);
		initialize();

		editingView.getSizeSpinner().setModel(new NullsafeSpinnerModel());
		editingView.getSizeSpinner().setEditor(new NullsafeSpinnerEditor(editingView.getSizeSpinner()));

		DefaultComboBoxModel theDataTypes = new DefaultComboBoxModel();
		for (DataType theType : aModel.getAvailableDataTypes()) {
			theDataTypes.addElement(theType);
		}

		editingView.getDataType().setModel(theDataTypes);
		editingView.getDataType().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				DataType theType = (DataType) editingView.getDataType().getSelectedItem();
				setSpinnerState(theType);
				if (theType != null && theType.isDomain()) {
					editingView.getNullable().setSelected(((Domain) theType).isNullable());
				}
			}

		});

		attributeListModel = editingView.getAttributeList().getModel();
		indexListModel = editingView.getIndexList().getModel();

		model = aModel;
		editingView.getAttributeList().setCellRenderer(new AttributeListCellRenderer(this));

		tableBindingInfo.addBinding("name", editingView.getEntityName(), true);
		tableBindingInfo.addBinding("comment", editingView.getEntityComment());
		tableBindingInfo.configure();

		attributeBindingInfo.addBinding("name", editingView.getAttributeName(), true);
		attributeBindingInfo.addBinding("comment", editingView.getAttributeComment());
		attributeBindingInfo.addBinding("nullable", editingView.getNullable());
		attributeBindingInfo.addBinding("defaultValue", editingView.getDefault());
		attributeBindingInfo.addBinding("datatype", editingView.getDataType(), true);
		attributeBindingInfo.addBinding("size", editingView.getSizeSpinner());
		attributeBindingInfo.addBinding("fraction", editingView.getFractionSpinner(), true);
		attributeBindingInfo.addBinding("scale", editingView.getScaleSpinner(), true);
		attributeBindingInfo.addBinding("defaultValue", editingView.getDefault());
		attributeBindingInfo.addBinding("extra", editingView.getExtra());
		attributeBindingInfo.configure();

		indexBindingInfo.addBinding("name", editingView.getIndexName(), true);
		indexBindingInfo.addBinding("attributes", new IndexAttributesPropertyAdapter(editingView.getIndexFieldList()
		));

		RadioButtonAdapter theAdapter = new RadioButtonAdapter();
		theAdapter.addMapping(IndexType.PRIMARYKEY, editingView.getPrimaryIndex());
		theAdapter.addMapping(IndexType.UNIQUE, editingView.getUniqueIndex());
		theAdapter.addMapping(IndexType.NONUNIQUE, editingView.getNotUniqueIndex());
		indexBindingInfo.addBinding("indexType", theAdapter);
		indexBindingInfo.configure();

		indexExpressionBindingInfo.addBinding("expression", editingView.getIndexExpression(), true);
		indexExpressionBindingInfo.configure();

		indexExpressionBindingInfo2.addBinding("attribute", editingView.getIndexAttribute(), true);
		indexExpressionBindingInfo2.configure();

		UIInitializer.getInstance().initialize(this);
	}

	/**
	 * This method initializes this.
	 */
	private void initialize() {

		editingView = new TableEditorView();
		editingView.getOkButton().setAction(okAction);
		editingView.getCancelButton().setAction(cancelAction);
		editingView.getMainTabbedPane().addChangeListener(new javax.swing.event.ChangeListener() {

			@Override
			public void stateChanged(javax.swing.event.ChangeEvent e) {
				commandTabStateChange();
			}
		});
		editingView.getAttributeList().addListSelectionListener(new javax.swing.event.ListSelectionListener() {

			@Override
			public void valueChanged(javax.swing.event.ListSelectionEvent e) {
				commandAttributeListValueChanged(e);
			}
		});
		editingView.getNewButton().setAction(newAttributeAction);
		editingView.getDeleteButton().setAction(deleteAttributeAction);
		editingView.getUpdateAttributeButton().setAction(updateAttribute);
		editingView.getUpdateIndexButton().setAction(updateIndex);
		editingView.getNewIndexButton().setAction(newIndexAction);
		editingView.getDeleteIndexButton().setAction(deleteIndexAction);
		editingView.getIndexList().addListSelectionListener(new javax.swing.event.ListSelectionListener() {

			@Override
			public void valueChanged(javax.swing.event.ListSelectionEvent e) {
				commandIndexListValueChanged(e);
			}
		});

		editingView.getIndexFieldList().addListSelectionListener(new javax.swing.event.ListSelectionListener() {

			@Override
			public void valueChanged(javax.swing.event.ListSelectionEvent e) {
				commandIndexFieldListValueChanged(e);
			}
		});

		editingView.getAddIndexAttribute().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				updateIndexStatusFields();
			}

		});

		editingView.getAddIndexExpression().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				updateIndexStatusFields();
			}

		});

		editingView.getRemoveFromIndexButton().setAction(removeIndexElement);
		editingView.getAddAttributeToIndexButton().setAction(addIndexAttribute);
		editingView.getAddExpressionToIndexButton().setAction(addIndexExpression);

		removeIndexElement.setEnabled(false);

		setContentPane(editingView);

		pack();

		editingView.getUpdateIndexButton().setEnabled(false);
	}

	public void initializeFor(Table aTable) {

		tableBindingInfo.setDefaultModel(aTable);
		tableBindingInfo.model2view();

		editingView.getEntityName().setName(aTable.getName());
		for (Attribute theAttribute : aTable.getAttributes()) {

			Attribute theClone = theAttribute.clone();

			attributeListModel.add(theClone);
			knownAttributeValues.put(theClone.getSystemId(), theClone);
		}
		for (Index theIndex : aTable.getIndexes()) {

			Index theClone = theIndex.clone();

			indexListModel.add(theClone);
			knownIndexValues.put(theClone.getSystemId(), theClone);
		}

		updateAttributeEditFields();

		tableProperties = model.getDialect().createTablePropertiesFor(aTable);
		DefaultTabbedPaneTab theTab = editingView.getTablePropertiesTab();
		tablePropertiesWrapper = ScaffoldingUtils.createScaffoldingPanelFor(model, tableProperties);
		theTab.add(tablePropertiesWrapper.getComponent(), BorderLayout.CENTER);
		if (!tablePropertiesWrapper.hasComponents()) {
			editingView.disableTablePropertiesTab();
		} else {
			UIInitializer.getInstance().initialize(theTab);
		}

		commandNewAttribute();
	}

	@Override
	protected void commandOk() {

		if (attributeBindingInfo.isChanged()) {
			MessagesHelper.displayErrorMessage(this, getResourceHelper().getFormattedText(
					ERDesignerBundle.SAVEATTRIBUTECHANGESFIRST));
			return;
		}

		if (editingView.getAddExpressionToIndexButton().isEnabled() && indexExpressionBindingInfo.isChanged()) {
			MessagesHelper.displayErrorMessage(this, getResourceHelper().getFormattedText(
					ERDesignerBundle.SAVEINDEXCHANGESFIRST));
			return;
		}

		if (editingView.getAddAttributeToIndexButton().isEnabled() && indexExpressionBindingInfo2.isChanged()) {
			MessagesHelper.displayErrorMessage(this, getResourceHelper().getFormattedText(
					ERDesignerBundle.SAVEINDEXCHANGESFIRST));
			return;
		}

		if (indexBindingInfo.isChanged()) {
			MessagesHelper.displayErrorMessage(this, getResourceHelper().getFormattedText(
					ERDesignerBundle.SAVEINDEXCHANGESFIRST));
			return;
		}

		if (tableBindingInfo.validate().isEmpty()) {
			if (attributeListModel.getSize() == 0) {
				MessagesHelper.displayErrorMessage(this, getResourceHelper().getText(
						ERDesignerBundle.TABLEMUSTHAVEATLEASTONEATTRIBUTE));
				return;
			}
			try {
				model.checkName(editingView.getEntityName().getText());
			} catch (ElementInvalidNameException e) {
				MessagesHelper.displayErrorMessage(this, getResourceHelper().getText(ERDesignerBundle.NAMEINVALID));
				return;
			}

			setModalResult(MODAL_RESULT_OK);
		}
	}

	private void commandTabStateChange() {
		int theIndex = editingView.getMainTabbedPane().getSelectedIndex();
		if (theIndex == 0) {
			commandNewAttribute();
		}
		if (theIndex == 1) {
			commandNewIndex();
		}
	}

	private void commandUpdateAttribute() {
		Attribute theAttribute = attributeBindingInfo.getDefaultModel();
		List<ValidationError> theValidationResult = attributeBindingInfo.validate();
		if (theValidationResult.isEmpty()) {

			Dialect theDialect = model.getDialect();

			for (int i = 0; i < attributeListModel.getSize(); i++) {
				Attribute theTempAttribute = (Attribute) attributeListModel.get(i);
				try {
					if (theDialect.checkName(theTempAttribute.getName()).equals(
							theDialect.checkName(editingView.getAttributeName().getText()))
							&& !theTempAttribute.getSystemId().equals(theAttribute.getSystemId())) {
						MessagesHelper.displayErrorMessage(this, getResourceHelper().getText(
								ERDesignerBundle.ATTRIBUTEALREADYEXISTS));
						return;
					}
				} catch (ElementInvalidNameException e) {
					MessagesHelper.displayErrorMessage(this, getResourceHelper().getText(ERDesignerBundle.NAMEINVALID));
					return;
				}
			}

			attributeBindingInfo.view2model();

			if (!attributeListModel.contains(theAttribute)) {

				attributeListModel.add(theAttribute);
				knownAttributeValues.put(theAttribute.getSystemId(), theAttribute);
			}

			updateAttributeEditFields();
		}
	}

	private void setSpinnerState(DataType aValue) {
		if (aValue != null) {
			editingView.getSizeSpinner().setEnabled(aValue.supportsSize() && !aValue.isDomain());
			editingView.getFractionSpinner().setEnabled(aValue.supportsFraction() && !aValue.isDomain());
			editingView.getScaleSpinner().setEnabled(aValue.supportsScale() && !aValue.isDomain());
		} else {
			editingView.getSizeSpinner().setEnabled(false);
			editingView.getFractionSpinner().setEnabled(false);
			editingView.getScaleSpinner().setEnabled(false);
		}
	}

	private void updateAttributeEditFields() {

		Attribute theValue = attributeBindingInfo.getDefaultModel();

		if (theValue != null) {
			DataType theDataType = theValue.getDatatype();

			boolean isNew = !attributeListModel.contains(theValue);

			editingView.getNewButton().setEnabled(true);
			editingView.getDeleteButton().setEnabled(!isNew);
			editingView.getAttributeName().setEnabled(true);
			editingView.getNullable().setEnabled(true);
			editingView.getDefault().setEnabled(true);
			if (model.getDialect() != null) {
				editingView.getExtra().setEnabled(model.getDialect().isSupportsColumnExtra());
			} else {
				editingView.getExtra().setEnabled(false);
			}
			editingView.getDataType().setEnabled(true);
			setSpinnerState(theDataType);

		} else {
			editingView.getNewButton().setEnabled(true);
			editingView.getDeleteButton().setEnabled(false);
			editingView.getAttributeName().setEnabled(false);
			editingView.getNullable().setEnabled(false);
			editingView.getDefault().setEnabled(false);
			editingView.getExtra().setEnabled(false);
			editingView.getDataType().setEnabled(false);
			setSpinnerState(null);
		}

		attributeBindingInfo.model2view();

		editingView.getAttributeList().invalidate();
		editingView.getAttributeList().setSelectedValue(attributeBindingInfo.getDefaultModel(), true);

	}

	private void updateIndexEditFields() {
		Index theValue = indexBindingInfo.getDefaultModel();

		if (theValue != null) {

			boolean isNew = !indexListModel.contains(theValue);

			editingView.getNewIndexButton().setEnabled(true);
			editingView.getUpdateIndexButton().setEnabled(true);

			indexBindingInfo.setEnabled(true);

			editingView.getIndexFieldList().setEnabled(true);
			editingView.getPrimaryIndex().setEnabled(true);
			editingView.getUniqueIndex().setEnabled(true);
			editingView.getNotUniqueIndex().setEnabled(true);

			editingView.getDeleteIndexButton().setEnabled(!isNew);
		} else {

			editingView.getNewIndexButton().setEnabled(true);
			editingView.getDeleteIndexButton().setEnabled(false);
			editingView.getUpdateIndexButton().setEnabled(false);

			editingView.getIndexFieldList().setEnabled(false);

			indexBindingInfo.setEnabled(false);
		}

		DefaultComboBoxModel theAttModel = new DefaultComboBoxModel();
		for (int i = 0; i < attributeListModel.getSize(); i++) {
			theAttModel.addElement(attributeListModel.get(i));
		}
		editingView.getIndexAttribute().setModel(theAttModel);

		updateIndexStatusFields();

		indexBindingInfo.model2view();

		editingView.getIndexList().invalidate();
		editingView.getIndexList().setSelectedValue(indexBindingInfo.getDefaultModel(), true);
	}

	private void updateIndexStatusFields() {

		boolean theCurrentIndexSelected = indexBindingInfo.getDefaultModel() != null;
		editingView.getAddIndexAttribute().setEnabled(theCurrentIndexSelected);
		editingView.getAddIndexExpression().setEnabled(theCurrentIndexSelected);

		boolean theEnabled = editingView.getAddIndexAttribute().isSelected() && theCurrentIndexSelected;
		editingView.getIndexAttribute().setEnabled(theEnabled);
		editingView.getAddAttributeToIndexButton().setEnabled(theEnabled);

		IndexValueModel theModel = new IndexValueModel();

		if (theEnabled) {
			indexExpressionBindingInfo2.setDefaultModel(theModel);
			indexExpressionBindingInfo2.model2view();
		}

		theEnabled = editingView.getAddIndexExpression().isSelected() && theCurrentIndexSelected;
		editingView.getIndexExpression().setEnabled(theEnabled);
		editingView.getAddExpressionToIndexButton().setEnabled(theEnabled);
		if (theEnabled) {
			indexExpressionBindingInfo.setDefaultModel(theModel);
			indexExpressionBindingInfo.model2view();
		}

	}

	private void commandAttributeListValueChanged(javax.swing.event.ListSelectionEvent evt) {

		int index = editingView.getAttributeList().getSelectedIndex();
		if (index >= 0) {
			attributeBindingInfo.setDefaultModel((Attribute) attributeListModel.get(index));
		}

		updateAttributeEditFields();
	}

	private void commandIndexFieldListValueChanged(javax.swing.event.ListSelectionEvent evt) {

		removeIndexElement.setEnabled(editingView.getIndexFieldList().getSelectedValue() != null);

		updateIndexStatusFields();
	}

	private void commandIndexListValueChanged(javax.swing.event.ListSelectionEvent evt) {

		int index = editingView.getIndexList().getSelectedIndex();
		if (index >= 0) {
			indexBindingInfo.setDefaultModel((Index) indexListModel.get(index));

			updateIndexProperties();
		}

		updateIndexEditFields();
	}

	private void updateIndexProperties() {

		Index theIndex = indexBindingInfo.getDefaultModel();

		indexProperties = model.getDialect().createIndexPropertiesFor(theIndex);
		DefaultTabbedPaneTab theTab = editingView.getIndexPropertiesTab();
		indexPropertiesWrapper = ScaffoldingUtils.createScaffoldingPanelFor(model, indexProperties);
		theTab.removeAll();

		theTab.add(indexPropertiesWrapper.getComponent(), BorderLayout.CENTER);
		if (!indexPropertiesWrapper.hasComponents()) {
			editingView.disableIndexPropertiesTab();
		} else {
			editingView.enableIndexPropertiesTab();
		}

		UIInitializer.getInstance().initialize(editingView.getIndexTabbedPane());

	}

	private void commandDeleteAttribute(java.awt.event.ActionEvent aEvent) {

		Attribute theAttribute = attributeBindingInfo.getDefaultModel();

		if (model.checkIfUsedAsForeignKey(tableBindingInfo.getDefaultModel(), theAttribute)) {

			MessagesHelper.displayErrorMessage(this, getResourceHelper().getText(
					ERDesignerBundle.ATTRIBUTEISUSEDINFOREIGNKEYS));

			return;
		}

		if (isUsedInIndex(theAttribute)) {

			MessagesHelper.displayErrorMessage(this, getResourceHelper().getText(
					ERDesignerBundle.ATTRIBUTEISUSEDININDEX));

			return;
		}

		if (MessagesHelper.displayQuestionMessage(this, ERDesignerBundle.DOYOUREALLYWANTTODELETE)) {
			knownAttributeValues.remove(theAttribute.getSystemId());
			attributeListModel.remove(theAttribute);

			removedAttributes.add(theAttribute);
		}
	}

	private void commandNewAttribute() {
		attributeBindingInfo.setDefaultModel(new Attribute());
		updateAttributeEditFields();

		editingView.getAttributeList().clearSelection();
	}

	private void commandDeleteIndex() {
		Index theAttribute = indexBindingInfo.getDefaultModel();

		if (MessagesHelper.displayQuestionMessage(this, ERDesignerBundle.DOYOUREALLYWANTTODELETE)) {
			knownIndexValues.remove(theAttribute.getSystemId());
			indexListModel.remove(theAttribute);

			removedIndexes.add(theAttribute);
		}
	}

	private void commandNewIndex() {
		indexBindingInfo.setDefaultModel(new Index());

		updateIndexProperties();

		updateIndexEditFields();

		editingView.getIndexList().clearSelection();
	}

	private void commandUpdateIndex() {
		Index theIndex = indexBindingInfo.getDefaultModel();
		List<ValidationError> theValidationResult = indexBindingInfo.validate();
		if (theValidationResult.isEmpty()) {

			Dialect theDialect = model.getDialect();
			for (int i = 0; i < indexListModel.getSize(); i++) {
				Index theTempIndex = (Index) indexListModel.get(i);
				try {
					if (theDialect.checkName(theTempIndex.getName()).equals(
							theDialect.checkName(editingView.getIndexName().getText()))
							&& !theTempIndex.getSystemId().equals(theIndex.getSystemId())) {
						MessagesHelper.displayErrorMessage(this, getResourceHelper().getText(
								ERDesignerBundle.INDEXALREADYEXISTS));
						return;
					}
				} catch (ElementInvalidNameException e) {
					MessagesHelper.displayErrorMessage(this, getResourceHelper().getText(ERDesignerBundle.NAMEINVALID));
					return;
				}
			}

			indexBindingInfo.view2model();

			if (theIndex.getIndexType().equals(IndexType.PRIMARYKEY)) {
				for (int i = 0; i < indexListModel.getSize(); i++) {
					Index theTempIndex = (Index) indexListModel.get(i);
					if ((theTempIndex.getIndexType().equals(IndexType.PRIMARYKEY) && (!theIndex.equals(theTempIndex)))) {
						MessagesHelper.displayErrorMessage(this, getResourceHelper().getText(
								ERDesignerBundle.THEREISALREADYAPRIMARYKEY));
						return;
					}
				}
			}

			DefaultListModel<IndexExpression> theListModel = editingView.getIndexFieldList().getModel();
			if (theListModel.getSize() == 0) {
				MessagesHelper.displayErrorMessage(this, getResourceHelper().getText(
						ERDesignerBundle.ANINDEXMUSTHAVEATLEASTONEELEMENT));
				return;
			}

			if (!indexListModel.contains(theIndex)) {

				indexListModel.add(theIndex);
				knownIndexValues.put(theIndex.getSystemId(), theIndex);
			}

			theIndex.getExpressions().clear();
			for (int i = 0; i < theListModel.getSize(); i++) {
				theIndex.getExpressions().add(theListModel.get(i));
			}

			updateIndexEditFields();

			indexPropertiesWrapper.save();
			indexProperties.copyTo(theIndex);
		}
	}

	private void commandAddIndexExpression() {
		if (indexExpressionBindingInfo.validate().isEmpty() && indexBindingInfo.validate().isEmpty()) {

			indexBindingInfo.view2model();

			indexExpressionBindingInfo.view2model();
			IndexValueModel theValue = indexExpressionBindingInfo.getDefaultModel();

			Index theCurrentModel = indexBindingInfo.getDefaultModel();

			theCurrentModel.getExpressions().addExpressionFor(theValue.getExpression());

			indexBindingInfo.model2view();
			indexBindingInfo.markChanged();
		}
	}

	private void commandAddIndexAttribute() {
		if (indexExpressionBindingInfo2.validate().isEmpty() && indexBindingInfo.validate().isEmpty()) {

			indexBindingInfo.view2model();

			indexExpressionBindingInfo2.view2model();
			IndexValueModel theValue = indexExpressionBindingInfo2.getDefaultModel();

			Index theCurrentModel = indexBindingInfo.getDefaultModel();

			try {
				theCurrentModel.getExpressions().addExpressionFor(theValue.getAttribute());

				indexBindingInfo.model2view();
				indexBindingInfo.markChanged();
			} catch (ElementAlreadyExistsException e) {
				MessagesHelper.displayErrorMessage(this, getResourceHelper().getText(
						ERDesignerBundle.ATTRIBUTEALREADYPARTOFINDEX));
			}
		}
	}

	private void commandRemoveIndexElement() {
		if (indexBindingInfo.validate().isEmpty()) {

			if (MessagesHelper.displayQuestionMessage(this, ERDesignerBundle.DOYOUREALLYWANTTODELETE)) {

				indexBindingInfo.view2model();

				Index theCurrentModel = indexBindingInfo.getDefaultModel();
				theCurrentModel.getExpressions().remove(editingView.getIndexFieldList().getSelectedValue());

				indexBindingInfo.model2view();
				indexBindingInfo.markChanged();

				removeIndexElement.setEnabled(false);
			}
		}
	}

	@Override
	public void applyValues() throws ElementAlreadyExistsException, ElementInvalidNameException, VetoException {

		Table theTable = tableBindingInfo.getDefaultModel();

		tablePropertiesWrapper.save();
		tableProperties.copyTo(theTable);

		if (!model.getTables().contains(theTable)) {

			tableBindingInfo.view2model();

			// The table is new, so just add it
			for (int i = 0; i < attributeListModel.getSize(); i++) {
				theTable.addAttribute(model, (Attribute) attributeListModel.get(i));
			}

			for (int i = 0; i < indexListModel.getSize(); i++) {
				theTable.addIndex(model, (Index) indexListModel.get(i));
			}

			model.addTable(theTable);
		} else {
			// The table exists already in the model
			Table theTempTable = new Table();

			tableBindingInfo.setDefaultModel(theTempTable);
			tableBindingInfo.view2model();

			// Check if it was renamed and issue the required commands
			if (theTable.isRenamed(theTempTable.getName())) {
				model.renameTable(theTable, theTempTable.getName());
			}

			// Check if the comment was changed and issue the required commands
			if (theTable.isCommentChanged(theTempTable.getComment())) {
				model.changeTableComment(theTable, theTempTable.getComment());
			}

			// Remove the removed attributes
			for (Attribute theAttribute : removedAttributes) {
				model.removeAttributeFromTable(theTable, theAttribute);
			}

			// Remove the removed indexes
			for (Index theIndex : removedIndexes) {
				model.removeIndex(theTable, theIndex);
			}

			// And finally check all attributes if they are new or were renamed
			// or modified
			for (String theKey : knownAttributeValues.keySet()) {
				Attribute theAttribute = knownAttributeValues.get(theKey);

				Attribute theExistingAttribute = theTable.getAttributes().findBySystemId(theKey);
				if (theExistingAttribute == null) {
					model.addAttributeToTable(theTable, theAttribute);
				} else {
					try {
						if (theExistingAttribute.isRenamed(theAttribute)) {
							model.renameAttribute(theExistingAttribute, theAttribute.getName());
						} else {
							if (theExistingAttribute.isModified(theAttribute, false)) {
								model.changeAttribute(theExistingAttribute, theAttribute);
							}
						}
					} catch (VetoException e1) {
						throw e1;
					} catch (Exception e) {
						logFatalError(e);
					}
				}
			}

			for (String theKey : knownIndexValues.keySet()) {
				Index theIndex = knownIndexValues.get(theKey);

				Index theExistingIndex = theTable.getIndexes().findBySystemId(theKey);
				if (theExistingIndex == null) {
					model.addIndexToTable(theTable, theIndex);
				} else {
					try {
						if (theExistingIndex.isModified(theIndex, false)) {
							model.changeIndex(theExistingIndex, theIndex);
						}
					} catch (VetoException e1) {
						throw e1;
					} catch (Exception e) {
						logFatalError(e);
					}
				}
			}
		}
	}

	boolean isPrimaryKey(Attribute aAttribute) {
		for (int i = 0; i < indexListModel.getSize(); i++) {
			Index theIndex = (Index) indexListModel.get(i);
			if (IndexType.PRIMARYKEY.equals(theIndex.getIndexType())) {
				return theIndex.getExpressions().findByAttribute(aAttribute) != null;
			}
		}
		return false;
	}

	private boolean isUsedInIndex(Attribute aAttribute) {
		for (int i = 0; i < indexListModel.getSize(); i++) {
			Index theIndex = (Index) indexListModel.get(i);
			if (theIndex.getExpressions().findByAttribute(aAttribute) != null) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Set the currently selected attribute.
	 *
	 * @param aAttribute the attribute
	 */
	public void setSelectedAttribute(Attribute aAttribute) {
		editingView.getAttributeList().setSelectedValue(aAttribute, true);
	}

	/**
	 * Set the currently selected index.
	 *
	 * @param aIndex the index
	 */
	public void setSelectedIndex(Index aIndex) {
		editingView.getMainTabbedPane().setSelectedIndex(1);
		editingView.getIndexList().setSelectedValue(aIndex, true);
	}
}
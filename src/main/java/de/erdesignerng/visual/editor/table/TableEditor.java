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
import de.erdesignerng.dialect.Dialect;
import de.erdesignerng.dialect.IndexProperties;
import de.erdesignerng.dialect.TableProperties;
import de.erdesignerng.exception.ElementAlreadyExistsException;
import de.erdesignerng.exception.ElementInvalidNameException;
import de.erdesignerng.model.*;
import de.erdesignerng.visual.MessagesHelper;
import de.erdesignerng.visual.editor.BaseEditor;
import de.erdesignerng.visual.editor.ModelItemNameCellEditor;
import de.erdesignerng.visual.scaffolding.ScaffoldingUtils;
import de.erdesignerng.visual.scaffolding.ScaffoldingWrapper;
import de.mogwai.common.client.binding.BindingInfo;
import de.mogwai.common.client.binding.adapter.RadioButtonAdapter;
import de.mogwai.common.client.binding.validator.ValidationError;
import de.mogwai.common.client.looks.UIInitializer;
import de.mogwai.common.client.looks.components.DefaultTabbedPaneTab;
import de.mogwai.common.client.looks.components.action.DefaultAction;
import de.mogwai.common.client.looks.components.list.DefaultListModel;

import javax.swing.*;
import java.awt.*;
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

	private final BindingInfo<Table> tableBindingInfo = new BindingInfo<>();

	private final BindingInfo<Index> indexBindingInfo = new BindingInfo<>();

	private final BindingInfo<IndexValueModel> indexExpressionBindingInfo = new BindingInfo<>();

	private final BindingInfo<IndexValueModel> indexExpressionBindingInfo2 = new BindingInfo<>();

	private final DefaultListModel indexListModel;

	private final Map<String, Attribute<Table>> knownAttributeValues = new HashMap<>();

	private final Map<String, Index> knownIndexValues = new HashMap<>();

	private final List<Attribute<Table>> removedAttributes = new ArrayList<>();

	private final List<Index> removedIndexes = new ArrayList<>();

	private final DefaultAction newAttributeAction = new DefaultAction(e -> commandNewAttribute(), this, ERDesignerBundle.NEW);

	private final DefaultAction deleteAttributeAction = new DefaultAction(this::commandDeleteAttribute, this, ERDesignerBundle.DELETE);

	private final DefaultAction newIndexAction = new DefaultAction(e -> commandNewIndex(), this, ERDesignerBundle.NEW);

	private final DefaultAction deleteIndexAction = new DefaultAction(e -> commandDeleteIndex(), this, ERDesignerBundle.DELETE);

	private final DefaultAction updateIndex = new DefaultAction(e -> commandUpdateIndex(), this, ERDesignerBundle.UPDATE);

	private final DefaultAction addIndexAttribute = new DefaultAction(e -> commandAddIndexAttribute(), this, ERDesignerBundle.NEWONLYICON);

	private final DefaultAction addIndexExpression = new DefaultAction(e -> commandAddIndexExpression(), this, ERDesignerBundle.NEWONLYICON);

	private final DefaultAction removeIndexElement = new DefaultAction(e -> commandRemoveIndexElement(), this, ERDesignerBundle.DELETEONLYICON);

	private TableProperties tableProperties;

	private ScaffoldingWrapper tablePropertiesWrapper;

	private IndexProperties indexProperties;

	private ScaffoldingWrapper indexPropertiesWrapper;

	private final ModelItemNameCellEditor<Attribute<Table>> attributeEditor;

	public TableEditor(final Model aModel, final Component aParent) {
		super(aParent, ERDesignerBundle.ENTITYEDITOR);
		initialize();

		final DefaultComboBoxModel dataTypesModel = editingView.getDataTypeModel();

        aModel.getAvailableDataTypes().forEach(dataTypesModel::addElement);

		indexListModel = editingView.getIndexList().getModel();

		model = aModel;
		attributeEditor = new ModelItemNameCellEditor<>(model.getDialect());
		editingView.getAttributesTable().getColumnModel().getColumn(0).setCellRenderer(new AttributeListAttributeCellRenderer(this));
		editingView.getAttributesTable().getColumnModel().getColumn(0).setCellEditor(attributeEditor);
		editingView.getAttributesTable().getSelectionModel().addListSelectionListener(e -> updateAttributeEditFields());

		tableBindingInfo.addBinding("name", editingView.getEntityName(), true);
		tableBindingInfo.addBinding("comment", editingView.getEntityComment());
		tableBindingInfo.configure();

		indexBindingInfo.addBinding("name", editingView.getIndexName(), true);
		indexBindingInfo.addBinding("attributes", new IndexAttributesPropertyAdapter(editingView.getIndexFieldList()
		));

		final RadioButtonAdapter theAdapter = new RadioButtonAdapter();
		theAdapter.addMapping(IndexType.PRIMARYKEY, editingView.getPrimaryIndex());
		theAdapter.addMapping(IndexType.UNIQUE, editingView.getUniqueIndex());
		theAdapter.addMapping(IndexType.NONUNIQUE, editingView.getNotUniqueIndex());
		theAdapter.addMapping(IndexType.SPATIAL, editingView.getSpatialIndex());
		theAdapter.addMapping(IndexType.FULLTEXT, editingView.getFulltextIndex());
		indexBindingInfo.addBinding("indexType", theAdapter);
		indexBindingInfo.configure();

		indexExpressionBindingInfo.addBinding("expression", editingView.getIndexExpression(), true);
		indexExpressionBindingInfo.configure();

		indexExpressionBindingInfo2.addBinding("attribute", editingView.getIndexAttribute(), true);
		indexExpressionBindingInfo2.configure();

		if (!model.getDialect().supportsSpatialIndexes()) {
			editingView.getSpatialIndex().setVisible(false);
		}
		if (!model.getDialect().supportsFulltextIndexes()) {
			editingView.getFulltextIndex().setVisible(false);
		}

		updateAttributeEditFields();

		UIInitializer.getInstance().initialize(this);
	}

	/**
	 * This method initializes this.
	 */
	private void initialize() {

		editingView = new TableEditorView() {
			@Override
			protected void attributeEditorRemoved(final Attribute<Table> aAttribute) {
				if (aAttribute.getName() == null) {
					removeAttribute(aAttribute);
				}
			}
		};
		editingView.getOkButton().setAction(okAction);
		editingView.getCancelButton().setAction(cancelAction);
		editingView.getMainTabbedPane().addChangeListener(e -> commandTabStateChange());
		editingView.getNewButton().setAction(newAttributeAction);
		editingView.getDeleteButton().setAction(deleteAttributeAction);
		editingView.getUpdateIndexButton().setAction(updateIndex);
		editingView.getNewIndexButton().setAction(newIndexAction);
		editingView.getDeleteIndexButton().setAction(deleteIndexAction);
		editingView.getIndexList().addListSelectionListener(this::commandIndexListValueChanged);

		editingView.getIndexFieldList().addListSelectionListener(this::commandIndexFieldListValueChanged);

		editingView.getAddIndexAttribute().addActionListener(e -> updateIndexStatusFields());

		editingView.getAddIndexExpression().addActionListener(e -> updateIndexStatusFields());

		editingView.getRemoveFromIndexButton().setAction(removeIndexElement);
		editingView.getAddAttributeToIndexButton().setAction(addIndexAttribute);
		editingView.getAddExpressionToIndexButton().setAction(addIndexExpression);

		removeIndexElement.setEnabled(false);

		setContentPane(editingView);

		pack();

		editingView.getUpdateIndexButton().setEnabled(false);
	}

	public void initializeFor(final Table aTable) {

		tableBindingInfo.setDefaultModel(aTable);
		tableBindingInfo.model2view();

		editingView.getEntityName().setName(aTable.getName());
		for (final Attribute<Table> theAttribute : aTable.getAttributes()) {

			final Attribute<Table> theClone = theAttribute.clone();

			editingView.getAttributeTableModel().add(theClone);
			knownAttributeValues.put(theClone.getSystemId(), theClone);
		}
		for (final Index theIndex : aTable.getIndexes()) {

			final Index theClone = theIndex.clone();

			indexListModel.add(theClone);
			knownIndexValues.put(theClone.getSystemId(), theClone);
		}

		updateAttributeEditFields();

		tableProperties = model.getDialect().createTablePropertiesFor(aTable);
		final DefaultTabbedPaneTab theTab = editingView.getTablePropertiesTab();
		tablePropertiesWrapper = ScaffoldingUtils.createScaffoldingPanelFor(model, tableProperties);
		theTab.add(tablePropertiesWrapper.getComponent(), BorderLayout.CENTER);
		if (!tablePropertiesWrapper.hasComponents()) {
			editingView.disableTablePropertiesTab();
		} else {
			UIInitializer.getInstance().initialize(theTab);
		}
	}

	@Override
	protected void commandOk() {

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
			if (editingView.getAttributeTableModel().getRowCount() == 0) {
				MessagesHelper.displayErrorMessage(this, getResourceHelper().getText(
						ERDesignerBundle.TABLEMUSTHAVEATLEASTONEATTRIBUTE));
				return;
			}
			try {
				model.checkName(editingView.getEntityName().getText());
			} catch (final ElementInvalidNameException e) {
				MessagesHelper.displayErrorMessage(this, getResourceHelper().getText(ERDesignerBundle.NAMEINVALID));
				return;
			}

			setModalResult(MODAL_RESULT_OK);
		}
	}

	private void commandTabStateChange() {
		final int theIndex = editingView.getMainTabbedPane().getSelectedIndex();
		if (theIndex == 1) {
			commandNewIndex();
		}
	}

	public Model getModel() {
		return model;
	}

	private void updateAttributeEditFields() {
		final int theRow = editingView.getAttributesTable().getSelectedRow();
		if (theRow >= 0) {
			deleteAttributeAction.setEnabled(true);
		} else {
			deleteAttributeAction.setEnabled(false);
		}
	}

	private void updateIndexEditFields() {
		final Index theValue = indexBindingInfo.getDefaultModel();

		if (theValue != null) {

			final boolean isNew = !indexListModel.contains(theValue);

			editingView.getNewIndexButton().setEnabled(true);
			editingView.getUpdateIndexButton().setEnabled(true);

			indexBindingInfo.setEnabled(true);

			editingView.getIndexFieldList().setEnabled(true);
			editingView.getPrimaryIndex().setEnabled(true);
			editingView.getUniqueIndex().setEnabled(true);
			editingView.getNotUniqueIndex().setEnabled(true);
			editingView.getSpatialIndex().setEnabled(true);
			editingView.getFulltextIndex().setEnabled(true);

			editingView.getDeleteIndexButton().setEnabled(!isNew);
		} else {

			editingView.getNewIndexButton().setEnabled(true);
			editingView.getDeleteIndexButton().setEnabled(false);
			editingView.getUpdateIndexButton().setEnabled(false);

			editingView.getIndexFieldList().setEnabled(false);

			indexBindingInfo.setEnabled(false);
		}

		final DefaultComboBoxModel theAttModel = new DefaultComboBoxModel();
		for (int i = 0; i < editingView.getAttributeTableModel().getRowCount(); i++) {
			theAttModel.addElement(editingView.getAttributeTableModel().getRow(i));
		}
		editingView.getIndexAttribute().setModel(theAttModel);

		updateIndexStatusFields();

		indexBindingInfo.model2view();

		editingView.getIndexList().invalidate();
		editingView.getIndexList().setSelectedValue(indexBindingInfo.getDefaultModel(), true);
	}

	private void updateIndexStatusFields() {

		final boolean theCurrentIndexSelected = indexBindingInfo.getDefaultModel() != null;
		editingView.getAddIndexAttribute().setEnabled(theCurrentIndexSelected);
		editingView.getAddIndexExpression().setEnabled(theCurrentIndexSelected);

		boolean theEnabled = editingView.getAddIndexAttribute().isSelected() && theCurrentIndexSelected;
		editingView.getIndexAttribute().setEnabled(theEnabled);
		editingView.getAddAttributeToIndexButton().setEnabled(theEnabled);

		final IndexValueModel theModel = new IndexValueModel();

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

	private void commandIndexFieldListValueChanged(final javax.swing.event.ListSelectionEvent evt) {

		removeIndexElement.setEnabled(editingView.getIndexFieldList().getSelectedValue() != null);

		updateIndexStatusFields();
	}

	private void commandIndexListValueChanged(final javax.swing.event.ListSelectionEvent evt) {

		final int index = editingView.getIndexList().getSelectedIndex();
		if (index >= 0) {
			indexBindingInfo.setDefaultModel((Index) indexListModel.get(index));

			updateIndexProperties();
		}

		updateIndexEditFields();
	}

	private void updateIndexProperties() {

		final Index theIndex = indexBindingInfo.getDefaultModel();

		indexProperties = model.getDialect().createIndexPropertiesFor(theIndex);
		final DefaultTabbedPaneTab theTab = editingView.getIndexPropertiesTab();
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

	private void commandDeleteAttribute(final java.awt.event.ActionEvent aEvent) {

		final int theRow = editingView.getAttributesTable().getSelectedRow();
		final Attribute<Table> theAttribute = editingView.getAttributeTableModel().getRow(theRow);

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
			editingView.getAttributeTableModel().remove(theAttribute);

			removedAttributes.add(theAttribute);
		}
	}

	private void commandNewAttribute() {
		final Attribute<Table> theNewAttribute = new Attribute<>();
		editingView.getAttributeTableModel().add(theNewAttribute);
		final int theRow = editingView.getAttributeTableModel().getRowCount();
		editingView.getAttributesTable().setRowSelectionInterval(theRow - 1, theRow - 1);
		knownAttributeValues.put(theNewAttribute.getSystemId(), theNewAttribute);

		editingView.getAttributesTable().editCellAt(theRow - 1, 0);
		attributeEditor.getComponent().requestFocus();
	}

	private void commandDeleteIndex() {
		final Index theAttribute = indexBindingInfo.getDefaultModel();

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
		final Index theIndex = indexBindingInfo.getDefaultModel();
		final List<ValidationError> theValidationResult = indexBindingInfo.validate();
		if (theValidationResult.isEmpty()) {

			final Dialect theDialect = model.getDialect();
			for (int i = 0; i < indexListModel.getSize(); i++) {
				final Index theTempIndex = (Index) indexListModel.get(i);
				try {
					if (theDialect.checkName(theTempIndex.getName()).equals(
							theDialect.checkName(editingView.getIndexName().getText()))
							&& !theTempIndex.getSystemId().equals(theIndex.getSystemId())) {
						MessagesHelper.displayErrorMessage(this, getResourceHelper().getText(
								ERDesignerBundle.INDEXALREADYEXISTS));
						return;
					}
				} catch (final ElementInvalidNameException e) {
					MessagesHelper.displayErrorMessage(this, getResourceHelper().getText(ERDesignerBundle.NAMEINVALID));
					return;
				}
			}

			indexBindingInfo.view2model();

			if (theIndex.getIndexType() == IndexType.PRIMARYKEY) {
				for (int i = 0; i < indexListModel.getSize(); i++) {
					final Index theTempIndex = (Index) indexListModel.get(i);
					if ((theTempIndex.getIndexType() == IndexType.PRIMARYKEY && (!theIndex.equals(theTempIndex)))) {
						MessagesHelper.displayErrorMessage(this, getResourceHelper().getText(
								ERDesignerBundle.THEREISALREADYAPRIMARYKEY));
						return;
					}
				}
			}

			final DefaultListModel<IndexExpression> theListModel = editingView.getIndexFieldList().getModel();
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
			final IndexValueModel theValue = indexExpressionBindingInfo.getDefaultModel();

			final Index theCurrentModel = indexBindingInfo.getDefaultModel();

			theCurrentModel.getExpressions().addExpressionFor(theValue.getExpression());

			indexBindingInfo.model2view();
			indexBindingInfo.markChanged();
		}
	}

	private void commandAddIndexAttribute() {
		if (indexExpressionBindingInfo2.validate().isEmpty() && indexBindingInfo.validate().isEmpty()) {

			indexBindingInfo.view2model();

			indexExpressionBindingInfo2.view2model();
			final IndexValueModel theValue = indexExpressionBindingInfo2.getDefaultModel();

			final Index theCurrentModel = indexBindingInfo.getDefaultModel();

			try {
				theCurrentModel.getExpressions().addExpressionFor(theValue.getAttribute());

				indexBindingInfo.model2view();
				indexBindingInfo.markChanged();
			} catch (final ElementAlreadyExistsException e) {
				MessagesHelper.displayErrorMessage(this, getResourceHelper().getText(
						ERDesignerBundle.ATTRIBUTEALREADYPARTOFINDEX));
			}
		}
	}

	private void commandRemoveIndexElement() {
		if (indexBindingInfo.validate().isEmpty()) {

			if (MessagesHelper.displayQuestionMessage(this, ERDesignerBundle.DOYOUREALLYWANTTODELETE)) {

				indexBindingInfo.view2model();

				final Index theCurrentModel = indexBindingInfo.getDefaultModel();
				theCurrentModel.getExpressions().remove(editingView.getIndexFieldList().getSelectedValue());

				indexBindingInfo.model2view();
				indexBindingInfo.markChanged();

				removeIndexElement.setEnabled(false);
			}
		}
	}

	@Override
	public void applyValues() throws ElementAlreadyExistsException, ElementInvalidNameException {

		final Table theTable = tableBindingInfo.getDefaultModel();

		tablePropertiesWrapper.save();
		tableProperties.copyTo(theTable);

		if (!model.getTables().contains(theTable)) {

			tableBindingInfo.view2model();

			// The table is new, so just add it
			// In case of a clone, we have to check it is not already added
			for (int i = 0; i < editingView.getAttributeTableModel().getRowCount(); i++) {
				final Attribute<Table> theAttribute = editingView.getAttributeTableModel().getRow(i);
				if (!theTable.getAttributes().contains(theAttribute)) {
					theTable.addAttribute(model, editingView.getAttributeTableModel().getRow(i));
				}
			}

			for (int i = 0; i < indexListModel.getSize(); i++) {
				final Index theIndex = (Index) indexListModel.get(i);
				if (!theTable.getIndexes().contains(theIndex)) {
					theTable.addIndex(model, (Index) indexListModel.get(i));
				}
			}

			model.addTable(theTable);
		} else {
			// The table exists already in the model
			final Table theTempTable = new Table();

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
			for (final Attribute<Table> theAttribute : removedAttributes) {
				model.removeAttributeFromTable(theTable, theAttribute);
			}

			// Remove the removed indexes
			for (final Index theIndex : removedIndexes) {
				model.removeIndex(theTable, theIndex);
			}

			// And finally check all attributes if they are new or were renamed
			// or modified
			for (final String theKey : knownAttributeValues.keySet()) {
				final Attribute<Table> theAttribute = knownAttributeValues.get(theKey);

				final Attribute<Table> theExistingAttribute = theTable.getAttributes().findBySystemId(theKey);
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
					} catch (final Exception e) {
						logFatalError(e);
					}
				}
			}

			for (final String theKey : knownIndexValues.keySet()) {
				final Index theIndex = knownIndexValues.get(theKey);

				final Index theExistingIndex = theTable.getIndexes().findBySystemId(theKey);
				if (theExistingIndex == null) {
					model.addIndexToTable(theTable, theIndex);
				} else {
					try {
						if (theExistingIndex.isModified(theIndex, false)) {
							model.changeIndex(theExistingIndex, theIndex);
						}
					} catch (final Exception e) {
						logFatalError(e);
					}
				}
			}
		}
	}

	boolean isPrimaryKey(final Attribute<Table> aAttribute) {
		for (int i = 0; i < indexListModel.getSize(); i++) {
			final Index theIndex = (Index) indexListModel.get(i);
			if (IndexType.PRIMARYKEY == theIndex.getIndexType()) {
				return theIndex.getExpressions().findByAttribute(aAttribute) != null;
			}
		}
		return false;
	}

	private boolean isUsedInIndex(final Attribute<Table> aAttribute) {
		for (int i = 0; i < indexListModel.getSize(); i++) {
			final Index theIndex = (Index) indexListModel.get(i);
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
	public void setSelectedAttribute(final Attribute<Table> aAttribute) {
		final int theIndex = editingView.getAttributeTableModel().getRowIndex(aAttribute);
		editingView.getAttributesTable().setRowSelectionInterval(theIndex, theIndex);
	}

	/**
	 * Set the currently selected index.
	 *
	 * @param aIndex the index
	 */
	public void setSelectedIndex(final Index aIndex) {
		editingView.getMainTabbedPane().setSelectedIndex(1);
		editingView.getIndexList().setSelectedValue(aIndex, true);
	}

	/**
	 * Will be called if editing of an attribute name was canceled and the name is null or empty.
	 *
	 * @param aAttribute
	 */
	private void removeAttribute(final Attribute<Table> aAttribute) {
		knownAttributeValues.remove(aAttribute.getSystemId());

		editingView.getAttributeTableModel().remove(aAttribute);
	}
}
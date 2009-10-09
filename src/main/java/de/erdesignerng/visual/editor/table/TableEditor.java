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
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.event.ChangeEvent;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.dialect.DataType;
import de.erdesignerng.exception.ElementAlreadyExistsException;
import de.erdesignerng.exception.ElementInvalidNameException;
import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.Index;
import de.erdesignerng.model.IndexExpression;
import de.erdesignerng.model.IndexType;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.Table;
import de.erdesignerng.modificationtracker.VetoException;
import de.erdesignerng.visual.MessagesHelper;
import de.erdesignerng.visual.editor.BaseEditor;
import de.mogwai.common.client.binding.BindingInfo;
import de.mogwai.common.client.binding.adapter.RadioButtonAdapter;
import de.mogwai.common.client.binding.validator.ValidationError;
import de.mogwai.common.client.looks.UIInitializer;
import de.mogwai.common.client.looks.components.action.ActionEventProcessor;
import de.mogwai.common.client.looks.components.action.DefaultAction;
import de.mogwai.common.client.looks.components.list.DefaultListModel;

/**
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-13 15:40:33 $
 */
public class TableEditor extends BaseEditor {

    private Model model;

    private TableEditorView editingView;

    private BindingInfo<Table> tableBindingInfo = new BindingInfo<Table>();

    private BindingInfo<Attribute> attributeBindingInfo = new BindingInfo<Attribute>();

    private BindingInfo<Index> indexBindingInfo = new BindingInfo<Index>();

    private BindingInfo<IndexValueModel> indexExpressionBindingInfo = new BindingInfo<IndexValueModel>();

    private BindingInfo<IndexValueModel> indexExpressionBindingInfo2 = new BindingInfo<IndexValueModel>();

    private DefaultListModel attributeListModel;

    private DefaultListModel indexListModel;

    private Map<String, Attribute> knownAttributeValues = new HashMap<String, Attribute>();

    private Map<String, Index> knownIndexValues = new HashMap<String, Index>();

    private List<Attribute> removedAttributes = new ArrayList<Attribute>();

    private List<Index> removedIndexes = new ArrayList<Index>();

    private DefaultAction newAttributeAction = new DefaultAction(new ActionEventProcessor() {

        public void processActionEvent(ActionEvent e) {
            commandNewAttribute(e);
        }
    }, this, ERDesignerBundle.NEW);

    private DefaultAction deleteAttributeAction = new DefaultAction(new ActionEventProcessor() {

        public void processActionEvent(ActionEvent e) {
            commandDeleteAttribute(e);
        }
    }, this, ERDesignerBundle.DELETE);

    private DefaultAction updateAttribute = new DefaultAction(new ActionEventProcessor() {

        public void processActionEvent(ActionEvent e) {
            commandUpdateAttribute(e);
        }
    }, this, ERDesignerBundle.UPDATE);

    private DefaultAction newIndexAction = new DefaultAction(new ActionEventProcessor() {

        public void processActionEvent(ActionEvent e) {
            commandNewIndex();
        }
    }, this, ERDesignerBundle.NEW);

    private DefaultAction deleteIndexAction = new DefaultAction(new ActionEventProcessor() {

        public void processActionEvent(ActionEvent e) {
            commandDeleteIndex();
        }
    }, this, ERDesignerBundle.DELETE);

    private DefaultAction updateIndex = new DefaultAction(new ActionEventProcessor() {

        public void processActionEvent(ActionEvent e) {
            commandUpdateIndex();
        }
    }, this, ERDesignerBundle.UPDATE);

    private DefaultAction addIndexAttribute = new DefaultAction(new ActionEventProcessor() {

        public void processActionEvent(ActionEvent e) {
            commandAddIndexAttribute();
        }
    }, this, ERDesignerBundle.NEWONLYICON);

    private DefaultAction addIndexExpression = new DefaultAction(new ActionEventProcessor() {

        public void processActionEvent(ActionEvent e) {
            commandAddIndexExpression();
        }
    }, this, ERDesignerBundle.NEWONLYICON);

    private DefaultAction removeIndexElement = new DefaultAction(new ActionEventProcessor() {

        public void processActionEvent(ActionEvent e) {
            commandRemoveIndexElement();
        }
    }, this, ERDesignerBundle.DELETEONLYICON);

    public TableEditor(Model aModel, Component aParent) {
        super(aParent, ERDesignerBundle.ENTITYEDITOR);
        initialize();

        DefaultComboBoxModel theDataTypes = new DefaultComboBoxModel();
        for (DataType theType : aModel.getAvailableDataTypes()) {
            theDataTypes.addElement(theType);
        }

        editingView.getDataType().setModel(theDataTypes);
        editingView.getDataType().addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                setSpinnerState((DataType) editingView.getDataType().getSelectedItem());
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
        attributeBindingInfo.addBinding("size", editingView.getSizeSpinner(), true);
        attributeBindingInfo.addBinding("fraction", editingView.getFractionSpinner(), true);
        attributeBindingInfo.addBinding("scale", editingView.getScaleSpinner(), true);
        attributeBindingInfo.addBinding("defaultValue", editingView.getDefault());
        attributeBindingInfo.addBinding("extra", editingView.getExtra());
        attributeBindingInfo.configure();

        indexBindingInfo.addBinding("name", editingView.getIndexName(), true);
        indexBindingInfo.addBinding("attributes", new IndexAttributesPropertyAdapter(editingView.getIndexFieldList(),
                null));

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

            public void stateChanged(javax.swing.event.ChangeEvent e) {
                commandTabStateChange(e);
            }
        });
        editingView.getAttributeList().addListSelectionListener(new javax.swing.event.ListSelectionListener() {

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

            public void valueChanged(javax.swing.event.ListSelectionEvent e) {
                commandIndexListValueChanged(e);
            }
        });

        editingView.getIndexFieldList().addListSelectionListener(new javax.swing.event.ListSelectionListener() {

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
    }

    @Override
    protected void commandOk() {
        
        if (attributeBindingInfo.isChanged()) {
            MessagesHelper.displayErrorMessage(this, getResourceHelper().getFormattedText(ERDesignerBundle.SAVEATTRIBUTECHANGESFIRST));
            return;
        }
        
        if (editingView.getAddExpressionToIndexButton().isEnabled() && indexExpressionBindingInfo.isChanged()) {
            MessagesHelper.displayErrorMessage(this, getResourceHelper().getFormattedText(ERDesignerBundle.SAVEINDEXCHANGESFIRST));
            return;
        }

        if (editingView.getAddAttributeToIndexButton().isEnabled() && indexExpressionBindingInfo2.isChanged()) {
            MessagesHelper.displayErrorMessage(this, getResourceHelper().getFormattedText(ERDesignerBundle.SAVEINDEXCHANGESFIRST));
            return;
        }

        if (indexBindingInfo.isChanged()) {
            MessagesHelper.displayErrorMessage(this, getResourceHelper().getFormattedText(ERDesignerBundle.SAVEINDEXCHANGESFIRST));
            return;
        }
        
        if (tableBindingInfo.validate().size() == 0) {
            if (attributeListModel.getSize() == 0) {
                MessagesHelper.displayErrorMessage(this, getResourceHelper().getText(
                        ERDesignerBundle.TABLEMUSTHAVEATLEASTONEATTRIBUTE));
                return;
            }
            setModalResult(MODAL_RESULT_OK);
        }
    }

    private void commandTabStateChange(ChangeEvent e) {
        int theIndex = editingView.getMainTabbedPane().getSelectedIndex();
        if (theIndex == 0) {
            attributeBindingInfo.setDefaultModel(null);
            updateAttributeEditFields();
        }
        if (theIndex == 1) {
            indexBindingInfo.setDefaultModel(null);
            updateIndexEditFields();
        }
    }

    private void commandUpdateAttribute(java.awt.event.ActionEvent evt) {
        Attribute theModel = attributeBindingInfo.getDefaultModel();
        List<ValidationError> theValidationResult = attributeBindingInfo.validate();
        if (theValidationResult.size() == 0) {
            attributeBindingInfo.view2model();

            if (!attributeListModel.contains(theModel)) {

                attributeListModel.add(theModel);
                knownAttributeValues.put(theModel.getSystemId(), theModel);
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
        if (theEnabled) {
            indexExpressionBindingInfo2.setDefaultModel(new IndexValueModel());
            indexExpressionBindingInfo2.model2view();
        }

        theEnabled = editingView.getAddIndexExpression().isSelected() && theCurrentIndexSelected;
        editingView.getIndexExpression().setEnabled(theEnabled);
        editingView.getAddExpressionToIndexButton().setEnabled(theEnabled);
        if (theEnabled) {
            indexExpressionBindingInfo.setDefaultModel(new IndexValueModel());
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
    }

    private void commandIndexListValueChanged(javax.swing.event.ListSelectionEvent evt) {

        int index = editingView.getIndexList().getSelectedIndex();
        if (index >= 0) {
            indexBindingInfo.setDefaultModel((Index) indexListModel.get(index));
        }

        updateIndexEditFields();
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

    private void commandNewAttribute(java.awt.event.ActionEvent evt) {
        attributeBindingInfo.setDefaultModel(new Attribute());
        updateAttributeEditFields();
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
        updateIndexEditFields();
    }

    private void commandUpdateIndex() {
        Index theModel = indexBindingInfo.getDefaultModel();
        List<ValidationError> theValidationResult = indexBindingInfo.validate();
        if (theValidationResult.size() == 0) {
            indexBindingInfo.view2model();

            if (theModel.getIndexType().equals(IndexType.PRIMARYKEY)) {
                for (int i = 0; i < indexListModel.getSize(); i++) {
                    Index theIndex = (Index) indexListModel.get(i);
                    if ((theIndex.getIndexType().equals(IndexType.PRIMARYKEY) && (!theModel.equals(theIndex)))) {
                        MessagesHelper.displayErrorMessage(this, getResourceHelper().getText(
                                ERDesignerBundle.THEREISALREADYAPRIMARYKEY));
                        return;
                    }
                }
            }

            theModel.getExpressions().clear();
            DefaultListModel<IndexExpression> theListModel = editingView.getIndexFieldList().getModel();
            for (int i = 0; i < theListModel.getSize(); i++) {
                theModel.getExpressions().add(theListModel.get(i));
            }

            if (theModel.getExpressions().size() == 0) {
                MessagesHelper.displayErrorMessage(this, getResourceHelper().getText(
                        ERDesignerBundle.ANINDEXMUSTHAVEATLEASTONEELEMENT));
                return;
            }

            if (!indexListModel.contains(theModel)) {
                indexListModel.add(theModel);
                knownIndexValues.put(theModel.getSystemId(), theModel);
            }

            updateIndexEditFields();
        }

    }

    private void commandAddIndexExpression() {
        if (indexExpressionBindingInfo.validate().size() == 0 && indexBindingInfo.validate().size() == 0) {

            indexBindingInfo.view2model();

            indexExpressionBindingInfo.view2model();
            IndexValueModel theValue = indexExpressionBindingInfo.getDefaultModel();

            Index theCurrentModel = indexBindingInfo.getDefaultModel();

            theCurrentModel.getExpressions().addExpressionFor(theValue.getExpression());

            indexBindingInfo.model2view();
        }
    }

    private void commandAddIndexAttribute() {
        if (indexExpressionBindingInfo2.validate().size() == 0 && indexBindingInfo.validate().size() == 0) {

            indexBindingInfo.view2model();

            indexExpressionBindingInfo2.view2model();
            IndexValueModel theValue = indexExpressionBindingInfo2.getDefaultModel();

            Index theCurrentModel = indexBindingInfo.getDefaultModel();

            try {
                theCurrentModel.getExpressions().addExpressionFor(theValue.getAttribute());

                indexBindingInfo.model2view();
            } catch (ElementAlreadyExistsException e) {
                MessagesHelper.displayErrorMessage(this, getResourceHelper().getText(
                        ERDesignerBundle.ATTRIBUTEALREADYPARTOFINDEX));
            }
        }
    }

    private void commandRemoveIndexElement() {
        if (indexBindingInfo.validate().size() == 0) {

            if (MessagesHelper.displayQuestionMessage(this, ERDesignerBundle.DOYOUREALLYWANTTODELETE)) {

                indexBindingInfo.view2model();

                Index theCurrentModel = indexBindingInfo.getDefaultModel();
                theCurrentModel.getExpressions().remove(editingView.getIndexFieldList().getSelectedValue());

                indexBindingInfo.model2view();

                removeIndexElement.setEnabled(false);
            }
        }
    }

    @Override
    public void applyValues() throws ElementAlreadyExistsException, ElementInvalidNameException, VetoException {

        Table theTable = tableBindingInfo.getDefaultModel();

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

                Attribute theExistantAttribute = theTable.getAttributes().findBySystemId(theKey);
                if (theExistantAttribute == null) {
                    model.addAttributeToTable(theTable, theAttribute);
                } else {
                    try {
                        if (theExistantAttribute.isRenamed(theAttribute)) {
                            model.renameAttribute(theExistantAttribute, theAttribute.getName());
                        } else {
                            if (theExistantAttribute.isModified(theAttribute, false)) {
                                model.changeAttribute(theExistantAttribute, theAttribute);
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

                Index theExistantIndex = theTable.getIndexes().findBySystemId(theKey);
                if (theExistantIndex == null) {
                    model.addIndexToTable(theTable, theIndex);
                } else {
                    try {
                        if (theExistantIndex.isModified(theIndex, false)) {
                            model.changeIndex(theExistantIndex, theIndex);
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
}
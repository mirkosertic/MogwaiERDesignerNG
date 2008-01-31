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
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.event.ListSelectionEvent;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.exception.ElementAlreadyExistsException;
import de.erdesignerng.exception.ElementInvalidNameException;
import de.erdesignerng.model.Domain;
import de.erdesignerng.model.DomainList;
import de.erdesignerng.model.Model;
import de.erdesignerng.dialect.DataType;
import de.erdesignerng.visual.MessagesHelper;
import de.erdesignerng.visual.editor.BaseEditor;
import de.erdesignerng.visual.editor.DialogConstants;
import de.mogwai.common.client.binding.BindingInfo;
import de.mogwai.common.client.looks.UIInitializer;
import de.mogwai.common.client.looks.components.action.ActionEventProcessor;
import de.mogwai.common.client.looks.components.action.DefaultAction;
import de.mogwai.common.client.looks.components.list.DefaultListModel;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-01-31 20:08:52 $
 */
public class DomainEditor extends BaseEditor {

    private DefaultListModel domainListModel;

    private DefaultComboBoxModel datatypesModel;

    private BindingInfo<Domain> bindingInfo = new BindingInfo<Domain>();

    private DomainEditorView editingView = new DomainEditorView();

    private Model model;

    private List<Domain> removedDomains = new ArrayList<Domain>();

    private Map<String, Domain> knownValues = new HashMap<String, Domain>();

    private DefaultAction updateAction = new DefaultAction(new ActionEventProcessor() {

        public void processActionEvent(ActionEvent e) {
            commandUpdate();
        }
    }, this, ERDesignerBundle.UPDATE);

    private DefaultAction closeAction = new DefaultAction(new ActionEventProcessor() {

        public void processActionEvent(ActionEvent e) {
            commandClose();
        }
    }, this, ERDesignerBundle.OK);

    private DefaultAction cancelAction = new DefaultAction(new ActionEventProcessor() {

        public void processActionEvent(ActionEvent e) {
            commandCancel();
        }
    }, this, ERDesignerBundle.CANCEL);

    private DefaultAction newAction = new DefaultAction(new ActionEventProcessor() {

        public void processActionEvent(ActionEvent e) {
            commandNew();
        }
    }, this, ERDesignerBundle.NEW);

    private DefaultAction deleteAction = new DefaultAction(new ActionEventProcessor() {

        public void processActionEvent(ActionEvent e) {
            commandDelete();
        }
    }, this, ERDesignerBundle.DELETE);

    /**
     * Create a domain editor.
     * 
     * @param aModel
     *            the model
     * @param aParent
     *            the parent container
     */
    public DomainEditor(Model aModel, Component aParent) {
        super(aParent, ERDesignerBundle.DOMAINS);

        model = aModel;

        initialize();

        datatypesModel = new DefaultComboBoxModel();
        List<DataType> theTypes = model.getDialect().getDataTypes();
        Collections.sort(theTypes);
        for (DataType theType : theTypes) {
            datatypesModel.addElement(theType);
        }

        editingView.getDataType().setModel(datatypesModel);

        domainListModel = editingView.getDomainList().getModel();
        for (Domain theDomain : aModel.getDomains()) {

            Domain theClone = theDomain.clone();

            domainListModel.add(theClone);

            knownValues.put(theClone.getName(), theClone);
        }

        editingView.getDataType().addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent aEvent) {
                updateSpinners((DataType) editingView.getDataType().getSelectedItem());
            }

        });

        editingView.getDomainList().setModel(domainListModel);

        bindingInfo.addBinding("name", editingView.getDomainName(), true);
        bindingInfo.addBinding("datatype", editingView.getDataType(), true);
        bindingInfo.addBinding("size", editingView.getSizeSpinner(), true);
        bindingInfo.addBinding("precision", editingView.getPrecisionSpinner(), true);
        bindingInfo.addBinding("scale", editingView.getScaleSpinner(), true);
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

        editingView.getDomainList().addListSelectionListener(new javax.swing.event.ListSelectionListener() {

            public void valueChanged(javax.swing.event.ListSelectionEvent e) {
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

            editingView.getDataType().setEnabled(true);
            editingView.getNewButton().setEnabled(true);
            editingView.getDeleteButton().setEnabled(!isNew);
            editingView.getDomainName().setEnabled(true);
            editingView.getUpdateButton().setEnabled(true);
            editingView.getSizeSpinner().setEnabled(true);
            editingView.getPrecisionSpinner().setEnabled(true);
            editingView.getScaleSpinner().setEnabled(true);

        } else {
            editingView.getDataType().setEnabled(false);
            editingView.getNewButton().setEnabled(true);
            editingView.getDeleteButton().setEnabled(false);
            editingView.getDomainName().setEnabled(false);
            editingView.getUpdateButton().setEnabled(false);
            editingView.getSizeSpinner().setEnabled(false);
            editingView.getPrecisionSpinner().setEnabled(false);
            editingView.getScaleSpinner().setEnabled(false);
        }

        bindingInfo.model2view();

        updateSpinners(theValue != null ? theValue.getDatatype() : null);

        editingView.getDomainList().invalidate();
        editingView.getDomainList().setSelectedValue(bindingInfo.getDefaultModel(), true);
    }

    private void updateSpinners(DataType aDataType) {
        if (aDataType != null) {
            editingView.getSizeSpinner().setEnabled(aDataType.supportsSize());
            editingView.getPrecisionSpinner().setEnabled(aDataType.supportsPrecision());
            editingView.getScaleSpinner().setEnabled(aDataType.supportsScale());
        } else {
            editingView.getSizeSpinner().setEnabled(false);
            editingView.getPrecisionSpinner().setEnabled(false);
            editingView.getScaleSpinner().setEnabled(false);
        }
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
                    MessagesHelper.displayErrorMessage(this, "Name already in use!");
                    return;
                }
                domainListModel.add(theModel);
                knownValues.put(theModel.getName(), theModel);
            }

            updateEditFields();
        }

    }

    private void commandDelete() {

        Domain theDomain = bindingInfo.getDefaultModel();

        if (!model.getTables().isDomainUsed(theDomain)) {
            if (MessagesHelper.displayQuestionMessage(this, ERDesignerBundle.DOYOUREALLYWANTTODELETE)) {

                removedDomains.add(theDomain);
                domainListModel.remove(theDomain);

                commandNew();
            }
        } else {
            MessagesHelper.displayErrorMessage(this, getResourceHelper().getText(ERDesignerBundle.ELEMENTINUSE));
        }
    }

    @Override
    public void applyValues() throws ElementAlreadyExistsException, ElementInvalidNameException {

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

        model.getDomains().removeAll(removedDomains);
    }
}

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
import java.util.List;

import javax.swing.DefaultComboBoxModel;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.dialect.DataType;
import de.erdesignerng.exception.ElementAlreadyExistsException;
import de.erdesignerng.exception.ElementInvalidNameException;
import de.erdesignerng.model.Domain;
import de.erdesignerng.model.Model;
import de.erdesignerng.modificationtracker.VetoException;
import de.erdesignerng.visual.MessagesHelper;
import de.erdesignerng.visual.editor.BaseEditor;
import de.mogwai.common.client.binding.BindingInfo;
import de.mogwai.common.client.binding.validator.ValidationError;
import de.mogwai.common.client.looks.UIInitializer;
import de.mogwai.common.client.looks.components.action.ActionEventProcessor;
import de.mogwai.common.client.looks.components.action.DefaultAction;
import de.mogwai.common.client.looks.components.list.DefaultListModel;

/**
 * The domain editor.
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2008-06-15 17:53:55 $
 */
public class DomainEditor extends BaseEditor {

    private Model model;

    private DomainEditorView editingView;

    private BindingInfo<Domain> domainBindingInfo = new BindingInfo<Domain>();

    private DefaultListModel domainListModel;

    private List<Domain> removedDomains = new ArrayList<Domain>();

    private DefaultAction okAction = new DefaultAction(new ActionEventProcessor() {

        public void processActionEvent(ActionEvent e) {
            commandOk();
        }
    }, this, ERDesignerBundle.OK);

    private DefaultAction cancelAction = new DefaultAction(new ActionEventProcessor() {

        public void processActionEvent(ActionEvent e) {
            commandCancel();
        }
    }, this, ERDesignerBundle.CANCEL);

    private DefaultAction newAttributeAction = new DefaultAction(new ActionEventProcessor() {

        public void processActionEvent(ActionEvent e) {
            commandNewDomain(e);
        }
    }, this, ERDesignerBundle.NEW);

    private DefaultAction deleteAttributeAction = new DefaultAction(new ActionEventProcessor() {

        public void processActionEvent(ActionEvent e) {
            commandDeleteDomain(e);
        }
    }, this, ERDesignerBundle.DELETE);

    private DefaultAction updateAttribute = new DefaultAction(new ActionEventProcessor() {

        public void processActionEvent(ActionEvent e) {
            commandUpdateDomain(e);
        }
    }, this, ERDesignerBundle.UPDATE);

    public DomainEditor(Model aModel, Component aParent) {
        super(aParent, ERDesignerBundle.DOMAINEDITOR);
        initialize();

        DefaultComboBoxModel theDataTypes = new DefaultComboBoxModel();
        for (DataType theType : aModel.getDomainDataTypes()) {
            theDataTypes.addElement(theType);
        }

        editingView.getDataType().setModel(theDataTypes);
        editingView.getDataType().addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                setSpinnerState((DataType) editingView.getDataType().getSelectedItem());
            }

        });

        domainListModel = editingView.getDomainList().getModel();
        for (Domain theDomain : aModel.getDomains()) {
            domainListModel.add(theDomain.clone());
        }

        model = aModel;

        domainBindingInfo.addBinding("name", editingView.getDomainName(), true);
        domainBindingInfo.addBinding("attribute.size", editingView.getSizeSpinner());
        domainBindingInfo.addBinding("attribute.fraction", editingView.getFractionSpinner());
        domainBindingInfo.addBinding("attribute.scale", editingView.getScaleSpinner());
        domainBindingInfo.configure();

        UIInitializer.getInstance().initialize(this);
        updateDomainEditFields();
    }

    /**
     * This method initializes this.
     */
    private void initialize() {

        editingView = new DomainEditorView();
        editingView.getOkButton().setAction(okAction);
        editingView.getCancelButton().setAction(cancelAction);
        editingView.getDomainList().addListSelectionListener(new javax.swing.event.ListSelectionListener() {

            public void valueChanged(javax.swing.event.ListSelectionEvent e) {
                commandAttributeListValueChanged(e);
            }
        });
        editingView.getNewButton().setAction(newAttributeAction);
        editingView.getDeleteButton().setAction(deleteAttributeAction);
        editingView.getUpdateDomainButton().setAction(updateAttribute);

        setContentPane(editingView);

        pack();
    }

    private void commandOk() {
        setModalResult(MODAL_RESULT_OK);
    }

    private void commandUpdateDomain(java.awt.event.ActionEvent evt) {

        Domain theModel = domainBindingInfo.getDefaultModel();
        List<ValidationError> theValidationResult = domainBindingInfo.validate();
        if (theValidationResult.size() == 0) {
            domainBindingInfo.view2model();

            if (!domainListModel.contains(theModel)) {
                domainListModel.add(theModel);
            }

            updateDomainEditFields();
        }
    }

    private void setSpinnerState(DataType aValue) {
        if (aValue != null) {
            editingView.getSizeSpinner().setEnabled(aValue.supportsSize());
            editingView.getFractionSpinner().setEnabled(aValue.supportsFraction());
            editingView.getScaleSpinner().setEnabled(aValue.supportsScale());
        } else {
            editingView.getSizeSpinner().setEnabled(false);
            editingView.getFractionSpinner().setEnabled(false);
            editingView.getScaleSpinner().setEnabled(false);
        }
    }

    private void updateDomainEditFields() {

        Domain theValue = domainBindingInfo.getDefaultModel();

        if (theValue != null) {
            DataType theDataType = theValue.getAttribute().getDatatype();

            boolean isNew = !domainListModel.contains(theValue);

            editingView.getNewButton().setEnabled(true);
            editingView.getDeleteButton().setEnabled(!isNew);
            editingView.getDomainName().setEnabled(true);
            editingView.getDataType().setEnabled(true);
            setSpinnerState(theDataType);

        } else {
            editingView.getNewButton().setEnabled(true);
            editingView.getDeleteButton().setEnabled(false);
            editingView.getDomainName().setEnabled(false);
            editingView.getDataType().setEnabled(false);
            setSpinnerState(null);
        }

        domainBindingInfo.model2view();

        editingView.getDomainList().invalidate();
        editingView.getDomainList().setSelectedValue(domainBindingInfo.getDefaultModel(), true);

    }

    private void commandAttributeListValueChanged(javax.swing.event.ListSelectionEvent evt) {

        int index = editingView.getDomainList().getSelectedIndex();
        if (index >= 0) {
            domainBindingInfo.setDefaultModel((Domain) domainListModel.get(index));
        }

        updateDomainEditFields();

    }

    private void commandDeleteDomain(java.awt.event.ActionEvent aEvent) {

        Domain theDomain = domainBindingInfo.getDefaultModel();

        if (MessagesHelper.displayQuestionMessage(this, ERDesignerBundle.DOYOUREALLYWANTTODELETE)) {
            domainListModel.remove(theDomain);
            removedDomains.add(theDomain);
        }
    }

    private void commandNewDomain(java.awt.event.ActionEvent evt) {
        domainBindingInfo.setDefaultModel(new Domain());
        updateDomainEditFields();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void applyValues() throws ElementAlreadyExistsException, ElementInvalidNameException, VetoException {

        for (Domain aDomain : removedDomains) {
            model.removeDomain(aDomain);
        }

        for (int i = 0; i < domainListModel.getSize(); i++) {
            Domain theDomain = (Domain) domainListModel.get(i);

            Domain theOriginalDomain = model.getDomains().findBySystemId(theDomain.getSystemId());
            if (theOriginalDomain == null) {
                model.addDomain(theDomain);
            } else {
                theOriginalDomain.restoreFrom(theDomain);
            }
        }
    }
}
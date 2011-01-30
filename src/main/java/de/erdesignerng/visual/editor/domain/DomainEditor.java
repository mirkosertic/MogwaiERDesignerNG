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

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.dialect.DataType;
import de.erdesignerng.dialect.DomainProperties;
import de.erdesignerng.exception.ElementAlreadyExistsException;
import de.erdesignerng.exception.ElementInvalidNameException;
import de.erdesignerng.model.Domain;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.Table;
import de.erdesignerng.modificationtracker.VetoException;
import de.erdesignerng.visual.MessagesHelper;
import de.erdesignerng.visual.editor.BaseEditor;
import de.erdesignerng.visual.editor.NullsafeSpinnerEditor;
import de.erdesignerng.visual.editor.NullsafeSpinnerModel;
import de.erdesignerng.visual.scaffolding.ScaffoldingUtils;
import de.erdesignerng.visual.scaffolding.ScaffoldingWrapper;
import de.mogwai.common.client.binding.BindingInfo;
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
import java.util.List;

/**
 * The domain editor.
 *
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-13 15:40:33 $
 */
public class DomainEditor extends BaseEditor {

    private final Model model;

    private DomainEditorView editingView;

    private final BindingInfo<Domain> domainBindingInfo = new BindingInfo<Domain>();

    private final DefaultListModel domainListModel;

    private final List<Domain> removedDomains = new ArrayList<Domain>();

    private final DefaultAction newDomainAction = new DefaultAction(new ActionEventProcessor() {

        public void processActionEvent(ActionEvent e) {
            commandNewDomain(e);
        }
    }, this, ERDesignerBundle.NEW);

    private final DefaultAction deleteDomainAction = new DefaultAction(new ActionEventProcessor() {

        public void processActionEvent(ActionEvent e) {
            commandDeleteDomain(e);
        }
    }, this, ERDesignerBundle.DELETE);

    private final DefaultAction updateDomain = new DefaultAction(new ActionEventProcessor() {

        public void processActionEvent(ActionEvent e) {
            commandUpdateDomain();
        }
    }, this, ERDesignerBundle.UPDATE);

    private DomainProperties domainProperties;

    private ScaffoldingWrapper domainPropertiesWrapper;

    public DomainEditor(Model aModel, Component aParent) {
        super(aParent, ERDesignerBundle.DOMAINEDITOR);
        initialize();

        editingView.getSizeSpinner().setModel(new NullsafeSpinnerModel());
        editingView.getSizeSpinner().setEditor(new NullsafeSpinnerEditor(editingView.getSizeSpinner()));

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
        domainBindingInfo.addBinding("concreteType", editingView.getDataType(), true);
        domainBindingInfo.addBinding("size", editingView.getSizeSpinner());
        domainBindingInfo.addBinding("fraction", editingView.getFractionSpinner());
        domainBindingInfo.addBinding("scale", editingView.getScaleSpinner());
        domainBindingInfo.addBinding("nullable", editingView.getNullable());
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
        editingView.getNewButton().setAction(newDomainAction);
        editingView.getDeleteButton().setAction(deleteDomainAction);
        editingView.getUpdateDomainButton().setAction(updateDomain);

        setContentPane(editingView);

        pack();
    }

    private void commandUpdateDomain() {

        Domain theModel = domainBindingInfo.getDefaultModel();
        List<ValidationError> theValidationResult = domainBindingInfo.validate();
        if (theValidationResult.size() == 0) {

            domainPropertiesWrapper.save();
            domainProperties.copyTo(theModel);

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
            DataType theDataType = theValue.getConcreteType();

            boolean isNew = !domainListModel.contains(theValue);

            editingView.getNewButton().setEnabled(true);
            editingView.getDeleteButton().setEnabled(!isNew);
            editingView.getDomainName().setEnabled(true);
            editingView.getDataType().setEnabled(true);
            editingView.getNullable().setEnabled(true);
            setSpinnerState(theDataType);

        } else {
            editingView.getNewButton().setEnabled(true);
            editingView.getDeleteButton().setEnabled(false);
            editingView.getDomainName().setEnabled(false);
            editingView.getDataType().setEnabled(false);
            editingView.getNullable().setEnabled(false);
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

            initializeDomainEdit(domainBindingInfo.getDefaultModel());
        }

        updateDomainEditFields();
    }

    private void initializeDomainEdit(Domain aDomain) {
        domainProperties = model.getDialect().createDomainPropertiesFor(aDomain);
        DefaultTabbedPaneTab theTab = editingView.getPropertiesPanel();
        theTab.removeAll();
        domainPropertiesWrapper = ScaffoldingUtils.createScaffoldingPanelFor(model, domainProperties);
        theTab.add(domainPropertiesWrapper.getComponent(), BorderLayout.CENTER);
        editingView.disablePropertiesTab();
        if (domainPropertiesWrapper.hasComponents()) {
            editingView.enablePropertiesTab();
            UIInitializer.getInstance().initialize(theTab);
        }
    }

    private void commandDeleteDomain(java.awt.event.ActionEvent aEvent) {

        Domain theDomain = domainBindingInfo.getDefaultModel();
        Table theTable = model.getTables().checkIfUsedByTable(theDomain);
        if (theTable == null) {

            if (MessagesHelper.displayQuestionMessage(this, ERDesignerBundle.DOYOUREALLYWANTTODELETE)) {
                domainListModel.remove(theDomain);
                removedDomains.add(theDomain);
            }
        } else {
            MessagesHelper.displayErrorMessage(this, getResourceHelper().getFormattedText(
                    ERDesignerBundle.DOMAINISINUSEBYTABLE, theTable.getName()));
        }
    }

    private void commandNewDomain(java.awt.event.ActionEvent evt) {
        domainBindingInfo.setDefaultModel(new Domain());
        updateDomainEditFields();

        initializeDomainEdit(domainBindingInfo.getDefaultModel());
    }

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

    /**
     * Set the selected main.
     *
     * @param aDomain the selected domain
     */
    public void setSelectedDomain(Domain aDomain) {
        editingView.getDomainList().setSelectedValue(aDomain, true);
    }
}
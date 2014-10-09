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
import de.erdesignerng.exception.ElementAlreadyExistsException;
import de.erdesignerng.exception.ElementInvalidNameException;
import de.erdesignerng.model.Domain;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.Table;
import de.erdesignerng.modificationtracker.VetoException;
import de.erdesignerng.visual.MessagesHelper;
import de.erdesignerng.visual.editor.BaseEditor;
import de.erdesignerng.visual.editor.ModelItemDefaultCellRenderer;
import de.erdesignerng.visual.editor.ModelItemNameCellEditor;
import de.mogwai.common.client.looks.UIInitializer;
import de.mogwai.common.client.looks.components.action.ActionEventProcessor;
import de.mogwai.common.client.looks.components.action.DefaultAction;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
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

    private final List<Domain> removedDomains = new ArrayList<>();

    private final DefaultAction newDomainAction = new DefaultAction(new ActionEventProcessor() {

        @Override
        public void processActionEvent(ActionEvent e) {
            commandNewDomain();
        }
    }, this, ERDesignerBundle.NEW);

    private final DefaultAction deleteDomainAction = new DefaultAction(new ActionEventProcessor() {

        @Override
        public void processActionEvent(ActionEvent e) {
            commandDeleteDomain(e);
        }
    }, this, ERDesignerBundle.DELETE);

    private final ModelItemNameCellEditor<Domain> domainEditor;

    public DomainEditor(Model aModel, Component aParent) {
        super(aParent, ERDesignerBundle.DOMAINEDITOR);
        initialize();

        DefaultComboBoxModel theDataTypes = editingView.getDataTypesModel();
        for (DataType theType : aModel.getDomainDataTypes()) {
            theDataTypes.addElement(theType);
        }

        DomainTableModel theModel = editingView.getDomainTableModel();
        for (Domain theDomain : aModel.getDomains()) {
            theModel.add(theDomain.clone());
        }

        model = aModel;
        domainEditor = new ModelItemNameCellEditor<>(model.getDialect());
        editingView.getDomainTable().getColumnModel().getColumn(0).setCellRenderer(ModelItemDefaultCellRenderer.getInstance());
        editingView.getDomainTable().getColumnModel().getColumn(0).setCellEditor(domainEditor);
        editingView.getDomainTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                updateDomainEditFields();
            }
        });

        updateDomainEditFields();

        UIInitializer.getInstance().initialize(this);
    }

    private void updateDomainEditFields() {
        int theRow = editingView.getDomainTable().getSelectedRow();
        if (theRow >= 0) {
            deleteDomainAction.setEnabled(true);
        } else {
            deleteDomainAction.setEnabled(false);
        }
    }

    /**
     * This method initializes this.
     */
    private void initialize() {

        editingView = new DomainEditorView() {
            @Override
            protected void domainEditorRemoved(Domain aDomain) {
                if (aDomain.getName() == null) {
                    removeDomain(aDomain);
                }
            }
        };
        editingView.getOkButton().setAction(okAction);
        editingView.getCancelButton().setAction(cancelAction);

        editingView.getNewButton().setAction(newDomainAction);
        editingView.getDeleteButton().setAction(deleteDomainAction);

        setContentPane(editingView);

        pack();
    }

    private void commandDeleteDomain(java.awt.event.ActionEvent aEvent) {

        Domain theDomain = editingView.getDomainTableModel().getRow(editingView.getDomainTable().getSelectedRow());
        Table theTable = model.getTables().checkIfUsedByTable(theDomain);
        if (theTable == null) {

            if (MessagesHelper.displayQuestionMessage(this, ERDesignerBundle.DOYOUREALLYWANTTODELETE)) {
                editingView.getDomainTableModel().remove(theDomain);
                removedDomains.add(theDomain);
            }
        } else {
            MessagesHelper.displayErrorMessage(this, getResourceHelper().getFormattedText(
                    ERDesignerBundle.DOMAINISINUSEBYTABLE, theTable.getName()));
        }
    }

    private void commandNewDomain() {

        Domain theNewAttribute = new Domain();
        editingView.getDomainTableModel().add(theNewAttribute);
        int theRow = editingView.getDomainTableModel().getRowCount();
        editingView.getDomainTable().setRowSelectionInterval(theRow - 1, theRow - 1);

        editingView.getDomainTable().editCellAt(theRow - 1, 0);
        domainEditor.getComponent().requestFocus();
    }

    @Override
    public void applyValues() throws ElementAlreadyExistsException, ElementInvalidNameException, VetoException {

        for (Domain aDomain : removedDomains) {
            model.removeDomain(aDomain);
        }

        DomainTableModel theModel = editingView.getDomainTableModel();
        for (int i = 0; i < theModel.getRowCount(); i++) {
            Domain theDomain = theModel.getRow(i);

            Domain theOriginalDomain = model.getDomains().findBySystemId(theDomain.getSystemId());
            if (theOriginalDomain == null) {
                model.addDomain(theDomain);
            } else {
                theOriginalDomain.restoreFrom(theDomain);
            }
        }
    }

    /**
     * Set the selected domain.
     *
     * @param aDomain the selected domain
     */
    public void setSelectedDomain(Domain aDomain) {
        int theIndex = editingView.getDomainTableModel().getRowIndex(aDomain);
        editingView.getDomainTable().setRowSelectionInterval(theIndex, theIndex);
    }

    /**
     * Will be called if editing of a domain name was canceled and the name is null or empty.
     *
     * @param aDomain
     */
    private void removeDomain(Domain aDomain) {
        editingView.getDomainTableModel().remove(aDomain);
    }
}
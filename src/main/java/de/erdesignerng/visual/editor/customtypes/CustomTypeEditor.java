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
package de.erdesignerng.visual.editor.customtypes;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.model.CustomType;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.Table;
import de.erdesignerng.visual.MessagesHelper;
import de.erdesignerng.visual.editor.BaseEditor;
import de.mogwai.common.client.binding.BindingInfo;
import de.mogwai.common.client.binding.validator.ValidationError;
import de.mogwai.common.client.looks.UIInitializer;
import de.mogwai.common.client.looks.components.action.DefaultAction;
import de.mogwai.common.client.looks.components.list.DefaultListModel;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The domain editor.
 *
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-13 15:40:33 $
 */
public class CustomTypeEditor extends BaseEditor {

    private final Model model;

    private CustomTypeEditorView editingView;

    private final BindingInfo<CustomType> typeBindingInfo = new BindingInfo<>();

    private final DefaultListModel typeListModel;

    private final List<CustomType> removedTypes = new ArrayList<>();

    private final DefaultAction newTypeAction = new DefaultAction(this::commandNewType, this, ERDesignerBundle.NEW);

    private final DefaultAction deleteTypeAction = new DefaultAction(this::commandDeleteType, this, ERDesignerBundle.DELETE);

    private final DefaultAction updateTypeAction = new DefaultAction(this::commandUpdateType, this, ERDesignerBundle.UPDATE);

    public CustomTypeEditor(final Model aModel, final Component aParent) {
        super(aParent, ERDesignerBundle.CUSTOMTYPEEDITOR);
        initialize();

        typeListModel = editingView.getTypesList().getModel();
        for (final CustomType theType : aModel.getCustomTypes()) {
            typeListModel.add(theType.clone());
        }

        model = aModel;

        typeBindingInfo.addBinding("name", editingView.getTypeName(), true);
        typeBindingInfo.addBinding("sqlDefinition", editingView.getTypeddl(), true);
        typeBindingInfo.configure();

        UIInitializer.getInstance().initialize(this);
        updateTypeEditFields();
    }

    /**
     * This method initializes this.
     */
    private void initialize() {

        editingView = new CustomTypeEditorView();
        editingView.getOkButton().setAction(okAction);
        editingView.getCancelButton().setAction(cancelAction);
        editingView.getTypesList().addListSelectionListener(this::commandAttributeListValueChanged);
        editingView.getNewButton().setAction(newTypeAction);
        editingView.getDeleteButton().setAction(deleteTypeAction);
        editingView.getUpdateTypeButton().setAction(updateTypeAction);

        setContentPane(editingView);

        pack();
    }

    private void commandUpdateType(final java.awt.event.ActionEvent evt) {

        final CustomType theModel = typeBindingInfo.getDefaultModel();
        final List<ValidationError> theValidationResult = typeBindingInfo.validate();
        if (theValidationResult.isEmpty()) {

            typeBindingInfo.view2model();

            if (!typeListModel.contains(theModel)) {
                typeListModel.add(theModel);
            }

            updateTypeEditFields();
        }
    }

    private void updateTypeEditFields() {

        final CustomType theValue = typeBindingInfo.getDefaultModel();

        if (theValue != null) {

            final boolean isNew = !typeListModel.contains(theValue);

            editingView.getNewButton().setEnabled(true);
            editingView.getDeleteButton().setEnabled(!isNew);
            editingView.getTypeName().setEnabled(true);
            editingView.getTypeddl().setEnabled(true);

        } else {
            editingView.getNewButton().setEnabled(true);
            editingView.getDeleteButton().setEnabled(false);
            editingView.getTypeName().setEnabled(false);
            editingView.getTypeddl().setEnabled(false);
        }

        typeBindingInfo.model2view();

        editingView.getTypesList().invalidate();
        editingView.getTypesList().setSelectedValue(typeBindingInfo.getDefaultModel(), true);

    }

    private void commandAttributeListValueChanged(final javax.swing.event.ListSelectionEvent evt) {

        final int index = editingView.getTypesList().getSelectedIndex();
        if (index >= 0) {
            typeBindingInfo.setDefaultModel((CustomType) typeListModel.get(index));
        }

        updateTypeEditFields();
    }

    private void commandDeleteType(final java.awt.event.ActionEvent aEvent) {

        final CustomType theType = typeBindingInfo.getDefaultModel();
        final Table theTable = model.getTables().checkIfUsedByTable(theType);
        if (theTable == null) {

            if (MessagesHelper.displayQuestionMessage(this, ERDesignerBundle.DOYOUREALLYWANTTODELETE)) {
                typeListModel.remove(theType);
                removedTypes.add(theType);
            }
        } else {
            MessagesHelper.displayErrorMessage(this, getResourceHelper().getFormattedText(
                    ERDesignerBundle.DOMAINISINUSEBYTABLE, theTable.getName()));
        }
    }

    private void commandNewType(final java.awt.event.ActionEvent evt) {
        typeBindingInfo.setDefaultModel(new CustomType());
        updateTypeEditFields();
    }

    @Override
    public void applyValues() {

        for (final CustomType theRemovedType : removedTypes) {
            model.removeCustomType(theRemovedType);
        }

        for (int i = 0; i < typeListModel.getSize(); i++) {
            final CustomType theType = (CustomType) typeListModel.get(i);

            final CustomType theOriginalType = model.getCustomTypes().findBySystemId(theType.getSystemId());
            if (theOriginalType == null) {
                model.addCustomType(theType);
            } else {
                theOriginalType.restoreFrom(theType);
            }
        }
    }

    /**
     * Set the selected type.
     *
     * @param aType the type to select
     */
    public void setSelectedType(final CustomType aType) {
        editingView.getTypesList().setSelectedValue(aType, true);
    }
}
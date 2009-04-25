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
package de.erdesignerng.visual.editor.relation;

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;

import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.Index;
import de.erdesignerng.model.IndexExpression;
import de.erdesignerng.model.Relation;
import de.mogwai.common.client.binding.BindingBundle;
import de.mogwai.common.client.binding.PropertyAdapter;
import de.mogwai.common.client.binding.validator.ValidationError;
import de.mogwai.common.client.looks.components.DefaultComboBox;
import de.mogwai.common.client.looks.components.DefaultTable;
import de.mogwai.common.i18n.ResourceHelper;

public class RelationAttributesPropertyAdapter extends PropertyAdapter {

    private static final ResourceHelper BINDINGHELPER = ResourceHelper.getResourceHelper(BindingBundle.BUNDLE_NAME);
    
    private ResourceHelper helper;

    public RelationAttributesPropertyAdapter(JComponent aComponent, String aPropertyName, ResourceHelper aHelper) {
        super(aComponent, aPropertyName);
        helper = aHelper;
    }

    @Override
    public void model2view(Object aModel, String aPropertyName) {

        Relation theRelation = (Relation) aModel;

        Index thePrimaryKey = theRelation.getExportingTable().getPrimarykey();
        Attribute[] theAssigned;
        if (thePrimaryKey != null) {
            theAssigned = new Attribute[thePrimaryKey.getExpressions().size()];
            for (int count = 0; count < thePrimaryKey.getExpressions().size(); count++) {
                theAssigned[count] = theRelation.getMapping().get(thePrimaryKey.getExpressions().get(count));
            }
        } else {
            theAssigned = new Attribute[0];
        }
        AttributeTableModel theTableModel = new AttributeTableModel(theRelation.getExportingTable().getName(),
                theRelation.getImportingTable().getName(), thePrimaryKey, theAssigned);

        DefaultTable theTable = (DefaultTable) getComponent()[0];
        theTable.setModel(theTableModel);
        theTable.getTableHeader().setReorderingAllowed(false);

        DefaultComboBox theAttributes = new DefaultComboBox();
        theAttributes.setModel(new DefaultComboBoxModel(theRelation.getImportingTable().getAttributes()));
        theTable.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(theAttributes));
        theTable.setRowHeight((int) theAttributes.getPreferredSize().getHeight());
    }

    @Override
    public void view2model(Object aModel, String aPropertyName) {

        Relation theRelation = (Relation) aModel;
        DefaultTable theTable = (DefaultTable) getComponent()[0];
        AttributeTableModel theTableModel = (AttributeTableModel) theTable.getModel();

        theRelation.getMapping().clear();
        for (int i = 0; i < theTableModel.getRowCount(); i++) {
            IndexExpression theIndexExpression = (IndexExpression) theTableModel.getValueAt(i, 0);
            Attribute theAssignedAttribute = (Attribute) theTableModel.getValueAt(i, 1);

            theRelation.getMapping().put(theIndexExpression, theAssignedAttribute);
        }

    }

    @Override
    public List<ValidationError> validate() {
        DefaultTable theTable = (DefaultTable) getComponent()[0];
        List<ValidationError> theErrors = new ArrayList<ValidationError>();
        AttributeTableModel theTableModel = (AttributeTableModel) theTable.getModel();
        for (int i = 0; i < theTableModel.getRowCount(); i++) {
            Attribute theAssignedAttribute = (Attribute) theTableModel.getValueAt(i, 1);
            if (theAssignedAttribute == null) {
                theErrors.add(new ValidationError(this, BINDINGHELPER.getText(BindingBundle.MISSINGREQUIREDFIELD)));
            }
        }

        if (theTableModel.getRowCount() == 0) {
            theErrors.add(new ValidationError(this, BINDINGHELPER.getText(BindingBundle.MISSINGREQUIREDFIELD)));
        }

        if (theErrors.size() == 0) {
            markValid();
        } else {
            markInvalid(theErrors);
        }
        return theErrors;
    }
}
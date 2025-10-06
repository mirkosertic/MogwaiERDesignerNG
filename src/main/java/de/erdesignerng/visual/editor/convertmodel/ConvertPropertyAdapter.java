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
package de.erdesignerng.visual.editor.convertmodel;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.dialect.ConversionInfos;
import de.erdesignerng.dialect.DataType;
import de.mogwai.common.client.binding.BindingBundle;
import de.mogwai.common.client.binding.PropertyAdapter;
import de.mogwai.common.client.binding.validator.ValidationError;
import de.mogwai.common.client.looks.components.DefaultComboBox;
import de.mogwai.common.client.looks.components.DefaultTable;
import de.mogwai.common.i18n.ResourceHelper;
import org.apache.commons.beanutils.BeanComparator;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class ConvertPropertyAdapter extends PropertyAdapter {

    private static final ResourceHelper BINDINGHELPER = ResourceHelper.getResourceHelper(BindingBundle.BUNDLE_NAME);

    private final ResourceHelper helper;

    public ConvertPropertyAdapter(final JComponent aComponent, final String aPropertyName, final ResourceHelper aHelper) {
        super(aComponent, aPropertyName);
        helper = aHelper;
    }

    @Override
    public void model2view(final Object aModel, final String aPropertyName) {

        final ConversionInfos theInfos = (ConversionInfos) aModel;

        final String theCurrentTypeName = helper.getText(ERDesignerBundle.CURRENTDATATYPE);
        final String theTargetTypeName = helper.getText(ERDesignerBundle.TARGETDATATYPE);

        final DataType[] theTargetTypes = new DataType[theInfos.getTypeMapping().keySet().size()];

        final List<DataType> theCurrentTypes = new ArrayList<>();
        theCurrentTypes.addAll(theInfos.getTypeMapping().keySet());

        theCurrentTypes.sort(new BeanComparator("name"));
        for (int i = 0; i < theCurrentTypes.size(); i++) {
            theTargetTypes[i] = theInfos.getTypeMapping().get(theCurrentTypes.get(i));
        }

        final DefaultTable theTable = (DefaultTable) getComponent()[0];
        final ConversionTableModel theModel = new ConversionTableModel(theCurrentTypeName, theTargetTypeName,
                theCurrentTypes, theTargetTypes);
        theTable.setModel(theModel);

        final DefaultComboBox theTargetTypesEditor = new DefaultComboBox();
        theTargetTypesEditor.setBorder(BorderFactory.createEmptyBorder());
        theTargetTypesEditor.setModel(new DefaultComboBoxModel(theInfos.getTargetDialect().getDataTypes().toArray(
                new DataType[theInfos.getTargetDialect().getDataTypes().size()])));
        theTable.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(theTargetTypesEditor));
        theTable.setRowHeight((int) theTargetTypesEditor.getPreferredSize().getHeight());
    }

    @Override
    public void view2model(final Object aModel, final String aPropertyName) {
        final ConversionInfos theConversionInfos = (ConversionInfos) aModel;
        final DefaultTable theTable = (DefaultTable) getComponent()[0];
        final ConversionTableModel theTableModel = (ConversionTableModel) theTable.getModel();

        theConversionInfos.getTypeMapping().clear();
        for (int i = 0; i < theTableModel.getRowCount(); i++) {
            final DataType theSourceType = (DataType) theTableModel.getValueAt(i, 0);
            final DataType theTargetType = (DataType) theTableModel.getValueAt(i, 1);

            theConversionInfos.getTypeMapping().put(theSourceType, theTargetType);
        }
    }

    @Override
    public List<ValidationError> validate() {
        final DefaultTable theTable = (DefaultTable) getComponent()[0];
        final List<ValidationError> theErrors = new ArrayList<>();
        final ConversionTableModel theTableModel = (ConversionTableModel) theTable.getModel();
        for (int i = 0; i < theTableModel.getRowCount(); i++) {
            final DataType theAssignedAttribute = (DataType) theTableModel.getValueAt(i, 1);
            if (theAssignedAttribute == null) {
                theErrors.add(new ValidationError(this, BINDINGHELPER.getText(BindingBundle.MISSINGREQUIREDFIELD)));
            }
        }

        if (theTableModel.getRowCount() == 0) {
            theErrors.add(new ValidationError(this, BINDINGHELPER.getText(BindingBundle.MISSINGREQUIREDFIELD)));
        }

        if (theErrors.isEmpty()) {
            markValid();
        } else {
            markInvalid(theErrors);
        }
        return theErrors;
    }
}
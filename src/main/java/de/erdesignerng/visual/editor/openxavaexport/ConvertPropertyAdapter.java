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
package de.erdesignerng.visual.editor.openxavaexport;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.dialect.DataType;
import de.erdesignerng.generator.openxava.OpenXavaOptions;
import de.erdesignerng.generator.openxava.OpenXavaTypeMap;
import de.erdesignerng.visual.editor.ModelItemDefaultCellRenderer;
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

        final OpenXavaOptions theOptions = (OpenXavaOptions) aModel;

        final String theCurrentTypeName = helper.getText(ERDesignerBundle.CURRENTDATATYPE);
        final String theTargetTypeName = helper.getText(ERDesignerBundle.TARGETDATATYPE);
        final String theStereoTypeName = helper.getText(ERDesignerBundle.STEREOTYPE);

        final String[] theTargetTypes = new String[theOptions.getTypeMapping().keySet().size()];
        final String[] theStereoTypes = new String[theOptions.getTypeMapping().keySet().size()];

        final List<DataType> theCurrentTypes = new ArrayList<>();
        theCurrentTypes.addAll(theOptions.getTypeMapping().keySet());

        theCurrentTypes.sort(new BeanComparator("name"));
        int theRow = 0;
        for (final DataType theType : theCurrentTypes) {
            final OpenXavaTypeMap theMap = theOptions.getTypeMapping().get(theType);
            theTargetTypes[theRow] = theMap.getJavaType();
            theStereoTypes[theRow] = theMap.getStereoType();
            theRow++;
        }

        final DefaultTable theTable = (DefaultTable) getComponent()[0];
        final OpenXavaExportTableModel theModel = new OpenXavaExportTableModel(theCurrentTypeName, theTargetTypeName,
                theStereoTypeName, theCurrentTypes, theTargetTypes, theStereoTypes);
        theTable.setModel(theModel);

        theTable.getColumnModel().getColumn(0).setCellRenderer(ModelItemDefaultCellRenderer.getInstance());
        theTable.getColumnModel().getColumn(1).setCellRenderer(ModelItemDefaultCellRenderer.getInstance());
        theTable.getColumnModel().getColumn(2).setCellRenderer(ModelItemDefaultCellRenderer.getInstance());

        final DefaultComboBox theTargetTypesEditor = new DefaultComboBox();
        theTargetTypesEditor.setBorder(BorderFactory.createEmptyBorder());
        theTargetTypesEditor.setModel(new DefaultComboBoxModel(OpenXavaOptions.SUPPORTED_STEREOTYPES));
        theTable.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(theTargetTypesEditor));
        theTable.setRowHeight((int) theTargetTypesEditor.getPreferredSize().getHeight());
    }

    @Override
    public void view2model(final Object aModel, final String aPropertyName) {
        final OpenXavaOptions theConversionInfos = (OpenXavaOptions) aModel;
        final DefaultTable theTable = (DefaultTable) getComponent()[0];
        final OpenXavaExportTableModel theTableModel = (OpenXavaExportTableModel) theTable.getModel();

        theConversionInfos.getTypeMapping().clear();
        for (int i = 0; i < theTableModel.getRowCount(); i++) {
            final DataType theSourceType = (DataType) theTableModel.getValueAt(i, 0);
            final String theJavaType = (String) theTableModel.getValueAt(i, 1);
            final String theStereoType = (String) theTableModel.getValueAt(i, 2);

            final OpenXavaTypeMap theMapping = new OpenXavaTypeMap();
            theMapping.setJavaType(theJavaType);
            theMapping.setStereoType(theStereoType);

            theConversionInfos.getTypeMapping().put(theSourceType, theMapping);
        }
    }

    @Override
    public List<ValidationError> validate() {
        final DefaultTable theTable = (DefaultTable) getComponent()[0];
        final List<ValidationError> theErrors = new ArrayList<>();
        final OpenXavaExportTableModel theTableModel = (OpenXavaExportTableModel) theTable.getModel();
        for (int i = 0; i < theTableModel.getRowCount(); i++) {
            // A Datatype mapping must be there, the other things are optional
            final String theAssignedAttribute = (String) theTableModel.getValueAt(i, 1);
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
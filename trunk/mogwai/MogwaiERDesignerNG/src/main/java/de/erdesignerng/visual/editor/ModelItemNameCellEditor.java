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
package de.erdesignerng.visual.editor;

import de.erdesignerng.dialect.Dialect;
import de.erdesignerng.exception.ElementInvalidNameException;
import de.erdesignerng.model.ModelItem;
import de.mogwai.common.client.looks.components.DefaultTextField;
import java.awt.Color;
import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JTable;

public class ModelItemNameCellEditor<T extends ModelItem> extends DefaultCellEditor {

    private ModelItemTableModel<T> model;
    private T currentAttribute;
    private Dialect dialect;

    public ModelItemNameCellEditor(Dialect aDialect) {
        super(new DefaultTextField());
        dialect = aDialect;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {

        model = (ModelItemTableModel<T>) table.getModel();
        currentAttribute = model.getRow(row);

        DefaultTextField theTextfield = (DefaultTextField) getComponent();
        theTextfield.setBorder(BorderFactory.createLineBorder(Color.black));

        return super.getTableCellEditorComponent(table, value, isSelected, row, column);
    }

    private boolean isValidInput(DefaultTextField aTextfield) {

        String theTempName = aTextfield.getText();
        try {
            dialect.checkName(theTempName);
        } catch (ElementInvalidNameException e) {
            return false;
        }

        for (int i = 0; i < model.getRowCount(); i++) {
            T theTempAttribute = model.getRow(i);
            if (theTempAttribute != currentAttribute) {
                boolean theSame;
                if (dialect.isCaseSensitive()) {
                    theSame = theTempAttribute.getName().equals(theTempName);
                } else {
                    theSame = theTempAttribute.getName().equalsIgnoreCase(theTempName);
                }
                if (theSame) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean stopCellEditing() {
        DefaultTextField theTextfield = (DefaultTextField) getComponent();
        if (isValidInput(theTextfield)) {
            return super.stopCellEditing();
        }

        theTextfield.setBorder(BorderFactory.createLineBorder(Color.red));

        return false;
    }
}
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
import de.erdesignerng.model.Domain;
import de.erdesignerng.visual.editor.ModelItemTableModel;

public class DomainTableModel extends ModelItemTableModel<Domain> {

    public DomainTableModel() {
        columnNames.add(HELPER.getText(ERDesignerBundle.NAME));
        columnNames.add(HELPER.getText(ERDesignerBundle.DATATYPE));
        columnNames.add(HELPER.getText(ERDesignerBundle.SIZE));
        columnNames.add(HELPER.getText(ERDesignerBundle.FRACTION));
        columnNames.add(HELPER.getText(ERDesignerBundle.SCALE));
        columnNames.add(HELPER.getText(ERDesignerBundle.NULLABLE));
        columnNames.add(HELPER.getText(ERDesignerBundle.COMMENTS));
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                // name
                return String.class;
            case 1:
                // type
                return DataType.class;
            case 2:
                // size
                return Integer.class;
            case 3:
                // fraction
                return Integer.class;
            case 4:
                // scale
                return Integer.class;
            case 5:
                // nullable
                return Boolean.class;
            case 6:
                // comment
                return String.class;
        }
        throw new IllegalArgumentException("Wrong columnIndex : " + columnIndex);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        Domain theDomain = rowData.get(rowIndex);
        DataType theDataType = theDomain.getConcreteType();
        switch (columnIndex) {
            case 0:
                // name
                return true;
            case 1:
                // type
                return true;
            case 2:
                // size
                if (theDataType == null) {
                    // In case of an new attribute disable
                    // this until a datatype was chosen
                    return false;
                }
                if (theDataType.supportsSize()) {
                    return true;
                }
                return false;
            case 3:
                // fraction
                if (theDataType == null) {
                    // In case of an new attribute disable
                    // this until a datatype was chosen
                    return false;
                }
                if (theDataType.supportsFraction()) {
                    return true;
                }
                return false;
            case 4:
                // scale
                if (theDataType == null) {
                    // In case of an new attribute disable
                    // this until a datatype was chosen
                    return false;
                }
                if (theDataType.supportsScale()) {
                    return true;
                }
                return false;
            case 5:
                // nullable
                return true;
            case 6:
                // comment
                return true;
        }
        throw new IllegalArgumentException("Wrong columnIndex : " + columnIndex);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Domain theDomain = rowData.get(rowIndex);
        DataType theDataType = theDomain.getConcreteType();
        switch (columnIndex) {
            case 0:
                // name
                return theDomain.getName();
            case 1:
                // type
                return theDataType;
            case 2:
                // size
                if (theDataType == null) {
                    // In case of an new attribute show nothing
                    // until a datatype was chosen
                    return null;
                }

                if (theDataType.supportsSize() && !theDataType.isDomain()) {
                    return theDomain.getSize();
                }
                return null;
            case 3:
                // fraction
                if (theDataType == null) {
                    // In case of an new attribute show nothing
                    // until a datatype was chosen
                    return null;
                }

                if (theDataType.supportsFraction() && !theDataType.isDomain()) {
                    return theDomain.getFraction();
                }
                return null;
            case 4:
                // scale
                if (theDataType == null) {
                    // In case of an new attribute show nothing
                    // until a datatype was chosen
                    return null;
                }

                if (theDataType.supportsScale() && !theDataType.isDomain()) {
                    return theDomain.getScale();
                }
                return null;
            case 5:
                // nullable
                return theDomain.isNullable();
            case 6:
                // comment
                return theDomain.getComment();
        }
        throw new IllegalArgumentException("Wrong columnIndex : " + columnIndex);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Domain theAttribute = rowData.get(rowIndex);
        switch (columnIndex) {
            case 0:
                // name
                theAttribute.setName((String) aValue);
                break;
            case 1:
                // type
                DataType theType = (DataType) aValue;
                theAttribute.setConcreteType(theType);
                break;
            case 2:
                // size
                theAttribute.setSize((Integer) aValue);
                break;
            case 3:
                // fraction
                theAttribute.setFraction((Integer) aValue);
                break;
            case 4:
                // scale
                theAttribute.setScale((Integer) aValue);
                break;
            case 5:
                // nullable
                theAttribute.setNullable((Boolean) aValue);
                break;
            case 6:
                // comment
                theAttribute.setComment((String) aValue);
                break;
        }
    }
}
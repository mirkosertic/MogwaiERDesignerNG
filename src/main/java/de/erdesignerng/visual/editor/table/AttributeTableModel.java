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
package de.erdesignerng.visual.editor.table;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.dialect.DataType;
import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.Domain;
import de.mogwai.common.i18n.ResourceHelper;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class AttributeTableModel implements TableModel {

    private List<Attribute> rowData = new ArrayList<Attribute>();
    private List<String> columnNames = new ArrayList<String>();
    private List<TableModelListener> listener = new ArrayList<TableModelListener>();

    public AttributeTableModel() {
        ResourceHelper theHelper = ResourceHelper.getResourceHelper(ERDesignerBundle.BUNDLE_NAME);
        columnNames.add(theHelper.getText(ERDesignerBundle.NAME));
        columnNames.add(theHelper.getText(ERDesignerBundle.DATATYPE));
        columnNames.add(theHelper.getText(ERDesignerBundle.SIZE));
        columnNames.add(theHelper.getText(ERDesignerBundle.FRACTION));
        columnNames.add(theHelper.getText(ERDesignerBundle.SCALE));
        columnNames.add(theHelper.getText(ERDesignerBundle.NULLABLE));
        columnNames.add(theHelper.getText(ERDesignerBundle.DEFAULT));
        columnNames.add(theHelper.getText(ERDesignerBundle.EXTRA));
        columnNames.add(theHelper.getText(ERDesignerBundle.COMMENTS));
    }

    public void add(Attribute aAttribute) {
        if (!rowData.contains(aAttribute)) {
            rowData.add(aAttribute);
            fireTableChanged(new TableModelEvent(this));
        }
    }

    public void remove(Attribute aAttribute) {
        if (rowData.remove(aAttribute)) {
            fireTableChanged(new TableModelEvent(this));
        }
    }

    @Override
    public int getRowCount() {
        return rowData.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.size();
    }

    public Attribute getRow(int aRowIndex) {
        return rowData.get(aRowIndex);
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columnNames.get(columnIndex);
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
                // default
                return String.class;
            case 7:
                // extra
                return String.class;
            case 8:
                // comment
                return String.class;
        }
        throw new IllegalArgumentException("Wrong columnIndex : " + columnIndex);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        Attribute theAttribute = rowData.get(rowIndex);
        DataType theDataType = theAttribute.getDatatype();
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
                if (theDataType.supportsSize() && !theDataType.isDomain()) {
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
                if (theDataType.supportsFraction() && !theDataType.isDomain()) {
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
                if (theDataType.supportsScale() && !theDataType.isDomain()) {
                    return true;
                }
                return false;
            case 5:
                // nullable
                return true;
            case 6:
                // default
                return true;
            case 7:
                // extra
                if (theDataType == null) {
                    // In case of an new attribute disable
                    // this until a datatype was chosen
                    return false;
                }

                if (theDataType.supportsExtra()) {
                    return true;
                }
                return false;
            case 8:
                // comment
                return true;
        }
        throw new IllegalArgumentException("Wrong columnIndex : " + columnIndex);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Attribute theAttribute = rowData.get(rowIndex);
        DataType theDataType = theAttribute.getDatatype();
        switch (columnIndex) {
            case 0:
                // name
                return theAttribute.getName();
            case 1:
                // type
                return theAttribute.getDatatype();
            case 2:
                // size
                if (theDataType == null) {
                    // In case of an new attribute show nothing
                    // until a datatype was chosen
                    return null;
                }

                if (theDataType.supportsSize() && !theDataType.isDomain()) {
                    return theAttribute.getSize();
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
                    return theAttribute.getFraction();
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
                    return theAttribute.getScale();
                }
                return null;
            case 5:
                // nullable
                return theAttribute.isNullable();
            case 6:
                // default
                return theAttribute.getDefaultValue();
            case 7:
                // extra
                if (theDataType == null) {
                    // In case of an new attribute show nothing
                    // until a datatype was chosen
                    return null;
                }

                if (theDataType.supportsExtra()) {
                    return theAttribute.getExtra();
                }
                return null;
            case 8:
                // comment
                return theAttribute.getComment();
        }
        throw new IllegalArgumentException("Wrong columnIndex : " + columnIndex);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Attribute theAttribute = rowData.get(rowIndex);
        switch (columnIndex) {
            case 0:
                // name
                theAttribute.setName((String) aValue);
                break;
            case 1:
                // type
                DataType theType = (DataType) aValue;
                theAttribute.setDatatype(theType);
                if (theType != null && theType.isDomain()) {
                    // If the new datatype is a domain, set the nullable flag according to the domain
                    theAttribute.setNullable(((Domain) theType).isNullable());
                }
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
                // default
                theAttribute.setDefaultValue((String) aValue);
                break;
            case 7:
                // extra
                theAttribute.setExtra((String) aValue);
                break;
            case 8:
                // comment
                theAttribute.setComment((String) aValue);
                break;
        }
    }

    @Override
    public void addTableModelListener(TableModelListener l) {
        listener.add(l);
    }

    @Override
    public void removeTableModelListener(TableModelListener l) {
        listener.remove(l);
    }

    private void fireTableChanged(TableModelEvent e) {
        for (TableModelListener theListeber : listener) {
            theListeber.tableChanged(e);
        }
    }

    public boolean contains(Attribute aAttribute) {
        return rowData.contains(aAttribute);
    }

    public int getRowIndex(Attribute aAttribute) {
        return rowData.indexOf(aAttribute);
    }
}
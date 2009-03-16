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

import java.util.List;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import de.erdesignerng.dialect.DataType;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-09 19:07:31 $
 */
public class ConversionTableModel implements TableModel {

    private List<DataType> dataTypes;

    private DataType[] targetTypes;

    private String sourceName;

    private String targetName;

    public ConversionTableModel(String aSourceName, String aTargetName, List<DataType> aDataTypes,
            DataType[] aTargetTypes) {
        sourceName = aSourceName;
        targetName = aTargetName;
        dataTypes = aDataTypes;
        targetTypes = aTargetTypes;
    }

    public Class<Object> getColumnClass(int aColumn) {
        return Object.class;
    }

    public int getColumnCount() {
        return 2;
    }

    public String getColumnName(int aColumn) {
        if (aColumn == 0) {
            return sourceName;
        } else {
            return targetName;
        }
    }

    public int getRowCount() {
        return dataTypes.size();
    }

    public Object getValueAt(int aRow, int aColumn) {
        if (aColumn == 1) {
            return targetTypes[aRow];
        } else {
            return dataTypes.get(aRow);
        }
    }

    public boolean isCellEditable(int aRow, int aColumn) {
        return aColumn != 0;
    }

    public void setValueAt(Object aValue, int aRow, int aColumn) {
        targetTypes[aRow] = (DataType) aValue;
    }

    public void addTableModelListener(TableModelListener l) {
    }

    public void removeTableModelListener(TableModelListener l) {
    }
};

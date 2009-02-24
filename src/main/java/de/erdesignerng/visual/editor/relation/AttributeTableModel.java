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

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.Index;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2009-02-24 19:36:28 $
 */
public class AttributeTableModel implements TableModel {

    private Index primaryKey;

    private Attribute[] assignedAttributes;

    private String primaryKeyTableName;

    private String assignedTableName;

    public AttributeTableModel(String aPrimaryEntityName, String aSecondaryEntityName, Index aPrimaryKey,
            Attribute[] aSecondaryKey) {
        primaryKey = aPrimaryKey;
        assignedAttributes = aSecondaryKey;
        primaryKeyTableName = aPrimaryEntityName;
        assignedTableName = aSecondaryEntityName;
    }

    public Class<Object> getColumnClass(int aColumn) {
        return Object.class;
    }

    public int getColumnCount() {
        return 2;
    }

    public String getColumnName(int aColumn) {
        if (aColumn == 0) {
            return primaryKeyTableName;
        } else {
            return assignedTableName;
        }
    }

    public int getRowCount() {
        return primaryKey.getExpressions().size();
    }

    public Object getValueAt(int aRow, int aColumn) {
        if (aColumn == 1) {
            return assignedAttributes[aRow];
        } else {
            return primaryKey.getExpressions().get(aRow);
        }
    }

    public boolean isCellEditable(int aRow, int aColumn) {
        return aColumn != 0;
    }

    public void setValueAt(Object aValue, int aRow, int aColumn) {
        assignedAttributes[aRow] = (Attribute) aValue;
    }

    public void addTableModelListener(TableModelListener l) {
    }

    public void removeTableModelListener(TableModelListener l) {
    }
};

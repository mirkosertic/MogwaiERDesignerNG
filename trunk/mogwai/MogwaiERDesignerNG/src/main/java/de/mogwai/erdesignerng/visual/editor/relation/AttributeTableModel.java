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
package de.mogwai.erdesignerng.visual.editor.relation;

import java.util.List;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import de.mogwai.erdesignerng.model.Attribute;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2007-07-08 17:55:43 $
 */
public class AttributeTableModel implements TableModel {

	private List<Attribute> primaryKey;

	private Attribute[] assignedAttributes;

	private String primaryKeyTableName;

	private String assignedTableName;

	public AttributeTableModel(String aPrimaryEntityName,
			String aSecondaryEntityName, List<Attribute> aPrimaryKey,
			Attribute[] aSecondaryKey) {
		this.primaryKey = aPrimaryKey;
		this.assignedAttributes = aSecondaryKey;
		this.primaryKeyTableName = aPrimaryEntityName;
		this.assignedTableName = aSecondaryEntityName;
	}

	public Class getColumnClass(int aColumn) {
		return Object.class;
	}

	public int getColumnCount() {
		return 2;
	}

	public String getColumnName(int aColumn) {
		if (aColumn == 0)
			return this.primaryKeyTableName;
		else
			return this.assignedTableName;
	}

	public int getRowCount() {
		return this.primaryKey.size();
	}

	public Object getValueAt(int aRow, int aColumn) {
		if (aColumn == 1)
			return this.assignedAttributes[aRow];
		else
			return this.primaryKey.get(aRow);
	}

	public boolean isCellEditable(int aRow, int aColumn) {
		return aColumn != 0;
	}

	public void setValueAt(Object aValue, int aRow, int aColumn) {
		this.assignedAttributes[aRow] = (Attribute) aValue;
	}

	public void addTableModelListener(TableModelListener l) {
	}

	public void removeTableModelListener(TableModelListener l) {
	}
};

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

import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.Index;

import de.erdesignerng.model.Table;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-09 19:07:31 $
 */
public class AttributeTableModel implements TableModel {

	private final Index primaryKey;

	private final Attribute<Table>[] assignedAttributes;

	private final String primaryKeyTableName;

	private final String assignedTableName;

	public AttributeTableModel(final String aPrimaryEntityName, final String aSecondaryEntityName, final Index aPrimaryKey,
                               final Attribute<Table>[] aSecondaryKey) {
		primaryKey = aPrimaryKey;
		assignedAttributes = aSecondaryKey;
		primaryKeyTableName = aPrimaryEntityName;
		assignedTableName = aSecondaryEntityName;
	}

	@Override
	public Class<Object> getColumnClass(final int aColumn) {
		return Object.class;
	}

	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public String getColumnName(final int aColumn) {
		if (aColumn == 0) {
			return primaryKeyTableName;
		}
		return assignedTableName;
	}

	@Override
	public int getRowCount() {
		return primaryKey.getExpressions().size();
	}

	@Override
	public Object getValueAt(final int aRow, final int aColumn) {
		if (aColumn == 1) {
			return assignedAttributes[aRow];
		}
		return primaryKey.getExpressions().get(aRow);
	}

	@Override
	public boolean isCellEditable(final int aRow, final int aColumn) {
		return aColumn != 0;
	}

	@Override
	public void setValueAt(final Object aValue, final int aRow, final int aColumn) {
		assignedAttributes[aRow] = (Attribute<Table>) aValue;
	}

	@Override
	public void addTableModelListener(final TableModelListener l) {
	}

	@Override
	public void removeTableModelListener(final TableModelListener l) {
	}
}

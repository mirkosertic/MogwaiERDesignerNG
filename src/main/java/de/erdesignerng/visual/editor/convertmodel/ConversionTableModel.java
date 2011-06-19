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

import de.erdesignerng.dialect.DataType;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.util.List;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-09 19:07:31 $
 */
public class ConversionTableModel implements TableModel {

	private final List<DataType> dataTypes;

	private final DataType[] targetTypes;

	private final String sourceName;

	private final String targetName;

	public ConversionTableModel(String aSourceName, String aTargetName, List<DataType> aDataTypes,
			DataType[] aTargetTypes) {
		sourceName = aSourceName;
		targetName = aTargetName;
		dataTypes = aDataTypes;
		targetTypes = aTargetTypes;
	}

	@Override
	public Class<Object> getColumnClass(int aColumn) {
		return Object.class;
	}

	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public String getColumnName(int aColumn) {
		if (aColumn == 0) {
			return sourceName;
		}
		return targetName;
	}

	@Override
	public int getRowCount() {
		return dataTypes.size();
	}

	@Override
	public Object getValueAt(int aRow, int aColumn) {
		if (aColumn == 1) {
			return targetTypes[aRow];
		}
		return dataTypes.get(aRow);
	}

	@Override
	public boolean isCellEditable(int aRow, int aColumn) {
		return aColumn != 0;
	}

	@Override
	public void setValueAt(Object aValue, int aRow, int aColumn) {
		targetTypes[aRow] = (DataType) aValue;
	}

	@Override
	public void addTableModelListener(TableModelListener l) {
	}

	@Override
	public void removeTableModelListener(TableModelListener l) {
	}
}

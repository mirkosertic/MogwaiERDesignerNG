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

import de.erdesignerng.dialect.DataType;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.util.List;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-09 19:07:31 $
 */
public class OpenXavaExportTableModel implements TableModel {

	private final List<DataType> dataTypes;

	private final String[] targetTypes;

	private final String[] stereoTypes;

	private final String sourceName;

	private final String targetName;

	private final String stereoTypeName;

	public OpenXavaExportTableModel(String aSourceName, String aTargetName, String aStereoTypeName,
			List<DataType> aDataTypes, String[] aTargetTypes, String[] aStereoTypes) {
		sourceName = aSourceName;
		targetName = aTargetName;
		dataTypes = aDataTypes;
		targetTypes = aTargetTypes;
		stereoTypes = aStereoTypes;
		stereoTypeName = aStereoTypeName;
	}

	@Override
	public Class<Object> getColumnClass(int aColumn) {
		return Object.class;
	}

	@Override
	public int getColumnCount() {
		return 3;
	}

	@Override
	public String getColumnName(int aColumn) {
		switch (aColumn) {
		case 0:
			return sourceName;
		case 1:
			return targetName;
		default:
			return stereoTypeName;
		}
	}

	@Override
	public int getRowCount() {
		return dataTypes.size();
	}

	@Override
	public Object getValueAt(int aRow, int aColumn) {
		switch (aColumn) {
		case 0:
			return dataTypes.get(aRow);
		case 1:
			return targetTypes[aRow];
		default:
			return stereoTypes[aRow];
		}
	}

	@Override
	public boolean isCellEditable(int aRow, int aColumn) {
		return aColumn != 0;
	}

	@Override
	public void setValueAt(Object aValue, int aRow, int aColumn) {
		switch (aColumn) {
		case 1:
			targetTypes[aRow] = (String) aValue;
			break;
		case 2:
			stereoTypes[aRow] = (String) aValue;
			break;
		}
	}

	@Override
	public void addTableModelListener(TableModelListener l) {
	}

	@Override
	public void removeTableModelListener(TableModelListener l) {
	}
}

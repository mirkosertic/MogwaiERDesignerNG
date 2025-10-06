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

	public OpenXavaExportTableModel(final String aSourceName, final String aTargetName, final String aStereoTypeName,
                                    final List<DataType> aDataTypes, final String[] aTargetTypes, final String[] aStereoTypes) {
		sourceName = aSourceName;
		targetName = aTargetName;
		dataTypes = aDataTypes;
		targetTypes = aTargetTypes;
		stereoTypes = aStereoTypes;
		stereoTypeName = aStereoTypeName;
	}

	@Override
	public Class<Object> getColumnClass(final int aColumn) {
		return Object.class;
	}

	@Override
	public int getColumnCount() {
		return 3;
	}

	@Override
	public String getColumnName(final int aColumn) {
        return switch (aColumn) {
            case 0 -> sourceName;
            case 1 -> targetName;
            default -> stereoTypeName;
        };
	}

	@Override
	public int getRowCount() {
		return dataTypes.size();
	}

	@Override
	public Object getValueAt(final int aRow, final int aColumn) {
        return switch (aColumn) {
            case 0 -> dataTypes.get(aRow);
            case 1 -> targetTypes[aRow];
            default -> stereoTypes[aRow];
        };
	}

	@Override
	public boolean isCellEditable(final int aRow, final int aColumn) {
		return aColumn != 0;
	}

	@Override
	public void setValueAt(final Object aValue, final int aRow, final int aColumn) {
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
	public void addTableModelListener(final TableModelListener l) {
	}

	@Override
	public void removeTableModelListener(final TableModelListener l) {
	}
}

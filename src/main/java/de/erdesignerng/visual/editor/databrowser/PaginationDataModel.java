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
package de.erdesignerng.visual.editor.databrowser;

import de.erdesignerng.dialect.Dialect;
import de.erdesignerng.util.JDBCUtils;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaginationDataModel extends AbstractTableModel {

	private ResultSet resultSet;
	private ResultSetMetaData metadata;
	private int currentRow = -1;
	private int rowCount = 1;
	private List<List<Object>> cache = new ArrayList<List<Object>>();
    private boolean lastRecordReached;

    public ResultSetMetaData getResultSetMetaData() {
        return metadata;
    }

    public Map<String, Object> getRowData(int aRow) throws SQLException {
        Map<String, Object> theRow = new HashMap<String, Object>();
        List<Object> theRowData = cache.get(aRow);
        for (int i=0;i<theRowData.size();i++) {
            String theName = metadata.getColumnName(i+1);
            theRow.put(theName, theRowData.get(i));
        }
        return theRow;
    }

    public interface SeekListener {
		void seeked();
	}

	private List<SeekListener> seekListener = new ArrayList<SeekListener>();
	private JTable owner;
	private Dialect dialect;

	public PaginationDataModel(Dialect aDialect, JTable aTable,
			ResultSet aResultSet) throws SQLException {
		owner = aTable;
		resultSet = aResultSet;
		metadata = aResultSet.getMetaData();
		dialect = aDialect;
		if (aResultSet.isLast()) {
			rowCount = 0;
		}
	}

	public void addSeekListener(SeekListener aListener) {
		seekListener.add(aListener);
	}

	@Override
	public int getColumnCount() {
		try {
			return metadata.getColumnCount();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getColumnName(int column) {
		try {
			return dialect.getCastType().cast(
					metadata.getColumnName(column + 1));
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	@Override
	public int getRowCount() {
		return rowCount;
	}

	public void seekToRow(int aRowIndex) throws SQLException {

		boolean seeked = false;
		while (currentRow < aRowIndex && resultSet.next()) {

			List<Object> theRow = new ArrayList<Object>();
			for (int i = 1; i <= metadata.getColumnCount(); i++) {
				theRow.add(resultSet.getObject(i));
			}

			cache.add(theRow);

			currentRow++;
			rowCount++;
			seeked = true;
		}

        if (resultSet.isLast() && !lastRecordReached) {
            rowCount--;
            lastRecordReached = true;
        }

		if (currentRow < aRowIndex) {
			rowCount = currentRow + 1;
			seeked = true;
		}

		if (seeked) {

			for (SeekListener theListener : seekListener) {
				theListener.seeked();
			}

			int selectedRow = owner.getSelectedRow();

			fireTableDataChanged();

			if (selectedRow >= 0) {
				owner.setRowSelectionInterval(selectedRow, selectedRow);
			}
		}

	}

	@Override
	public Object getValueAt(int aRowIndex, int aColumnIndex) {
		try {
			seekToRow(aRowIndex);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		List<Object> theRow = cache.get(aRowIndex);
		return theRow.get(aColumnIndex);
	}

	public void cleanup() {
		JDBCUtils.closeQuietly(resultSet);
	}

	public int computeColumnWidth(int aColumnIndex) {
		int width = 0;
		for (int i = 0; i < rowCount - 1; i++) {
			Object theValue = getValueAt(i, aColumnIndex);
			if (theValue != null) {
				String theString = theValue.toString();
				if (theString.length() > width) {
					width = theString.length();
				}
			}
		}
		return width;
	}
}
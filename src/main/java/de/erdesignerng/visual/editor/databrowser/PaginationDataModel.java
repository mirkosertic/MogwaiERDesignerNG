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

	private final ResultSet resultSet;
	private final ResultSetMetaData metadata;
	private int currentRow = -1;
	private int rowCount = 1;
	private final List<List<Object>> cache = new ArrayList<>();
	private boolean lastRecordReached;

	public ResultSetMetaData getResultSetMetaData() {
		return metadata;
	}

	public Map<String, Object> getRowData(final int aRow) throws SQLException {
		final Map<String, Object> theRow = new HashMap<>();
		final List<Object> theRowData = cache.get(aRow);
		for (int i=0;i<theRowData.size();i++) {
			final String theName = metadata.getColumnName(i+1);
			theRow.put(theName, theRowData.get(i));
		}
		return theRow;
	}

	public interface SeekListener {
		void seeked();
	}

	private final List<SeekListener> seekListener = new ArrayList<>();
	private final JTable owner;
	private final Dialect dialect;

	public PaginationDataModel(final Dialect aDialect, final JTable aTable,
                               final ResultSet aResultSet) throws SQLException {
		owner = aTable;
		resultSet = aResultSet;
		metadata = aResultSet.getMetaData();
		dialect = aDialect;
		if (aResultSet.isLast()) {
			rowCount = 0;
		}
	}

	public void addSeekListener(final SeekListener aListener) {
		seekListener.add(aListener);
	}

	@Override
	public int getColumnCount() {
		try {
			return metadata.getColumnCount();
		} catch (final SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getColumnName(final int column) {
		try {
			return dialect.getCastType().cast(
					metadata.getColumnName(column + 1));
		} catch (final SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean isCellEditable(final int rowIndex, final int columnIndex) {
		return false;
	}

	@Override
	public int getRowCount() {
		return rowCount;
	}

	public void seekToRow(final int aRowIndex) throws SQLException {

		boolean seeked = false;
		while (currentRow < aRowIndex && resultSet.next()) {

			final List<Object> theRow = new ArrayList<>();
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

			for (final SeekListener theListener : seekListener) {
				theListener.seeked();
			}

			final int selectedRow = owner.getSelectedRow();

			fireTableDataChanged();

			if (selectedRow >= 0) {
				owner.setRowSelectionInterval(selectedRow, selectedRow);
			}
		}

	}

	@Override
	public Object getValueAt(final int aRowIndex, final int aColumnIndex) {
		try {
			seekToRow(aRowIndex);
		} catch (final SQLException e) {
			throw new RuntimeException(e);
		}
		final List<Object> theRow = cache.get(aRowIndex);
		return theRow.get(aColumnIndex);
	}

	public void cleanup() {
		JDBCUtils.closeQuietly(resultSet);
	}

	public int computeColumnWidth(final int aColumnIndex) {
		int width = 0;
		for (int i = 0; i < rowCount - 1; i++) {
			final Object theValue = getValueAt(i, aColumnIndex);
			if (theValue != null) {
				final String theString = theValue.toString();
				if (theString.length() > width) {
					width = theString.length();
				}
			}
		}
		return width;
	}
}
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
package de.erdesignerng.visual.editor;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.model.ModelItem;
import de.mogwai.common.i18n.ResourceHelper;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public abstract class ModelItemTableModel<T extends ModelItem> implements TableModel {

    public static final ResourceHelper HELPER = ResourceHelper.getResourceHelper(ERDesignerBundle.BUNDLE_NAME);

    protected final List<T> rowData = new ArrayList<>();
    protected final List<String> columnNames = new ArrayList<>();
    protected final List<TableModelListener> listener = new ArrayList<>();

    public void add(final T aAttribute) {
        if (!rowData.contains(aAttribute)) {
            rowData.add(aAttribute);
            fireTableChanged(new TableModelEvent(this));
        }
    }

    public void remove(final T aAttribute) {
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

    public T getRow(final int aRowIndex) {
        return rowData.get(aRowIndex);
    }

    @Override
    public String getColumnName(final int columnIndex) {
        return columnNames.get(columnIndex);
    }

    @Override
    public void addTableModelListener(final TableModelListener l) {
        listener.add(l);
    }

    @Override
    public void removeTableModelListener(final TableModelListener l) {
        listener.remove(l);
    }

    private void fireTableChanged(final TableModelEvent e) {
        for (final TableModelListener theListeber : listener) {
            theListeber.tableChanged(e);
        }
    }

    public boolean contains(final T aAttribute) {
        return rowData.contains(aAttribute);
    }

    public int getRowIndex(final T aAttribute) {
        return rowData.indexOf(aAttribute);
    }
}
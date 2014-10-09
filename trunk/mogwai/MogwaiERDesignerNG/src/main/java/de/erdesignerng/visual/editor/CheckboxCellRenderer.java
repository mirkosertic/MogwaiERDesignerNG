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

import de.mogwai.common.client.looks.UIInitializer;
import java.awt.Component;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public final class CheckboxCellRenderer extends JCheckBox implements TableCellRenderer {

    private static final CheckboxCellRenderer me = new CheckboxCellRenderer();

    private final UIInitializer initializer;

    public static CheckboxCellRenderer getInstance() {
        return me;
    }

    private CheckboxCellRenderer() {
        setOpaque(true);
        setVerticalAlignment(TOP);
        setHorizontalAlignment(CENTER);

        initializer = UIInitializer.getInstance();
        initializer.initializeComponent(this);
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                   int row, int column) {

        if (value != null) {
            setSelected((Boolean) value);
        } else {
            setSelected(false);
        }
        if (isSelected) {
            setBackground(initializer.getConfiguration().getDefaultListSelectionBackground());
            setForeground(initializer.getConfiguration().getDefaultListSelectionForeground());
        } else {
            setBackground(initializer.getConfiguration().getDefaultListNonSelectionBackground());
            setForeground(initializer.getConfiguration().getDefaultListNonSelectionForeground());
        }
        return this;
    }
}
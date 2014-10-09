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
import de.mogwai.common.client.looks.components.renderer.DefaultRenderer;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class ModelItemDefaultCellRenderer extends DefaultRenderer implements TableCellRenderer {

    public static final ModelItemDefaultCellRenderer ME = new ModelItemDefaultCellRenderer();

    private final UIInitializer initializer;

    public static ModelItemDefaultCellRenderer getInstance() {
        return ME;
    }

    private ModelItemDefaultCellRenderer() {

        initializer = UIInitializer.getInstance();
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                   int row, int column) {

        setText(objectToString(value));
        setIcon(objectToIcon(value));
        if (isSelected) {
            if (table.isCellEditable(row, column)) {
                setBackground(initializer.getConfiguration().getDefaultListSelectionBackground());
            } else {
                setBackground(Color.lightGray);
            }
            setForeground(initializer.getConfiguration().getDefaultListSelectionForeground());
        } else {
            if (table.isCellEditable(row, column)) {
                setBackground(initializer.getConfiguration().getDefaultListNonSelectionBackground());
            } else {
                setBackground(Color.lightGray);
            }
            setForeground(initializer.getConfiguration().getDefaultListNonSelectionForeground());
        }
        return this;
    }

}

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

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class TableHelper {

    private static final KeyStroke TABKEYSTROKE = KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0);

    private TableHelper() {
    }

    public static void processEditorRemovel(JTable aTable) {
        AWTEvent currentEvent = EventQueue.getCurrentEvent();
        if (currentEvent instanceof KeyEvent) {
            KeyEvent ke = (KeyEvent) currentEvent;
            if (KeyStroke.getKeyStrokeForEvent(ke).equals(TABKEYSTROKE)) {
                // Tab key was pressed, so edit next cell
                int selectedRow = aTable.getSelectedRow();
                if (ke.isShiftDown()) {
                    int selectedColumn = aTable.getSelectedColumn() - 1;
                    while (selectedColumn >= 0) {
                        if (aTable.getModel().isCellEditable(selectedRow, selectedColumn)) {
                            aTable.editCellAt(selectedRow, selectedColumn);
                            aTable.getEditorComponent().requestFocus();
                            return;
                        }
                        selectedColumn--;
                    }
                } else {
                    int selectedColumn = aTable.getSelectedColumn() + 1;
                    while (selectedColumn < aTable.getColumnCount()) {
                        if (aTable.getModel().isCellEditable(selectedRow, selectedColumn)) {
                            aTable.editCellAt(selectedRow, selectedColumn);
                            aTable.getEditorComponent().requestFocus();
                            return;
                        }
                        selectedColumn++;
                    }
                }
            }
        }

    }
}

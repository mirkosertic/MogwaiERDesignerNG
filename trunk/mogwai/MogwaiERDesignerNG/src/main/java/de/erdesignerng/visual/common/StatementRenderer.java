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
package de.erdesignerng.visual.common;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JList;
import javax.swing.ListCellRenderer;

import de.erdesignerng.dialect.Statement;
import de.mogwai.common.client.looks.UIInitializer;
import de.mogwai.common.client.looks.components.DefaultTextArea;

public class StatementRenderer implements ListCellRenderer {

    private DefaultTextArea component = new DefaultTextArea();

    private UIInitializer initializer = UIInitializer.getInstance();

    public Component getListCellRendererComponent(JList aList, Object aValue, int aIndex, boolean isSelected,
            boolean cellHasFocus) {
        Statement theStatement = (Statement) aValue;
        if (theStatement.isExecuted()) {
            component.setForeground(Color.BLACK);
        } else {
            component.setForeground(Color.GRAY);

        }

        component.setText(theStatement.getSql());
        if (isSelected) {
            component.setBackground(initializer.getConfiguration().getDefaultListSelectionBackground());
        } else {
            component.setBackground(initializer.getConfiguration().getDefaultListNonSelectionBackground());
        }

        return component;
    }
}
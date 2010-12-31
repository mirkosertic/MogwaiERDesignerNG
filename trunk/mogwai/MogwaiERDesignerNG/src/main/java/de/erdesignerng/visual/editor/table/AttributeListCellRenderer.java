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
package de.erdesignerng.visual.editor.table;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import de.erdesignerng.model.Attribute;
import de.erdesignerng.visual.IconFactory;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-06-13 16:49:00 $
 */
public class AttributeListCellRenderer implements ListCellRenderer {

	private final JPanel panel;

	private final JPanel labelPanel;

	private final JLabel label;

	private final JLabel keyLabel;

	private static final ImageIcon keyIcon = IconFactory.getKeyIcon();

	private final TableEditor editor;

	public AttributeListCellRenderer(TableEditor aEditor) {

		editor = aEditor;

		panel = new JPanel(new BorderLayout());
		labelPanel = new JPanel(new BorderLayout());

		label = new JLabel();
		label.setFont(label.getFont().deriveFont(Font.PLAIN));
		labelPanel.add(label);

		panel.add(labelPanel);
		panel.setOpaque(false);
		labelPanel.setOpaque(false);

		JPanel theLeft = new JPanel(new BorderLayout());
		keyLabel = new JLabel(keyIcon);
		theLeft.add(keyLabel);
		theLeft.setSize(20, 10);
		theLeft.setPreferredSize(new Dimension(10, 10));
		theLeft.setOpaque(false);

		panel.add(theLeft, BorderLayout.WEST);

		labelPanel.setBackground(new Color(221, 221, 233));
	}

	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {

		Attribute theAttribute = (Attribute) value;
		label.setText(theAttribute.getName());

		label.setForeground(Color.black);

		boolean isPrimaryKey = editor.isPrimaryKey(theAttribute);

		if (isPrimaryKey || theAttribute.isForeignKey()) {
			label.setForeground(Color.red);
		}

		keyLabel.setVisible(isPrimaryKey);

		labelPanel.setOpaque(isSelected);
		if (isSelected) {
			labelPanel.setBorder(BorderFactory.createLineBorder(new Color(160, 160, 180)));
		} else {
			labelPanel.setBorder(null);
		}

		return panel;
	}
}

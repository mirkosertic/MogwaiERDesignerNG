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

import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.Table;
import de.erdesignerng.visual.IconFactory;
import de.mogwai.common.client.looks.UIInitializer;
import java.awt.*;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-06-13 16:49:00 $
 */
public class AttributeListAttributeCellRenderer implements TableCellRenderer {

	private static final ImageIcon KEYICON = IconFactory.getKeyIcon();

	private final JPanel panel;

	private final JPanel labelPanel;

	private final JLabel label;

	private final JLabel keyLabel;

	private final TableEditor editor;

	public AttributeListAttributeCellRenderer(TableEditor aEditor) {

		editor = aEditor;

		panel = new JPanel(new BorderLayout());
		labelPanel = new JPanel(new BorderLayout());

		label = new JLabel();
		label.setFont(label.getFont().deriveFont(Font.PLAIN));
		labelPanel.add(label);

		panel.add(labelPanel);
		panel.setOpaque(true);
		labelPanel.setOpaque(true);

		JPanel theLeft = new JPanel(new BorderLayout());
		keyLabel = new JLabel(KEYICON);
		theLeft.add(keyLabel);
		theLeft.setSize(20, 10);
		theLeft.setPreferredSize(new Dimension(10, 10));
		theLeft.setOpaque(false);

		panel.add(theLeft, BorderLayout.WEST);

		labelPanel.setBackground(new Color(221, 221, 233));
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

		AttributeTableModel theModel = (AttributeTableModel) table.getModel();
		Attribute<Table> theAttribute = theModel.getRow(row);

		label.setText(theAttribute.getName());
		label.setForeground(Color.black);

		boolean isPrimaryKey = editor.isPrimaryKey(theAttribute);

		if (isPrimaryKey || theAttribute.isForeignKey()) {
			label.setForeground(Color.red);
		}

		keyLabel.setVisible(isPrimaryKey);

		UIInitializer initializer = UIInitializer.getInstance();

		if (isSelected) {
			labelPanel.setBackground(initializer.getConfiguration().getDefaultListSelectionBackground());
			labelPanel.setForeground(initializer.getConfiguration().getDefaultListSelectionForeground());
			keyLabel.setBackground(initializer.getConfiguration().getDefaultListSelectionBackground());
			keyLabel.setForeground(initializer.getConfiguration().getDefaultListSelectionForeground());
		} else {
			labelPanel.setBackground(initializer.getConfiguration().getDefaultListNonSelectionBackground());
			labelPanel.setForeground(initializer.getConfiguration().getDefaultListNonSelectionForeground());
			keyLabel.setBackground(initializer.getConfiguration().getDefaultListNonSelectionBackground());
			keyLabel.setForeground(initializer.getConfiguration().getDefaultListNonSelectionForeground());
		}

		return panel;
	}
}

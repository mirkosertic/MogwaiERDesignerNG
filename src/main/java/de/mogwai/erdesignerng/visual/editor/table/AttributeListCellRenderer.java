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
package de.mogwai.erdesignerng.visual.editor.table;

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

import de.mogwai.erdesignerng.model.Attribute;
import de.mogwai.erdesignerng.visual.IconFactory;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2007-07-08 17:55:43 $
 */
public class AttributeListCellRenderer implements ListCellRenderer {

	private JPanel m_panel;

	private JPanel m_labelpanel;

	private JLabel m_label;

	private JLabel m_key;

	private static ImageIcon key = IconFactory.getKeyIcon();

	public AttributeListCellRenderer() {
		this.m_panel = new JPanel(new BorderLayout());
		this.m_labelpanel = new JPanel(new BorderLayout());

		this.m_label = new JLabel();
		this.m_label.setFont(this.m_label.getFont().deriveFont(Font.PLAIN));
		this.m_labelpanel.add(this.m_label);

		this.m_panel.add(this.m_labelpanel);
		this.m_panel.setOpaque(false);
		this.m_labelpanel.setOpaque(false);

		JPanel left = new JPanel(new BorderLayout());
		this.m_key = new JLabel(key);
		left.add(this.m_key);
		left.setSize(20, 10);
		left.setPreferredSize(new Dimension(10, 10));
		left.setOpaque(false);

		this.m_panel.add(left, BorderLayout.WEST);

		this.m_labelpanel.setBackground(new Color(221, 221, 233));
	}

	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {

		Attribute attr = (Attribute) value;
		this.m_label.setText(attr.getName());

		if (attr.isPrimaryKey())
			this.m_label.setForeground(Color.red);
		else
			this.m_label.setForeground(Color.black);

		this.m_key.setVisible(attr.isPrimaryKey());

		this.m_labelpanel.setOpaque(isSelected);
		if (isSelected)
			this.m_labelpanel.setBorder(BorderFactory
					.createLineBorder(new Color(160, 160, 180)));
		else
			this.m_labelpanel.setBorder(null);

		return this.m_panel;
	}

};
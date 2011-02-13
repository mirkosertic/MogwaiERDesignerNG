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
package de.erdesignerng.visual.editor.reverseengineer;

import de.erdesignerng.util.SelectableWrapper;
import de.mogwai.common.client.looks.UIInitializer;
import de.mogwai.common.client.looks.tools.StringRendererHelper;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;

public class CheckboxTreeCellRenderer extends JCheckBox implements
		TreeCellRenderer {

	private static CheckboxTreeCellRenderer ME = new CheckboxTreeCellRenderer();

	private UIInitializer initializer;

	public static CheckboxTreeCellRenderer getInstance() {
		return ME;
	}

	private CheckboxTreeCellRenderer() {

		initializer = UIInitializer.getInstance();
		initialize();
	}

	private void initialize() {
		initializer.initializeComponent(this);
	}

	protected String objectToString(Object aObject) {
		return StringRendererHelper.objectToString(aObject);
	}

	@Override
	public Component getTreeCellRendererComponent(JTree aTree, Object aValue,
			boolean isSelected, boolean isExpanded, boolean isLeaf, int isRow,
			boolean hasFocus) {

		DefaultMutableTreeNode theNode = (DefaultMutableTreeNode) aValue;
		if (theNode.getUserObject() instanceof SelectableWrapper) {
			SelectableWrapper theWrapper = (SelectableWrapper) theNode
					.getUserObject();

			setEnabled(true);
			setText(objectToString(theWrapper.getValue()));
			setSelected(theWrapper.isSelected());
		} else {
			setText(objectToString(theNode.getUserObject()));
			setSelected(true);
			setEnabled(false);
		}
		if (isSelected) {
			setBackground(initializer.getConfiguration()
					.getDefaultListSelectionBackground());
			setForeground(initializer.getConfiguration()
					.getDefaultListSelectionForeground());
		} else {
			setBackground(initializer.getConfiguration()
					.getDefaultListNonSelectionBackground());
			setForeground(initializer.getConfiguration()
					.getDefaultListNonSelectionForeground());
		}
		return this;
	}
}
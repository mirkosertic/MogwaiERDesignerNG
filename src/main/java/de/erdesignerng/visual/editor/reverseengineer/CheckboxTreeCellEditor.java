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

import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreePath;
import java.awt.Component;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;

public class CheckboxTreeCellEditor extends AbstractCellEditor implements
        TreeCellEditor {

    private final JTree tree;
    private final UIInitializer initializer = UIInitializer.getInstance();
    private DefaultMutableTreeNode editingNode;

    public CheckboxTreeCellEditor(final JTree tree) {
        this.tree = tree;
    }

    @Override
    public boolean isCellEditable(final EventObject event) {
        boolean returnValue = false;
        if (event instanceof MouseEvent) {
            final MouseEvent mouseEvent = (MouseEvent) event;
            final TreePath path = tree.getPathForLocation(mouseEvent.getX(), mouseEvent.getY());
            if (path != null) {
                final Object node = path.getLastPathComponent();
                if ((node != null) && (node instanceof DefaultMutableTreeNode)) {
                    final DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) node;
                    final Object userObject = treeNode.getUserObject();
                    returnValue = (userObject instanceof SelectableWrapper);
                }
            }
        }
        return returnValue;
    }

    @Override
    public Component getTreeCellEditorComponent(final JTree aTree, final Object aValue,
                                                final boolean isSelected, final boolean isExpanded, final boolean isLeaf, final int isRow) {

        final JCheckBox theEditor = new JCheckBox();
        UIInitializer.getInstance().initializeComponent(theEditor);

        editingNode = (DefaultMutableTreeNode) aValue;
        if (editingNode.getUserObject() instanceof SelectableWrapper) {
            final SelectableWrapper theWrapper = (SelectableWrapper) editingNode
                    .getUserObject();

            theEditor.setEnabled(true);
            theEditor.setText(objectToString(theWrapper.getValue()));
            theEditor.setSelected(theWrapper.isSelected());

            if (isSelected) {
                theEditor.setBackground(initializer.getConfiguration()
                        .getDefaultListSelectionBackground());
                theEditor.setForeground(initializer.getConfiguration()
                        .getDefaultListSelectionForeground());
            } else {
                theEditor.setBackground(initializer.getConfiguration()
                        .getDefaultListNonSelectionBackground());
                theEditor.setForeground(initializer.getConfiguration()
                        .getDefaultListNonSelectionForeground());
            }

            final ItemListener theItemListener = itemEvent -> {
                if (stopCellEditing()) {
                    final SelectableTableModel theModel = (SelectableTableModel) tree.getModel();
                    theModel.setSelected(editingNode, theEditor.isSelected());
                    fireEditingStopped();
                }
            };

            theEditor.addItemListener(theItemListener);
            return theEditor;
        }
        throw new IllegalArgumentException("Wrong object type");
    }

    protected String objectToString(final Object aObject) {
        return StringRendererHelper.objectToString(aObject);
    }

    @Override
    public Object getCellEditorValue() {
        return editingNode.getUserObject();
    }
}

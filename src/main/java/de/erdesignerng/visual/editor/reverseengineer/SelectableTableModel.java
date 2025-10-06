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

import de.erdesignerng.dialect.TableEntry;
import de.erdesignerng.util.SelectableWrapper;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SelectableTableModel extends DefaultTreeModel {

    protected interface TreeVisitor {
        boolean visit(Object aValue, DefaultMutableTreeNode aNode);
    }

    public SelectableTableModel(final DefaultMutableTreeNode aRoot) {
        super(aRoot);
    }

    protected void visitAll(final DefaultMutableTreeNode aNode, final TreeVisitor aVisitor) {
        if (aVisitor.visit(aNode.getUserObject(), aNode)) {
            nodeChanged(aNode);
        }
        for (int i = 0; i < aNode.getChildCount(); i++) {
            visitAll((DefaultMutableTreeNode) aNode.getChildAt(i), aVisitor);
        }
    }

    // Implemented FR 3317539 [ERDesignerNG] TableSelectEditor: (De)select whole subtrees
    public void setSelected(final DefaultMutableTreeNode aRootNode, final Boolean isSelected) {
        setSelected(aRootNode, isSelected, true);
    }

    private void setSelected(final DefaultMutableTreeNode aRootNode, final Boolean isSelected, final boolean aRecursiveSelection) {
        //change the direct selection-state of the appropriate leaf node
        visitAll(aRootNode, (aValue, aNode) -> {
            if (aRecursiveSelection || aValue.equals(aRootNode.getUserObject())) {
                if (aValue instanceof SelectableWrapper) {
                    final SelectableWrapper theWrapper = (SelectableWrapper) aValue;

                    if (isSelected == null) {
                        theWrapper.invertSelection();
                    } else {
                        theWrapper.setSelected(isSelected);
                    }

                    return true;
                }

                return false;
            }

            return !aRecursiveSelection;
        });

        //check if the selection state of main nodes is affected indirectly by
        //changing selection state of leaf nodes
        if (aRootNode.isLeaf() && aRootNode.getUserObject() instanceof SelectableWrapper && ((SelectableWrapper) aRootNode.getUserObject()).getValue() instanceof TableEntry) {
            boolean allSelected = true;
            final DefaultMutableTreeNode theMainNode = (DefaultMutableTreeNode) aRootNode.getParent();

            for (int i = 0; (allSelected && (i < theMainNode.getChildCount())); i++) {
                allSelected &= (((SelectableWrapper) (((DefaultMutableTreeNode) theMainNode.getChildAt(i)).getUserObject())).isSelected());
            }

            final SelectableWrapper theMainWrapper = (SelectableWrapper) theMainNode.getUserObject();
            if (theMainWrapper.isSelected() != allSelected) {
                setSelected(theMainNode, allSelected, false);
            }
        }
    }

    public void selectAll() {
        setSelected((DefaultMutableTreeNode) getRoot(), true);
    }

    public void deselectAll() {
        setSelected((DefaultMutableTreeNode) getRoot(), false);
    }

    public void invertSelection() {
        setSelected((DefaultMutableTreeNode) getRoot(), null);
    }

    public Collection getSelectedEntries() {
        final Set theResult = new HashSet();

        visitAll((DefaultMutableTreeNode) getRoot(), (aValue, aNode) -> {
            if (aValue instanceof SelectableWrapper) {
                final SelectableWrapper theWrapper = (SelectableWrapper) aValue;
                if (theWrapper.isSelected() && (theWrapper.getValue() instanceof TableEntry)) {
                    theResult.add(theWrapper.getValue());
                }
            }

            return false;
        });

        return theResult;
    }
}
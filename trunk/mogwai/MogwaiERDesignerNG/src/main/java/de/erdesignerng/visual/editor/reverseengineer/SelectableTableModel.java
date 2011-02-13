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

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SelectableTableModel extends DefaultTreeModel {

	private interface TreeVisitor {

		boolean visit(Object aValue);
	}

	public SelectableTableModel(DefaultMutableTreeNode aRoot) {
		super(aRoot);
	}

	protected void visitAll(DefaultMutableTreeNode aNode, TreeVisitor aVisitor) {
		if (aVisitor.visit(aNode.getUserObject())) {
			nodeChanged(aNode);
		}
		for (int i = 0; i < aNode.getChildCount(); i++) {
			visitAll((DefaultMutableTreeNode) aNode.getChildAt(i), aVisitor);
		}
	}

	public void selectAll() {
		visitAll((DefaultMutableTreeNode) getRoot(), new TreeVisitor() {

			@Override
			public boolean visit(Object aValue) {
				if (aValue instanceof SelectableWrapper) {
					SelectableWrapper theWrapper = (SelectableWrapper) aValue;
					if (!(theWrapper.getValue() instanceof String)) {
						theWrapper.setSelected(true);
					}
					return true;
				}
				return false;
			}
		});
	}

	public void deselectAll() {
		visitAll((DefaultMutableTreeNode) getRoot(), new TreeVisitor() {

			@Override
			public boolean visit(Object aValue) {
				if (aValue instanceof SelectableWrapper) {
					SelectableWrapper theWrapper = (SelectableWrapper) aValue;
					if (!(theWrapper.getValue() instanceof String)) {
						theWrapper.setSelected(false);
					}
					return true;
				}
				return false;
			}
		});
	}

	public void invertSelection() {
		visitAll((DefaultMutableTreeNode) getRoot(), new TreeVisitor() {

			@Override
			public boolean visit(Object aValue) {
				if (aValue instanceof SelectableWrapper) {
					SelectableWrapper theWrapper = (SelectableWrapper) aValue;
					if (!(theWrapper.getValue() instanceof String)) {
						theWrapper.invertSelection();
					}
					return true;
				}
				return false;
			}
		});
	}

	public Collection getSelectedEntries() {
		final Set theResult = new HashSet();
		visitAll((DefaultMutableTreeNode) getRoot(), new TreeVisitor() {

			@Override
			public boolean visit(Object aValue) {
				if (aValue instanceof SelectableWrapper) {
					SelectableWrapper theWrapper = (SelectableWrapper) aValue;
					if (theWrapper.isSelected()
							&& !(theWrapper.getValue() instanceof String)) {
						theResult.add(theWrapper.getValue());
					}
				}
				return false;
			}
		});
		return theResult;
	}
}

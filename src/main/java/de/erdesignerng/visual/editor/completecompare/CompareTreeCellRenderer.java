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
package de.erdesignerng.visual.editor.completecompare;

import de.erdesignerng.model.utils.MissingInfo;
import de.erdesignerng.model.utils.RedefinedInfo;
import de.erdesignerng.visual.IconFactory;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-02-11 18:01:03 $
 */
public class CompareTreeCellRenderer extends DefaultTreeCellRenderer {

	private final ImageIcon missingIcon;

	private final ImageIcon matchingIcon;

	public CompareTreeCellRenderer() {
		missingIcon = IconFactory.getMissingIcon();
		matchingIcon = IconFactory.getMatchingIcon();
	}

	@Override
	public Component getTreeCellRendererComponent(JTree aTree, Object aValue, boolean aSelected, boolean expanded,
			boolean aLeaf, int aRow, boolean aHasFocus) {

		DefaultMutableTreeNode theNode = (DefaultMutableTreeNode) aValue;

		setLeafIcon(aLeaf ? matchingIcon : null);
		setOpenIcon(aLeaf ? matchingIcon : null);
		setClosedIcon(aLeaf ? matchingIcon : null);

		if (theNode.getUserObject() instanceof MissingInfo) {

			setLeafIcon(missingIcon);
			setOpenIcon(missingIcon);
			setClosedIcon(missingIcon);

		}

		if (theNode.getUserObject() instanceof RedefinedInfo) {

			setLeafIcon(missingIcon);
			setOpenIcon(missingIcon);
			setClosedIcon(missingIcon);

		}

		return super.getTreeCellRendererComponent(aTree, aValue, aSelected, expanded, aLeaf, aRow, aHasFocus);
	}
}
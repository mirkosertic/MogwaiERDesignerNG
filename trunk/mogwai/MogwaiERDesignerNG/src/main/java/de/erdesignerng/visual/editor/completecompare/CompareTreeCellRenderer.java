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

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import de.erdesignerng.visual.IconFactory;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-02-11 18:01:03 $
 */
public class CompareTreeCellRenderer extends DefaultTreeCellRenderer {

    private ImageIcon missingIcon;

    private ImageIcon matchingIcon;

    public CompareTreeCellRenderer() {
        missingIcon = IconFactory.getMissingIcon();
        matchingIcon = IconFactory.getMatchingIcon();
    }

    @Override
    public Component getTreeCellRendererComponent(JTree aTree, Object aValue, boolean selected,
            boolean expanded, boolean leaf, int row, boolean hasFocus) {
    
        DefaultMutableTreeNode theNode = (DefaultMutableTreeNode) aValue;
    
        setLeafIcon(leaf ? matchingIcon : null);
        setOpenIcon(leaf ? matchingIcon : null);
        setClosedIcon(leaf ? matchingIcon : null);
    
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
    
        return super.getTreeCellRendererComponent(aTree, aValue, selected, expanded, leaf, row, hasFocus);
    }
}
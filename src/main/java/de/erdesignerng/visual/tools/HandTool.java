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
package de.erdesignerng.visual.tools;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.modificationtracker.VetoException;
import de.erdesignerng.visual.ERDesignerGraph;
import de.mogwai.common.client.looks.UIInitializer;
import de.mogwai.common.client.looks.components.DefaultPopupMenu;
import de.mogwai.common.client.looks.components.action.DefaultAction;
import de.mogwai.common.client.looks.components.menu.DefaultMenuItem;
import de.mogwai.common.i18n.ResourceHelper;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-03-08 12:58:07 $
 */
public class HandTool extends BaseTool {

    public HandTool(ERDesignerGraph aGraph) {
        super(aGraph);
    }

    @Override
    public boolean isForceMarqueeEvent(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            return true;
        }
        return super.isForceMarqueeEvent(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {

            Object[] theCells = graph.getSelectionCells();

            if ((theCells != null) && (theCells.length > 0)) {
                DefaultPopupMenu menu = createPopupMenu(e.getPoint(), theCells);
                menu.show(graph, e.getX(), e.getY());
            }
        } else {
            super.mousePressed(e);
        }
    }

    public DefaultPopupMenu createPopupMenu(Point aPoint, final Object[] aCells) {

        DefaultPopupMenu theMenu = new DefaultPopupMenu(ResourceHelper.getResourceHelper(ERDesignerBundle.BUNDLE_NAME));

        DefaultAction theAction = new DefaultAction(ERDesignerBundle.BUNDLE_NAME, ERDesignerBundle.DELETE);
        DefaultMenuItem theItem = new DefaultMenuItem(theAction);
        theAction.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (displayQuestionMessage(ERDesignerBundle.DOYOUREALLYWANTTODELETE)) {
                    try {
                        graph.commandDeleteCells(aCells);
                    } catch (VetoException ex) {

                        // TODO [mirkosertic] add better error handling here
                        displayErrorMessage("Cannot delete model entry : " + ex.getMessage());
                    }
                }
            }
        });

        theMenu.add(theItem);
        UIInitializer.getInstance().initialize(theMenu);

        return theMenu;
    }
}

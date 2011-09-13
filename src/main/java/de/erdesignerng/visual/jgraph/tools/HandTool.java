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
package de.erdesignerng.visual.jgraph.tools;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.model.ModelItem;
import de.erdesignerng.visual.common.ContextMenuFactory;
import de.erdesignerng.visual.common.GenericModelEditor;
import de.erdesignerng.visual.jgraph.ERDesignerGraph;
import de.mogwai.common.client.looks.UIInitializer;
import de.mogwai.common.client.looks.components.DefaultPopupMenu;
import de.mogwai.common.i18n.ResourceHelper;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;
import org.jgraph.graph.DefaultGraphCell;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-09 19:07:30 $
 */
public class HandTool extends BaseTool {

    private GenericModelEditor editor;

    public HandTool(GenericModelEditor aEditor, ERDesignerGraph aGraph) {
        super(aGraph);
        editor = aEditor;
    }

    @Override
    public boolean isForceMarqueeEvent(MouseEvent e) {

        return SwingUtilities.isRightMouseButton(e) && !e.isAltDown();

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {

            Object[] theSelectionCells = graph.getSelectionCells();
            List<DefaultGraphCell> theList = new ArrayList<DefaultGraphCell>();
            if (theSelectionCells != null) {
                for (Object theCell : theSelectionCells) {
                    if (theCell instanceof DefaultGraphCell) {
                        theList.add((DefaultGraphCell) theCell);
                    }
                }
            }
            if (theList.size() > 0) {
                DefaultPopupMenu menu = createPopupMenu(theList);
                menu.show(graph, e.getX(), e.getY());
                return;
            }
        }
        super.mousePressed(e);
    }

    public DefaultPopupMenu createPopupMenu(final List<DefaultGraphCell> aCells) {

        DefaultPopupMenu theMenu = new DefaultPopupMenu(ResourceHelper
                .getResourceHelper(ERDesignerBundle.BUNDLE_NAME));

        List<ModelItem> theItems = new ArrayList<ModelItem>();
        for (DefaultGraphCell theCell : aCells) {
            theItems.add((ModelItem) theCell.getUserObject());
        }

        ContextMenuFactory.addActionsToMenu(editor, theMenu, theItems);

        UIInitializer.getInstance().initialize(theMenu);

        return theMenu;
    }
}
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
package de.erdesignerng.visual;

import de.erdesignerng.model.Table;
import de.erdesignerng.model.View;
import de.erdesignerng.visual.jgraph.cells.views.TableCellView;
import de.erdesignerng.visual.jgraph.cells.views.ViewCellView;

import javax.swing.JComponent;
import java.awt.Dimension;
import java.awt.Point;
import java.util.List;
import java.util.Set;

public class LayoutHelper {

    private LayoutHelper() {
    }

    public static Dimension performTreeLayout(Point aStartLocation, List<Set<Table>> aHierarchy, List<View> aViews) {
        int yp = aStartLocation.y;
        int maxx = 0;
        int maxy = 0;
        for (Set<Table> theEntry : aHierarchy) {

            TableCellView.MyRenderer theRenderer = new TableCellView.MyRenderer();
            int xp = aStartLocation.x;
            int maxHeight = 0;
            for (Table theTable : theEntry) {

                theTable.getProperties().setPointProperty(Table.PROPERTY_LOCATION, xp, yp);

                JComponent theRenderComponent = theRenderer.getRendererComponent(theTable);
                Dimension theSize = theRenderComponent.getPreferredSize();

                maxHeight = Math.max(maxHeight, (int) theSize.getHeight());

                xp += theSize.getWidth() + 60;

                maxx = Math.max(xp, maxx);
                maxy = Math.max(yp + maxHeight, maxy);
            }

            if (theEntry.size() > 0) {
                yp += maxHeight + 60;
            }
        }

        ViewCellView.MyRenderer theRenderer = new ViewCellView.MyRenderer();

        int xp = aStartLocation.x;
        for (View theView : aViews) {

            theView.getProperties().setPointProperty(Table.PROPERTY_LOCATION, xp, yp);

            JComponent theRenderComponent = theRenderer.getRendererComponent(theView);
            Dimension theSize = theRenderComponent.getPreferredSize();

            xp += theSize.getWidth() + 60;

            maxx = Math.max(xp, maxx);
            maxy = Math.max(yp + theSize.height, maxy);
        }
        return new Dimension(maxx - aStartLocation.x, maxy - aStartLocation.y);
    }
}
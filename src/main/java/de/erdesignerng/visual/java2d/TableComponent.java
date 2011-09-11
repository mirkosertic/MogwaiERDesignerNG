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
package de.erdesignerng.visual.java2d;

import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.Table;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;

public class TableComponent extends JPanel {

    private Table table;

    public TableComponent(Table aTable) {
        table = aTable;
    }

    @Override
    public Dimension getPreferredSize() {
        return getSize();
    }

    private Dimension update(Dimension aDimension, int width, int height) {
        aDimension.width = Math.max(aDimension.width, width);
        aDimension.height += height;
        return aDimension;
    }

    @Override
    public Dimension getSize() {
        Dimension theSize = new Dimension(0, 0);
        FontMetrics theMetrics = getFontMetrics(getFont());
        for (Attribute theAttriute : table.getAttributes()) {
            if (!theAttriute.isForeignKey() && !theAttriute.isNullable()) {
                Rectangle2D theStringSize = theMetrics.getStringBounds(theAttriute.getName(), null);
                theSize = update(theSize, (int) theStringSize.getWidth(), theMetrics.getAscent());
            }
        }

        theSize.width += 35;
        theSize.height += 35;

        return theSize;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        Graphics2D theGraphics = (Graphics2D) g;
        Dimension theSize = getSize();
        FontMetrics theMetrics = getFontMetrics(getFont());

        theGraphics.setColor(Color.blue);
        theGraphics.drawRect(10, 10, theSize.width - 10, theSize.height - 10);
        theGraphics.drawRect(10, 10, theSize.width - 10, 10 + theMetrics.getAscent());

        GradientPaint thePaint = new GradientPaint(0, 0, Color.blue, theSize.width - 35, theSize.height,
                Color.black, false);
        theGraphics.setPaint(thePaint);
        theGraphics.fillRect(11, 11, theSize.width - 10 - 1, 10 + theMetrics.getAscent() - 1);

        thePaint = new GradientPaint(0, 0, new Color(90, 90, 90), theSize.width - 35, theSize.height,
                Color.black, false);
        theGraphics.setPaint(thePaint);
        theGraphics.fillRect(11, 19 + theMetrics.getAscent(), theSize.width - 10 - 1, theSize.height - 32);

        theGraphics.setColor(Color.white);

        theGraphics.drawString(table.getName(), 15, 10 + theMetrics.getAscent());

        int y = 18 + theMetrics.getAscent();

        for (Attribute theAttriute : table.getAttributes()) {
            if (!theAttriute.isForeignKey() && !theAttriute.isNullable()) {
                theGraphics.drawString(theAttriute.getName(), 15, y + theMetrics.getAscent());
                y += theMetrics.getAscent();
            }
        }
    }
}

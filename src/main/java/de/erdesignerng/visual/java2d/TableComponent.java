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

import de.erdesignerng.model.*;
import de.erdesignerng.visual.IconFactory;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.geom.Rectangle2D;
import java.text.AttributedString;
import javax.swing.ImageIcon;

public class TableComponent extends BaseRendererComponent {

    private final Table table;
    private boolean fullMode;
    private boolean showSelfReference;

    public TableComponent(Table aTable) {
        table = aTable;
        initFlags();
    }

    public TableComponent(Table aTable, boolean aFullmode) {
        table = aTable;
        fullMode = aFullmode;
        initFlags();
    }

    private void initFlags() {
        showSelfReference = false;
        for (Relation theRelation : table.getOwner().getRelations().getForeignKeysFor(table)) {
            if (theRelation.isSelfReference()) {
                showSelfReference = true;
            }
        }
    }

    @Override
    public Dimension getSize() {
        Dimension theSize = new Dimension(0, 0);
        FontMetrics theMetrics = getFontMetrics(getFont());

        Rectangle2D theStringSize = theMetrics.getStringBounds(table.getName(), null);
        theSize = update(theSize, (int) theStringSize.getWidth(), theMetrics.getAscent());

        for (Attribute<Table> theAttriute : table.getAttributes()) {
            boolean theInclude = true;
            if (!fullMode) {
                theInclude = theAttriute.isForeignKey() || !theAttriute.isNullable();
            }
            if (theInclude) {
                String theText = theAttriute.getName();
                if (fullMode) {
                    theText += ":";
                    theText += theAttriute.getLogicalDeclaration();
                }
                theStringSize = theMetrics.getStringBounds(theText, null);
                theSize = update(theSize, (int) theStringSize.getWidth(), theMetrics.getAscent());
            }
        }
        if (table.getIndexes().size() > 0 && fullMode) {
            for (Index theIndex : table.getIndexes()) {
                if (theIndex.getIndexType() != IndexType.PRIMARYKEY) {
                    String theName = theIndex.getName();
                    theStringSize = theMetrics.getStringBounds(theName, null);
                    theSize = update(theSize, (int) theStringSize.getWidth(), theMetrics.getAscent());
                    for (IndexExpression theExpression : theIndex.getExpressions()) {
                        theName = theExpression.toString();
                        theStringSize = theMetrics.getStringBounds(theName, null);
                        theSize = update(theSize, (int) theStringSize.getWidth() + 20, theMetrics.getAscent());
                    }
                }
            }
        }


        theSize.width += 20;
        if (fullMode) {
            theSize.width += 10;
        }

        theSize.height += 25;

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

        for (Attribute<Table> theAttriute : table.getAttributes()) {

            g.setColor(Color.white);

            boolean theInclude = true;
            if (!fullMode) {
                theInclude = theAttriute.isForeignKey() || !theAttriute.isNullable();
            }
            if (theInclude) {
                String theText = theAttriute.getName();
                if (fullMode) {
                    theText += ":";
                    theText += theAttriute.getLogicalDeclaration();

                    if (theAttriute.isNullable()) {
                        theGraphics.drawString("N", theSize.width - 10, y + theMetrics.getAscent());
                    }
                }
                if (theAttriute.isForeignKey()) {
                    g.setColor(Color.green);
                }

                if (theAttriute.isPrimaryKey()) {
                    // Primarx key has underline
                    AttributedString as = new AttributedString(theText);
                    as.addAttribute(TextAttribute.FONT, theGraphics.getFont());
                    as.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON, 0,
                            theText.length());

                    theGraphics.drawString(as.getIterator(), 15, y + theMetrics.getAscent());
                } else {
                    theGraphics.drawString(theText, 15, y + theMetrics.getAscent());
                }

                y += theMetrics.getAscent();
            }
            if (showSelfReference) {
                ImageIcon theIcon = IconFactory.getSelfReferenceIcon();
                int xp = theSize.width - theIcon.getIconWidth() - 4;
                int yp = 14;

                theIcon.paintIcon(this, theGraphics, xp, yp);
            }
        }
        if (table.getIndexes().size() > 0 && fullMode) {
            boolean lineDrawn = false;
            for (Index theIndex : table.getIndexes()) {
                if (theIndex.getIndexType() != IndexType.PRIMARYKEY) {
                    if (!lineDrawn) {
                        y += 3;
                        theGraphics.setColor(Color.blue);
                        theGraphics.drawLine(10, y, theSize.width, y);
                        lineDrawn = true;
                    }
                    String theName = theIndex.getName();
                    theGraphics.setColor(Color.white);
                    theGraphics.drawString(theName, 15, y + theMetrics.getAscent());
                    y += theMetrics.getAscent();
                    for (IndexExpression theExpression : theIndex.getExpressions()) {
                        theName = theExpression.toString();
                        theGraphics.drawString(theName, 20, y + theMetrics.getAscent());
                        y += theMetrics.getAscent();
                    }
                }
            }
        }
    }

}

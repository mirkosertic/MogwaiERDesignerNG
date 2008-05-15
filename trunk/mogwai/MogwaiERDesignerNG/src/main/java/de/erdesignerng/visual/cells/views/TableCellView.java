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
package de.erdesignerng.visual.cells.views;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.io.Serializable;
import java.util.List;
import java.util.Vector;

import javax.swing.ImageIcon;

import org.apache.commons.lang.StringUtils;
import org.jgraph.JGraph;
import org.jgraph.graph.CellView;
import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.GraphCellEditor;
import org.jgraph.graph.VertexRenderer;
import org.jgraph.graph.VertexView;

import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.Index;
import de.erdesignerng.model.ModelItem;
import de.erdesignerng.model.Table;
import de.erdesignerng.visual.IconFactory;
import de.erdesignerng.visual.cells.TableCell;
import de.erdesignerng.visual.editor.CellEditorFactory;

/**
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2008-05-15 18:16:31 $
 */
public class TableCellView extends VertexView {

    private static MyRenderer renderer = new MyRenderer();

    public TableCellView(TableCell aCell) {
        super(aCell);
    }

    @Override
    public CellViewRenderer getRenderer() {
        return renderer;
    }

    public static class MyRenderer extends VertexRenderer implements CellViewRenderer, Serializable {

        private Table table;

        private boolean roundedRect = false;

        private boolean selected;

        private boolean includeComments = false;

        private static ImageIcon key = IconFactory.getKeyIcon();

        public MyRenderer() {
            setBackground(Color.white);
        }

        private void fillRect(Graphics aGraphics, int aX1, int aY1, int aWidth, int aHeight) {
            if (roundedRect) {
                aGraphics.fillRoundRect(aX1, aY1, aWidth, aHeight, 10, 10);
                return;
            }

            aGraphics.fillRect(aX1, aY1, aWidth, aHeight);
        }

        private void drawRect(Graphics aGraphics, int aX1, int aY1, int aWidth, int aHeight) {
            if (roundedRect) {
                aGraphics.drawRoundRect(aX1, aY1, aWidth, aHeight, 10, 10);
                return;
            }

            aGraphics.drawRect(aX1, aY1, aWidth, aHeight);
        }

        protected String getConvertedName(ModelItem aItem) {
            String theText = aItem.getName();
            if (includeComments) {
                if (!StringUtils.isEmpty(aItem.getComment())) {
                    theText += " (" + aItem.getComment() + ")";
                }
            }
            return theText;
        }

        @Override
        public void paint(Graphics aGraphics) {

            Index thePrimaryKey = table.getPrimarykey();

            Dimension theSize = getSize();
            int theWidth = theSize.width;
            int theHeight = theSize.height;

            aGraphics.setFont(getFont());
            aGraphics.setColor(getBackground());
            // aGraphics.fillRect(0, 0, theWidth, theHeight);

            FontMetrics theMetrics = aGraphics.getFontMetrics();

            aGraphics.setColor(Color.black);
            String theString = getConvertedName(table);

            aGraphics.drawString(theString, 0, theMetrics.getAscent());

            int theYOffset = theMetrics.getHeight();

            aGraphics.setColor(selected ? Color.blue : Color.black);

            fillRect(aGraphics, 5, theYOffset + 5, theWidth - 5, theHeight - theYOffset - 5);

            aGraphics.setColor(new Color(255, 255, 212));

            fillRect(aGraphics, 0, theYOffset, theWidth - 5, theHeight - theYOffset - 6);

            aGraphics.setColor(selected ? Color.blue : Color.black);

            drawRect(aGraphics, 0, theYOffset, theWidth - 5, theHeight - theYOffset - 6);

            int theTextXOffset = 15;

            List<Attribute> theAllAttributes = new Vector<Attribute>();
            theAllAttributes.addAll(table.getAttributes());

            boolean hasPrimaryKey = false;

            // Draw the attributes
            for (Attribute theAttribute : table.getAttributes()) {

                boolean isPrimaryKey = false;
                if (thePrimaryKey != null) {
                    isPrimaryKey = thePrimaryKey.getAttributes().contains(theAttribute);
                }

                if (isPrimaryKey) {

                    hasPrimaryKey = true;

                    theAllAttributes.remove(theAttribute);

                    aGraphics.setColor(Color.red);

                    theString = getConvertedName(theAttribute);

                    theString += " : ";

                    theString += theAttribute.getPhysicalDeclaration();

                    if (theAttribute.getExtra() != null) {
                        theString += " ";
                        theString += theAttribute.getExtra();
                    }
                    if (theAttribute.isForeignKey()) {
                        theString += " (FK)";
                    }

                    aGraphics.drawString(theString, theTextXOffset, theYOffset + theMetrics.getAscent());
                    key.paintIcon(this, aGraphics, 5, theYOffset + 4);
                    theYOffset += theMetrics.getHeight();
                }
            }

            // Only do the following if there are any not primary key attributes
            if (theAllAttributes.size() > 0) {

                // This line is only neccesary in case that there are PK
                // attributes
                if (hasPrimaryKey) {

                    // Draw the border line
                    aGraphics.setColor(Color.black);
                    aGraphics.drawLine(0, theYOffset, theWidth - 5, theYOffset);
                }

                // Draw the attributes
                for (Attribute theAttribute : theAllAttributes) {

                    boolean isFK = theAttribute.isForeignKey();

                    theString = getConvertedName(theAttribute);

                    theString += " : ";

                    theString += theAttribute.getPhysicalDeclaration();

                    if (theAttribute.getExtra() != null) {
                        theString += " ";
                        theString += theAttribute.getExtra();
                    }
                    if (isFK) {
                        theString += " (FK)";
                    }

                    aGraphics.setColor(isFK ? Color.red : Color.black);

                    aGraphics.drawString(theString, theTextXOffset, theYOffset + theMetrics.getAscent());
                    theYOffset += theMetrics.getHeight();
                }
            }

        }

        @Override
        public Dimension getPreferredSize() {

            Index thePrimaryKey = table.getPrimarykey();

            int theMaxX = 150;
            int theMaxY = 8;

            FontMetrics theMetrics = this.getFontMetrics(this.getFont());

            int theYOffset = theMetrics.getHeight();
            int theXTextOffset = 30;

            String theString = getConvertedName(table);

            int theLength = theMetrics.stringWidth(theString);
            if (theLength > theMaxX) {
                theMaxX = theLength + 5;
            }

            List<Attribute> theAllAttributes = new Vector<Attribute>();
            theAllAttributes.addAll(table.getAttributes());

            for (Attribute theAttribute : table.getAttributes()) {

                boolean isPrimaryKey = false;
                if (thePrimaryKey != null) {
                    isPrimaryKey = thePrimaryKey.getAttributes().contains(theAttribute);
                }

                if (isPrimaryKey) {

                    theAllAttributes.remove(theAttribute);

                    String theText = getConvertedName(theAttribute);
                    theText += " : ";

                    theText += theAttribute.getPhysicalDeclaration();

                    if (theAttribute.getExtra() != null) {
                        theText += " ";
                        theText += theAttribute.getExtra();
                    }

                    if (theAttribute.isForeignKey()) {
                        theText += " (FK)";
                    }

                    theLength = theMetrics.stringWidth(theText);
                    if (theLength + theXTextOffset > theMaxX) {
                        theMaxX = theLength + theXTextOffset;
                    }

                    theYOffset += theMetrics.getHeight();
                }
            }

            for (Attribute theAttribute : theAllAttributes) {

                String theText = getConvertedName(theAttribute);
                theText += " : ";

                theText += theAttribute.getPhysicalDeclaration();

                if (theAttribute.getExtra() != null) {
                    theText += " ";
                    theText += theAttribute.getExtra();
                }

                if (theAttribute.isForeignKey()) {
                    theText += " (FK)";
                }

                theLength = theMetrics.stringWidth(theText);
                if (theLength + theXTextOffset > theMaxX) {
                    theMaxX = theLength + theXTextOffset;
                }

                theYOffset += theMetrics.getHeight();
            }

            theYOffset += 8;
            theMaxX += 8;

            Insets theInsets = getInsets();
            theMaxX += theInsets.left + theInsets.right;
            theMaxY += theInsets.top + theInsets.bottom;

            if (theYOffset > theMaxY) {
                theMaxY = theYOffset;
            }

            return new Dimension(theMaxX, theMaxY);
        }

        @Override
        public Component getRendererComponent(JGraph aGraph, CellView aView, boolean aSelected, boolean aHasFocus,
                boolean aPreview) {

            TableCellView theView = (TableCellView) aView;
            table = (Table) ((TableCell) theView.getCell()).getUserObject();
            selected = aSelected;

            return this;
        }
    }

    @Override
    public GraphCellEditor getEditor() {
        return new CellEditorFactory();
    }
}

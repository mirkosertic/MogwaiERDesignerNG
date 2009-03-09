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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.ImageIcon;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ReverseComparator;
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
import de.erdesignerng.visual.DisplayLevel;
import de.erdesignerng.visual.DisplayOrder;
import de.erdesignerng.visual.ERDesignerGraph;
import de.erdesignerng.visual.IconFactory;
import de.erdesignerng.visual.cells.TableCell;
import de.erdesignerng.visual.editor.CellEditorFactory;

/**
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-09 19:07:30 $
 */
public class TableCellView extends VertexView {

    private static final Comparator NAME_COMPARATOR = new BeanComparator("name", String.CASE_INSENSITIVE_ORDER);

    private static final Comparator REVERSE_NAME_COMPARATOR = new ReverseComparator(NAME_COMPARATOR);

    private static MyRenderer renderer = new MyRenderer();

    public TableCellView(TableCell aCell) {
        super(aCell);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CellViewRenderer getRenderer() {
        return renderer;
    }

    public static class MyRenderer extends VertexRenderer implements CellViewRenderer, Serializable {

        private Table table;

        private boolean roundedRect = false;

        private boolean selected;

        private boolean includeComments = false;

        private boolean physicalLayout = true;

        private DisplayLevel displayLevel;

        private DisplayOrder displayOrder;

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

        /**
         * {@inheritDoc}
         */
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

            List<Attribute> theTempList = new ArrayList<Attribute>();
            theTempList.addAll(table.getAttributes());

            switch (displayOrder) {
            case NATURAL:
                break;
            case ASCENDING:
                Collections.sort(theTempList, NAME_COMPARATOR);
                break;
            case DESCENDING:
                Collections.sort(theTempList, REVERSE_NAME_COMPARATOR);
                break;
            default:
                throw new IllegalStateException("Unknown display order");
            }

            List<Attribute> theAllAttributes = new ArrayList<Attribute>();
            theAllAttributes.addAll(theTempList);

            boolean hasPrimaryKey = false;

            // Draw the attributes
            for (Attribute theAttribute : theTempList) {

                boolean isPrimaryKey = false;
                if (thePrimaryKey != null) {
                    isPrimaryKey = thePrimaryKey.containsAttribute(theAttribute);
                }

                if (isPrimaryKey) {

                    hasPrimaryKey = true;

                    theAllAttributes.remove(theAttribute);

                    aGraphics.setColor(Color.red);

                    theString = getConvertedName(theAttribute);

                    theString += " : ";

                    if (physicalLayout) {
                        theString += theAttribute.getLayoutProvider().getPhysicalDeclaration();

                        if (theAttribute.getExtra() != null) {
                            theString += " ";
                            theString += theAttribute.getLayoutProvider().getExtra();
                        }

                    } else {
                        theString += theAttribute.getLayoutProvider().getLogicalDeclaration();
                    }

                    if (theAttribute.isForeignKey()) {
                        theString += " (FK)";
                    }

                    aGraphics.drawString(theString, theTextXOffset, theYOffset + theMetrics.getAscent());
                    key.paintIcon(this, aGraphics, 5, theYOffset + 4);
                    theYOffset += theMetrics.getHeight();
                }
            }

            if (DisplayLevel.ALL.equals(displayLevel) || (DisplayLevel.PRIMARYKEYSANDFOREIGNKEYS.equals(displayLevel))) {

                // Only do the following if there are any not primary key
                // attributes
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

                        if (DisplayLevel.ALL.equals(displayLevel) || (theAttribute.isForeignKey())) {
                            boolean isFK = theAttribute.isForeignKey();

                            theString = getConvertedName(theAttribute);

                            theString += " : ";

                            if (physicalLayout) {
                                theString += theAttribute.getLayoutProvider().getPhysicalDeclaration();

                                if (theAttribute.getExtra() != null) {
                                    theString += " ";
                                    theString += theAttribute.getExtra();
                                }
                            } else {
                                theString += theAttribute.getLayoutProvider().getLogicalDeclaration();
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
            }

        }

        /**
         * {@inheritDoc}
         */
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

            List<Attribute> theAllAttributes = new ArrayList<Attribute>();
            theAllAttributes.addAll(table.getAttributes());

            for (Attribute theAttribute : table.getAttributes()) {

                boolean isPrimaryKey = false;
                if (thePrimaryKey != null) {
                    isPrimaryKey = thePrimaryKey.containsAttribute(theAttribute);
                }

                if (isPrimaryKey) {

                    theAllAttributes.remove(theAttribute);

                    String theText = getConvertedName(theAttribute);
                    theText += " : ";

                    if (physicalLayout) {
                        theText += theAttribute.getLayoutProvider().getPhysicalDeclaration();

                        if (theAttribute.getExtra() != null) {
                            theText += " ";
                            theText += theAttribute.getLayoutProvider().getExtra();
                        }
                    } else {
                        theText += theAttribute.getLayoutProvider().getLogicalDeclaration();
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

            if (DisplayLevel.ALL.equals(displayLevel) || (DisplayLevel.PRIMARYKEYSANDFOREIGNKEYS.equals(displayLevel))) {
                for (Attribute theAttribute : theAllAttributes) {

                    if (DisplayLevel.ALL.equals(displayLevel) || (theAttribute.isForeignKey())) {

                        String theText = getConvertedName(theAttribute);
                        theText += " : ";

                        if (physicalLayout) {
                            theText += theAttribute.getLayoutProvider().getPhysicalDeclaration();

                            if (theAttribute.getExtra() != null) {
                                theText += " ";
                                theText += theAttribute.getLayoutProvider().getExtra();
                            }
                        } else {
                            theText += theAttribute.getLayoutProvider().getLogicalDeclaration();
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

        /**
         * {@inheritDoc}
         */
        @Override
        public Component getRendererComponent(JGraph aGraph, CellView aView, boolean aSelected, boolean aHasFocus,
                boolean aPreview) {

            TableCellView theView = (TableCellView) aView;
            table = (Table) ((TableCell) theView.getCell()).getUserObject();
            selected = aSelected;

            ERDesignerGraph theGraph = (ERDesignerGraph) aGraph;
            includeComments = theGraph.isDisplayComments();
            physicalLayout = theGraph.isPhysicalLayout();
            displayLevel = theGraph.getDisplayLevel();
            displayOrder = theGraph.getDisplayOrder();

            return this;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraphCellEditor getEditor() {
        return new CellEditorFactory();
    }
}

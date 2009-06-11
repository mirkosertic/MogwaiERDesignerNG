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

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ReverseComparator;
import org.apache.commons.lang.StringUtils;
import org.jgraph.JGraph;
import org.jgraph.graph.CellView;
import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.GraphCellEditor;
import org.jgraph.graph.VertexRenderer;
import org.jgraph.graph.VertexView;

import de.erdesignerng.model.ModelItem;
import de.erdesignerng.model.Table;
import de.erdesignerng.model.View;
import de.erdesignerng.model.ViewAttribute;
import de.erdesignerng.visual.DisplayOrder;
import de.erdesignerng.visual.ERDesignerGraph;
import de.erdesignerng.visual.cells.ViewCell;
import de.erdesignerng.visual.editor.CellEditorFactory;

/**
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-13 15:40:33 $
 */
public class ViewCellView extends VertexView {

    private static final Comparator NAME_COMPARATOR = new BeanComparator("name", String.CASE_INSENSITIVE_ORDER);

    private static final Comparator REVERSE_NAME_COMPARATOR = new ReverseComparator(NAME_COMPARATOR);

    private static MyRenderer renderer = new MyRenderer();

    public ViewCellView(ViewCell aCell) {
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

        private View view;

        private boolean selected;

        private DisplayOrder displayOrder;

        public MyRenderer() {
            setBackground(Color.white);
        }

        private void fillRect(Graphics aGraphics, int aX1, int aY1, int aWidth, int aHeight) {

            aGraphics.fillRect(aX1, aY1, aWidth, aHeight);
        }

        private void drawRect(Graphics aGraphics, int aX1, int aY1, int aWidth, int aHeight) {

            aGraphics.drawRect(aX1, aY1, aWidth, aHeight);
        }

        protected String getConvertedName(ModelItem aItem) {
            String theText = aItem.getName();
            if (aItem instanceof View) {
                String theSchema = ((View) aItem).getSchema();
                if (!StringUtils.isEmpty(theSchema)) {
                    theText = theSchema + "." + aItem.getName();
                }
            }
            return theText;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void paint(Graphics aGraphics) {

            Dimension theSize = getSize();
            int theWidth = theSize.width;
            int theHeight = theSize.height;

            aGraphics.setFont(getFont());
            aGraphics.setColor(getBackground());

            FontMetrics theMetrics = aGraphics.getFontMetrics();

            aGraphics.setColor(Color.black);
            String theString = getConvertedName(view);

            aGraphics.drawString(theString, 0, theMetrics.getAscent());

            int theYOffset = theMetrics.getHeight();

            aGraphics.setColor(selected ? Color.blue : Color.black);

            fillRect(aGraphics, 5, theYOffset + 5, theWidth - 5, theHeight - theYOffset - 5);

            aGraphics.setColor(new Color(255, 255, 212));

            fillRect(aGraphics, 0, theYOffset, theWidth - 5, theHeight - theYOffset - 6);

            aGraphics.setColor(selected ? Color.blue : Color.black);

            drawRect(aGraphics, 0, theYOffset, theWidth - 5, theHeight - theYOffset - 6);

            int theTextXOffset = 15;

            List<ViewAttribute> theTempList = new ArrayList<ViewAttribute>();
            theTempList.addAll(view.getAttributes());

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

            List<ViewAttribute> theAllAttributes = new ArrayList<ViewAttribute>();
            theAllAttributes.addAll(theTempList);

            // Only do the following if there are any not primary key
            // attributes
            if (theAllAttributes.size() > 0) {

                // Draw the attributes
                for (ViewAttribute theAttribute : theAllAttributes) {

                    theString = getConvertedName(theAttribute);

                    /**
                     * theString += " : ";
                     * 
                     * theString +=
                     * theAttribute.getLayoutProvider().getLogicalDeclaration();
                     */

                    aGraphics.setColor(Color.black);

                    aGraphics.drawString(theString, theTextXOffset, theYOffset + theMetrics.getAscent());
                    theYOffset += theMetrics.getHeight();
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Dimension getPreferredSize() {

            int theMaxX = 150;
            int theMaxY = 8;

            FontMetrics theMetrics = this.getFontMetrics(this.getFont());

            int theYOffset = theMetrics.getHeight();
            int theXTextOffset = 30;

            String theString = getConvertedName(view);

            int theLength = theMetrics.stringWidth(theString);
            if (theLength > theMaxX) {
                theMaxX = theLength + 5;
            }

            List<ViewAttribute> theAllAttributes = new ArrayList<ViewAttribute>();
            theAllAttributes.addAll(view.getAttributes());

            for (ViewAttribute theAttribute : theAllAttributes) {

                String theText = getConvertedName(theAttribute);

                /*
                 * theText += " : ";
                 * 
                 * theText +=
                 * theAttribute.getLayoutProvider().getLogicalDeclaration();
                 */

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

            // TODO [mirkosertic] This should be the default size if there are
            // no attributes
            if (theMaxY < 50) {
                theMaxY = 50;
            }

            return new Dimension(theMaxX, theMaxY);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Component getRendererComponent(JGraph aGraph, CellView aView, boolean aSelected, boolean aHasFocus,
                boolean aPreview) {

            ViewCellView theView = (ViewCellView) aView;
            view = (View) ((ViewCell) theView.getCell()).getUserObject();
            selected = aSelected;

            ERDesignerGraph theGraph = (ERDesignerGraph) aGraph;
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
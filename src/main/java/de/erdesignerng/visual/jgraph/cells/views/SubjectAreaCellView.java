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
package de.erdesignerng.visual.jgraph.cells.views;

import de.erdesignerng.model.ModelItem;
import de.erdesignerng.model.SubjectArea;
import de.erdesignerng.visual.IconFactory;
import de.erdesignerng.visual.jgraph.CellEditorFactory;
import de.erdesignerng.visual.jgraph.cells.SubjectAreaCell;
import org.jgraph.JGraph;
import org.jgraph.graph.CellView;
import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.GraphCellEditor;
import org.jgraph.graph.VertexRenderer;
import org.jgraph.graph.VertexView;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-06-13 16:48:58 $
 */
public class SubjectAreaCellView extends VertexView {

    private static final MyRenderer RENDERER = new MyRenderer();

    public SubjectAreaCellView(SubjectAreaCell aCell) {
        super(aCell);
    }

    @Override
    public CellViewRenderer getRenderer() {
        return RENDERER;
    }

    public static class MyRenderer extends VertexRenderer implements CellViewRenderer, Serializable {

        final static float dash1[] = {10.0f};
        private final static BasicStroke DASHED_STROKE = new BasicStroke(1.0f,
                BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER,
                10.0f, dash1, 0.0f);

        private final static BasicStroke NORMAL_STROKE = new BasicStroke(1);


        private SubjectArea subjectArea;

        private boolean selected;
        private boolean expanded;

        public MyRenderer() {
            setBackground(Color.white);
        }

        @Override
        public void paint(Graphics aGraphics) {

            Graphics2D theGraphics = (Graphics2D) aGraphics;

            Dimension theSize = getSize();
            int theWidth = theSize.width;
            int theHeight = theSize.height;

            GradientPaint thePaint = new GradientPaint(0, 0, subjectArea.getColor(), theWidth, theHeight,
                    getBackground(), false);
            theGraphics.setPaint(thePaint);
            aGraphics.fillRect(0, 0, theWidth - 1, theHeight - 1);

            aGraphics.setColor(selected ? Color.blue : Color.black);
            if (expanded) {
                theGraphics.setStroke(DASHED_STROKE);
            } else {
                theGraphics.setStroke(NORMAL_STROKE);
            }
            aGraphics.drawRect(0, 0, theWidth - 1, theHeight - 1);

            theGraphics.setStroke(NORMAL_STROKE);

            aGraphics.setColor(Color.black);

            FontMetrics theMetrics = aGraphics.getFontMetrics();
            int theYOffset = theMetrics.getHeight();

            aGraphics.setFont(getFont().deriveFont(Font.BOLD));

            if (expanded) {
                ImageIcon theIcon = IconFactory.getCollapseIcon();
                theIcon.paintIcon(this, theGraphics, -5, -5);
                aGraphics.drawString(subjectArea.getName(), 24, theYOffset);
            } else {
                Rectangle2D theTextSize = theMetrics.getStringBounds(subjectArea.getName(), getGraphics());

                int xp = (int) (theWidth / 2 - theTextSize.getWidth() / 2);
                int yp = theHeight / 2;

                ImageIcon theIcon = IconFactory.getExpandIcon();
                theIcon.paintIcon(this, theGraphics, -5, -5);
                aGraphics.drawString(subjectArea.getName(), xp, yp);
            }
        }

        @Override
        public Dimension getPreferredSize() {
            FontMetrics theMetrics = getFontMetrics(getFont());
            Rectangle2D theSize = theMetrics.getStringBounds(subjectArea.getName(), getGraphics());

            if (!expanded) {
                return new Dimension((int) theSize.getWidth() + 40, theMetrics.getHeight() * 2);
            } else {
                // TODO MSE: Compute the size of subject areas the right ays
                Dimension theGroupSize = super.getPreferredSize();
                if (theGroupSize.width < theSize.getWidth() + 40) {
                    theGroupSize = new Dimension((int) theSize.getWidth() + 40, (int) theGroupSize.getHeight());
                }
                return theGroupSize;
            }
        }

        @Override
        public Component getRendererComponent(JGraph aGraph, CellView aView, boolean aSelected, boolean aHasFocus,
                                              boolean aPreview) {

            SubjectAreaCellView theView = (SubjectAreaCellView) aView;
            SubjectAreaCell theCell = (SubjectAreaCell) aView.getCell();
            subjectArea = (SubjectArea) ((SubjectAreaCell) theView.getCell()).getUserObject();
            selected = aSelected;
            expanded = theCell.isExpanded();

            return this;
        }
    }

    @Override
    public GraphCellEditor getEditor() {
        return new CellEditorFactory();
    }

    @Override
    public void translate(double dx, double dy) {
        super.translate(dx, dy);

        SubjectAreaCell theCell = (SubjectAreaCell) getCell();
        //if (!theCell.isExpanded()) {
        // Invisible childs are not automatically translated
        // if a collapsed subject area is moved, so we have to do that by hand.
        SubjectArea theArea = (SubjectArea) theCell.getUserObject();
        for (ModelItem theItem : theArea.getTables()) {
            Point2D theLocation = theItem.getProperties().getPoint2DProperty(ModelItem.PROPERTY_LOCATION);
            theItem.getProperties().setPointProperty(ModelItem.PROPERTY_LOCATION, (int) (theLocation.getX() + dx), (int) (theLocation.getY() + dy));
        }
        for (ModelItem theItem : theArea.getViews()) {
            Point2D theLocation = theItem.getProperties().getPoint2DProperty(ModelItem.PROPERTY_LOCATION);
            theItem.getProperties().setPointProperty(ModelItem.PROPERTY_LOCATION, (int) (theLocation.getX() + dx), (int) (theLocation.getY() + dy));
        }
        //}
    }
}

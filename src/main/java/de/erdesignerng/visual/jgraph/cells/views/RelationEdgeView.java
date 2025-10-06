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

import de.erdesignerng.model.Relation;
import de.erdesignerng.visual.jgraph.CellEditorFactory;
import de.erdesignerng.visual.jgraph.cells.RelationEdge;
import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.EdgeRenderer;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphCellEditor;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-01-17 19:34:29 $
 */
public class RelationEdgeView extends EdgeView {

    private static final MyRenderer RENDERER = new MyRenderer();

    public RelationEdgeView(final RelationEdge aRelation) {
        super(aRelation);
    }

    @Override
    public CellViewRenderer getRenderer() {
        return RENDERER;
    }

    public static class MyRenderer extends EdgeRenderer {

        @Override
        protected Shape createLineEnd(final int size, final int style, final Point2D src, final Point2D dst) {

            if (style == RelationEdge.LINE_BEGIN) {
                return createLineBeginShape(dst, src);
            }
            if (style == RelationEdge.LINE_END) {
                return createLineEndShape(dst, src);
            }

            return super.createLineEnd(size, style, src, dst);
        }

        private Shape createLineEndShape(final Point2D aSrc, final Point2D aDst) {
            final RelationEdge theEdge = (RelationEdge) view.getCell();
            final Relation theRelation = (Relation) theEdge.getUserObject();

            final double angle = Math.atan2(aDst.getY() - aSrc.getY(), aDst.getX() - aSrc.getX());

            final Path2DRotatedHelper theHelper = new Path2DRotatedHelper(aSrc.getX(), aSrc.getY(), angle);
            theHelper.moveTo(10, -10);
            theHelper.lineTo(10, 10);

            final boolean isIdentifying = theRelation.isIdentifying();
            if (isIdentifying) {
                theHelper.moveTo(15, -10);
                theHelper.lineTo(15, 10);
            } else {
                theHelper.circle(20, 0, 6);
            }

            return theHelper.getPath();
        }

        private Shape createLineBeginShape(final Point2D aSrc, final Point2D aDst) {
            final RelationEdge theEdge = (RelationEdge) view.getCell();
            final Relation theRelation = (Relation) theEdge.getUserObject();

            final double angle = Math.atan2(aDst.getY() - aSrc.getY(), aDst.getX() - aSrc.getX());

            final Path2DRotatedHelper theHelper = new Path2DRotatedHelper(aSrc.getX(), aSrc.getY(), angle);

            final boolean isOneToOne = theRelation.isOneToOne();

            if (isOneToOne) {
                // Case 1: FK fields are also the primary key
                theHelper.moveTo(10, -10);
                theHelper.lineTo(10, 10);
                theHelper.circle(20, 0, 6);
            } else {
                // Case 2: FK Fields are partially or no primary key fields at all
                theHelper.circle(25, 0, 6);
                theHelper.moveTo(15, 0);
                theHelper.lineTo(0, 10);
                theHelper.moveTo(15, 0);
                theHelper.lineTo(0, -10);
            }


            return theHelper.getPath();
        }

    }

    private static class Path2DRotatedHelper {

        private final double ox;
        private final double oy;
        private final Path2D.Double path;
        private final double cosAngle;
        private final double sinAngle;

        public Path2DRotatedHelper(final double aOx, final double aOy, final double angle) {
            path = new Path2D.Double();
            ox = aOx;
            oy = aOy;
            cosAngle = Math.cos(angle);
            sinAngle = Math.sin(angle);
        }

        public Path2D getPath() {
            return path;
        }

        public void moveTo(final double mx, final double my) {

            final double x1 = mx * cosAngle - my * sinAngle;
            final double y1 = my * cosAngle + mx * sinAngle;

            path.moveTo(ox + x1, oy + y1);
        }

        public void lineTo(final double mx, final double my) {

            final double x1 = mx * cosAngle - my * sinAngle;
            final double y1 = my * cosAngle + mx * sinAngle;

            path.lineTo(ox + x1, oy + y1);
        }

        public void circle(final double mx, final double my, final double radius) {

            final double x1 = mx * cosAngle - my * sinAngle;
            final double y1 = my * cosAngle + mx * sinAngle;

            for (int i = 0; i <= 360; i += 10) {

                final double theAngle = Math.toRadians(i);

                final double x = ox + x1 + Math.cos(theAngle) * radius;
                final double y = oy + y1 + Math.sin(theAngle) * radius;
                if (i == 0) {
                    path.moveTo(x, y);
                } else {
                    path.lineTo(x, y);
                }
            }
        }
    }

    @Override
    public GraphCellEditor getEditor() {
        return new CellEditorFactory();
    }
}
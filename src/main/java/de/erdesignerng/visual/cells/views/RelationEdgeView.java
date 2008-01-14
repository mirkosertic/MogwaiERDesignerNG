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

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.EdgeRenderer;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphCellEditor;
import org.jgraph.graph.GraphConstants;
import org.jgraph.util.Bezier;
import org.jgraph.util.Spline2D;

import de.erdesignerng.visual.cells.RelationEdge;
import de.erdesignerng.visual.editor.CellEditorFactory;

/**
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2008-01-14 20:01:05 $
 */
public class RelationEdgeView extends EdgeView {

    private static MyRenderer renderer = new MyRenderer();

    public RelationEdgeView(RelationEdge aRelation) {
        super(aRelation);
    }

    @Override
    public GraphCellEditor getEditor() {
        return new CellEditorFactory();
    }

    @Override
    public CellViewRenderer getRenderer() {
        return renderer;
    }

    public static class MyRenderer extends EdgeRenderer {

        public static final int STYLE_GRAPHVIZ_BEZIER = 14;

        @Override
        protected Shape createShape() {
            int n = view.getPointCount();
            if (n > 1) {
                // Following block may modify static vars as side effect
                // (Flyweight
                // Design)
                EdgeView tmp = view;
                Point2D[] p = null;
                p = new Point2D[n];
                for (int i = 0; i < n; i++) {
                    Point2D pt = tmp.getPoint(i);
                    if (pt == null) {
                        return null; // exit
                    }
                    p[i] = new Point2D.Double(pt.getX(), pt.getY());
                }

                // End of Side-Effect Block
                // Undo Possible MT-Side Effects
                if (view != tmp) {
                    view = tmp;
                    installAttributes(view);
                }
                // End of Undo
                if (view.sharedPath == null) {
                    view.sharedPath = new GeneralPath(GeneralPath.WIND_NON_ZERO, n);
                } else {
                    view.sharedPath.reset();
                }
                view.beginShape = null;
                view.lineShape = null;
                view.endShape = null;
                
                Point2D p0 = p[0];
                Point2D pe = p[n - 1];
                Point2D p1 = p[1];
                Point2D p2 = p[n - 2];

                if (lineStyle == GraphConstants.STYLE_BEZIER && n > 2) {
                    bezier = new Bezier(p);
                    p2 = bezier.getPoint(bezier.getPointCount() - 1);
                } else if (lineStyle == GraphConstants.STYLE_SPLINE && n > 2) {
                    spline = new Spline2D(p);
                    double[] point = spline.getPoint(0.9875);
                    // Extrapolate p2 away from the end point, pe, to avoid
                    // integer
                    // rounding errors becoming too large when creating the line
                    // end
                    double scaledX = pe.getX() - ((pe.getX() - point[0]) * 128);
                    double scaledY = pe.getY() - ((pe.getY() - point[1]) * 128);
                    p2.setLocation(scaledX, scaledY);
                }

                if (beginDeco != GraphConstants.ARROW_NONE) {
                    view.beginShape = createLineEnd(beginSize, beginDeco, p1, p0);
                }
                if (endDeco != GraphConstants.ARROW_NONE) {
                    view.endShape = createLineEnd(endSize, endDeco, p2, pe);
                }
                view.sharedPath.moveTo((float) p0.getX(), (float) p0.getY());
                /* THIS CODE WAS ADDED BY MARTIN KRUEGER 10/20/2003 */
                if (lineStyle == GraphConstants.STYLE_BEZIER && n > 2) {
                    Point2D[] b = bezier.getPoints();
                    view.sharedPath.quadTo((float) b[0].getX(), (float) b[0].getY(), (float) p1.getX(), (float) p1
                            .getY());
                    for (int i = 2; i < n - 1; i++) {
                        Point2D b0 = b[2 * i - 3];
                        Point2D b1 = b[2 * i - 2];
                        view.sharedPath.curveTo((float) b0.getX(), (float) b0.getY(), (float) b1.getX(), (float) b1
                                .getY(), (float) p[i].getX(), (float) p[i].getY());
                    }
                    view.sharedPath.quadTo((float) b[b.length - 1].getX(), (float) b[b.length - 1].getY(),
                            (float) p[n - 1].getX(), (float) p[n - 1].getY());
                } else if (lineStyle == GraphConstants.STYLE_SPLINE && n > 2) {
                    for (double t = 0; t <= 1; t += 0.0125) {
                        double[] xy = spline.getPoint(t);
                        view.sharedPath.lineTo((float) xy[0], (float) xy[1]);
                    }
                } else if (lineStyle == MyRenderer.STYLE_GRAPHVIZ_BEZIER && n > 2) {
                    for (int i = 3; i < p.length; i += 3) {
                        view.sharedPath.curveTo((float) p[i - 2].getX(), (float) p[i - 2].getY(), (float) p[i - 1]
                                .getX(), (float) p[i - 1].getY(), (float) p[i].getX(), (float) p[i].getY());
                    }

                    view.sharedPath.lineTo((float) p[n - 1].getX(), (float) p[n - 1].getY());
                }

                /* END */
                else {
                    for (int i = 1; i < n - 1; i++) {
                        view.sharedPath.lineTo((float) p[i].getX(), (float) p[i].getY());
                    }
                    view.sharedPath.lineTo((float) pe.getX(), (float) pe.getY());
                }
                view.sharedPath.moveTo((float) pe.getX(), (float) pe.getY());
                if (view.endShape == null && view.beginShape == null) {
                    // With no end decorations the line shape is the same as the
                    // shared path and memory
                    view.lineShape = view.sharedPath;
                } else {
                    view.lineShape = (GeneralPath) view.sharedPath.clone();
                    if (view.endShape != null) {
                        view.sharedPath.append(view.endShape, true);
                    }
                    if (view.beginShape != null) {
                        view.sharedPath.append(view.beginShape, true);
                    }
                }
                return view.sharedPath;
            }
            return null;
        }
    }
}
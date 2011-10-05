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

    public RelationEdgeView(RelationEdge aRelation) {
        super(aRelation);
    }

    @Override
    public CellViewRenderer getRenderer() {
        return RENDERER;
    }

    public static class MyRenderer extends EdgeRenderer {

        @Override
        protected Shape createLineEnd(int size, int style, Point2D src, Point2D dst) {

            /*if (style == RelationEdge.LINE_BEGIN) {
                return createLineBeginShape(src, dst);
            }
            if (style == RelationEdge.LINE_END) {
                return createLineEndShape(src, dst);
            } */

            return super.createLineEnd(size, style, src, dst);
        }

        private Shape createLineBeginShape(Point2D aSrc, Point2D aDst) {
            RelationEdge theEdge = (RelationEdge) view.getCell();
            Relation theRelation = (Relation) theEdge.getUserObject();

            Path2D thePath = new Path2D.Double();
            thePath.moveTo(aSrc.getX(), aSrc.getY() + 5);
            thePath.lineTo(aSrc.getX() - 5, aSrc.getY() + 5);
            thePath.lineTo(aSrc.getX() + 5, aSrc.getY() + 5);
            return thePath;
        }

        private Shape createLineEndShape(Point2D aSrc, Point2D aDst) {
            RelationEdge theEdge = (RelationEdge) view.getCell();
            Relation theRelation = (Relation) theEdge.getUserObject();

            Path2D thePath = new Path2D.Double();
            thePath.moveTo(0, -5);
            thePath.lineTo(0, 5);
            thePath.moveTo(-5, 0);
            thePath.lineTo(5, 0);
            return thePath;
        }

    }

    @Override
    public GraphCellEditor getEditor() {
        return new CellEditorFactory();
    }
}
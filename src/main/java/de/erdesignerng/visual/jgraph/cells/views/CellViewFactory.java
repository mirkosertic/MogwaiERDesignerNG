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

import de.erdesignerng.visual.jgraph.cells.CommentCell;
import de.erdesignerng.visual.jgraph.cells.RelationEdge;
import de.erdesignerng.visual.jgraph.cells.SubjectAreaCell;
import de.erdesignerng.visual.jgraph.cells.TableCell;
import de.erdesignerng.visual.jgraph.cells.ViewCell;
import org.jgraph.graph.DefaultCellViewFactory;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.PortView;
import org.jgraph.graph.VertexView;

/**
 * Factory for the cell views.
 *
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-09 19:07:30 $
 */
public class CellViewFactory extends DefaultCellViewFactory {

    @Override
    protected VertexView createVertexView(Object aVertex) {
        if (aVertex instanceof TableCell) {
            return new TableCellView((TableCell) aVertex);
        }
        if (aVertex instanceof SubjectAreaCell) {
            return new SubjectAreaCellView((SubjectAreaCell) aVertex);
        }
        if (aVertex instanceof CommentCell) {
            return new CommentCellView((CommentCell) aVertex);
        }
        if (aVertex instanceof ViewCell) {
            return new ViewCellView((ViewCell) aVertex);
        }

        return super.createVertexView(aVertex);
    }

    @Override
    protected EdgeView createEdgeView(Object aObject) {
        if (aObject instanceof RelationEdge) {
            return new RelationEdgeView((RelationEdge) aObject);
        }
        return super.createEdgeView(aObject);
    }

    @Override
    protected PortView createPortView(Object cell) {
        if (cell instanceof DefaultPort) {
            return new DefaultPortView(cell);
        }
        return super.createPortView(cell);
    }
}

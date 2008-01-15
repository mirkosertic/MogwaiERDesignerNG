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

import org.jgraph.graph.DefaultCellViewFactory;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.PortView;
import org.jgraph.graph.VertexView;

import de.erdesignerng.visual.cells.RelationEdge;
import de.erdesignerng.visual.cells.TableCell;

/**
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2008-01-15 19:22:42 $
 */
public class CellViewFactory extends DefaultCellViewFactory {

    @Override
    protected VertexView createVertexView(Object aVertex) {
        if (aVertex instanceof TableCell) {
            return new TableCellView((TableCell) aVertex);
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

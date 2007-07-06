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
package de.mogwai.erdesignerng.visual.cells.views;

import org.jgraph.graph.DefaultCellViewFactory;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.VertexView;

import de.mogwai.erdesignerng.visual.cells.RelationCell;
import de.mogwai.erdesignerng.visual.cells.TableCell;

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
		if (aObject instanceof RelationCell) {
			return new RelationCellView((RelationCell) aObject);
		}
		return super.createEdgeView(aObject);
	}

}

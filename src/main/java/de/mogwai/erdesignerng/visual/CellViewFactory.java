package de.mogwai.erdesignerng.visual;

import org.jgraph.graph.DefaultCellViewFactory;
import org.jgraph.graph.VertexView;

public class CellViewFactory extends DefaultCellViewFactory {

	@Override
	protected VertexView createVertexView(Object aVertex) {
		if (aVertex instanceof TableCell) {
			return new TableCellView((TableCell) aVertex);
		}

		return super.createVertexView(aVertex);
	}

}

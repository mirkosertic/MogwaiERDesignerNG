package de.mogwai.erdesignerng.visual;

import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.GraphConstants;

public class RelationCell extends DefaultEdge {
	
	public RelationCell() {
		int arrow = GraphConstants.ARROW_CLASSIC;
		GraphConstants.setLineStyle(getAttributes(), GraphConstants.STYLE_ORTHOGONAL);
		GraphConstants.setLineEnd(getAttributes(), arrow);
		GraphConstants.setEndFill(getAttributes(), true);
	}

}

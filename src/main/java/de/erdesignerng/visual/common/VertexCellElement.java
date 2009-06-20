package de.erdesignerng.visual.common;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Rectangle2D;

import org.jgraph.graph.GraphCell;
import org.jgraph.graph.GraphConstants;

import de.mogwai.layout.graph.Element;

public class VertexCellElement extends Element {

	private GraphCell cell;
	
    public VertexCellElement(GraphCell aCell) {
        cell = aCell;
    }
    
    public GraphCell getCell() {
        return cell;
    }

    @Override
    public Point getLocation() {

        GraphCell theCell = getCell();
        Rectangle2D theBounds = GraphConstants.getBounds(theCell.getAttributes());
        return new Point((int) theBounds.getX(), (int) theBounds.getY());
    }

    @Override
    public Dimension getSize() {
        GraphCell theCell = getCell();
        Rectangle2D theBounds = GraphConstants.getBounds(theCell.getAttributes());
        return new Dimension((int) theBounds.getWidth(), (int) theBounds.getHeight());
    }
}
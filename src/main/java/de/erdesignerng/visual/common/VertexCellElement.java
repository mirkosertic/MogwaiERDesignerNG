package de.erdesignerng.visual.common;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Rectangle2D;

import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;

import de.mogwai.layout.graph.Element;

public class VertexCellElement extends Element {

    private DefaultGraphCell cell;

    public VertexCellElement(DefaultGraphCell aCell) {
        cell = aCell;
    }

    @Override
    public Point getLocation() {

        Rectangle2D theBounds = GraphConstants.getBounds(cell.getAttributes());
        return new Point((int) theBounds.getX(), (int) theBounds.getY());
    }

    @Override
    public Dimension getSize() {
        Rectangle2D theBounds = GraphConstants.getBounds(cell.getAttributes());
        return new Dimension((int) theBounds.getWidth(), (int) theBounds.getHeight());
    }

    @Override
    public void setLocation(Point aLocation) {
        Rectangle2D theBounds = GraphConstants.getBounds(cell.getAttributes());
        theBounds.setRect(aLocation.x, aLocation.y, theBounds.getWidth(), theBounds.getHeight());
        GraphConstants.setBounds(cell.getAttributes(), theBounds);
    }
}

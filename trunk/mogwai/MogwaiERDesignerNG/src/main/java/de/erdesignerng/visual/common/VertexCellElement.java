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
package de.erdesignerng.visual.common;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Rectangle2D;

import org.jgraph.graph.CellView;
import org.jgraph.graph.GraphCell;
import org.jgraph.graph.GraphConstants;

import de.mogwai.layout.graph.Element;

public class VertexCellElement extends Element {

    private Point location;

    private Dimension size;

    private CellView view;

    private Point forcePoint;

    private Double radius;

    private Rectangle2D boundaries;

    public VertexCellElement(CellView aView) {
        view = aView;
    }

    public GraphCell getCell() {
        return (GraphCell) view.getCell();
    }

    public CellView getView() {
        return view;
    }

    @Override
    public Point getLocation() {

        if (location == null) {
            GraphCell theCell = getCell();
            Rectangle2D theBounds = GraphConstants.getBounds(theCell.getAttributes());
            location = new Point((int) theBounds.getX(), (int) theBounds.getY());
        }
        return location;
    }

    @Override
    public Dimension getSize() {

        if (size == null) {
            GraphCell theCell = getCell();
            Rectangle2D theBounds = GraphConstants.getBounds(theCell.getAttributes());
            size = new Dimension((int) theBounds.getWidth(), (int) theBounds.getHeight());
        }
        return size;

    }

    @Override
    public double computeRadius() {
        if (radius == null) {
            radius = super.computeRadius();
        }
        return radius;
    }

    @Override
    public Rectangle2D getBoundaries() {
        if (boundaries == null) {
            boundaries = super.getBoundaries();
        }
        return boundaries;
    }

    @Override
    public Point getForcePoint() {
        if (forcePoint == null) {
            forcePoint = super.getForcePoint();
        }
        return forcePoint;
    }
}
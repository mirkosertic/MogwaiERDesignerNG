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
package de.erdesignerng.visual.layout.jung;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.HashSet;

import org.jgraph.JGraph;
import org.jgraph.graph.CellView;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;

import de.erdesignerng.util.ApplicationPreferences;
import de.erdesignerng.visual.cells.TableCell;
import de.erdesignerng.visual.layout.LayoutException;
import de.erdesignerng.visual.layout.SizeableLayouter;
import edu.uci.ics.jung.graph.DirectedEdge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;
import edu.uci.ics.jung.visualization.Layout;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-03-11 20:27:52 $
 */
public abstract class JungLayouter implements SizeableLayouter {
    
    protected int maxIterations = 100;
    
    private Dimension size;
    
    protected JungLayouter() {
    }
    
    protected abstract Layout createLayout(Graph aGraph);

    public void applyLayout(ApplicationPreferences aPreferences, JGraph aGraph, Object[] aCells) throws LayoutException {

        Graph theGraph = new DirectedSparseGraph();

        Collection<ERDesignerJungLayoutEntity> theTables = new HashSet<ERDesignerJungLayoutEntity>();
        Collection<ERDesignerJungLayoutRelationship> theRelationships = new HashSet<ERDesignerJungLayoutRelationship>();

        GraphModel theModel = aGraph.getModel();
        GraphLayoutCache theLayoutCache = aGraph.getGraphLayoutCache();

        // First the tables
        for (Object cell : aCells) {

            if (theModel.isPort(cell)) {
                continue;
            } else if (!theModel.isEdge(cell)) {

                CellView theCellView = theLayoutCache.getMapping(cell, true);
                TableCell theCell = (TableCell) theCellView.getCell();
                //Rectangle2D theBounds = GraphConstants.getBounds(((TableCell) theCellView.getCell()).getAttributes());

                Vertex theVertex = theGraph.addVertex(new DirectedSparseVertex());
                ERDesignerJungLayoutEntity theEntity = new ERDesignerJungLayoutEntity(theVertex, theCell);

                theTables.add(theEntity);
            }
        }

        // Now the relations
        for (Object cell : aCells) {

            if (theModel.isPort(cell)) {
                continue;
            } else if (theModel.isEdge(cell)) {

                EdgeView theEdgeView = (EdgeView) theLayoutCache.getMapping(cell, true);

                Object theSource = DefaultGraphModel.getSourceVertex(theModel, theEdgeView.getCell());
                Object theTarget = DefaultGraphModel.getTargetVertex(theModel, theEdgeView.getCell());

                CellView theSourceView = theLayoutCache.getMapping(theSource, true);
                CellView theTargetView = theLayoutCache.getMapping(theTarget, true);

                TableCell theSourceCell = (TableCell) theSourceView.getCell();
                TableCell theTargetCell = (TableCell) theTargetView.getCell();

                ERDesignerJungLayoutEntity theSourceEntity = findByEntity(theTables, theSourceCell);
                ERDesignerJungLayoutEntity theTargetEntity = findByEntity(theTables, theTargetCell);

                ERDesignerJungLayoutRelationship theRelation = new ERDesignerJungLayoutRelationship();
                theRelation.setSourceInLayout(theSourceEntity);
                theRelation.setDestinationInLayout(theTargetEntity);

                DirectedEdge theEdge = (DirectedEdge) theGraph.addEdge(new DirectedSparseEdge(theSourceEntity
                        .getVertex(), theTargetEntity.getVertex()));
                theRelation.setEdge(theEdge);

                theRelationships.add(theRelation);
            }
        }

        Layout theLayout = createLayout(theGraph);
        theLayout.initialize(size);

        if (theLayout.isIncremental()) {
            
            int theIterations = 0;
            
            while ((!theLayout.incrementsAreDone()) && (theIterations++ < maxIterations)) {
                theLayout.advancePositions();
            }
        }

        for (ERDesignerJungLayoutEntity theEntity : theTables) {

            TableCell theCell = theEntity.getCell();
            Point2D thePoint = theLayout.getLocation(theEntity.getVertex());

            Rectangle2D theOldDimensions = GraphConstants.getBounds(theCell.getAttributes());
            GraphConstants.setBounds(theCell.getAttributes(), new Rectangle2D.Double(thePoint.getX(), thePoint.getY(),
                    theOldDimensions.getWidth(), theOldDimensions.getHeight()));

            theLayoutCache.editCell(theCell, theCell.getAttributes());
        }
    }

    private ERDesignerJungLayoutEntity findByEntity(Collection<ERDesignerJungLayoutEntity> aNodes, TableCell aSourceCell) {
        for (ERDesignerJungLayoutEntity theEntity : aNodes) {
            if (theEntity.getCell() == aSourceCell) {
                return theEntity;
            }
        }
        return null;
    }
    
    public void setSize(Dimension aSize) {
        size = aSize;
    }
}

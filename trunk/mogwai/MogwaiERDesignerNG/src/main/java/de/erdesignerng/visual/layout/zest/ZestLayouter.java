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
package de.erdesignerng.visual.layout.zest;

import java.awt.Dimension;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.HashSet;

import org.eclipse.mylyn.zest.layouts.InvalidLayoutConfiguration;
import org.eclipse.mylyn.zest.layouts.LayoutAlgorithm;
import org.eclipse.mylyn.zest.layouts.progress.ProgressEvent;
import org.eclipse.mylyn.zest.layouts.progress.ProgressListener;
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

public class ZestLayouter<T extends LayoutAlgorithm> implements SizeableLayouter {

    protected T layout;
    
    private Dimension size;

    protected ZestLayouter(T aLayout) {
        layout = aLayout;
        layout.addProgressListener(new ProgressListener() {

            public void progressEnded(ProgressEvent aEvent) {
                System.out.println("Layout finished");
            }

            public void progressStarted(ProgressEvent aEvent) {
                System.out.println("Layout start");
            }

            public void progressUpdated(ProgressEvent aEvent) {
                System.out.println("Layout progress " + aEvent.getStepsCompleted() + " / "
                        + aEvent.getTotalNumberOfSteps());
            }

        });
    }

    public void applyLayout(ApplicationPreferences aPreferences, JGraph aGraph, Object[] aCells) throws LayoutException {

        Collection<ERDesignerZestLayoutEntity> theTables = new HashSet<ERDesignerZestLayoutEntity>();
        Collection<ERDesignerZestLayoutRelationship> theRelationships = new HashSet<ERDesignerZestLayoutRelationship>();

        GraphModel theModel = aGraph.getModel();
        GraphLayoutCache theLayoutCache = aGraph.getGraphLayoutCache();

        // First the tables
        for (Object cell : aCells) {

            if (theModel.isPort(cell)) {
                continue;
            } else if (!theModel.isEdge(cell)) {

                CellView theCellView = theLayoutCache.getMapping(cell, true);
                TableCell theCell = (TableCell) theCellView.getCell();
                Rectangle2D theBounds = GraphConstants.getBounds(((TableCell) theCellView.getCell()).getAttributes());

                ERDesignerZestLayoutEntity theEntity = new ERDesignerZestLayoutEntity();
                theEntity.setCell(theCell);
                theEntity.setLocationInLayout(theBounds.getX(), theBounds.getY());
                theEntity.setSizeInLayout(theBounds.getWidth(), theBounds.getHeight());

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

                ERDesignerZestLayoutEntity theSourceEntity = findByEntity(theTables, theSourceCell);
                ERDesignerZestLayoutEntity theTargetEntity = findByEntity(theTables, theTargetCell);

                ERDesignerZestLayoutRelationship theRelation = new ERDesignerZestLayoutRelationship();
                theRelation.setSourceInLayout(theSourceEntity);
                theRelation.setDestinationInLayout(theTargetEntity);

                theRelationships.add(theRelation);
                
                layout.addRelationship(theRelation);
            }
        }

        ERDesignerZestLayoutEntity[] theEntities = theTables.toArray(new ERDesignerZestLayoutEntity[theTables.size()]);
        ERDesignerZestLayoutRelationship[] theRelations = theRelationships
                .toArray(new ERDesignerZestLayoutRelationship[theRelationships.size()]);

        int theTotalWidth = size.width;
        int theTotalHeight = size.height;
        double theRatio = theTotalWidth / theTotalHeight;
        
        layout.setEntityAspectRatio(theRatio);
        
        try {
            layout.applyLayout(theEntities, theRelations, 0, 0, theTotalWidth, theTotalHeight, false, false);
        } catch (InvalidLayoutConfiguration e) {
            throw new LayoutException("Error during layouting", e);
        }

        for (ERDesignerZestLayoutEntity theEntity : theEntities) {
            TableCell theCell = theEntity.getCell();
            double theX = theEntity.getXInLayout();
            double theY = theEntity.getYInLayout();
            double theWidth = theEntity.getWidthInLayout();
            double theHeight = theEntity.getHeightInLayout();

            //Rectangle2D theOldDimensions = GraphConstants.getBounds(theCell.getAttributes());
            // GraphConstants.setBounds(theCell.getAttributes(), new
            // Rectangle2D.Double(theX, theY, theOldDimensions.getWidth(),
            // theOldDimensions.getHeight()));
            GraphConstants.setBounds(theCell.getAttributes(), new Rectangle2D.Double(theX, theY, theWidth, theHeight));

            theLayoutCache.editCell(theCell, theCell.getAttributes());
        }
    }

    private ERDesignerZestLayoutEntity findByEntity(Collection<ERDesignerZestLayoutEntity> aNodes, TableCell aSourceCell) {
        for (ERDesignerZestLayoutEntity theEntity : aNodes) {
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

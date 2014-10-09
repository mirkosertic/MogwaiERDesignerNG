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
package de.erdesignerng.visual.jgraph;

import de.erdesignerng.model.ModelItem;
import de.erdesignerng.model.Relation;
import de.erdesignerng.visual.jgraph.cells.ModelCellWithPosition;
import de.erdesignerng.visual.jgraph.cells.RelationEdge;
import de.erdesignerng.visual.jgraph.cells.SubjectAreaCell;
import de.erdesignerng.visual.jgraph.cells.views.RelationEdgeView;
import de.mogwai.layout.ElectricSpringLayout;
import de.mogwai.layout.graph.Spring;
import org.jgraph.graph.CellView;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphCell;
import org.jgraph.graph.GraphConstants;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ERDesignerGraphLayout extends ElectricSpringLayout<VertexCellElement, CellView> {

    private final JGraphEditor component;

    public ERDesignerGraphLayout(JGraphEditor aComponent) {
        component = aComponent;
    }

    private final List<VertexCellElement> elements = new ArrayList<>();

    private final List<Spring<CellView, VertexCellElement>> springs = new ArrayList<>();

    private final Map<Object, Map> modelModifications = new HashMap<>();

    private final Set<ModelItem> elementsToIgnore = new HashSet<>();

    @Override
    public boolean preEvolveLayout() {
        super.preEvolveLayout();

        elements.clear();
        springs.clear();
        modelModifications.clear();

        if (component.getGraph() == null) {
            return false;
        }

        elementsToIgnore.clear();
        if (component.getGraph().isDragging()) {
            for (Object theCell : component.getGraph().getSelectionCells()) {
                if (theCell instanceof ModelCellWithPosition) {
                    ModelCellWithPosition<ModelItem> theTableCell = (ModelCellWithPosition<ModelItem>) theCell;
                    elementsToIgnore.add((ModelItem) theTableCell.getUserObject());
                }
                if (theCell instanceof SubjectAreaCell) {
                    ((SubjectAreaCell) theCell).getChildren().stream().filter(theChildCell -> theChildCell instanceof ModelCellWithPosition).forEach(theChildCell -> {
                        DefaultGraphCell theGraphCell = (DefaultGraphCell) theChildCell;
                        elementsToIgnore.add((ModelItem) theGraphCell.getUserObject());
                    });
                }
            }
        }

        Map<ModelItem, VertexCellElement> theTables = new HashMap<>();
        Set<RelationEdgeView> theRelations = new HashSet<>();

        for (CellView theView : component.getGraph().getGraphLayoutCache().getAllViews()) {

            if (theView.getCell() instanceof ModelCellWithPosition) {

                DefaultGraphCell theCell = (DefaultGraphCell) theView.getCell();

                ModelItem theModelItem = (ModelItem) theCell.getUserObject();
                if (!elementsToIgnore.contains(theModelItem)) {

                    VertexCellElement theElement = new VertexCellElement(theView);

                    theTables.put(theModelItem, theElement);
                    elements.add(theElement);
                }
            }

            if (theView instanceof RelationEdgeView) {
                theRelations.add((RelationEdgeView) theView);
            }
        }

        for (RelationEdgeView theRelationView : theRelations) {

            RelationEdge theCell = (RelationEdge) theRelationView.getCell();
            Relation theRelation = (Relation) theCell.getUserObject();

            if (!elementsToIgnore.contains(theRelation.getExportingTable())
                    && (!elementsToIgnore.contains(theRelation.getImportingTable()))) {
                Spring<CellView, VertexCellElement> theSpring = new Spring<>(theTables
                        .get(theRelation.getExportingTable()), theTables.get(theRelation.getImportingTable()),
                        theRelationView);
                springs.add(theSpring);
            }
        }

        return true;
    }

    @Override
    public void postEvolveLayout() {
        super.postEvolveLayout();

        // Move graph origin to 20,20
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;

        List<DefaultGraphCell> theCells = new ArrayList<>();

        for (CellView theView : component.getGraph().getGraphLayoutCache().getAllViews()) {

            Object theObjectCell = theView.getCell();

            if (theObjectCell instanceof ModelCellWithPosition) {
                DefaultGraphCell theCell = (DefaultGraphCell) theObjectCell;

                Map theAttributes = modelModifications.get(theCell);
                if (theAttributes == null) {
                    theAttributes = theCell.getAttributes();
                }

                Rectangle2D theBounds = GraphConstants.getBounds(theAttributes);
                minX = (int) Math.min(minX, theBounds.getX());
                minY = (int) Math.min(minY, theBounds.getY());

                theCells.add(theCell);
            }

            if (theObjectCell instanceof SubjectAreaCell) {
                for (Object theChildCell : ((SubjectAreaCell) theObjectCell).getChildren()) {
                    if (theChildCell instanceof ModelCellWithPosition) {

                        DefaultGraphCell theCell = (DefaultGraphCell) theChildCell;

                        Map theAttributes = modelModifications.get(theCell);
                        if (theAttributes == null) {
                            theAttributes = theCell.getAttributes();
                        }

                        Rectangle2D theBounds = GraphConstants.getBounds(theAttributes);
                        minX = (int) Math.min(minX, theBounds.getX());
                        minY = (int) Math.min(minY, theBounds.getY());

                        theCells.add(theCell);
                    }
                }
            }
        }

        if (minX < 20 || minY < 20) {
            int mx = minX < 20 ? 20 - minX : 0;
            int my = minY < 20 ? 20 - minY : 0;
            for (DefaultGraphCell theCell : theCells) {
                evolvePosition(theCell, mx, my);
            }
        }

        if (modelModifications.size() > 0) {
            component.getGraph().getGraphLayoutCache().edit(modelModifications);
        }
    }

    @Override
    public List<VertexCellElement> getElements() {
        return elements;
    }

    @Override
    public List<Spring<CellView, VertexCellElement>> getSprings() {
        return springs;
    }

    private void evolvePosition(GraphCell aCell, int movementX, int movementY) {

        if (movementX != 0 || movementY != 0) {
            Rectangle2D theBounds;
            Map theAttributes = modelModifications.get(aCell);
            if (theAttributes != null) {
                theBounds = GraphConstants.getBounds(theAttributes);
            } else {
                theAttributes = new HashMap();
                theBounds = GraphConstants.getBounds(aCell.getAttributes());

                modelModifications.put(aCell, theAttributes);
            }

            theBounds.setRect(theBounds.getX() + movementX, theBounds.getY() + movementY, theBounds.getWidth(),
                    theBounds.getHeight());
            GraphConstants.setBounds(theAttributes, theBounds);
        }
    }

    @Override
    public void evolvePosition(VertexCellElement aElement, int movementX, int movementY) {
        evolvePosition(aElement.getCell(), movementX, movementY);
    }
}
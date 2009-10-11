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

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgraph.graph.CellView;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;

import de.erdesignerng.model.ModelItem;
import de.erdesignerng.model.Relation;
import de.erdesignerng.visual.cells.ModelCellWithPosition;
import de.erdesignerng.visual.cells.RelationEdge;
import de.erdesignerng.visual.cells.SubjectAreaCell;
import de.erdesignerng.visual.cells.views.RelationEdgeView;
import de.mogwai.layout.ElectricSpringLayout;
import de.mogwai.layout.graph.Spring;

public class ERDesignerGraphLayout extends ElectricSpringLayout<VertexCellElement, CellView> {

    private ERDesignerComponent component;

    public ERDesignerGraphLayout(ERDesignerComponent aComponent) {
        component = aComponent;
    }

    private List<VertexCellElement> elements = new ArrayList<VertexCellElement>();

    private List<Spring<CellView, VertexCellElement>> springs = new ArrayList<Spring<CellView, VertexCellElement>>();

    private Map<Object, Map> modelModifications = new HashMap<Object, Map>();

    private Set<ModelItem> elementsToIgnore = new HashSet<ModelItem>();

    @Override
    public boolean preEvolveLayout() {
        super.preEvolveLayout();

        elements.clear();
        springs.clear();
        modelModifications.clear();

        if (component.graph == null) {
            return false;
        }

        elementsToIgnore.clear();
        if (component.graph.isDragging()) {
            for (Object theCell : component.graph.getSelectionCells()) {
                if (theCell instanceof ModelCellWithPosition) {
                    ModelCellWithPosition<ModelItem> theTableCell = (ModelCellWithPosition<ModelItem>) theCell;
                    elementsToIgnore.add((ModelItem) theTableCell.getUserObject());
                }
                if (theCell instanceof SubjectAreaCell) {
                    for (Object theChildCell : ((SubjectAreaCell) theCell).getChildren()) {
                        if (theCell instanceof ModelCellWithPosition) {
                            DefaultGraphCell theGraphCell = (DefaultGraphCell) theChildCell;
                            elementsToIgnore.add((ModelItem) theGraphCell.getUserObject());
                        }
                    }
                }
            }
        }

        Map<ModelItem, VertexCellElement> theTables = new HashMap<ModelItem, VertexCellElement>();
        Set<RelationEdgeView> theRelations = new HashSet<RelationEdgeView>();

        for (CellView theView : component.graph.getGraphLayoutCache().getAllViews()) {

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
                Spring<CellView, VertexCellElement> theSpring = new Spring<CellView, VertexCellElement>(theTables
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
        //TODO:

        if (modelModifications.size() > 0) {

            component.graph.getGraphLayoutCache().edit(modelModifications);
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

    @Override
    public void evolvePosition(VertexCellElement aElement, int movementX, int movementY) {

        if (movementX != 0 || movementY != 0) {

            Rectangle2D theBounds;
            Map theAttributes = modelModifications.get(aElement.getCell());
            if (theAttributes != null) {
                theBounds = GraphConstants.getBounds(theAttributes);
            } else {
                theAttributes = new HashMap();
                theBounds = GraphConstants.getBounds(aElement.getCell().getAttributes());

                modelModifications.put(aElement.getCell(), theAttributes);
            }

            theBounds.setRect(theBounds.getX() + movementX, theBounds.getY() + movementY, theBounds.getWidth(),
                    theBounds.getHeight());
            GraphConstants.setBounds(theAttributes, theBounds);
        }
    }
}

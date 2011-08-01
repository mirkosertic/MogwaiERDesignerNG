package de.erdesignerng.visual.common;

import de.erdesignerng.model.Relation;
import de.erdesignerng.model.Table;
import de.erdesignerng.visual.cells.RelationEdge;
import de.erdesignerng.visual.cells.views.RelationEdgeView;

import java.util.HashSet;
import java.util.Set;

/**
 * Command to show or hide all relations of a given table.
 */
public class ShowHideTableRelationsCommand extends UICommand {

    private Table table;
    private boolean show;

    public ShowHideTableRelationsCommand(ERDesignerComponent aComponent, Table aTable, boolean aShow) {
        super(aComponent);

        table = aTable;
        show  = aShow;
    }

    @Override
    public void execute() {
        Set<RelationEdge> theCellsToHide = new HashSet<RelationEdge>();
        for (Object theItem : component.graph.getGraphLayoutCache().getCellViews()) {
            if (theItem instanceof RelationEdgeView) {
                RelationEdgeView theView = (RelationEdgeView) theItem;
                RelationEdge theCell = (RelationEdge) theView.getCell();
                Relation theRelation = (Relation) theCell.getUserObject();
                if (theRelation.getExportingTable() == table || theRelation.getImportingTable() == table) {
                    theCellsToHide.add(theCell);
                }
            }
        }
        if (show) {
            component.graph.getGraphLayoutCache().showCells(theCellsToHide.toArray(new RelationEdge[theCellsToHide.size()]), true);
        } else {
            component.graph.getGraphLayoutCache().hideCells(theCellsToHide.toArray(new RelationEdge[theCellsToHide.size()]), true);
        }
    }
}

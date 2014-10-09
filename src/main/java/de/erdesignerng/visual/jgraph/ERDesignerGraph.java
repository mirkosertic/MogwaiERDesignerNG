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

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.exception.ElementAlreadyExistsException;
import de.erdesignerng.exception.ElementInvalidNameException;
import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.Comment;
import de.erdesignerng.model.IndexExpression;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.Relation;
import de.erdesignerng.model.SubjectArea;
import de.erdesignerng.model.Table;
import de.erdesignerng.model.View;
import de.erdesignerng.modificationtracker.VetoException;
import de.erdesignerng.visual.DisplayLevel;
import de.erdesignerng.visual.DisplayOrder;
import de.erdesignerng.visual.MessagesHelper;
import de.erdesignerng.visual.jgraph.cells.CommentCell;
import de.erdesignerng.visual.jgraph.cells.HideableCell;
import de.erdesignerng.visual.jgraph.cells.RelationEdge;
import de.erdesignerng.visual.jgraph.cells.SubjectAreaCell;
import de.erdesignerng.visual.jgraph.cells.TableCell;
import de.erdesignerng.visual.jgraph.cells.ViewCell;
import de.erdesignerng.visual.jgraph.cells.views.RelationEdgeView;
import de.erdesignerng.visual.jgraph.tools.BaseTool;
import org.jgraph.JGraph;
import org.jgraph.graph.CellView;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-09 19:07:31 $
 */
public abstract class ERDesignerGraph extends JGraph {

    private final Model model;

    private boolean displayComments;

    private boolean dragging;

    private DisplayLevel displayLevel = DisplayLevel.ALL;

    private DisplayOrder displayOrder = DisplayOrder.NATURAL;

    public ERDesignerGraph(Model aDBModel, GraphModel aModel,
                           GraphLayoutCache aLayoutCache) {
        super(aModel, aLayoutCache);
        model = aDBModel;

        setMoveIntoGroups(true);
        setMoveOutOfGroups(true);
    }

    /**
     * @return the model
     */
    public Model getDBModel() {
        return model;
    }

    public void setTool(BaseTool aTool) {
        setMarqueeHandler(aTool);
    }

    public void commandDeleteCells(List<DefaultGraphCell> aCells)
            throws VetoException {

        GraphModel theModel = getModel();

        List<DefaultGraphCell> theObjectsToRemove = new ArrayList<>();

        for (DefaultGraphCell theSingleCell : aCells) {

            if (!theObjectsToRemove.contains(theSingleCell)) {
                if (theSingleCell instanceof RelationEdge) {
                    RelationEdge theEdge = (RelationEdge) theSingleCell;

                    Relation theRelation = (Relation) theEdge.getUserObject();

                    getDBModel().removeRelation(theRelation);
                    theModel.remove(new Object[]{theEdge});

                    for (Map.Entry<IndexExpression, Attribute<Table>> theEntry : theRelation.getMapping().entrySet()) {
                        Attribute<Table> theImportingAttribute = theEntry.getValue();

                        if (!(theImportingAttribute.isForeignKey() || theImportingAttribute
                                .isPrimaryKey())) {
                            // Only attributes not used in foreign keys or
                            // primary keys can
                            // be dropped, as other things might corrupt the
                            // database if the
                            // update script is run against a filled database
                            if (MessagesHelper.displayQuestionMessage(this,
                                    ERDesignerBundle.DELETENOTUSEDATTRIBUTES,
                                    theImportingAttribute.getName(),
                                    theImportingAttribute.getOwner().getName())) {
                                try {
                                    getDBModel().removeAttributeFromTable(
                                            theRelation.getImportingTable(),
                                            theImportingAttribute);
                                } catch (ElementInvalidNameException e) {
                                    throw new VetoException(
                                            "Cannot recreate existing index", e);
                                } catch (ElementAlreadyExistsException e) {
                                    throw new VetoException(
                                            "Cannot recreate existing index", e);
                                }
                            }
                        }
                    }
                }

                if (theSingleCell instanceof CommentCell) {
                    CommentCell theComment = (CommentCell) theSingleCell;

                    getDBModel().removeComment(
                            (Comment) theComment.getUserObject());
                    theModel.remove(new Object[]{theComment});
                }

                if (theSingleCell instanceof ViewCell) {
                    ViewCell theViewCell = (ViewCell) theSingleCell;

                    getDBModel().removeView((View) theViewCell.getUserObject());
                    theModel.remove(new Object[]{theViewCell});
                }

                if (theSingleCell instanceof TableCell) {
                    TableCell theCell = (TableCell) theSingleCell;
                    Table theTable = (Table) theCell.getUserObject();

                    theObjectsToRemove.add(theCell);

                    CellView[] theViews = getGraphLayoutCache().getAllViews();
                    for (CellView theView : theViews) {
                        if (theView instanceof RelationEdgeView) {
                            RelationEdgeView theRelationView = (RelationEdgeView) theView;
                            RelationEdge theEdge = (RelationEdge) theRelationView
                                    .getCell();
                            TableCell theSource = (TableCell) ((DefaultPort) theEdge
                                    .getSource()).getParent();
                            TableCell theDestination = (TableCell) ((DefaultPort) theEdge
                                    .getTarget()).getParent();

                            if (theTable.equals(theSource.getUserObject())) {
                                getDBModel().removeRelation(
                                        (Relation) theEdge.getUserObject());
                                theObjectsToRemove.add(theEdge);
                            } else {
                                if (theTable.equals(theDestination
                                        .getUserObject())) {
                                    getDBModel().removeRelation(
                                            (Relation) theEdge.getUserObject());
                                    theObjectsToRemove.add(theEdge);
                                }
                            }
                        }
                    }

                    getDBModel().removeTable(theTable);
                }
            }
        }

        theModel.remove(theObjectsToRemove.toArray());

        refreshOutline();
    }

    /**
     * Add a new table to the model.
     *
     * @param aPoint the location
     */
    public abstract void commandNewTable(Point2D aPoint);

    /**
     * Add a new view to the model.
     *
     * @param aPoint the location
     */
    public abstract void commandNewView(Point2D aPoint);

    /**
     * Create a new subject area for a set of cells.
     *
     * @param aCells the cells to add to the subject area
     */
    public void commandAddToNewSubjectArea(List<DefaultGraphCell> aCells) {

        SubjectArea theArea = new SubjectArea();
        theArea.setExpanded(true);
        SubjectAreaCell theSubjectAreaCell = new SubjectAreaCell(theArea);
        for (DefaultGraphCell theCell : aCells) {
            Object theUserObject = theCell.getUserObject();
            if (theUserObject instanceof Table) {
                theArea.getTables().add((Table) theUserObject);
            }
            if (theUserObject instanceof View) {
                theArea.getViews().add((View) theUserObject);
            }
        }

        getGraphLayoutCache().insertGroup(theSubjectAreaCell, aCells.toArray());

        model.addSubjectArea(theArea);
        getGraphLayoutCache().toBack(new Object[]{theSubjectAreaCell});
    }

    /**
     * @return the displayComments
     */
    public boolean isDisplayComments() {
        return displayComments;
    }

    /**
     * @param displayComments the displayComments to set
     */
    public void setDisplayComments(boolean displayComments) {
        this.displayComments = displayComments;
    }

    /**
     * Add a new comment to the model.
     *
     * @param aLocation the location
     */
    public abstract void commandNewComment(Point2D aLocation);

    /**
     * Add a relation to the model.
     *
     * @param aImportingCell - importing Cell
     * @param aExportingCell - exporting Cell
     */
    public abstract void commandNewRelation(TableCell aImportingCell,
                                            TableCell aExportingCell);

    /**
     * @return the displayLevel
     */
    public DisplayLevel getDisplayLevel() {
        return displayLevel;
    }

    /**
     * @param displayLevel the displayLevel to set
     */
    public void setDisplayLevel(DisplayLevel displayLevel) {
        this.displayLevel = displayLevel;
    }

    /**
     * @return the displayOrder
     */
    public DisplayOrder getDisplayOrder() {
        return displayOrder;
    }

    /**
     * @param displayOrder the displayOrder to set
     */
    public void setDisplayOrder(DisplayOrder displayOrder) {
        this.displayOrder = displayOrder;
    }

    /**
     * @return the dragging
     */
    public boolean isDragging() {
        return dragging;
    }

    /**
     * @param dragging the dragging to set
     */
    public void setDragging(boolean dragging) {
        this.dragging = dragging;
    }

    @Override
    public void repaint() {
        addOffscreenDirty(new Rectangle2D.Double(0, 0, getWidth(), getHeight()));
        super.repaint();
    }

    /**
     * Hide a list of specific cells.
     *
     * @param aCellsToHide the cells to hide
     */
    public abstract void commandHideCells(List<HideableCell> aCellsToHide);

    /**
     * Create a new table and add if to a new relation.
     *
     * @param aLocation           the location where the table should be created
     * @param aExportingTableCell the exporting table cell
     * @param aNewTableIsChild    true, if the new table should be the child of the new relation
     */
    public abstract void commandNewTableAndRelation(Point2D aLocation,
                                                    TableCell aExportingTableCell, boolean aNewTableIsChild);

    /**
     * Update the outline.
     */
    public abstract void refreshOutline();

    private void toggleVisibilityOfRelationsInSubjectArea(SubjectAreaCell aCell, boolean aVisibilityState) {

        Set<Relation> theRelationDoWork = new HashSet<>();

        SubjectArea theSA = (SubjectArea) aCell.getUserObject();
        for (Table theTable : theSA.getTables()) {
            theRelationDoWork.addAll(model.getRelations().getForeignKeysFor(theTable).stream().filter(theRelation -> theSA.getTables().contains(theRelation.getExportingTable())).collect(Collectors.toList()));
        }

        Set<DefaultGraphCell> theCellsToWork = new HashSet<>();

        for (CellView theView : getGraphLayoutCache().getAllViews()) {
            if (theView instanceof RelationEdgeView) {
                RelationEdgeView theEdgeView = (RelationEdgeView) theView;
                DefaultGraphCell theCell = (DefaultGraphCell) theEdgeView.getCell();
                if (theRelationDoWork.contains(theCell.getUserObject())) {
                    theCellsToWork.add(theCell);
                }
            }
        }

        if (theCellsToWork.size() > 0) {
            if (aVisibilityState) {
                getGraphLayoutCache().showCells(theCellsToWork.toArray(new DefaultGraphCell[theCellsToWork.size()]), true);
            } else {
                getGraphLayoutCache().hideCells(theCellsToWork.toArray(new DefaultGraphCell[theCellsToWork.size()]), true);
            }
        }
    }

    public void setSubjectAreaCellExpanded(SubjectAreaCell aCell) {

        getGraphLayoutCache().expand(new Object[]{aCell});
        aCell.setExpanded(true);

        toggleVisibilityOfRelationsInSubjectArea(aCell, true);

        getGraphLayoutCache().editCell(aCell, aCell.getAttributes());
    }

    public void setSubjectAreaCellCollapsed(SubjectAreaCell aCell) {

        getGraphLayoutCache().collapse(new Object[]{aCell});
        aCell.setExpanded(false);

        toggleVisibilityOfRelationsInSubjectArea(aCell, false);

        getGraphLayoutCache().editCell(aCell, aCell.getAttributes());
    }
}

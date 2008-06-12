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
package de.erdesignerng.visual;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import org.jgraph.JGraph;
import org.jgraph.graph.CellView;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;

import de.erdesignerng.model.Comment;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.Relation;
import de.erdesignerng.model.SubjectArea;
import de.erdesignerng.model.Table;
import de.erdesignerng.modificationtracker.VetoException;
import de.erdesignerng.visual.cells.CommentCell;
import de.erdesignerng.visual.cells.RelationEdge;
import de.erdesignerng.visual.cells.SubjectAreaCell;
import de.erdesignerng.visual.cells.TableCell;
import de.erdesignerng.visual.cells.views.RelationEdgeView;
import de.erdesignerng.visual.tools.BaseTool;

/**
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2008-06-12 20:14:58 $
 */
public abstract class ERDesignerGraph extends JGraph {

    private Model model;
    
    private boolean displayComments;

    public ERDesignerGraph(Model aDBModel, GraphModel aModel, GraphLayoutCache aLayoutCache) {
        super(aModel, aLayoutCache);
        // setPortsVisible(true);
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

    public void commandDeleteCells(Object[] aCells) throws VetoException {

        GraphModel theModel = getModel();

        List<DefaultGraphCell> theObjectsToRemove = new ArrayList<DefaultGraphCell>();

        for (Object theSingleCell : aCells) {

            if (!theObjectsToRemove.contains(theSingleCell)) {
                if (theSingleCell instanceof RelationEdge) {
                    RelationEdge theEdge = (RelationEdge) theSingleCell;

                    getDBModel().removeRelation((Relation) theEdge.getUserObject());
                    theModel.remove(new Object[] { theEdge });
                }

                if (theSingleCell instanceof CommentCell) {
                    CommentCell theComment = (CommentCell) theSingleCell;

                    getDBModel().removeComment((Comment) theComment.getUserObject());
                    theModel.remove(new Object[] { theComment });
                }

                if (theSingleCell instanceof TableCell) {
                    TableCell theCell = (TableCell) theSingleCell;
                    Table theTable = (Table) theCell.getUserObject();

                    theObjectsToRemove.add(theCell);

                    CellView[] theViews = getGraphLayoutCache().getAllViews();
                    for (CellView theView : theViews) {
                        if (theView instanceof RelationEdgeView) {
                            RelationEdgeView theRelationView = (RelationEdgeView) theView;
                            RelationEdge theEdge = (RelationEdge) theRelationView.getCell();
                            TableCell theSource = (TableCell) ((DefaultPort) theEdge.getSource()).getParent();
                            TableCell theDestination = (TableCell) ((DefaultPort) theEdge.getTarget()).getParent();

                            if (theTable.equals(theSource.getUserObject())) {
                                getDBModel().removeRelation((Relation) theEdge.getUserObject());
                                theObjectsToRemove.add(theEdge);
                            } else {
                                if (theTable.equals(theDestination.getUserObject())) {
                                    getDBModel().removeRelation((Relation) theEdge.getUserObject());
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
    }

    /**
     * Add a new taböe to the model. 
     * 
     * @param aPoint the location
     */
    public abstract void commandNewTable(Point2D aPoint);

    /**
     * Create a new subject area for a set of cells.
     * 
     * @param aCells
     *            the cells to add to the subject area
     */
    public void commandAddToNewSubjectArea(List<TableCell> aCells) {

        SubjectArea theArea = new SubjectArea();
        SubjectAreaCell theSubjectAreaCell = new SubjectAreaCell(theArea);
        for (TableCell theCell : aCells) {
            theArea.getTables().add((Table) theCell.getUserObject());
        }

        getGraphLayoutCache().insertGroup(theSubjectAreaCell, aCells.toArray());

        model.addSubjectArea(theArea);
        getGraphLayoutCache().toBack(new Object[] { theSubjectAreaCell });
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
}

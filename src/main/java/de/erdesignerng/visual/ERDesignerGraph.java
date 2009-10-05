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
import java.awt.geom.Rectangle2D;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jgraph.JGraph;
import org.jgraph.graph.CellView;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphCell;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.exception.ElementAlreadyExistsException;
import de.erdesignerng.exception.ElementInvalidNameException;
import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.Comment;
import de.erdesignerng.model.Index;
import de.erdesignerng.model.IndexExpression;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.ModelUtilities;
import de.erdesignerng.model.Relation;
import de.erdesignerng.model.SubjectArea;
import de.erdesignerng.model.Table;
import de.erdesignerng.model.View;
import de.erdesignerng.modificationtracker.VetoException;
import de.erdesignerng.util.ApplicationPreferences;
import de.erdesignerng.visual.cells.CommentCell;
import de.erdesignerng.visual.cells.HideableCell;
import de.erdesignerng.visual.cells.ModelCell;
import de.erdesignerng.visual.cells.RelationEdge;
import de.erdesignerng.visual.cells.SubjectAreaCell;
import de.erdesignerng.visual.cells.TableCell;
import de.erdesignerng.visual.cells.ViewCell;
import de.erdesignerng.visual.cells.views.RelationEdgeView;
import de.erdesignerng.visual.editor.DialogConstants;
import de.erdesignerng.visual.editor.relation.RelationEditor;
import de.erdesignerng.visual.tools.BaseTool;

/**
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-09 19:07:31 $
 */
public abstract class ERDesignerGraph extends JGraph {

    private static final Logger LOGGER = Logger.getLogger(ERDesignerGraph.class);

    private Model model;

    private boolean displayComments;

    private boolean physicalLayout;

    private boolean dragging;

    private DisplayLevel displayLevel = DisplayLevel.ALL;

    private DisplayOrder displayOrder = DisplayOrder.NATURAL;
    
    private ApplicationPreferences preferences;

    public ERDesignerGraph(ApplicationPreferences aPreferences, Model aDBModel, GraphModel aModel, GraphLayoutCache aLayoutCache) {
        super(aModel, aLayoutCache);
        model = aDBModel;
        preferences = aPreferences;

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

                    Relation theRelation = (Relation) theEdge.getUserObject();

                    getDBModel().removeRelation(theRelation);
                    theModel.remove(new Object[] { theEdge });

                    for (Map.Entry<IndexExpression, Attribute> theEntry : theRelation.getMapping().entrySet()) {
                        Attribute theImportingAttribute = theEntry.getValue();

                        if (!(theImportingAttribute.isForeignKey() || theImportingAttribute.isPrimaryKey())) {
                            // Only atributes not used in foreign keys or
                            // primary keys can
                            // be dropped, as other things might corrupt the
                            // database if the
                            // update script is run against a filled database
                            if (MessagesHelper.displayQuestionMessage(this, ERDesignerBundle.DELETENOTUSEDATTRIBUTES,
                                    theImportingAttribute.getName(), theImportingAttribute.getOwner().getName())) {
                                try {
                                    getDBModel().removeAttributeFromTable(theRelation.getImportingTable(),
                                            theImportingAttribute);
                                } catch (ElementInvalidNameException e) {
                                    throw new VetoException("Cannot recreate existing index", e);
                                } catch (ElementAlreadyExistsException e) {
                                    throw new VetoException("Cannot recreate existing index", e);
                                }
                            }
                        }
                    }
                }

                if (theSingleCell instanceof CommentCell) {
                    CommentCell theComment = (CommentCell) theSingleCell;

                    getDBModel().removeComment((Comment) theComment.getUserObject());
                    theModel.remove(new Object[] { theComment });
                }

                if (theSingleCell instanceof ViewCell) {
                    ViewCell theViewCell = (ViewCell) theSingleCell;

                    getDBModel().removeView((View) theViewCell.getUserObject());
                    theModel.remove(new Object[] { theViewCell });
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
     * Add a new table to the model.
     * 
     * @param aPoint
     *            the location
     */
    public abstract void commandNewTable(Point2D aPoint);

    /**
     * Add a new view to the model.
     * 
     * @param aPoint
     *            the location
     */
    public abstract void commandNewView(Point2D aPoint);

    /**
     * Create a new subject area for a set of cells.
     * 
     * @param aCells
     *            the cells to add to the subject area
     */
    public void commandAddToNewSubjectArea(List<ModelCell> aCells) {

        SubjectArea theArea = new SubjectArea();
        SubjectAreaCell theSubjectAreaCell = new SubjectAreaCell(theArea);
        for (ModelCell theCell : aCells) {
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
        getGraphLayoutCache().toBack(new Object[] { theSubjectAreaCell });
    }

    /**
     * @return the displayComments
     */
    public boolean isDisplayComments() {
        return displayComments;
    }

    /**
     * @param displayComments
     *            the displayComments to set
     */
    public void setDisplayComments(boolean displayComments) {
        this.displayComments = displayComments;
    }

    /**
     * Gibt den Wert des Attributs <code>physicalLayout</code> zur�ck.
     * 
     * @return Wert des Attributs physicalLayout.
     */
    public boolean isPhysicalLayout() {
        return physicalLayout;
    }

    /**
     * Setzt den Wert des Attributs <code>physicalLayout</code>.
     * 
     * @param physicalLayout
     *            Wert f�r das Attribut physicalLayout.
     */
    public void setPhysicalLayout(boolean physicalLayout) {
        this.physicalLayout = physicalLayout;
    }

    /**
     * Add a new comment to the model.
     * 
     * @param aLocation
     *            the location
     */
    public abstract void commandNewComment(Point2D aLocation);

    /**
     * Add a new relation to the model.
     * 
     * @param theSourceCell
     *            the source
     * @param theTargetCell
     *            the target
     */
    public void commandNewRelation(GraphCell theSourceCell, GraphCell theTargetCell) {

        Table theSourceTable = (Table) ((TableCell) theSourceCell).getUserObject();
        Table theTargetTable = (Table) ((TableCell) theTargetCell).getUserObject();

        Relation theRelation = new Relation();
        theRelation.setImportingTable(theSourceTable);
        theRelation.setExportingTable(theTargetTable);
        theRelation.setOnUpdate(preferences.getOnUpdateDefault());
        theRelation.setOnDelete(preferences.getOnDeleteDefault());

        String thePattern = preferences.getAutomaticRelationAttributePattern();
        String theTargetTableName = model.getDialect().getCastType().cast(theTargetTable.getName());

        // Create the foreign key suggestions
        Index thePrimaryKey = theTargetTable.getPrimarykey();
        for (IndexExpression theExpression : thePrimaryKey.getExpressions()) {
            Attribute theAttribute = theExpression.getAttributeRef();
            if (theAttribute != null) {
                String theNewname = MessageFormat.format(thePattern, theTargetTableName, theAttribute.getName());
                Attribute theNewAttribute = theSourceTable.getAttributes().findByName(theNewname);
                if (theNewAttribute == null) {
                    theNewAttribute = theAttribute.clone();
                    theNewAttribute.setSystemId(ModelUtilities.createSystemIdFor(theNewAttribute));
                    theNewAttribute.setOwner(null);
                    theNewAttribute.setName(theNewname);
                }
                theRelation.getMapping().put(theExpression, theNewAttribute);
            }
        }

        RelationEditor theEditor = new RelationEditor(theSourceTable.getOwner(), this);
        theEditor.initializeFor(theRelation);

        if (theEditor.showModal() == DialogConstants.MODAL_RESULT_OK) {

            RelationEdge theEdge = new RelationEdge(theRelation, (TableCell) theSourceCell, (TableCell) theTargetCell);

            try {
                theEditor.applyValues();
                getGraphLayoutCache().insert(theEdge);
            } catch (Exception e) {
                LOGGER.error("Error adding new relation", e);
            }
        }
    }

    /**
     * @return the displayLevel
     */
    public DisplayLevel getDisplayLevel() {
        return displayLevel;
    }

    /**
     * @param displayLevel
     *            the displayLevel to set
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
     * @param displayOrder
     *            the displayOrder to set
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
     * @param dragging
     *            the dragging to set
     */
    public void setDragging(boolean dragging) {
        this.dragging = dragging;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void repaint() {
        addOffscreenDirty(new Rectangle2D.Double(0, 0, getWidth(), getHeight()));
        super.repaint();
    }

    /**
     * Hide a list of specific cells.
     * 
     * @param aCellsToHide
     *            the cells to hide
     */
    public abstract void commandHideCells(List<HideableCell> aCellsToHide);
}
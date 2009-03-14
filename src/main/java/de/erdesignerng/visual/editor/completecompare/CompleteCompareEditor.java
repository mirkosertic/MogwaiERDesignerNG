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
package de.erdesignerng.visual.editor.completecompare;

import java.awt.Component;

import javax.swing.JScrollPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.AttributeList;
import de.erdesignerng.model.Index;
import de.erdesignerng.model.IndexList;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.Relation;
import de.erdesignerng.model.RelationList;
import de.erdesignerng.model.Table;
import de.erdesignerng.model.TableList;
import de.erdesignerng.model.View;
import de.erdesignerng.model.ViewList;
import de.erdesignerng.visual.editor.BaseEditor;
import de.mogwai.common.client.looks.UIInitializer;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-13 15:40:33 $
 */
public class CompleteCompareEditor extends BaseEditor {

    private CompleteCompareEditorView editingView;

    private Model currentModel;

    private Model databaseModel;

    public CompleteCompareEditor(Component aParent, Model aCurrentModel, Model aDatabaseModel) {
        super(aParent, ERDesignerBundle.COMPLETECOMPARE);

        initialize();

        currentModel = aCurrentModel;
        databaseModel = aDatabaseModel;

        TreeCellRenderer theRenderer = new CompareTreeCellRenderer();

        editingView.getCurrentModelView().setCellRenderer(theRenderer);
        editingView.getDatabaseView().setCellRenderer(theRenderer);
        editingView.getOkButton().setAction(okAction);

        refreshView();

        UIInitializer.getInstance().initialize(this);
    }

    private void refreshView() {

        DefaultMutableTreeNode theModelSideRootNode = new DefaultMutableTreeNode(getResourceHelper().getText(
                ERDesignerBundle.MODEL));
        DefaultMutableTreeNode theDBSideRootNode = new DefaultMutableTreeNode(getResourceHelper().getText(
                ERDesignerBundle.DATABASE));

        TableList theAllTables = new TableList();
        theAllTables.addAll(currentModel.getTables());

        for (Table theTable : databaseModel.getTables()) {
            if (theAllTables.findByName(theTable.getName()) == null) {
                theAllTables.add(theTable);
            }
        }

        for (Table theTable : theAllTables) {

            String theTableName = theTable.getName();

            DefaultMutableTreeNode theModelSideTableNode = null;
            DefaultMutableTreeNode theDBSideTableNode = null;

            Table theTableFromModel = currentModel.getTables().findByName(theTableName);
            Table theTableFromDB = null;

            // Add it to both sides
            if (theTableFromModel != null) {

                // Entitiy exists in model
                theModelSideTableNode = new DefaultMutableTreeNode(theTableName);
                theModelSideRootNode.add(theModelSideTableNode);

            } else {

                // Entity does not exist in model
                theModelSideTableNode = new DefaultMutableTreeNode(new MissingEntityInfo(this, theTableName));
                theModelSideRootNode.add(theModelSideTableNode);

            }

            if (databaseModel.getTables().findByName(theTableName) != null) {

                // Entity exists in db
                theDBSideTableNode = new DefaultMutableTreeNode(theTableName);
                theDBSideRootNode.add(theDBSideTableNode);

                theTableFromDB = databaseModel.getTables().findByName(theTableName);

            } else {

                // Entity does not exists in db
                theDBSideTableNode = new DefaultMutableTreeNode(new MissingEntityInfo(this, theTableName));
                theDBSideRootNode.add(theDBSideTableNode);

            }

            AttributeList theAllAttributes = new AttributeList();
            IndexList theAllIndexes = new IndexList();
            RelationList theAllRelations = new RelationList();

            if (theTableFromModel != null) {
                theAllAttributes.addAll(theTableFromModel.getAttributes());
                theAllIndexes.addAll(theTableFromModel.getIndexes());
                theAllRelations.addAll(currentModel.getRelations().getForeignKeysFor(theTableFromModel));
            }

            if (theTableFromDB != null) {
                for (Attribute theAttribute : theTableFromDB.getAttributes()) {
                    if (theAllAttributes.findByName(theAttribute.getName()) == null) {
                        theAllAttributes.add(theAttribute);
                    }
                }
                for (Index theIndex : theTableFromDB.getIndexes()) {
                    if (theAllIndexes.findByName(theIndex.getName()) == null) {
                        theAllIndexes.add(theIndex);
                    }
                }
                for (Relation theRelation : databaseModel.getRelations().getForeignKeysFor(theTableFromDB)) {
                    if (theAllRelations.findByName(theRelation.getName()) == null) {
                        theAllRelations.add(theRelation);
                    }
                }
            }

            generateAttributesForTable(theModelSideTableNode, theDBSideTableNode, theTableFromModel, theTableFromDB,
                    theAllAttributes);

            DefaultMutableTreeNode theIndexModelSideNode = new DefaultMutableTreeNode(getResourceHelper().getText(
                    ERDesignerBundle.INDEXES));
            DefaultMutableTreeNode theIndexDBSideNode = new DefaultMutableTreeNode(getResourceHelper().getText(
                    ERDesignerBundle.INDEXES));

            theModelSideTableNode.add(theIndexModelSideNode);
            theDBSideTableNode.add(theIndexDBSideNode);

            generateIndexesForTable(theIndexModelSideNode, theIndexDBSideNode, theTableFromModel, theTableFromDB,
                    theAllIndexes);

            DefaultMutableTreeNode theRelationsModelSideNode = new DefaultMutableTreeNode(getResourceHelper().getText(
                    ERDesignerBundle.RELATIONS));
            DefaultMutableTreeNode theRelationsDBSideNode = new DefaultMutableTreeNode(getResourceHelper().getText(
                    ERDesignerBundle.RELATIONS));

            theModelSideTableNode.add(theRelationsModelSideNode);
            theDBSideTableNode.add(theRelationsDBSideNode);

            generateRelationsForTable(theRelationsModelSideNode, theRelationsDBSideNode, theTableFromModel,
                    theTableFromDB, theAllRelations, databaseModel, currentModel);

        }
        
        ViewList theAllViews = new ViewList();
        theAllViews.addAll(currentModel.getViews());

        for (View theView : databaseModel.getViews()) {
            if (theAllViews.findByName(theView.getName()) == null) {
                theAllViews.add(theView);
            }
        }

        for (View theView : theAllViews) {

            String theViewName = theView.getName();

            DefaultMutableTreeNode theModelSideTableNode = null;
            DefaultMutableTreeNode theDBSideTableNode = null;

            View theViewFromModel = currentModel.getViews().findByName(theViewName);
            View theViewFromDB = databaseModel.getViews().findByName(theViewName);

            // Add it to both sides
            if (theViewFromModel != null) {
                
                if (theViewFromModel.isModified(theView)) {
                    // View was redifined
                    theModelSideTableNode = new DefaultMutableTreeNode(new RedefinedViewInfo(this, theViewName));
                    theModelSideRootNode.add(theModelSideTableNode);
                } else {
                    // View exists in model
                    theModelSideTableNode = new DefaultMutableTreeNode(theViewName);
                    theModelSideRootNode.add(theModelSideTableNode);
                }

            } else {

                // Entity does not exist in model
                theModelSideTableNode = new DefaultMutableTreeNode(new MissingViewInfo(this, theViewName));
                theModelSideRootNode.add(theModelSideTableNode);

            }

            if (theViewFromDB != null) {
                
                if (theViewFromDB.isModified(theView)) {
                    // View was redifined
                    theModelSideTableNode = new DefaultMutableTreeNode(new RedefinedViewInfo(this, theViewName));
                    theModelSideRootNode.add(theModelSideTableNode);
                    
                } else {
                    // View exists in db
                    theDBSideTableNode = new DefaultMutableTreeNode(theViewName);
                    theDBSideRootNode.add(theDBSideTableNode);
                }

            } else {

                // Entity does not exists in db
                theDBSideTableNode = new DefaultMutableTreeNode(new MissingViewInfo(this, theViewName));
                theDBSideRootNode.add(theDBSideTableNode);

            }
        }

        editingView.getCurrentModelView().setModel(new DefaultTreeModel(theModelSideRootNode));

        editingView.getDatabaseView().setModel(new DefaultTreeModel(theDBSideRootNode));

        int theRow = 0;
        while (theRow < editingView.getCurrentModelView().getRowCount()) {
            editingView.getCurrentModelView().expandRow(theRow++);
        }

        theRow = 0;
        while (theRow < editingView.getDatabaseView().getRowCount()) {
            editingView.getDatabaseView().expandRow(theRow++);
        }
    }

    private void generateAttributesForTable(DefaultMutableTreeNode aModelSideTableNode,
            DefaultMutableTreeNode aDBSideTableNode, Table aTableFromModel, Table aTableFromDB,
            AttributeList aAllAttributes) {

        // Now, doit for each attribute
        for (Attribute theAttribute : aAllAttributes) {
            String theAttributeName = theAttribute.getName();

            // First, handle the model side
            if (aTableFromModel != null) {

                Attribute theAttributeFromModel = aTableFromModel.getAttributes().findByName(theAttributeName);

                if (theAttributeFromModel != null) {

                    Attribute theAttributeFromDB = null;
                    if (aTableFromDB != null) {
                        theAttributeFromDB = aTableFromDB.getAttributes().findByName(theAttributeName);
                    }

                    if (theAttributeFromDB != null) {

                        if (theAttributeFromModel.isModified(theAttributeFromDB, true)) {
                            // Compute the difference

                            String theDiffInfo = theAttributeFromModel.getPhysicalDeclaration();

                            // Differences in definition
                            DefaultMutableTreeNode error = new DefaultMutableTreeNode(new RedefinedAttributeInfo(this,
                                    theAttributeName + " " + theDiffInfo));
                            aModelSideTableNode.add(error);

                        } else {
                            DefaultMutableTreeNode existant = new DefaultMutableTreeNode(theAttributeName);
                            aModelSideTableNode.add(existant);
                        }

                        // Here, we have to compare the attributes

                    } else {

                        DefaultMutableTreeNode existant = new DefaultMutableTreeNode(theAttributeName);
                        aModelSideTableNode.add(existant);
                    }

                } else {

                    // The entity is existant, but the attribute is
                    // missing
                    DefaultMutableTreeNode missing = new DefaultMutableTreeNode(new MissingAttributeInfo(this,
                            theAttributeName));
                    aModelSideTableNode.add(missing);

                }

            } else {

                // The entity is not exising in the model, so every
                // attribute is missing
                DefaultMutableTreeNode missing = new DefaultMutableTreeNode(new MissingAttributeInfo(this,
                        theAttributeName));
                aModelSideTableNode.add(missing);

            }

            // Now, the database side
            if (aTableFromDB != null) {

                Attribute theAttributeFromDB = aTableFromDB.getAttributes().findByName(theAttributeName);

                if (theAttributeFromDB != null) {

                    Attribute theAttributeFromModel = null;
                    if (aTableFromModel != null) {
                        theAttributeFromModel = aTableFromModel.getAttributes().findByName(theAttributeName);
                    }

                    if (theAttributeFromModel != null) {

                        if (theAttributeFromModel.isModified(theAttributeFromDB, true)) {

                            String diffInfo = theAttributeFromDB.getPhysicalDeclaration();

                            // Modified
                            // Differences in definition
                            DefaultMutableTreeNode error = new DefaultMutableTreeNode(new RedefinedAttributeInfo(this,
                                    theAttributeName + " " + diffInfo));
                            aDBSideTableNode.add(error);

                        } else {
                            DefaultMutableTreeNode existant = new DefaultMutableTreeNode(theAttributeName);
                            aDBSideTableNode.add(existant);
                        }

                    } else {

                        DefaultMutableTreeNode existant = new DefaultMutableTreeNode(theAttributeName);
                        aDBSideTableNode.add(existant);
                    }

                } else {

                    // The entity is existant, but the attribute is
                    // missing
                    DefaultMutableTreeNode missing = new DefaultMutableTreeNode(new MissingAttributeInfo(this,
                            theAttributeName));
                    aDBSideTableNode.add(missing);

                }

            } else {

                // The entity is not exising in the model, so every
                // attribute is missing
                DefaultMutableTreeNode missing = new DefaultMutableTreeNode(new MissingAttributeInfo(this,
                        theAttributeName));
                aDBSideTableNode.add(missing);

            }
        }
    }

    private void generateIndexesForTable(DefaultMutableTreeNode aModelSideTableNode,
            DefaultMutableTreeNode aDBSideTableNode, Table aTableFromModel, Table aTableFromDB, IndexList aAllIndexes) {

        // Now, doit for each attribute
        for (Index theAttribute : aAllIndexes) {
            String theIndexName = theAttribute.getName();

            // First, handle the model side
            if (aTableFromModel != null) {

                Index theIndexFromModel = aTableFromModel.getIndexes().findByName(theIndexName);

                if (theIndexFromModel != null) {

                    Index theIndexFromDB = null;
                    if (aTableFromDB != null) {
                        theIndexFromDB = aTableFromDB.getIndexes().findByName(theIndexName);
                    }

                    if (theIndexFromDB != null) {

                        if (theIndexFromModel.isModified(theIndexFromDB, true)) {
                            // Compute the difference

                            String theDiffInfo = "";

                            // Differences in definition
                            DefaultMutableTreeNode error = new DefaultMutableTreeNode(new RedefinedIndexInfo(this,
                                    theIndexName + " " + theDiffInfo));
                            aModelSideTableNode.add(error);

                        } else {
                            DefaultMutableTreeNode existant = new DefaultMutableTreeNode(theIndexName);
                            aModelSideTableNode.add(existant);
                        }

                        // Here, we have to compare the attributes

                    } else {

                        DefaultMutableTreeNode existant = new DefaultMutableTreeNode(theIndexName);
                        aModelSideTableNode.add(existant);
                    }

                } else {

                    // The entity is existant, but the attribute is
                    // missing
                    DefaultMutableTreeNode missing = new DefaultMutableTreeNode(
                            new MissingIndexInfo(this, theIndexName));
                    aModelSideTableNode.add(missing);

                }

            } else {

                // The entity is not exising in the model, so every
                // attribute is missing
                DefaultMutableTreeNode missing = new DefaultMutableTreeNode(new MissingIndexInfo(this, theIndexName));
                aModelSideTableNode.add(missing);

            }

            // Now, the database side
            if (aTableFromDB != null) {

                Index theIndexFromDB = aTableFromDB.getIndexes().findByName(theIndexName);

                if (theIndexFromDB != null) {

                    Index theIndexFromModel = null;
                    if (aTableFromModel != null) {
                        theIndexFromModel = aTableFromModel.getIndexes().findByName(theIndexName);
                    }

                    if (theIndexFromModel != null) {

                        if (theIndexFromModel.isModified(theIndexFromDB, true)) {

                            String diffInfo = "";

                            // Modified
                            // Differences in definition
                            DefaultMutableTreeNode error = new DefaultMutableTreeNode(new RedefinedIndexInfo(this,
                                    theIndexName + " " + diffInfo));
                            aDBSideTableNode.add(error);

                        } else {
                            DefaultMutableTreeNode existant = new DefaultMutableTreeNode(theIndexName);
                            aDBSideTableNode.add(existant);
                        }

                    } else {

                        DefaultMutableTreeNode existant = new DefaultMutableTreeNode(theIndexName);
                        aDBSideTableNode.add(existant);
                    }

                } else {

                    // The entity is existant, but the attribute is
                    // missing
                    DefaultMutableTreeNode missing = new DefaultMutableTreeNode(
                            new MissingIndexInfo(this, theIndexName));
                    aDBSideTableNode.add(missing);

                }

            } else {

                // The entity is not exising in the model, so every
                // attribute is missing
                DefaultMutableTreeNode missing = new DefaultMutableTreeNode(new MissingIndexInfo(this, theIndexName));
                aDBSideTableNode.add(missing);

            }
        }
    }

    private void generateRelationsForTable(DefaultMutableTreeNode aModelSideTableNode,
            DefaultMutableTreeNode aDBSideTableNode, Table aTableFromModel, Table aTableFromDB,
            RelationList aAllRelations, Model aDBModel, Model aCurrentModel) {

        // Now, doit for each attribute
        for (Relation theRelation : aAllRelations) {
            String theRelationName = theRelation.getName();

            // First, handle the model side
            if (aTableFromModel != null) {

                Relation theRelationFromModel = aCurrentModel.getRelations().findByName(theRelationName);

                if (theRelationFromModel != null) {

                    Relation theRelationFromDB = null;
                    if (aTableFromDB != null) {
                        theRelationFromDB = aDBModel.getRelations().findByName(theRelationName);
                    }

                    if (theRelationFromDB != null) {

                        if (theRelationFromModel.isModified(theRelationFromDB, true)) {
                            // Compute the difference

                            String theDiffInfo = "";

                            // Differences in definition
                            DefaultMutableTreeNode error = new DefaultMutableTreeNode(new RedefinedIndexInfo(this,
                                    theRelationName + " " + theDiffInfo));
                            aModelSideTableNode.add(error);

                        } else {
                            DefaultMutableTreeNode existant = new DefaultMutableTreeNode(theRelationName);
                            aModelSideTableNode.add(existant);
                        }

                        // Here, we have to compare the attributes

                    } else {

                        DefaultMutableTreeNode existant = new DefaultMutableTreeNode(theRelationName);
                        aModelSideTableNode.add(existant);
                    }

                } else {

                    // The entity is existant, but the attribute is
                    // missing
                    DefaultMutableTreeNode missing = new DefaultMutableTreeNode(
                            new MissingIndexInfo(this, theRelationName));
                    aModelSideTableNode.add(missing);

                }

            } else {

                // The entity is not exising in the model, so every
                // attribute is missing
                DefaultMutableTreeNode missing = new DefaultMutableTreeNode(new MissingIndexInfo(this, theRelationName));
                aModelSideTableNode.add(missing);

            }

            // Now, the database side
            if (aTableFromDB != null) {

                Relation theRelationFromDB = aDBModel.getRelations().findByName(theRelationName);

                if (theRelationFromDB != null) {

                    Relation theRelationFromModel = null;
                    if (aTableFromModel != null) {
                        theRelationFromModel = aCurrentModel.getRelations().findByName(theRelationName);
                    }

                    if (theRelationFromModel != null) {

                        if (theRelationFromModel.isModified(theRelationFromDB, true)) {

                            String diffInfo = "";

                            // Modified
                            // Differences in definition
                            DefaultMutableTreeNode error = new DefaultMutableTreeNode(new RedefinedIndexInfo(this,
                                    theRelationName + " " + diffInfo));
                            aDBSideTableNode.add(error);

                        } else {
                            DefaultMutableTreeNode existant = new DefaultMutableTreeNode(theRelationName);
                            aDBSideTableNode.add(existant);
                        }

                    } else {

                        DefaultMutableTreeNode existant = new DefaultMutableTreeNode(theRelationName);
                        aDBSideTableNode.add(existant);
                    }

                } else {

                    // The entity is existant, but the attribute is
                    // missing
                    DefaultMutableTreeNode missing = new DefaultMutableTreeNode(
                            new MissingIndexInfo(this, theRelationName));
                    aDBSideTableNode.add(missing);

                }

            } else {

                // The entity is not exising in the model, so every
                // attribute is missing
                DefaultMutableTreeNode missing = new DefaultMutableTreeNode(new MissingIndexInfo(this, theRelationName));
                aDBSideTableNode.add(missing);

            }
        }
    }

    private void initialize() {

        editingView = new CompleteCompareEditorView();
        editingView.getOkButton().setAction(okAction);

        JScrollPane modelScroll = editingView.getCurrentModelView().getScrollPane();
        JScrollPane dbScroll = editingView.getDatabaseView().getScrollPane();

        modelScroll.getVerticalScrollBar().setModel(dbScroll.getVerticalScrollBar().getModel());
        modelScroll.getHorizontalScrollBar().setModel(dbScroll.getHorizontalScrollBar().getModel());

        setContentPane(editingView);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void applyValues() throws Exception {
    }
}

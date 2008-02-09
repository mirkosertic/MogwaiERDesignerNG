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
import java.awt.event.ActionEvent;

import javax.swing.JScrollPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.AttributeList;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.Table;
import de.erdesignerng.model.TableList;
import de.erdesignerng.visual.editor.BaseEditor;
import de.mogwai.common.client.looks.UIInitializer;
import de.mogwai.common.client.looks.components.action.ActionEventProcessor;
import de.mogwai.common.client.looks.components.action.DefaultAction;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-02-09 14:57:35 $
 */
public class CompleteCompareEditor extends BaseEditor {

    private DefaultAction okAction = new DefaultAction(new ActionEventProcessor() {

        public void processActionEvent(ActionEvent e) {
            commandClose();
        }
    }, this, ERDesignerBundle.OK);

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
            if (theTableFromModel != null) {
                theAllAttributes.addAll(theTableFromModel.getAttributes());
            }

            if (theTableFromDB != null) {
                for (Attribute theAttribute : theTableFromDB.getAttributes()) {
                    if (theAllAttributes.findByName(theAttribute.getName()) == null) {
                        theAllAttributes.add(theAttribute);
                    }
                }
            }

            // Now, doit for each attribute
            for (Attribute theAttribute : theAllAttributes) {
                String theAttributeName = theAttribute.getName();

                // First, handle the model side
                if (theTableFromModel != null) {

                    Attribute theAttributeFromModel = theTableFromModel.getAttributes().findByName(theAttributeName);

                    if (theAttributeFromModel != null) {

                        Attribute theAttributeFromDB = null;
                        if (theTableFromDB != null) {
                            theAttributeFromDB = theTableFromDB.getAttributes().findByName(theAttributeName);
                        }

                        if (theAttributeFromDB != null) {

                            if (theAttributeFromModel.isModified(theAttributeFromDB)) {
                                // Compute the difference

                                String theDiffInfo = theAttributeFromModel.getPhysicalDeclaration();

                                // Differences in definition
                                DefaultMutableTreeNode error = new DefaultMutableTreeNode(new RedefinedAttributeInfo(
                                        this, theAttributeName + " " + theDiffInfo));
                                theModelSideTableNode.add(error);

                            } else {
                                DefaultMutableTreeNode existant = new DefaultMutableTreeNode(theAttributeName);
                                theModelSideTableNode.add(existant);
                            }

                            // Here, we have to compare the attributes

                        } else {

                            DefaultMutableTreeNode existant = new DefaultMutableTreeNode(theAttributeName);
                            theModelSideTableNode.add(existant);
                        }

                    } else {

                        // The entity is existant, but the attribute is
                        // missing
                        DefaultMutableTreeNode missing = new DefaultMutableTreeNode(new MissingAttributeInfo(this,
                                theAttributeName));
                        theModelSideTableNode.add(missing);

                    }

                } else {

                    // The entity is not exising in the model, so every
                    // attribute is missing
                    DefaultMutableTreeNode missing = new DefaultMutableTreeNode(new MissingAttributeInfo(this,
                            theAttributeName));
                    theModelSideTableNode.add(missing);

                }

                // Now, the database side
                if (theTableFromDB != null) {

                    Attribute theAttributeFromDB = theTableFromDB.getAttributes().findByName(theAttributeName);

                    if (theAttributeFromDB != null) {

                        Attribute theAttributeFromModel = null;
                        if (theTableFromModel != null) {
                            theAttributeFromModel = theTableFromModel.getAttributes().findByName(theAttributeName);
                        }

                        if (theAttributeFromModel != null) {

                            if (theAttributeFromDB.isModified(theAttributeFromModel)) {

                                String diffInfo = theAttributeFromDB.getPhysicalDeclaration();

                                // Modified
                                // Differences in definition
                                DefaultMutableTreeNode error = new DefaultMutableTreeNode(new RedefinedAttributeInfo(
                                        this, theAttributeName + " " + diffInfo));
                                theDBSideTableNode.add(error);

                            } else {
                                DefaultMutableTreeNode existant = new DefaultMutableTreeNode(theAttributeName);
                                theDBSideTableNode.add(existant);
                            }

                        } else {

                            DefaultMutableTreeNode existant = new DefaultMutableTreeNode(theAttributeName);
                            theDBSideTableNode.add(existant);
                        }

                    } else {

                        // The entity is existant, but the attribute is
                        // missing
                        DefaultMutableTreeNode missing = new DefaultMutableTreeNode(new MissingAttributeInfo(this,
                                theAttributeName));
                        theDBSideTableNode.add(missing);

                    }

                } else {

                    // The entity is not exising in the model, so every
                    // attribute is missing
                    DefaultMutableTreeNode missing = new DefaultMutableTreeNode(new MissingAttributeInfo(this,
                            theAttributeName));
                    theDBSideTableNode.add(missing);

                }
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

    private void commandClose() {
        setModalResult(MODAL_RESULT_OK);
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

    @Override
    public void applyValues() throws Exception {
    }
}

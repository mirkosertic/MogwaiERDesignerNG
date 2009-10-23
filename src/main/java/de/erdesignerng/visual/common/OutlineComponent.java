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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;

import org.apache.commons.beanutils.BeanComparator;

import com.lowagie.text.Font;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.Index;
import de.erdesignerng.model.IndexExpression;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.ModelItem;
import de.erdesignerng.model.Relation;
import de.erdesignerng.model.Table;
import de.erdesignerng.model.View;
import de.erdesignerng.visual.IconFactory;
import de.mogwai.common.client.looks.components.DefaultLabel;
import de.mogwai.common.client.looks.components.DefaultTree;
import de.mogwai.common.i18n.ResourceHelper;
import de.mogwai.common.i18n.ResourceHelperProvider;

public class OutlineComponent extends JPanel implements ResourceHelperProvider {

    private DefaultTree tree;

    public OutlineComponent() {
        initialize();

        setPreferredSize(new Dimension(350, 100));
    }

    private void initialize() {

        tree = new DefaultTree();
        tree.setCellRenderer(new TreeCellRenderer() {

            private DefaultLabel theLabel = new DefaultLabel();

            @Override
            public Component getTreeCellRendererComponent(JTree aTree, Object aValue, boolean aSelected,
                    boolean aExpanded, boolean aLeaf, int aRow, boolean hasFocus) {
                theLabel.setColon(false);
                theLabel.setText("");
                theLabel.setOpaque(true);
                theLabel.setIcon(null);
                theLabel.setFont(theLabel.getFont().deriveFont(Font.BOLD));
                if (aSelected) {
                    theLabel.setBackground(new Color(236, 233, 216));
                    theLabel.setForeground(Color.black);
                    theLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                } else {
                    theLabel.setBackground(Color.white);
                    theLabel.setForeground(Color.black);
                    theLabel.setBorder(BorderFactory.createLineBorder(Color.white));
                }
                if (aValue != null) {
                    DefaultMutableTreeNode theNode = (DefaultMutableTreeNode) aValue;
                    Object theUserObject = theNode.getUserObject();

                    theLabel.setFont(theLabel.getFont().deriveFont(Font.NORMAL));

                    if (theUserObject instanceof Table) {
                        theLabel.setIcon(IconFactory.getEntityIcon());
                        theLabel.setFont(theLabel.getFont().deriveFont(Font.BOLD));
                    }
                    if (theUserObject instanceof View) {
                        theLabel.setIcon(IconFactory.getViewIcon());
                        theLabel.setFont(theLabel.getFont().deriveFont(Font.BOLD));
                    }
                    if (theUserObject instanceof Relation) {
                        theLabel.setIcon(IconFactory.getRelationIcon());
                    }
                    if (theUserObject instanceof Attribute) {
                        theLabel.setIcon(IconFactory.getAttributeIcon());
                    }
                    if (theUserObject instanceof Index) {
                        theLabel.setIcon(IconFactory.getIndexIcon());
                    }
                    if (theUserObject instanceof IndexExpression) {
                        IndexExpression theExpression = (IndexExpression) theUserObject;
                        if (theExpression.getAttributeRef() != null) {
                            theLabel.setIcon(IconFactory.getAttributeIcon());
                        } else {
                            theLabel.setIcon(IconFactory.getExpressionIcon());
                        }

                    }

                    theLabel.setText(aValue.toString());
                }
                return theLabel;
            }

        });

        setLayout(new BorderLayout());
        add(tree.getScrollPane(), BorderLayout.CENTER);
    }

    public void setModel(Model aModel) {

        DefaultMutableTreeNode theRoot = new DefaultMutableTreeNode(getResourceHelper().getFormattedText(ERDesignerBundle.MODEL));

        DefaultTreeModel theModel = new DefaultTreeModel(theRoot);

        List<ModelItem> theItems = new ArrayList<ModelItem>();
        theItems.addAll(aModel.getTables());
        theItems.addAll(aModel.getViews());
        Collections.sort(theItems, new BeanComparator("name"));

        for (ModelItem theItem : theItems) {

            if (theItem instanceof Table) {
                Table theTable = (Table) theItem;
                DefaultMutableTreeNode theTableNode = new DefaultMutableTreeNode(theTable);
                theRoot.add(theTableNode);

                for (Attribute theAttribute : theTable.getAttributes()) {
                    DefaultMutableTreeNode theAttribtueNode = new DefaultMutableTreeNode(theAttribute);
                    theTableNode.add(theAttribtueNode);
                }

                for (Index theIndex : theTable.getIndexes()) {
                    DefaultMutableTreeNode theIndexNode = new DefaultMutableTreeNode(theIndex);
                    for (IndexExpression theExpression : theIndex.getExpressions()) {
                        DefaultMutableTreeNode theExpressionNode = new DefaultMutableTreeNode(theExpression);
                        theIndexNode.add(theExpressionNode);
                    }
                    theTableNode.add(theIndexNode);
                }

                for (Relation theRelation : aModel.getRelations().getForeignKeysFor(theTable)) {
                    DefaultMutableTreeNode theRelationNode = new DefaultMutableTreeNode(theRelation);
                    for (Map.Entry<IndexExpression, Attribute> theEntry : theRelation.getMapping().entrySet()) {
                        DefaultMutableTreeNode theAttributeNode = new DefaultMutableTreeNode(theEntry.getValue());
                        theRelationNode.add(theAttributeNode);
                    }

                    theTableNode.add(theRelationNode);
                }
            }

            if (theItem instanceof View) {
                View theView = (View) theItem;
                DefaultMutableTreeNode theViewNode = new DefaultMutableTreeNode(theView);
                theRoot.add(theViewNode);
            }
        }

        tree.setModel(theModel);

        for (int row = 0; row < tree.getRowCount(); row++) {
            tree.expandRow(row);
        }
    }

    @Override
    public ResourceHelper getResourceHelper() {
        return ResourceHelper.getResourceHelper(ERDesignerBundle.BUNDLE_NAME);
    }
}
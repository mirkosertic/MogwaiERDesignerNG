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
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.lang.StringUtils;
import org.jgraph.graph.DefaultGraphCell;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.Domain;
import de.erdesignerng.model.Index;
import de.erdesignerng.model.IndexExpression;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.ModelItem;
import de.erdesignerng.model.OwnedModelItem;
import de.erdesignerng.model.Relation;
import de.erdesignerng.model.SubjectArea;
import de.erdesignerng.model.Table;
import de.erdesignerng.model.View;
import de.erdesignerng.visual.IconFactory;
import de.mogwai.common.client.looks.UIInitializer;
import de.mogwai.common.client.looks.components.DefaultButton;
import de.mogwai.common.client.looks.components.DefaultLabel;
import de.mogwai.common.client.looks.components.DefaultPanel;
import de.mogwai.common.client.looks.components.DefaultTextField;
import de.mogwai.common.client.looks.components.DefaultTree;
import de.mogwai.common.i18n.ResourceHelper;
import de.mogwai.common.i18n.ResourceHelperProvider;

public class OutlineComponent extends DefaultPanel implements ResourceHelperProvider {

    private final class OutlineSelectionListener implements TreeSelectionListener {
        @Override
        public void valueChanged(TreeSelectionEvent aEvent) {
            TreePath thePath = aEvent.getNewLeadSelectionPath();
            if (thePath != null) {
                DefaultMutableTreeNode theNode = (DefaultMutableTreeNode) thePath.getLastPathComponent();
                if (theNode != null) {
                    Object theUserObject = theNode.getUserObject();
                    if (theUserObject instanceof ModelItem) {
                        SQLComponent.getDefault().displaySQLFor(new ModelItem[] { (ModelItem) theUserObject });
                        ERDesignerComponent.getDefault().setSelectedObject((ModelItem) theUserObject);
                    } else {
                        SQLComponent.getDefault().resetDisplay();
                    }
                }
            }
        }
    }

    private final class OutlineDisableFilterActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            filterField.setText("");
            refresh(ERDesignerComponent.getDefault().getModel(), null);
        }
    }

    private final class OutlineKeyAdapter extends KeyAdapter {

        private final Timer timer = new Timer();

        private TimerTask oldTask;

        @Override
        public void keyTyped(KeyEvent aEvent) {
            if (oldTask != null) {
                oldTask.cancel();
            }
            oldTask = new TimerTask() {

                @Override
                public void run() {
                    try {
                        SwingUtilities.invokeAndWait(new Runnable() {

                            @Override
                            public void run() {
                                refresh(ERDesignerComponent.getDefault().getModel(), null);
                            }
                        });
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            };
            timer.schedule(oldTask, 500);
        }
    }

    private final class OutlineTreeExpansionListener implements TreeExpansionListener {
        @Override
        public void treeCollapsed(TreeExpansionEvent aEvent) {
            DefaultMutableTreeNode theNode = (DefaultMutableTreeNode) aEvent.getPath().getLastPathComponent();
            expandedUserObjects.remove(theNode.getUserObject());
        }

        @Override
        public void treeExpanded(TreeExpansionEvent aEvent) {
            DefaultMutableTreeNode theNode = (DefaultMutableTreeNode) aEvent.getPath().getLastPathComponent();
            expandedUserObjects.add(theNode.getUserObject());
        }
    }

    private final class OutlineMouseListener extends MouseAdapter {
        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.isPopupTrigger()) {
                TreePath thePath = tree.getClosestPathForLocation(e.getX(), e.getY());
                if (thePath != null) {
                    DefaultMutableTreeNode theNode = (DefaultMutableTreeNode) thePath.getLastPathComponent();
                    if (theNode != null) {

                        tree.setSelectionPath(thePath);

                        JPopupMenu theMenu = new JPopupMenu();

                        initializeActionsFor(theNode, theMenu, false);

                        UIInitializer.getInstance().initialize(theMenu);
                        theMenu.show(tree, e.getX(), e.getY());
                    }
                }
            }
        }
    }

    private final class OutlineTreeCellRenderer implements TreeCellRenderer {
        private final DefaultLabel theLabel = new DefaultLabel();

        @Override
        public Component getTreeCellRendererComponent(JTree aTree, Object aValue, boolean aSelected, boolean aExpanded,
                boolean aLeaf, int aRow, boolean hasFocus) {
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

                theLabel.setFont(theLabel.getFont().deriveFont(Font.PLAIN));

                if (theUserObject instanceof Table) {
                    theLabel.setIcon(IconFactory.getEntityIcon());
                    theLabel.setFont(theLabel.getFont().deriveFont(Font.BOLD));
                }
                if (theUserObject instanceof View) {
                    theLabel.setIcon(IconFactory.getViewIcon());
                    theLabel.setFont(theLabel.getFont().deriveFont(Font.BOLD));
                }
                if (theUserObject instanceof Domain) {
                    theLabel.setFont(theLabel.getFont().deriveFont(Font.BOLD));
                }
                if (theUserObject instanceof SubjectArea) {
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

                if (theUserObject instanceof TreeGroupingElement) {
                    switch ((TreeGroupingElement) theUserObject) {
                    case MODEL:
                        theLabel.setText(getResourceHelper().getFormattedText(ERDesignerBundle.MODEL));
                        break;
                    case DOMAINS:
                        theLabel.setText(getResourceHelper().getFormattedText(ERDesignerBundle.DOMAINSLIST));
                        break;
                    case TABLES:
                        theLabel.setText(getResourceHelper().getFormattedText(ERDesignerBundle.TABLES));
                        break;
                    case VIEWS:
                        theLabel.setText(getResourceHelper().getFormattedText(ERDesignerBundle.VIEWS));
                        break;
                    case SUBJECTAREAS:
                        theLabel.setText(getResourceHelper().getFormattedText(ERDesignerBundle.SUBJECTAREALIST));
                        break;
                    case RELATIONS:
                        theLabel.setText(getResourceHelper().getFormattedText(ERDesignerBundle.RELATIONS));
                        break;
                    case INDEXES:
                        theLabel.setText(getResourceHelper().getFormattedText(ERDesignerBundle.INDEXES));
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown grouping element : " + aValue);
                    }
                } else {
                    theLabel.setText(aValue.toString());
                }
            }
            return theLabel;
        }
    }

    private enum TreeGroupingElement {
        MODEL, DOMAINS, TABLES, VIEWS, SUBJECTAREAS, INDEXES, RELATIONS;
    }

    private DefaultTree tree;

    private DefaultTextField filterField;

    private DefaultButton disableFilterButton;

    private final Map<Object, DefaultMutableTreeNode> userObjectMap = new HashMap<Object, DefaultMutableTreeNode>();

    private final Set<Object> expandedUserObjects = new HashSet<Object>();

    private static OutlineComponent DEFAULT;

    private OutlineComponent() {

        initialize();

        setPreferredSize(new Dimension(350, 100));
    }

    public static OutlineComponent initializeComponent() {
        if (DEFAULT == null) {
            DEFAULT = new OutlineComponent();
        }
        return DEFAULT;
    }

    public static OutlineComponent getDefault() {
        initializeComponent();
        return DEFAULT;
    }

    /**
     * Register a combination of an object and a treenode.
     * 
     * An object can only be registered once!
     * 
     * @param aObject
     * @param aNode
     */
    private void registerUserObject(Object aObject, DefaultMutableTreeNode aNode) {
        if (!userObjectMap.containsKey(aObject)) {
            userObjectMap.put(aObject, aNode);
        }
    }

    private void initialize() {

        tree = new DefaultTree();
        tree.setCellRenderer(new OutlineTreeCellRenderer());
        tree.addMouseListener(new OutlineMouseListener());
        tree.addTreeSelectionListener(new OutlineSelectionListener());
        tree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode(TreeGroupingElement.MODEL)));

        setLayout(new BorderLayout());
        add(tree.getScrollPane(), BorderLayout.CENTER);

        filterField = new DefaultTextField();
        filterField.setColumns(255);
        filterField.addKeyListener(new OutlineKeyAdapter());
        disableFilterButton = new DefaultButton();
        disableFilterButton.setIcon(IconFactory.getCancelIcon());
        disableFilterButton.setMaximumSize(new Dimension(21, 21));
        disableFilterButton.setMinimumSize(new Dimension(21, 21));
        disableFilterButton.setPreferredSize(new Dimension(21, 21));
        disableFilterButton.addActionListener(new OutlineDisableFilterActionListener());

        JPanel theFilterPanel = new JPanel();
        theFilterPanel.setLayout(new BorderLayout());
        theFilterPanel.add(filterField, BorderLayout.CENTER);
        theFilterPanel.add(disableFilterButton, BorderLayout.EAST);

        add(theFilterPanel, BorderLayout.NORTH);

        tree.addTreeExpansionListener(new OutlineTreeExpansionListener());
    }

    private boolean isVisible(ModelItem aItem) {
        String thePartialString = filterField.getText().toLowerCase();
        if (!StringUtils.isEmpty(thePartialString)) {

            boolean theOverride = false;

            if (aItem instanceof Table) {
                Table theTable = (Table) aItem;
                for (Attribute theAttribute : theTable.getAttributes()) {
                    if (isVisible(theAttribute)) {
                        theOverride = true;
                    }
                }
                for (Index theIndex : theTable.getIndexes()) {
                    if (isVisible(theIndex)) {
                        theOverride = true;
                    }
                    for (IndexExpression theExpression : theIndex.getExpressions()) {
                        if (isVisible(theExpression)) {
                            theOverride = true;
                        }
                    }
                }

                for (Relation theRelation : ERDesignerComponent.getDefault().getModel().getRelations()
                        .getForeignKeysFor(theTable)) {
                    if (isVisible(theRelation)) {
                        theOverride = true;
                    }
                }
            }

            if (aItem instanceof Relation) {
                Relation theRelation = (Relation) aItem;
                for (Attribute theAttribute : theRelation.getMapping().values()) {
                    if (isVisible(theAttribute)) {
                        theOverride = true;
                    }
                }
            }

            if (aItem instanceof SubjectArea) {
                SubjectArea theArea = (SubjectArea) aItem;
                for (Table theTable : theArea.getTables()) {
                    if (isVisible(theTable)) {
                        theOverride = true;
                    }
                }
                for (View theView : theArea.getViews()) {
                    if (isVisible(theView)) {
                        theOverride = true;
                    }
                }
            }

            String theName = aItem.toString();

            if (!StringUtils.isEmpty(theName)) {
                return theName.toLowerCase().contains(thePartialString) || theOverride;
            }
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    public void setModel(Model aModel, boolean aExpandAll) {

        userObjectMap.clear();
        expandedUserObjects.clear();

        DefaultMutableTreeNode theRoot = new DefaultMutableTreeNode(TreeGroupingElement.MODEL);

        Comparator<OwnedModelItem> theComparator = new BeanComparator("name");

        // Add the domains
        List<Domain> theDomains = new ArrayList<Domain>();
        theDomains.addAll(aModel.getDomains());
        Collections.sort(theDomains, theComparator);
        buildDomainsChilds(aModel, theRoot, theDomains);

        // Add the Tables
        List<Table> theTables = new ArrayList<Table>();
        theTables.addAll(aModel.getTables());
        Collections.sort(theTables, theComparator);
        buildTablesChilds(aModel, theRoot, theTables);

        // Add the Views
        List<View> theViews = new ArrayList<View>();
        theViews.addAll(aModel.getViews());
        Collections.sort(theTables, theComparator);
        buildViewsChilds(aModel, theRoot, theViews);

        // Add the Relations
        List<Relation> theRelations = new ArrayList<Relation>();
        theRelations.addAll(aModel.getRelations());
        Collections.sort(theRelations, theComparator);
        buildRelationChilds(theRoot, theRelations);

        // Add the Indexes
        List<Index> theIndexes = new ArrayList<Index>();
        for (Table theTable : aModel.getTables()) {
            theIndexes.addAll(theTable.getIndexes());
        }
        Collections.sort(theIndexes, theComparator);
        buildIndexChilds(theRoot, theIndexes);

        // Add the subject areas
        List<SubjectArea> theSAList = new ArrayList<SubjectArea>();
        theSAList.addAll(aModel.getSubjectAreas());
        Collections.sort(theSAList, theComparator);
        buildSubjectAreasChilds(aModel, theRoot, theSAList);

        tree.setModel(new DefaultTreeModel(theRoot));

        // if (aExpandAll) {
        for (int row = 0; row < tree.getRowCount(); row++) {
            TreePath thePath = tree.getPathForRow(row);
            DefaultMutableTreeNode theNode = (DefaultMutableTreeNode) thePath.getLastPathComponent();
            Object theUserObject = theNode.getUserObject();
            if (theUserObject instanceof TreeGroupingElement) {
                tree.expandRow(row);
            }
        }
        // }
    }

    @SuppressWarnings("unchecked")
    private void buildSubjectAreasChilds(Model aModel, DefaultMutableTreeNode aParent, List<SubjectArea> aList) {
        DefaultMutableTreeNode theSANode = new DefaultMutableTreeNode(TreeGroupingElement.SUBJECTAREAS);
        for (SubjectArea theArea : aList) {
            if (isVisible(theArea)) {
                DefaultMutableTreeNode theAreaNode = new DefaultMutableTreeNode(theArea);
                theSANode.add(theAreaNode);

                registerUserObject(theArea, theAreaNode);

                List<Table> theSATables = new ArrayList<Table>();
                theSATables.addAll(theArea.getTables());
                Collections.sort(theSATables, new BeanComparator("name"));
                buildTablesChilds(aModel, theAreaNode, theSATables);

                List<View> theSAViews = new ArrayList<View>();
                theSAViews.addAll(aModel.getViews());
                Collections.sort(theSAViews, new BeanComparator("name"));
                buildViewsChilds(aModel, theAreaNode, theSAViews);

            }
        }
        aParent.add(theSANode);
    }

    private void buildViewsChilds(Model aModel, DefaultMutableTreeNode aParent, List<View> aViews) {
        DefaultMutableTreeNode theViewsNode = new DefaultMutableTreeNode(TreeGroupingElement.VIEWS);
        for (View theView : aViews) {
            if (isVisible(theView)) {
                DefaultMutableTreeNode theViewNode = new DefaultMutableTreeNode(theView);
                theViewsNode.add(theViewNode);

                registerUserObject(theView, theViewNode);

                updateViewTreeNode(aModel, theView, theViewNode);
            }
        }
        aParent.add(theViewsNode);
    }

    private void buildDomainsChilds(Model aModel, DefaultMutableTreeNode aParent, List<Domain> aDomainList) {
        DefaultMutableTreeNode theDomainsNode = new DefaultMutableTreeNode(TreeGroupingElement.DOMAINS);
        for (Domain theDomain : aDomainList) {
            if (isVisible(theDomain)) {
                DefaultMutableTreeNode theDomainNode = new DefaultMutableTreeNode(theDomain);
                theDomainsNode.add(theDomainNode);

                registerUserObject(theDomain, theDomainNode);

                updateDomainTreeNode(aModel, theDomain, theDomainNode);
            }
        }
        aParent.add(theDomainsNode);
    }

    private void buildTablesChilds(Model aModel, DefaultMutableTreeNode aParentNode, List<Table> aTableList) {
        DefaultMutableTreeNode theTablesNode = new DefaultMutableTreeNode(TreeGroupingElement.TABLES);
        for (Table theTable : aTableList) {
            if (isVisible(theTable)) {
                DefaultMutableTreeNode theTableNode = new DefaultMutableTreeNode(theTable);
                theTablesNode.add(theTableNode);

                registerUserObject(theTable, theTableNode);

                updateTableTreeNode(aModel, theTable, theTableNode);
            }
        }
        aParentNode.add(theTablesNode);
    }

    private void buildRelationChilds(DefaultMutableTreeNode aParentNode, List<Relation> aRelationList) {
        DefaultMutableTreeNode theRelationssNode = new DefaultMutableTreeNode(TreeGroupingElement.RELATIONS);
        for (Relation theRelation : aRelationList) {
            if (isVisible(theRelation)) {
                createRelationTreeNode(theRelationssNode, theRelation);
            }
        }
        aParentNode.add(theRelationssNode);
    }

    private void buildIndexChilds(DefaultMutableTreeNode aParentNode, List<Index> aIndexList) {
        DefaultMutableTreeNode theIndexesNode = new DefaultMutableTreeNode(TreeGroupingElement.INDEXES);
        for (Index theIndex : aIndexList) {
            if (isVisible(theIndex)) {
                createIndexTreeNode(theIndexesNode, theIndex);
            }
        }
        aParentNode.add(theIndexesNode);
    }

    private void createIndexTreeNode(DefaultMutableTreeNode aParentNode, Index aIndex) {
        DefaultMutableTreeNode theIndexNode = new DefaultMutableTreeNode(aIndex);
        aParentNode.add(theIndexNode);
        registerUserObject(aIndex, theIndexNode);

        for (IndexExpression theExpression : aIndex.getExpressions()) {
            if (isVisible(theExpression)) {
                DefaultMutableTreeNode theExpressionNode = new DefaultMutableTreeNode(theExpression);
                theIndexNode.add(theExpressionNode);

                registerUserObject(theExpression, theExpressionNode);
            }
        }
    }

    private void createRelationTreeNode(DefaultMutableTreeNode aParent, Relation aRelation) {
        DefaultMutableTreeNode theRelationNode = new DefaultMutableTreeNode(aRelation);
        aParent.add(theRelationNode);

        registerUserObject(aRelation, theRelationNode);

        for (Map.Entry<IndexExpression, Attribute> theEntry : aRelation.getMapping().entrySet()) {
            if (isVisible(theEntry.getValue())) {
                DefaultMutableTreeNode theAttributeNode = new DefaultMutableTreeNode(theEntry.getValue());
                theRelationNode.add(theAttributeNode);

                registerUserObject(theEntry.getKey(), theAttributeNode);
            }
        }
    }

    private void updateDomainTreeNode(Model aModel, Domain aDomain, DefaultMutableTreeNode aDomainNode) {

        aDomainNode.removeAllChildren();
    }

    private void updateViewTreeNode(Model aModel, View aView, DefaultMutableTreeNode aViewNode) {

        aViewNode.removeAllChildren();
    }

    private void updateTableTreeNode(Model aModel, Table aTable, DefaultMutableTreeNode aTableNode) {

        aTableNode.removeAllChildren();

        for (Attribute theAttribute : aTable.getAttributes()) {
            if (isVisible(theAttribute)) {
                DefaultMutableTreeNode theAttribtueNode = new DefaultMutableTreeNode(theAttribute);
                aTableNode.add(theAttribtueNode);

                registerUserObject(theAttribute, theAttribtueNode);
            }
        }

        for (Index theIndex : aTable.getIndexes()) {

            if (isVisible(theIndex)) {
                createIndexTreeNode(aTableNode, theIndex);
            }
        }

        for (Relation theRelation : aModel.getRelations().getForeignKeysFor(aTable)) {
            if (isVisible(theRelation)) {
                createRelationTreeNode(aTableNode, theRelation);
            }
        }
    }

    @Override
    public ResourceHelper getResourceHelper() {
        return ResourceHelper.getResourceHelper(ERDesignerBundle.BUNDLE_NAME);
    }

    /**
     * Locate a specific object in the tree.
     * 
     * @param aObject
     *            the object to locate
     */
    public void locateObject(Object aObject) {
        DefaultGraphCell theCell = (DefaultGraphCell) aObject;
        DefaultMutableTreeNode theNode = userObjectMap.get(theCell.getUserObject());
        if (theNode != null) {
            TreeNode[] theNodes = theNode.getPath();
            TreePath thePath = new TreePath(theNodes);
            tree.setSelectionPath(thePath);
            tree.scrollPathToVisible(thePath);

        }
    }

    /**
     * Refresh the model tree as there were changes.
     */
    public void refresh(Model aModel, Object aElement) {

        if (aModel != null) {

            TreePath theSelected = tree.getSelectionPath();

            DefaultMutableTreeNode theGroup = null;
            DefaultMutableTreeNode theSelectedNode = theSelected != null ? (DefaultMutableTreeNode) theSelected
                    .getLastPathComponent() : null;
            if (theSelected != null && theSelected.getPathCount() > 1) {
                theGroup = (DefaultMutableTreeNode) theSelected.getPath()[1];
            }

            Set<Object> theExpandedUserObjects = expandedUserObjects;

            setModel(aModel, false);

            List<TreePath> thePathsToExpand = new ArrayList<TreePath>();
            TreePath theNewSelection = null;

            for (int theRow = 0; theRow < tree.getRowCount(); theRow++) {
                TreePath thePath = tree.getPathForRow(theRow);

                DefaultMutableTreeNode theLastNew = (DefaultMutableTreeNode) thePath.getLastPathComponent();
                if (theExpandedUserObjects.contains(theLastNew.getUserObject())) {
                    thePathsToExpand.add(thePath);
                }
                if (theSelectedNode != null) {
                    DefaultMutableTreeNode theLastGroup = null;
                    if (thePath.getPathCount() > 1) {
                        theLastGroup = (DefaultMutableTreeNode) thePath.getPath()[1];
                    }
                    if (theLastGroup != null && theGroup != null) {
                        if (!theLastGroup.getUserObject().equals(theGroup.getUserObject())) {
                            continue;
                        }
                    }
                    if (theLastNew.getUserObject().equals(theSelectedNode.getUserObject())) {
                        theNewSelection = thePath;
                    }
                }
            }

            for (TreePath thePath : thePathsToExpand) {
                tree.expandPath(thePath);
            }

            if (theNewSelection != null) {
                tree.setSelectionPath(theNewSelection);
                tree.scrollPathToVisible(theNewSelection);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void expandOrCollapseAllChildsOfNode(TreePath aParentPath, boolean aExpand) {
        TreeNode node = (TreeNode) aParentPath.getLastPathComponent();
        if (node.getChildCount() > 0) {
            for (Enumeration<TreeNode> en = node.children(); en.hasMoreElements();) {
                TreeNode n = en.nextElement();
                TreePath path = aParentPath.pathByAddingChild(n);
                expandOrCollapseAllChildsOfNode(path, aExpand);
            }
        }
        if (aExpand) {
            tree.expandPath(aParentPath);
        } else {
            tree.collapsePath(aParentPath);
        }
    }

    /**
     * Create the PopupMenu actions correlating to a specific treenode.
     * 
     * @param aNode
     *            the node
     * @param aMenu
     *            the menu to add the actions to
     * @param aRecursive
     */
    private void initializeActionsFor(final DefaultMutableTreeNode aNode, JPopupMenu aMenu, boolean aRecursive) {

        final ERDesignerComponent theComponent = ERDesignerComponent.getDefault();

        Object theUserObject = aNode.getUserObject();

        if (!aRecursive) {
            JMenuItem theExpandAllItem = new JMenuItem();
            theExpandAllItem.setText(getResourceHelper().getFormattedText(ERDesignerBundle.EXPANDALL));
            theExpandAllItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    expandOrCollapseAllChildsOfNode(new TreePath(aNode.getPath()), true);
                }
            });
            aMenu.add(theExpandAllItem);

            JMenuItem theCollapseAllItem = new JMenuItem();
            theCollapseAllItem.setText(getResourceHelper().getFormattedText(ERDesignerBundle.COLLAPSEALL));
            theCollapseAllItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    expandOrCollapseAllChildsOfNode(new TreePath(aNode.getPath()), false);
                }
            });

            aMenu.add(theCollapseAllItem);
            aMenu.addSeparator();
        }

        if (theUserObject instanceof Table) {

            Table theTable = (Table) theUserObject;

            JMenuItem theEditItem = new JMenuItem();
            theEditItem.setText(getResourceHelper().getFormattedText(ERDesignerBundle.EDITTABLE, theTable.getName()));
            theEditItem.addActionListener(new EditTableCommand(theComponent, theTable));

            aMenu.add(theEditItem);
        }
        if (theUserObject instanceof View) {

            View theView = (View) theUserObject;

            JMenuItem theEditItem = new JMenuItem();
            theEditItem.setText(getResourceHelper().getFormattedText(ERDesignerBundle.EDITVIEW, theView.getName()));
            theEditItem.addActionListener(new EditViewCommand(theComponent, theView));

            aMenu.add(theEditItem);

        }
        if (theUserObject instanceof Relation) {

            Relation theRelation = (Relation) theUserObject;

            JMenuItem theEditItem = new JMenuItem();
            theEditItem.setText(getResourceHelper().getFormattedText(ERDesignerBundle.EDITRELATION,
                    theRelation.getName()));
            theEditItem.addActionListener(new EditRelationCommand(theComponent, theRelation));

            aMenu.add(theEditItem);

        }
        if (theUserObject instanceof Domain) {

            Domain theDomain = (Domain) theUserObject;

            JMenuItem theEditItem = new JMenuItem();
            theEditItem.setText(getResourceHelper().getFormattedText(ERDesignerBundle.EDITDOMAIN, theDomain.getName()));
            theEditItem.addActionListener(new EditDomainCommand(theComponent, theDomain));

            aMenu.add(theEditItem);

        }
        if (theUserObject instanceof SubjectArea) {

            SubjectArea theSubjectArea = (SubjectArea) theUserObject;
            JMenuItem theEditItem = new JMenuItem();
            theEditItem.setText(getResourceHelper().getFormattedText(ERDesignerBundle.EDITSUBJECTAREA,
                    theSubjectArea.getName()));
            theEditItem.addActionListener(new EditSubjectAreaCommand(theComponent, theSubjectArea));

            aMenu.add(theEditItem);

        }
        if (theUserObject instanceof Attribute) {

            Attribute theAttribute = (Attribute) theUserObject;

            JMenuItem theEditItem = new JMenuItem();
            theEditItem.setText(getResourceHelper().getFormattedText(ERDesignerBundle.EDITATTRIBUTE,
                    theAttribute.getName()));
            theEditItem.addActionListener(new EditTableCommand(theComponent, theAttribute.getOwner(), theAttribute));

            aMenu.add(theEditItem);
        }
        if (theUserObject instanceof Index) {

            Index theIndex = (Index) theUserObject;

            JMenuItem theEditItem = new JMenuItem();
            theEditItem.setText(getResourceHelper().getFormattedText(ERDesignerBundle.EDITINDEX, theIndex.getName()));
            theEditItem.addActionListener(new EditTableCommand(theComponent, theIndex.getOwner(), theIndex));

            aMenu.add(theEditItem);

        }
        if (aNode.getParent() != null) {
            initializeActionsFor((DefaultMutableTreeNode) aNode.getParent(), aMenu, true);
        }
    }

    /**
     * Set the currently selected item.
     * 
     * @param aSelection
     *            the selection
     */
    public void setSelectedItem(ModelItem aSelection) {

        TreePath theSelected = tree.getSelectionPath();
        if (theSelected != null) {
            DefaultMutableTreeNode theNode = (DefaultMutableTreeNode) theSelected.getLastPathComponent();
            if (theNode.getUserObject().equals(aSelection)) {
                // The object is already selected, so keep the selection
                return;
            }
        }

        DefaultMutableTreeNode theNode = userObjectMap.get(aSelection);
        if (theNode != null) {
            TreePath thePath = new TreePath(theNode.getPath());
            tree.setSelectionPath(thePath);
            tree.scrollPathToVisible(thePath);
        } else {
            tree.clearSelection();
        }
    }
}
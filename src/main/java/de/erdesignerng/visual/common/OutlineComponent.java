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

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.CustomType;
import de.erdesignerng.model.CustomTypeType;
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
import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.lang.StringUtils;

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
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class OutlineComponent extends DefaultPanel implements
		ResourceHelperProvider {

	private static class UsedBy {
		private Object ref;

		@Override
		public String toString() {
			return ref.toString();
		}
	}

	private static final class OutlineSelectionListener implements
			TreeSelectionListener {
		@Override
		public void valueChanged(final TreeSelectionEvent aEvent) {
			final TreePath thePath = aEvent.getNewLeadSelectionPath();
			if (thePath != null) {
				final DefaultMutableTreeNode theNode = (DefaultMutableTreeNode) thePath
						.getLastPathComponent();
				if (theNode != null) {
					Object theUserObject = theNode.getUserObject();
					if (theUserObject instanceof UsedBy) {
						theUserObject = ((UsedBy) theUserObject).ref;
					}
					if (theUserObject instanceof ModelItem) {
						SQLComponent.getDefault().displaySQLFor(
								new ModelItem[]{(ModelItem) theUserObject});
						ERDesignerComponent.getDefault().setSelectedObject(
								(ModelItem) theUserObject);
					} else {
						SQLComponent.getDefault().resetDisplay();
					}
				}
			}
		}
	}

	private final class OutlineDisableFilterActionListener implements
			ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			filterField.setText("");
			refresh(ERDesignerComponent.getDefault().getModel());
		}
	}

	private final class OutlineKeyAdapter extends KeyAdapter {

		private final Timer timer = new Timer();

		private TimerTask oldTask;

		@Override
		public void keyTyped(final KeyEvent aEvent) {
			if (oldTask != null) {
				oldTask.cancel();
			}
			oldTask = new TimerTask() {

				@Override
				public void run() {
					try {
						SwingUtilities.invokeAndWait(() -> refresh(ERDesignerComponent.getDefault()
                                .getModel()));
					} catch (final InterruptedException | InvocationTargetException e) {
						throw new RuntimeException(e);
					}
				}
			};
			timer.schedule(oldTask, 500);
		}
	}

	private final class OutlineTreeExpansionListener implements
			TreeExpansionListener {
		@Override
		public void treeCollapsed(final TreeExpansionEvent aEvent) {
			final DefaultMutableTreeNode theNode = (DefaultMutableTreeNode) aEvent
					.getPath().getLastPathComponent();
			expandedUserObjects.remove(theNode.getUserObject());
		}

		@Override
		public void treeExpanded(final TreeExpansionEvent aEvent) {
			final DefaultMutableTreeNode theNode = (DefaultMutableTreeNode) aEvent
					.getPath().getLastPathComponent();
			expandedUserObjects.add(theNode.getUserObject());
		}
	}

	private final class OutlineMouseListener extends MouseAdapter {
		@Override
		public void mouseReleased(final MouseEvent e) {
			if (e.isPopupTrigger()) {
				final TreePath thePath = tree.getClosestPathForLocation(e.getX(), e
						.getY());
				if (thePath != null) {
					final DefaultMutableTreeNode theNode = (DefaultMutableTreeNode) thePath
							.getLastPathComponent();
					if (theNode != null) {

						tree.setSelectionPath(thePath);

						final JPopupMenu theMenu = new JPopupMenu();

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
		public Component getTreeCellRendererComponent(final JTree aTree,
                                                      final Object aValue, final boolean aSelected, final boolean aExpanded,
                                                      final boolean aLeaf, final int aRow, final boolean hasFocus) {
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
				final DefaultMutableTreeNode theNode = (DefaultMutableTreeNode) aValue;
				final Object theUserObject = theNode.getUserObject();

				theLabel.setFont(theLabel.getFont().deriveFont(Font.PLAIN));

				if (theUserObject instanceof Table) {
					theLabel.setIcon(IconFactory.getEntityIcon());
					theLabel.setFont(theLabel.getFont().deriveFont(Font.BOLD));
				}
				if (theUserObject instanceof View) {
					theLabel.setIcon(IconFactory.getViewIcon());
					theLabel.setFont(theLabel.getFont().deriveFont(Font.BOLD));
				}
				if (theUserObject instanceof CustomType) {
					theLabel.setFont(theLabel.getFont().deriveFont(Font.BOLD));
					final CustomTypeType theCustomTypeType = ((CustomType) theUserObject).getType();
					if (theCustomTypeType != null) {
						switch (theCustomTypeType) {
							case COMPOSITE:
								theLabel.setIcon(IconFactory.getCustomTypeComposite());
								break;
							case ENUMERATION:
								theLabel.setIcon(IconFactory.getCustomTypeEnumeration());
								break;
							case EXTERNAL:
								theLabel.setIcon(IconFactory.getCustomTypeExternal());
								break;
							default:
						}
					}
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
				if (theUserObject instanceof UsedBy) {
					theLabel.setIcon(IconFactory.getRelationIcon());
				}
				if (theUserObject instanceof Attribute) {
					final ModelItem theOwner = ((Attribute) theUserObject).getOwner();
					if (theOwner instanceof Table) {
						theLabel.setIcon(IconFactory.getAttributeIcon());
					} else if (theOwner instanceof CustomType) {
						final CustomTypeType theCustomTypeType = ((CustomType) theOwner).getType();
						if (theCustomTypeType != null) {
							switch (theCustomTypeType) {
								case COMPOSITE:
									theLabel.setIcon(IconFactory.getCustomTypeCompositeElement());
									break;
								case ENUMERATION:
									theLabel.setIcon(IconFactory.getCustomTypeEnumerationElement());
									break;
								case EXTERNAL:
									theLabel.setIcon(IconFactory.getCustomTypeExternalElement());
									break;
								default:
							}
						}
					}
				}
				if (theUserObject instanceof Index) {
					theLabel.setIcon(IconFactory.getIndexIcon());
				}
				if (theUserObject instanceof IndexExpression) {
					final IndexExpression theExpression = (IndexExpression) theUserObject;
					if (theExpression.getAttributeRef() != null) {
						theLabel.setIcon(IconFactory.getAttributeIcon());
					} else {
						theLabel.setIcon(IconFactory.getExpressionIcon());
					}

				}

				if (theUserObject instanceof TreeGroupingElement) {
					switch ((TreeGroupingElement) theUserObject) {
						case MODEL:
							theLabel.setText(getResourceHelper().getFormattedText(
									ERDesignerBundle.CURRENTMODEL));
							break;
						case CUSTOMTYPES:
							theLabel.setText(getResourceHelper().getFormattedText(
									ERDesignerBundle.CUSTOMTYPESLIST));
							break;
						case DOMAINS:
							theLabel.setText(getResourceHelper().getFormattedText(
									ERDesignerBundle.DOMAINSLIST));
							break;
						case TABLES:
							theLabel.setText(getResourceHelper().getFormattedText(
									ERDesignerBundle.TABLES));
							break;
						case VIEWS:
							theLabel.setText(getResourceHelper().getFormattedText(
									ERDesignerBundle.VIEWS));
							break;
						case SUBJECTAREAS:
							theLabel.setText(getResourceHelper().getFormattedText(
									ERDesignerBundle.SUBJECTAREALIST));
							break;
						case RELATIONS:
							theLabel.setText(getResourceHelper().getFormattedText(
									ERDesignerBundle.RELATIONS));
							break;
						case INDEXES:
							theLabel.setText(getResourceHelper().getFormattedText(
									ERDesignerBundle.INDEXES));
							break;
						default:
							throw new IllegalArgumentException(
									"Unknown grouping element : " + aValue);
					}
				} else {
					if (theUserObject instanceof Relation) {
						final Relation theRelation = (Relation) theUserObject;
						theLabel.setText(theRelation + " -> " + theRelation.getExportingTable());
					} else if (theUserObject instanceof UsedBy) {
						final UsedBy theUsedBy = (UsedBy) theUserObject;
						theLabel.setText("<< " + theUsedBy);
					} else {
						theLabel.setText(aValue.toString());
					}
				}
			}
			return theLabel;
		}
	}

	private enum TreeGroupingElement {
		MODEL, CUSTOMTYPES, DOMAINS, TABLES, VIEWS, SUBJECTAREAS, INDEXES, RELATIONS
	}

	private DefaultTree tree;

	private DefaultTextField filterField;

	private final Map<Object, DefaultMutableTreeNode> userObjectMap = new HashMap<>();

	private final Set<Object> expandedUserObjects = new HashSet<>();

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
	 * Register a combination of an object and a tree node.
	 * <p/>
	 * An object can only be registered once!
	 *
	 * @param aObject - object
	 * @param aNode   - node
	 */
	private void registerUserObject(final Object aObject, final DefaultMutableTreeNode aNode) {
		if (!userObjectMap.containsKey(aObject)) {
			userObjectMap.put(aObject, aNode);
		}
	}

	private void initialize() {

		tree = new DefaultTree();
		tree.setCellRenderer(new OutlineTreeCellRenderer());
		tree.addMouseListener(new OutlineMouseListener());
		tree.addTreeSelectionListener(new OutlineSelectionListener());
		tree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode(
				TreeGroupingElement.MODEL)));

		setLayout(new BorderLayout());
		add(tree.getScrollPane(), BorderLayout.CENTER);

		filterField = new DefaultTextField();
		filterField.setColumns(255);
		filterField.addKeyListener(new OutlineKeyAdapter());
		final DefaultButton disableFilterButton = new DefaultButton();
		disableFilterButton.setIcon(IconFactory.getCancelIcon());
		disableFilterButton.setMaximumSize(new Dimension(21, 21));
		disableFilterButton.setMinimumSize(new Dimension(21, 21));
		disableFilterButton.setPreferredSize(new Dimension(21, 21));
		disableFilterButton
				.addActionListener(new OutlineDisableFilterActionListener());

		final JPanel theFilterPanel = new JPanel();
		theFilterPanel.setLayout(new BorderLayout());
		theFilterPanel.add(filterField, BorderLayout.CENTER);
		theFilterPanel.add(disableFilterButton, BorderLayout.EAST);

		add(theFilterPanel, BorderLayout.NORTH);

		tree.addTreeExpansionListener(new OutlineTreeExpansionListener());
	}

	private boolean isVisible(final ModelItem aItem) {
		final String thePartialString = filterField.getText().toLowerCase();
		if (StringUtils.isNotEmpty(thePartialString)) {

			boolean theOverride = false;

			if (aItem instanceof Table) {
				final Table theTable = (Table) aItem;
				for (final Attribute<Table> theAttribute : theTable.getAttributes()) {
					if (isVisible(theAttribute)) {
						theOverride = true;
					}
				}
				for (final Index theIndex : theTable.getIndexes()) {
					if (isVisible(theIndex)) {
						theOverride = true;
					}
					for (final IndexExpression theExpression : theIndex
							.getExpressions()) {
						if (isVisible(theExpression)) {
							theOverride = true;
						}
					}
				}

				for (final Relation theRelation : ERDesignerComponent.getDefault()
						.getModel().getRelations().getForeignKeysFor(theTable)) {
					if (isVisible(theRelation)) {
						theOverride = true;
					}
				}
			}

			if (aItem instanceof Relation) {
				final Relation theRelation = (Relation) aItem;
				for (final Attribute<Table> theAttribute : theRelation.getMapping().values()) {
					if (isVisible(theAttribute)) {
						theOverride = true;
					}
				}
			}

			if (aItem instanceof SubjectArea) {
				final SubjectArea theArea = (SubjectArea) aItem;
				for (final Table theTable : theArea.getTables()) {
					if (isVisible(theTable)) {
						theOverride = true;
					}
				}
				for (final View theView : theArea.getViews()) {
					if (isVisible(theView)) {
						theOverride = true;
					}
				}
			}

			final String theName = aItem.toString();

			if (StringUtils.isNotEmpty(theName)) {
				return theName.toLowerCase().contains(thePartialString) || theOverride;
			}
		}

		return true;
	}

	public void setModel(final Model aModel) {

		userObjectMap.clear();
		expandedUserObjects.clear();

		final DefaultMutableTreeNode theRoot = new DefaultMutableTreeNode(
				TreeGroupingElement.MODEL);

		final Comparator<OwnedModelItem> theComparator = new BeanComparator("name");

		// Add the user-defined datatypes
		if (aModel.getDialect() != null) {
			if (aModel.getDialect().isSupportsCustomTypes()) {
				final List<CustomType> theCustomTypes = new ArrayList<>();
				theCustomTypes.addAll(aModel.getCustomTypes());
				theCustomTypes.sort(theComparator);
				buildCustomTypesChildren(aModel, theRoot, theCustomTypes);
			}

			// Add the domains
			final List<Domain> theDomains = new ArrayList<>();
			theDomains.addAll(aModel.getDomains());
			theDomains.sort(theComparator);
			buildDomainsChildren(aModel, theRoot, theDomains);
		}

		// Add the Tables
		final List<Table> theTables = new ArrayList<>();
		theTables.addAll(aModel.getTables());
		theTables.sort(theComparator);
		buildTablesChildren(aModel, theRoot, theTables);

		// Add the Views
		final List<View> theViews = new ArrayList<>();
		theViews.addAll(aModel.getViews());
		theTables.sort(theComparator);
		buildViewsChildren(aModel, theRoot, theViews);

		// Add the Relations
		final List<Relation> theRelations = new ArrayList<>();
		theRelations.addAll(aModel.getRelations());
		theRelations.sort(theComparator);
		buildRelationChildren(theRoot, theRelations);

		// Add the Indexes
		final List<Index> theIndexes = new ArrayList<>();
		for (final Table theTable : aModel.getTables()) {
			theIndexes.addAll(theTable.getIndexes());
		}
		theIndexes.sort(theComparator);
		buildIndexChildren(theRoot, theIndexes);

		// Add the subject areas
		final List<SubjectArea> theSAList = new ArrayList<>();
		theSAList.addAll(aModel.getSubjectAreas());
		theSAList.sort(theComparator);
		buildSubjectAreasChildren(aModel, theRoot, theSAList);

		tree.setModel(new DefaultTreeModel(theRoot));

		// if (aExpandAll) {
		for (int row = 0; row < tree.getRowCount(); row++) {
			final TreePath thePath = tree.getPathForRow(row);
			final DefaultMutableTreeNode theNode = (DefaultMutableTreeNode) thePath
					.getLastPathComponent();
			final Object theUserObject = theNode.getUserObject();
			if (theUserObject instanceof TreeGroupingElement) {
				tree.expandRow(row);
			}
		}
		// }
	}

	private void buildSubjectAreasChildren(final Model aModel,
                                           final DefaultMutableTreeNode aParent, final List<SubjectArea> aList) {
		final DefaultMutableTreeNode theSANode = new DefaultMutableTreeNode(
				TreeGroupingElement.SUBJECTAREAS);
        aList.stream().filter(this::isVisible).forEach(theArea -> {
            final DefaultMutableTreeNode theAreaNode = new DefaultMutableTreeNode(
                    theArea);
            theSANode.add(theAreaNode);

            registerUserObject(theArea, theAreaNode);

            final List<Table> theSATables = new ArrayList<>();
            theSATables.addAll(theArea.getTables());
            theSATables.sort(new BeanComparator("name"));
            buildTablesChildren(aModel, theAreaNode, theSATables);

            final List<View> theSAViews = new ArrayList<>();
            theSAViews.addAll(theArea.getViews());
            theSAViews.sort(new BeanComparator("name"));
            buildViewsChildren(aModel, theAreaNode, theSAViews);

        });
		aParent.add(theSANode);
	}

	private void buildViewsChildren(final Model aModel,
                                    final DefaultMutableTreeNode aParent, final List<View> aViews) {
		final DefaultMutableTreeNode theViewsNode = new DefaultMutableTreeNode(
				TreeGroupingElement.VIEWS);
        aViews.stream().filter(this::isVisible).forEach(theView -> {
            final DefaultMutableTreeNode theViewNode = new DefaultMutableTreeNode(
                    theView);
            theViewsNode.add(theViewNode);

            registerUserObject(theView, theViewNode);

            updateViewTreeNode(aModel, theView, theViewNode);
        });
		aParent.add(theViewsNode);
	}

	private void buildCustomTypesChildren(final Model aModel,
                                          final DefaultMutableTreeNode aParent, final List<CustomType> aCustomTypesList) {
		final DefaultMutableTreeNode theCustomTypesNode = new DefaultMutableTreeNode(
				TreeGroupingElement.CUSTOMTYPES);
        aCustomTypesList.stream().filter(this::isVisible).forEach(theCustomType -> {
            final DefaultMutableTreeNode theCustomTypeNode = new DefaultMutableTreeNode(
                    theCustomType);
            theCustomTypesNode.add(theCustomTypeNode);

            registerUserObject(theCustomType, theCustomTypeNode);

            updateCustomTypeTreeNode(aModel, theCustomType,
                    theCustomTypeNode);
        });
		aParent.add(theCustomTypesNode);
	}

	private void buildDomainsChildren(final Model aModel,
                                      final DefaultMutableTreeNode aParent, final List<Domain> aDomainList) {
		final DefaultMutableTreeNode theDomainsNode = new DefaultMutableTreeNode(
				TreeGroupingElement.DOMAINS);
        aDomainList.stream().filter(this::isVisible).forEach(theDomain -> {
            final DefaultMutableTreeNode theDomainNode = new DefaultMutableTreeNode(
                    theDomain);
            theDomainsNode.add(theDomainNode);

            registerUserObject(theDomain, theDomainNode);

            updateDomainTreeNode(aModel, theDomain, theDomainNode);
        });
		aParent.add(theDomainsNode);
	}

	private void buildTablesChildren(final Model aModel,
                                     final DefaultMutableTreeNode aParentNode, final List<Table> aTableList) {
		final DefaultMutableTreeNode theTablesNode = new DefaultMutableTreeNode(
				TreeGroupingElement.TABLES);
        aTableList.stream().filter(this::isVisible).forEach(theTable -> {
            final DefaultMutableTreeNode theTableNode = new DefaultMutableTreeNode(
                    theTable);
            theTablesNode.add(theTableNode);

            registerUserObject(theTable, theTableNode);

            updateTableTreeNode(aModel, theTable, theTableNode);
        });
		aParentNode.add(theTablesNode);
	}

	private void buildRelationChildren(final DefaultMutableTreeNode aParentNode,
                                       final List<Relation> aRelationList) {
		final DefaultMutableTreeNode theRelationsNode = new DefaultMutableTreeNode(
				TreeGroupingElement.RELATIONS);
        aRelationList.stream().filter(this::isVisible).forEach(theRelation -> createRelationTreeNode(theRelationsNode, theRelation));
		aParentNode.add(theRelationsNode);
	}

	private void buildIndexChildren(final DefaultMutableTreeNode aParentNode,
                                    final List<Index> aIndexList) {
		final DefaultMutableTreeNode theIndexesNode = new DefaultMutableTreeNode(
				TreeGroupingElement.INDEXES);
        aIndexList.stream().filter(this::isVisible).forEach(theIndex -> createIndexTreeNode(theIndexesNode, theIndex));
		aParentNode.add(theIndexesNode);
	}

	private void createIndexTreeNode(final DefaultMutableTreeNode aParentNode,
                                     final Index aIndex) {
		final DefaultMutableTreeNode theIndexNode = new DefaultMutableTreeNode(aIndex);
		aParentNode.add(theIndexNode);
		registerUserObject(aIndex, theIndexNode);

        aIndex.getExpressions().stream().filter(this::isVisible).forEach(theExpression -> {
            final DefaultMutableTreeNode theExpressionNode = new DefaultMutableTreeNode(
                    theExpression);
            theIndexNode.add(theExpressionNode);

            registerUserObject(theExpression, theExpressionNode);
        });
	}

	private void createRelationTreeNode(final DefaultMutableTreeNode aParent, final Relation aRelation) {
		final DefaultMutableTreeNode theRelationNode = new DefaultMutableTreeNode(aRelation);
		aParent.add(theRelationNode);

		registerUserObject(aRelation, theRelationNode);

        aRelation
                .getMapping().entrySet().stream().filter(theEntry -> isVisible(theEntry.getValue())).forEach(theEntry -> {
            final DefaultMutableTreeNode theAttributeNode = new DefaultMutableTreeNode(
                    theEntry.getValue());
            theRelationNode.add(theAttributeNode);

            registerUserObject(theEntry.getKey(), theAttributeNode);
        });
	}

	private void updateCustomTypeTreeNode(final Model aModel, final CustomType aCustomType,
                                          final DefaultMutableTreeNode aCustomTypeNode) {

		aCustomTypeNode.removeAllChildren();

		//display only details of ENUMERATION and COMPOSITE CustomTypes
		if ((aCustomType.getType() == CustomTypeType.ENUMERATION) ||
				(aCustomType.getType() == CustomTypeType.COMPOSITE)) {
            aCustomType.getAttributes().stream().filter(this::isVisible).forEach(theAttribute -> {
                final DefaultMutableTreeNode theAttributeNode = new DefaultMutableTreeNode(theAttribute);

                aCustomTypeNode.add(theAttributeNode);

                registerUserObject(theAttribute, theAttributeNode);
            });
		} else {
			//TODO: handle EXTERNAL CustomTypes here
		}
	}

	private void updateDomainTreeNode(final Model aModel, final Domain aDomain,
                                      final DefaultMutableTreeNode aDomainNode) {

		aDomainNode.removeAllChildren();
	}

	private void updateViewTreeNode(final Model aModel, final View aView,
                                    final DefaultMutableTreeNode aViewNode) {

		aViewNode.removeAllChildren();
	}

	private void updateTableTreeNode(final Model aModel, final Table aTable,
                                     final DefaultMutableTreeNode aTableNode) {

		aTableNode.removeAllChildren();

        aTable.getAttributes().stream().filter(this::isVisible).forEach(theAttribute -> {
            final DefaultMutableTreeNode theAttributeNode = new DefaultMutableTreeNode(
                    theAttribute);
            aTableNode.add(theAttributeNode);

            registerUserObject(theAttribute, theAttributeNode);
        });

        aTable.getIndexes().stream().filter(this::isVisible).forEach(theIndex -> createIndexTreeNode(aTableNode, theIndex));

        aModel.getRelations().getForeignKeysFor(
                aTable).stream().filter(this::isVisible).forEach(theRelation -> createRelationTreeNode(aTableNode, theRelation));

		final Set<Table> theAlreadyKnown = new HashSet<>();
        aModel.getRelations().getExportedKeysFor(aTable).stream().filter(theRelation -> isVisible(theRelation) && !theAlreadyKnown.contains(theRelation.getImportingTable())).forEach(theRelation -> {
            final UsedBy theUsedBy = new UsedBy();
            theUsedBy.ref = theRelation.getImportingTable();
            final DefaultMutableTreeNode theUsedByNode = new DefaultMutableTreeNode(theUsedBy);
            aTableNode.add(theUsedByNode);

            theAlreadyKnown.add(theRelation.getImportingTable());
        });
	}

	@Override
	public ResourceHelper getResourceHelper() {
		return ResourceHelper.getResourceHelper(ERDesignerBundle.BUNDLE_NAME);
	}

	/**
	 * Refresh the model tree as there were changes.
	 *
	 * @param aModel - model
	 */
	public void refresh(final Model aModel) {

		if (aModel != null) {

			final TreePath theSelected = tree.getSelectionPath();

			DefaultMutableTreeNode theGroup = null;
			final DefaultMutableTreeNode theSelectedNode = theSelected != null ? (DefaultMutableTreeNode) theSelected
					.getLastPathComponent()
					: null;
			if (theSelected != null && theSelected.getPathCount() > 1) {
				theGroup = (DefaultMutableTreeNode) theSelected.getPath()[1];
			}

            setModel(aModel);

			final List<TreePath> thePathsToExpand = new ArrayList<>();
			TreePath theNewSelection = null;

			for (int theRow = 0; theRow < tree.getRowCount(); theRow++) {
				final TreePath thePath = tree.getPathForRow(theRow);

				final DefaultMutableTreeNode theLastNew = (DefaultMutableTreeNode) thePath
						.getLastPathComponent();
				if (expandedUserObjects.contains(theLastNew.getUserObject())) {
					thePathsToExpand.add(thePath);
				}
				if (theSelectedNode != null) {
					DefaultMutableTreeNode theLastGroup = null;
					if (thePath.getPathCount() > 1) {
						theLastGroup = (DefaultMutableTreeNode) thePath
								.getPath()[1];
					}
					if (theLastGroup != null && theGroup != null) {
						if (!theLastGroup.getUserObject().equals(
								theGroup.getUserObject())) {
							continue;
						}
					}
					if (theLastNew.getUserObject().equals(
							theSelectedNode.getUserObject())) {
						theNewSelection = thePath;
					}
				}
			}

            thePathsToExpand.forEach(tree::expandPath);

			if (theNewSelection != null) {
				tree.setSelectionPath(theNewSelection);
				tree.scrollPathToVisible(theNewSelection);
			}
		}
	}

	private void expandOrCollapseAllChildrenOfNode(final TreePath aParentPath,
                                                   final boolean aExpand) {
		final TreeNode node = (TreeNode) aParentPath.getLastPathComponent();
		if (node.getChildCount() > 0) {
			for (final Enumeration<TreeNode> en = (Enumeration<TreeNode>) node.children(); en
					.hasMoreElements(); ) {
				final TreeNode n = en.nextElement();
				final TreePath path = aParentPath.pathByAddingChild(n);
				expandOrCollapseAllChildrenOfNode(path, aExpand);
			}
		}
		if (aExpand) {
			tree.expandPath(aParentPath);
		} else {
			tree.collapsePath(aParentPath);
		}
	}

	/**
	 * Create the PopupMenu actions correlating to a specific tree node.
	 *
	 * @param aNode	  - the node
	 * @param aMenu	  - the menu to add the actions to
	 * @param aRecursive - recursive
	 */
	private void initializeActionsFor(final DefaultMutableTreeNode aNode,
                                      final JPopupMenu aMenu, final boolean aRecursive) {

		final Object theUserObject = aNode.getUserObject();

		if (!aRecursive) {
			final JMenuItem theExpandAllItem = new JMenuItem();
			theExpandAllItem.setText(getResourceHelper().getFormattedText(
					ERDesignerBundle.EXPANDALL));
			theExpandAllItem.addActionListener(e -> expandOrCollapseAllChildrenOfNode(new TreePath(aNode
                    .getPath()), true));
			aMenu.add(theExpandAllItem);

			final JMenuItem theCollapseAllItem = new JMenuItem();
			theCollapseAllItem.setText(getResourceHelper().getFormattedText(
					ERDesignerBundle.COLLAPSEALL));
			theCollapseAllItem.addActionListener(e -> expandOrCollapseAllChildrenOfNode(new TreePath(aNode
                    .getPath()), false));

			aMenu.add(theCollapseAllItem);
			aMenu.addSeparator();
		}

		final List<ModelItem> theItemList = new ArrayList<>();
		if (theUserObject instanceof ModelItem) {
			theItemList.add((ModelItem) theUserObject);
			ContextMenuFactory.addActionsToMenu(ERDesignerComponent.getDefault().getEditor(), aMenu, theItemList);
		}

		if (aNode.getParent() != null) {
			initializeActionsFor((DefaultMutableTreeNode) aNode.getParent(),
					aMenu, true);
		}
	}

	/**
	 * Set the currently selected item.
	 *
	 * @param aSelection the selection
	 */
	public void setSelectedItem(final ModelItem aSelection) {

		final TreePath theSelected = tree.getSelectionPath();
		if (theSelected != null) {
			final DefaultMutableTreeNode theNode = (DefaultMutableTreeNode) theSelected
					.getLastPathComponent();
			if (theNode.getUserObject().equals(aSelection)) {
				// The object is already selected, so keep the selection
				return;
			}
		}

		final DefaultMutableTreeNode theNode = userObjectMap.get(aSelection);
		if (theNode != null) {
			final TreePath thePath = new TreePath(theNode.getPath());
			tree.setSelectionPath(thePath);
			tree.scrollPathToVisible(thePath);
		} else {
			tree.clearSelection();
		}

		tree.invalidate();
		tree.repaint();
	}
}
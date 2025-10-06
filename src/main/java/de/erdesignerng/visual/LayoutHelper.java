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

import de.erdesignerng.model.*;
import de.erdesignerng.visual.jgraph.cells.views.TableCellView;
import de.erdesignerng.visual.jgraph.cells.views.ViewCellView;
import java.awt.Dimension;
import java.awt.Point;
import java.util.*;
import javax.swing.JComponent;
import org.apache.log4j.Logger;

public class LayoutHelper {

    private static final Logger LOGGER = Logger.getLogger(LayoutHelper.class);

    private static final int OFFSETX = 60;

    private static final int OFFSETY = 60;

    private static final int DISTANCEX = 120;

    private static final int DISTANCEY = 120;

    private static final int MAXWIDTH = 6000;

    private static class Cluster {
        Table root;
        final Set<ModelItem> nodes = new HashSet<>();
        final Map<ModelItem, Integer> hierarchyLevel = new HashMap<>();
        int maxLevel;

        List<Set<Table>> computeHierarchy() {

            final List<ModelItem> theAllNodes = new ArrayList<>();
            if (root != null) {
                theAllNodes.add(root);
            }
            theAllNodes.addAll(nodes);

            final List<Set<Table>> theResult = new ArrayList<>();
            for (int i = 0; i <= maxLevel; i++) {
                final Set<Table> theLevel = new HashSet<>();
                for (final ModelItem theItem : theAllNodes) {
                    if (hierarchyLevel.get(theItem) == i) {
                        theLevel.add((Table) theItem);
                    }
                }
                theResult.add(theLevel);
            }
            return theResult;
        }
    }

    private final Set<ModelItem> alreadyProcessed = new HashSet<>();

    public LayoutHelper() {
    }

    private void buildTree(final Model aModel, final Table aNode, final Cluster aCluster, final int aLevel) {
        for (final Relation theRelation : aModel.getRelations().getForeignKeysFor(aNode)) {
            final Table theExp = theRelation.getExportingTable();
            if (!aNode.equals(theExp) && !aCluster.nodes.contains(theExp) && !alreadyProcessed.contains(theExp)) {
                aCluster.nodes.add(theExp);
                aCluster.hierarchyLevel.put(theExp, aLevel);
                aCluster.maxLevel = Math.max(aCluster.maxLevel, aLevel);
                buildTree(aModel, theExp, aCluster, aLevel + 1);
            }
        }
    }

    public void performClusterLayout(final Model aModel) {

        LOGGER.info("Destroying all subject areas");

        long theDuration = System.currentTimeMillis();

        // Destroy all subject areas in the model
        aModel.getSubjectAreas().clear();

        boolean continueRun = true;

        final Point theStart = new Point(OFFSETX, OFFSETY);
        final List<Table> theAllTables = new ArrayList<>();
        theAllTables.addAll(aModel.getTables());
        int maxHeight = 0;

        while (continueRun) {

            LOGGER.info("Starting with build cluster run");

            continueRun = false;

            // Step 1. Identify the Root Nodes
            final List<Cluster> theRoots = new ArrayList<>();
            for (final Table theTable : aModel.getTables()) {
                if (!alreadyProcessed.contains(theTable)) {
                    boolean isRoot = true;
                    for (final Relation theRelation : aModel.getRelations().getExportedKeysFor(theTable)) {
                        // Check for self references
                        if (!theRelation.getImportingTable().equals(theTable)) {
                            isRoot = false;
                        }
                    }
                    if (isRoot) {
                        final Cluster theCluster = new Cluster();
                        theCluster.root = theTable;
                        theRoots.add(theCluster);
                    }
                }
            }

            LOGGER.info("Found " + theRoots.size() + " root nodes : " + theRoots);

            // Step 2. Build the tree for every root node
            for (final Cluster theCluster : theRoots) {
                theCluster.hierarchyLevel.put(theCluster.root, 0);
                theCluster.maxLevel = 0;
                buildTree(aModel, theCluster.root, theCluster, 1);
            }

            final Set<ModelItem> theClusteredTables = new HashSet<>();

            // Step 3. Remove nodes that are assigned to more than one cluster
            for (final Cluster theCluster : theRoots) {
                for (final Cluster theCluster2 : theRoots) {
                    if (theCluster != theCluster2) {
                        theCluster.nodes.remove(theCluster2.root);
                        theCluster2.nodes.remove(theCluster.root);
                        theCluster.nodes.removeAll(theCluster2.nodes);
                        theCluster2.nodes.removeAll(theCluster.nodes);

                        theClusteredTables.addAll(theCluster.nodes);
                        theClusteredTables.addAll(theCluster2.nodes);
                    }
                }
            }

            // Check for clustes with only one root
            for (final Cluster theCluster : theRoots) {
                if (theCluster.nodes.isEmpty()) {
                    final Set<Table> theDependentTables = new HashSet<>();
                    for (final Relation theRelation : aModel.getRelations().getForeignKeysFor(theCluster.root)) {
                        if (!theRelation.isSelfReference()) {
                            theDependentTables.add(theRelation.getExportingTable());
                        }
                    }
                    if (!theDependentTables.isEmpty()) {
                        // Map the root node of the cluster to the cluster that contains all the referenced
                        // tables
                        for (final Cluster theCluster2 : theRoots) {
                            if (theCluster2.nodes.containsAll(theDependentTables)) {
                                theCluster2.nodes.add(theCluster.root);
                                theCluster2.hierarchyLevel.put(theCluster.root, theCluster2.maxLevel);
                            }
                        }
                    }
                }
            }

            // Now, we have the top level clusters
            // Every cluster might result in a new subject area
            for (final Cluster theCluster : theRoots) {
                // A cluster should have more than 1 element including the root
                if (!theCluster.nodes.isEmpty()) {
                    LOGGER.info("Creating new subject area for cluster " + theCluster.root + " with " + theCluster.nodes.size() + " nodes : " + theCluster.nodes);

                    final SubjectArea theArea = new SubjectArea();
                    theArea.setExpanded(true);
                    theArea.setName(theCluster.root.getName());
                    if (theCluster.root != null) {
                        theArea.getTables().add(theCluster.root);
                        alreadyProcessed.add(theCluster.root);
                        theAllTables.remove(theCluster.root);
                    }
                    for (final ModelItem theItem : theCluster.nodes) {
                        alreadyProcessed.add(theItem);
                        if (theItem instanceof Table) {
                            theArea.getTables().add((Table) theItem);
                            theAllTables.remove(theItem);
                        }
                        if (theItem instanceof View) {
                            theArea.getViews().add((View) theItem);
                        }
                    }

                    // We layout every subject area initially as a tree
                    final Dimension theSize = performTreeLayout(theStart, theCluster.computeHierarchy(), theArea.getViews());
                    theStart.x += theSize.width + DISTANCEX;
                    maxHeight = Math.max(maxHeight, theSize.height);

                    if (theStart.x > MAXWIDTH) {
                        theStart.x = OFFSETX;
                        theStart.y += maxHeight + DISTANCEY;
                        maxHeight = 0;
                    }

                    aModel.getSubjectAreas().add(theArea);

                    // We continue as long as we find new subject areas
                    continueRun = true;
                }
            }
        }

        // Now, we recompute the position of the remaining tables and views
        theStart.x = OFFSETX;
        if (maxHeight > 0) {
            theStart.y += maxHeight + DISTANCEY;
            maxHeight = 0;
        }

        final TableCellView.MyRenderer theTableRenderer = new TableCellView.MyRenderer();

        for (final Table theTable : theAllTables) {
            theTable.getProperties().setPointProperty(Table.PROPERTY_LOCATION, theStart.x, theStart.y);

            final JComponent theRendererComponent = theTableRenderer.getRendererComponent(theTable);
            final Dimension theSize = theRendererComponent.getPreferredSize();

            theStart.x += theSize.width + DISTANCEX;
            maxHeight = Math.max(maxHeight, theSize.height);

            if (theStart.x > MAXWIDTH) {
                theStart.x = OFFSETX;
                theStart.y += maxHeight + DISTANCEY;
                maxHeight = 0;
            }
        }

        theStart.x = OFFSETX;
        if (maxHeight > 0) {
            theStart.y += maxHeight + DISTANCEY;
            maxHeight = 0;
        }

        final ViewCellView.MyRenderer theViewRenderer = new ViewCellView.MyRenderer();

        for (final View theView : aModel.getViews()) {

            theView.getProperties().setPointProperty(Table.PROPERTY_LOCATION, theStart.x, theStart.y);

            final JComponent theRendererComponent = theViewRenderer.getRendererComponent(theView);
            final Dimension theSize = theRendererComponent.getPreferredSize();

            theStart.x += theSize.width + DISTANCEX;
            maxHeight = Math.max(maxHeight, theSize.height);

            if (theStart.x > MAXWIDTH) {
                theStart.x = OFFSETX;
                theStart.y += maxHeight + DISTANCEY;
                maxHeight = 0;
            }
        }

        theDuration = System.currentTimeMillis() - theDuration;

        LOGGER.info("Cluster run finished in " + theDuration + "ms");
    }

    public Dimension performTreeLayout(final Point aStartLocation, final List<Set<Table>> aHierarchy, final List<View> aViews) {
        int yp = aStartLocation.y;
        int maxx = 0;
        int maxy = 0;
        for (final Set<Table> theEntry : aHierarchy) {

            final TableCellView.MyRenderer theRenderer = new TableCellView.MyRenderer();
            int xp = aStartLocation.x;
            int maxHeight = 0;
            for (final Table theTable : theEntry) {

                theTable.getProperties().setPointProperty(Table.PROPERTY_LOCATION, xp, yp);

                final JComponent theRenderComponent = theRenderer.getRendererComponent(theTable);
                final Dimension theSize = theRenderComponent.getPreferredSize();

                maxHeight = Math.max(maxHeight, (int) theSize.getHeight());

                xp += theSize.getWidth() + DISTANCEX;

                maxx = Math.max(xp, maxx);
                maxy = Math.max(yp + maxHeight, maxy);
            }

            if (!theEntry.isEmpty()) {
                yp += maxHeight + DISTANCEY;
            }
        }

        final ViewCellView.MyRenderer theRenderer = new ViewCellView.MyRenderer();

        int xp = aStartLocation.x;
        for (final View theView : aViews) {

            theView.getProperties().setPointProperty(Table.PROPERTY_LOCATION, xp, yp);

            final JComponent theRenderComponent = theRenderer.getRendererComponent(theView);
            final Dimension theSize = theRenderComponent.getPreferredSize();

            xp += theSize.getWidth() + DISTANCEX;

            maxx = Math.max(xp, maxx);
            maxy = Math.max(yp + theSize.height, maxy);
        }
        return new Dimension(maxx - aStartLocation.x, maxy - aStartLocation.y);
    }
}
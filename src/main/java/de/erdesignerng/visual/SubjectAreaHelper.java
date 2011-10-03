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

import de.erdesignerng.model.Model;
import de.erdesignerng.model.ModelIOUtilities;
import de.erdesignerng.model.ModelItem;
import de.erdesignerng.model.Relation;
import de.erdesignerng.model.SubjectArea;
import de.erdesignerng.model.Table;
import de.erdesignerng.model.View;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.awt.Dimension;
import java.awt.Point;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Helper class to find clusters of tables in a database model
 * and group them together as a subject area.
 */
public class SubjectAreaHelper {

    private static final Logger LOGGER = Logger.getLogger(SubjectAreaHelper.class);

    private class Cluster {
        Table root;
        Set<ModelItem> nodes = new HashSet<ModelItem>();
        Map<ModelItem, Integer> hierarchyLevel = new HashMap<ModelItem, Integer>();
        int maxLevel;

        List<Set<Table>> computeHierarchy() {

            List<ModelItem> theAllNodes = new ArrayList<ModelItem>();
            if (root != null) {
                theAllNodes.add(root);
            }
            theAllNodes.addAll(nodes);

            List<Set<Table>> theResult = new ArrayList<Set<Table>>();
            for (int i = 0; i <= maxLevel; i++) {
                Set<Table> theLevel = new HashSet<Table>();
                for (ModelItem theItem : theAllNodes) {
                    if (hierarchyLevel.get(theItem) == i) {
                        theLevel.add((Table) theItem);
                    }
                }
                theResult.add(theLevel);
            }
            return theResult;
        }
    }

    private Set<ModelItem> alreadyProcessed = new HashSet<ModelItem>();

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        FileInputStream theStream = new FileInputStream("C:\\TEMP\\Capitastra_6_5_0.mxm");
        Model theModel = ModelIOUtilities.getInstance().deserializeModelFromXML(theStream);

        SubjectAreaHelper theHelper = new SubjectAreaHelper();
        theHelper.computeCluster(theModel);
    }

    public SubjectAreaHelper() {

    }

    private void buildTree(Model aModel, Table aNode, Cluster aCluster, int aLevel) {
        for (Relation theRelation : aModel.getRelations().getForeignKeysFor(aNode)) {
            Table theExp = theRelation.getExportingTable();
            if (!aNode.equals(theExp) && !aCluster.nodes.contains(theExp) && !alreadyProcessed.contains(theExp)) {
                aCluster.nodes.add(theExp);
                aCluster.hierarchyLevel.put(theExp, aLevel);
                aCluster.maxLevel = Math.max(aCluster.maxLevel, aLevel);
                buildTree(aModel, theExp, aCluster, aLevel + 1);
            }
        }
    }

    public void computeCluster(Model aModel) {

        LOGGER.info("Destroying all subject areas");

        long theDuration = System.currentTimeMillis();

        // Destroy all subject areas in the model
        aModel.getSubjectAreas().clear();

        boolean continueRun = true;

        Point theStart = new Point(60, 60);
        List<Table> theAllTables = new ArrayList<Table>();
        theAllTables.addAll(aModel.getTables());
        int maxHeight = 0;

        while (continueRun) {

            LOGGER.info("Starting with build cluster run");

            continueRun = false;

            // Step 1. Identify the Root Nodes
            List<Cluster> theRoots = new ArrayList<Cluster>();
            for (Table theTable : aModel.getTables()) {
                if (!alreadyProcessed.contains(theTable)) {
                    boolean isRoot = true;
                    for (Relation theRelation : aModel.getRelations().getExportedKeysFor(theTable)) {
                        // Check for self references
                        if (!theRelation.getImportingTable().equals(theTable)) {
                            isRoot = false;
                        }
                    }
                    if (isRoot) {
                        Cluster theCluster = new Cluster();
                        theCluster.root = theTable;
                        theRoots.add(theCluster);
                    }
                }
            }

            LOGGER.info("Found " + theRoots.size() + " root nodes : " + theRoots);

            // Step 2. Build the tree for every root node
            for (Cluster theCluster : theRoots) {
                theCluster.hierarchyLevel.put(theCluster.root, 0);
                theCluster.maxLevel = 0;
                buildTree(aModel, theCluster.root, theCluster, 1);
            }

            // Step 3. Remove nodes that are assigned to more than one cluster
            for (Cluster theCluster : theRoots) {
                for (Cluster theCluster2 : theRoots) {
                    if (theCluster != theCluster2) {
                        theCluster.nodes.remove(theCluster2.root);
                        theCluster2.nodes.remove(theCluster.root);
                        theCluster.nodes.removeAll(theCluster2.nodes);
                        theCluster2.nodes.removeAll(theCluster.nodes);
                    }
                }
            }

            // Now, we have the top level clusters
            // Every cluster might result in a new subject area
            for (Cluster theCluster : theRoots) {
                // A cluster should have more than 1 element including the root
                if (theCluster.nodes.size() > 0) {
                    LOGGER.info("Creating new subject area for cluster " + theCluster.root + " with " + theCluster.nodes.size() + " nodes : " + theCluster.nodes);

                    SubjectArea theArea = new SubjectArea();
                    theArea.setExpanded(false);
                    theArea.setName(theCluster.root.getName());
                    if (theCluster.root != null) {
                        theArea.getTables().add(theCluster.root);
                        alreadyProcessed.add(theCluster.root);
                        theAllTables.remove(theCluster.root);
                    }
                    for (ModelItem theItem : theCluster.nodes) {
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
                    Dimension theSize = LayoutHelper.performTreeLayout(theStart, theCluster.computeHierarchy(), theArea.getViews());
                    theStart.x += theSize.width + 60;

                    maxHeight = Math.max(maxHeight, theSize.height);

                    aModel.getSubjectAreas().add(theArea);

                    // We continue as long as we find new subject areas
                    continueRun = true;
                }
            }
        }

        // Now, we recompute the position of the remaining tables
        theStart = new Point(60, 120 + maxHeight);
        for (Table theTable : theAllTables) {
            theTable.getProperties().setPointProperty(Table.PROPERTY_LOCATION, theStart.x, theStart.y);
            theStart.x += 120 + 60;
        }

        theDuration = System.currentTimeMillis() - theDuration;

        LOGGER.info("Cluster run finished in " + theDuration + "ms");
    }
}

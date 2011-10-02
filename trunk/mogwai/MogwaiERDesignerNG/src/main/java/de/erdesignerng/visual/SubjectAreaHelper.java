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
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Helper class to find clusters of tables in a database model
 * and group them together as a subject area.
 */
public class SubjectAreaHelper {

    private class Cluster {
        Table root;
        Set<ModelItem> nodes = new HashSet<ModelItem>();
    }

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        FileInputStream theStream = new FileInputStream("C:\\TEMP\\Capitastra_6_5_0.mxm");
        Model theModel = ModelIOUtilities.getInstance().deserializeModelFromXML(theStream);

        SubjectAreaHelper theHelper = new SubjectAreaHelper();
        theHelper.computeCluster(theModel);
    }

    public SubjectAreaHelper() {

    }

    private void buildTree(Model aModel, Table aNode, Cluster aCluster) {
        for (Relation theRelation : aModel.getRelations().getForeignKeysFor(aNode)) {
            if (!aNode.equals(theRelation.getExportingTable()) && !aCluster.nodes.contains(theRelation.getExportingTable())) {
                aCluster.nodes.add(theRelation.getExportingTable());
                buildTree(aModel, theRelation.getExportingTable(), aCluster);
            }
        }
    }

    public void computeCluster(Model aModel) {

        // Destroy all subject areas in the model
        aModel.getSubjectAreas().clear();

        // Step 1. Identify the Root Nodes
        List<Cluster> theRoots = new ArrayList<Cluster>();
        for (Table theTable : aModel.getTables()) {
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
        // Step 2. Build the tree for every root node
        for (Cluster theCluster : theRoots) {
            buildTree(aModel, theCluster.root, theCluster);
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

        // One Cluster contains all of the views
        Cluster theViewCluster = new Cluster();
        theViewCluster.nodes.addAll(aModel.getViews());
        theRoots.add(theViewCluster);

        int counter = 1;

        // Now, we have the top level clusters
        // Every cluster might result in a new subject area
        for (Cluster theCluster : theRoots) {
            // A cluster should have more than 1 element including the root
            if (theCluster.nodes.size() > 0) {
                System.out.println("Cluster with " + theCluster.root + " " + theCluster.nodes.size() + " " + theCluster.nodes);

                SubjectArea theArea = new SubjectArea();
                theArea.setExpanded(false);
                theArea.setName("Subject Area " + counter++);
                if (theCluster.root != null) {
                    theArea.getTables().add(theCluster.root);
                }
                for (ModelItem theItem : theCluster.nodes) {
                    if (theItem instanceof Table) {
                        theArea.getTables().add((Table) theItem);
                    }
                    if (theItem instanceof View) {
                        theArea.getViews().add((View) theItem);
                    }
                }
                aModel.getSubjectAreas().add(theArea);
            }
        }
    }
}

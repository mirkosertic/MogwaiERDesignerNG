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
package de.erdesignerng.visual.java2d;

import de.erdesignerng.model.Comment;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.ModelItem;
import de.erdesignerng.model.Relation;
import de.erdesignerng.model.SubjectArea;
import de.erdesignerng.model.Table;
import de.erdesignerng.model.View;
import de.erdesignerng.visual.DisplayLevel;
import de.erdesignerng.visual.DisplayOrder;
import de.erdesignerng.visual.common.ERDesignerComponent;
import de.erdesignerng.visual.common.GenericModelEditor;
import de.erdesignerng.visual.common.ToolEnum;
import de.erdesignerng.visual.common.ZoomInfo;
import de.erdesignerng.visual.jgraph.export.Exporter;
import de.mogwai.common.client.looks.components.menu.DefaultMenu;

import javax.swing.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Editor for Java2D Interactive Mode.
 */
public class Java2DEditor implements GenericModelEditor {

    private Model model;
    private ModelItem currentModelItem;
    private EditorPanel editorPanel;

    public Java2DEditor() {
        editorPanel = new EditorPanel() {
            @Override
            public void componentClicked(EditorComponent aComponent) {
                Java2DEditor.this.componentClicked(aComponent);
            }
        };
    }

    protected void componentClicked(EditorPanel.EditorComponent aComponent) {
        setSelectedObject((ModelItem) aComponent.userObject);
    }

    @Override
    public void repaintGraph() {
        editorPanel.invalidate();
        editorPanel.repaint();
    }

    @Override
    public void commandSetDisplayLevel(DisplayLevel aLevel) {
    }

    @Override
    public void commandSetDisplayOrder(DisplayOrder aOrder) {
    }

    @Override
    public void commandHideSubjectArea(SubjectArea aArea) {
    }

    @Override
    public void commandShowSubjectArea(SubjectArea aArea) {
    }

    @Override
    public void commandSetTool(ToolEnum aTool) {
    }

    @Override
    public void commandSetZoom(ZoomInfo aZoomInfo) {
    }

    @Override
    public void setModel(Model aModel) {
        model = aModel;
        setSelectedObject(null);
    }

    @Override
    public void commandSetDisplayCommentsState(boolean aState) {
    }

    @Override
    public void commandSetDisplayGridState(boolean aState) {
    }

    @Override
    public void refreshPreferences() {
    }

    @Override
    public void commandNotifyAboutEdit() {
    }

    @Override
    public void setIntelligentLayoutEnabled(boolean aStatus) {
    }

    @Override
    public void setSelectedObject(ModelItem aItem) {
        currentModelItem = aItem;
        editorPanel.cleanup();
        if (aItem instanceof Table) {
            generateGraphFor((Table) aItem);
        }
    }

    @Override
    public void commandAddToNewSubjectArea(List<ModelItem> aItems) {
    }

    @Override
    public void commandDelete(List<ModelItem> aItems) {
    }

    @Override
    public void commandCreateComment(Comment aComment, Point2D aLocation) {
    }

    @Override
    public void commandCreateRelation(Relation aRelation) {
    }

    @Override
    public void commandCreateTable(Table aTable, Point2D aLocation) {
    }

    @Override
    public void commandCreateView(View aView, Point2D aLocation) {
    }

    @Override
    public void commandShowOrHideRelationsFor(Table aTable, boolean aShow) {
    }

    @Override
    public JComponent getDetailComponent() {
        return editorPanel;
    }

    @Override
    public void addExportEntries(DefaultMenu aMenu, Exporter aExporter) {
    }

    private void generateGraphFor(Table aTable) {

        Map<ModelItem, EditorPanel.EditorComponent> theComponentMap = new HashMap<ModelItem, EditorPanel.EditorComponent>();

        TableComponent theButton = new TableComponent(aTable);
        EditorPanel.EditorComponent theRoot = new EditorPanel.EditorComponent(aTable, 0, 0, theButton, true);
        editorPanel.add(theRoot);

        theComponentMap.put(aTable, theRoot);

        int r1 = 250;

        List<Table> theAlreadyKnown = new ArrayList<Table>();
        theAlreadyKnown.add(aTable);

        // Level 1
        List<Relation> theIncomingRelationsLevel1 = model.getRelations().getForeignKeysFor(aTable);
        //List<Relation> theOutgoingRelationsLevel1 = model.getRelations().getExportedKeysFor(aTable);
        //theIncomingRelationsLevel1.addAll(theOutgoingRelationsLevel1);
        List<Table> theTablesLevel1 = new ArrayList<Table>();
        for (Relation theRelation : theIncomingRelationsLevel1) {
            if (!theTablesLevel1.contains(theRelation.getExportingTable()) && !theAlreadyKnown.contains(theRelation.getExportingTable())) {
                theTablesLevel1.add(theRelation.getExportingTable());
                theAlreadyKnown.add(theRelation.getExportingTable());
            }
            if (!theTablesLevel1.contains(theRelation.getImportingTable()) && !theAlreadyKnown.contains(theRelation.getImportingTable())) {
                theTablesLevel1.add(theRelation.getImportingTable());
                theAlreadyKnown.add(theRelation.getImportingTable());
            }
        }

        if (theTablesLevel1.size() > 0) {
            float theIncrement1 = 360 / theTablesLevel1.size();
            float theAngleLevel1 = 0;

            for (Table theTableLevel1 : theTablesLevel1) {

                TableComponent theButtonLevel1 = new TableComponent(theTableLevel1);
                EditorPanel.EditorComponent theChildLevel1 = new EditorPanel.EditorComponent(theTableLevel1, theAngleLevel1, r1, theButtonLevel1);
                editorPanel.add(theChildLevel1);

                theComponentMap.put(theTableLevel1, theChildLevel1);

                theAngleLevel1 += theIncrement1;
            }
        }

        for (Table theKnownTable : theAlreadyKnown) {
            for (Relation theRelation : model.getRelations().getForeignKeysFor(theKnownTable)) {
                EditorPanel.EditorComponent theFrom = theComponentMap.get(theRelation.getExportingTable());
                EditorPanel.EditorComponent theTo = theComponentMap.get(theRelation.getImportingTable());
                if (theFrom != null && theTo != null && theFrom != theTo && !editorPanel.hasConnection(theFrom, theTo)) {
                    editorPanel.add(new EditorPanel.Connector(theFrom, theTo));
                }
            }
        }

        editorPanel.invalidate();
        editorPanel.repaint();
        editorPanel.explodeAnimation();
    }
}
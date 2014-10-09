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

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.model.*;
import de.erdesignerng.visual.DisplayLevel;
import de.erdesignerng.visual.DisplayOrder;
import de.erdesignerng.visual.common.ERDesignerComponent;
import de.erdesignerng.visual.common.GenericModelEditor;
import de.erdesignerng.visual.common.ToolEnum;
import de.erdesignerng.visual.common.ZoomInfo;
import de.erdesignerng.visual.jgraph.export.Exporter;
import de.mogwai.common.client.looks.UIInitializer;
import de.mogwai.common.client.looks.components.menu.DefaultMenu;
import de.mogwai.common.i18n.ResourceHelper;
import de.mogwai.common.i18n.ResourceHelperProvider;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
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
    private final JPanel mainPanel;
    private final JCheckBox includeIncoming;
    private final JCheckBox includeOutgoing;
    private final JLabel currentElement;
    private final EditorPanel editorPanel;

    public Java2DEditor() {

        editorPanel = new EditorPanel() {
            @Override
            public void componentClicked(EditorComponent aComponent, MouseEvent aEvent) {
                Java2DEditor.this.componentClicked(aComponent, aEvent);
            }

            @Override
            protected JComponent getHighlightComponentFor(EditorComponent aComponent) {
                ModelItem theItem = (ModelItem) aComponent.userObject;
                if (theItem instanceof Table) {
                    return new TableComponent((Table) theItem, true);
                }
                if (theItem instanceof View) {
                    return new ViewComponent((View) theItem);
                }
                throw new IllegalArgumentException();
            }
        };

        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(editorPanel, BorderLayout.CENTER);

        ResourceHelper theHelper = ResourceHelper.getResourceHelper(ERDesignerBundle.BUNDLE_NAME);

        JPanel bottom = new JPanel();
        bottom.setLayout(new FlowLayout(FlowLayout.LEFT));
        includeIncoming = new JCheckBox(theHelper.getText(ERDesignerBundle.INCLUDEINCOMINGRELATIONS));
        includeOutgoing = new JCheckBox(theHelper.getText(ERDesignerBundle.INCLUDEOUTGOINGRELATIONS));
        currentElement = new JLabel();

        ActionListener theUpdateActionListener = e -> setSelectedObject(currentModelItem);

        includeIncoming.setSelected(true);
        includeIncoming.addActionListener(theUpdateActionListener);
        includeOutgoing.setSelected(false);
        includeOutgoing.addActionListener(theUpdateActionListener);

        currentElement.setMinimumSize(new Dimension(300, 21));
        currentElement.setPreferredSize(new Dimension(300, 21));

        bottom.add(currentElement);
        bottom.add(includeIncoming);
        bottom.add(includeOutgoing);

        mainPanel.add(bottom, BorderLayout.SOUTH);

        UIInitializer.getInstance().initialize(mainPanel);
    }

    protected void componentClicked(EditorPanel.EditorComponent aComponent, MouseEvent aEvent) {
        setSelectedObject((ModelItem) aComponent.userObject);
    }

    @Override
    public void repaintGraph() {
        mainPanel.invalidate();
        editorPanel.invalidate();
        mainPanel.doLayout();
        mainPanel.repaint();
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
        includeIncoming.setSelected(true);
        includeOutgoing.setSelected(false);
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
    public void setSelectedObject(ModelItem aItem) {
        if (aItem == null) {
            editorPanel.cleanup();
        } else {
            // We only show views and tables as the center of attention in the editor.
            if (aItem instanceof Index) {
                aItem = ((Index) aItem).getOwner();
            }
            if (aItem instanceof Attribute) {
                ModelItem theOwner = ((Attribute) aItem).getOwner();

                if (theOwner instanceof Table) {
                    aItem = theOwner;
                }
            }
            if (aItem instanceof Relation) {
                aItem = ((Relation) aItem).getImportingTable();
            }
            if (!(aItem instanceof Table) && !(aItem instanceof View)) {
                aItem = null;
            }

            if (aItem != currentModelItem) {

                editorPanel.cleanup();

                currentModelItem = aItem;

                if (aItem instanceof Table) {
                    generateGraphFor((Table) aItem);
                }
                if (aItem instanceof View) {
                    generateGraphFor((View) aItem);
                }

                editorPanel.invalidate();
                mainPanel.invalidate();
                mainPanel.repaint();
            }
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
        return mainPanel;
    }

    @Override
    public void addExportEntries(DefaultMenu aMenu, Exporter aExporter) {
    }

    @Override
    public boolean supportsZoom() {
        return false;
    }

    @Override
    public boolean supportsHandAction() {
        return true;
    }

    @Override
    public boolean supportsRelationAction() {
        return false;
    }

    @Override
    public boolean supportsCommentAction() {
        return false;
    }

    @Override
    public boolean supportsViewAction() {
        return false;
    }

    @Override
    public void initExportEntries(ResourceHelperProvider aProvider, DefaultMenu aExportMenu) {
        aExportMenu.setEnabled(false);
    }

    @Override
    public boolean supportsEntityAction() {
        return false;
    }

    @Override
    public boolean supportsGrid() {
        return false;
    }

    @Override
    public boolean supportsDisplayLevel() {
        return false;
    }

    @Override
    public boolean supportsSubjectAreas() {
        return false;
    }

    @Override
    public boolean supportsAttributeOrder() {
        return false;
    }

    @Override
    public boolean supportsDeletionOfObjects() {
        return false;
    }

    @Override
    public boolean supportShowingAndHidingOfRelations() {
        return false;
    }

    private void generateGraphFor(Table aTable) {

        Map<ModelItem, EditorPanel.EditorComponent> theComponentMap = new HashMap<>();

        TableComponent theButton = new TableComponent(aTable);
        EditorPanel.EditorComponent theRoot = new EditorPanel.EditorComponent(aTable, 0, 0, theButton, true);
        editorPanel.add(theRoot);

        theComponentMap.put(aTable, theRoot);

        int r1 = 250;

        List<Table> theAlreadyKnown = new ArrayList<>();
        theAlreadyKnown.add(aTable);

        // Level 1
        List<Relation> theIncomingRelationsLevel1 = new ArrayList<>();
        if (includeIncoming.isSelected()) {
            theIncomingRelationsLevel1.addAll(model.getRelations().getForeignKeysFor(aTable));
        }
        if (includeOutgoing.isSelected()) {
            theIncomingRelationsLevel1.addAll(model.getRelations().getExportedKeysFor(aTable));
        }
        List<Table> theTablesLevel1 = new ArrayList<>();
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

    private void generateGraphFor(View aView) {

        ViewComponent theButton = new ViewComponent(aView);
        EditorPanel.EditorComponent theRoot = new EditorPanel.EditorComponent(aView, 0, 0, theButton, true);
        editorPanel.add(theRoot);

        editorPanel.invalidate();
        editorPanel.repaint();
        editorPanel.explodeAnimation();
    }

    @Override
    public void initLayoutMenu(ERDesignerComponent aComponent, DefaultMenu aLayoutMenu) {
        aLayoutMenu.setEnabled(false);
    }

    @Override
    public void setIntelligentLayoutEnabled(boolean aStatus) {
    }

    @Override
    public boolean supportsIntelligentLayout() {
        return false;
    }
}
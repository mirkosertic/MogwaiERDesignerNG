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
            public void componentClicked(final EditorComponent aComponent, final MouseEvent aEvent) {
                Java2DEditor.this.componentClicked(aComponent, aEvent);
            }

            @Override
            protected JComponent getHighlightComponentFor(final EditorComponent aComponent) {
                final ModelItem theItem = (ModelItem) aComponent.userObject;
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

        final ResourceHelper theHelper = ResourceHelper.getResourceHelper(ERDesignerBundle.BUNDLE_NAME);

        final JPanel bottom = new JPanel();
        bottom.setLayout(new FlowLayout(FlowLayout.LEFT));
        includeIncoming = new JCheckBox(theHelper.getText(ERDesignerBundle.INCLUDEINCOMINGRELATIONS));
        includeOutgoing = new JCheckBox(theHelper.getText(ERDesignerBundle.INCLUDEOUTGOINGRELATIONS));
        currentElement = new JLabel();

        final ActionListener theUpdateActionListener = e -> setSelectedObject(currentModelItem);

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

    protected void componentClicked(final EditorPanel.EditorComponent aComponent, final MouseEvent aEvent) {
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
    public void commandSetDisplayLevel(final DisplayLevel aLevel) {
    }

    @Override
    public void commandSetDisplayOrder(final DisplayOrder aOrder) {
    }

    @Override
    public void commandHideSubjectArea(final SubjectArea aArea) {
    }

    @Override
    public void commandShowSubjectArea(final SubjectArea aArea) {
    }

    @Override
    public void commandSetTool(final ToolEnum aTool) {
    }

    @Override
    public void commandSetZoom(final ZoomInfo aZoomInfo) {
    }

    @Override
    public void setModel(final Model aModel) {
        model = aModel;
        includeIncoming.setSelected(true);
        includeOutgoing.setSelected(false);
        setSelectedObject(null);
    }

    @Override
    public void commandSetDisplayCommentsState(final boolean aState) {
    }

    @Override
    public void commandSetDisplayGridState(final boolean aState) {
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
                final ModelItem theOwner = ((Attribute) aItem).getOwner();

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
    public void commandAddToNewSubjectArea(final List<ModelItem> aItems) {
    }

    @Override
    public void commandDelete(final List<ModelItem> aItems) {
    }

    @Override
    public void commandCreateComment(final Comment aComment, final Point2D aLocation) {
    }

    @Override
    public void commandCreateRelation(final Relation aRelation) {
    }

    @Override
    public void commandCreateTable(final Table aTable, final Point2D aLocation) {
    }

    @Override
    public void commandCreateView(final View aView, final Point2D aLocation) {
    }

    @Override
    public void commandShowOrHideRelationsFor(final Table aTable, final boolean aShow) {
    }

    @Override
    public JComponent getDetailComponent() {
        return mainPanel;
    }

    @Override
    public void addExportEntries(final DefaultMenu aMenu, final Exporter aExporter) {
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
    public void initExportEntries(final ResourceHelperProvider aProvider, final DefaultMenu aExportMenu) {
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

    private void generateGraphFor(final Table aTable) {

        final Map<ModelItem, EditorPanel.EditorComponent> theComponentMap = new HashMap<>();

        final TableComponent theButton = new TableComponent(aTable);
        final EditorPanel.EditorComponent theRoot = new EditorPanel.EditorComponent(aTable, 0, 0, theButton, true);
        editorPanel.add(theRoot);

        theComponentMap.put(aTable, theRoot);

        final int r1 = 250;

        final List<Table> theAlreadyKnown = new ArrayList<>();
        theAlreadyKnown.add(aTable);

        // Level 1
        final List<Relation> theIncomingRelationsLevel1 = new ArrayList<>();
        if (includeIncoming.isSelected()) {
            theIncomingRelationsLevel1.addAll(model.getRelations().getForeignKeysFor(aTable));
        }
        if (includeOutgoing.isSelected()) {
            theIncomingRelationsLevel1.addAll(model.getRelations().getExportedKeysFor(aTable));
        }
        final List<Table> theTablesLevel1 = new ArrayList<>();
        for (final Relation theRelation : theIncomingRelationsLevel1) {
            if (!theTablesLevel1.contains(theRelation.getExportingTable()) && !theAlreadyKnown.contains(theRelation.getExportingTable())) {
                theTablesLevel1.add(theRelation.getExportingTable());
                theAlreadyKnown.add(theRelation.getExportingTable());
            }
            if (!theTablesLevel1.contains(theRelation.getImportingTable()) && !theAlreadyKnown.contains(theRelation.getImportingTable())) {
                theTablesLevel1.add(theRelation.getImportingTable());
                theAlreadyKnown.add(theRelation.getImportingTable());
            }
        }

        if (!theTablesLevel1.isEmpty()) {
            final float theIncrement1 = 360 / theTablesLevel1.size();
            float theAngleLevel1 = 0;

            for (final Table theTableLevel1 : theTablesLevel1) {

                final TableComponent theButtonLevel1 = new TableComponent(theTableLevel1);
                final EditorPanel.EditorComponent theChildLevel1 = new EditorPanel.EditorComponent(theTableLevel1, theAngleLevel1, r1, theButtonLevel1);
                editorPanel.add(theChildLevel1);

                theComponentMap.put(theTableLevel1, theChildLevel1);

                theAngleLevel1 += theIncrement1;
            }
        }

        for (final Table theKnownTable : theAlreadyKnown) {
            for (final Relation theRelation : model.getRelations().getForeignKeysFor(theKnownTable)) {
                final EditorPanel.EditorComponent theFrom = theComponentMap.get(theRelation.getExportingTable());
                final EditorPanel.EditorComponent theTo = theComponentMap.get(theRelation.getImportingTable());
                if (theFrom != null && theTo != null && theFrom != theTo && !editorPanel.hasConnection(theFrom, theTo)) {
                    editorPanel.add(new EditorPanel.Connector(theFrom, theTo));
                }
            }
        }

        editorPanel.invalidate();
        editorPanel.repaint();
        editorPanel.explodeAnimation();
    }

    private void generateGraphFor(final View aView) {

        final ViewComponent theButton = new ViewComponent(aView);
        final EditorPanel.EditorComponent theRoot = new EditorPanel.EditorComponent(aView, 0, 0, theButton, true);
        editorPanel.add(theRoot);

        editorPanel.invalidate();
        editorPanel.repaint();
        editorPanel.explodeAnimation();
    }

    @Override
    public void initLayoutMenu(final ERDesignerComponent aComponent, final DefaultMenu aLayoutMenu) {
        aLayoutMenu.setEnabled(false);
    }

    @Override
    public void setIntelligentLayoutEnabled(final boolean aStatus) {
    }

    @Override
    public boolean supportsIntelligentLayout() {
        return false;
    }
}
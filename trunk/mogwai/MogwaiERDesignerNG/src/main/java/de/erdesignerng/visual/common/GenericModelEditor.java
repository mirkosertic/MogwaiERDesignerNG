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

import de.erdesignerng.model.Comment;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.ModelItem;
import de.erdesignerng.model.Relation;
import de.erdesignerng.model.SubjectArea;
import de.erdesignerng.model.Table;
import de.erdesignerng.model.View;
import de.erdesignerng.visual.DisplayLevel;
import de.erdesignerng.visual.DisplayOrder;
import de.erdesignerng.visual.jgraph.export.Exporter;
import de.mogwai.common.client.looks.components.menu.DefaultMenu;
import de.mogwai.common.i18n.ResourceHelperProvider;

import javax.swing.JComponent;
import java.awt.geom.Point2D;
import java.util.List;

/**
 * Interface for Model editors.
 * <p/>
 * Every Model Editor must implement this interface.
 */
public interface GenericModelEditor {
    /**
     * Repaint the current graph.
     */
    void repaintGraph();

    /**
     * Set the current display level.
     *
     * @param aLevel the level
     */
    void commandSetDisplayLevel(DisplayLevel aLevel);

    /**
     * Set the current display order.
     *
     * @param aOrder the display order
     */
    void commandSetDisplayOrder(DisplayOrder aOrder);

    /**
     * Hide a specific subject area.
     *
     * @param aArea the area
     */
    void commandHideSubjectArea(SubjectArea aArea);

    /**
     * Show a specific subject area.
     *
     * @param aArea the subject area to show
     */
    void commandShowSubjectArea(SubjectArea aArea);

    /**
     * Set the current editing tool.
     *
     * @param aTool the tool
     */
    void commandSetTool(ToolEnum aTool);

    void commandSetZoom(ZoomInfo aZoomInfo);

    void setModel(Model model);

    /**
     * Toggle the include comments view state.
     *
     * @param aState true if comments shall be displayed, else false
     */
    void commandSetDisplayCommentsState(boolean aState);

    /**
     * Toggle the include comments view state.
     *
     * @param aState true if comments shall be displayed, else false
     */
    void commandSetDisplayGridState(boolean aState);

    /**
     * The preferences where changed, so they need to be reloaded.
     */
    void refreshPreferences();

    /**
     * Hook method. Will be called if a cell was successfully edited.
     */
    void commandNotifyAboutEdit();

    /**
     * Set the status of the intelligent layout functionality.
     *
     * @param aStatus true if enabled, else false
     */
    void setIntelligentLayoutEnabled(boolean aStatus);

    /**
     * Set the currently selected cell depending on its user object.
     *
     * @param aItem the user object.
     */
    void setSelectedObject(ModelItem aItem);

    /**
     * Add a list of items to a new subject area.
     *
     * @param aItems the items to be added
     */
    void commandAddToNewSubjectArea(List<ModelItem> aItems);

    void commandDelete(List<ModelItem> aItems);

    void commandCreateComment(Comment aComment, Point2D aLocation);

    void commandCreateRelation(Relation aRelation);

    void commandCreateTable(Table aTable, Point2D aLocation);

    void commandCreateView(View aView, Point2D aLocation);

    void commandShowOrHideRelationsFor(Table aTable, boolean aShow);

    JComponent getDetailComponent();

    void addExportEntries(DefaultMenu aMenu, Exporter aExporter);

    boolean supportsZoom();

    boolean supportsHandAction();

    boolean supportsRelationAction();

    boolean supportsCommentAction();

    boolean supportsViewAction();

    boolean supportsIntelligentLayout();

    void initExportEntries(ResourceHelperProvider aProvider, DefaultMenu aExportMenu);

    boolean supportsEntityAction();

    boolean supportsGrid();

    boolean supportsDisplayLevel();

    boolean supportsSubjectAreas();

    boolean supportsAttributeOrder();

    boolean supportsDeletionOfObjects();

    boolean supportShowingAndHidingOfRelations();

    void initLayoutMenu(ERDesignerComponent aComponent, DefaultMenu aLayoutMenu);
}

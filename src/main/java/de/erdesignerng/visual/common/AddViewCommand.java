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

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import de.erdesignerng.model.SubjectArea;
import de.erdesignerng.model.View;
import de.erdesignerng.modificationtracker.VetoException;
import de.erdesignerng.visual.cells.SubjectAreaCell;
import de.erdesignerng.visual.cells.ViewCell;
import de.erdesignerng.visual.editor.DialogConstants;
import de.erdesignerng.visual.editor.view.ViewEditor;

public class AddViewCommand extends UICommand {

    private Point2D location;

    public AddViewCommand(ERDesignerComponent component, Point2D aLocation) {
        super(component);
        location = aLocation;
    }

    @Override
    public void execute() {
        if (!component.checkForValidConnection()) {
            return;
        }

        View theView = new View();
        ViewEditor theEditor = new ViewEditor(component.getModel(), getDetailComponent());
        theEditor.initializeFor(theView);
        if (theEditor.showModal() == DialogConstants.MODAL_RESULT_OK) {
            try {

                try {
                    theEditor.applyValues();
                } catch (VetoException e) {
                    getWorldConnector().notifyAboutException(e);
                    return;
                }

                ViewCell theCell = new ViewCell(theView);
                theCell.transferPropertiesToAttributes(theView);

                Object theTargetCell = component.graph.getFirstCellForLocation(location.getX(), location.getY());
                if (theTargetCell instanceof SubjectAreaCell) {
                    SubjectAreaCell theSACell = (SubjectAreaCell) theTargetCell;
                    SubjectArea theArea = (SubjectArea) theSACell.getUserObject();
                    theArea.getViews().add(theView);

                    theSACell.add(theCell);
                }

                theCell.setBounds(new Rectangle2D.Double(location.getX(), location.getY(), -1, -1));

                component.graph.getGraphLayoutCache().insert(theCell);

                theCell.transferAttributesToProperties(theCell.getAttributes());

                component.graph.doLayout();

            } catch (Exception e) {
                getWorldConnector().notifyAboutException(e);
            }
        }
    }
}
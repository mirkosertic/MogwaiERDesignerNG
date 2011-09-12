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

import de.erdesignerng.model.Table;
import de.erdesignerng.modificationtracker.VetoException;
import de.erdesignerng.visual.editor.DialogConstants;
import de.erdesignerng.visual.editor.table.TableEditor;

import java.awt.geom.Point2D;

public class AddTableCommand extends UICommand {

    private final Point2D location;

    private final Table exportingCell;

    private final boolean newTableIsChild;

    public AddTableCommand(Point2D aLocation, Table aExportingCell,
                           boolean aNewTableIsChild) {
        location = aLocation;
        exportingCell = aExportingCell;
        newTableIsChild = aNewTableIsChild;
    }

    @Override
    public void execute() {

        ERDesignerComponent component = ERDesignerComponent.getDefault();

        if (!component.checkForValidConnection()) {
            return;
        }

        Table theTable = new Table();
        TableEditor theTableEditor = new TableEditor(component.getModel(), getDetailComponent());
        theTableEditor.initializeFor(theTable);
        if (theTableEditor.showModal() == DialogConstants.MODAL_RESULT_OK) {
            try {

                try {
                    theTableEditor.applyValues();
                } catch (VetoException e) {
                    getWorldConnector().notifyAboutException(e);
                    return;
                }

                component.commandCreateTable(theTable, location);

                if (exportingCell != null) {

                    // If the user cancels the add relation dialog
                    // the table is added, too
                    if (newTableIsChild) {
                        new AddRelationCommand(theTable, exportingCell).execute();
                    } else {
                        new AddRelationCommand(exportingCell, theTable).execute();
                    }
                }

                refreshDisplayAndOutline();

            } catch (Exception e) {
                getWorldConnector().notifyAboutException(e);
            }
        }
    }
}

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

import de.erdesignerng.util.ConnectionDescriptor;
import de.erdesignerng.visual.editor.DialogConstants;
import de.erdesignerng.visual.editor.connection.DatabaseConnectionEditor;

public class DBConnectionCommand extends UICommand {

    public DBConnectionCommand() {
    }

    @Override
    public void execute() {

        ERDesignerComponent component = ERDesignerComponent.getDefault();
        execute(component.getModel().createConnectionHistoryEntry());
    }

    public void execute(ConnectionDescriptor aConnection) {

        ERDesignerComponent component = ERDesignerComponent.getDefault();
        DatabaseConnectionEditor theEditor = new DatabaseConnectionEditor(
                getDetailComponent(), component.getModel(), aConnection);
        if (theEditor.showModal() == DialogConstants.MODAL_RESULT_OK) {
            try {
                theEditor.applyValues();
                component.addCurrentConnectionToConnectionHistory();

            } catch (Exception e) {
                getWorldConnector().notifyAboutException(e);
            }
        }
    }
}
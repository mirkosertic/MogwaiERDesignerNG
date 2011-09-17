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

import de.erdesignerng.model.View;
import de.erdesignerng.visual.editor.table.TableEditor;
import de.erdesignerng.visual.editor.view.ViewEditor;

public class EditViewCommand extends UICommand {

    private final View view;

    public EditViewCommand(View aTable) {
        view = aTable;
    }

    protected void beforeRefresh() {
    }

    @Override
    public void execute() {
        ERDesignerComponent component = ERDesignerComponent.getDefault();
        ViewEditor theEditor = new ViewEditor(component.getModel(), component.getDetailComponent());
        theEditor.initializeFor(view);
        if (theEditor.showModal() == TableEditor.MODAL_RESULT_OK) {
            try {
                theEditor.applyValues();

                beforeRefresh();

                refreshDisplayAndOutline();
            } catch (Exception e) {
                getWorldConnector().notifyAboutException(e);
            }
        }
    }
}
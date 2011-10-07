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
import de.erdesignerng.model.View;
import de.erdesignerng.visual.UsageDataCollector;
import de.erdesignerng.visual.editor.databrowser.DataBrowserEditor;
import de.erdesignerng.visual.editor.table.TableEditor;

public class DataBrowserCommand extends UICommand {

    private Table table;
    private View view;

    public DataBrowserCommand(Table aTable) {
        table = aTable;
    }

    public DataBrowserCommand(View aView) {
        view = aView;
    }

    @Override
    public void execute() {

        UsageDataCollector.getInstance().addExecutedUsecase(UsageDataCollector.Usecase.DATABROWSER);

        ERDesignerComponent component = ERDesignerComponent.getDefault();
        DataBrowserEditor theEditor = new DataBrowserEditor(component
                .getDetailComponent());
        if (table != null) {
            theEditor.initializeFor(table);
        } else {
            theEditor.initializeFor(view);
        }

        if (theEditor.showModal() == TableEditor.MODAL_RESULT_OK) {
            try {
                theEditor.applyValues();

            } catch (Exception e) {
                getWorldConnector().notifyAboutException(e);
            }
        }
    }
}
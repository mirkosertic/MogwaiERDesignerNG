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

import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.Index;
import de.erdesignerng.model.Table;
import de.erdesignerng.visual.editor.table.TableEditor;

public class EditTableCommand extends UICommand {

    private final Table table;

    private Attribute<Table> attribute;

    private Index index;

    public EditTableCommand(Table aTable) {
        table = aTable;
    }

    public EditTableCommand(Table aTable, Attribute<Table> aAttribute) {
        table = aTable;
        attribute = aAttribute;
    }

    public EditTableCommand(Table aTable, Index aIndex) {
        table = aTable;
        index = aIndex;
    }

    protected void beforeRefresh() {
    }

    @Override
    public void execute() {

        ERDesignerComponent component = ERDesignerComponent.getDefault();
        TableEditor theEditor = new TableEditor(component.getModel(), component.getDetailComponent());
        theEditor.initializeFor(table);
        if (attribute != null) {
            theEditor.setSelectedAttribute(attribute);
        }
        if (index != null) {
            theEditor.setSelectedIndex(index);
        }
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

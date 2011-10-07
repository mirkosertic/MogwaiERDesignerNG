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

import de.erdesignerng.model.CustomType;
import de.erdesignerng.visual.editor.DialogConstants;
import de.erdesignerng.visual.editor.customtypes.CustomTypeEditor;

/**
 * @author $Author: de_death $
 * @version $Date: 2010-04-05 23:30:00 $
 */
public class EditCustomTypesCommand extends UICommand {

    private final CustomType customType;

    public EditCustomTypesCommand() {
        this(null);
    }

    public EditCustomTypesCommand(CustomType aCustomType) {
        customType = aCustomType;
    }

    @Override
    public void execute() {

        ERDesignerComponent component = ERDesignerComponent.getDefault();
        CustomTypeEditor theEditor = new CustomTypeEditor(component.getModel(), getDetailComponent());
        if (customType != null) {
            theEditor.setSelectedType(customType);
        }
        if (theEditor.showModal() == DialogConstants.MODAL_RESULT_OK) {
            try {
                theEditor.applyValues();

                refreshDisplayAndOutline();
            } catch (Exception e) {
                getWorldConnector().notifyAboutException(e);
            }
        }
    }
}
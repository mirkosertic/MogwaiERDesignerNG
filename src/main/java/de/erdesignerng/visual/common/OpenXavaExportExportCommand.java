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

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.visual.UsageDataCollector;
import de.erdesignerng.visual.editor.DialogConstants;
import de.erdesignerng.visual.editor.openxavaexport.OpenXavaExportEditor;

public class OpenXavaExportExportCommand extends UICommand {

    public OpenXavaExportExportCommand() {
    }

    @Override
    public void execute() {

        UsageDataCollector.getInstance().addExecutedUsecase(UsageDataCollector.Usecase.OPENXAVA_EXPORT);

        ERDesignerComponent component = ERDesignerComponent.getDefault();
        OpenXavaExportEditor theEditor = new OpenXavaExportEditor(component.getModel(), getDetailComponent());
        if (theEditor.showModal() == DialogConstants.MODAL_RESULT_OK) {
            try {
                theEditor.applyValues();

                getWorldConnector().setStatusText(
                        component.getResourceHelper().getText(ERDesignerBundle.OPENXAVAEXPORTOK));
            } catch (Exception e) {
                getWorldConnector().notifyAboutException(e);
            }
        }
    }
}
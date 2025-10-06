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

import de.erdesignerng.model.Domain;
import de.erdesignerng.visual.editor.DialogConstants;
import de.erdesignerng.visual.editor.domain.DomainEditor;

public class EditDomainCommand extends UICommand {

    private final Domain domain;

    public EditDomainCommand() {
        this(null);
    }

    public EditDomainCommand(final Domain aDomain) {
        domain = aDomain;
    }

    @Override
    public void execute() {

        final ERDesignerComponent component = ERDesignerComponent.getDefault();
        final DomainEditor theEditor = new DomainEditor(component.getModel(), getDetailComponent());
        if (domain != null) {
            theEditor.setSelectedDomain(domain);
        }
        if (theEditor.showModal() == DialogConstants.MODAL_RESULT_OK) {
            try {
                theEditor.applyValues();

                refreshDisplayAndOutline();
            } catch (final Exception e) {
                getWorldConnector().notifyAboutException(e);
            }
        }
    }
}
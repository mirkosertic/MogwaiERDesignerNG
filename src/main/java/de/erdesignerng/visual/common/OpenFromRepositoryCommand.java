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
import de.erdesignerng.dialect.Dialect;
import de.erdesignerng.dialect.DialectFactory;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.ModelIOUtilities;
import de.erdesignerng.model.serializer.repository.RepositoryEntryDescriptor;
import de.erdesignerng.util.ApplicationPreferences;
import de.erdesignerng.util.ConnectionDescriptor;
import de.erdesignerng.visual.MessagesHelper;
import de.erdesignerng.visual.editor.DialogConstants;
import de.erdesignerng.visual.editor.repository.LoadFromRepositoryEditor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class OpenFromRepositoryCommand extends UICommand {

    public OpenFromRepositoryCommand() {
    }

    @Override
    public void execute() {

        final ERDesignerComponent component = ERDesignerComponent.getDefault();
        final ConnectionDescriptor theRepositoryConnection = ApplicationPreferences
                .getInstance().getRepositoryConnection();
        if (theRepositoryConnection == null) {
            MessagesHelper.displayErrorMessage(getDetailComponent(), component
                    .getResourceHelper().getText(
                            ERDesignerBundle.ERRORINREPOSITORYCONNECTION));
            return;
        }
        Connection theConnection = null;
        final Dialect theDialect = DialectFactory.getInstance().getDialect(
                theRepositoryConnection.getDialect());
        try {
            theConnection = theDialect.createConnection(ApplicationPreferences
                    .getInstance().createDriverClassLoader(),
                    theRepositoryConnection.getDriver(),
                    theRepositoryConnection.getUrl(), theRepositoryConnection
                    .getUsername(), theRepositoryConnection
                    .getPassword(), false);

            final List<RepositoryEntryDescriptor> theEntries = ModelIOUtilities
                    .getInstance().getRepositoryEntries(theDialect,
                            theConnection);

            final LoadFromRepositoryEditor theEditor = new LoadFromRepositoryEditor(
                    getDetailComponent(), theEntries);
            if (theEditor.showModal() == DialogConstants.MODAL_RESULT_OK) {

                final RepositoryEntryDescriptor theDescriptor = theEditor.getModel()
                        .getEntry();

                final Model theModel = ModelIOUtilities.getInstance()
                        .deserializeModelFromRepository(theDescriptor,
                                theDialect, theConnection);
                getWorldConnector().initializeLoadedModel(theModel);

                component.setupViewFor(theDescriptor);
                getWorldConnector().setStatusText(
                        component.getResourceHelper().getText(
                                ERDesignerBundle.FILELOADED));

                component.currentRepositoryEntry = theDescriptor;
                component.currentEditingFile = null;

                component.setModel(theModel);
            }

        } catch (final Exception e) {
            getWorldConnector().notifyAboutException(e);
        } finally {
            if (theConnection != null
                    && !theDialect.generatesManagedConnection()) {
                try {
                    theConnection.close();
                } catch (final SQLException e) {
                    // Do nothing here
                }
            }
        }
    }
}
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
import de.erdesignerng.model.ModelIOUtilities;
import de.erdesignerng.model.serializer.repository.RepositoryEntryDescriptor;
import de.erdesignerng.util.ApplicationPreferences;
import de.erdesignerng.util.ConnectionDescriptor;
import de.erdesignerng.util.JDBCUtils;
import de.erdesignerng.visual.MessagesHelper;
import de.erdesignerng.visual.UsageDataCollector;
import de.erdesignerng.visual.editor.DialogConstants;
import de.erdesignerng.visual.editor.repository.SaveToRepositoryEditor;

import java.sql.Connection;
import java.util.List;

public class SaveToRepositoryCommand extends UICommand {

    public SaveToRepositoryCommand() {
    }

    @Override
    public void execute() {

        UsageDataCollector.getInstance().addExecutedUsecase(UsageDataCollector.Usecase.SAVE_TO_REPOSITORY);

        ERDesignerComponent component = ERDesignerComponent.getDefault();

        ConnectionDescriptor theRepositoryConnection = ApplicationPreferences
                .getInstance().getRepositoryConnection();
        if (theRepositoryConnection == null) {
            MessagesHelper.displayErrorMessage(getDetailComponent(), component
                    .getResourceHelper().getText(
                            ERDesignerBundle.ERRORINREPOSITORYCONNECTION));
            return;
        }
        Connection theConnection = null;
        Dialect theDialect = DialectFactory.getInstance().getDialect(
                theRepositoryConnection.getDialect());
        try {
            theConnection = theDialect.createConnection(ApplicationPreferences
                    .getInstance().createDriverClassLoader(),
                    theRepositoryConnection.getDriver(),
                    theRepositoryConnection.getUrl(), theRepositoryConnection
                    .getUsername(), theRepositoryConnection
                    .getPassword(), false);

            List<RepositoryEntryDescriptor> theEntries = ModelIOUtilities
                    .getInstance().getRepositoryEntries(theDialect,
                            theConnection);

            SaveToRepositoryEditor theEditor = new SaveToRepositoryEditor(
                    getDetailComponent(), theEntries,
                    component.currentRepositoryEntry);
            if (theEditor.showModal() == DialogConstants.MODAL_RESULT_OK) {

                RepositoryEntryDescriptor theDesc = theEditor
                        .getRepositoryDescriptor();

                theDesc = ModelIOUtilities.getInstance().serializeModelToDB(
                        theDesc, theDialect, theConnection,
                        component.getModel());

                component.setupViewFor(theDesc);
                getWorldConnector().setStatusText(
                        component.getResourceHelper().getText(
                                ERDesignerBundle.FILESAVED));

            }
        } catch (Exception e) {
            getWorldConnector().notifyAboutException(e);
        } finally {
            if (theConnection != null
                    && !theDialect.generatesManagedConnection()) {
                JDBCUtils.closeQuietly(theConnection);
            }
        }
    }
}
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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.dialect.Dialect;
import de.erdesignerng.dialect.DialectFactory;
import de.erdesignerng.model.ModelIOUtilities;
import de.erdesignerng.model.serializer.repository.RepositoryEntryDesciptor;
import de.erdesignerng.util.ConnectionDescriptor;
import de.erdesignerng.visual.MessagesHelper;
import de.erdesignerng.visual.editor.DialogConstants;
import de.erdesignerng.visual.editor.repository.SaveToRepositoryEditor;

public class SaveToRepositoryCommand extends UICommand {

    public SaveToRepositoryCommand(ERDesignerComponent component) {
        super(component);
    }

    @Override
    public void execute() {
        ConnectionDescriptor theRepositoryConnection = component.preferences.getRepositoryConnection();
        if (theRepositoryConnection == null) {
            MessagesHelper.displayErrorMessage(component.scrollPane, component.getResourceHelper().getText(
                    ERDesignerBundle.ERRORINREPOSITORYCONNECTION));
            return;
        }
        Connection theConnection = null;
        Dialect theDialect = DialectFactory.getInstance().getDialect(theRepositoryConnection.getDialect());
        try {

            component.setIntelligentLayoutEnabled(false);

            theConnection = theDialect.createConnection(component.preferences.createDriverClassLoader(),
                    theRepositoryConnection.getDriver(), theRepositoryConnection.getUrl(), theRepositoryConnection
                            .getUsername(), theRepositoryConnection.getPassword(), false);

            List<RepositoryEntryDesciptor> theEntries = ModelIOUtilities.getInstance().getRepositoryEntries(theDialect,
                    theConnection);

            SaveToRepositoryEditor theEditor = new SaveToRepositoryEditor(component.scrollPane, theEntries,
                    component.currentRepositoryEntry);
            if (theEditor.showModal() == DialogConstants.MODAL_RESULT_OK) {
                try {

                    RepositoryEntryDesciptor theDesc = theEditor.getRepositoryDescriptor();

                    theDesc = ModelIOUtilities.getInstance().serializeModelToDB(theDesc, theDialect, theConnection,
                            component.model, component.preferences);

                    component.setupViewFor(theDesc);
                    component.worldConnector.setStatusText(component.getResourceHelper().getText(
                            ERDesignerBundle.FILESAVED));

                } catch (Exception e) {
                    component.worldConnector.notifyAboutException(e);
                }
            }
        } catch (Exception e) {
            component.worldConnector.notifyAboutException(e);
        } finally {
            if (theConnection != null && !theDialect.generatesManagedConnection()) {
                try {
                    theConnection.close();
                } catch (SQLException e) {
                    // Do nothing here
                }
            }

            component.setIntelligentLayoutEnabled(component.preferences.isIntelligentLayout());
        }
    }
}

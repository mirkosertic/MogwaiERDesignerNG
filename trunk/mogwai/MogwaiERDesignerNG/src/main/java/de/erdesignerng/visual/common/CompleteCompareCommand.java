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
import java.text.MessageFormat;

import de.erdesignerng.dialect.ReverseEngineeringNotifier;
import de.erdesignerng.dialect.ReverseEngineeringOptions;
import de.erdesignerng.dialect.ReverseEngineeringStrategy;
import de.erdesignerng.model.Model;
import de.erdesignerng.visual.LongRunningTask;
import de.erdesignerng.visual.editor.DialogConstants;
import de.erdesignerng.visual.editor.completecompare.CompleteCompareEditor;
import de.erdesignerng.visual.editor.reverseengineer.ReverseEngineerEditor;

public class CompleteCompareCommand extends UICommand {

    public CompleteCompareCommand(ERDesignerComponent component) {
        super(component);
    }

    @Override
    void execute() {
        if (!component.checkForValidConnection()) {
            return;
        }

        final ReverseEngineerEditor theEditor = new ReverseEngineerEditor(component.model, component.scrollPane,
                component.preferences);
        if (theEditor.showModal() == DialogConstants.MODAL_RESULT_OK) {

            try {
                final Connection theConnection = component.model.createConnection(component.preferences);
                if (theConnection == null) {
                    return;
                }
                final ReverseEngineeringStrategy theStrategy = component.model.getDialect()
                        .getReverseEngineeringStrategy();
                final ReverseEngineeringOptions theOptions = theEditor.createREOptions();

                final Model theDatabaseModel = component.worldConnector.createNewModel();
                theDatabaseModel.setDialect(component.model.getDialect());
                theDatabaseModel.getProperties().copyFrom(component.model);

                LongRunningTask<Model> theTask = new LongRunningTask<Model>(component.worldConnector) {

                    @Override
                    public Model doWork(final MessagePublisher aPublisher) throws Exception {
                        theOptions.getTableEntries().addAll(
                                theStrategy.getTablesForSchemas(theConnection, theOptions.getSchemaEntries()));

                        ReverseEngineeringNotifier theNotifier = new ReverseEngineeringNotifier() {

                            public void notifyMessage(String aResourceKey, String... aValues) {
                                String theMessage = MessageFormat.format(component.getResourceHelper().getText(
                                        aResourceKey), (Object[]) aValues);
                                aPublisher.publishMessage(theMessage);
                            }

                        };

                        theStrategy.updateModelFromConnection(theDatabaseModel, component.worldConnector,
                                theConnection, theOptions, theNotifier);

                        return theDatabaseModel;

                    }

                    @Override
                    public void handleResult(Model aResultModel) {
                        component.addConnectionToConnectionHistory(theDatabaseModel.createConnectionHistoryEntry());

                        CompleteCompareEditor theCompare = new CompleteCompareEditor(component.scrollPane,
                                component.model, aResultModel, component.preferences);
                        theCompare.showModal();
                    }

                    @Override
                    public void cleanup() throws SQLException {
                        if (!component.model.getDialect().generatesManagedConnection()) {
                            theConnection.close();
                        }
                    }
                };
                theTask.start();

            } catch (Exception e) {
                component.worldConnector.notifyAboutException(e);
            }
        }
    }
}
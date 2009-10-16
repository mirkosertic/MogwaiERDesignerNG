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
import de.erdesignerng.visual.editor.reverseengineer.ReverseEngineerEditor;
import de.erdesignerng.visual.editor.reverseengineer.TablesSelectEditor;

public class ReverseEngineerCommand extends UICommand {

    public ReverseEngineerCommand(ERDesignerComponent aComponent) {
        super(aComponent);
    }
    
    @Override
    public void execute() {
        if (!component.checkForValidConnection()) {
            return;
        }
        
        final Model theModel = component.getModel();
        
        final ReverseEngineerEditor theEditor = new ReverseEngineerEditor(theModel, getDetailComponent(), getPreferences());
        if (theEditor.showModal() == DialogConstants.MODAL_RESULT_OK) {

            component.setIntelligentLayoutEnabled(false);

            try {

                final Connection theConnection = theModel.createConnection(getPreferences());
                if (theConnection == null) {
                    return;
                }
                final ReverseEngineeringStrategy theStrategy = theModel.getDialect().getReverseEngineeringStrategy();

                LongRunningTask<ReverseEngineeringOptions> theRETask = new LongRunningTask<ReverseEngineeringOptions>(
                        getWorldConnector()) {

                    @Override
                    public ReverseEngineeringOptions doWork(MessagePublisher aMessagePublisher) throws Exception {
                        ReverseEngineeringOptions theOptions = theEditor.createREOptions();
                        theOptions.getTableEntries().addAll(
                                theStrategy.getTablesForSchemas(theConnection, theOptions.getSchemaEntries()));
                        return theOptions;
                    }

                    @Override
                    public void handleResult(final ReverseEngineeringOptions aResult) {
                        TablesSelectEditor theTablesEditor = new TablesSelectEditor(aResult, getDetailComponent());
                        if (theTablesEditor.showModal() == DialogConstants.MODAL_RESULT_OK) {

                            LongRunningTask<Model> theTask = new LongRunningTask<Model>(getWorldConnector()) {

                                @Override
                                public Model doWork(final MessagePublisher aPublisher) throws Exception {
                                    ReverseEngineeringNotifier theNotifier = new ReverseEngineeringNotifier() {

                                        public void notifyMessage(String aResourceKey, String... aValues) {
                                            String theMessage = MessageFormat.format(component.getResourceHelper().getText(
                                                    aResourceKey), (Object[]) aValues);
                                            aPublisher.publishMessage(theMessage);
                                        }

                                    };

                                    theStrategy.updateModelFromConnection(theModel, getWorldConnector(), theConnection,
                                            aResult, theNotifier);

                                    return theModel;
                                }

                                @Override
                                public void handleResult(Model aResultModel) {
                                    component.setModel(aResultModel);
                                }

                                @Override
                                public void cleanup() throws SQLException {
                                    if (!theModel.getDialect().generatesManagedConnection()) {
                                        theConnection.close();
                                    }
                                }

                            };
                            theTask.start();
                        }
                    }

                };
                theRETask.start();

            } catch (Exception e) {
                getWorldConnector().notifyAboutException(e);
            } finally {
                component.setIntelligentLayoutEnabled(getPreferences().isIntelligentLayout());
            }
        }
    }
}
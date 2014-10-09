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

import de.erdesignerng.dialect.JDBCReverseEngineeringStrategy;
import de.erdesignerng.dialect.ReverseEngineeringNotifier;
import de.erdesignerng.dialect.ReverseEngineeringOptions;
import de.erdesignerng.dialect.TableNamingEnum;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.ModelItem;
import de.erdesignerng.model.Table;
import de.erdesignerng.model.View;
import de.erdesignerng.util.ApplicationPreferences;
import de.erdesignerng.visual.LongRunningTask;
import de.erdesignerng.visual.editor.DialogConstants;
import de.erdesignerng.visual.editor.reverseengineer.ReverseEngineerEditor;
import de.erdesignerng.visual.editor.reverseengineer.TablesSelectEditor;
import de.erdesignerng.visual.jgraph.cells.views.TableCellView;
import de.erdesignerng.visual.jgraph.cells.views.ViewCellView;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

public class ReverseEngineerCommand extends UICommand {

	private static final Logger LOGGER = Logger.getLogger(JDBCReverseEngineeringStrategy.class);

	public ReverseEngineerCommand() {
	}

	@Override
	public void execute() {

		ERDesignerComponent component = ERDesignerComponent.getDefault();

		if (!component.checkForValidConnection()) {
			return;
		}

		final Model theModel = component.getModel();
		final JDBCReverseEngineeringStrategy theStrategy = theModel.getDialect().getReverseEngineeringStrategy();

		if (theModel.getDialect().isSupportsSchemaInformation()) {
			final ReverseEngineerEditor theEditor = new ReverseEngineerEditor(theModel, getDetailComponent());
			if (theEditor.showModal() == DialogConstants.MODAL_RESULT_OK) {

				try {

					final Connection theConnection = theModel.createConnection();
					if (theConnection == null) {
						return;
					}

					LongRunningTask<ReverseEngineeringOptions> theRETask = new LongRunningTask<ReverseEngineeringOptions>(getWorldConnector()) {

						@Override
						public ReverseEngineeringOptions doWork(MessagePublisher aMessagePublisher) throws Exception {
							ReverseEngineeringOptions theOptions = theEditor.createREOptions();
							theOptions.getTableEntries().addAll(theStrategy.getTablesForSchemas(theConnection, theOptions.getSchemaEntries()));

							return theOptions;
						}

						@Override
						public void handleResult(ReverseEngineeringOptions theOptions) {
							showTablesSelectEditor(theStrategy, theModel, theConnection, theOptions);
						}

					};

					theRETask.start();

				} catch (ClassNotFoundException | IllegalAccessException | InstantiationException | SQLException e) {
					getWorldConnector().notifyAboutException(e);
				}
			}
		} else {
			try {

				Connection theConnection = theModel.createConnection();
				if (theConnection == null) {
					return;
				}

				ReverseEngineeringOptions theOptions = new ReverseEngineeringOptions();
				theOptions.setTableNaming(TableNamingEnum.STANDARD);
				theOptions.getTableEntries().addAll(theStrategy.getTablesForSchemas(theConnection, theOptions.getSchemaEntries()));

				showTablesSelectEditor(theStrategy, theModel, theConnection, theOptions);

			} catch (ClassNotFoundException | IllegalAccessException | InstantiationException | SQLException e) {
				getWorldConnector().notifyAboutException(e);
			}
		}
	}

	// FR 2895534 [ERDesignerNG] show RevEngEd only on DBs with schema support
	private void showTablesSelectEditor(final JDBCReverseEngineeringStrategy aStrategy, final Model aModel, final Connection aConnection, final ReverseEngineeringOptions theOptions) {
		TablesSelectEditor theTablesEditor = new TablesSelectEditor(theOptions, getDetailComponent());

		if (theTablesEditor.showModal() == DialogConstants.MODAL_RESULT_OK) {

			LongRunningTask<Model> theTask = new LongRunningTask<Model>(getWorldConnector()) {

				@Override
				public Model doWork(final MessagePublisher aPublisher) throws Exception {
					ReverseEngineeringNotifier theNotifier = (aResourceKey, aValues) -> {
                        String theMessage = MessageFormat.format(ERDesignerComponent.getDefault().getResourceHelper().getText(aResourceKey), (Object[]) aValues);
                        aPublisher.publishMessage(theMessage);
                    };

					aStrategy.updateModelFromConnection(aModel, getWorldConnector(), aConnection, theOptions, theNotifier);

					// Iterate over the views and the tables and
					// order them in a matrix like position
					List<ModelItem> theItems = new ArrayList<>();
					theItems.addAll(aModel.getTables());
					theItems.addAll(aModel.getViews());
					int xoffset = 20;
					int yoffset = 20;
					int xcounter = 0;
					int maxheight = Integer.MIN_VALUE;

					for (ModelItem theItem : theItems) {
						Component theComponent = null;
						if (theItem instanceof Table) {
							theComponent = new TableCellView.MyRenderer().getRendererComponent((Table) theItem);
						}

						if (theItem instanceof View) {
							theComponent = new ViewCellView.MyRenderer().getRendererComponent((View) theItem);
						}
						Dimension theSize = theComponent.getPreferredSize();

						//check if PROPERTY_LOCATION is already set. This can be the case if reverse engineering into an existing model. Only set, if not present.
						if (theItem.getProperties().getProperty(ModelItem.PROPERTY_LOCATION) == null) {
							String theLocation = xoffset + ":" + yoffset;
							theItem.getProperties().setProperty(ModelItem.PROPERTY_LOCATION, theLocation);
						} else {
							LOGGER.info("graph layout properties for item '" + theItem.getName() + "' taken from previous model.");
						}

						maxheight = Math.max(maxheight, theSize.height);
						xoffset += theSize.width + 20;

						xcounter++;
						if (xcounter >= ApplicationPreferences.getInstance().getGridWidthAfterReverseEngineering()) {
							xcounter = 0;
							xoffset = 0;
							yoffset += maxheight + 20;
							maxheight = Integer.MIN_VALUE;
						}
					}

					return aModel;
				}

				@Override
				public void handleResult(final Model aResultModel) {
					try {
						// Make sure this is called in the EDT, as else JGraph might throw a NPE
						SwingUtilities.invokeAndWait(() -> ERDesignerComponent.getDefault().setModel(aResultModel));
					} catch (InterruptedException | InvocationTargetException e) {
						throw new RuntimeException("Cannot set model in editor", e);
					}
				}

				@Override
				public void cleanup() throws SQLException {
					if (!aModel.getDialect().generatesManagedConnection()) {
						aConnection.close();
					}
				}

			};

			theTask.start();
		}
	}
}
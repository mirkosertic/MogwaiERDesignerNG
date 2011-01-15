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
package de.erdesignerng.visual.editor.databrowser;

import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JTable;
import javax.swing.table.TableColumn;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.dialect.Dialect;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.Table;
import de.erdesignerng.model.View;
import de.erdesignerng.util.ApplicationPreferences;
import de.erdesignerng.visual.common.ERDesignerWorldConnector;
import de.erdesignerng.visual.editor.BaseEditor;
import de.erdesignerng.visual.editor.DialogConstants;
import de.mogwai.common.client.binding.BindingInfo;
import de.mogwai.common.client.looks.UIInitializer;
import de.mogwai.common.client.looks.components.action.ActionEventProcessor;
import de.mogwai.common.client.looks.components.action.DefaultAction;
import de.mogwai.common.client.looks.components.renderer.DefaultCellRenderer;

/**
 * DataBrowser.
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-09 19:07:29 $
 */
public class DataBrowserEditor extends BaseEditor {

	private final DefaultAction closeAction = new DefaultAction(
			new ActionEventProcessor() {

				public void processActionEvent(ActionEvent e) {
					commandClose();
				}
			}, this, ERDesignerBundle.CLOSE);

	private final DefaultAction queryAction = new DefaultAction(
			new ActionEventProcessor() {

				public void processActionEvent(ActionEvent e) {
					commandQuery();
				}
			}, this, ERDesignerBundle.QUERY);

	private DataBrowserEditorView view = new DataBrowserEditorView();

	private Model currentModel;
	private Dialect currentDialect;

	private BindingInfo<DataBrowserModel> sqlBindingInfo = new BindingInfo<DataBrowserModel>();
	private Connection connection;
	private Statement statement;

	private PaginationDataModel dataModel;

	public DataBrowserEditor(Component aParent,
			ERDesignerWorldConnector aConnector) {
		super(aParent, ERDesignerBundle.DATABROWSER);

		view.getCloseButton().setAction(closeAction);
		view.getQueryButton().setAction(queryAction);
		view.getData().setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		initialize();

		sqlBindingInfo.setDefaultModel(new DataBrowserModel());
		sqlBindingInfo.addBinding("sql", view.getSql(), true);
		sqlBindingInfo.configure();
	}

	public void initializeFor(Table aTable) {

		currentModel = aTable.getOwner();
		currentDialect = aTable.getOwner().getDialect();

		DataBrowserModel theModel = sqlBindingInfo.getDefaultModel();
		theModel.setSql(currentDialect.createSQLGenerator()
				.createSelectAllScriptFor(aTable));
		sqlBindingInfo.model2view();

		commandQuery();
	}

	public void initializeFor(View aView) {

		currentModel = aView.getOwner();
		currentDialect = aView.getOwner().getDialect();

		DataBrowserModel theModel = sqlBindingInfo.getDefaultModel();
		theModel.setSql(currentDialect.createSQLGenerator()
				.createSelectAllScriptFor(aView));
		sqlBindingInfo.model2view();

		commandQuery();
	}

	private void initialize() {

		setContentPane(view);
		setResizable(true);

		pack();

		setMinimumSize(getSize());
		ApplicationPreferences.getInstance().setWindowSize(
				getClass().getSimpleName(), this);

		UIInitializer.getInstance().initialize(this);
	}

	@Override
	public void applyValues() throws Exception {
	}

	private void commandQuery() {

		if (sqlBindingInfo.validate().size() == 0) {

			sqlBindingInfo.view2model();

			try {
				if (connection == null) {
					connection = currentModel.createConnection();
				}
				if (statement == null) {
					statement = connection.createStatement();
				}

				ResultSet theResult = statement.executeQuery(sqlBindingInfo
						.getDefaultModel().getSql());

				if (dataModel != null) {
					dataModel.cleanup();
				}

				dataModel = new PaginationDataModel(view.getData(), theResult);
				dataModel.seekToRow(10);

				view.getData().setModel(dataModel);
				view.getData().getTableHeader().setReorderingAllowed(false);

				dataModel
						.addSeekListener(new PaginationDataModel.SeekListener() {

							@Override
							public void seeked() {
								updateTableColumnWIdth();
							}
						});

				updateTableColumnWIdth();

			} catch (Exception e) {
				logFatalError(e);
			}
		}

	}

	private void updateTableColumnWIdth() {
		FontMetrics theMetrics = getFontMetrics(getFont());
		int theWWidth = theMetrics.stringWidth("w");

		for (int i = 0; i < dataModel.getColumnCount(); i++) {

			TableColumn theColumn = view.getData().getColumnModel()
					.getColumn(i);

			theColumn.setCellRenderer(DefaultCellRenderer.getInstance());

			int theTextWidth = dataModel.computeColumnWidth(i);
			int theHeaderWidth = theColumn.getHeaderValue().toString().length();

			theColumn.setPreferredWidth(theWWidth
					* Math.max(theTextWidth, theHeaderWidth));
		}
	}

	private void commandClose() {

		if (statement != null) {
			try {
				statement.close();
			} catch (SQLException e) {
				// Ignore this
			}
		}
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				// Ignore this
			}
		}

		ApplicationPreferences.getInstance().updateWindowSize(
				getClass().getSimpleName(), this);
		setModalResult(DialogConstants.MODAL_RESULT_OK);
	}
}
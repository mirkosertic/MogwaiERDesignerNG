package de.erdesignerng.visual.editor.connection;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.dialect.Dialect;
import de.erdesignerng.dialect.DialectFactory;
import de.erdesignerng.util.ApplicationPreferences;
import de.erdesignerng.util.ConnectionDescriptor;
import de.erdesignerng.visual.MessagesHelper;
import de.erdesignerng.visual.editor.BaseEditor;
import de.erdesignerng.visual.editor.DialogConstants;
import de.mogwai.common.client.binding.BindingInfo;
import de.mogwai.common.client.looks.UIInitializer;
import de.mogwai.common.client.looks.components.action.DefaultAction;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.List;

/**
 * Editor for the database connection.
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-13 15:40:33 $
 */
public class RepositoryConnectionEditor extends BaseEditor {

	private final DefaultAction testAction = new DefaultAction(
            e -> commandTest(), this, ERDesignerBundle.TEST);

	private final RepositoryConnectionEditorView view = new RepositoryConnectionEditorView() {

		@Override
		public void handleDialectChange(Dialect aDialect) {
			commandChangeDialect(aDialect);
		}
	};

	private final BindingInfo<DatabaseConnectionDatamodel> bindingInfo = new BindingInfo<>();

	public RepositoryConnectionEditor(Component aParent) {
		super(aParent, ERDesignerBundle.REPOSITORYCONNECTION);

		initialize();

		DefaultComboBoxModel theModel = new DefaultComboBoxModel();
		theModel.addElement(null);

		List<Dialect> theDialects = DialectFactory.getInstance()
				.getSupportedDialects();
        theDialects.forEach(theModel::addElement);

		view.getDialect().setModel(theModel);

		DatabaseConnectionDatamodel theDescriptor = new DatabaseConnectionDatamodel();

		ConnectionDescriptor theConnection = ApplicationPreferences
				.getInstance().getRepositoryConnection();
		if (theConnection != null) {
			if (theConnection.getDialect() != null) {
				theDescriptor.setDialect(DialectFactory.getInstance()
						.getDialect(theConnection.getDialect()));
			}
			theDescriptor.setDriver(theConnection.getDriver());
			theDescriptor.setUrl(theConnection.getUrl());
			theDescriptor.setUser(theConnection.getUsername());
			theDescriptor.setPassword(theConnection.getPassword());
		}

		bindingInfo.setDefaultModel(theDescriptor);
		bindingInfo.addBinding("dialect", view.getDialect(), true);
		bindingInfo.addBinding("driver", view.getDriver(), true);
		bindingInfo.addBinding("url", view.getUrl(), true);
		bindingInfo.addBinding("user", view.getUser(), true);
		bindingInfo.addBinding("password", view.getPassword());

		bindingInfo.configure();

		bindingInfo.model2view();
	}

	private void initialize() {

		view.getOkButton().setAction(okAction);
		view.getCancelButton().setAction(cancelAction);
		view.getTestButton().setAction(testAction);

		setContentPane(view);
		setResizable(false);

		pack();

		UIInitializer.getInstance().initialize(this);
	}

	@Override
	public void applyValues() throws Exception {

		DatabaseConnectionDatamodel theDescriptor = bindingInfo
				.getDefaultModel();
		ApplicationPreferences.getInstance().setRepositoryConnection(
				theDescriptor.createConnectionDescriptor());
	}

	@Override
	protected void commandOk() {

		if (bindingInfo.validate().isEmpty()) {

			bindingInfo.view2model();

			setModalResult(DialogConstants.MODAL_RESULT_OK);
		}
	}

	private void commandTest() {

		if (bindingInfo.validate().isEmpty()) {

			bindingInfo.view2model();

			DatabaseConnectionDatamodel theModel = bindingInfo
					.getDefaultModel();

			Dialect theDialect = theModel.getDialect();

			try {

				Connection theConnection = theDialect.createConnection(
						ApplicationPreferences.getInstance()
								.createDriverClassLoader(), theModel
								.getDriver(), theModel.getUrl(), theModel
								.getUser(), theModel.getPassword(), false);

				DatabaseMetaData theMeta = theConnection.getMetaData();

				String theDB = theMeta.getDatabaseProductName();
				String theVersion = theMeta.getDatabaseProductVersion();

				if (!theDialect.generatesManagedConnection()) {
					theConnection.close();
				}

				MessagesHelper.displayInfoMessage(this, getResourceHelper()
						.getText(ERDesignerBundle.CONNECTIONSEEMSTOBEOK)
						+ " DB : " + theDB + " " + theVersion);

			} catch (Exception e) {

				MessagesHelper.displayErrorMessage(this, e.getMessage());
			}
		}
	}

	private void commandChangeDialect(Dialect aDialect) {

		if (!bindingInfo.isBinding()) {
			DatabaseConnectionDatamodel theDescriptor = bindingInfo
					.getDefaultModel();

			if (aDialect != null) {
				theDescriptor.setDriver(aDialect.getDriverClassName());
				theDescriptor.setUrl(aDialect.getDriverURLTemplate());
				theDescriptor.setDialect(aDialect);
			}

			bindingInfo.model2view();
		}
	}
}
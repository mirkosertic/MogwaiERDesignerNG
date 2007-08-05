package de.mogwai.erdesignerng.visual.editor.connection;

import java.awt.Component;
import java.sql.Connection;
import java.util.List;

import javax.swing.DefaultComboBoxModel;

import de.mogwai.binding.BindingInfo;
import de.mogwai.erdesignerng.ERDesignerBundle;
import de.mogwai.erdesignerng.dialect.Dialect;
import de.mogwai.erdesignerng.dialect.DialectFactory;
import de.mogwai.erdesignerng.model.Model;
import de.mogwai.erdesignerng.util.ApplicationPreferences;
import de.mogwai.erdesignerng.visual.editor.BaseEditor;
import de.mogwai.erdesignerng.visual.editor.DialogConstants;
import de.mogwai.looks.UIInitializer;

/**
 * Editor for the database connection.
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2007-08-05 18:15:02 $
 */
public class DatabaseConnectionEditor extends BaseEditor {

	private DatabaseConnectionEditorView view = new DatabaseConnectionEditorView() {

		@Override
		protected void handleTest() {
			commandTest();
		}

		@Override
		protected void handleCancel() {
			commandCancel();
		}

		@Override
		protected void handleOk() {
			commandClose();
		}

		@Override
		public void handleDialectChange(Dialect aDialect) {
			commandChangeDialect(aDialect);
		}
	};

	private Model model;

	private ApplicationPreferences preferences;

	private BindingInfo<DatabaseConnectionDatamodel> bindingInfo = new BindingInfo<DatabaseConnectionDatamodel>();

	public DatabaseConnectionEditor(Component aParent, Model aModel,
			ApplicationPreferences aPreferences) {
		super(aParent, ERDesignerBundle.CONNECTIONCONFIGURATION);

		model = aModel;
		preferences = aPreferences;

		initialize();

		DefaultComboBoxModel theModel = new DefaultComboBoxModel();
		theModel.addElement(null);

		List<Dialect> theDialects = DialectFactory.getInstance()
				.getSupportedDialects();
		for (Dialect theDialect : theDialects) {
			theModel.addElement(theDialect);
		}

		view.getDialect().setModel(theModel);

		DatabaseConnectionDatamodel theDescriptor = new DatabaseConnectionDatamodel();
		theDescriptor.setDialect(model.getDialect());
		theDescriptor.setDriver(model.getProperties().getProperty(
				Model.PROPERTY_DRIVER));
		theDescriptor.setUrl(model.getProperties().getProperty(
				Model.PROPERTY_URL));
		theDescriptor.setUser(model.getProperties().getProperty(
				Model.PROPERTY_USER));
		theDescriptor.setPassword(model.getProperties().getProperty(
				Model.PROPERTY_PASSWORD));

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

		setContentPane(view);
		setResizable(false);
		pack();

		UIInitializer.getInstance().initialize(this);
	}

	@Override
	public void applyValues() throws Exception {

		DatabaseConnectionDatamodel theDescriptor = bindingInfo
				.getDefaultModel();

		model.setDialect(theDescriptor.getDialect());
		model.getProperties().setProperty(Model.PROPERTY_DRIVER,
				theDescriptor.getDriver());
		model.getProperties().setProperty(Model.PROPERTY_URL,
				theDescriptor.getUrl());
		model.getProperties().setProperty(Model.PROPERTY_USER,
				theDescriptor.getUser());
		model.getProperties().setProperty(Model.PROPERTY_PASSWORD,
				theDescriptor.getPassword());
	}

	private void commandClose() {

		if (bindingInfo.validate().size() == 0) {

			bindingInfo.view2model();

			setModalResult(DialogConstants.MODAL_RESULT_OK);
		}
	}

	private void commandTest() {

		if (bindingInfo.validate().size() == 0) {

			bindingInfo.view2model();

			DatabaseConnectionDatamodel theModel = bindingInfo
					.getDefaultModel();

			Dialect theDialect = theModel.getDialect();

			try {

				Connection theConnection = theDialect.createConnection(
						preferences.createDriverClassLoader(), theModel
								.getDriver(), theModel.getUrl(), theModel
								.getUser(), theModel.getPassword());
				theConnection.close();

				displayInfoMessage("Connection seems to be ok");

			} catch (Exception e) {

				displayErrorMessage(e.getMessage());
			}
		}
	}

	private void commandCancel() {

		setModalResult(DialogConstants.MODAL_RESULT_CANCEL);
	}

	private void commandChangeDialect(Dialect aDialect) {

		if (!bindingInfo.isBinding()) {
			DatabaseConnectionDatamodel theDescriptor = bindingInfo
					.getDefaultModel();

			if (aDialect != null) {
				theDescriptor.setDriver(aDialect.getDriverClassName());
				theDescriptor.setUrl(aDialect.getDriverURLTemplate());
			}

			bindingInfo.model2view();
		}
	}
}

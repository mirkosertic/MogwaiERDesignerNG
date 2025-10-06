package de.erdesignerng.visual.editor.connection;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.dialect.Dialect;
import de.erdesignerng.dialect.DialectFactory;
import de.erdesignerng.model.Model;
import de.erdesignerng.util.ApplicationPreferences;
import de.erdesignerng.util.ConnectionDescriptor;
import de.erdesignerng.visual.MessagesHelper;
import de.erdesignerng.visual.editor.BaseEditor;
import de.erdesignerng.visual.editor.DialogConstants;
import de.mogwai.common.client.binding.BindingInfo;
import de.mogwai.common.client.looks.UIInitializer;
import de.mogwai.common.client.looks.components.action.DefaultAction;
import org.apache.commons.lang.StringUtils;

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
public class DatabaseConnectionEditor extends BaseEditor {

	private final DefaultAction testAction = new DefaultAction(
            e -> commandTest(), this, ERDesignerBundle.TEST);

	private final DatabaseConnectionEditorView view = new DatabaseConnectionEditorView() {

		@Override
		public void handleDialectChange(final Dialect aDialect) {
			commandChangeDialect(aDialect, getAlias().getText(), getDriver()
					.getText(), getUrl().getText(), getUser().getText(), String
					.valueOf(getPassword().getPassword()));
		}
	};

	private final Model model;

	private final BindingInfo<DatabaseConnectionDatamodel> bindingInfo = new BindingInfo<>();

	public DatabaseConnectionEditor(final Component aParent, final Model aModel,
                                    final ConnectionDescriptor aConnection) {
		super(aParent, ERDesignerBundle.CONNECTIONCONFIGURATION);

		model = aModel;

		initialize();

		final DefaultComboBoxModel theModel = new DefaultComboBoxModel();
		theModel.addElement(null);

		final List<Dialect> theDialects = DialectFactory.getInstance()
				.getSupportedDialects();
        theDialects.forEach(theModel::addElement);

		view.getDialect().setModel(theModel);

		final DatabaseConnectionDatamodel theDescriptor = new DatabaseConnectionDatamodel();
		if (aConnection.getDialect() != null) {
			theDescriptor.setDialect(DialectFactory.getInstance().getDialect(
					aConnection.getDialect()));
		}
		theDescriptor.setAlias(aConnection.getAlias());
		theDescriptor.setDriver(aConnection.getDriver());
		theDescriptor.setUrl(aConnection.getUrl());
		theDescriptor.setUser(aConnection.getUsername());
		theDescriptor.setPassword(aConnection.getPassword());
		theDescriptor.setPromptForPassword(aConnection.isPromptForPassword());

		bindingInfo.setDefaultModel(theDescriptor);
		bindingInfo.addBinding("alias", view.getAlias(), true);
		bindingInfo.addBinding("dialect", view.getDialect(), true);
		bindingInfo.addBinding("driver", view.getDriver(), true);
		bindingInfo.addBinding("url", view.getUrl(), true);
		bindingInfo.addBinding("user", view.getUser(), true);
		bindingInfo.addBinding("password", view.getPassword());
		bindingInfo
				.addBinding("promptForPassword", view.getPromptForPassword());

		bindingInfo.configure();

		bindingInfo.model2view();

		final boolean isDefinedModel = !aModel.getDomains().isEmpty()
				|| !aModel.getTables().isEmpty();
		if (isDefinedModel) {
			// If there are domains or tables already defined, the dialect
			// cannot be changed
			view.getDialect().setEnabled(false);
		}
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
	public void applyValues() {

		final DatabaseConnectionDatamodel theDescriptor = bindingInfo
				.getDefaultModel();
		model.initializeWith(theDescriptor.createConnectionDescriptor());
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

			final DatabaseConnectionDatamodel theModel = bindingInfo
					.getDefaultModel();

			final Dialect theDialect = theModel.getDialect();

			try {

				final Connection theConnection = theDialect.createConnection(
						ApplicationPreferences.getInstance()
								.createDriverClassLoader(), theModel
								.getDriver(), theModel.getUrl(), theModel
								.getUser(), theModel.getPassword(), theModel
								.isPromptForPassword());
				if (theConnection == null) {
					return;
				}

				final DatabaseMetaData theMeta = theConnection.getMetaData();

				final String theDB = theMeta.getDatabaseProductName();
				final String theVersion = theMeta.getDatabaseProductVersion();

				if (!theDialect.generatesManagedConnection()) {
					theConnection.close();
				}

				MessagesHelper.displayInfoMessage(this, getResourceHelper()
						.getText(ERDesignerBundle.CONNECTIONSEEMSTOBEOK)
						+ " DB : " + theDB + " " + theVersion);

			} catch (final Exception e) {

				MessagesHelper.displayErrorMessage(this, e.getMessage());
			}
		}
	}

	private void commandChangeDialect(final Dialect aNewDialect,
                                      final String theUserdefinedAlias, final String theUserdefinedDriver,
                                      final String theUserdefinedUrl, final String theUserdefinedUsername,
                                      final String theUserdefinedPassword) {

		if (!bindingInfo.isBinding()) {
			final DatabaseConnectionDatamodel theDescriptor = bindingInfo
					.getDefaultModel();

			if (aNewDialect != null) {
				// Bug Fixing 2895853 [ERDesignerNG] DbConnEditor *don't*
				// overwrite user input
				// always respect user input for alias, username and password
				// over the use of the chosen dialects defaults
				theDescriptor.setAlias(getSetting(theUserdefinedAlias, ""));
				theDescriptor.setUser(getSetting(theUserdefinedUsername,
						aNewDialect.getDefaultUserName()));
				theDescriptor
						.setPassword(getSetting(theUserdefinedPassword, ""));

				// here the driver names are compared because a Dialect name
				// change
				// can result in theDescriptor.getDialect() == null
				if (aNewDialect.getDriverClassName().equals(
						theDescriptor.getDriver())) {
					// if re-selecting the same DB only set fields to the
					// dialects defaults in case they are empty
					theDescriptor.setDriver(getSetting(theUserdefinedDriver,
							aNewDialect.getDriverClassName()));
					theDescriptor.setUrl(getSetting(theUserdefinedUrl,
							aNewDialect.getDriverURLTemplate()));
				} else {
					// if selecting a completely different DB set generally set
					// the fields to the dialects defaults
					theDescriptor.setDriver(aNewDialect.getDriverClassName());
					theDescriptor.setUrl(aNewDialect.getDriverURLTemplate());
				}

				theDescriptor.setDialect(aNewDialect);
			}

			bindingInfo.model2view();
		}
	}

	private String getSetting(final String aPrimarySetting, final String aSecondarySetting) {
		return StringUtils.isNotEmpty(aPrimarySetting) ? aPrimarySetting : aSecondarySetting;
	}
}

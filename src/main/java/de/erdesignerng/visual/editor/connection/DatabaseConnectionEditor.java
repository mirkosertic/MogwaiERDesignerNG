package de.erdesignerng.visual.editor.connection;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.List;

import javax.swing.DefaultComboBoxModel;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.dialect.Dialect;
import de.erdesignerng.dialect.DialectFactory;
import de.erdesignerng.dialect.JDBCDialect;
import de.erdesignerng.model.Model;
import de.erdesignerng.util.ApplicationPreferences;
import de.erdesignerng.util.RecentlyUsedConnection;
import de.erdesignerng.visual.MessagesHelper;
import de.erdesignerng.visual.editor.BaseEditor;
import de.erdesignerng.visual.editor.DialogConstants;
import de.mogwai.common.client.binding.BindingInfo;
import de.mogwai.common.client.looks.UIInitializer;
import de.mogwai.common.client.looks.components.action.ActionEventProcessor;
import de.mogwai.common.client.looks.components.action.DefaultAction;

/**
 * Editor for the database connection.
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2008-06-13 16:48:59 $
 */
public class DatabaseConnectionEditor extends BaseEditor {

    private DefaultAction testAction = new DefaultAction(new ActionEventProcessor() {

        public void processActionEvent(ActionEvent e) {
            commandTest();
        }
    }, this, ERDesignerBundle.TEST);

    private DefaultAction okAction = new DefaultAction(new ActionEventProcessor() {

        public void processActionEvent(ActionEvent e) {
            commandClose();
        }
    }, this, ERDesignerBundle.OK);

    private DefaultAction cancelAction = new DefaultAction(new ActionEventProcessor() {

        public void processActionEvent(ActionEvent e) {
            commandCancel();
        }
    }, this, ERDesignerBundle.CANCEL);

    private DatabaseConnectionEditorView view = new DatabaseConnectionEditorView() {

        @Override
        public void handleDialectChange(JDBCDialect aDialect) {
            commandChangeDialect(aDialect);
        }
    };

    private Model model;

    private ApplicationPreferences preferences;

    private BindingInfo<DatabaseConnectionDatamodel> bindingInfo = new BindingInfo<DatabaseConnectionDatamodel>();

    public DatabaseConnectionEditor(Component aParent, Model aModel, ApplicationPreferences aPreferences, RecentlyUsedConnection aConnection) {
        super(aParent, ERDesignerBundle.CONNECTIONCONFIGURATION);

        model = aModel;
        preferences = aPreferences;

        initialize();

        DefaultComboBoxModel theModel = new DefaultComboBoxModel();
        theModel.addElement(null);

        List<Dialect> theDialects = DialectFactory.getInstance().getSupportedDialects();
        for (Dialect theDialect : theDialects) {
            theModel.addElement(theDialect);
        }

        view.getDialect().setModel(theModel);

        DatabaseConnectionDatamodel theDescriptor = new DatabaseConnectionDatamodel();
        if (aConnection.getDialect() != null) {
            theDescriptor.setDialect(DialectFactory.getInstance().getDialect(aConnection.getDialect()));
        }
        theDescriptor.setDriver(aConnection.getDriver());
        theDescriptor.setUrl(aConnection.getUrl());
        theDescriptor.setUser(aConnection.getUsername());
        theDescriptor.setPassword(aConnection.getPassword());

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

    /**
     * {@inheritDoc}
     */
    @Override
    public void applyValues() throws Exception {

        DatabaseConnectionDatamodel theDescriptor = bindingInfo.getDefaultModel();

        model.setDialect(theDescriptor.getDialect());
        model.getProperties().setProperty(Model.PROPERTY_DRIVER, theDescriptor.getDriver());
        model.getProperties().setProperty(Model.PROPERTY_URL, theDescriptor.getUrl());
        model.getProperties().setProperty(Model.PROPERTY_USER, theDescriptor.getUser());
        model.getProperties().setProperty(Model.PROPERTY_PASSWORD, theDescriptor.getPassword());
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

            DatabaseConnectionDatamodel theModel = bindingInfo.getDefaultModel();

            Dialect theDialect = theModel.getDialect();

            try {

                Connection theConnection = theDialect.createConnection(preferences.createDriverClassLoader(), theModel
                        .getDriver(), theModel.getUrl(), theModel.getUser(), theModel.getPassword());

                DatabaseMetaData theMeta = theConnection.getMetaData();

                String theDB = theMeta.getDatabaseProductName();
                String theVersion = theMeta.getDatabaseProductVersion();

                if (!theDialect.generatesManagedConnection()) {
                    theConnection.close();
                }

                MessagesHelper.displayInfoMessage(this, getResourceHelper().getText(
                        ERDesignerBundle.CONNECTIONSEEMSTOBEOK)
                        + " DB : " + theDB + " " + theVersion);

            } catch (Exception e) {

                MessagesHelper.displayErrorMessage(this, e.getMessage());
            }
        }
    }

    private void commandChangeDialect(JDBCDialect aDialect) {

        if (!bindingInfo.isBinding()) {
            DatabaseConnectionDatamodel theDescriptor = bindingInfo.getDefaultModel();

            if (aDialect != null) {
                theDescriptor.setDriver(aDialect.getDriverClassName());
                theDescriptor.setUrl(aDialect.getDriverURLTemplate());
                theDescriptor.setDialect(aDialect);
            }

            bindingInfo.model2view();
        }
    }
}

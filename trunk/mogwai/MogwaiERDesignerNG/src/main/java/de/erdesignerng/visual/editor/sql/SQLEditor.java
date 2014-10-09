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
package de.erdesignerng.visual.editor.sql;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.dialect.ConnectionProvider;
import de.erdesignerng.dialect.Statement;
import de.erdesignerng.dialect.StatementList;
import de.erdesignerng.io.SQLFileFilter;
import de.erdesignerng.util.ApplicationPreferences;
import de.erdesignerng.util.JDBCUtils;
import de.erdesignerng.visual.LongRunningTask;
import de.erdesignerng.visual.MessagesHelper;
import de.erdesignerng.visual.common.ERDesignerWorldConnector;
import de.erdesignerng.visual.editor.BaseEditor;
import de.erdesignerng.visual.editor.DialogConstants;
import de.mogwai.common.client.looks.UIInitializer;
import de.mogwai.common.client.looks.components.action.DefaultAction;
import de.mogwai.common.client.looks.components.list.DefaultListModel;

import javax.swing.JFileChooser;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.Component;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Editor for sql statements.
 *
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-09 19:07:29 $
 */
public class SQLEditor extends BaseEditor {

    private final DefaultAction closeAction = new DefaultAction(
            e -> commandClose(), this, ERDesignerBundle.CLOSE);

    private final DefaultAction executeAction = new DefaultAction(
            e -> commandExecute(), this, ERDesignerBundle.EXECUTESCRIPT);

    private final DefaultAction saveToFileAction = new DefaultAction(
            e -> commandSaveToFile(), this, ERDesignerBundle.SAVESCRIPTTOFILE);

    private final DefaultAction deleteAction = new DefaultAction(
            e -> commandDeleteSelectedEntry(), this, ERDesignerBundle.DELETE);

    private final File lastEditedFile;

    private final String filename;

    private final SQLEditorView view = new SQLEditorView();

    private final ConnectionProvider connectionAdapter;

    private final StatementList statements;

    private final ERDesignerWorldConnector worldConnector;

    public SQLEditor(Component aParent, ConnectionProvider aConnectionAdapter,
                     StatementList aStatements, File aLastEditedFile, String aFileName,
                     ERDesignerWorldConnector aConnector) {
        super(aParent, ERDesignerBundle.SQLWINDOW);

        connectionAdapter = aConnectionAdapter;
        lastEditedFile = aLastEditedFile;
        filename = aFileName;
        statements = aStatements;
        worldConnector = aConnector;

        initialize();

        view.getSqlList().setCellRenderer(new StatementRenderer());
        view.getSqlList().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                deleteAction
                        .setEnabled(view.getSqlList().getSelectedIndex() >= 0);
            }
        });

        view.getCloseButton().setAction(closeAction);
        view.getExecuteButton().setAction(executeAction);
        view.getSaveToFileButton().setAction(saveToFileAction);
        view.getDeleteButton().setAction(deleteAction);
        deleteAction.setEnabled(false);

        DefaultListModel theModel = view.getSqlList().getModel();
        aStatements.forEach(theModel::add);
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

        DefaultListModel theModel = view.getSqlList().getModel();

        StatementList theDeleted = statements.stream().filter(theStatement -> !theModel.contains(theStatement)).collect(Collectors.toCollection(() -> new StatementList()));

        statements.removeAll(theDeleted);

    }

    private void commandClose() {
        ApplicationPreferences.getInstance().updateWindowSize(
                getClass().getSimpleName(), this);
        setModalResult(DialogConstants.MODAL_RESULT_OK);
    }

    private void commandExecute() {
        closeAction.setEnabled(false);
        executeAction.setEnabled(false);
        saveToFileAction.setEnabled(false);
        deleteAction.setEnabled(false);

        LongRunningTask<String> theTask = new LongRunningTask<String>(
                worldConnector) {
            @Override
            public String doWork(MessagePublisher aPublisher) throws Exception {

                DefaultListModel theModel = view.getSqlList().getModel();

                Connection theConnection = null;
                try {

                    theConnection = connectionAdapter.createConnection();

                    for (int i = 0; i < theModel.getSize(); i++) {
                        Statement theStatement = (Statement) theModel.get(i);
                        if (!theStatement.isExecuted()) {

                            view.getSqlList().setSelectedIndex(i);
                            view.getSqlList().ensureIndexIsVisible(i);

                            java.sql.Statement theJDBCStatement = null;
                            try {
                                theJDBCStatement = theConnection
                                        .createStatement();
                                theJDBCStatement.execute(theStatement.getSql());

                                theStatement.setExecuted(true);
                            } catch (Exception e) {
                                logFatalError(e);
                                return null;
                            } finally {
                                if (theJDBCStatement != null) {
                                    try {
                                        theJDBCStatement.close();
                                    } catch (Exception e) {
                                        // Do nothing here
                                    }
                                }
                                aPublisher.publishMessage("OK");
                            }
                        }
                    }
                } catch (Exception e) {
                    worldConnector.notifyAboutException(e);
                } finally {
                    if (!connectionAdapter.generatesManagedConnection()) {
                        JDBCUtils.closeQuietly(theConnection);
                    }
                }

                return null;
            }

            @Override
            public void handleProcess(List<String> aValue) {
                view.getSqlList().invalidate();
                view.getSqlList().repaint();
            }

            @Override
            public void handleResult(String aResult) {
                closeAction.setEnabled(true);
                executeAction.setEnabled(true);
                saveToFileAction.setEnabled(true);
            }
        };
        theTask.start();
    }

    private void commandSaveToFile() {
        SQLFileFilter theFiler = new SQLFileFilter();

        JFileChooser theChooser = new JFileChooser();
        theChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        theChooser.setFileFilter(theFiler);
        if (lastEditedFile != null) {
            theChooser.setSelectedFile(new File(lastEditedFile.getParent(),
                    filename));
        } else {
            theChooser.setSelectedFile(new File(filename));
        }
        if (theChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {

            DefaultListModel theModel = view.getSqlList().getModel();
            try {
                File theFile = theFiler.getCompletedFile(theChooser
                        .getSelectedFile());
                FileWriter theWriter = new FileWriter(theFile);
                PrintWriter thePW = new PrintWriter(theWriter);

                for (int i = 0; i < theModel.getSize(); i++) {
                    Statement theStatement = (Statement) theModel.get(i);

                    thePW.print(theStatement.getSql());
                    thePW.println(connectionAdapter
                            .createScriptStatementSeparator());
                }

                thePW.flush();
                thePW.close();

                MessagesHelper.displayInfoMessage(this, getResourceHelper()
                        .getText(ERDesignerBundle.FILESAVED));

            } catch (Exception e) {
                logFatalError(e);
            }
        }
    }

    private void commandDeleteSelectedEntry() {

        if (MessagesHelper.displayQuestionMessage(this,
                ERDesignerBundle.DOYOUREALLYWANTTODELETE)) {
            Object theSelectedElement = view.getSqlList().getSelectedValue();

            DefaultListModel theModel = view.getSqlList().getModel();
            theModel.remove(theSelectedElement);
        }
    }
}
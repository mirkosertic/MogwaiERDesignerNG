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

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.swingworker.SwingWorker;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.dialect.SQLGenerator;
import de.erdesignerng.dialect.Statement;
import de.erdesignerng.dialect.StatementList;
import de.erdesignerng.io.SQLFileFilter;
import de.erdesignerng.model.Model;
import de.erdesignerng.util.ApplicationPreferences;
import de.erdesignerng.visual.MessagesHelper;
import de.erdesignerng.visual.editor.BaseEditor;
import de.erdesignerng.visual.editor.DialogConstants;
import de.mogwai.common.client.looks.UIInitializer;
import de.mogwai.common.client.looks.components.DefaultTextArea;
import de.mogwai.common.client.looks.components.action.ActionEventProcessor;
import de.mogwai.common.client.looks.components.action.DefaultAction;
import de.mogwai.common.client.looks.components.list.DefaultListModel;

/**
 * Editor for the class path entries.
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2008-03-11 20:41:58 $
 */
public class SQLEditor extends BaseEditor {

    private DefaultAction closeAction = new DefaultAction(new ActionEventProcessor() {

        public void processActionEvent(ActionEvent e) {
            commandClose();
        }
    }, this, ERDesignerBundle.CLOSE);

    private DefaultAction executeAction = new DefaultAction(new ActionEventProcessor() {

        public void processActionEvent(ActionEvent e) {
            commandExecute();
        }
    }, this, ERDesignerBundle.EXECUTESCRIPT);

    private DefaultAction saveToFileAction = new DefaultAction(new ActionEventProcessor() {

        public void processActionEvent(ActionEvent e) {
            commandSaveToFile();
        }
    }, this, ERDesignerBundle.SAVESCRIPTTOFILE);

    private DefaultAction deleteAction = new DefaultAction(new ActionEventProcessor() {

        public void processActionEvent(ActionEvent e) {
            commandDeleteSelectedEntry();
        }
    }, this, ERDesignerBundle.DELETE);

    private File lastEditedFile;

    private SQLGenerator generator;

    private String filename;

    private class ExecuteSQLSwingWorker extends SwingWorker<String, String> {

        @Override
        protected String doInBackground() throws Exception {

            final DefaultListModel theModel = (DefaultListModel) view.getSqlList().getModel();

            closeAction.setEnabled(false);
            executeAction.setEnabled(false);
            saveToFileAction.setEnabled(false);
            deleteAction.setEnabled(false);

            Connection theConnection = null;
            try {

                theConnection = model.createConnection(ApplicationPreferences.getInstance());

                for (int i = 0; i < theModel.getSize(); i++) {
                    Statement theStatement = (Statement) theModel.get(i);
                    if (!theStatement.isExecuted()) {

                        view.getSqlList().setSelectedIndex(i);
                        view.getSqlList().ensureIndexIsVisible(i);

                        java.sql.Statement theJDBCStatement = null;
                        try {
                            theJDBCStatement = theConnection.createStatement();
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
                            publish("OK");
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (!model.getDialect().generatesManagedConnection()) {
                    if (theConnection != null) {
                        try {
                            theConnection.close();
                        } catch (Exception e) {
                            // Do nothing here
                        }
                    }
                }

                closeAction.setEnabled(true);
                executeAction.setEnabled(true);
                saveToFileAction.setEnabled(true);
            }

            return null;
        }

        @Override
        protected void process(List<String> aValue) {
            view.getSqlList().invalidate();
            view.getSqlList().repaint();
        }
    }

    private static final class StatementRenderer implements ListCellRenderer {

        private DefaultTextArea component = new DefaultTextArea();

        private UIInitializer initializer = UIInitializer.getInstance();

        public Component getListCellRendererComponent(JList aList, Object aValue, int aIndex, boolean isSelected,
                boolean cellHasFocus) {
            Statement theStatement = (Statement) aValue;
            if (theStatement.isExecuted()) {
                component.setForeground(Color.BLACK);
            } else {
                component.setForeground(Color.GRAY);

            }

            component.setText(theStatement.getSql());
            if (isSelected) {
                component.setBackground(initializer.getConfiguration().getDefaultListSelectionBackground());
            } else {
                component.setBackground(initializer.getConfiguration().getDefaultListNonSelectionBackground());
            }

            return component;
        }
    }

    private SQLEditorView view = new SQLEditorView();

    private Model model;

    private StatementList statements;

    public SQLEditor(Component aParent, Model aModel, StatementList aStatements, File aLastEditedFile,
            SQLGenerator aGenerator, String aFileName) {
        super(aParent, ERDesignerBundle.SQLWINDOW);

        initialize();

        model = aModel;
        lastEditedFile = aLastEditedFile;
        generator = aGenerator;
        filename = aFileName;
        statements = aStatements;

        view.getSqlList().setCellRenderer(new StatementRenderer());
        view.getSqlList().addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                deleteAction.setEnabled(view.getSqlList().getSelectedIndex() >= 0);
            }
        });

        view.getCloseButton().setAction(closeAction);
        view.getExecuteButton().setAction(executeAction);
        view.getSaveToFileButton().setAction(saveToFileAction);
        view.getDeleteButton().setAction(deleteAction);
        deleteAction.setEnabled(false);

        DefaultListModel theModel = (DefaultListModel) view.getSqlList().getModel();
        for (Statement theStatement : aStatements) {
            theModel.add(theStatement);
        }
    }

    private void initialize() {

        setContentPane(view);
        setResizable(false);

        pack();

        UIInitializer.getInstance().initialize(this);
    }

    @Override
    public void applyValues() throws Exception {

        DefaultListModel theModel = (DefaultListModel) view.getSqlList().getModel();

        StatementList theDeleted = new StatementList();
        for (Statement theStatement : statements) {
            if (!theModel.contains(theStatement)) {
                theDeleted.add(theStatement);
            }
        }

        statements.removeAll(theDeleted);

    }

    private void commandClose() {
        setModalResult(DialogConstants.MODAL_RESULT_OK);
    }

    private void commandExecute() {
        new ExecuteSQLSwingWorker().execute();
    }

    private void commandSaveToFile() {
        SQLFileFilter theFiler = new SQLFileFilter();

        JFileChooser theChooser = new JFileChooser();
        theChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        theChooser.setFileFilter(theFiler);
        if (lastEditedFile != null) {
            theChooser.setSelectedFile(new File(lastEditedFile.getParent(), filename));
        }
        if (theChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {

            DefaultListModel theModel = (DefaultListModel) view.getSqlList().getModel();
            try {
                File theFile = theFiler.getCompletedFile(theChooser.getSelectedFile());
                FileWriter theWriter = new FileWriter(theFile);
                PrintWriter thePW = new PrintWriter(theWriter);

                for (int i = 0; i < theModel.getSize(); i++) {
                    Statement theStatement = (Statement) theModel.get(i);

                    thePW.print(theStatement.getSql());
                    thePW.println(generator.createScriptStatementSeparator());
                }

                thePW.flush();
                thePW.close();

                MessagesHelper.displayInfoMessage(this, getResourceHelper().getText(ERDesignerBundle.FILESAVED));

            } catch (Exception e) {
                logFatalError(e);
            }
        }
    }

    private void commandDeleteSelectedEntry() {

        if (MessagesHelper.displayQuestionMessage(this, ERDesignerBundle.DOYOUREALLYWANTTODELETE)) {
            Object theSelectedElement = view.getSqlList().getSelectedValue();

            DefaultListModel theModel = (DefaultListModel) view.getSqlList().getModel();
            theModel.remove(theSelectedElement);
        }
    }
}

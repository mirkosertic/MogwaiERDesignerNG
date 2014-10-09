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

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.dialect.Dialect;
import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.IndexExpression;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.Relation;
import de.erdesignerng.model.Table;
import de.erdesignerng.model.View;
import de.erdesignerng.util.ApplicationPreferences;
import de.erdesignerng.util.JDBCUtils;
import de.erdesignerng.visual.editor.BaseEditor;
import de.erdesignerng.visual.editor.DialogConstants;
import de.mogwai.common.client.binding.BindingInfo;
import de.mogwai.common.client.looks.UIInitializer;
import de.mogwai.common.client.looks.components.DefaultPopupMenu;
import de.mogwai.common.client.looks.components.action.DefaultAction;
import de.mogwai.common.client.looks.components.renderer.DefaultCellRenderer;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import java.awt.Component;
import java.awt.FontMetrics;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * DataBrowser.
 *
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-09 19:07:29 $
 */
public class DataBrowserEditor extends BaseEditor {

    private final DataBrowserEditorView view = new DataBrowserEditorView();

    private Model currentModel;
    private Dialect currentDialect;

    private final BindingInfo<DataBrowserModel> sqlBindingInfo = new BindingInfo<>();
    private Connection connection;
    private Statement statement;

    private PaginationDataModel dataModel;

    public DataBrowserEditor(Component aParent) {
        super(aParent, ERDesignerBundle.DATABROWSER);

        DefaultAction closeAction = new DefaultAction(
                e -> commandClose(), this, ERDesignerBundle.CLOSE);

        view.getCloseButton().setAction(closeAction);

        DefaultAction queryAction = new DefaultAction(
                e -> commandQuery(), this, ERDesignerBundle.QUERY);
        view.getQueryButton().setAction(queryAction);
        view.getData().setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        initialize();

        sqlBindingInfo.setDefaultModel(new DataBrowserModel());
        sqlBindingInfo.addBinding("sql", view.getSql(), true);
        sqlBindingInfo.configure();
    }

    public void initializeFor(final Table aTable) {

        currentModel = aTable.getOwner();
        currentDialect = aTable.getOwner().getDialect();

        Map<Attribute<Table>, Object> theWhereValues = new HashMap<>();

        DataBrowserModel theModel = sqlBindingInfo.getDefaultModel();
        theModel.setSql(currentDialect.createSQLGenerator()
                .createSelectAllScriptFor(aTable, theWhereValues));
        sqlBindingInfo.model2view();

        initializeContextMenu(aTable);

        final String theSQL = theModel.getSql();

        view.addBreadCrumb(aTable.getName(), e -> {

            sqlBindingInfo.getDefaultModel().setSql(theSQL);
            sqlBindingInfo.model2view();
            commandQuery();

            initializeContextMenu(aTable);
        });

        commandQuery();
    }

    private void initializeContextMenu(final Table aTable) {
        DefaultPopupMenu theMenu = new DefaultPopupMenu();

        Map<Table, JMenu> theMap = new HashMap<>();

        for (Relation theRelation : currentModel.getRelations()
                .getForeignKeysFor(aTable)) {

            final Relation theFinalRelation = theRelation;

            Table theNavigationTarget = theRelation.getExportingTable();

            JMenuItem theItem = new JMenuItem();
            theItem.setText(getResourceHelper().getFormattedText(
                    ERDesignerBundle.SHOWDATAOFUSING,
                    theNavigationTarget.getName(), theFinalRelation.getName()));

            theItem.addActionListener(e -> navigateToWithForeignKey(theFinalRelation));

            JMenu theMenuToAdd = theMap.get(theNavigationTarget);
            if (theMenuToAdd == null) {
                theMenuToAdd = new JMenu(theNavigationTarget.getName());
                theMap.put(theNavigationTarget, theMenuToAdd);
            }
            theMenuToAdd.add(theItem);
        }

        for (Relation theRelation : currentModel.getRelations()
                .getExportedKeysFor(aTable)) {

            final Relation theFinalRelation = theRelation;
            Table theNavigationTarget = theRelation.getImportingTable();

            JMenuItem theItem = new JMenuItem();
            theItem.setText(getResourceHelper().getFormattedText(
                    ERDesignerBundle.SHOWDATAOFUSING,
                    theNavigationTarget.getName(), theFinalRelation.getName()));

            theItem.addActionListener(e -> navigateToWithImportingKey(theFinalRelation));

            JMenu theMenuToAdd = theMap.get(theNavigationTarget);
            if (theMenuToAdd == null) {
                theMenuToAdd = new JMenu(theNavigationTarget.getName());
                theMap.put(theNavigationTarget, theMenuToAdd);
            }
            theMenuToAdd.add(theItem);

        }

        if (theMap.size() > 0) {
            for (Map.Entry<Table, JMenu> theEntry : theMap.entrySet()) {
                theMenu.add(theEntry.getValue());
            }
        }
        final JMenuItem theItem = new JMenuItem(getResourceHelper().getText(ERDesignerBundle.EDITROW));
        theItem.addActionListener(e -> {
            try {
                RowEditor theEditor = new RowEditor(theItem, dataModel, view.getData().getSelectedRow());
                theEditor.setVisible(true);
            } catch (SQLException ex) {
                logFatalError(ex);
            }
        });


        // Deactivatred, will be available in future version
        //if (theMap.size() > 0) {
        //	theMenu.addSeparator();
        //}
        //theMenu.add(theItem);

        view.getData().setContextMenu(theMenu);
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

    private void navigateToWithForeignKey(final Relation aRelation) {
        int theCurrentRow = view.getData().getSelectedRow();
        if (theCurrentRow >= 0) {

            Map<Attribute<Table>, Object> theWhereValues = new HashMap<>();
            for (Map.Entry<IndexExpression, Attribute<Table>> theEntry : aRelation
                    .getMapping().entrySet()) {

                Attribute<Table> theAttribute = theEntry.getValue();
                int theIndex = theAttribute.getOwner().getAttributes().indexOf(
                        theAttribute);

                Object theValue = dataModel.getValueAt(theCurrentRow, theIndex);

                Attribute<Table> theKey = theEntry.getKey().getAttributeRef();
                if (theKey != null) {
                    theWhereValues.put(theKey, theValue);
                }
            }

            DataBrowserModel theModel = sqlBindingInfo.getDefaultModel();
            theModel.setSql(currentDialect.createSQLGenerator()
                    .createSelectAllScriptFor(aRelation.getExportingTable(),
                            theWhereValues));
            sqlBindingInfo.model2view();

            initializeContextMenu(aRelation.getExportingTable());

            commandQuery();

            final String theSQL = sqlBindingInfo.getDefaultModel().getSql();

            view.addBreadCrumb(aRelation.getExportingTable().getName(),
                    e -> {

                        sqlBindingInfo.getDefaultModel().setSql(theSQL);
                        sqlBindingInfo.model2view();
                        commandQuery();

                        initializeContextMenu(aRelation.getExportingTable());
                    });

        }
    }

    private void navigateToWithImportingKey(final Relation aRelation) {
        int theCurrentRow = view.getData().getSelectedRow();
        if (theCurrentRow >= 0) {

            Map<Attribute<Table>, Object> theWhereValues = new HashMap<>();
            for (Map.Entry<IndexExpression, Attribute<Table>> theEntry : aRelation.getMapping().entrySet()) {
                Attribute<Table> theAttribute = theEntry.getKey().getAttributeRef();
                if (theAttribute != null) {
                    int theIndex = theAttribute.getOwner().getAttributes().indexOf(theAttribute);
                    Object theValue = dataModel.getValueAt(theCurrentRow, theIndex);

                    theWhereValues.put(theEntry.getValue(), theValue);
                }
            }

            DataBrowserModel theModel = sqlBindingInfo.getDefaultModel();
            theModel.setSql(currentDialect.createSQLGenerator()
                    .createSelectAllScriptFor(aRelation.getImportingTable(),
                            theWhereValues));
            sqlBindingInfo.model2view();

            initializeContextMenu(aRelation.getImportingTable());

            commandQuery();

            final String theSQL = sqlBindingInfo.getDefaultModel().getSql();

            view.addBreadCrumb(aRelation.getImportingTable().getName(),
                    e -> {

                        sqlBindingInfo.getDefaultModel().setSql(theSQL);
                        sqlBindingInfo.model2view();
                        commandQuery();

                        initializeContextMenu(aRelation.getImportingTable());
                    });
        }
    }

    private void initialize() {

        setContentPane(view);
        setResizable(true);

        pack();

        ApplicationPreferences.getInstance().setWindowSize(
                getClass().getSimpleName(), this);

        UIInitializer.getInstance().initialize(this);
    }

    @Override
    public void applyValues() throws Exception {
    }

    private void commandQuery() {

        if (sqlBindingInfo.validate().isEmpty()) {

            sqlBindingInfo.view2model();

            try {
                if (connection == null) {
                    connection = currentModel.createConnection();
                }
                if (statement == null) {
                    statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                }

                ResultSet theResult = statement.executeQuery(sqlBindingInfo
                        .getDefaultModel().getSql());

                if (dataModel != null) {
                    dataModel.cleanup();
                }

                dataModel = new PaginationDataModel(currentDialect, view
                        .getData(), theResult);
                dataModel.seekToRow(5);

                view.getData().setModel(dataModel);
                view.getData().getTableHeader().setReorderingAllowed(false);

                dataModel
                        .addSeekListener(() -> updateTableColumnWIdth());

                updateTableColumnWIdth();

            } catch (Exception e) {
                logFatalError(e);
            }
        }

    }

    private void updateTableColumnWIdth() {
        FontMetrics theMetrics = getFontMetrics(getFont());
        int theWWidth = theMetrics.stringWidth("W");

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

        if (dataModel != null) {
            dataModel.cleanup();
        }

        JDBCUtils.closeQuietly(statement);
        JDBCUtils.closeQuietly(connection);

        ApplicationPreferences.getInstance().updateWindowSize(
                getClass().getSimpleName(), this);
        setModalResult(DialogConstants.MODAL_RESULT_OK);
    }
}

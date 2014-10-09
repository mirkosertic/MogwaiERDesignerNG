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
package de.erdesignerng.visual.editor.table;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.dialect.DataType;
import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.IndexExpression;
import de.erdesignerng.model.Table;
import de.erdesignerng.visual.editor.CheckboxCellRenderer;
import de.erdesignerng.visual.editor.ModelItemDefaultCellRenderer;
import de.erdesignerng.visual.editor.TableHelper;
import de.mogwai.common.client.looks.components.DefaultButton;
import de.mogwai.common.client.looks.components.DefaultComboBox;
import de.mogwai.common.client.looks.components.DefaultLabel;
import de.mogwai.common.client.looks.components.DefaultList;
import de.mogwai.common.client.looks.components.DefaultPanel;
import de.mogwai.common.client.looks.components.DefaultRadioButton;
import de.mogwai.common.client.looks.components.DefaultTabbedPane;
import de.mogwai.common.client.looks.components.DefaultTabbedPaneTab;
import de.mogwai.common.client.looks.components.DefaultTable;
import de.mogwai.common.client.looks.components.DefaultTextArea;
import de.mogwai.common.client.looks.components.DefaultTextField;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-13 15:40:33 $
 */
public class TableEditorView extends DefaultPanel {

    private DefaultLabel component1;

    private DefaultTextField entityName;

    private DefaultTabbedPane mainTabbedPane;

    private DefaultTabbedPaneTab attributesTab;

    private DefaultButton newButton;

    private DefaultButton deleteButton;

    private DefaultTabbedPaneTab indexesTab;

    private DefaultList indexList;

    private DefaultButton newIndexButton;

    private DefaultButton deleteIndexButton;

    private DefaultTabbedPane indexTabbedPane;

    private DefaultTabbedPaneTab indexGeneralTab;

    private DefaultLabel label1;

    private DefaultTextField indexName;

    private DefaultTextField extra;

    private DefaultRadioButton uniqueIndex;

    private DefaultRadioButton notUniqueIndex;

    private DefaultRadioButton primaryKeyIndex;

    private DefaultRadioButton spatialIndex;

    private DefaultRadioButton fulltextIndex;

    private DefaultList<IndexExpression> indexAttributesList;

    private DefaultButton updateIndexButton;

    private DefaultTabbedPaneTab tableCommentsTab;

    private DefaultTextArea tableComment;

    private DefaultButton okButton;

    private DefaultButton cancelButton;

    private final DefaultComboBoxModel dataTypeModel = new DefaultComboBoxModel();

    private final DefaultComboBox indexAttribute = new DefaultComboBox();

    private final DefaultTextField indexExpression = new DefaultTextField();

    private final DefaultRadioButton addIndexAttribute = new DefaultRadioButton(ERDesignerBundle.ATTRIBUTE);

    private final DefaultRadioButton addIndexExpression = new DefaultRadioButton(ERDesignerBundle.EXPRESSION);

    private final DefaultButton addExpressionToIndexButton = new DefaultButton(ERDesignerBundle.NEWONLYICON);

    private final DefaultButton addAttributeToIndexButton = new DefaultButton(ERDesignerBundle.NEWONLYICON);

    private final DefaultButton removeFromIndexButton = new DefaultButton(ERDesignerBundle.DELETEONLYICON);

    private DefaultTabbedPaneTab tablePropertiesTab;

    private DefaultTabbedPaneTab indexPropertiesTab;

    private final DefaultTable attributesTable = new DefaultTable() {

        @Override
        public void removeEditor() {
            super.removeEditor();

            Attribute<Table> theAttribute = attributeTableModel.getRow(getSelectedRow());
            attributeEditorRemoved(theAttribute);

            invalidate();
            repaint();

            TableHelper.processEditorRemovel(this);
        }
    };

    private final AttributeTableModel attributeTableModel = new AttributeTableModel();

    public TableEditorView() {
        initialize();
    }

    protected void attributeEditorRemoved(Attribute<Table> aAttribute) {

    }

    private void initialize() {

        String rowDef = "2dlu,p,2dlu,p,fill:260dlu,p,20dlu,p,2dlu";
        String colDef = "2dlu,left:45dlu,2dlu,fill:140dlu:grow,fill:60dlu,2dlu,fill:60dlu,2dlu";

        FormLayout layout = new FormLayout(colDef, rowDef);
        setLayout(layout);

        attributesTable.setCellSelectionEnabled(true);

        CellConstraints cons = new CellConstraints();

        add(getComponent1(), cons.xywh(2, 2, 1, 1));
        add(getEntityName(), cons.xywh(4, 2, 4, 1));
        add(getMainTabbedPane(), cons.xywh(2, 4, 6, 2));
        add(getOkButton(), cons.xywh(5, 8, 1, 1));
        add(getCancelButton(), cons.xywh(7, 8, 1, 1));

        buildGroups();

        getAddIndexAttribute().setSelected(true);
        getRemoveFromIndexButton().setEnabled(false);
    }

    public AttributeTableModel getAttributeTableModel() {
        return attributeTableModel;
    }

    public DefaultTable getAttributesTable() {
        return attributesTable;
    }

    public JLabel getComponent1() {

        if (component1 == null) {
            component1 = new DefaultLabel(ERDesignerBundle.ENTITYNAME);
        }

        return component1;
    }

    public DefaultTextField getEntityName() {

        if (entityName == null) {
            entityName = new DefaultTextField();
            entityName.setName("Entity_name");
        }

        return entityName;
    }

    public DefaultTabbedPane getMainTabbedPane() {

        if (mainTabbedPane == null) {
            mainTabbedPane = new DefaultTabbedPane();
            mainTabbedPane.addTab(null, getAttributesTab());
            mainTabbedPane.addTab(null, getIndexesTab());
            mainTabbedPane.addTab(null, getTableCommentsTab());
            mainTabbedPane.addTab(null, getTablePropertiesTab());
            mainTabbedPane.setName("MainTabbedPane");
            mainTabbedPane.setSelectedIndex(0);
        }

        return mainTabbedPane;
    }

    public DefaultTabbedPaneTab getAttributesTab() {

        if (attributesTab == null) {
            attributesTab = new DefaultTabbedPaneTab(mainTabbedPane, ERDesignerBundle.ATTRIBUTES);

            FormLayout theLayout = new FormLayout("fill:10dlu:grow,2dlu,60dlu,2dlu,60dlu", "fill:10dlu:grow,2dlu,p");
            attributesTab.setLayout(theLayout);

            CellConstraints cons = new CellConstraints();
            attributesTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
            attributesTable.setModel(attributeTableModel);
            attributesTable.getColumnModel().getColumn(0).setPreferredWidth(200);
            attributesTable.getColumnModel().getColumn(1).setPreferredWidth(100);
            attributesTable.getColumnModel().getColumn(2).setPreferredWidth(60);
            attributesTable.getColumnModel().getColumn(3).setPreferredWidth(60);
            attributesTable.getColumnModel().getColumn(4).setPreferredWidth(60);
            attributesTable.getColumnModel().getColumn(5).setPreferredWidth(50);
            attributesTable.getColumnModel().getColumn(6).setPreferredWidth(100);
            attributesTable.getColumnModel().getColumn(7).setPreferredWidth(100);
            attributesTable.getColumnModel().getColumn(8).setPreferredWidth(300);
            attributesTable.getTableHeader().setResizingAllowed(true);
            attributesTable.getTableHeader().setReorderingAllowed(false);
            attributesTable.setAutoResizeMode(DefaultTable.AUTO_RESIZE_OFF);
            attributesTable.setRowHeight(22);

            DefaultComboBox theBox = new DefaultComboBox();
            theBox.setBorder(BorderFactory.createEmptyBorder());
            theBox.setModel(dataTypeModel);
            attributesTable.setDefaultEditor(DataType.class, new DefaultCellEditor(theBox));
            attributesTable.setDefaultRenderer(DataType.class, ModelItemDefaultCellRenderer.getInstance());
            attributesTable.setDefaultRenderer(String.class, ModelItemDefaultCellRenderer.getInstance());
            attributesTable.setDefaultRenderer(Integer.class, ModelItemDefaultCellRenderer.getInstance());
            attributesTable.setDefaultRenderer(Boolean.class, CheckboxCellRenderer.getInstance());

            attributesTab.add(attributesTable.getScrollPane(), cons.xywh(1, 1, 5, 1));
            attributesTab.add(getNewButton(), cons.xy(3, 3));
            attributesTab.add(getDeleteButton(), cons.xy(5, 3));
        }

        return attributesTab;
    }

    public JButton getNewButton() {

        if (newButton == null) {
            newButton = new DefaultButton(ERDesignerBundle.NEW);
        }

        return newButton;
    }

    public JButton getDeleteButton() {

        if (deleteButton == null) {
            deleteButton = new DefaultButton(ERDesignerBundle.DELETE);
        }

        return deleteButton;
    }

    public DefaultTabbedPaneTab getIndexesTab() {

        if (indexesTab == null) {
            indexesTab = new DefaultTabbedPaneTab(mainTabbedPane, ERDesignerBundle.INDEXES);

            String rowDef = "2dlu,p,185dlu:grow,p,2dlu,p,2dlu";
            String colDef = "2dlu,50dlu:grow,2dlu,50dlu:grow,2dlu,50dlu:grow,11dlu:grow,2dlu,11dlu:grow,2dlu,80dlu:grow,2dlu,70dlu:grow,2dlu";

            FormLayout layout = new FormLayout(colDef, rowDef);
            indexesTab.setLayout(layout);

            CellConstraints cons = new CellConstraints();

            indexesTab.add(getIndexList().getScrollPane(), cons.xywh(2, 2, 8, 3));
            indexesTab.add(getNewIndexButton(), cons.xywh(2, 6, 1, 1));
            indexesTab.add(getDeleteIndexButton(), cons.xywh(6, 6, 4, 1));
            indexesTab.add(getIndexTabbedPane(), cons.xywh(11, 2, 3, 3));
            indexesTab.add(getUpdateIndexButton(), cons.xywh(13, 6, 1, 1));
            indexesTab.setName("IndexesTab");
            indexesTab.setVisible(false);
        }

        return indexesTab;
    }

    public DefaultList getIndexList() {

        if (indexList == null) {
            indexList = new DefaultList();
            indexList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        }

        return indexList;
    }

    public JButton getNewIndexButton() {

        if (newIndexButton == null) {
            newIndexButton = new DefaultButton(ERDesignerBundle.NEW);
        }

        return newIndexButton;
    }

    public JButton getDeleteIndexButton() {

        if (deleteIndexButton == null) {
            deleteIndexButton = new DefaultButton(ERDesignerBundle.DELETE);
        }

        return deleteIndexButton;
    }

    public DefaultTabbedPane getIndexTabbedPane() {

        if (indexTabbedPane == null) {
            indexTabbedPane = new DefaultTabbedPane();
            indexTabbedPane.addTab(null, getIndexGeneralTab());
            indexTabbedPane.addTab(null, getIndexPropertiesTab());
            indexTabbedPane.setName("IndexTabbedPane");
            indexTabbedPane.setSelectedIndex(0);
        }

        return indexTabbedPane;
    }

    public DefaultTabbedPaneTab getIndexGeneralTab() {

        if (indexGeneralTab == null) {
            indexGeneralTab = new DefaultTabbedPaneTab(indexTabbedPane, ERDesignerBundle.GENERAL);

            String colDef = "2dlu,left:40dlu,2dlu,60dlu:grow,2dlu,20dlu,2dlu";
            String rowDef = "2dlu,p,2dlu,fill:40dlu:grow,2dlu,p,2dlu,p,2dlu,p,4dlu,p,2dlu,p,2dlu,p,2dlu,p,2dlu,p,2dlu,p,2dlu,p,2dlu";

            FormLayout layout = new FormLayout(colDef, rowDef);
            indexGeneralTab.setLayout(layout);

            CellConstraints cons = new CellConstraints();

            indexGeneralTab.add(getLabel1(), cons.xywh(2, 2, 1, 1));
            indexGeneralTab.add(getIndexName(), cons.xywh(4, 2, 3, 1));

            indexGeneralTab.add(getIndexFieldList().getScrollPane(), cons.xywh(2, 4, 5, 1));

            indexGeneralTab.add(getRemoveFromIndexButton(), cons.xy(6, 6));

            indexGeneralTab.add(getAddIndexAttribute(), cons.xy(2, 8));
            indexGeneralTab.add(getIndexAttribute(), cons.xy(4, 8));
            indexGeneralTab.add(getAddAttributeToIndexButton(), cons.xy(6, 8));

            indexGeneralTab.add(getAddIndexExpression(), cons.xy(2, 10));
            indexGeneralTab.add(getIndexExpression(), cons.xy(4, 10));
            indexGeneralTab.add(getAddExpressionToIndexButton(), cons.xy(6, 10));

            indexGeneralTab.add(getPrimaryIndex(), cons.xywh(4, 12, 3, 1));
            indexGeneralTab.add(getUniqueIndex(), cons.xywh(4, 14, 3, 1));
            indexGeneralTab.add(getNotUniqueIndex(), cons.xywh(4, 16, 3, 1));
            indexGeneralTab.add(getSpatialIndex(), cons.xywh(4, 18, 3, 1));
            indexGeneralTab.add(getFulltextIndex(), cons.xywh(4, 20, 3, 1));
            indexGeneralTab.setName("IndexGeneralTab");
        }

        return indexGeneralTab;
    }

    public DefaultLabel getLabel1() {

        if (label1 == null) {
            label1 = new DefaultLabel(ERDesignerBundle.NAME);
        }

        return label1;
    }

    public DefaultTextField getIndexName() {

        if (indexName == null) {
            indexName = new DefaultTextField();
            indexName.setName("IndexName");
        }

        return indexName;
    }

    public DefaultRadioButton getUniqueIndex() {

        if (uniqueIndex == null) {
            uniqueIndex = new DefaultRadioButton(ERDesignerBundle.INDEXISUNIQUE);
        }

        return uniqueIndex;
    }

    public DefaultRadioButton getPrimaryIndex() {

        if (primaryKeyIndex == null) {
            primaryKeyIndex = new DefaultRadioButton(ERDesignerBundle.INDEXISPRIMARY);
        }

        return primaryKeyIndex;
    }

    public DefaultRadioButton getNotUniqueIndex() {

        if (notUniqueIndex == null) {
            notUniqueIndex = new DefaultRadioButton(ERDesignerBundle.INDEXISNOTUNIQUE);
        }

        return notUniqueIndex;
    }

    public DefaultRadioButton getSpatialIndex() {

        if (spatialIndex == null) {
            spatialIndex = new DefaultRadioButton(ERDesignerBundle.SPATIALINDEX);
        }

        return spatialIndex;
    }

    public DefaultRadioButton getFulltextIndex() {

        if (fulltextIndex == null) {
            fulltextIndex = new DefaultRadioButton(ERDesignerBundle.FULLTEXTINDEX);
        }

        return fulltextIndex;
    }

    public DefaultList<IndexExpression> getIndexFieldList() {

        if (indexAttributesList == null) {
            indexAttributesList = new DefaultList<>();
            indexAttributesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        }

        return indexAttributesList;
    }

    public DefaultButton getUpdateIndexButton() {

        if (updateIndexButton == null) {
            updateIndexButton = new DefaultButton(ERDesignerBundle.UPDATE);
        }

        return updateIndexButton;
    }

    public DefaultTabbedPaneTab getTableCommentsTab() {

        if (tableCommentsTab == null) {
            tableCommentsTab = new DefaultTabbedPaneTab(mainTabbedPane, ERDesignerBundle.COMMENTS);

            String rowDef = "2dlu,p,100dlu:grow,p,2dlu";
            String colDef = "2dlu,40dlu:grow,2dlu";

            FormLayout layout = new FormLayout(colDef, rowDef);
            tableCommentsTab.setLayout(layout);

            CellConstraints cons = new CellConstraints();

            tableCommentsTab.add(getEntityComment().getScrollPane(), cons.xywh(2, 2, 1, 3));
            tableCommentsTab.setName("MainCommentsTab");
            tableCommentsTab.setVisible(false);
        }

        return tableCommentsTab;
    }

    public DefaultTextArea getEntityComment() {

        if (tableComment == null) {
            tableComment = new DefaultTextArea();
            tableComment.setName("EntityComment");
        }

        return tableComment;
    }

    public JButton getOkButton() {

        if (okButton == null) {
            okButton = new DefaultButton(ERDesignerBundle.OK);
        }

        return okButton;
    }

    public JButton getCancelButton() {

        if (cancelButton == null) {
            cancelButton = new DefaultButton(ERDesignerBundle.CANCEL);
        }

        return cancelButton;
    }

    /**
     * Initialize method.
     */
    private void buildGroups() {

        ButtonGroup theGroup = new ButtonGroup();
        theGroup.add(getPrimaryIndex());
        theGroup.add(getUniqueIndex());
        theGroup.add(getNotUniqueIndex());
        theGroup.add(getSpatialIndex());
        theGroup.add(getFulltextIndex());

        ButtonGroup theGroup2 = new ButtonGroup();
        theGroup2.add(getAddIndexAttribute());
        theGroup2.add(getAddIndexExpression());
    }

    public DefaultComboBoxModel getDataTypeModel() {
        return dataTypeModel;
    }

    public DefaultComboBox getIndexAttribute() {
        return indexAttribute;
    }

    public DefaultRadioButton getAddIndexAttribute() {
        return addIndexAttribute;
    }

    public DefaultRadioButton getAddIndexExpression() {
        return addIndexExpression;
    }

    public DefaultButton getAddExpressionToIndexButton() {
        return addExpressionToIndexButton;
    }

    public DefaultButton getAddAttributeToIndexButton() {
        return addAttributeToIndexButton;
    }

    public DefaultTextField getIndexExpression() {
        return indexExpression;
    }

    public DefaultButton getRemoveFromIndexButton() {
        return removeFromIndexButton;
    }

    public DefaultTabbedPaneTab getTablePropertiesTab() {
        if (tablePropertiesTab == null) {
            tablePropertiesTab = new DefaultTabbedPaneTab(mainTabbedPane, ERDesignerBundle.PROPERTIES);
            tablePropertiesTab.setLayout(new BorderLayout());
        }
        return tablePropertiesTab;
    }

    public DefaultTabbedPaneTab getIndexPropertiesTab() {
        if (indexPropertiesTab == null) {
            indexPropertiesTab = new DefaultTabbedPaneTab(indexTabbedPane, ERDesignerBundle.PROPERTIES);
            indexPropertiesTab.setLayout(new BorderLayout());
        }
        return indexPropertiesTab;
    }

    public void disableTablePropertiesTab() {
        getMainTabbedPane().removeTabAt(3);
    }

    public void disableIndexPropertiesTab() {
        if (getIndexTabbedPane().getTabCount() > 1) {
            getIndexTabbedPane().removeTabAt(1);
        }
    }

    public void enableIndexPropertiesTab() {
        getIndexTabbedPane().addTab(null, getIndexPropertiesTab());
    }
}
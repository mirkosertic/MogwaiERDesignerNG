package de.erdesignerng.visual.editor.table;

import javax.swing.ButtonGroup;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.model.Attribute;
import de.mogwai.common.client.looks.components.DefaultButton;
import de.mogwai.common.client.looks.components.DefaultCheckBox;
import de.mogwai.common.client.looks.components.DefaultCheckBoxList;
import de.mogwai.common.client.looks.components.DefaultComboBox;
import de.mogwai.common.client.looks.components.DefaultLabel;
import de.mogwai.common.client.looks.components.DefaultList;
import de.mogwai.common.client.looks.components.DefaultPanel;
import de.mogwai.common.client.looks.components.DefaultRadioButton;
import de.mogwai.common.client.looks.components.DefaultScrollPane;
import de.mogwai.common.client.looks.components.DefaultSpinner;
import de.mogwai.common.client.looks.components.DefaultTabbedPane;
import de.mogwai.common.client.looks.components.DefaultTabbedPaneTab;
import de.mogwai.common.client.looks.components.DefaultTextArea;
import de.mogwai.common.client.looks.components.DefaultTextField;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-02-08 19:49:26 $
 */
public class TableEditorView extends DefaultPanel {

    private DefaultLabel component1;

    private DefaultTextField entityName;

    private DefaultTabbedPane mainTabbedPane;

    private DefaultTabbedPaneTab attributesTab;

    private DefaultList attributeList;

    private DefaultButton newButton;

    private DefaultButton deleteButton;

    private DefaultTabbedPane component15;

    private DefaultTabbedPaneTab attributesGeneralTab;

    private DefaultLabel component20;

    private DefaultTextField attributeName;

    private DefaultCheckBox nullable;

    private DefaultTabbedPaneTab optionsTab;

    private DefaultLabel component42;

    private DefaultTabbedPaneTab attributeCommentsTab;

    private DefaultTextArea attributeComments;

    private DefaultButton updateAttributeButton;

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

    private DefaultCheckBoxList<Attribute> indexAttributesList;

    private DefaultButton updateIndexButton;

    private DefaultTabbedPaneTab tableCommentsTab;

    private DefaultTextArea tableComment;

    private DefaultButton okButton;

    private DefaultButton cancelButton;
    
    private DefaultComboBox dataType = new DefaultComboBox();
    
    private DefaultSpinner sizeSpinner = new DefaultSpinner();
    
    private DefaultSpinner fractionSpinner = new DefaultSpinner();
    
    private DefaultSpinner scaleSpinner = new DefaultSpinner();
    
    private DefaultTextField defaultValue = new DefaultTextField();
    
    /**
     * Constructor.
     */
    public TableEditorView() {
        initialize();
    }

    /**
     * Initialize method.
     */
    private void initialize() {

        String rowDef = "2dlu,p,2dlu,p,fill:220dlu,p,20dlu,p,2dlu";
        String colDef = "2dlu,left:45dlu,2dlu,fill:140dlu:grow,fill:60dlu,2dlu,fill:60dlu,2dlu";

        FormLayout layout = new FormLayout(colDef, rowDef);
        setLayout(layout);

        CellConstraints cons = new CellConstraints();

        this.add(getComponent1(), cons.xywh(2, 2, 1, 1));
        this.add(getEntityName(), cons.xywh(4, 2, 4, 1));
        this.add(getMainTabbedPane(), cons.xywh(2, 4, 6, 2));
        this.add(getOkButton(), cons.xywh(5, 8, 1, 1));
        this.add(getCancelButton(), cons.xywh(7, 8, 1, 1));

        buildGroups();
    }

    /**
     * Getter method for component Component_1.
     * 
     * @return the initialized component
     */
    public javax.swing.JLabel getComponent1() {

        if (component1 == null) {
            component1 = new DefaultLabel(ERDesignerBundle.ENTITYNAME);
        }

        return component1;
    }

    /**
     * Getter method for component Entity_name.
     * 
     * @return the initialized component
     */
    public DefaultTextField getEntityName() {

        if (entityName == null) {
            entityName = new DefaultTextField();
            entityName.setName("Entity_name");
        }

        return entityName;
    }

    /**
     * Getter method for component MainTabbedPane.
     * 
     * @return the initialized component
     */
    public DefaultTabbedPane getMainTabbedPane() {

        if (mainTabbedPane == null) {
            mainTabbedPane = new DefaultTabbedPane();
            mainTabbedPane.addTab(null, getAttributesTab());
            mainTabbedPane.addTab(null, getIndexesTab());
            mainTabbedPane.addTab(null, getTableCommentsTab());
            mainTabbedPane.setName("MainTabbedPane");
            mainTabbedPane.setSelectedIndex(0);
        }

        return mainTabbedPane;
    }

    /**
     * Getter method for component AttributesTab.
     * 
     * @return the initialized component
     */
    public DefaultTabbedPaneTab getAttributesTab() {

        if (attributesTab == null) {
            attributesTab = new DefaultTabbedPaneTab(mainTabbedPane, ERDesignerBundle.ATTRIBUTES);

            String rowDef = "2dlu,p,2dlu,p,165dlu:grow,p,2dlu,p,2dlu";
            String colDef = "2dlu,50dlu:grow,2dlu,50dlu:grow,2dlu,25dlu:grow,11dlu:grow,2dlu,11dlu:grow,2dlu,80dlu:grow,2dlu,60dlu:grow,2dlu";

            FormLayout layout = new FormLayout(colDef, rowDef);
            attributesTab.setLayout(layout);

            CellConstraints cons = new CellConstraints();

            // this.m_attributestab.add(this.getUpButton(), cons.xywh(7, 2, 1,
            // 1));
            // this.m_attributestab.add(this.getDownButton(), cons
            // .xywh(9, 2, 1, 1));
            attributesTab.add(new DefaultScrollPane(getAttributeList()), cons.xywh(2, 4, 8, 3));
            attributesTab.add(getNewButton(), cons.xywh(2, 8, 1, 1));
            attributesTab.add(getDeleteButton(), cons.xywh(6, 8, 4, 1));
            attributesTab.add(getComponent15(), cons.xywh(11, 2, 3, 5));
            attributesTab.add(getUpdateAttributeButton(), cons.xywh(13, 8, 1, 1));
            attributesTab.setName("AttributesTab");
        }

        return attributesTab;
    }

    /**
     * Getter method for component AttributeList.
     * 
     * @return the initialized component
     */
    public DefaultList getAttributeList() {

        if (attributeList == null) {
            attributeList = new DefaultList();
        }

        return attributeList;
    }

    /**
     * Getter method for component NewButton.
     * 
     * @return the initialized component
     */
    public javax.swing.JButton getNewButton() {

        if (newButton == null) {
            newButton = new DefaultButton(ERDesignerBundle.NEW);
        }

        return newButton;
    }

    /**
     * Getter method for component DeleteButton.
     * 
     * @return the initialized component
     */
    public javax.swing.JButton getDeleteButton() {

        if (deleteButton == null) {
            deleteButton = new DefaultButton(ERDesignerBundle.DELETE);
        }

        return deleteButton;
    }

    /**
     * Getter method for component Component_15.
     * 
     * @return the initialized component
     */
    public DefaultTabbedPane getComponent15() {

        if (component15 == null) {
            component15 = new DefaultTabbedPane();
            component15.addTab(null, getAttributesGeneralTab());
            component15.addTab(null, getAttributeCommentTab());
            component15.setName("Component_15");
            component15.setSelectedIndex(0);
        }

        return component15;
    }

    /**
     * Getter method for component AttributesGeneralTab.
     * 
     * @return the initialized component
     */
    public DefaultTabbedPaneTab getAttributesGeneralTab() {

        if (attributesGeneralTab == null) {
            attributesGeneralTab = new DefaultTabbedPaneTab(component15, ERDesignerBundle.GENERAL);

            String rowDef = "2dlu,p,2dlu,p,10dlu,p,2dlu,p,2dlu,p,2dlu,p,2dlu,p,2dlu,p,2dlu";
            String colDef = "2dlu,left:40dlu,2dlu,60dlu:grow,2dlu";

            FormLayout layout = new FormLayout(colDef, rowDef);
            attributesGeneralTab.setLayout(layout);

            CellConstraints cons = new CellConstraints();

            attributesGeneralTab.add(getComponent20(), cons.xywh(2, 2, 1, 1));
            attributesGeneralTab.add(getAttributeName(), cons.xywh(4, 2, 1, 1));
            attributesGeneralTab.add(new DefaultLabel(ERDesignerBundle.DATATYPE), cons.xywh(2, 4, 1, 1));
            attributesGeneralTab.add(getDataType(), cons.xywh(4, 4, 1, 1));
            attributesGeneralTab.add(new DefaultLabel(ERDesignerBundle.SIZE), cons.xywh(2, 6, 1, 1));
            attributesGeneralTab.add(getSizeSpinner(), cons.xywh(4, 6, 1, 1));
            attributesGeneralTab.add(new DefaultLabel(ERDesignerBundle.FRACTION), cons.xywh(2, 8, 1, 1));
            attributesGeneralTab.add(getFractionSpinner(), cons.xywh(4, 8, 1, 1));
            attributesGeneralTab.add(new DefaultLabel(ERDesignerBundle.SCALE), cons.xywh(2, 10, 1, 1));
            attributesGeneralTab.add(getScaleSpinner(), cons.xywh(4, 10, 1, 1));
            
            attributesGeneralTab.add(getNullable(), cons.xywh(4, 12, 1, 1));
            
            attributesGeneralTab.add(new DefaultLabel(ERDesignerBundle.DEFAULT), cons.xywh(2, 14, 1, 1));
            attributesGeneralTab.add(getDefault(), cons.xywh(4, 14, 1, 1));
            attributesGeneralTab.add(new DefaultLabel(ERDesignerBundle.EXTRA), cons.xywh(2, 16, 1, 1));
            attributesGeneralTab.add(getExtra(), cons.xywh(4, 16, 1, 1));
            

            attributesGeneralTab.setName("AttributesGeneralTab");
        }

        return attributesGeneralTab;
    }

    /**
     * Getter method for component Component_20.
     * 
     * @return the initialized component
     */
    public javax.swing.JLabel getComponent20() {

        if (component20 == null) {
            component20 = new DefaultLabel(ERDesignerBundle.NAME);
        }

        return component20;
    }

    /**
     * Getter method for component AttributeName.
     * 
     * @return the initialized component
     */
    public DefaultTextField getAttributeName() {

        if (attributeName == null) {
            attributeName = new DefaultTextField();
            attributeName.setName("AttributeName");
        }

        return attributeName;
    }

    /**
     * Getter method for component Required.
     * 
     * @return the initialized component
     */
    public javax.swing.JCheckBox getNullable() {

        if (nullable == null) {
            nullable = new DefaultCheckBox(ERDesignerBundle.NULLABLE);
        }

        return nullable;
    }

    /**
     * Getter method for component Component_42.
     * 
     * @return the initialized component
     */
    public javax.swing.JLabel getComponent42() {

        if (component42 == null) {
            component42 = new DefaultLabel(ERDesignerBundle.DEFAULT);
        }

        return component42;
    }

    /**
     * Getter method for component Default.
     * 
     * @return the initialized component
     */
    public DefaultTextField getDefault() {

        if (defaultValue == null) {
            defaultValue = new DefaultTextField();
        }

        return defaultValue;
    }
    
    public DefaultTextField getExtra() {

        if (extra == null) {
            extra = new DefaultTextField();
        }

        return extra;
    }    

    /**
     * Getter method for component AttributeCommentTab.
     * 
     * @return the initialized component
     */
    public DefaultTabbedPaneTab getAttributeCommentTab() {

        if (attributeCommentsTab == null) {
            attributeCommentsTab = new DefaultTabbedPaneTab(component15, ERDesignerBundle.COMMENTS);

            String rowDef = "2dlu,p,160dlu:grow,p,2dlu";
            String colDef = "2dlu,left:40dlu,2dlu,60dlu:grow,2dlu";

            FormLayout layout = new FormLayout(colDef, rowDef);
            attributeCommentsTab.setLayout(layout);

            CellConstraints cons = new CellConstraints();

            attributeCommentsTab.add(new DefaultScrollPane(getAttributeComment()), cons.xywh(2, 2, 3, 3));
            attributeCommentsTab.setName("AttributeCommentTab");
            attributeCommentsTab.setVisible(false);
        }

        return attributeCommentsTab;
    }

    /**
     * Getter method for component AttributeComment.
     * 
     * @return the initialized component
     */
    public javax.swing.JTextArea getAttributeComment() {

        if (attributeComments == null) {
            attributeComments = new DefaultTextArea();
        }

        return attributeComments;
    }

    /**
     * Getter method for component UpdateAttributeButton.
     * 
     * @return the initialized component
     */
    public javax.swing.JButton getUpdateAttributeButton() {

        if (updateAttributeButton == null) {
            updateAttributeButton = new DefaultButton(ERDesignerBundle.UPDATE);
        }

        return updateAttributeButton;
    }

    /**
     * Getter method for component IndexesTab.
     * 
     * @return the initialized component
     */
    public DefaultTabbedPaneTab getIndexesTab() {

        if (indexesTab == null) {
            indexesTab = new DefaultTabbedPaneTab(mainTabbedPane, ERDesignerBundle.INDEXES);

            String rowDef = "2dlu,p,165dlu:grow,p,2dlu,p,2dlu";
            String colDef = "2dlu,50dlu:grow,2dlu,50dlu:grow,2dlu,25dlu:grow,11dlu:grow,2dlu,11dlu:grow,2dlu,80dlu:grow,2dlu,60dlu:grow,2dlu";

            FormLayout layout = new FormLayout(colDef, rowDef);
            indexesTab.setLayout(layout);

            CellConstraints cons = new CellConstraints();

            indexesTab.add(new DefaultScrollPane(getIndexList()), cons.xywh(2, 2, 8, 3));
            indexesTab.add(getNewIndexButton(), cons.xywh(2, 6, 1, 1));
            indexesTab.add(getDeleteIndexButton(), cons.xywh(6, 6, 4, 1));
            indexesTab.add(getIndexTabbedPane(), cons.xywh(11, 2, 3, 3));
            indexesTab.add(getUpdateIndexButton(), cons.xywh(13, 6, 1, 1));
            indexesTab.setName("IndexesTab");
            indexesTab.setVisible(false);
        }

        return indexesTab;
    }

    /**
     * Getter method for component IndexList.
     * 
     * @return the initialized component
     */
    public DefaultList getIndexList() {

        if (indexList == null) {
            indexList = new DefaultList();
            indexList.setName("IndexList");
        }

        return indexList;
    }

    /**
     * Getter method for component NewIndexButton.
     * 
     * @return the initialized component
     */
    public javax.swing.JButton getNewIndexButton() {

        if (newIndexButton == null) {
            newIndexButton = new DefaultButton(ERDesignerBundle.NEW);
        }

        return newIndexButton;
    }

    /**
     * Getter method for component DeleteIndexButton.
     * 
     * @return the initialized component
     */
    public javax.swing.JButton getDeleteIndexButton() {

        if (deleteIndexButton == null) {
            deleteIndexButton = new DefaultButton(ERDesignerBundle.DELETE);
        }

        return deleteIndexButton;
    }

    /**
     * Getter method for component IndexTabbedPane.
     * 
     * @return the initialized component
     */
    public DefaultTabbedPane getIndexTabbedPane() {

        if (indexTabbedPane == null) {
            indexTabbedPane = new DefaultTabbedPane();
            indexTabbedPane.addTab(null, getIndexGeneralTab());
            indexTabbedPane.setName("IndexTabbedPane");
            indexTabbedPane.setSelectedIndex(0);
        }

        return indexTabbedPane;
    }

    /**
     * Getter method for component IndexGeneralTab.
     * 
     * @return the initialized component
     */
    public DefaultTabbedPaneTab getIndexGeneralTab() {

        if (indexGeneralTab == null) {
            indexGeneralTab = new DefaultTabbedPaneTab(indexTabbedPane, ERDesignerBundle.GENERAL);

            String colDef = "2dlu,left:40dlu,2dlu,60dlu:grow,2dlu";
            String rowDef = "2dlu,p,2dlu,fill:20dlu:grow,2dlu,p,2dlu,p,2dlu,p,2dlu,p,8dlu";            

            FormLayout layout = new FormLayout(colDef, rowDef);
            indexGeneralTab.setLayout(layout);

            CellConstraints cons = new CellConstraints();

            indexGeneralTab.add(getLabel1(), cons.xywh(2, 2, 1, 1));
            indexGeneralTab.add(getIndexName(), cons.xywh(4, 2, 1, 1));
            indexGeneralTab.add(new DefaultScrollPane(getIndexFieldList()), cons.xywh(2, 4, 3, 1));
            indexGeneralTab.add(getPrimaryIndex(), cons.xywh(4, 8, 1, 1));            
            indexGeneralTab.add(getUniqueIndex(), cons.xywh(4, 10, 1, 1));
            indexGeneralTab.add(getNotUniqueIndex(), cons.xywh(4, 12, 1, 1));
            indexGeneralTab.setName("IndexGeneralTab");
        }

        return indexGeneralTab;
    }

    /**
     * Getter method for component Label1.
     * 
     * @return the initialized component
     */
    public DefaultLabel getLabel1() {

        if (label1 == null) {
            label1 = new DefaultLabel(ERDesignerBundle.NAME);
        }

        return label1;
    }

    /**
     * Getter method for component IndexName.
     * 
     * @return the initialized component
     */
    public DefaultTextField getIndexName() {

        if (indexName == null) {
            indexName = new DefaultTextField();
            indexName.setName("IndexName");
        }

        return indexName;
    }

    /**
     * Getter method for component UniqueIndex.
     * 
     * @return the initialized component
     */
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

    
    /**
     * Getter method for component NotUniqueIndex.
     * 
     * @return the initialized component
     */
    public DefaultRadioButton getNotUniqueIndex() {

        if (notUniqueIndex == null) {
            notUniqueIndex = new DefaultRadioButton(ERDesignerBundle.INDEXISNOTUNIQUE);
        }

        return notUniqueIndex;
    }

    /**
     * Getter method for component IndexFieldList.
     * 
     * @return the initialized component
     */
    public DefaultCheckBoxList<Attribute> getIndexFieldList() {

        if (indexAttributesList == null) {
            indexAttributesList = new DefaultCheckBoxList<Attribute>();
        }

        return indexAttributesList;
    }

    /**
     * Getter method for component UpdateIndexButton.
     * 
     * @return the initialized component
     */
    public DefaultButton getUpdateIndexButton() {

        if (updateIndexButton == null) {
            updateIndexButton = new DefaultButton(ERDesignerBundle.UPDATE);
        }

        return updateIndexButton;
    }

    /**
     * Getter method for component MainCommensTab.
     * 
     * @return the initialized component
     */
    public DefaultTabbedPaneTab getTableCommentsTab() {

        if (tableCommentsTab == null) {
            tableCommentsTab = new DefaultTabbedPaneTab(mainTabbedPane, ERDesignerBundle.COMMENTS);

            String rowDef = "2dlu,p,100dlu:grow,p,2dlu";
            String colDef = "2dlu,40dlu:grow,2dlu";

            FormLayout layout = new FormLayout(colDef, rowDef);
            tableCommentsTab.setLayout(layout);

            CellConstraints cons = new CellConstraints();

            tableCommentsTab.add(new DefaultScrollPane(getEntityComment()), cons.xywh(2, 2, 1, 3));
            tableCommentsTab.setName("MainCommensTab");
            tableCommentsTab.setVisible(false);
        }

        return tableCommentsTab;
    }

    /**
     * Getter method for component EntityComment.
     * 
     * @return the initialized component
     */
    public DefaultTextArea getEntityComment() {

        if (tableComment == null) {
            tableComment = new DefaultTextArea();
            tableComment.setName("EntityComment");
        }

        return tableComment;
    }

    /**
     * Getter method for component OkButton.
     * 
     * @return the initialized component
     */
    public javax.swing.JButton getOkButton() {

        if (okButton == null) {
            okButton = new DefaultButton(ERDesignerBundle.OK);
        }

        return okButton;
    }

    /**
     * Getter method for component CancelButton.
     * 
     * @return the initialized component
     */
    public javax.swing.JButton getCancelButton() {

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
    }

    /**
     * Getter for the group value for group Group1.
     * 
     * @return the value for the current selected item in the group or null if
     *         nothing was selected
     */
    public String getGroup1Value() {

        if (getUniqueIndex().isSelected()) {
            return "U";
        }
        if (getNotUniqueIndex().isSelected()) {
            return "NU";
        }
        return null;
    }

    /**
     * Setter for the group value for group Group1.
     * 
     * @param aValue
     *            value for the current selected item in the group or null if
     *            nothing is selected
     */
    public void setGroup1Value(String aValue) {

        getUniqueIndex().setSelected("U".equals(aValue));
        getNotUniqueIndex().setSelected("NU".equals(aValue));
    }

    /**
     * @return the dataType
     */
    public DefaultComboBox getDataType() {
        return dataType;
    }

    /**
     * @return the precisionSpinner
     */
    public DefaultSpinner getFractionSpinner() {
        return fractionSpinner;
    }

    /**
     * @return the scaleSpinner
     */
    public DefaultSpinner getScaleSpinner() {
        return scaleSpinner;
    }

    /**
     * @return the sizeSpinner
     */
    public DefaultSpinner getSizeSpinner() {
        return sizeSpinner;
    }
}
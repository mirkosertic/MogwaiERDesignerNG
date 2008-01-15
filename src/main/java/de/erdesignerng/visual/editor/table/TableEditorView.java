package de.erdesignerng.visual.editor.table;

import javax.swing.ButtonGroup;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.erdesignerng.ERDesignerBundle;
import de.mogwai.common.client.looks.components.DefaultButton;
import de.mogwai.common.client.looks.components.DefaultCheckBox;
import de.mogwai.common.client.looks.components.DefaultComboBox;
import de.mogwai.common.client.looks.components.DefaultLabel;
import de.mogwai.common.client.looks.components.DefaultList;
import de.mogwai.common.client.looks.components.DefaultPanel;
import de.mogwai.common.client.looks.components.DefaultRadioButton;
import de.mogwai.common.client.looks.components.DefaultScrollPane;
import de.mogwai.common.client.looks.components.DefaultTabbedPane;
import de.mogwai.common.client.looks.components.DefaultTabbedPaneTab;
import de.mogwai.common.client.looks.components.DefaultTextArea;
import de.mogwai.common.client.looks.components.DefaultTextField;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-01-15 20:04:24 $
 */
public class TableEditorView extends DefaultPanel {

    private DefaultLabel m_component_1;

    private DefaultTextField m_entity_name;

    private DefaultTabbedPane m_maintabbedpane;

    private DefaultTabbedPaneTab m_attributestab;

    private DefaultList m_attributelist;

    private DefaultButton m_newbutton;

    private DefaultButton m_deletebutton;

    private DefaultTabbedPane m_component_15;

    private DefaultTabbedPaneTab m_attributesgeneraltab;

    private DefaultLabel m_component_20;

    private DefaultTextField m_attributename;

    private DefaultList m_domainlist;

    private DefaultCheckBox m_primarykey;

    private DefaultCheckBox m_nullable;

    private javax.swing.JButton m_domaindictionary;

    private DefaultTabbedPaneTab m_optionstab;

    private DefaultLabel m_component_42;

    private DefaultComboBox m_default;

    private DefaultTabbedPaneTab m_attributecommenttab;

    private DefaultTextArea m_attributecomment;

    private DefaultButton m_updateattributebutton;

    private DefaultTabbedPaneTab m_indexestab;

    private DefaultList m_indexlist;

    private DefaultButton m_newindexbutton;

    private DefaultButton m_deleteindexbutton;

    private DefaultTabbedPane m_indextabbedpane;

    private DefaultTabbedPaneTab m_indexgeneraltab;

    private DefaultLabel m_label1;

    private DefaultTextField m_indexname;

    private DefaultRadioButton m_uniqueindex;

    private DefaultRadioButton m_notuniqueindex;

    private javax.swing.JTable m_indexfieldlist;

    private DefaultButton m_updateindexbutton;

    private DefaultTabbedPaneTab m_maincommenstab;

    private DefaultTextArea m_entitycomment;

    private DefaultButton m_okbutton;

    private DefaultButton m_cancelbutton;

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

        String rowDef = "2dlu,p,2dlu,p,220dlu,p,20dlu,p,2dlu";
        String colDef = "2dlu,left:45dlu,2dlu,140dlu:grow,50dlu:grow,2dlu,50dlu:grow,2dlu";

        FormLayout layout = new FormLayout(colDef, rowDef);
        setLayout(layout);

        CellConstraints cons = new CellConstraints();

        this.add(getComponent_1(), cons.xywh(2, 2, 1, 1));
        this.add(getEntity_name(), cons.xywh(4, 2, 4, 1));
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
    public javax.swing.JLabel getComponent_1() {

        if (m_component_1 == null) {
            m_component_1 = new DefaultLabel(ERDesignerBundle.ENTITYNAME);
        }

        return m_component_1;
    }

    /**
     * Getter method for component Entity_name.
     * 
     * @return the initialized component
     */
    public DefaultTextField getEntity_name() {

        if (m_entity_name == null) {
            m_entity_name = new DefaultTextField();
            m_entity_name.setName("Entity_name");
        }

        return m_entity_name;
    }

    /**
     * Getter method for component MainTabbedPane.
     * 
     * @return the initialized component
     */
    public DefaultTabbedPane getMainTabbedPane() {

        if (m_maintabbedpane == null) {
            m_maintabbedpane = new DefaultTabbedPane();
            m_maintabbedpane.addTab(null, getAttributesTab());
            m_maintabbedpane.addTab(null, getIndexesTab());
            m_maintabbedpane.addTab(null, getMainCommensTab());
            m_maintabbedpane.setName("MainTabbedPane");
            m_maintabbedpane.setSelectedIndex(0);
        }

        return m_maintabbedpane;
    }

    /**
     * Getter method for component AttributesTab.
     * 
     * @return the initialized component
     */
    public DefaultTabbedPaneTab getAttributesTab() {

        if (m_attributestab == null) {
            m_attributestab = new DefaultTabbedPaneTab(m_maintabbedpane, ERDesignerBundle.ATTRIBUTES);

            String rowDef = "2dlu,p,2dlu,p,165dlu:grow,p,2dlu,p,2dlu";
            String colDef = "2dlu,50dlu:grow,2dlu,50dlu:grow,2dlu,25dlu:grow,11dlu:grow,2dlu,11dlu:grow,2dlu,80dlu:grow,2dlu,60dlu:grow,2dlu";

            FormLayout layout = new FormLayout(colDef, rowDef);
            m_attributestab.setLayout(layout);

            CellConstraints cons = new CellConstraints();

            // this.m_attributestab.add(this.getUpButton(), cons.xywh(7, 2, 1,
            // 1));
            // this.m_attributestab.add(this.getDownButton(), cons
            // .xywh(9, 2, 1, 1));
            m_attributestab.add(new DefaultScrollPane(getAttributeList()), cons.xywh(2, 4, 8, 3));
            m_attributestab.add(getNewButton(), cons.xywh(2, 8, 1, 1));
            m_attributestab.add(getDeleteButton(), cons.xywh(6, 8, 4, 1));
            m_attributestab.add(getComponent_15(), cons.xywh(11, 2, 3, 5));
            m_attributestab.add(getUpdateAttributeButton(), cons.xywh(13, 8, 1, 1));
            m_attributestab.setName("AttributesTab");
        }

        return m_attributestab;
    }

    /**
     * Getter method for component AttributeList.
     * 
     * @return the initialized component
     */
    public DefaultList getAttributeList() {

        if (m_attributelist == null) {
            m_attributelist = new DefaultList();
        }

        return m_attributelist;
    }

    /**
     * Getter method for component NewButton.
     * 
     * @return the initialized component
     */
    public javax.swing.JButton getNewButton() {

        if (m_newbutton == null) {
            m_newbutton = new DefaultButton(ERDesignerBundle.NEW);
        }

        return m_newbutton;
    }

    /**
     * Getter method for component DeleteButton.
     * 
     * @return the initialized component
     */
    public javax.swing.JButton getDeleteButton() {

        if (m_deletebutton == null) {
            m_deletebutton = new DefaultButton(ERDesignerBundle.DELETE);
        }

        return m_deletebutton;
    }

    /**
     * Getter method for component Component_15.
     * 
     * @return the initialized component
     */
    public DefaultTabbedPane getComponent_15() {

        if (m_component_15 == null) {
            m_component_15 = new DefaultTabbedPane();
            m_component_15.addTab(null, getAttributesGeneralTab());
            m_component_15.addTab(null, getOptionsTab());
            m_component_15.addTab(null, getAttributeCommentTab());
            m_component_15.setName("Component_15");
            m_component_15.setSelectedIndex(0);
        }

        return m_component_15;
    }

    /**
     * Getter method for component AttributesGeneralTab.
     * 
     * @return the initialized component
     */
    public DefaultTabbedPaneTab getAttributesGeneralTab() {

        if (m_attributesgeneraltab == null) {
            m_attributesgeneraltab = new DefaultTabbedPaneTab(m_component_15, ERDesignerBundle.GENERAL);

            String rowDef = "2dlu,p,2dlu,p,100dlu:grow,p,2dlu,p,2dlu,p,2dlu,p";
            String colDef = "2dlu,left:40dlu,2dlu,60dlu:grow,2dlu";

            FormLayout layout = new FormLayout(colDef, rowDef);
            m_attributesgeneraltab.setLayout(layout);

            CellConstraints cons = new CellConstraints();

            m_attributesgeneraltab.add(getComponent_20(), cons.xywh(2, 2, 1, 1));
            m_attributesgeneraltab.add(getAttributeName(), cons.xywh(4, 2, 1, 1));
            m_attributesgeneraltab.add(new DefaultScrollPane(getDomainList()), cons.xywh(2, 4, 3, 3));
            m_attributesgeneraltab.add(getPrimaryKey(), cons.xywh(4, 8, 1, 1));
            m_attributesgeneraltab.add(getNullable(), cons.xywh(4, 10, 1, 1));
            // this.m_attributesgeneraltab.add(this.getDomainDictionary(), cons
            // .xywh(4, 12, 1, 1));
            m_attributesgeneraltab.setName("AttributesGeneralTab");
        }

        return m_attributesgeneraltab;
    }

    /**
     * Getter method for component Component_20.
     * 
     * @return the initialized component
     */
    public javax.swing.JLabel getComponent_20() {

        if (m_component_20 == null) {
            m_component_20 = new DefaultLabel(ERDesignerBundle.NAME);
        }

        return m_component_20;
    }

    /**
     * Getter method for component AttributeName.
     * 
     * @return the initialized component
     */
    public DefaultTextField getAttributeName() {

        if (m_attributename == null) {
            m_attributename = new DefaultTextField();
            m_attributename.setName("AttributeName");
        }

        return m_attributename;
    }

    /**
     * Getter method for component DomainList.
     * 
     * @return the initialized component
     */
    public DefaultList getDomainList() {

        if (m_domainlist == null) {
            m_domainlist = new DefaultList();
        }

        return m_domainlist;
    }

    /**
     * Getter method for component PrimaryKey.
     * 
     * @return the initialized component
     */
    public javax.swing.JCheckBox getPrimaryKey() {

        if (m_primarykey == null) {
            m_primarykey = new DefaultCheckBox(ERDesignerBundle.PRIMARYKEY);
        }

        return m_primarykey;
    }

    /**
     * Getter method for component Required.
     * 
     * @return the initialized component
     */
    public javax.swing.JCheckBox getNullable() {

        if (m_nullable == null) {
            m_nullable = new DefaultCheckBox(ERDesignerBundle.REQUIRED);
        }

        return m_nullable;
    }

    /**
     * Getter method for component DomainDictionary.
     * 
     * @return the initialized component
     */
    public javax.swing.JButton getDomainDictionary() {

        if (m_domaindictionary == null) {
            m_domaindictionary = new javax.swing.JButton();
            m_domaindictionary.setActionCommand("Domain dictionary ...");
            m_domaindictionary.setName("DomainDictionary");
            m_domaindictionary.setText("Domain dictionary ...");
        }

        return m_domaindictionary;
    }

    /**
     * Getter method for component OptionsTab.
     * 
     * @return the initialized component
     */
    public DefaultTabbedPaneTab getOptionsTab() {

        if (m_optionstab == null) {
            m_optionstab = new DefaultTabbedPaneTab(m_component_15, ERDesignerBundle.OPTIONS);

            String rowDef = "2dlu,p,2dlu";
            String colDef = "2dlu,left:40dlu,2dlu,60dlu:grow,2dlu";

            FormLayout layout = new FormLayout(colDef, rowDef);
            m_optionstab.setLayout(layout);

            CellConstraints cons = new CellConstraints();

            m_optionstab.add(getComponent_42(), cons.xywh(2, 2, 1, 1));
            m_optionstab.add(getDefault(), cons.xywh(4, 2, 1, 1));
            m_optionstab.setName("OptionsTab");
            m_optionstab.setVisible(false);
        }

        return m_optionstab;
    }

    /**
     * Getter method for component Component_42.
     * 
     * @return the initialized component
     */
    public javax.swing.JLabel getComponent_42() {

        if (m_component_42 == null) {
            m_component_42 = new DefaultLabel(ERDesignerBundle.DEFAULT);
        }

        return m_component_42;
    }

    /**
     * Getter method for component Default.
     * 
     * @return the initialized component
     */
    public javax.swing.JComboBox getDefault() {

        if (m_default == null) {
            m_default = new DefaultComboBox();
        }

        return m_default;
    }

    /**
     * Getter method for component AttributeCommentTab.
     * 
     * @return the initialized component
     */
    public DefaultTabbedPaneTab getAttributeCommentTab() {

        if (m_attributecommenttab == null) {
            m_attributecommenttab = new DefaultTabbedPaneTab(m_component_15, ERDesignerBundle.COMMENTS);

            String rowDef = "2dlu,p,160dlu:grow,p,2dlu";
            String colDef = "2dlu,left:40dlu,2dlu,60dlu:grow,2dlu";

            FormLayout layout = new FormLayout(colDef, rowDef);
            m_attributecommenttab.setLayout(layout);

            CellConstraints cons = new CellConstraints();

            m_attributecommenttab.add(new DefaultScrollPane(getAttributeComment()), cons.xywh(2, 2, 3, 3));
            m_attributecommenttab.setName("AttributeCommentTab");
            m_attributecommenttab.setVisible(false);
        }

        return m_attributecommenttab;
    }

    /**
     * Getter method for component AttributeComment.
     * 
     * @return the initialized component
     */
    public javax.swing.JTextArea getAttributeComment() {

        if (m_attributecomment == null) {
            m_attributecomment = new DefaultTextArea();
        }

        return m_attributecomment;
    }

    /**
     * Getter method for component UpdateAttributeButton.
     * 
     * @return the initialized component
     */
    public javax.swing.JButton getUpdateAttributeButton() {

        if (m_updateattributebutton == null) {
            m_updateattributebutton = new DefaultButton(ERDesignerBundle.UPDATE);
        }

        return m_updateattributebutton;
    }

    /**
     * Getter method for component IndexesTab.
     * 
     * @return the initialized component
     */
    public DefaultTabbedPaneTab getIndexesTab() {

        if (m_indexestab == null) {
            m_indexestab = new DefaultTabbedPaneTab(m_maintabbedpane, ERDesignerBundle.INDEXES);

            String rowDef = "2dlu,p,165dlu:grow,p,2dlu,p,2dlu";
            String colDef = "2dlu,50dlu:grow,2dlu,50dlu:grow,2dlu,25dlu:grow,11dlu:grow,2dlu,11dlu:grow,2dlu,80dlu:grow,2dlu,60dlu:grow,2dlu";

            FormLayout layout = new FormLayout(colDef, rowDef);
            m_indexestab.setLayout(layout);

            CellConstraints cons = new CellConstraints();

            m_indexestab.add(new DefaultScrollPane(getIndexList()), cons.xywh(2, 2, 8, 3));
            m_indexestab.add(getNewIndexButton(), cons.xywh(2, 6, 1, 1));
            m_indexestab.add(getDeleteIndexButton(), cons.xywh(6, 6, 4, 1));
            m_indexestab.add(getIndexTabbedPane(), cons.xywh(11, 2, 3, 3));
            m_indexestab.add(getUpdateIndexButton(), cons.xywh(13, 6, 1, 1));
            m_indexestab.setName("IndexesTab");
            m_indexestab.setVisible(false);
        }

        return m_indexestab;
    }

    /**
     * Getter method for component IndexList.
     * 
     * @return the initialized component
     */
    public DefaultList getIndexList() {

        if (m_indexlist == null) {
            m_indexlist = new DefaultList();
            m_indexlist.setName("IndexList");
        }

        return m_indexlist;
    }

    /**
     * Getter method for component NewIndexButton.
     * 
     * @return the initialized component
     */
    public javax.swing.JButton getNewIndexButton() {

        if (m_newindexbutton == null) {
            m_newindexbutton = new DefaultButton(ERDesignerBundle.NEW);
        }

        return m_newindexbutton;
    }

    /**
     * Getter method for component DeleteIndexButton.
     * 
     * @return the initialized component
     */
    public javax.swing.JButton getDeleteIndexButton() {

        if (m_deleteindexbutton == null) {
            m_deleteindexbutton = new DefaultButton(ERDesignerBundle.DELETE);
        }

        return m_deleteindexbutton;
    }

    /**
     * Getter method for component IndexTabbedPane.
     * 
     * @return the initialized component
     */
    public DefaultTabbedPane getIndexTabbedPane() {

        if (m_indextabbedpane == null) {
            m_indextabbedpane = new DefaultTabbedPane();
            m_indextabbedpane.addTab(null, getIndexGeneralTab());
            m_indextabbedpane.setName("IndexTabbedPane");
            m_indextabbedpane.setSelectedIndex(0);
        }

        return m_indextabbedpane;
    }

    /**
     * Getter method for component IndexGeneralTab.
     * 
     * @return the initialized component
     */
    public DefaultTabbedPaneTab getIndexGeneralTab() {

        if (m_indexgeneraltab == null) {
            m_indexgeneraltab = new DefaultTabbedPaneTab(m_indextabbedpane, ERDesignerBundle.GENERAL);

            String rowDef = "2dlu,p,2dlu,p,100dlu:grow,p,2dlu,p,2dlu,p,2dlu";
            String colDef = "2dlu,left:40dlu,2dlu,60dlu:grow,2dlu";

            FormLayout layout = new FormLayout(colDef, rowDef);
            m_indexgeneraltab.setLayout(layout);

            CellConstraints cons = new CellConstraints();

            m_indexgeneraltab.add(getLabel1(), cons.xywh(2, 2, 1, 1));
            m_indexgeneraltab.add(getIndexName(), cons.xywh(4, 2, 1, 1));
            m_indexgeneraltab.add(getUniqueIndex(), cons.xywh(4, 8, 1, 1));
            m_indexgeneraltab.add(getNotUniqueIndex(), cons.xywh(4, 10, 1, 1));
            m_indexgeneraltab.add(new DefaultScrollPane(getIndexFieldList()), cons.xywh(2, 4, 3, 3));
            m_indexgeneraltab.setName("IndexGeneralTab");
        }

        return m_indexgeneraltab;
    }

    /**
     * Getter method for component Label1.
     * 
     * @return the initialized component
     */
    public DefaultLabel getLabel1() {

        if (m_label1 == null) {
            m_label1 = new DefaultLabel(ERDesignerBundle.NAME);
        }

        return m_label1;
    }

    /**
     * Getter method for component IndexName.
     * 
     * @return the initialized component
     */
    public DefaultTextField getIndexName() {

        if (m_indexname == null) {
            m_indexname = new DefaultTextField();
            m_indexname.setName("IndexName");
        }

        return m_indexname;
    }

    /**
     * Getter method for component UniqueIndex.
     * 
     * @return the initialized component
     */
    public DefaultRadioButton getUniqueIndex() {

        if (m_uniqueindex == null) {
            m_uniqueindex = new DefaultRadioButton(ERDesignerBundle.INDEXISUNIQUE);
        }

        return m_uniqueindex;
    }

    /**
     * Getter method for component NotUniqueIndex.
     * 
     * @return the initialized component
     */
    public DefaultRadioButton getNotUniqueIndex() {

        if (m_notuniqueindex == null) {
            m_notuniqueindex = new DefaultRadioButton(ERDesignerBundle.INDEXISNOTUNIQUE);
        }

        return m_notuniqueindex;
    }

    /**
     * Getter method for component IndexFieldList.
     * 
     * @return the initialized component
     */
    public javax.swing.JTable getIndexFieldList() {

        if (m_indexfieldlist == null) {
            m_indexfieldlist = new javax.swing.JTable() {
                /**
                 * public TableCellRenderer getCellRenderer(int row, int column) {
                 * return new AttributeSelectListCellRenderer(); }
                 * 
                 * public TableCellEditor getCellEditor(int row, int column) {
                 * 
                 * AttributeSelectListCellRenderer.Item item =
                 * (AttributeSelectListCellRenderer.Item) this
                 * .getModel().getValueAt(row, column);
                 * 
                 * AttributeSelectListCellEditor editor = new
                 * AttributeSelectListCellEditor( item);
                 * 
                 * return editor; }
                 */
            };
            m_indexfieldlist.setName("IndexFieldList");
            m_indexfieldlist.setShowGrid(false);
            m_indexfieldlist.setOpaque(false);
        }

        return m_indexfieldlist;
    }

    /**
     * Getter method for component UpdateIndexButton.
     * 
     * @return the initialized component
     */
    public DefaultButton getUpdateIndexButton() {

        if (m_updateindexbutton == null) {
            m_updateindexbutton = new DefaultButton(ERDesignerBundle.UPDATE);
        }

        return m_updateindexbutton;
    }

    /**
     * Getter method for component MainCommensTab.
     * 
     * @return the initialized component
     */
    public DefaultTabbedPaneTab getMainCommensTab() {

        if (m_maincommenstab == null) {
            m_maincommenstab = new DefaultTabbedPaneTab(m_maintabbedpane, ERDesignerBundle.COMMENTS);

            String rowDef = "2dlu,p,100dlu:grow,p,2dlu";
            String colDef = "2dlu,40dlu:grow,2dlu";

            FormLayout layout = new FormLayout(colDef, rowDef);
            m_maincommenstab.setLayout(layout);

            CellConstraints cons = new CellConstraints();

            m_maincommenstab.add(new DefaultScrollPane(getEntityComment()), cons.xywh(2, 2, 1, 3));
            m_maincommenstab.setName("MainCommensTab");
            m_maincommenstab.setVisible(false);
        }

        return m_maincommenstab;
    }

    /**
     * Getter method for component EntityComment.
     * 
     * @return the initialized component
     */
    public DefaultTextArea getEntityComment() {

        if (m_entitycomment == null) {
            m_entitycomment = new DefaultTextArea();
            m_entitycomment.setName("EntityComment");
        }

        return m_entitycomment;
    }

    /**
     * Getter method for component OkButton.
     * 
     * @return the initialized component
     */
    public javax.swing.JButton getOkButton() {

        if (m_okbutton == null) {
            m_okbutton = new DefaultButton(ERDesignerBundle.OK);
        }

        return m_okbutton;
    }

    /**
     * Getter method for component CancelButton.
     * 
     * @return the initialized component
     */
    public javax.swing.JButton getCancelButton() {

        if (m_cancelbutton == null) {
            m_cancelbutton = new DefaultButton(ERDesignerBundle.CANCEL);
        }

        return m_cancelbutton;
    }

    /**
     * Initialize method.
     */
    private void buildGroups() {

        ButtonGroup Group1 = new ButtonGroup();
        Group1.add(getUniqueIndex());
        Group1.add(getNotUniqueIndex());
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
}

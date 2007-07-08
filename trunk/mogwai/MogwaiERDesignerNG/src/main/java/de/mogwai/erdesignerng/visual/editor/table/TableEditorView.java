package de.mogwai.erdesignerng.visual.editor.table;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.mogwai.erdesignerng.visual.IconFactory;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2007-07-08 17:55:43 $
 */
public class TableEditorView extends JPanel {

	private javax.swing.JLabel m_component_1;

	private javax.swing.JTextField m_entity_name;

	private javax.swing.JTabbedPane m_maintabbedpane;

	private javax.swing.JPanel m_attributestab;

	private javax.swing.JButton m_upbutton;

	private javax.swing.JButton m_downbutton;

	private javax.swing.JList m_attributelist;

	private javax.swing.JButton m_newbutton;

	private javax.swing.JButton m_renamebutton;

	private javax.swing.JButton m_deletebutton;

	private javax.swing.JTabbedPane m_component_15;

	private javax.swing.JPanel m_attributesgeneraltab;

	private javax.swing.JLabel m_component_20;

	private javax.swing.JTextField m_attributename;

	private javax.swing.JList m_domainlist;

	private javax.swing.JCheckBox m_primarykey;

	private javax.swing.JCheckBox m_nullable;

	private javax.swing.JButton m_domaindictionary;

	private javax.swing.JPanel m_optionstab;

	private javax.swing.JLabel m_component_42;

	private javax.swing.JComboBox m_default;

	private javax.swing.JPanel m_attributecommenttab;

	private javax.swing.JTextArea m_attributecomment;

	private javax.swing.JButton m_updateattributebutton;

	private javax.swing.JPanel m_indexestab;

	private javax.swing.JList m_indexlist;

	private javax.swing.JButton m_newindexbutton;

	private javax.swing.JButton m_renameindexbutton;

	private javax.swing.JButton m_deleteindexbutton;

	private javax.swing.JTabbedPane m_indextabbedpane;

	private javax.swing.JPanel m_indexgeneraltab;

	private javax.swing.JLabel m_label1;

	private javax.swing.JTextField m_indexname;

	private javax.swing.JRadioButton m_uniqueindex;

	private javax.swing.JRadioButton m_notuniqueindex;

	private javax.swing.JTable m_indexfieldlist;

	private javax.swing.JButton m_updateindexbutton;

	private javax.swing.JPanel m_maincommenstab;

	private javax.swing.JTextArea m_entitycomment;

	private javax.swing.JButton m_okbutton;

	private javax.swing.JButton m_cancelbutton;

	/**
	 * Constructor.
	 */
	public TableEditorView() {
		this.initialize();
	}

	/**
	 * Initialize method.
	 */
	private void initialize() {

		String rowDef = "2dlu,p,2dlu,p,220dlu,p,20dlu,p,2dlu";
		String colDef = "2dlu,left:45dlu,2dlu,140dlu:grow,50dlu:grow,2dlu,50dlu:grow,2dlu";

		FormLayout layout = new FormLayout(colDef, rowDef);
		this.setLayout(layout);

		CellConstraints cons = new CellConstraints();

		this.add(this.getComponent_1(), cons.xywh(2, 2, 1, 1));
		this.add(this.getEntity_name(), cons.xywh(4, 2, 4, 1));
		this.add(this.getMainTabbedPane(), cons.xywh(2, 4, 6, 2));
		this.add(this.getOkButton(), cons.xywh(5, 8, 1, 1));
		this.add(this.getCancelButton(), cons.xywh(7, 8, 1, 1));

		this.buildGroups();
	}

	/**
	 * Getter method for component Component_1.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JLabel getComponent_1() {

		if (this.m_component_1 == null) {
			this.m_component_1 = new javax.swing.JLabel();
			this.m_component_1.setName("Component_1");
			this.m_component_1.setText("Entity name :");
		}

		return this.m_component_1;
	}

	/**
	 * Getter method for component Entity_name.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JTextField getEntity_name() {

		if (this.m_entity_name == null) {
			this.m_entity_name = new javax.swing.JTextField();
			this.m_entity_name.setName("Entity_name");
		}

		return this.m_entity_name;
	}

	/**
	 * Getter method for component MainTabbedPane.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JTabbedPane getMainTabbedPane() {

		if (this.m_maintabbedpane == null) {
			this.m_maintabbedpane = new javax.swing.JTabbedPane();
			this.m_maintabbedpane.addTab("Attributes", this.getAttributesTab());
			// this.m_maintabbedpane.addTab("Indexes", this.getIndexesTab());
			this.m_maintabbedpane.addTab("Comments", this.getMainCommensTab());
			this.m_maintabbedpane.setName("MainTabbedPane");
			this.m_maintabbedpane.setSelectedIndex(0);
		}

		return this.m_maintabbedpane;
	}

	/**
	 * Getter method for component AttributesTab.
	 * 
	 * @return the initialized component
	 */
	public JPanel getAttributesTab() {

		if (this.m_attributestab == null) {
			this.m_attributestab = new JPanel();

			String rowDef = "2dlu,p,2dlu,p,165dlu:grow,p,2dlu,p,2dlu";
			String colDef = "2dlu,50dlu:grow,2dlu,50dlu:grow,2dlu,25dlu:grow,11dlu:grow,2dlu,11dlu:grow,2dlu,80dlu:grow,2dlu,60dlu:grow,2dlu";

			FormLayout layout = new FormLayout(colDef, rowDef);
			this.m_attributestab.setLayout(layout);

			CellConstraints cons = new CellConstraints();

			// this.m_attributestab.add(this.getUpButton(), cons.xywh(7, 2, 1,
			// 1));
			// this.m_attributestab.add(this.getDownButton(), cons
			// .xywh(9, 2, 1, 1));
			this.m_attributestab.add(new JScrollPane(this.getAttributeList()),
					cons.xywh(2, 4, 8, 3));
			this.m_attributestab
					.add(this.getNewButton(), cons.xywh(2, 8, 1, 1));
			this.m_attributestab.add(this.getRenameButton(), cons.xywh(4, 8, 1,
					1));
			this.m_attributestab.add(this.getDeleteButton(), cons.xywh(6, 8, 4,
					1));
			this.m_attributestab.add(this.getComponent_15(), cons.xywh(11, 2,
					3, 5));
			this.m_attributestab.add(this.getUpdateAttributeButton(), cons
					.xywh(13, 8, 1, 1));
			this.m_attributestab.setName("AttributesTab");
		}

		return this.m_attributestab;
	}

	/**
	 * Getter method for component UpButton.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JButton getUpButton() {

		if (this.m_upbutton == null) {
			this.m_upbutton = new javax.swing.JButton();
			this.m_upbutton.setFocusPainted(false);
			this.m_upbutton.setIcon(IconFactory.getArrowUpIcon());
			this.m_upbutton.setName("UpButton");
		}

		return this.m_upbutton;
	}

	/**
	 * Getter method for component DownButton.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JButton getDownButton() {

		if (this.m_downbutton == null) {
			this.m_downbutton = new javax.swing.JButton();
			this.m_downbutton.setIcon(IconFactory.getArrowDownIcon());
			this.m_downbutton.setName("DownButton");
		}

		return this.m_downbutton;
	}

	/**
	 * Getter method for component AttributeList.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JList getAttributeList() {

		if (this.m_attributelist == null) {
			this.m_attributelist = new javax.swing.JList();
			this.m_attributelist.setName("AttributeList");
		}

		return this.m_attributelist;
	}

	/**
	 * Getter method for component NewButton.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JButton getNewButton() {

		if (this.m_newbutton == null) {
			this.m_newbutton = new javax.swing.JButton();
			this.m_newbutton.setActionCommand("New");
			this.m_newbutton.setIcon(IconFactory.getNewIcon());
			this.m_newbutton.setName("NewButton");
			this.m_newbutton.setText("New");
		}

		return this.m_newbutton;
	}

	/**
	 * Getter method for component RenameButton.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JButton getRenameButton() {

		if (this.m_renamebutton == null) {
			this.m_renamebutton = new javax.swing.JButton();
			this.m_renamebutton.setActionCommand("Rename");
			this.m_renamebutton.setName("RenameButton");
			this.m_renamebutton.setText("Rename");
		}

		return this.m_renamebutton;
	}

	/**
	 * Getter method for component DeleteButton.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JButton getDeleteButton() {

		if (this.m_deletebutton == null) {
			this.m_deletebutton = new javax.swing.JButton();
			this.m_deletebutton.setActionCommand("Delete");
			this.m_deletebutton.setIcon(IconFactory.getDeleteIcon());
			this.m_deletebutton.setName("DeleteButton");
			this.m_deletebutton.setText("Delete");
		}

		return this.m_deletebutton;
	}

	/**
	 * Getter method for component Component_15.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JTabbedPane getComponent_15() {

		if (this.m_component_15 == null) {
			this.m_component_15 = new javax.swing.JTabbedPane();
			this.m_component_15.addTab("General", this
					.getAttributesGeneralTab());
			this.m_component_15.addTab("Options", this.getOptionsTab());
			this.m_component_15
					.addTab("Comment", this.getAttributeCommentTab());
			this.m_component_15.addTab("General", this
					.getAttributesGeneralTab());
			this.m_component_15.addTab("Options", this.getOptionsTab());
			this.m_component_15
					.addTab("Comment", this.getAttributeCommentTab());
			this.m_component_15.addTab("General", this
					.getAttributesGeneralTab());
			this.m_component_15.addTab("Options", this.getOptionsTab());
			this.m_component_15
					.addTab("Comment", this.getAttributeCommentTab());
			this.m_component_15.setName("Component_15");
			this.m_component_15.setSelectedIndex(0);
		}

		return this.m_component_15;
	}

	/**
	 * Getter method for component AttributesGeneralTab.
	 * 
	 * @return the initialized component
	 */
	public JPanel getAttributesGeneralTab() {

		if (this.m_attributesgeneraltab == null) {
			this.m_attributesgeneraltab = new JPanel();

			String rowDef = "2dlu,p,2dlu,p,100dlu:grow,p,2dlu,p,2dlu,p,2dlu,p";
			String colDef = "2dlu,left:40dlu,2dlu,60dlu:grow,2dlu";

			FormLayout layout = new FormLayout(colDef, rowDef);
			this.m_attributesgeneraltab.setLayout(layout);

			CellConstraints cons = new CellConstraints();

			this.m_attributesgeneraltab.add(this.getComponent_20(), cons.xywh(
					2, 2, 1, 1));
			this.m_attributesgeneraltab.add(this.getAttributeName(), cons.xywh(
					4, 2, 1, 1));
			this.m_attributesgeneraltab.add(new JScrollPane(this
					.getDomainList()), cons.xywh(2, 4, 3, 3));
			this.m_attributesgeneraltab.add(this.getPrimaryKey(), cons.xywh(4,
					8, 1, 1));
			this.m_attributesgeneraltab.add(this.getNullable(), cons.xywh(4,
					10, 1, 1));
			// this.m_attributesgeneraltab.add(this.getDomainDictionary(), cons
			// .xywh(4, 12, 1, 1));
			this.m_attributesgeneraltab.setName("AttributesGeneralTab");
		}

		return this.m_attributesgeneraltab;
	}

	/**
	 * Getter method for component Component_20.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JLabel getComponent_20() {

		if (this.m_component_20 == null) {
			this.m_component_20 = new javax.swing.JLabel();
			this.m_component_20.setName("Component_20");
			this.m_component_20.setText("Name :");
		}

		return this.m_component_20;
	}

	/**
	 * Getter method for component AttributeName.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JTextField getAttributeName() {

		if (this.m_attributename == null) {
			this.m_attributename = new javax.swing.JTextField();
			this.m_attributename.setName("AttributeName");
		}

		return this.m_attributename;
	}

	/**
	 * Getter method for component DomainList.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JList getDomainList() {

		if (this.m_domainlist == null) {
			this.m_domainlist = new javax.swing.JList();
			this.m_domainlist.setName("DomainList");
		}

		return this.m_domainlist;
	}

	/**
	 * Getter method for component PrimaryKey.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JCheckBox getPrimaryKey() {

		if (this.m_primarykey == null) {
			this.m_primarykey = new javax.swing.JCheckBox();
			this.m_primarykey.setActionCommand("Is primary key");
			this.m_primarykey.setName("PrimaryKey");
			this.m_primarykey.setText("Is primary key");
		}

		return this.m_primarykey;
	}

	/**
	 * Getter method for component Required.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JCheckBox getNullable() {

		if (this.m_nullable == null) {
			this.m_nullable = new javax.swing.JCheckBox();
			this.m_nullable.setActionCommand("Is required (NOT NULL)");
			this.m_nullable.setName("Required");
			this.m_nullable.setText("Is Nullable");
		}

		return this.m_nullable;
	}

	/**
	 * Getter method for component DomainDictionary.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JButton getDomainDictionary() {

		if (this.m_domaindictionary == null) {
			this.m_domaindictionary = new javax.swing.JButton();
			this.m_domaindictionary.setActionCommand("Domain dictionary ...");
			this.m_domaindictionary.setName("DomainDictionary");
			this.m_domaindictionary.setText("Domain dictionary ...");
		}

		return this.m_domaindictionary;
	}

	/**
	 * Getter method for component OptionsTab.
	 * 
	 * @return the initialized component
	 */
	public JPanel getOptionsTab() {

		if (this.m_optionstab == null) {
			this.m_optionstab = new JPanel();

			String rowDef = "2dlu,p,2dlu";
			String colDef = "2dlu,left:40dlu,2dlu,60dlu:grow,2dlu";

			FormLayout layout = new FormLayout(colDef, rowDef);
			this.m_optionstab.setLayout(layout);

			CellConstraints cons = new CellConstraints();

			this.m_optionstab
					.add(this.getComponent_42(), cons.xywh(2, 2, 1, 1));
			this.m_optionstab.add(this.getDefault(), cons.xywh(4, 2, 1, 1));
			this.m_optionstab.setName("OptionsTab");
			this.m_optionstab.setVisible(false);
		}

		return this.m_optionstab;
	}

	/**
	 * Getter method for component Component_42.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JLabel getComponent_42() {

		if (this.m_component_42 == null) {
			this.m_component_42 = new javax.swing.JLabel();
			this.m_component_42.setName("Component_42");
			this.m_component_42.setText("Default :");
		}

		return this.m_component_42;
	}

	/**
	 * Getter method for component Default.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JComboBox getDefault() {

		if (this.m_default == null) {
			this.m_default = new javax.swing.JComboBox();
			this.m_default.setName("Default");
		}

		return this.m_default;
	}

	/**
	 * Getter method for component AttributeCommentTab.
	 * 
	 * @return the initialized component
	 */
	public JPanel getAttributeCommentTab() {

		if (this.m_attributecommenttab == null) {
			this.m_attributecommenttab = new JPanel();

			String rowDef = "2dlu,p,160dlu:grow,p,2dlu";
			String colDef = "2dlu,left:40dlu,2dlu,60dlu:grow,2dlu";

			FormLayout layout = new FormLayout(colDef, rowDef);
			this.m_attributecommenttab.setLayout(layout);

			CellConstraints cons = new CellConstraints();

			this.m_attributecommenttab.add(new JScrollPane(this
					.getAttributeComment()), cons.xywh(2, 2, 3, 3));
			this.m_attributecommenttab.setName("AttributeCommentTab");
			this.m_attributecommenttab.setVisible(false);
		}

		return this.m_attributecommenttab;
	}

	/**
	 * Getter method for component AttributeComment.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JTextArea getAttributeComment() {

		if (this.m_attributecomment == null) {
			this.m_attributecomment = new javax.swing.JTextArea();
			this.m_attributecomment.setName("AttributeComment");
		}

		return this.m_attributecomment;
	}

	/**
	 * Getter method for component UpdateAttributeButton.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JButton getUpdateAttributeButton() {

		if (this.m_updateattributebutton == null) {
			this.m_updateattributebutton = new javax.swing.JButton();
			this.m_updateattributebutton.setActionCommand("Update");
			this.m_updateattributebutton.setIcon(IconFactory.getUpdateIcon());
			this.m_updateattributebutton.setName("UpdateAttributeButton");
			this.m_updateattributebutton.setText("Update");
		}

		return this.m_updateattributebutton;
	}

	/**
	 * Getter method for component IndexesTab.
	 * 
	 * @return the initialized component
	 */
	public JPanel getIndexesTab() {

		if (this.m_indexestab == null) {
			this.m_indexestab = new JPanel();

			String rowDef = "2dlu,p,165dlu:grow,p,2dlu,p,2dlu";
			String colDef = "2dlu,50dlu:grow,2dlu,50dlu:grow,2dlu,25dlu:grow,11dlu:grow,2dlu,11dlu:grow,2dlu,80dlu:grow,2dlu,60dlu:grow,2dlu";

			FormLayout layout = new FormLayout(colDef, rowDef);
			this.m_indexestab.setLayout(layout);

			CellConstraints cons = new CellConstraints();

			this.m_indexestab.add(new JScrollPane(this.getIndexList()), cons
					.xywh(2, 2, 8, 3));
			this.m_indexestab.add(this.getNewIndexButton(), cons.xywh(2, 6, 1,
					1));
			this.m_indexestab.add(this.getRenameIndexButton(), cons.xywh(4, 6,
					1, 1));
			this.m_indexestab.add(this.getDeleteIndexButton(), cons.xywh(6, 6,
					4, 1));
			this.m_indexestab.add(this.getIndexTabbedPane(), cons.xywh(11, 2,
					3, 3));
			this.m_indexestab.add(this.getUpdateIndexButton(), cons.xywh(13, 6,
					1, 1));
			this.m_indexestab.setName("IndexesTab");
			this.m_indexestab.setVisible(false);
		}

		return this.m_indexestab;
	}

	/**
	 * Getter method for component IndexList.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JList getIndexList() {

		if (this.m_indexlist == null) {
			this.m_indexlist = new javax.swing.JList();
			this.m_indexlist.setName("IndexList");
		}

		return this.m_indexlist;
	}

	/**
	 * Getter method for component NewIndexButton.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JButton getNewIndexButton() {

		if (this.m_newindexbutton == null) {
			this.m_newindexbutton = new javax.swing.JButton();
			this.m_newindexbutton.setActionCommand("New");
			this.m_newindexbutton.setIcon(IconFactory.getNewIcon());
			this.m_newindexbutton.setName("NewIndexButton");
			this.m_newindexbutton.setText("New");
		}

		return this.m_newindexbutton;
	}

	/**
	 * Getter method for component RenameIndexButton.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JButton getRenameIndexButton() {

		if (this.m_renameindexbutton == null) {
			this.m_renameindexbutton = new javax.swing.JButton();
			this.m_renameindexbutton.setActionCommand("Rename");
			this.m_renameindexbutton.setName("RenameIndexButton");
			this.m_renameindexbutton.setText("Rename");
		}

		return this.m_renameindexbutton;
	}

	/**
	 * Getter method for component DeleteIndexButton.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JButton getDeleteIndexButton() {

		if (this.m_deleteindexbutton == null) {
			this.m_deleteindexbutton = new javax.swing.JButton();
			this.m_deleteindexbutton.setActionCommand("Delete");
			this.m_deleteindexbutton.setIcon(IconFactory.getDeleteIcon());
			this.m_deleteindexbutton.setName("DeleteIndexButton");
			this.m_deleteindexbutton.setText("Delete");
		}

		return this.m_deleteindexbutton;
	}

	/**
	 * Getter method for component IndexTabbedPane.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JTabbedPane getIndexTabbedPane() {

		if (this.m_indextabbedpane == null) {
			this.m_indextabbedpane = new javax.swing.JTabbedPane();
			this.m_indextabbedpane.addTab("General", this.getIndexGeneralTab());
			this.m_indextabbedpane.setName("IndexTabbedPane");
			this.m_indextabbedpane.setSelectedIndex(0);
		}

		return this.m_indextabbedpane;
	}

	/**
	 * Getter method for component IndexGeneralTab.
	 * 
	 * @return the initialized component
	 */
	public JPanel getIndexGeneralTab() {

		if (this.m_indexgeneraltab == null) {
			this.m_indexgeneraltab = new JPanel();

			String rowDef = "2dlu,p,2dlu,p,100dlu:grow,p,2dlu,p,2dlu,p,2dlu";
			String colDef = "2dlu,left:40dlu,2dlu,60dlu:grow,2dlu";

			FormLayout layout = new FormLayout(colDef, rowDef);
			this.m_indexgeneraltab.setLayout(layout);

			CellConstraints cons = new CellConstraints();

			this.m_indexgeneraltab.add(this.getLabel1(), cons.xywh(2, 2, 1, 1));
			this.m_indexgeneraltab.add(this.getIndexName(), cons.xywh(4, 2, 1,
					1));
			this.m_indexgeneraltab.add(this.getUniqueIndex(), cons.xywh(4, 8,
					1, 1));
			this.m_indexgeneraltab.add(this.getNotUniqueIndex(), cons.xywh(4,
					10, 1, 1));
			this.m_indexgeneraltab.add(
					new JScrollPane(this.getIndexFieldList()), cons.xywh(2, 4,
							3, 3));
			this.m_indexgeneraltab.setName("IndexGeneralTab");
		}

		return this.m_indexgeneraltab;
	}

	/**
	 * Getter method for component Label1.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JLabel getLabel1() {

		if (this.m_label1 == null) {
			this.m_label1 = new javax.swing.JLabel();
			this.m_label1.setName("Label1");
			this.m_label1.setText("Name :");
		}

		return this.m_label1;
	}

	/**
	 * Getter method for component IndexName.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JTextField getIndexName() {

		if (this.m_indexname == null) {
			this.m_indexname = new javax.swing.JTextField();
			this.m_indexname.setName("IndexName");
		}

		return this.m_indexname;
	}

	/**
	 * Getter method for component UniqueIndex.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JRadioButton getUniqueIndex() {

		if (this.m_uniqueindex == null) {
			this.m_uniqueindex = new javax.swing.JRadioButton();
			this.m_uniqueindex.setActionCommand("Index is unique");
			this.m_uniqueindex.setName("UniqueIndex#Group1!U");
			this.m_uniqueindex.setText("Index is unique");
		}

		return this.m_uniqueindex;
	}

	/**
	 * Getter method for component NotUniqueIndex.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JRadioButton getNotUniqueIndex() {

		if (this.m_notuniqueindex == null) {
			this.m_notuniqueindex = new javax.swing.JRadioButton();
			this.m_notuniqueindex.setActionCommand("Index is not unique");
			this.m_notuniqueindex.setName("NotUniqueIndex#Group1!NU");
			this.m_notuniqueindex.setText("Index is not unique");
		}

		return this.m_notuniqueindex;
	}

	/**
	 * Getter method for component IndexFieldList.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JTable getIndexFieldList() {

		if (this.m_indexfieldlist == null) {
			this.m_indexfieldlist = new javax.swing.JTable() {
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
			this.m_indexfieldlist.setName("IndexFieldList");
			this.m_indexfieldlist.setShowGrid(false);
			this.m_indexfieldlist.setOpaque(false);
		}

		return this.m_indexfieldlist;
	}

	/**
	 * Getter method for component UpdateIndexButton.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JButton getUpdateIndexButton() {

		if (this.m_updateindexbutton == null) {
			this.m_updateindexbutton = new javax.swing.JButton();
			this.m_updateindexbutton.setActionCommand("Update");
			this.m_updateindexbutton.setIcon(IconFactory.getUpdateIcon());
			this.m_updateindexbutton.setName("UpdateIndexButton");
			this.m_updateindexbutton.setText("Update");
		}

		return this.m_updateindexbutton;
	}

	/**
	 * Getter method for component MainCommensTab.
	 * 
	 * @return the initialized component
	 */
	public JPanel getMainCommensTab() {

		if (this.m_maincommenstab == null) {
			this.m_maincommenstab = new JPanel();

			String rowDef = "2dlu,p,100dlu:grow,p,2dlu";
			String colDef = "2dlu,40dlu:grow,2dlu";

			FormLayout layout = new FormLayout(colDef, rowDef);
			this.m_maincommenstab.setLayout(layout);

			CellConstraints cons = new CellConstraints();

			this.m_maincommenstab.add(new JScrollPane(this.getEntityComment()),
					cons.xywh(2, 2, 1, 3));
			this.m_maincommenstab.setName("MainCommensTab");
			this.m_maincommenstab.setVisible(false);
		}

		return this.m_maincommenstab;
	}

	/**
	 * Getter method for component EntityComment.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JTextArea getEntityComment() {

		if (this.m_entitycomment == null) {
			this.m_entitycomment = new javax.swing.JTextArea();
			this.m_entitycomment.setName("EntityComment");
		}

		return this.m_entitycomment;
	}

	/**
	 * Getter method for component OkButton.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JButton getOkButton() {

		if (this.m_okbutton == null) {
			this.m_okbutton = new javax.swing.JButton();
			this.m_okbutton.setActionCommand("Ok");
			this.m_okbutton.setIcon(IconFactory.getSaveIcon());
			this.m_okbutton.setName("OkButton");
			this.m_okbutton.setText("Ok");
		}

		return this.m_okbutton;
	}

	/**
	 * Getter method for component CancelButton.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JButton getCancelButton() {

		if (this.m_cancelbutton == null) {
			this.m_cancelbutton = new javax.swing.JButton();
			this.m_cancelbutton.setActionCommand("Cancel");
			this.m_cancelbutton.setIcon(IconFactory.getCancelIcon());
			this.m_cancelbutton.setName("CancelButton");
			this.m_cancelbutton.setText("Cancel");
		}

		return this.m_cancelbutton;
	}

	/**
	 * Initialize method.
	 */
	private void buildGroups() {

		ButtonGroup Group1 = new ButtonGroup();
		Group1.add(this.getUniqueIndex());
		Group1.add(this.getNotUniqueIndex());
	}

	/**
	 * Getter for the group value for group Group1.
	 * 
	 * @return the value for the current selected item in the group or null if
	 *         nothing was selected
	 */
	public String getGroup1Value() {

		if (this.getUniqueIndex().isSelected()) {
			return "U";
		}
		if (this.getNotUniqueIndex().isSelected()) {
			return "NU";
		}
		return null;
	}

	/**
	 * Setter for the group value for group Group1.
	 * 
	 * @param the
	 *            value for the current selected item in the group or null if
	 *            nothing is selected
	 */
	public void setGroup1Value(String value) {

		this.getUniqueIndex().setSelected("U".equals(value));
		this.getNotUniqueIndex().setSelected("NU".equals(value));
	}
}

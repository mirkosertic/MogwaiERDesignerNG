package de.mogwai.erdesignerng.visual.editor.domain;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * Visual class DomainEditorView.
 * 
 * Created with Mogwai FormMaker 0.5.
 */
public class DomainEditorView extends JPanel {

	private javax.swing.JList m_domainlist;

	private javax.swing.JButton m_newbutton;

	private javax.swing.JButton m_renamebutton;

	private javax.swing.JButton m_deletebutton;

	private javax.swing.JTabbedPane m_detailtabbedpane;

	private javax.swing.JPanel m_component_6;

	private javax.swing.JLabel m_component_9;

	private javax.swing.JLabel m_component_10;

	private javax.swing.JLabel m_component_11;

	private javax.swing.JLabel m_component_12;

	private javax.swing.JTextField m_domainname;

	private javax.swing.JTextField m_declaration;

	private javax.swing.JComboBox m_javatype;

	private javax.swing.JCheckBox m_sequenced;

	private javax.swing.JButton m_updatebutton;

	private javax.swing.JButton m_okbutton;

	private javax.swing.JButton m_cancelbutton;

	/**
	 * Constructor.
	 */
	public DomainEditorView() {
		initialize();
	}

	/**
	 * Initialize method.
	 */
	private void initialize() {

		String rowDef = "2dlu,p,140dlu,2dlu,p,20dlu,p,2dlu";
		String colDef = "2dlu,50dlu:grow,2dlu,50dlu:grow,2dlu,50dlu:grow,2dlu,80dlu:grow,2dlu,60dlu:grow,2dlu";

		FormLayout layout = new FormLayout(colDef, rowDef);
		setLayout(layout);

		CellConstraints cons = new CellConstraints();

		add(new JScrollPane(getDomainList()), cons.xywh(2, 2, 5, 2));
		add(getNewButton(), cons.xywh(2, 5, 1, 1));
		add(getRenameButton(), cons.xywh(4, 5, 1, 1));
		add(getDeleteButton(), cons.xywh(6, 5, 1, 1));
		add(getDetailTabbedPane(), cons.xywh(8, 2, 3, 4));
		add(getOkButton(), cons.xywh(8, 7, 1, 1));
		add(getCancelButton(), cons.xywh(10, 7, 1, 1));

		buildGroups();
	}

	/**
	 * Initialize method.
	 */
	private void buildGroups() {

	}

	/**
	 * Getter method for component DomainList.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JList getDomainList() {

		if (m_domainlist == null) {
			m_domainlist = new javax.swing.JList();
			m_domainlist.setName("DomainList");
		}

		return m_domainlist;
	}

	/**
	 * Getter method for component NewButton.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JButton getNewButton() {

		if (m_newbutton == null) {
			m_newbutton = new javax.swing.JButton();
			m_newbutton.setActionCommand("New");
			m_newbutton.setIcon(new ImageIcon(ClassLoader
					.getSystemResource("de/mogwai/erdesigner/icons/new.png")));
			m_newbutton.setName("NewButton");
			m_newbutton.setText("New");
		}

		return m_newbutton;
	}

	/**
	 * Getter method for component RenameButton.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JButton getRenameButton() {

		if (m_renamebutton == null) {
			m_renamebutton = new javax.swing.JButton();
			m_renamebutton.setActionCommand("Rename");
			m_renamebutton.setName("RenameButton");
			m_renamebutton.setText("Rename");
		}

		return m_renamebutton;
	}

	/**
	 * Getter method for component DeleteButton.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JButton getDeleteButton() {

		if (m_deletebutton == null) {
			m_deletebutton = new javax.swing.JButton();
			m_deletebutton.setActionCommand("Delete");
			m_deletebutton
					.setIcon(new ImageIcon(
							ClassLoader
									.getSystemResource("de/mogwai/erdesigner/icons/delete.png")));
			m_deletebutton.setName("DeleteButton");
			m_deletebutton.setText("Delete");
		}

		return m_deletebutton;
	}

	/**
	 * Getter method for component DetailTabbedPane.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JTabbedPane getDetailTabbedPane() {

		if (m_detailtabbedpane == null) {
			m_detailtabbedpane = new javax.swing.JTabbedPane();
			m_detailtabbedpane.addTab("Domain properties", this
					.getComponent_6());
			m_detailtabbedpane.setName("DetailTabbedPane");
			m_detailtabbedpane.setSelectedIndex(0);
		}

		return m_detailtabbedpane;
	}

	/**
	 * Getter method for component Component_6.
	 * 
	 * @return the initialized component
	 */
	public JPanel getComponent_6() {

		if (m_component_6 == null) {
			m_component_6 = new JPanel();

			String rowDef = "2dlu,p,2dlu,p,2dlu,p,2dlu,p,2dlu,p,8dlu,p,2dlu";
			String colDef = "2dlu,left:60dlu,2dlu,60dlu:grow,2dlu";

			FormLayout layout = new FormLayout(colDef, rowDef);
			m_component_6.setLayout(layout);

			CellConstraints cons = new CellConstraints();

			m_component_6.add(getComponent_9(), cons.xywh(2, 2, 1, 1));
			m_component_6.add(getComponent_10(), cons.xywh(2, 4, 1, 1));
			m_component_6.add(getComponent_12(), cons.xywh(2, 8, 1, 1));
			m_component_6.add(getDomainName(), cons.xywh(4, 2, 1, 1));
			m_component_6.add(getDeclaration(), cons.xywh(4, 4, 1, 1));
			m_component_6.add(getJavatype(), cons.xywh(4, 8, 1, 1));
			m_component_6.add(getSequenced(), cons.xywh(4, 10, 1, 1));
			m_component_6.add(getUpdateButton(), cons.xywh(4, 12, 1, 1));
			m_component_6.setName("Component_6");
		}

		return m_component_6;
	}

	/**
	 * Getter method for component Component_9.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JLabel getComponent_9() {

		if (m_component_9 == null) {
			m_component_9 = new javax.swing.JLabel();
			m_component_9.setName("Component_9");
			m_component_9.setText("Name :");
		}

		return m_component_9;
	}

	/**
	 * Getter method for component Component_10.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JLabel getComponent_10() {

		if (m_component_10 == null) {
			m_component_10 = new javax.swing.JLabel();
			m_component_10.setName("Component_10");
			m_component_10.setText("Declaration :");
		}

		return m_component_10;
	}

	/**
	 * Getter method for component Component_12.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JLabel getComponent_12() {

		if (m_component_12 == null) {
			m_component_12 = new javax.swing.JLabel();
			m_component_12.setName("Component_12");
			m_component_12.setText("Java - Type :");
		}

		return m_component_12;
	}

	/**
	 * Getter method for component DomainName.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JTextField getDomainName() {

		if (m_domainname == null) {
			m_domainname = new javax.swing.JTextField();
			m_domainname.setName("DomainName");
		}

		return m_domainname;
	}

	/**
	 * Getter method for component Declaration.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JTextField getDeclaration() {

		if (m_declaration == null) {
			m_declaration = new javax.swing.JTextField();
			m_declaration.setName("Declaration");
		}

		return m_declaration;
	}

	/**
	 * Getter method for component Javatype.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JComboBox getJavatype() {

		if (m_javatype == null) {
			m_javatype = new javax.swing.JComboBox();
			m_javatype.setName("Javatype");
		}

		return m_javatype;
	}

	/**
	 * Getter method for component Sequenced.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JCheckBox getSequenced() {

		if (m_sequenced == null) {
			m_sequenced = new javax.swing.JCheckBox();
			m_sequenced.setActionCommand("Sequenced");
			m_sequenced.setName("Sequenced");
			m_sequenced.setText("Sequenced");
		}

		return m_sequenced;
	}

	/**
	 * Getter method for component UpdateButton.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JButton getUpdateButton() {

		if (m_updatebutton == null) {
			m_updatebutton = new javax.swing.JButton();
			m_updatebutton.setActionCommand("Update");
			m_updatebutton
					.setIcon(new ImageIcon(
							ClassLoader
									.getSystemResource("de/mogwai/erdesigner/icons/arrow_refresh_small.png")));
			m_updatebutton.setName("UpdateButton");
			m_updatebutton.setText("Update");
		}

		return m_updatebutton;
	}

	/**
	 * Getter method for component OkButton.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JButton getOkButton() {

		if (m_okbutton == null) {
			m_okbutton = new javax.swing.JButton();
			m_okbutton.setActionCommand("Ok");
			m_okbutton
					.setIcon(new ImageIcon(
							ClassLoader
									.getSystemResource("de/mogwai/erdesigner/icons/script_save.png")));
			m_okbutton.setName("OkButton");
			m_okbutton.setText("Ok");
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
			m_cancelbutton = new javax.swing.JButton();
			m_cancelbutton.setActionCommand("Cancel");
			m_cancelbutton
					.setIcon(new ImageIcon(
							ClassLoader
									.getSystemResource("de/mogwai/erdesigner/icons/cancel.png")));
			m_cancelbutton.setName("CancelButton");
			m_cancelbutton.setText("Cancel");
		}

		return m_cancelbutton;
	}
}

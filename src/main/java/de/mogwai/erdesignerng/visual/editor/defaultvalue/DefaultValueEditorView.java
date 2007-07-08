package de.mogwai.erdesignerng.visual.editor.defaultvalue;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.mogwai.erdesignerng.visual.IconFactory;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2007-07-08 10:06:26 $
 */
public class DefaultValueEditorView extends JPanel {

	private javax.swing.JList m_defaultvaluelist;

	private javax.swing.JButton m_newbutton;

	private javax.swing.JButton m_renamebutton;

	private javax.swing.JButton m_deletebutton;

	private javax.swing.JTabbedPane m_detailtabbedpane;

	private javax.swing.JPanel m_component_6;

	private javax.swing.JLabel m_component_9;

	private javax.swing.JLabel m_component_10;

	private javax.swing.JTextField m_defaultvaluename;

	private javax.swing.JTextField m_declaration;

	private javax.swing.JButton m_updatebutton;

	private javax.swing.JButton m_okbutton;

	private javax.swing.JButton m_cancelbutton;

	/**
	 * Constructor.
	 */
	public DefaultValueEditorView() {
		this.initialize();
	}

	/**
	 * Initialize method.
	 */
	private void initialize() {

		String rowDef = "2dlu,p,140dlu,2dlu,p,20dlu,p,2dlu";
		String colDef = "2dlu,50dlu:grow,2dlu,50dlu:grow,2dlu,50dlu:grow,2dlu,80dlu:grow,2dlu,60dlu:grow,2dlu";

		FormLayout layout = new FormLayout(colDef, rowDef);
		this.setLayout(layout);

		CellConstraints cons = new CellConstraints();

		this.add(new JScrollPane(this.getDefaultValueList()), cons.xywh(2, 2,
				5, 2));
		this.add(this.getNewButton(), cons.xywh(2, 5, 1, 1));
		this.add(this.getRenameButton(), cons.xywh(4, 5, 1, 1));
		this.add(this.getDeleteButton(), cons.xywh(6, 5, 1, 1));
		this.add(this.getDetailTabbedPane(), cons.xywh(8, 2, 3, 4));
		this.add(this.getOkButton(), cons.xywh(8, 7, 1, 1));
		this.add(this.getCancelButton(), cons.xywh(10, 7, 1, 1));

		this.buildGroups();
	}

	/**
	 * Initialize method.
	 */
	private void buildGroups() {

	}

	/**
	 * Getter method for component DefaultValueList.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JList getDefaultValueList() {

		if (this.m_defaultvaluelist == null) {
			this.m_defaultvaluelist = new javax.swing.JList();
			this.m_defaultvaluelist.setName("DefaultValueList");
		}

		return this.m_defaultvaluelist;
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
	 * Getter method for component DetailTabbedPane.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JTabbedPane getDetailTabbedPane() {

		if (this.m_detailtabbedpane == null) {
			this.m_detailtabbedpane = new javax.swing.JTabbedPane();
			this.m_detailtabbedpane.addTab("Default value properties", this
					.getComponent_6());
			this.m_detailtabbedpane.setName("DetailTabbedPane");
			this.m_detailtabbedpane.setSelectedIndex(0);
		}

		return this.m_detailtabbedpane;
	}

	/**
	 * Getter method for component Component_6.
	 * 
	 * @return the initialized component
	 */
	public JPanel getComponent_6() {

		if (this.m_component_6 == null) {
			this.m_component_6 = new JPanel();

			String rowDef = "2dlu,p,2dlu,p,20dlu,p,2dlu";
			String colDef = "2dlu,left:60dlu,2dlu,60dlu:grow,2dlu";

			FormLayout layout = new FormLayout(colDef, rowDef);
			this.m_component_6.setLayout(layout);

			CellConstraints cons = new CellConstraints();

			this.m_component_6
					.add(this.getComponent_9(), cons.xywh(2, 2, 1, 1));
			this.m_component_6.add(this.getComponent_10(), cons
					.xywh(2, 4, 1, 1));
			this.m_component_6.add(this.getDefaultValueName(), cons.xywh(4, 2,
					1, 1));
			this.m_component_6
					.add(this.getDeclaration(), cons.xywh(4, 4, 1, 1));
			this.m_component_6.add(this.getUpdateButton(), cons
					.xywh(4, 6, 1, 1));
			this.m_component_6.setName("Component_6");
		}

		return this.m_component_6;
	}

	/**
	 * Getter method for component Component_9.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JLabel getComponent_9() {

		if (this.m_component_9 == null) {
			this.m_component_9 = new javax.swing.JLabel();
			this.m_component_9.setName("Component_9");
			this.m_component_9.setText("Name :");
		}

		return this.m_component_9;
	}

	/**
	 * Getter method for component Component_10.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JLabel getComponent_10() {

		if (this.m_component_10 == null) {
			this.m_component_10 = new javax.swing.JLabel();
			this.m_component_10.setName("Component_10");
			this.m_component_10.setText("Declaration :");
		}

		return this.m_component_10;
	}

	/**
	 * Getter method for component DefaultValueName.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JTextField getDefaultValueName() {

		if (this.m_defaultvaluename == null) {
			this.m_defaultvaluename = new javax.swing.JTextField();
			this.m_defaultvaluename.setName("DefaultValueName");
		}

		return this.m_defaultvaluename;
	}

	/**
	 * Getter method for component Declaration.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JTextField getDeclaration() {

		if (this.m_declaration == null) {
			this.m_declaration = new javax.swing.JTextField();
			this.m_declaration.setName("Declaration");
		}

		return this.m_declaration;
	}

	/**
	 * Getter method for component UpdateButton.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JButton getUpdateButton() {

		if (this.m_updatebutton == null) {
			this.m_updatebutton = new javax.swing.JButton();
			this.m_updatebutton.setActionCommand("Update");
			this.m_updatebutton.setIcon(IconFactory.getUpdateIcon());
			this.m_updatebutton.setName("UpdateButton");
			this.m_updatebutton.setText("Update");
		}

		return this.m_updatebutton;
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
}

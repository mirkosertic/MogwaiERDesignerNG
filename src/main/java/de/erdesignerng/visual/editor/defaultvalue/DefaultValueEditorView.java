package de.erdesignerng.visual.editor.defaultvalue;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.visual.IconFactory;
import de.mogwai.common.client.looks.components.DefaultLabel;
import de.mogwai.common.client.looks.components.DefaultTabbedPane;
import de.mogwai.common.client.looks.components.DefaultTabbedPaneTab;
import de.mogwai.common.client.looks.components.DefaultTextField;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-01-07 19:09:38 $
 */
public class DefaultValueEditorView extends JPanel {

	private javax.swing.JList m_defaultvaluelist;

	private javax.swing.JButton m_newbutton;

	private javax.swing.JButton m_deletebutton;

	private DefaultTabbedPane m_detailtabbedpane;

	private DefaultTabbedPaneTab m_component_6;

	private DefaultLabel m_component_9;

	private DefaultLabel m_component_10;

	private DefaultTextField m_defaultvaluename;

	private DefaultTextField m_declaration;

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

		if (m_defaultvaluelist == null) {
			m_defaultvaluelist = new javax.swing.JList();
			m_defaultvaluelist.setName("DefaultValueList");
		}

		return m_defaultvaluelist;
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
			m_newbutton.setIcon(IconFactory.getNewIcon());
			m_newbutton.setName("NewButton");
			m_newbutton.setText("New");
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
			m_deletebutton = new javax.swing.JButton();
			m_deletebutton.setActionCommand("Delete");
			m_deletebutton.setIcon(IconFactory.getDeleteIcon());
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
			m_detailtabbedpane = new DefaultTabbedPane();
			m_detailtabbedpane.addTab(null, this
					.getDetailTab());
			m_detailtabbedpane.setSelectedIndex(0);
		}

		return m_detailtabbedpane;
	}

	/**
	 * Getter method for component Component_6.
	 * 
	 * @return the initialized component
	 */
	public JPanel getDetailTab() {

		if (m_component_6 == null) {
			m_component_6 = new DefaultTabbedPaneTab(getDetailTabbedPane(),ERDesignerBundle.DEFAULTVALUEPROPERTIES);

			String rowDef = "2dlu,p,2dlu,p,20dlu,p,2dlu";
			String colDef = "2dlu,left:60dlu,2dlu,60dlu:grow,2dlu";

			FormLayout layout = new FormLayout(colDef, rowDef);
			m_component_6.setLayout(layout);

			CellConstraints cons = new CellConstraints();

			m_component_6
					.add(this.getComponent_9(), cons.xywh(2, 2, 1, 1));
			m_component_6.add(this.getComponent_10(), cons
					.xywh(2, 4, 1, 1));
			m_component_6.add(this.getDefaultValueName(), cons.xywh(4, 2,
					1, 1));
			m_component_6
					.add(this.getDeclaration(), cons.xywh(4, 4, 1, 1));
			m_component_6.add(this.getUpdateButton(), cons
					.xywh(4, 6, 1, 1));
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
			m_component_9 = new DefaultLabel(ERDesignerBundle.NAME);
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
			m_component_10 = new DefaultLabel(ERDesignerBundle.DECLRATATION);
		}

		return m_component_10;
	}

	/**
	 * Getter method for component DefaultValueName.
	 * 
	 * @return the initialized component
	 */
	public DefaultTextField getDefaultValueName() {

		if (m_defaultvaluename == null) {
			m_defaultvaluename = new DefaultTextField();
			m_defaultvaluename.setName("DefaultValueName");
		}

		return m_defaultvaluename;
	}

	/**
	 * Getter method for component Declaration.
	 * 
	 * @return the initialized component
	 */
	public DefaultTextField getDeclaration() {

		if (m_declaration == null) {
			m_declaration = new DefaultTextField();
			m_declaration.setName("Declaration");
		}

		return m_declaration;
	}

	/**
	 * Getter method for component UpdateButton.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JButton getUpdateButton() {

		if (m_updatebutton == null) {
			m_updatebutton = new javax.swing.JButton();
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
		}

		return m_cancelbutton;
	}
}

package de.mogwai.erdesignerng.visual.editor.relation;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.jgoodies.forms.factories.DefaultComponentFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.mogwai.erdesignerng.visual.IconFactory;

/**
 * Visual class RelationEditorView.
 * 
 * Created with Mogwai FormMaker 0.6.
 */
public class RelationEditorView extends JPanel {

	private javax.swing.JLabel m_component_1;

	private javax.swing.JTextField m_relationname;

	private javax.swing.JTable m_component_5;

	private JPanel m_component_7;

	private javax.swing.JRadioButton m_component_11;

	private javax.swing.JRadioButton m_component_12;

	private javax.swing.JRadioButton m_component_13;

	private JPanel m_component_8;

	private javax.swing.JButton m_okbutton;

	private javax.swing.JButton m_cancelbutton;

	/**
	 * Constructor.
	 */
	public RelationEditorView() {
		initialize();
	}

	/**
	 * Initialize method.
	 */
	private void initialize() {

		String rowDef = "2dlu,p,8dlu,p,8dlu,p,2dlu,fill:100dlu,8dlu,p,8dlu,p,20dlu,p,2dlu";
		String colDef = "2dlu,60dlu,2dlu,150dlu,2dlu";

		FormLayout layout = new FormLayout(colDef, rowDef);
		setLayout(layout);

		CellConstraints cons = new CellConstraints();

		add(getComponent_1(), cons.xywh(2, 4, 1, 1));
		add(DefaultComponentFactory.getInstance().createSeparator(
				"Relation properties"), cons.xywh(2, 2, 3, 1));
		add(getRelationname(), cons.xywh(4, 4, 1, 1));
		add(DefaultComponentFactory.getInstance().createSeparator(
				"Attribute mapping"), cons.xywh(2, 6, 3, 1));
		add(new JScrollPane(getComponent_5()), cons.xywh(2, 8, 3, 1));
		add(DefaultComponentFactory.getInstance().createSeparator(
				"Delete / Cascade handling"), cons.xywh(2, 10, 3, 1));
		add(getComponent_7(), cons.xywh(2, 12, 3, 1));
		add(getComponent_8(), cons.xywh(2, 14, 3, 1));

		buildGroups();
	}

	/**
	 * Getter method for component Component_1.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JLabel getComponent_1() {

		if (m_component_1 == null) {
			m_component_1 = new javax.swing.JLabel();
			m_component_1.setName("Component_1");
			m_component_1.setText("Relation name :");
		}

		return m_component_1;
	}

	/**
	 * Getter method for component Relationname.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JTextField getRelationname() {

		if (m_relationname == null) {
			m_relationname = new javax.swing.JTextField();
			m_relationname.setName("Relationname");
			m_relationname.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					handleRelationnameActionPerformed(e.getActionCommand());
				}
			});
		}

		return m_relationname;
	}

	/**
	 * Getter method for component Component_5.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JTable getComponent_5() {

		if (m_component_5 == null) {
			m_component_5 = new javax.swing.JTable();
			m_component_5.setName("Component_5");
		}

		return m_component_5;
	}

	/**
	 * Getter method for component Component_7.
	 * 
	 * @return the initialized component
	 */
	public JPanel getComponent_7() {

		if (m_component_7 == null) {
			m_component_7 = new JPanel();

			String rowDef = "p,2dlu,p,2dlu,p";
			String colDef = "50dlu:grow";

			FormLayout layout = new FormLayout(colDef, rowDef);
			m_component_7.setLayout(layout);

			CellConstraints cons = new CellConstraints();

			m_component_7.add(getComponent_11(), cons.xywh(1, 1, 1, 1));
			m_component_7.add(getComponent_12(), cons.xywh(1, 3, 1, 1));
			m_component_7.add(getComponent_13(), cons.xywh(1, 5, 1, 1));
			m_component_7.setName("Component_7");
		}

		return m_component_7;
	}

	/**
	 * Getter method for component Component_11.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JRadioButton getComponent_11() {

		if (m_component_11 == null) {
			m_component_11 = new javax.swing.JRadioButton();
			m_component_11.setActionCommand("Database default");
			m_component_11.setName("Component_11#Group1!DEFAULT");
			m_component_11.setText("Database default");
			m_component_11.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					handleComponent_11ActionPerformed(e.getActionCommand());
				}
			});
			m_component_11
					.addChangeListener(new javax.swing.event.ChangeListener() {

						public void stateChanged(javax.swing.event.ChangeEvent e) {
							handleComponent_11StateChanged();
						}
					});
		}

		return m_component_11;
	}

	/**
	 * Getter method for component Component_12.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JRadioButton getComponent_12() {

		if (m_component_12 == null) {
			m_component_12 = new javax.swing.JRadioButton();
			m_component_12.setActionCommand("ON DELETE CASCADE");
			m_component_12.setName("Component_12#Group1!CASCADE");
			m_component_12.setSelected(true);
			m_component_12.setText("ON DELETE CASCADE");
			m_component_12.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					handleComponent_12ActionPerformed(e.getActionCommand());
				}
			});
			m_component_12
					.addChangeListener(new javax.swing.event.ChangeListener() {

						public void stateChanged(javax.swing.event.ChangeEvent e) {
							handleComponent_12StateChanged();
						}
					});
		}

		return m_component_12;
	}

	/**
	 * Getter method for component Component_13.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JRadioButton getComponent_13() {

		if (m_component_13 == null) {
			m_component_13 = new javax.swing.JRadioButton();
			m_component_13.setActionCommand("ON DELETE SET NULL");
			m_component_13.setName("Component_13#Group1!SETNULL");
			m_component_13.setText("ON DELETE SET NULL");
			m_component_13.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					handleComponent_13ActionPerformed(e.getActionCommand());
				}
			});
			m_component_13
					.addChangeListener(new javax.swing.event.ChangeListener() {

						public void stateChanged(javax.swing.event.ChangeEvent e) {
							handleComponent_13StateChanged();
						}
					});
		}

		return m_component_13;
	}

	/**
	 * Getter method for component Component_8.
	 * 
	 * @return the initialized component
	 */
	public JPanel getComponent_8() {

		if (m_component_8 == null) {
			m_component_8 = new JPanel();

			String rowDef = "p";
			String colDef = "60dlu,2dlu:grow,60dlu";

			FormLayout layout = new FormLayout(colDef, rowDef);
			m_component_8.setLayout(layout);

			CellConstraints cons = new CellConstraints();

			m_component_8.add(getOKButton(), cons.xywh(1, 1, 1, 1));
			m_component_8.add(getCancelButton(), cons.xywh(3, 1, 1, 1));
			m_component_8.setName("Component_8");
		}

		return m_component_8;
	}

	/**
	 * Getter method for component OKButton.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JButton getOKButton() {

		if (m_okbutton == null) {
			m_okbutton = new javax.swing.JButton();
			m_okbutton.setActionCommand("Ok");
			m_okbutton
					.setIcon(IconFactory.getSaveIcon());
			m_okbutton.setName("OKButton");
			m_okbutton.setText("Ok");
			m_okbutton.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					handleOKButtonActionPerformed(e.getActionCommand());
				}
			});
			m_okbutton
					.addChangeListener(new javax.swing.event.ChangeListener() {

						public void stateChanged(javax.swing.event.ChangeEvent e) {
							handleOKButtonStateChanged();
						}
					});
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
					.setIcon(IconFactory.getCancelIcon());
			m_cancelbutton.setName("CancelButton");
			m_cancelbutton.setText("Cancel");
			m_cancelbutton.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					handleCancelButtonActionPerformed(e.getActionCommand());
				}
			});
			m_cancelbutton
					.addChangeListener(new javax.swing.event.ChangeListener() {

						public void stateChanged(javax.swing.event.ChangeEvent e) {
							handleCancelButtonStateChanged();
						}
					});
		}

		return m_cancelbutton;
	}

	/**
	 * Initialize method.
	 */
	private void buildGroups() {

		ButtonGroup Group1 = new ButtonGroup();
		Group1.add(getComponent_11());
		Group1.add(getComponent_12());
		Group1.add(getComponent_13());
	}

	/**
	 * Getter for the group value for group Group1.
	 * 
	 * @return the value for the current selected item in the group or null if
	 *         nothing was selected
	 */
	public String getGroup1Value() {

		if (getComponent_11().isSelected()) {
			return "DEFAULT";
		}
		if (getComponent_12().isSelected()) {
			return "CASCADE";
		}
		if (getComponent_13().isSelected()) {
			return "SETNULL";
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

		getComponent_11().setSelected("DEFAULT".equals(value));
		getComponent_12().setSelected("CASCADE".equals(value));
		getComponent_13().setSelected("SETNULL".equals(value));
	}

	/**
	 * Action listener implementation for Relationname.
	 * 
	 * @param actionCommand
	 *            the spanned action command
	 */
	public void handleRelationnameActionPerformed(String actionCommand) {
	}

	/**
	 * Action listener implementation for Component_11.
	 * 
	 * @param actionCommand
	 *            the spanned action command
	 */
	public void handleComponent_11ActionPerformed(String actionCommand) {
	}

	/**
	 * Change listener implementation for Component_11.
	 * 
	 * @param item
	 *            the selected item
	 */
	public void handleComponent_11StateChanged() {
	}

	/**
	 * Action listener implementation for Component_12.
	 * 
	 * @param actionCommand
	 *            the spanned action command
	 */
	public void handleComponent_12ActionPerformed(String actionCommand) {
	}

	/**
	 * Change listener implementation for Component_12.
	 * 
	 * @param item
	 *            the selected item
	 */
	public void handleComponent_12StateChanged() {
	}

	/**
	 * Action listener implementation for Component_13.
	 * 
	 * @param actionCommand
	 *            the spanned action command
	 */
	public void handleComponent_13ActionPerformed(String actionCommand) {
	}

	/**
	 * Change listener implementation for Component_13.
	 * 
	 * @param item
	 *            the selected item
	 */
	public void handleComponent_13StateChanged() {
	}

	/**
	 * Action listener implementation for OKButton.
	 * 
	 * @param actionCommand
	 *            the spanned action command
	 */
	public void handleOKButtonActionPerformed(String actionCommand) {
	}

	/**
	 * Change listener implementation for OKButton.
	 * 
	 * @param item
	 *            the selected item
	 */
	public void handleOKButtonStateChanged() {
	}

	/**
	 * Action listener implementation for CancelButton.
	 * 
	 * @param actionCommand
	 *            the spanned action command
	 */
	public void handleCancelButtonActionPerformed(String actionCommand) {
	}

	/**
	 * Change listener implementation for CancelButton.
	 * 
	 * @param item
	 *            the selected item
	 */
	public void handleCancelButtonStateChanged() {
	}
}

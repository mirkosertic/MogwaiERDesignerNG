package de.erdesignerng.visual.editor.domain;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.model.Domain;
import de.erdesignerng.visual.IconFactory;
import de.mogwai.looks.components.DefaultCheckBox;
import de.mogwai.looks.components.DefaultComboBox;
import de.mogwai.looks.components.DefaultLabel;
import de.mogwai.looks.components.DefaultList;
import de.mogwai.looks.components.DefaultPanel;
import de.mogwai.looks.components.DefaultScrollPane;
import de.mogwai.looks.components.DefaultTabbedPane;
import de.mogwai.looks.components.DefaultTabbedPaneTab;
import de.mogwai.looks.components.DefaultTextField;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-01-03 13:11:17 $
 */
public class DomainEditorView extends DefaultPanel {

	private DefaultList<Domain> domainList = new DefaultList<Domain>();

	private javax.swing.JButton newButton;

	private javax.swing.JButton renameButton;

	private javax.swing.JButton deleteButton;

	private DefaultTabbedPane detailTabbedPane;

	private DefaultTabbedPaneTab domainPropertiesTab;

	private DefaultLabel nameLabel = new DefaultLabel(ERDesignerBundle.NAME);

	private DefaultLabel declarationLabel = new DefaultLabel(ERDesignerBundle.DECLRATATION);

	private DefaultLabel javaTypeLabel = new DefaultLabel(ERDesignerBundle.JAVATYPE);

	private DefaultTextField domainname = new DefaultTextField();

	private DefaultTextField declaration = new DefaultTextField();

	private DefaultComboBox javatype = new DefaultComboBox();

	private DefaultCheckBox sequenced = new DefaultCheckBox(ERDesignerBundle.SEQUENCED);

	private javax.swing.JButton updatebutton;

	private javax.swing.JButton okbutton;

	private javax.swing.JButton cancelbutton;

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

		add(new DefaultScrollPane(getDomainList()), cons.xywh(2, 2, 5, 2));
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
	public DefaultList<Domain> getDomainList() {

		return domainList;
	}

	/**
	 * Getter method for component NewButton.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JButton getNewButton() {

		if (newButton == null) {
			newButton = new javax.swing.JButton();
			newButton.setIcon(IconFactory.getNewIcon());
			newButton.setText("New");
		}

		return newButton;
	}

	/**
	 * Getter method for component RenameButton.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JButton getRenameButton() {

		if (renameButton == null) {
			renameButton = new javax.swing.JButton();
			renameButton.setText("Rename");
		}

		return renameButton;
	}

	/**
	 * Getter method for component DeleteButton.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JButton getDeleteButton() {

		if (deleteButton == null) {
			deleteButton = new javax.swing.JButton();
			deleteButton.setIcon(IconFactory.getDeleteIcon());
			deleteButton.setText("Delete");
		}

		return deleteButton;
	}

	/**
	 * Getter method for component DetailTabbedPane.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JTabbedPane getDetailTabbedPane() {

		if (detailTabbedPane == null) {
			detailTabbedPane = new DefaultTabbedPane();
			detailTabbedPane.addTab(null, this
					.getDomainPropertiesTab());
			detailTabbedPane.setSelectedIndex(0);
		}

		return detailTabbedPane;
	}

	/**
	 * Getter method for component Component_6.
	 * 
	 * @return the initialized component
	 */
	public DefaultTabbedPaneTab getDomainPropertiesTab() {

		if (domainPropertiesTab == null) {
			domainPropertiesTab = new DefaultTabbedPaneTab(detailTabbedPane,ERDesignerBundle.DOMAINPROPERTIES);

			String rowDef = "2dlu,p,2dlu,p,2dlu,p,2dlu,p,2dlu,p,8dlu,p,2dlu";
			String colDef = "2dlu,left:60dlu,2dlu,60dlu:grow,2dlu";

			FormLayout layout = new FormLayout(colDef, rowDef);
			domainPropertiesTab.setLayout(layout);

			CellConstraints cons = new CellConstraints();

			domainPropertiesTab.add(getNameLabel(), cons.xywh(2, 2, 1, 1));
			domainPropertiesTab.add(getDeclarationLabel(), cons.xywh(2, 4, 1, 1));
			domainPropertiesTab.add(getJavaTypeLabel(), cons.xywh(2, 8, 1, 1));
			domainPropertiesTab.add(getDomainName(), cons.xywh(4, 2, 1, 1));
			domainPropertiesTab.add(getDeclaration(), cons.xywh(4, 4, 1, 1));
			domainPropertiesTab.add(getJavatype(), cons.xywh(4, 8, 1, 1));
			domainPropertiesTab.add(getSequenced(), cons.xywh(4, 10, 1, 1));
			domainPropertiesTab.add(getUpdateButton(), cons.xywh(4, 12, 1, 1));
		}

		return domainPropertiesTab;
	}

	/**
	 * Getter method for component Component_9.
	 * 
	 * @return the initialized component
	 */
	public DefaultLabel getNameLabel() {

		return nameLabel;
	}

	/**
	 * Getter method for component Component_10.
	 * 
	 * @return the initialized component
	 */
	public DefaultLabel getDeclarationLabel() {

		return declarationLabel;
	}

	/**
	 * Getter method for component Component_12.
	 * 
	 * @return the initialized component
	 */
	public DefaultLabel getJavaTypeLabel() {

		return javaTypeLabel;
	}

	/**
	 * Getter method for component DomainName.
	 * 
	 * @return the initialized component
	 */
	public DefaultTextField getDomainName() {

		return domainname;
	}

	/**
	 * Getter method for component Declaration.
	 * 
	 * @return the initialized component
	 */
	public DefaultTextField getDeclaration() {

		return declaration;
	}

	/**
	 * Getter method for component Javatype.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JComboBox getJavatype() {

		return javatype;
	}

	/**
	 * Getter method for component Sequenced.
	 * 
	 * @return the initialized component
	 */
	public DefaultCheckBox getSequenced() {

		return sequenced;
	}

	/**
	 * Getter method for component UpdateButton.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JButton getUpdateButton() {

		if (updatebutton == null) {
			updatebutton = new javax.swing.JButton();
			updatebutton.setIcon(IconFactory.getUpdateIcon());
			updatebutton.setText("Update");
		}

		return updatebutton;
	}

	/**
	 * Getter method for component OkButton.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JButton getOkButton() {

		if (okbutton == null) {
			okbutton = new javax.swing.JButton();
			okbutton.setIcon(IconFactory.getSaveIcon());
			okbutton.setText("Ok");
		}

		return okbutton;
	}

	/**
	 * Getter method for component CancelButton.
	 * 
	 * @return the initialized component
	 */
	public javax.swing.JButton getCancelButton() {

		if (cancelbutton == null) {
			cancelbutton = new javax.swing.JButton();
			cancelbutton.setIcon(IconFactory.getCancelIcon());
			cancelbutton.setText("Cancel");
		}

		return cancelbutton;
	}
}

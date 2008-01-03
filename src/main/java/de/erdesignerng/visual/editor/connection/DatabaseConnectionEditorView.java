package de.erdesignerng.visual.editor.connection;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.dialect.Dialect;
import de.mogwai.common.client.looks.components.DefaultButton;
import de.mogwai.common.client.looks.components.DefaultComboBox;
import de.mogwai.common.client.looks.components.DefaultLabel;
import de.mogwai.common.client.looks.components.DefaultPanel;
import de.mogwai.common.client.looks.components.DefaultTextField;

public class DatabaseConnectionEditorView extends DefaultPanel {

	private DefaultComboBox dialect = new DefaultComboBox();

	private DefaultTextField driver = new DefaultTextField();

	private DefaultTextField url = new DefaultTextField();

	private DefaultTextField user = new DefaultTextField();

	private DefaultTextField password = new DefaultTextField();
	
	private DefaultButton testButton = new DefaultButton();
	
	private DefaultButton okButton = new DefaultButton();

	private DefaultButton cancelButton = new DefaultButton();

	public DatabaseConnectionEditorView() {
		initialize();
	}

	private void initialize() {

		String theColDef = "2dlu,p,2dlu,150dlu,2";
		String theRowDef = "2dlu,p,10dlu,p,2dlu,p,2dlu,p,2dlu,p,10dlu,p,2dlu";

		FormLayout theLayout = new FormLayout(theColDef, theRowDef);
		setLayout(theLayout);

		CellConstraints cons = new CellConstraints();

		add(new DefaultLabel(ERDesignerBundle.DIALECT), cons.xy(2, 2));
		add(dialect, cons.xy(4, 2));

		add(new DefaultLabel(ERDesignerBundle.JDBCDRIVER), cons.xy(2, 4));
		add(driver, cons.xy(4, 4));

		add(new DefaultLabel(ERDesignerBundle.JDBCURL), cons.xy(2, 6));
		add(url, cons.xy(4, 6));

		add(new DefaultLabel(ERDesignerBundle.USER), cons.xy(2, 8));
		add(user, cons.xy(4, 8));

		add(new DefaultLabel(ERDesignerBundle.PASSWORD), cons.xy(2, 10));
		add(password, cons.xy(4, 10));

		JPanel thePanel = new JPanel();

		theColDef = "50dlu,2dlu:grow,50dlu,2dlu,50dlu,2dlu";
		theRowDef = "p";

		theLayout = new FormLayout(theColDef, theRowDef);
		thePanel.setLayout(theLayout);

		thePanel.add(testButton, cons.xy(1, 1));
		testButton.setText("Test");
		
		thePanel.add(okButton, cons.xy(3, 1));
		okButton.setText("Ok");

		thePanel.add(cancelButton, cons.xy(5, 1));
		cancelButton.setText("Cancel");
		
		dialect.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				handleDialectChange((Dialect)dialect.getSelectedItem());
			}
			
		});

		add(thePanel, cons.xyw(2, 12, 3));
	}

	public DefaultComboBox getDialect() {
		return dialect;
	}

	public DefaultTextField getDriver() {
		return driver;
	}

	public DefaultTextField getPassword() {
		return password;
	}

	public DefaultTextField getUrl() {
		return url;
	}

	public DefaultTextField getUser() {
		return user;
	}

	public void handleDialectChange(Dialect aDialect) {
	}

	/**
	 * @return the cancelButton
	 */
	public DefaultButton getCancelButton() {
		return cancelButton;
	}

	/**
	 * @return the okButton
	 */
	public DefaultButton getOkButton() {
		return okButton;
	}

	/**
	 * @return the testButton
	 */
	public DefaultButton getTestButton() {
		return testButton;
	}
}

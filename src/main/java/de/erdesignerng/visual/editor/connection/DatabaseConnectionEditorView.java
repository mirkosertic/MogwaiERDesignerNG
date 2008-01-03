package de.erdesignerng.visual.editor.connection;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.erdesignerng.dialect.Dialect;
import de.erdesignerng.visual.IconFactory;
import de.mogwai.looks.components.DefaultComboBox;
import de.mogwai.looks.components.DefaultTextField;

public class DatabaseConnectionEditorView extends JPanel {

	private DefaultComboBox dialect = new DefaultComboBox();

	private DefaultTextField driver = new DefaultTextField();

	private DefaultTextField url = new DefaultTextField();

	private DefaultTextField user = new DefaultTextField();

	private DefaultTextField password = new DefaultTextField();

	private JButton testButton = new JButton(IconFactory.getCancelIcon());
	
	private JButton okButton = new JButton(IconFactory.getSaveIcon());

	private JButton cancelButton = new JButton(IconFactory.getCancelIcon());

	public DatabaseConnectionEditorView() {
		initialize();
	}

	private void initialize() {

		String theColDef = "2dlu,p,2dlu,150dlu,2";
		String theRowDef = "2dlu,p,10dlu,p,2dlu,p,2dlu,p,2dlu,p,10dlu,p,2dlu";

		FormLayout theLayout = new FormLayout(theColDef, theRowDef);
		setLayout(theLayout);

		CellConstraints cons = new CellConstraints();

		add(new JLabel("Dialect:"), cons.xy(2, 2));
		add(dialect, cons.xy(4, 2));

		add(new JLabel("JDBC - Driver:"), cons.xy(2, 4));
		add(driver, cons.xy(4, 4));

		add(new JLabel("JDBC - URL:"), cons.xy(2, 6));
		add(url, cons.xy(4, 6));

		add(new JLabel("User:"), cons.xy(2, 8));
		add(user, cons.xy(4, 8));

		add(new JLabel("Password:"), cons.xy(2, 10));
		add(password, cons.xy(4, 10));

		JPanel thePanel = new JPanel();

		theColDef = "50dlu,2dlu:grow,50dlu,2dlu,50dlu,2dlu";
		theRowDef = "p";

		theLayout = new FormLayout(theColDef, theRowDef);
		thePanel.setLayout(theLayout);

		thePanel.add(testButton, cons.xy(1, 1));
		testButton.setText("Test");
		testButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				handleTest();
			}

		});
		
		
		thePanel.add(okButton, cons.xy(3, 1));
		okButton.setText("Ok");
		okButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				handleOk();
			}

		});
		thePanel.add(cancelButton, cons.xy(5, 1));
		cancelButton.setText("Cancel");
		cancelButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				handleCancel();
			}
		});
		
		dialect.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				handleDialectChange((Dialect)dialect.getSelectedItem());
			}
			
		});

		add(thePanel, cons.xyw(2, 12, 3));
	}

	protected void handleTest() {

	}
	
	protected void handleOk() {

	}

	protected void handleCancel() {

	}

	public DefaultComboBox getDialect() {
		return dialect;
	}

	public void setDialect(DefaultComboBox databaseType) {
		this.dialect = databaseType;
	}

	public DefaultTextField getDriver() {
		return driver;
	}

	public void setDriver(DefaultTextField driver) {
		this.driver = driver;
	}

	public DefaultTextField getPassword() {
		return password;
	}

	public void setPassword(DefaultTextField password) {
		this.password = password;
	}

	public DefaultTextField getUrl() {
		return url;
	}

	public void setUrl(DefaultTextField url) {
		this.url = url;
	}

	public DefaultTextField getUser() {
		return user;
	}

	public void setUser(DefaultTextField user) {
		this.user = user;
	}
	
	public void handleDialectChange(Dialect aDialect) {
	}
}

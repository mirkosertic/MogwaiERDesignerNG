package de.mogwai.erdesignerng.visual.editor.connection;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.mogwai.erdesignerng.visual.IconFactory;

public class DatabaseConnectionEditorView extends JPanel {

	private JComboBox dialect = new JComboBox();

	private JTextField driver = new JTextField();

	private JTextField url = new JTextField();

	private JTextField user = new JTextField();

	private JTextField password = new JTextField();

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

		theColDef = "fill:2dlu:grow,50dlu,2dlu,50dlu,2dlu";
		theRowDef = "p";

		theLayout = new FormLayout(theColDef, theRowDef);
		thePanel.setLayout(theLayout);

		thePanel.add(okButton, cons.xy(2, 1));
		okButton.setText("Ok");
		okButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				handleOk();
			}

		});
		thePanel.add(cancelButton, cons.xy(4, 1));
		cancelButton.setText("Cancel");
		cancelButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				handleCancel();
			}
		});

		add(thePanel, cons.xyw(2, 12, 3));
	}

	protected void handleOk() {

	}

	protected void handleCancel() {

	}

	public JComboBox getDialect() {
		return dialect;
	}

	public void setDialect(JComboBox databaseType) {
		this.dialect = databaseType;
	}

	public JTextField getDriver() {
		return driver;
	}

	public void setDriver(JTextField driver) {
		this.driver = driver;
	}

	public JTextField getPassword() {
		return password;
	}

	public void setPassword(JTextField password) {
		this.password = password;
	}

	public JTextField getUrl() {
		return url;
	}

	public void setUrl(JTextField url) {
		this.url = url;
	}

	public JTextField getUser() {
		return user;
	}

	public void setUser(JTextField user) {
		this.user = user;
	}
}

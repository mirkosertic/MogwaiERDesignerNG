package de.erdesignerng.visual.editor.connection;

import de.erdesignerng.dialect.Dialect;
import de.erdesignerng.util.ConnectionDescriptor;

/**
 * Descriptor for a database dialect.
 *
 * @author $Author: mirkosertic $
 * @version $Date: 2008-11-16 14:22:01 $
 */
public class DatabaseConnectionDatamodel {

	private String alias;

	private Dialect dialect;

	private String driver;

	private String url;

	private String user;

	private String password;

	private boolean promptForPassword;

	public Dialect getDialect() {
		return dialect;
	}

	public void setDialect(Dialect aDialect) {
		dialect = aDialect;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public boolean isPromptForPassword() {
		return promptForPassword;
	}

	public void setPromptForPassword(boolean promptForPassword) {
		this.promptForPassword = promptForPassword;
	}

	/**
	 * Create a connection descriptor.
	 *
	 * @return a connection descriptor
	 */
	public ConnectionDescriptor createConnectionDescriptor() {
		return new ConnectionDescriptor(alias, dialect.getUniqueName(), url, user, driver, password, promptForPassword);
	}

}
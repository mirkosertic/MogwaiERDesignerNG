package de.mogwai.erdesignerng.visual.editor.connection;

import de.mogwai.erdesignerng.dialect.Dialect;

/**
 * Descriptor for a database dialect.
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2007-07-30 15:21:27 $
 */
public class DatabaseConnectionDatamodel {

	private Dialect dialect;

	private String driver;

	private String url;

	private String user;

	private String password;

	public Dialect getDialect() {
		return dialect;
	}

	public void setDialect(Dialect connection) {
		this.dialect = connection;
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

}

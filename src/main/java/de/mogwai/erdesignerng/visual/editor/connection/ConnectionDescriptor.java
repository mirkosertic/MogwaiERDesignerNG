package de.mogwai.erdesignerng.visual.editor.connection;

/**
 * Descriptor for a database dialect.
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2007-07-27 18:23:37 $
 */
public class ConnectionDescriptor {

	private String dialect;

	private String driver;

	private String url;

	private String user;

	private String password;

	public String getDialect() {
		return dialect;
	}

	public void setDialect(String connection) {
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

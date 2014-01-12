/**
 * Mogwai ERDesigner. Copyright (C) 2002 The Mogwai Project.
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 */
package de.erdesignerng.util;

import org.apache.commons.lang.StringUtils;

/**
 * An entry for the last used connection history.
 *
 * @author $Author: mirkosertic $
 * @version $Date: 2008-11-16 14:22:01 $
 */
public class ConnectionDescriptor {

	private final String alias;

	private final String dialect;

	private final String url;

	private final String username;

	private final String driver;

	private final String password;

	private boolean promptForPassword;

	public ConnectionDescriptor(String aAlias, String aDialect, String aURL, String aUserName, String aDriver,
								String aPassword, boolean aPromptForPassword) {
		alias = aAlias;
		dialect = aDialect;
		url = aURL;
		username = aUserName;
		driver = aDriver;
		password = aPassword;
		promptForPassword = aPromptForPassword;
	}

	/**
	 * @return the dialect
	 */
	public String getDialect() {
		return dialect;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @return the driver
	 */
	public String getDriver() {
		return driver;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @return the alias
	 */
	public String getAlias() {
		return alias;
	}

	public boolean isPromptForPassword() {
		return promptForPassword;
	}

	public void setPromptForPassword(boolean promptForPassword) {
		this.promptForPassword = promptForPassword;
	}

	@Override
	public int hashCode() {
		int prime = 31;
		int result = 1;
		result = prime * result + ((alias == null) ? 0 : alias.hashCode());
		result = prime * result + ((dialect == null) ? 0 : dialect.hashCode());
		result = prime * result + ((driver == null) ? 0 : driver.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ConnectionDescriptor other = (ConnectionDescriptor) obj;
		if (alias == null) {
			if (other.alias != null) {
				return false;
			}
		} else if (!alias.equals(other.alias)) {
			return false;
		}
		if (dialect == null) {
			if (other.dialect != null) {
				return false;
			}
		} else if (!dialect.equals(other.dialect)) {
			return false;
		}
		if (driver == null) {
			if (other.driver != null) {
				return false;
			}
		} else if (!driver.equals(other.driver)) {
			return false;
		}
		if (password == null) {
			if (other.password != null) {
				return false;
			}
		} else if (!password.equals(other.password)) {
			return false;
		}
		if (url == null) {
			if (other.url != null) {
				return false;
			}
		} else if (!url.equals(other.url)) {
			return false;
		}
		if (username == null) {
			if (other.username != null) {
				return false;
			}
		} else if (!username.equals(other.username)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		if (StringUtils.isNotEmpty(alias)) {
			return alias;
		}
		return dialect + "/" + username + " -> " + url;
	}
}
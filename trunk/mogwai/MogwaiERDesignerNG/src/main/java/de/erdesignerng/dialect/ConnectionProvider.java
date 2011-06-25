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
package de.erdesignerng.dialect;

import java.sql.Connection;

/**
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-09 19:07:30 $
 */
public interface ConnectionProvider {

	/**
	 * Create a connection.
	 * 
	 * @return a connection
	 * @throws Exception
	 *			 will be thrown in case of an error
	 */
	Connection createConnection() throws Exception;

	/**
	 * Test, of the created connection is managed.
	 * 
	 * @return true if managed, else false
	 */
	boolean generatesManagedConnection();

	/**
	 * Get the script statement separator.
	 * 
	 * @return the separator
	 */
	String createScriptStatementSeparator();
}

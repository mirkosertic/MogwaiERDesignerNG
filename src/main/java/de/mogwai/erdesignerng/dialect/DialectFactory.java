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
package de.mogwai.erdesignerng.dialect;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import de.mogwai.erdesignerng.dialect.mssql.MSSQLDialect;
import de.mogwai.erdesignerng.dialect.mysql.MySQLDialect;
import de.mogwai.erdesignerng.dialect.oracle.OracleDialect;
import de.mogwai.erdesignerng.dialect.postgres.PostgresDialect;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2007-07-28 11:08:10 $
 */
public class DialectFactory {

	private static DialectFactory me;

	private Map<String, Dialect> knownDialects = new HashMap<String, Dialect>();

	private DialectFactory() {
		registerDialect(new MSSQLDialect());
		registerDialect(new MySQLDialect());
		registerDialect(new OracleDialect());
		registerDialect(new PostgresDialect());
	}

	public static DialectFactory getInstance() {
		if (me == null) {
			me = new DialectFactory();
		}
		return me;
	}

	protected void registerDialect(Dialect aDialect) {
		knownDialects.put(aDialect.getUniqueName(), aDialect);
	}

	public Dialect getDialect(String aUniqueName) {
		return knownDialects.get(aUniqueName);
	}

	public List<Dialect> getSupportedDialects() {
		Vector<Dialect> theDialects = new Vector<Dialect>();
		theDialects.addAll(knownDialects.values());
		return theDialects;
	}
}

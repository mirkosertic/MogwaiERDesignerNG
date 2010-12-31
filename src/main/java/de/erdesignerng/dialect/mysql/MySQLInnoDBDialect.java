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
package de.erdesignerng.dialect.mysql;

import de.erdesignerng.dialect.TableProperties;
import de.erdesignerng.dialect.mysql.MySQLTableProperties.EngineEnum;
import de.erdesignerng.model.Table;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-11-02 14:20:18 $
 */
public class MySQLInnoDBDialect extends MySQLDialect {

	public MySQLInnoDBDialect() {

		seal();
	}

	@Override
	public String getUniqueName() {
		return "MySQLInnoDBDialect";
	}

	@Override
	public MySQLInnoDBSQLGenerator createSQLGenerator() {
		return new MySQLInnoDBSQLGenerator(this);
	}

	@Override
	public Class getHibernateDialectClass() {
		return org.hibernate.dialect.MySQLInnoDBDialect.class;
	}

	@Override
	public TableProperties createTablePropertiesFor(Table aTable) {
		MySQLTableProperties theProperties = new MySQLTableProperties();
		theProperties.initializeFrom(aTable);
		theProperties.setEngine(EngineEnum.InnoDB);
		return theProperties;
	}
}
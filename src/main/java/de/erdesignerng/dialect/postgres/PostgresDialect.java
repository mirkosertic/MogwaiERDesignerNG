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
package de.erdesignerng.dialect.postgres;

import de.erdesignerng.dialect.JDBCReverseEngineeringStrategy;
import de.erdesignerng.dialect.NameCastType;
import de.erdesignerng.dialect.sql92.SQL92Dialect;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-01-03 13:11:30 $
 */
public class PostgresDialect extends SQL92Dialect {

	public PostgresDialect() {
		setSpacesAllowedInObjectNames(false);
		setCaseSensitive(false);
		setMaxObjectNameLength(28);
		setNullablePrimaryKeyAllowed(false);
		setCastType(NameCastType.UPPERCASE);
	}

	@Override
	public JDBCReverseEngineeringStrategy getReverseEngineeringStrategy() {
		return new PostgresReverseEngineeringStrategy(this);
	}

	@Override
	public String getUniqueName() {
		return "PostgresDialect";
	}

	@Override
	public String getDriverClassName() {
		return "org.postgresql.Driver";
	}

	@Override
	public String getDriverURLTemplate() {
		return "jdbc:postgresql://<host>:<port>/<db>";
	}

}

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
package de.mogwai.erdesignerng.util.dialect.mssql;

import de.mogwai.erdesignerng.reverseengineering.JDBCReverseEngineeringStrategy;
import de.mogwai.erdesignerng.util.dialect.NameCastType;
import de.mogwai.erdesignerng.util.dialect.sql92.SQL92Dialect;

/**
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2007-07-08 10:06:47 $
 */
public class MSSQLDialect extends SQL92Dialect {

	public MSSQLDialect() {
		setSpacesAllowedInObjectNames(false);
		setCaseSensitive(false);
		setMaxObjectNameLength(28);
		setNullablePrimaryKeyAllowed(false);
		setCastType(NameCastType.UPPERCASE);
	}

	@Override
	public JDBCReverseEngineeringStrategy getReverseEngineeringStrategy() {
		return new MSSQLReverseEngineeringStrategy(this);
	}

	@Override
	public String getUniqueName() {
		return "MSSQLDialect";
	}

}

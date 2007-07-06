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
package de.mogwai.erdesignerng.util.dialect.oracle;

import de.mogwai.erdesignerng.model.NameCastType;
import de.mogwai.erdesignerng.reverseengineering.JDBCReverseEngineeringStrategy;
import de.mogwai.erdesignerng.util.dialect.sql92.SQL92Dialect;

/**
 * Model properties for Oracle databases.
 * 
 * @author Mirko Sertic <mail@mirkosertic.de>
 */
public class OracleDialect extends SQL92Dialect {

	public OracleDialect() {
		setSpacesAllowedInObjectNames(false);
		setCaseSensitive(false);
		setMaxObjectNameLength(28);
		setNullablePrimaryKeyAllowed(false);
		setCastType(NameCastType.UPPERCASE);
	}

	@Override
	public JDBCReverseEngineeringStrategy getReverseEngineeringStrategy() {
		return new OracleReverseEngineeringStrategy(this);
	}

}

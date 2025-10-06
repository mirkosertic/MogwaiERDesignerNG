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

import de.erdesignerng.dialect.GenericDataTypeImpl;
import de.erdesignerng.model.Attribute;

/**
 * A DataType for PostgreSQL.
 * 
 * @author mirkosertic
 */
public class PostgresDataType extends GenericDataTypeImpl {

	public PostgresDataType(final String aName, final String aDefinition, final int... aJdbcDataType) {
		super(aName, aDefinition, aJdbcDataType);
	}

	public PostgresDataType(final String aName, final String aDefinition, final boolean anIdentity, final int... aJdbcDataType) {
		super(aName, aDefinition, anIdentity, aJdbcDataType);
	}

	public PostgresDataType(final String aName, final String aDefinition, final boolean anIdentity, final boolean anArray, final int... aJdbcDataType) {
		super(aName, aDefinition, anIdentity, anArray, aJdbcDataType);
	}

	@Override
	public String createTypeDefinitionFor(final Attribute aAttribute) {
		final String theBaseName;
		String theReturn;

		//first: remove the array indicating square brackets, if necessary
		if (isArray()) {
			theBaseName = name.replace(PostgresDialect.ARRAY_INDICATOR, "");
		} else {
			theBaseName = name;
		}

		//second: construct type parameters
		if (definition == null) {
			theReturn = theBaseName;
		} else {
			final String theAppend = patternToType(aAttribute);
			final int p = theBaseName.indexOf("(");
			if (theAppend.isEmpty()) {
				if (p > 0 && theBaseName.charAt(p + 1) == ')') { // remove empty ()
					theReturn = new StringBuilder(theBaseName).delete(p, p + 2).toString();
				} else {
					theReturn = theBaseName;
				}
			} else {
				if (p > 0) {
					theReturn = new StringBuilder(theBaseName).insert(p + 1, theAppend).toString();
				} else {
					theReturn = theBaseName + "(" + theAppend + ")";
				}
			}
		}

		//third: append array indicating square brackets again, if necessary
		if (isArray()) {
			theReturn = theReturn + PostgresDialect.ARRAY_INDICATOR;
		}

		return theReturn;
	}

}
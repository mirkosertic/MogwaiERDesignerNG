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
package de.erdesignerng.dialect.hsqldb;

import de.erdesignerng.dialect.GenericDataTypeImpl;

/**
 * @author $Author: gniddelgesicht $
 * @version $Date: 2008/11/15 17:04:23 $
 */
public class HSQLDBDataType extends GenericDataTypeImpl {

	public HSQLDBDataType(final String aName, final String aDefinition, final int... aJdbcDataType) {
		super(aName, aDefinition, aJdbcDataType);
	}

	public HSQLDBDataType(final String aName, final String aDefinition, final boolean anIdentity, final int... aJdbcDataType) {
		super(aName, aDefinition, anIdentity, aJdbcDataType);
	}

	public HSQLDBDataType(final String aName, final String aDefinition, final boolean anIdentity, final boolean anArray, final int... aJdbcDataType) {
		super(aName, aDefinition, anIdentity, anArray, aJdbcDataType);
	}

}
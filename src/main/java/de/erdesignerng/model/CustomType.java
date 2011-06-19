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
package de.erdesignerng.model;

import de.erdesignerng.dialect.DataType;

import java.sql.Types;

/**
 * A custom datatype.
 * 
 * @author $Author: dr-death $
 * @version $Date: 2010-03-30 20:00:00 $
 */
public class CustomType extends OwnedModelItem<Model> implements
		ModelItemCloneable<CustomType>, DataType {

	// The schema of the custom type
	private String schema;

	// The DDL Part to create the custom type, the part after the
	// "CREATE TYPE <name> AS "
	private String sqlDefinition;

	public CustomType() {
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public String getSqlDefinition() {
		return sqlDefinition;
	}

	public void setSqlDefinition(String sqlDefinition) {
		this.sqlDefinition = sqlDefinition;
	}

	@Override
	public CustomType clone() {
		CustomType theCustomType = new CustomType();
		theCustomType.setSystemId(getSystemId());
		theCustomType.setName(getName());
		theCustomType.setSqlDefinition(getSqlDefinition());
		return theCustomType;
	}

	@Override
	public void restoreFrom(CustomType aCustomType) {
		setSystemId(aCustomType.getSystemId());
		setName(aCustomType.getName());
		setSqlDefinition(aCustomType.getSqlDefinition());
	}

	@Override
	public boolean isDomain() {
		return false;
	}

	@Override
	public boolean isCustomType() {
		return true;
	}

	@Override
	public boolean supportsSize() {
		return false;
	}

	@Override
	public boolean supportsFraction() {
		return false;
	}

	@Override
	public boolean supportsScale() {
		return false;
	}

	@Override
	public boolean supportsExtra() {
		return false;
	}

	@Override
	public boolean isJDBCStringType() {
		return false;
	}

	@Override
	public String createTypeDefinitionFor(Attribute aAttribute) {
		return getName();
	}

	@Override
	public boolean isIdentity() {
		return false;
	}

	@Override
	public int[] getJDBCType() {
		return new int[] { Types.OTHER };
	}

	@Override
	public String getDefinition() {
		return "";
	}
}
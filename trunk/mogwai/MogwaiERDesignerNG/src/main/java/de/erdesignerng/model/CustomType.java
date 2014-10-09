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
import de.erdesignerng.exception.ElementAlreadyExistsException;
import de.erdesignerng.exception.ElementInvalidNameException;
import java.sql.Types;

/**
 * A custom datatype.
 *
 * @author $Author: dr-death2 $
 * @version $Date: 2010-03-30 20:00:00 $
 */
public class CustomType extends OwnedModelItem<Model> implements ModelItemCloneable<CustomType>, DataType, AttributeProvider<CustomType> {

	// The schema of the custom type
	private String schema;

	private String alias;

	// The type of the CustomType
	private CustomTypeType type;

	private AttributeList<CustomType> attributes = new AttributeList<>();

	public CustomType() {
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getSqlDefinition() {
		StringBuilder theSQL = new StringBuilder();

		switch (type) {
			case ENUMERATION:
				if (attributes.size() > 0) {
					theSQL.append("ENUM (");
					for (int i = 0; i < attributes.size(); i++) {
						Attribute<CustomType> theAttribute = attributes.elementAt(i);

						if (i > 0) {
							theSQL.append(",");
						}
						theSQL.append("\n  '");
						theSQL.append(theAttribute.getName());
						theSQL.append("'");
					}
					theSQL.append("\n)");
				}
				break;

			case COMPOSITE:
				if (attributes.size() > 0) {
					theSQL.append("(");
					for (int i = 0; i < attributes.size(); i++) {
						Attribute<CustomType> theAttribute = attributes.elementAt(i);

						if (i > 0) {
							theSQL.append(",");
						}
						theSQL.append("\n  ");
						theSQL.append(theAttribute.getName());
						theSQL.append(" ");
						theSQL.append(theAttribute.getDatatype().createTypeDefinitionFor(theAttribute));
					}
					theSQL.append("\n)");
				}
				break;

			case EXTERNAL:
				//TODO [dr-death2] implement SQL-Definition for "external" CustomTypes
				theSQL.append("TODO: This is not implemented yet. Feel free to do this.");
				break;

			default:
		}

		return theSQL.toString();
	}

	public void setType(CustomTypeType type) {
		this.type = type;
	}

	public CustomTypeType getType() {
		return type;
	}

	public void addAttribute(Model aModel, Attribute<CustomType> aAttribute) throws ElementAlreadyExistsException, ElementInvalidNameException {
		ModelUtilities.checkNameAndExistence(attributes, aAttribute, aModel.getDialect());

		aAttribute.setOwner(this);
		attributes.add(aAttribute);
	}

	@Override
	public AttributeList<CustomType> getAttributes() {
		return attributes;
	}

	@Override
	public Attribute<CustomType> createNewAttribute() {
		Attribute<CustomType> theNewAttribute = new Attribute<>();
		theNewAttribute.setOwner(this);
		attributes.add(theNewAttribute);
		return theNewAttribute;
	}

	public void setAttributes(AttributeList attributes) {
		this.attributes = attributes;
	}

	@Override
	public CustomType clone() {
		CustomType theCustomType = new CustomType();
		theCustomType.setSystemId(getSystemId());
		theCustomType.setName(getName());
		theCustomType.setAttributes(getAttributes());
		theCustomType.setType(getType());
		theCustomType.setComment(getComment());

		return theCustomType;
	}

	@Override
	public void restoreFrom(CustomType aCustomType) {
		setSystemId(aCustomType.getSystemId());
		setName(aCustomType.getName());
		setAttributes(aCustomType.getAttributes());
		setType(aCustomType.getType());
		setComment(aCustomType.getComment());
	}

	@Override
	public boolean isDomain() {
		return false;
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
		return new int[]{Types.OTHER};
	}

	@Override
	public String getDefinition() {
		return "";
	}

	@Override
	public boolean isSpatial() {
		return false;
	}

	@Override
	public boolean isArray() {
		return false;
	}

}
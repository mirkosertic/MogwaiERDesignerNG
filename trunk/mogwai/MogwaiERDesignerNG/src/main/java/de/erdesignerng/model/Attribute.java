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

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-13 15:40:33 $
 */
public class Attribute extends OwnedModelItem<Table> implements
		ModelItemCloneable<Attribute> {

	public static final int DEFAULT_SCALE = 10;

	private DataType datatype;

	private Integer size = null;

	private int fraction;

	private int scale = DEFAULT_SCALE;

	private boolean nullable = true;

	private String defaultValue;

	private String extra;

	/**
	 * @return the nullable
	 */
	public boolean isNullable() {
		return nullable;
	}

	/**
	 * Test if this attribute is part of a foreign key.
	 * 
	 * @return true if yes, else false
	 */
	public boolean isForeignKey() {
		Table theOwner = getOwner();
		if (theOwner != null) {
			return theOwner.isForeignKey(this);
		}

		return false;
	}

	/**
	 * Test if this attribute is part of a primary key.
	 * 
	 * @return true if yes, else false
	 */
	public boolean isPrimaryKey() {
		Table theOwner = getOwner();
		if (theOwner != null) {
			return theOwner.isPrimaryKey(this);
		}

		return false;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		synchronized (this) {
			this.defaultValue = defaultValue;
		}
	}

	public void setNullable(boolean nullable) {
		synchronized (this) {
			this.nullable = nullable;
		}
	}

	public DataType getDatatype() {
		return datatype;
	}

	public void setDatatype(DataType datatype) {
		synchronized (this) {
			this.datatype = datatype;
		}
	}

	public int getFraction() {
		return fraction;
	}

	public void setFraction(int fraction) {
		synchronized (this) {
			this.fraction = fraction;
		}
	}

	public int getScale() {
		return scale;
	}

	public void setScale(int scale) {
		synchronized (this) {
			if (scale == 0) {
				// Scale can either be 2 or 10.
				// Setting to 10 if 0 fixes a bug from
				// adding new attributes
				scale = DEFAULT_SCALE;
			}
			this.scale = scale;
		}
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		synchronized (this) {
			this.size = size;
		}
	}

	@Override
	public Attribute clone() {
		Attribute theAttribute = new Attribute();
		theAttribute.setSystemId(getSystemId());
		theAttribute.setOwner(getOwner());
		theAttribute.setName(getName());
		theAttribute.setDatatype(getDatatype());
		theAttribute.setSize(getSize());
		theAttribute.setFraction(getFraction());
		theAttribute.setScale(getScale());
		theAttribute.setNullable(isNullable());
		theAttribute.setDefaultValue(getDefaultValue());
		theAttribute.setComment(getComment());
		theAttribute.setExtra(getExtra());
		return theAttribute;
	}

	@Override
	public void restoreFrom(Attribute aValue) {
		setName(aValue.getName());
		setDatatype(aValue.getDatatype());
		setSize(aValue.getSize());
		setFraction(aValue.getFraction());
		setScale(aValue.getScale());
		setNullable(aValue.isNullable());
		setDefaultValue(aValue.getDefaultValue());
		setComment(aValue.getComment());
		setExtra(aValue.getExtra());
		setOwner(aValue.getOwner());
	}

	public boolean isModified(Attribute aAttribute, boolean aUseConcreteDataType) {

		if (!getName().equals(aAttribute.getName())) {
			return true;
		}

		if (aUseConcreteDataType) {
			if (!getPhysicalDeclaration().equals(
					aAttribute.getPhysicalDeclaration())) {
				return true;
			}
		} else {
			if (!getDatatype().getName().equals(
					aAttribute.getDatatype().getName())) {
				return true;
			}
		}

		if (datatype.supportsSize()) {
			if (size != null && !size.equals(aAttribute.getSize())) {
				return true;
			}
			if (size == null && aAttribute.getSize() != null) {
				return true;
			}
		}

		if (datatype.supportsFraction()) {
			if (fraction != aAttribute.getFraction()) {
				return true;
			}
		}

		if (datatype.supportsScale()) {
			if (scale != aAttribute.getScale()) {
				return true;
			}
		}

		if (isStringModified(defaultValue, aAttribute.getDefaultValue())) {
			return true;
		}

		if (isStringModified(getComment(), aAttribute.getComment())) {
			return true;
		}

		if (isStringModified(extra, aAttribute.getExtra())) {
			return true;
		}

		if (!nullable == aAttribute.isNullable()) {
			return true;
		}

		return false;
	}

	/**
	 * @return the extra
	 */
	public String getExtra() {
		return extra;
	}

	/**
	 * @param extra
	 *            the extra to set
	 */
	public void setExtra(String extra) {
		synchronized (this) {
			this.extra = extra;
		}
	}

	/**
	 * Test if the attribute was renamed.
	 * 
	 * @param aAttribute
	 *            the new attribute
	 * @return true if it was renamed, else false
	 */
	public boolean isRenamed(Attribute aAttribute) {
		return !getName().equals(aAttribute.getName());
	}

	public String getPhysicalDeclaration() {
		synchronized (this) {
			return datatype.createTypeDefinitionFor(this);
		}
	}

	public String getLogicalDeclaration() {
		if (datatype.isDomain()) {
			return datatype.getName();
		}
		return getPhysicalDeclaration();
	}
}
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

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-01-12 17:10:01 $
 */
public class Attribute extends OwnedModelItem<Table> implements
		ModelItemClonable<Attribute> {

	private Domain domain;

	private boolean nullable;

	private DefaultValue defaultValue;

	private boolean primaryKey;

	public void setDefinition(Domain aDomain, boolean aNullable,
			DefaultValue aDefaultValue) {

		nullable = aNullable;
		domain = aDomain;
		defaultValue = aDefaultValue;
	}

	/**
	 * @return the domain
	 */
	public Domain getDomain() {
		return domain;
	}

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

	public DefaultValue getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(DefaultValue defaultValue) {
		this.defaultValue = defaultValue;
	}

	public void setDomain(Domain domain) {
		this.domain = domain;
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	public boolean isPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}

	@Override
	public Attribute clone() {
		Attribute theAttribute = new Attribute();
		theAttribute.setSystemId(getSystemId());
		theAttribute.setOwner(getOwner());
		theAttribute.setName(getName());
		theAttribute.setDomain(getDomain());
		theAttribute.setNullable(isNullable());
		theAttribute.setDefaultValue(getDefaultValue());
		theAttribute.setComment(getComment());
		theAttribute.setPrimaryKey(isPrimaryKey());
		return theAttribute;
	}

	public void restoreFrom(Attribute aValue) throws Exception {
		setName(aValue.getName());
		setDomain(aValue.getDomain());
		setNullable(aValue.isNullable());
		setDefaultValue(aValue.getDefaultValue());
		setComment(aValue.getComment());
		setPrimaryKey(aValue.isPrimaryKey());
		setOwner(aValue.getOwner());
	}

}

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
 * A Domain.
 *
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-13 15:40:33 $
 */
public class Domain extends OwnedModelItem<Model> implements ModelItemCloneable<Domain>, DataType {

	private Integer size;

	private Integer fraction;

	private int scale = 10;

	private DataType concreteType;

	private boolean nullable = true;

	public Domain() {
	}

	/**
	* @return the size
	*/
	public Integer getSize() {
		return size;
	}

	/**
	* @param size the size to set
	*/
	public void setSize(Integer size) {
		this.size = size;
	}

	/**
	* @return the fraction
	*/
	public Integer getFraction() {
		return fraction;
	}

	/**
	* @param fraction the fraction to set
	*/
	public void setFraction(Integer fraction) {
		this.fraction = fraction;
	}

	/**
	* @return the scale
	*/
	public int getScale() {
		return scale;
	}

	/**
	* @param scale the scale to set
	*/
	public void setScale(int scale) {
		this.scale = scale;
	}

	/**
	* @return the concreteType
	*/
	public DataType getConcreteType() {
		return concreteType;
	}

	/**
	* @param concreteType the concreteType to set
	*/
	public void setConcreteType(DataType concreteType) {
		this.concreteType = concreteType;
	}

	@Override
	public boolean supportsFraction() {
		return concreteType.supportsFraction();
	}

	@Override
	public boolean supportsScale() {
		return concreteType.supportsScale();
	}

	@Override
	public boolean supportsSize() {
		return concreteType.supportsSize();
	}

	@Override
	public Domain clone() {
		Domain theDomain = new Domain();
		theDomain.setSystemId(getSystemId());
		theDomain.setName(getName());
		theDomain.setConcreteType(concreteType);
		theDomain.setSize(size);
		theDomain.setFraction(fraction);
		theDomain.setScale(scale);
		theDomain.setNullable(nullable);
		theDomain.setComment(getComment());
		return theDomain;
	}

	/**
	* Restore the data from a clone.
	*
	* @param aValue the clone
	*/
	@Override
	public void restoreFrom(Domain aValue) {
		setName(aValue.getName());
		setSystemId(aValue.getSystemId());
		setConcreteType(aValue.getConcreteType());
		setSize(aValue.getSize());
		setFraction(aValue.getFraction());
		setScale(aValue.getScale());
		setNullable(aValue.isNullable());
		setComment(aValue.getComment());
	}

	@Override
	public String createTypeDefinitionFor(Attribute aAttribute) {
		Attribute theClone = aAttribute.clone();
		theClone.setSize(size);
		theClone.setFraction(fraction);
		theClone.setScale(scale);
		theClone.setNullable(nullable);
		return concreteType.createTypeDefinitionFor(theClone);
	}

	@Override
	public boolean isDomain() {
		return true;
	}

	@Override
	public boolean isJDBCStringType() {
		return concreteType.isJDBCStringType();
	}

	@Override
	public int[] getJDBCType() {
		return concreteType.getJDBCType();
	}

	@Override
	public boolean isIdentity() {
		return concreteType.isIdentity();
	}

	@Override
	public String getDefinition() {
		return concreteType.getDefinition();
	}

	@Override
	public boolean isSpatial() {
		return false;
	}

	@Override
	public boolean supportsExtra() {
		return concreteType.supportsExtra();
	}

	public boolean isNullable() {
		return nullable;
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	@Override
	public boolean isArray() {
		return false;
	}
}
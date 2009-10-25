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
public class Domain extends OwnedModelItem<Model> implements ModelItemClonable<Domain>, DataType {

    private int size;

    private int fraction;

    private int scale = 10;

    private DataType concreteType;

    public Domain() {
    }

    /**
     * @return the size
     */
    public int getSize() {
        return size;
    }

    /**
     * @param size
     *            the size to set
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * @return the fraction
     */
    public int getFraction() {
        return fraction;
    }

    /**
     * @param fraction
     *            the fraction to set
     */
    public void setFraction(int fraction) {
        this.fraction = fraction;
    }

    /**
     * @return the scale
     */
    public int getScale() {
        return scale;
    }

    /**
     * @param scale
     *            the scale to set
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
     * @param concreteType
     *            the concreteType to set
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
        return theDomain;
    }

    /**
     * Restore the data from a clone.
     * 
     * @param aValue
     *            the clone
     */
    public void restoreFrom(Domain aValue) {
        setName(aValue.getName());
        setSystemId(aValue.getSystemId());
        setConcreteType(aValue.getConcreteType());
        setSize(aValue.getSize());
        setFraction(aValue.getFraction());
        setScale(aValue.getScale());
    }

    @Override
    public String createTypeDefinitionFor(Attribute aAttribute) {
        Attribute theTemp = new Attribute();
        theTemp.setDatatype(concreteType);
        theTemp.setSize(size);
        theTemp.setFraction(fraction);
        theTemp.setScale(scale);
        return concreteType.createTypeDefinitionFor(theTemp);
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
    public boolean supportsExtra() {
        return concreteType.supportsExtra();
    }
}
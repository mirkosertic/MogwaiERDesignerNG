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
 * @version $Date: 2008-02-08 19:38:17 $
 */
public class Attribute extends OwnedModelItem<Table> implements ModelItemClonable<Attribute> {

    private DataType datatype;

    private int size;

    private int fraction;

    private int scale;

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

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }
    
    public DataType getDatatype() {
        return datatype;
    }

    public void setDatatype(DataType datatype) {
        this.datatype = datatype;
    }

    public int getFraction() {
        return fraction;
    }

    public void setFraction(int fraction) {
        this.fraction = fraction;
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
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
        return theAttribute;
    }

    public void restoreFrom(Attribute aValue) throws Exception {
        setName(aValue.getName());
        setDatatype(aValue.getDatatype());
        setSize(aValue.getSize());
        setFraction(aValue.getFraction());
        setScale(aValue.getScale());
        setNullable(aValue.isNullable());
        setDefaultValue(aValue.getDefaultValue());
        setComment(aValue.getComment());
        setOwner(aValue.getOwner());
    }

    public boolean isModified(Attribute aAttribute) {

        if (!getName().equals(aAttribute.getName())) {
            return true;
        }

        if (!datatype.equals(aAttribute.getDatatype())) {
            return true;
        }

        if (size != aAttribute.getSize()) {
            return true;
        }

        if (fraction != aAttribute.getFraction()) {
            return true;
        }

        if (scale != aAttribute.getScale()) {
            return true;
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
     * @param extra the extra to set
     */
    public void setExtra(String extra) {
        this.extra = extra;
    }

    public boolean isRenamed(Attribute aAttribute) {
        return !getName().equals(aAttribute.getName());
    }

    public String getPhysicalDeclaration() {
        return datatype.createTypeDefinitionFor(this);
    }
}

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

/**
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2008-01-31 20:08:52 $
 */
public class Domain extends OwnedModelItem<Model> implements ModelItemClonable<Domain> {

    private DataType datatype;

    private int size;

    private int precision;

    private int scale;

    /**
     * @return the datatype
     */
    public DataType getDatatype() {
        return datatype;
    }

    /**
     * @param datatype the datatype to set
     */
    public void setDatatype(DataType datatype) {
        this.datatype = datatype;
    }

    /**
     * @return the precision
     */
    public int getPrecision() {
        return precision;
    }

    /**
     * @param precision the precision to set
     */
    public void setPrecision(int precision) {
        this.precision = precision;
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
     * @return the size
     */
    public int getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public Domain clone() {
        Domain theValue = new Domain();
        theValue.setName(getName());
        theValue.setDatatype(getDatatype());
        theValue.setSize(getSize());
        theValue.setPrecision(getPrecision());
        theValue.setScale(getScale());        
        theValue.setComment(getComment());
        
        return theValue;
    }

    public void restoreFrom(Domain aValue) throws ElementAlreadyExistsException, ElementInvalidNameException {
        setName(aValue.getName());
        setDatatype(aValue.getDatatype());
        setComment(aValue.getComment());
        setSize(aValue.getSize());
        setPrecision(aValue.getPrecision());
        setScale(aValue.getScale());
    }

    public boolean equals(String aDataType, int aSize, int aPrecision, int aScale) {
        return (size == aSize) && (precision == aPrecision) && (scale == aScale) && (aDataType.equals(datatype));
    }

}

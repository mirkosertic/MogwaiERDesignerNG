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

import de.erdesignerng.exception.ElementAlreadyExistsException;
import de.erdesignerng.exception.ElementInvalidNameException;
import de.erdesignerng.model.serializer.DomainSerializer;

/**
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2008-01-22 23:25:43 $
 */
public class Domain extends OwnedModelItem<Model> implements ModelItemClonable<Domain> {

    private String datatype;

    private int domainSize;

    private int fraction;

    private int radix;

    private boolean sequenced;

    private String javaClassName;
    
    public static final DomainSerializer SERIALIZER = new DomainSerializer();

    /**
     * Gibt den Wert des Attributs <code>datatype</code> zurück.
     * 
     * @return Wert des Attributs datatype.
     */
    public String getDatatype() {
        return datatype;
    }

    /**
     * Setzt den Wert des Attributs <code>datatype</code>.
     * 
     * @param aDatatype
     *            Wert für das Attribut datatype.
     */
    public void setDatatype(String aDatatype) {
        datatype = aDatatype;
    }

    /**
     * Gibt den Wert des Attributs <code>sequenced</code> zurück.
     * 
     * @return Wert des Attributs sequenced.
     */
    public boolean isSequenced() {
        return sequenced;
    }

    /**
     * Setzt den Wert des Attributs <code>sequenced</code>.
     * 
     * @param aSequenced
     *            Wert für das Attribut sequenced.
     */
    public void setSequenced(boolean aSequenced) {
        sequenced = aSequenced;
    }

    /**
     * Gibt den Wert des Attributs <code>javaClassName</code> zurück.
     * 
     * @return Wert des Attributs javaClassName.
     */
    public String getJavaClassName() {
        return javaClassName;
    }

    /**
     * Setzt den Wert des Attributs <code>javaClassName</code>.
     * 
     * @param aJavaClassName
     *            Wert für das Attribut javaClassName.
     */
    public void setJavaClassName(String aJavaClassName) {
        javaClassName = aJavaClassName;
    }

    /**
     * @return the faction
     */
    public int getFraction() {
        return fraction;
    }

    /**
     * @param faction
     *            the faction to set
     */
    public void setFraction(int faction) {
        this.fraction = faction;
    }

    /**
     * @return the radix
     */
    public int getRadix() {
        return radix;
    }

    /**
     * @param radix
     *            the radix to set
     */
    public void setRadix(int radix) {
        this.radix = radix;
    }

    /**
     * @return the size
     */
    public int getDomainSize() {
        return domainSize;
    }

    /**
     * @param size
     *            the size to set
     */
    public void setDomainSize(int size) {
        this.domainSize = size;
    }

    @Override
    public Domain clone() {
        Domain theValue = new Domain();
        theValue.setName(getName());
        theValue.setDatatype(getDatatype());
        theValue.setSequenced(isSequenced());
        theValue.setJavaClassName(getJavaClassName());
        theValue.setComment(getComment());
        theValue.setDomainSize(getDomainSize());
        theValue.setFraction(getFraction());
        theValue.setRadix(getRadix());
        return theValue;
    }

    public void restoreFrom(Domain aValue) throws ElementAlreadyExistsException, ElementInvalidNameException {
        setName(aValue.getName());
        setDatatype(aValue.getDatatype());
        setSequenced(aValue.isSequenced());
        setJavaClassName(aValue.getJavaClassName());
        setComment(aValue.getComment());
        setDomainSize(aValue.getDomainSize());
        setFraction(aValue.getFraction());
        setRadix(aValue.getRadix());
    }

    public boolean equals(String aDataType, int aSize, int aFraction, int aRadix) {
        return (domainSize == aSize) && (fraction == aFraction) && (radix == aRadix) && (aDataType.equals(datatype));
    }

}

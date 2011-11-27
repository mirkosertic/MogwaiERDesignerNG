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
package de.erdesignerng.model.serializer.repository.entities;

/**
 * Entity for attributes.
 *
 * @author mirkosertic
 */
public class AttributeEntity extends ModelEntity {
    /*
      * TODO [mirkosertic] Unify
      * de.erdesignerng.model.serializer.repository.entities.AttributeEntity and
      * de.erdesignerng.model.Attribute to reduce redundancy and increase
      * maintainability. Then only store de.erdesignerng.model.Attribute to
      * hibernate.
      *
      * Dito for similar classes.
      */
    private static final int DEFAULT_SCALE = 10;

    private String datatype;

    private String domainId;

    private Integer size = null;

    private Integer fraction = null;

    private int scale = DEFAULT_SCALE;

    private boolean nullable = true;

    private String defaultValue;

    private String extra;

    /**
     * @return the datatype
     */
    public String getDatatype() {
        return datatype;
    }

    /**
     * @param datatype the datatype to set
     */
    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    /**
     * @return the domain
     */
    public String getDomainId() {
        return domainId;
    }

    /**
     * @param domain the domain to set
     */
    public void setDomainId(String domain) {
        domainId = domain;
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
    public Integer getScale() {
        return scale;
    }

    /**
     * @param scale the scale to set
     */
    public void setScale(Integer scale) {
        this.scale = scale;
    }

    /**
     * @return the nullable
     */
    public boolean isNullable() {
        return nullable;
    }

    /**
     * @param nullable the nullable to set
     */
    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    /**
     * @return the defaultValue
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * @param defaultValue the defaultValue to set
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
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
}
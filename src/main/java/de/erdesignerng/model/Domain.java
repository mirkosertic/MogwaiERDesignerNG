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

import java.sql.Types;

import de.erdesignerng.dialect.DataType;

/**
 * A Domain. 
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2008-06-13 17:32:43 $
 */
public class Domain extends DataType implements LayoutProvider {

    private Attribute attribute = new Attribute();
    
    public Domain() {
        super("", "", Types.OTHER);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supportsFraction() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supportsScale() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supportsSize() {
        return false;
    }
    
    /**
     * Gibt den Wert des Attributs <code>attribute</code> zurück.
     * 
     * @return Wert des Attributs attribute.
     */
    public Attribute getAttribute() {
        return attribute;
    }

    /**
     * Setzt den Wert des Attributs <code>attribute</code>.
     * 
     * @param attribute Wert für das Attribut attribute.
     */
    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    /**
     * {@inheritDoc}
     */
    public String getDefaultValue() {
        return attribute.getDefaultValue();
    }

    /**
     * {@inheritDoc}
     */
    public String getExtra() {
        return attribute.getExtra();
    }

    /**
     * {@inheritDoc}
     */
    public String getPhysicalDeclaration() {
        return attribute.getPhysicalDeclaration();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isNullable() {
        return attribute.isNullable();
    }

    /**
     * {@inheritDoc}
     */
    public String getLogicalDeclaration() {
        return getName();
    }
}
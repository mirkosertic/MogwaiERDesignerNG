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
package de.erdesignerng.dialect.config;

public class ConfigDataType {

    private String name;
    
    private String pattern;

    /**
     * Gibt den Wert des Attributs <code>name</code> zurück.
     * 
     * @return Wert des Attributs name.
     */
    public String getName() {
        return name;
    }

    /**
     * Setzt den Wert des Attributs <code>name</code>.
     * 
     * @param name Wert für das Attribut name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gibt den Wert des Attributs <code>pattern</code> zurück.
     * 
     * @return Wert des Attributs pattern.
     */
    public String getPattern() {
        return pattern;
    }

    /**
     * Setzt den Wert des Attributs <code>pattern</code>.
     * 
     * @param pattern Wert für das Attribut pattern.
     */
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }
}
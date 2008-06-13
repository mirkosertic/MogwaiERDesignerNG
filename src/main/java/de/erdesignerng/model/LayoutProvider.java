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
 * Provider for layout information. 
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2008-06-13 16:48:57 $
 */
public interface LayoutProvider {

    /**
     * Get the default value. 
     * 
     * @return the default value
     */
    String getDefaultValue();
    
    /**
     * Get the extras. 
     * 
     * @return the extras
     */
    String getExtra();
    
    /**
     * Test if this is nullable. 
     * 
     * @return true if nullable, else false
     */
    boolean isNullable();
    
    /**
     * Get the Physical declaration. 
     * 
     * @return the declaration
     */
    String getPhysicalDeclaration();
    
    /**
     * Get the logical declaration. 
     * 
     * @return the logical declaration
     */
    String getLogicalDeclaration();
}

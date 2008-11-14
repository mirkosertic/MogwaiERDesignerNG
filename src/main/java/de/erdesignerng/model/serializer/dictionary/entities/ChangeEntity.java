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
package de.erdesignerng.model.serializer.dictionary.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * @author msertic
 */
public class ChangeEntity extends ModelEntity {

    private List<String> statements = new ArrayList<String>();

    /**
     * @return the statements
     */
    public List<String> getStatements() {
        return statements;
    }

    /**
     * @param statements the statements to set
     */
    public void setStatements(List<String> statements) {
        this.statements = statements;
    }
}
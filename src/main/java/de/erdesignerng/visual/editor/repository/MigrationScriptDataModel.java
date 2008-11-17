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
package de.erdesignerng.visual.editor.repository;

import de.erdesignerng.model.serializer.repository.entities.ChangeEntity;

/**
 * Datamodel for the migration script dialog.
 * 
 * @author msertic
 */
public class MigrationScriptDataModel {

    private ChangeEntity sourceChange;
    
    private ChangeEntity destinationChange;

    /**
     * @return the sourceChange
     */
    public ChangeEntity getSourceChange() {
        return sourceChange;
    }

    /**
     * @param sourceChange the sourceChange to set
     */
    public void setSourceChange(ChangeEntity sourceChange) {
        this.sourceChange = sourceChange;
    }

    /**
     * @return the destinationChange
     */
    public ChangeEntity getDestinationChange() {
        return destinationChange;
    }

    /**
     * @param destinationChange the destinationChange to set
     */
    public void setDestinationChange(ChangeEntity destinationChange) {
        this.destinationChange = destinationChange;
    }
}
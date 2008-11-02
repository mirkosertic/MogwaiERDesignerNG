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

import java.util.HashMap;
import java.util.Map;

/**
 * Entity for a table.
 * 
 * @author msertic
 */
public class RelationEntity extends ModelEntity {

    private String importingTable;

    private String exportingTable;

    private Map<String, String> mapping = new HashMap<String, String>();

    private int onDelete;

    private int onUpdate;

    /**
     * @return the importingTable
     */
    public String getImportingTable() {
        return importingTable;
    }

    /**
     * @param importingTable
     *                the importingTable to set
     */
    public void setImportingTable(String importingTable) {
        this.importingTable = importingTable;
    }

    /**
     * @return the exportingTable
     */
    public String getExportingTable() {
        return exportingTable;
    }

    /**
     * @param exportingTable
     *                the exportingTable to set
     */
    public void setExportingTable(String exportingTable) {
        this.exportingTable = exportingTable;
    }

    /**
     * @return the mapping
     */
    public Map<String, String> getMapping() {
        return mapping;
    }

    /**
     * @param mapping
     *                the mapping to set
     */
    public void setMapping(Map<String, String> mapping) {
        this.mapping = mapping;
    }

    /**
     * @return the onDelete
     */
    public int getOnDelete() {
        return onDelete;
    }

    /**
     * @param onDelete
     *                the onDelete to set
     */
    public void setOnDelete(int onDelete) {
        this.onDelete = onDelete;
    }

    /**
     * @return the onUpdate
     */
    public int getOnUpdate() {
        return onUpdate;
    }

    /**
     * @param onUpdate
     *                the onUpdate to set
     */
    public void setOnUpdate(int onUpdate) {
        this.onUpdate = onUpdate;
    }
}
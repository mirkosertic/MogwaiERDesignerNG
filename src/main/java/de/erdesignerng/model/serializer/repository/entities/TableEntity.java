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

import java.util.ArrayList;
import java.util.List;

/**
 * Entity for a table.
 *
 * @author mirkosertic
 */
public class TableEntity extends ModelEntity {

    private List<AttributeEntity> attributes = new ArrayList<AttributeEntity>();

    private List<IndexEntity> indexes = new ArrayList<IndexEntity>();

    private String schema;

    /**
     * @return the attributes
     */
    public List<AttributeEntity> getAttributes() {
        return attributes;
    }

    /**
     * @return the indexes
     */
    public List<IndexEntity> getIndexes() {
        return indexes;
    }

    /**
     * @return the schema
     */
    public String getSchema() {
        return schema;
    }

    /**
     * @param schema the schema to set
     */
    public void setSchema(String schema) {
        this.schema = schema;
    }
}
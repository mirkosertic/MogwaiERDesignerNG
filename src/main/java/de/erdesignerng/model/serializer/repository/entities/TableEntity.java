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
public class TableEntity extends ModelEntity implements AttributeEntityProvider {

    private List<AttributeEntity> attributes = new ArrayList<>();

    private List<IndexEntity> indexes = new ArrayList<>();

    private String schema;

	@Override
    public List<AttributeEntity> getAttributes() {
        return attributes;
    }

    public List<IndexEntity> getIndexes() {
        return indexes;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public void setAttributes(List<AttributeEntity> attributes) {
        this.attributes = attributes;
    }

    public void setIndexes(List<IndexEntity> indexes) {
        this.indexes = indexes;
    }
}
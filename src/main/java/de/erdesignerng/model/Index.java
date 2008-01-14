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
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2008-01-14 20:01:06 $
 */
public class Index extends OwnedModelItem<Table> {

    private IndexType indexType = IndexType.UNIQUE;

    private AttributeList attributes = new AttributeList();

    public AttributeList getAttributes() {
        return attributes;
    }

    public void setAttributes(AttributeList aAttributes) {
        attributes = aAttributes;
    }

    public IndexType getIndexType() {
        return indexType;
    }

    public void setIndexType(IndexType aIndexType) {
        indexType = aIndexType;
    }
}

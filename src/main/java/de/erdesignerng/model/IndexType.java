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
 * @author $Author: mirkosertic $
 * @version $Date: 2008-01-15 19:22:43 $
 */
public enum IndexType {
    UNDEFINED(-1),
	UNIQUE(0),
	NONUNIQUE(1),
	PRIMARYKEY(2),
	SPATIAL(3),
	FULLTEXT(4);

    private final int id;

    IndexType(final int id) {
        this.id = id;
    }

    public final int getId() {
        return id;
    }

	/**
	 * Returns the enum constant of this type with the specified id.
	 *
	 * @param id the id to return the enum constant for
	 * @return the enum constant with the specified id
	 * @throws IllegalArgumentException if this enum type has no constant with the specified id
	 */
	public static IndexType fromId(int id) {

		for(IndexType aIndexType : IndexType.values()) { 
			if (aIndexType.getId() == id) {
				return aIndexType;
			}
		}

		throw new IllegalArgumentException("Unknown type \"" + id + "\"!");
	}

	public static IndexType fromString(String aConstantName) {
		return IndexType.valueOf(aConstantName.toUpperCase());
	}

	@Override
	public final String toString() {
		return super.toString().toUpperCase();
	}
}

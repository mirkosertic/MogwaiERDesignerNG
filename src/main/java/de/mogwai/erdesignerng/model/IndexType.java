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
package de.mogwai.erdesignerng.model;

/**
 * The indextype.
 * 
 * @author Mirko Sertic <mail@mirkosertic.de>
 */
public enum IndexType {
	PRIMARYKEY("PK"), UNIQUE("UNIQUE"), NONUNIQUE("NONUNIQUE");

	private String type;

	private IndexType(String aType) {
		type = aType;
	}

	public String getType() {
		return type;
	}

	public static IndexType fromType(String aType) {
		if (PRIMARYKEY.getType().equals(aType)) {
			return PRIMARYKEY;
		}
		if (UNIQUE.getType().equals(aType)) {
			return UNIQUE;
		}
		if (NONUNIQUE.getType().equals(aType)) {
			return NONUNIQUE;
		}
		throw new IllegalArgumentException("Invalid type " + aType);
	}
}

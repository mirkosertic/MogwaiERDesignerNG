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
 * Typ for cascading functionality.
 * 
 * @author Mirko Sertic <mail@mirkosertic.de>
 */
public enum CascadeType {
	NOTHING("nothing"), CASCADE("cascade"), SET_NULL("setnull");

	private String type;

	private CascadeType(String aType) {
		type = aType;
	}

	public String getType() {
		return type;
	}

	public static CascadeType fromType(String aType) {
		if (NOTHING.getType().equals(aType)) {
			return NOTHING;
		}
		if (CASCADE.getType().equals(aType)) {
			return CASCADE;
		}
		if (SET_NULL.getType().equals(aType)) {
			return SET_NULL;
		}
		throw new IllegalArgumentException("Invalid type " + aType);
	}

}

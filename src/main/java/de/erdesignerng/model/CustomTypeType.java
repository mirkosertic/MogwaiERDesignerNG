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
 * A custom datatypes type.
 *
 * @author $Author: dr-death2 $
 * @version $Date: 2011-09-17 21:45:00 $
 * 
 * TODO: [dr-death2] extend this enum from abstract enum implementing the
 * methods and the property "id" if inheritance of enums is supported in java 8
 */
public enum CustomTypeType {

	// Do *not* rename the constants or change their ids! Their spelling is
	// used to store the model to *.mxm files and their ids are used to store
	// the model to the repository. Loading older models would fail then.
	COMPOSITE(0),
	ENUMERATION(1),
	EXTERNAL(2);

	private final int id;

	CustomTypeType(final int id) {
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
	public static CustomTypeType fromId(int id) {

		for(CustomTypeType aCustomTypeType : CustomTypeType.values()) { 
			if (aCustomTypeType.getId() == id) {
				return aCustomTypeType;
			}
		}

		throw new IllegalArgumentException("Unknown type \"" + id + "\"!");
	}

	public static CustomTypeType fromString(String aConstantName) {
		return CustomTypeType.valueOf(aConstantName.toUpperCase());
	}

	@Override
	public final String toString() {
		return super.toString().toUpperCase();
	}

}
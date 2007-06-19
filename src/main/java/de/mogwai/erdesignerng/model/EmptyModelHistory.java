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
 * An empty model history.
 * 
 * @author Mirko Sertic <mail@mirkosertic.de>
 */
public class EmptyModelHistory implements ModelHistory {

	public void createAttributeChangedCommand(Table aTable,
			String aAttributeName, Domain aDomain, boolean aNullable) {
		System.out.println("Change attribute T " + aTable.getName() + " A "
				+ aAttributeName + " D " + aDomain + " N " + aNullable);
	}

	public void createRenameAttributeCommand(Table aTable, String aOldName,
			String aNewName) {
		System.out.println("Rename attribute T " + aTable.getName() + " Ao "
				+ aOldName + " An " + aNewName);
	}

	public void createRenameRelationCommand(Relation aRelation, String aOldName,
			String aNewName) {
		System.out.println("Rename relation R " + aRelation.getName() + " Ao "
				+ aOldName + " An " + aNewName);
	}
	
	public void createRenameTableCommand(Table aTable, String aOldName,
			String aNewName) {
		System.out.println("Rename table To " + aOldName + " Tn " + aNewName);
	}

	public void createDeleteCommand(Attribute aAttribute) {
		System.out.println("Delete attribute " + aAttribute.getName());
	}

	public void createDeleteCommand(Table aTable) {
		System.out.println("Delete table " + aTable.getName());
	}

	public void createDeleteCommand(Relation aRelation) {
		System.out.println("Delete relation " + aRelation.getName());
	}

}

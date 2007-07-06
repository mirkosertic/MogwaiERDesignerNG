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
 * A default value.
 * 
 * @author Mirko Sertic <mail@mirkosertic.de>
 */
public class DefaultValue extends OwnedModelItem<Model> {

	private String datatype;

	/**
	 * Gibt den Wert des Attributs <code>datatype</code> zur�ck.
	 * 
	 * @return Wert des Attributs datatype.
	 */
	public String getDatatype() {
		return datatype;
	}

	/**
	 * Setzt den Wert des Attributs <code>datatype</code>.
	 * 
	 * @param datatype
	 *            Wert f�r das Attribut datatype.
	 */
	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}

	@Override
	protected void generateRenameHistoryCommand(String aNewName) {
	}

	@Override
	protected void generateDeleteCommand() {
	}
}

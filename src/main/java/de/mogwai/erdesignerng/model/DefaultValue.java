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

import de.mogwai.erdesignerng.exception.ElementAlreadyExistsException;
import de.mogwai.erdesignerng.exception.ElementInvalidNameException;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2007-07-08 10:06:34 $
 */
public class DefaultValue extends OwnedModelItem<Model> implements
		ModelItemClonable<DefaultValue> {

	private String datatype;

	/**
	 * Gibt den Wert des Attributs <code>datatype</code> zurück.
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
	 *            Wert für das Attribut datatype.
	 */
	public void setDatatype(String aDatatype) {
		datatype = aDatatype;
	}

	@Override
	public DefaultValue clone() {
		DefaultValue theValue = new DefaultValue();
		theValue.setName(getName());
		theValue.setDatatype(getDatatype());
		return theValue;
	}

	public void restoreFrom(DefaultValue aValue)
			throws ElementAlreadyExistsException, ElementInvalidNameException {
		setName(aValue.getName());
		setDatatype(aValue.getDatatype());
	}
}

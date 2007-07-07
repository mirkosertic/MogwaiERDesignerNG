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
 * A domain.
 * 
 * @author Mirko Sertic <mail@mirkosertic.de>
 */
public class Domain extends OwnedModelItem<Model> implements
		ModelItemClonable<Domain> {

	private String datatype;

	private boolean sequenced;

	private String javaClassName;

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
	 * @param aDatatype
	 *            Wert für das Attribut datatype.
	 */
	public void setDatatype(String aDatatype) {
		datatype = aDatatype;
	}

	/**
	 * Gibt den Wert des Attributs <code>sequenced</code> zurück.
	 * 
	 * @return Wert des Attributs sequenced.
	 */
	public boolean isSequenced() {
		return sequenced;
	}

	/**
	 * Setzt den Wert des Attributs <code>sequenced</code>.
	 * 
	 * @param aSequenced
	 *            Wert für das Attribut sequenced.
	 */
	public void setSequenced(boolean aSequenced) {
		sequenced = aSequenced;
	}

	/**
	 * Gibt den Wert des Attributs <code>javaClassName</code> zurück.
	 * 
	 * @return Wert des Attributs javaClassName.
	 */
	public String getJavaClassName() {
		return javaClassName;
	}

	/**
	 * Setzt den Wert des Attributs <code>javaClassName</code>.
	 * 
	 * @param aJavaClassName
	 *            Wert für das Attribut javaClassName.
	 */
	public void setJavaClassName(String aJavaClassName) {
		javaClassName = aJavaClassName;
	}

	@Override
	protected void generateRenameHistoryCommand(String aNewName) {
	}

	@Override
	protected void generateDeleteCommand() {
	}

	@Override
	public Domain clone() {
		Domain theValue = new Domain();
		theValue.setName(getName());
		theValue.setDatatype(getDatatype());
		theValue.setSequenced(isSequenced());
		theValue.setJavaClassName(getJavaClassName());
		return theValue;
	}

	public void restoreFrom(Domain aValue)
			throws ElementAlreadyExistsException, ElementInvalidNameException {
		renameTo(aValue.getName());
		setDatatype(aValue.getDatatype());
		setSequenced(aValue.isSequenced());
		setJavaClassName(aValue.getJavaClassName());
	}

}

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
 * A database attribute.
 * 
 * @author Mirko Sertic <mail@mirkosertic.de>
 */
public class Attribute extends OwnedModelItem<Table> {

	private Domain domain;

	private boolean nullable;

	private String defaultValue;

	public void setDefinition(Domain aDomain, boolean aNullable,
			String aDefaultValue) {
		if (getOwner() != null) {
			if (!aDomain.equals(domain) || (nullable != aNullable)) {
				nullable = aNullable;
				domain = aDomain;
				defaultValue = aDefaultValue;

				getOwner().getOwner().getModelHistory()
						.createAttributeChangedCommand(getOwner(), getName(),
								aDomain, aNullable, aDefaultValue);
			}
		} else {
			domain = aDomain;
			nullable = aNullable;
		}
	}

	/**
	 * @return the domain
	 */
	public Domain getDomain() {
		return domain;
	}

	/**
	 * @return the nullable
	 */
	public boolean isNullable() {
		return nullable;
	}

	@Override
	protected void generateRenameHistoryCommand(String aNewName) {
		Table theOwner = getOwner();
		if (theOwner != null) {
			theOwner.getOwner().getModelHistory().createRenameAttributeCommand(
					theOwner, this, aNewName);
		}
	}

	@Override
	protected void generateDeleteCommand() {
		Table theOwner = getOwner();
		if (theOwner != null) {
			theOwner.getOwner().getModelHistory().createDeleteCommand(this);
		}
	}

	/**
	 * Test if this attribute is part of a foreign key.
	 * 
	 * @return true if yes, else false
	 */
	public boolean isForeignKey() {
		return getOwner().isForeignKey(this);
	}

	/**
	 * Gibt den Wert des Attributs <code>defaultValue</code> zurück.
	 * 
	 * @return Wert des Attributs defaultValue.
	 */
	public String getDefaultValue() {
		return defaultValue;
	}
}

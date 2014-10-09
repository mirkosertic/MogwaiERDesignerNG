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

import org.apache.commons.lang.StringUtils;

public class IndexExpression extends ModelItem implements ModelItemCloneable<IndexExpression> {

	private String expression;

	private Attribute<Table> attributeRef;

	/**
	 * Gibt den Wert des Attributs <code>expression</code> zur�ck.
	 * 
	 * @return Wert des Attributs expression.
	 */
	public String getExpression() {
		return expression;
	}

	/**
	 * Setzt den Wert des Attributs <code>expression</code>.
	 * 
	 * @param expression
	 *			Wert f�r das Attribut expression.
	 */
	public void setExpression(String expression) {
		this.expression = expression;
	}

	/**
	 * Gibt den Wert des Attributs <code>attributeRef</code> zur�ck.
	 * 
	 * @return Wert des Attributs attributeRef.
	 */
	public Attribute<Table> getAttributeRef() {
		return attributeRef;
	}

	/**
	 * Setzt den Wert des Attributs <code>attributeRef</code>.
	 * 
	 * @param attributeRef
	 *			Wert f�r das Attribut attributeRef.
	 */
	public void setAttributeRef(Attribute<Table> attributeRef) {
		this.attributeRef = attributeRef;
	}

	@Override
	public IndexExpression clone() {
		IndexExpression theClone = new IndexExpression();
		theClone.setExpression(expression);
		theClone.setSystemId(getSystemId());
		if (attributeRef != null) {
			theClone.setAttributeRef(attributeRef.clone());
		}
		return theClone;
	}

	@Override
	public void restoreFrom(IndexExpression aValue) {
		expression = aValue.getExpression();
		setSystemId(aValue.getSystemId());
		Attribute<Table> theAttributeRef = aValue.getAttributeRef();
		if (theAttributeRef != null) {
			Attribute<Table> theNewAttribute = new Attribute<>();
			theNewAttribute.restoreFrom(theAttributeRef);
			attributeRef = theNewAttribute;
		} else {
			attributeRef = null;
		}
	}

	/**
	 * Test if this expression is not equals to another expression.
	 * 
	 * @param aExpression
	 *			the other expression
	 * @param aUseName
	 *			true if attribute check shall be done by name, not by system
	 *			id
	 * @return true if they are not equals
	 */
	public boolean isModified(IndexExpression aExpression, boolean aUseName) {
		if (!StringUtils.isEmpty(expression)) {
			return !expression.equals(aExpression.getExpression());
		}
		if (aUseName) {
			return !attributeRef.getName().equals(
					aExpression.getAttributeRef().getName());
		}
		return !attributeRef.equals(aExpression.getAttributeRef());
	}

	@Override
	public String toString() {
		if (!StringUtils.isEmpty(expression)) {
			return expression;
		}
		return attributeRef.toString();
	}
}
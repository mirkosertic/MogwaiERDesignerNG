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

import de.erdesignerng.exception.ElementAlreadyExistsException;
import org.apache.commons.lang.StringUtils;

public class IndexExpressionList extends ModelItemVector<IndexExpression> {

	/**
	 * Find an index expression by attribute name.
	 * 
	 * @param aAttributeName
	 *			the name of the attribute
	 * @return the expression or null if nothing was found
	 */
	public IndexExpression findByAttributeName(String aAttributeName) {
		for (IndexExpression theExpression : this) {
			Attribute<Table> theAttribute = theExpression.getAttributeRef();
			if (theAttribute != null) {
				if (aAttributeName.equals(theAttribute.getName())) {
					return theExpression;
				}
			}
		}
		return null;
	}

	/**
	 * Find an index expression by is referred attribute.
	 * 
	 * @param aAttribute
	 *			the attribute
	 * @return the expression or null if nothing was found
	 */
	public IndexExpression findByAttribute(Attribute<Table> aAttribute) {
		for (IndexExpression theExpression : this) {
			if (aAttribute.equals(theExpression.getAttributeRef())) {
				return theExpression;
			}
		}
		return null;
	}

	/**
	 * Add an expression for an attribute to the list.
	 * 
	 * @param aAttribute
	 *			the attribute
	 * @return the created IndexExpression
	 * @throws ElementAlreadyExistsException
	 *			 is thrown is the attribute is already part of this index
	 */
	public IndexExpression addExpressionFor(Attribute<Table> aAttribute)
			throws ElementAlreadyExistsException {
		for (IndexExpression theExpression : this) {
			if (aAttribute.equals(theExpression.getAttributeRef())) {
				throw new ElementAlreadyExistsException(
						"The attribute is already part of this index");
			}
		}
		IndexExpression theExpression = new IndexExpression();
		theExpression.setAttributeRef(aAttribute);
		add(theExpression);
		return theExpression;
	}

	/**
	 * Add an expression for an attribute to the list.
	 * 
	 * @param aColumnExpression
	 *			the expression
	 * @return the created IndexExpression
	 */
	public IndexExpression addExpressionFor(String aColumnExpression) {
		IndexExpression theExpression = new IndexExpression();
		theExpression.setExpression(aColumnExpression);
		add(theExpression);
		return theExpression;
	}

	/**
	 * Remove an attribute from the expression list.
	 * 
	 * @param aAttribute
	 *			the attribute to remove
	 */
	public void removeAttribute(Attribute<Table> aAttribute) {
		IndexExpression theExpression = findByAttribute(aAttribute);
		if (theExpression != null) {
			remove(theExpression);
		}
	}

	/**
	 * Check if this list contains the same expressions as another list.
	 * 
	 * @param aExpressions
	 *			the other list
	 * @return true if yes, else false
	 */
	public boolean containsAllExpressions(IndexExpressionList aExpressions) {
		if (size() == aExpressions.size()) {
			for (int i = 0; i < size(); i++) {
				IndexExpression theA = get(i);
				IndexExpression theB = aExpressions.get(i);

				if (theA.getAttributeRef() != null) {
					if (theA.getAttributeRef() != theB.getAttributeRef()) {
						return false;
					}
				}
				if (theA.getExpression() != null) {
					if (!StringUtils.equals(theA.getExpression(), theB
							.getExpression())) {
						return false;
					}
				}
			}
			return true;
		}
		return false;
	}
}
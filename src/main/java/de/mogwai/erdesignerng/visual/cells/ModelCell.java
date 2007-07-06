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
package de.mogwai.erdesignerng.visual.cells;

import java.util.Map;

/**
 * Interface for all model cells.
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2007-07-06 20:35:06 $
 */
public interface ModelCell<T> {

	/**
	 * Transfer the cell attributes to the model properties.
	 * 
	 * @param aAttributes
	 *            the cell attributes.
	 */
	void transferAttributesToProperties(Map aAttributes);

	/**
	 * Transfer the model properties to the cell attributes.
	 * 
	 * @param aObject
	 *            the model
	 */
	void transferPropertiesToAttributes(T aObject);

}

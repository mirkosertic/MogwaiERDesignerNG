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
package de.erdesignerng.model.serializer.repository.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity for indexes.
 * 
 * @author mirkosertic
 */
public class IndexEntity extends ModelEntity {

	private int type;

	private List<IndexExpressionEntity> expressions = new ArrayList<>();

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * @param type
	 *			the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}

	public List<IndexExpressionEntity> getExpressions() {
		return expressions;
	}

	public void setExpressions(List<IndexExpressionEntity> expressions) {
		this.expressions = expressions;
	}
}
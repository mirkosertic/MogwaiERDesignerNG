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

import java.util.HashMap;
import java.util.Map;

/**
 * A relation.
 * 
 * @author Mirko Sertic <mail@mirkosertic.de>
 */
public class Relation extends OwnedModelItem<Model> {

	private Table start;

	private Table end;

	private Map<Attribute, Attribute> mapping = new HashMap<Attribute, Attribute>();

	private CascadeType onDelete = CascadeType.CASCADE;

	private CascadeType onUpdate = CascadeType.CASCADE;

	/**
	 * @return the end
	 */
	public Table getEnd() {
		return end;
	}

	/**
	 * @param end
	 *            the end to set
	 */
	public void setEnd(Table end) {
		this.end = end;
	}

	/**
	 * @return the start
	 */
	public Table getStart() {
		return start;
	}

	/**
	 * @param start
	 *            the start to set
	 */
	public void setStart(Table start) {
		this.start = start;
	}

	@Override
	protected void generateRenameHistoryCommand(String aNewName) {
		if (owner != null) {
			owner.getModelHistory().createRenameRelationCommand(this,aNewName);
		}
	}

	@Override
	protected void generateDeleteCommand() {
		if (owner != null) {
			owner.getModelHistory().createDeleteCommand(this);
		}
	}

	/**
	 * @return the mapping
	 */
	public Map<Attribute, Attribute> getMapping() {
		return mapping;
	}

	public CascadeType getOnDelete() {
		return onDelete;
	}

	public void setOnDelete(CascadeType onDelete) {
		this.onDelete = onDelete;
	}

	public CascadeType getOnUpdate() {
		return onUpdate;
	}

	public void setOnUpdate(CascadeType onUpdate) {
		this.onUpdate = onUpdate;
	}

}

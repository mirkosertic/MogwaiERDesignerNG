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
package de.mogwai.erdesignerng.view.editpart;

import org.eclipse.draw2d.Figure;

public abstract class EditPart<M, F extends Figure> {

	private F figure;

	private M model;

	public EditPart() {
		figure = createFigure();
		EditPartMouseAdapter theAdapter = new EditPartMouseAdapter(this);
		figure.addMouseListener(theAdapter);
		figure.addMouseMotionListener(theAdapter);
	}

	protected abstract F createFigure();

	public F getFigure() {
		return figure;
	}

	public M getModel() {
		return model;
	}

	public void setModel(M model) {
		this.model = model;
	}

	protected abstract void updateView();

	protected abstract void updateModel();

	public String getId() {
		return "" + hashCode();
	}
}

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

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

import de.mogwai.erdesignerng.model.Table;
import de.mogwai.erdesignerng.view.figure.TableFigure;

public class TableEditPart extends EditPart<Table, TableFigure> {

	@Override
	protected TableFigure createFigure() {
		return new TableFigure();
	}

	@Override
	protected void updateModel() {
		Table theModel = getModel();
		TableFigure theFigure = getFigure();

		Point theLocation = theFigure.getLocation();

		theModel.setIntProperty(Table.PROPERTY_XLOCATION, theLocation.x);
		theModel.setIntProperty(Table.PROPERTY_YLOCATION, theLocation.y);
	}

	@Override
	protected void updateView() {
		
		TableFigure theFigure = getFigure();
		Table theModel = getModel();
		
		theFigure.setText(theModel.getName());
		
		int x = theModel.getIntProperty(Table.PROPERTY_XLOCATION, 0);
		int y = theModel.getIntProperty(Table.PROPERTY_YLOCATION, 0);
		
		theFigure.getParent().setConstraint(theFigure, new Rectangle(x,y,-1,-1));
	}
}

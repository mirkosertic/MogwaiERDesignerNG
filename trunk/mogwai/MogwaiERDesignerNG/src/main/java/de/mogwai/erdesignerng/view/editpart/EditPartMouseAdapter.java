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
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;


public class EditPartMouseAdapter implements MouseMotionListener , MouseListener {

	private int dx, dy;
	private EditPart editPart;
	
	public EditPartMouseAdapter(EditPart aEditPart) {
		editPart = aEditPart;
	}

	public void mouseDragged(MouseEvent aEvent) {

		Figure theFigure = (Figure) aEvent.getSource();

		theFigure.getParent().setConstraint(theFigure,
				new Rectangle(aEvent.x - dx, aEvent.y - dy, -1, -1));
		
		editPart.updateModel();
	}

	public void mouseEntered(MouseEvent aEvent) {
	}

	public void mouseExited(MouseEvent aEvent) {
	}

	public void mouseHover(MouseEvent aEvent) {
	}

	public void mouseMoved(MouseEvent aEvent) {
	}

	public void mouseDoubleClicked(MouseEvent aEvent) {
	}

	public void mousePressed(MouseEvent aEvent) {
		Figure theFigure = (Figure) aEvent.getSource();
		Point theLocation = theFigure.getLocation();
		
		dx = aEvent.x - theLocation.x;
		dy = aEvent.y - theLocation.y;
		
		System.out.println(dx+" "+dy);
	}

	public void mouseReleased(MouseEvent aEvent) {
	}
}

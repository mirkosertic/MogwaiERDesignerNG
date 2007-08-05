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
package de.mogwai.erdesignerng.visual;

import javax.swing.JFrame;
import javax.swing.UnsupportedLookAndFeelException;

import de.mogwai.erdesignerng.exception.ElementAlreadyExistsException;
import de.mogwai.erdesignerng.exception.ElementInvalidNameException;
import de.mogwai.erdesignerng.model.Model;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2007-08-05 13:38:49 $
 */
public final class ERDesigner {

	private ERDesigner() {
	}

	public static void main(String[] args)
			throws ElementAlreadyExistsException, ElementInvalidNameException,
			ClassNotFoundException, InstantiationException,
			IllegalAccessException, UnsupportedLookAndFeelException {

		ERDesignerMainFrame frame = new ERDesignerMainFrame();
		frame.setModel(new Model());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setExtendedState(ERDesignerMainFrame.MAXIMIZED_BOTH);
		frame.setVisible(true);
	}
}

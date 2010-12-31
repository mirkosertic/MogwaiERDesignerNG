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
package de.erdesignerng.visual.editor;

/**
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2008-01-15 19:22:44 $
 */
public interface DialogConstants {

	/**
	 * Result code for a correct closed modal dialog.
	 */
	int MODAL_RESULT_OK = 1;

	/**
	 * Result code for a canceled modal dialog.
	 */
	int MODAL_RESULT_CANCEL = 2;

	/**
	 * Show the dialog and return it's result code.
	 * 
	 * @return The model result state
	 */
	int showModal();
}

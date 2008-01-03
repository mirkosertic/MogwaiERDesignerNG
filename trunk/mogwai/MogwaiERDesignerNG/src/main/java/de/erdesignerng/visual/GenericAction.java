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
package de.erdesignerng.visual;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Icon;

/**
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2008-01-03 13:10:48 $
 */
public class GenericAction extends AbstractAction {

	private ActionListener actionListener;

	public GenericAction(String aKey) {
		this(aKey, (ActionListener) null);
	}

	public GenericAction(Icon aIcon, ActionListener aListener) {
		putValue(SMALL_ICON, aIcon);
		actionListener = aListener;
	}

	public GenericAction(Icon aIcon) {
		this(aIcon, null);
	}

	public GenericAction(String aKey, ActionListener aListener) {
		putValue(NAME, aKey);
		actionListener = aListener;
	}

	public GenericAction(String aKey, Icon aIcon, ActionListener aListener) {
		this(aKey, aListener);
		putValue(SMALL_ICON, aIcon);
	}

	public void actionPerformed(ActionEvent e) {
		if (actionListener != null) {
			actionListener.actionPerformed(e);
		}
	}
}

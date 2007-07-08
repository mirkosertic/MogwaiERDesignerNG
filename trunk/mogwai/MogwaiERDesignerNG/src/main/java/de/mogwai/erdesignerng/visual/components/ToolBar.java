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
package de.mogwai.erdesignerng.visual.components;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JToolBar;

/**
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2007-07-08 17:55:44 $
 */
public class ToolBar extends JToolBar {

	@Override
	public JButton add(final Action aAction) {
		Icon theIcon = (Icon) aAction.getValue(Action.SMALL_ICON);
		if (theIcon != null) {
			JButton theButton = new JButton(theIcon);
			theButton.setToolTipText((String) aAction
					.getValue(Action.SHORT_DESCRIPTION));
			Dimension theSize = new Dimension(32, 32);
			theButton.setPreferredSize(theSize);
			theButton.setSize(theSize);
			theButton.setMinimumSize(theSize);
			theButton.setMaximumSize(theSize);
			theButton.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					aAction.actionPerformed(e);
				}

			});
			add(theButton);

			return theButton;
		}

		return super.add(aAction);
	}

}

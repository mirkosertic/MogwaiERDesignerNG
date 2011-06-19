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
package de.erdesignerng.visual.common;

import de.mogwai.common.client.looks.components.DefaultButton;
import de.mogwai.common.client.looks.components.DefaultPopupMenu;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-01-19 15:25:31 $
 */
public class ERDesignerToolbarEntry extends DefaultButton {

	private final DefaultPopupMenu menu = new DefaultPopupMenu();

	public ERDesignerToolbarEntry(String aId) {
		super(aId);

		addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				menu.show(ERDesignerToolbarEntry.this, 0, getSize().height);
			}
		});
	}

	public void add(JMenuItem aItem) {
		menu.add(aItem);
	}

	public void addSeparator() {
		menu.addSeparator();
	}
}
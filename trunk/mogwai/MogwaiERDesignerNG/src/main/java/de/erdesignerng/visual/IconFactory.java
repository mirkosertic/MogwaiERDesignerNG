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

import javax.swing.ImageIcon;

/**
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2008-01-03 15:11:36 $
 */
public final class IconFactory {

	private IconFactory() {
	}

	public static ImageIcon getKeyIcon() {
		return new ImageIcon(IconFactory.class.getClassLoader().getResource(
				"de/erdesignerng/icons/key.gif"));
	}

	public static ImageIcon getNewIcon() {
		return new ImageIcon(IconFactory.class.getClassLoader().getResource(
				"de/erdesignerng/icons/new.png"));
	}

	public static ImageIcon getDeleteIcon() {
		return new ImageIcon(IconFactory.class.getClassLoader().getResource(
				"de/erdesignerng/icons/delete.png"));
	}

	public static ImageIcon getUpdateIcon() {
		return new ImageIcon(IconFactory.class.getClassLoader().getResource(
				"de/erdesignerng/icons/arrow_refresh_small.png"));
	}

	public static ImageIcon getSaveIcon() {
		return new ImageIcon(IconFactory.class.getClassLoader().getResource(
				"de/erdesignerng/icons/script_save.png"));
	}

	public static ImageIcon getCancelIcon() {
		return new ImageIcon(IconFactory.class.getClassLoader().getResource(
				"de/erdesignerng/icons/cancel.png"));
	}

	public static ImageIcon getArrowUpIcon() {
		return new ImageIcon(IconFactory.class.getClassLoader().getResource(
				"de/erdesignerng/icons/arrow_up.png"));
	}

	public static ImageIcon getArrowDownIcon() {
		return new ImageIcon(IconFactory.class.getClassLoader().getResource(
				"de/erdesignerng/icons/arrow_down.png"));
	}

	public static ImageIcon getFolderIcon() {
		return new ImageIcon(IconFactory.class.getClassLoader().getResource(
				"de/erdesignerng/icons/folder.png"));
	}

	public static ImageIcon getPageAddIcon() {
		return new ImageIcon(IconFactory.class.getClassLoader().getResource(
				"de/erdesignerng/icons/page_add.png"));
	}

	public static ImageIcon getFolderAddIcon() {
		return new ImageIcon(IconFactory.class.getClassLoader().getResource(
				"de/erdesignerng/icons/folder_add.png"));
	}

	public static ImageIcon getFolderRemoveIcon() {
		return new ImageIcon(IconFactory.class.getClassLoader().getResource(
				"de/erdesignerng/icons/folder_delete.png"));
	}

	public static ImageIcon getTableIcon() {
		return new ImageIcon(IconFactory.class.getClassLoader().getResource(
				"de/erdesignerng/icons/table.png"));
	}

}

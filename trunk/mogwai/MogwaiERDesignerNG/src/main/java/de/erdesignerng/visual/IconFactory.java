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

import javax.swing.*;

/**
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2008-06-13 16:48:59 $
 */
public final class IconFactory {

	private IconFactory() {
	}

	public static ImageIcon getKeyIcon() {
		return new ImageIcon(IconFactory.class.getClassLoader().getResource("de/erdesignerng/icons/key.gif"));
	}

	public static ImageIcon getMissingIcon() {
		return new ImageIcon(IconFactory.class.getClassLoader().getResource("de/erdesignerng/icons/fatalerror_obj.gif"));
	}

	public static ImageIcon getMatchingIcon() {
		return new ImageIcon(IconFactory.class.getClassLoader().getResource("de/erdesignerng/icons/ok_st_obj.gif"));
	}

	public static ImageIcon getEntityIcon() {
		return new ImageIcon(IconFactory.class.getClassLoader().getResource("de/erdesignerng/icons/entity_small.gif"));
	}

	public static ImageIcon getViewIcon() {
		return new ImageIcon(IconFactory.class.getClassLoader().getResource("de/erdesignerng/icons/view_small.gif"));
	}

	public static ImageIcon getRelationIcon() {
		return new ImageIcon(IconFactory.class.getClassLoader()
				.getResource("de/erdesignerng/icons/relation1_small.gif"));
	}

	public static ImageIcon getAttributeIcon() {
		return new ImageIcon(IconFactory.class.getClassLoader()
				.getResource("de/erdesignerng/icons/attribute_small.gif"));
	}

	public static ImageIcon getExpressionIcon() {
		return new ImageIcon(IconFactory.class.getClassLoader().getResource(
				"de/erdesignerng/icons/expression_small.gif"));
	}

	public static ImageIcon getIndexIcon() {
		return new ImageIcon(IconFactory.class.getClassLoader().getResource("de/erdesignerng/icons/index_small.gif"));
	}

	public static ImageIcon getCancelIcon() {
		return new ImageIcon(IconFactory.class.getClassLoader().getResource("de/erdesignerng/icons/cancel.png"));
	}
}

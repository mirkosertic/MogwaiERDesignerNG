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
package de.erdesignerng.visual.scaffolding;

import org.metawidget.swing.SwingMetawidget;
import org.metawidget.swing.widgetprocessor.binding.beanutils.BeanUtilsBindingProcessor;

import javax.swing.*;

public class ScaffoldingWrapper {

	private final SwingMetawidget widget;

	private final BeanUtilsBindingProcessor processor;

    private boolean components;

	public ScaffoldingWrapper(SwingMetawidget aWidget, BeanUtilsBindingProcessor aProcessor,
			boolean aComponents) {
		widget = aWidget;
		processor = aProcessor;
		components = aComponents;

        widget.getPreferredSize();
	}

	public JComponent getComponent() {
		return widget;
	}

	public void save() {
		processor.save(widget);
	}

	public boolean hasComponents() {
		return components;
	}
}

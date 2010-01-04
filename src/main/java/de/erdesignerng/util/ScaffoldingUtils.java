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
package de.erdesignerng.util;

import java.util.ResourceBundle;

import org.metawidget.inspector.composite.CompositeInspector;
import org.metawidget.inspector.composite.CompositeInspectorConfig;
import org.metawidget.inspector.java5.Java5Inspector;
import org.metawidget.inspector.propertytype.PropertyTypeInspector;
import org.metawidget.swing.SwingMetawidget;
import org.metawidget.swing.widgetprocessor.binding.beanutils.BeanUtilsBindingProcessor;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.model.Model;

public class ScaffoldingUtils {

	public final static ScaffoldingWrapper createScaffoldingPanelFor(
			Model aModel, Object aObject) {

		ERDesignerAnnotationInspector theInspector = new ERDesignerAnnotationInspector(aModel);
		BeanUtilsBindingProcessor theProcessor = new BeanUtilsBindingProcessor();

		SwingMetawidget theMetaWidget = new SwingMetawidget();
		theMetaWidget.setBundle(ResourceBundle
				.getBundle(ERDesignerBundle.BUNDLE_NAME));
		CompositeInspectorConfig inspectorConfig = new CompositeInspectorConfig()
				.setInspectors(theInspector,
						new PropertyTypeInspector(), new Java5Inspector());
		theMetaWidget.setInspector(new CompositeInspector(inspectorConfig));
		theMetaWidget.addWidgetProcessor(theProcessor);
		theMetaWidget.setToInspect(aObject);

		return new ScaffoldingWrapper(theMetaWidget, theProcessor, theInspector);
	}
}

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

import java.util.ResourceBundle;

import org.metawidget.inspector.composite.CompositeInspector;
import org.metawidget.inspector.composite.CompositeInspectorConfig;
import org.metawidget.inspector.java5.Java5Inspector;
import org.metawidget.inspector.propertytype.PropertyTypeInspector;
import org.metawidget.swing.SwingMetawidget;
import org.metawidget.swing.widgetprocessor.binding.beanutils.BeanUtilsBindingProcessor;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.model.Model;
import de.mogwai.common.i18n.ResourceHelper;

public final class ScaffoldingUtils {

	private ScaffoldingUtils() {
	}
	
	public final static ScaffoldingWrapper createScaffoldingPanelFor(
			Model aModel, Object aObject) {

		ERDesignerAnnotationInspector theInspector = new ERDesignerAnnotationInspector(
				aModel);
		BeanUtilsBindingProcessor theProcessor = new ERDesignerBeanUtilsBindingProcessor();

		SwingMetawidget theMetaWidget = new SwingMetawidget() {

			private ResourceHelper helper = ResourceHelper
					.getResourceHelper(ERDesignerBundle.BUNDLE_NAME);

			@Override
			public String getLocalizedKey(String key) {
				return helper.getText(key.toUpperCase());
			}
		};

		theMetaWidget.setBundle(ResourceBundle
				.getBundle(ERDesignerBundle.BUNDLE_NAME));
		CompositeInspectorConfig inspectorConfig = new CompositeInspectorConfig()
				.setInspectors(theInspector, new PropertyTypeInspector(),
						new Java5Inspector());
		theMetaWidget.setInspector(new CompositeInspector(inspectorConfig));
		theMetaWidget.addWidgetProcessor(theProcessor);
		theMetaWidget.setMetawidgetLayout(new JGoodiesTableLayout());
		theMetaWidget.setWidgetBuilder(new MogwaiWidgetBuilder());
		theMetaWidget.setToInspect(aObject);

		// Force the computation of the widgets
		theMetaWidget.getPreferredSize();

		return new ScaffoldingWrapper(theMetaWidget, theProcessor, theInspector);
	}
}

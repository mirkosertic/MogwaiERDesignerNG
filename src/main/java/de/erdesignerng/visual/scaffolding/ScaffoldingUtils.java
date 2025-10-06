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

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.model.Model;
import de.mogwai.common.i18n.ResourceHelper;
import org.metawidget.swing.SwingMetawidget;
import org.metawidget.swing.widgetbuilder.SwingWidgetBuilder;
import org.metawidget.swing.widgetprocessor.binding.beanutils.BeanUtilsBindingProcessor;
import org.metawidget.util.simple.StringUtils;

import java.util.ResourceBundle;

public final class ScaffoldingUtils {

	private ScaffoldingUtils() {
	}

	public static ScaffoldingWrapper createScaffoldingPanelFor(
            final Model aModel, final Object aObject) {

		final SwingMetawidget theMetaWidget = new SwingMetawidget() {

			private final ResourceHelper helper = ResourceHelper
					.getResourceHelper(ERDesignerBundle.BUNDLE_NAME);

			@Override
			public String getLocalizedKey(final String key) {
				try {
					return helper.getText(key.toUpperCase());
				} catch (final Exception e) {
					return StringUtils.RESOURCE_KEY_NOT_FOUND_PREFIX + key + StringUtils.RESOURCE_KEY_NOT_FOUND_SUFFIX;
				}
			}
		};

		theMetaWidget.setBundle(ResourceBundle
				.getBundle(ERDesignerBundle.BUNDLE_NAME));


        final BeanUtilsBindingProcessor theProcessor = new BeanUtilsBindingProcessor() {
            @Override
            public Object convertFromString(final String aValue, final Class<?> aExpectedType) {
                if (aValue == null) {
                    return null;
                }
                if (aExpectedType.isEnum()) {
                    return Enum.valueOf((Class) aExpectedType, aValue);
                }
                return super.convertFromString(aValue, aExpectedType);
            }
        };

        theMetaWidget.setWidgetBuilder(new SwingWidgetBuilder());
        theMetaWidget.addWidgetProcessor(theProcessor);
        theMetaWidget.setToInspect(aObject);

		return new ScaffoldingWrapper(theMetaWidget);
	}
}

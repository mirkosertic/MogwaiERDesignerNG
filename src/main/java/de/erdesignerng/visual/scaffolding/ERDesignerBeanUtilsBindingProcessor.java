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

import static org.metawidget.inspector.InspectionResultConstants.NAME;
import static org.metawidget.inspector.InspectionResultConstants.NO_SETTER;
import static org.metawidget.inspector.InspectionResultConstants.PROPERTY;
import static org.metawidget.inspector.InspectionResultConstants.TRUE;

import java.awt.Component;
import java.util.Map;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.text.JTextComponent;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.metawidget.swing.SwingMetawidget;
import org.metawidget.swing.widgetprocessor.binding.beanutils.BeanUtilsBindingProcessor;
import org.metawidget.util.CollectionUtils;
import org.metawidget.util.simple.PathUtils;
import org.metawidget.util.simple.StringUtils;
import org.metawidget.widgetprocessor.iface.WidgetProcessorException;

public class ERDesignerBeanUtilsBindingProcessor extends BeanUtilsBindingProcessor {

    /**
     * A better implementation for the binding converter.
     * 
     * This will not use the ConvertUtils, as here for every enum a converter
     * must be registered. To make life easier, Enums are not handled by
     * ConvertUtils.
     */
    @Override
    public Object convertFromString(String value, Class<?> expectedType) {
        if (expectedType.isEnum()) {
            return Enum.valueOf((Class<Enum>) expectedType, value);
        }
        if (!expectedType.isPrimitive()) {
            if (value == null) {
                return null;
            }
        }
        return ConvertUtils.convert(value, expectedType);
    }

    @Override
    public void onStartBuild(SwingMetawidget metawidget) {
        metawidget.putClientProperty(BeanUtilsBindingProcessor.class, null);
    }

    @Override
    public JComponent processWidget(JComponent component, String elementName, Map<String, String> attributes,
            SwingMetawidget metawidget) {
        // Unwrap JScrollPanes (for JTextAreas etc)

        JComponent componentToBind = component;

        if (componentToBind instanceof JScrollPane)
            componentToBind = (JComponent) ((JScrollPane) componentToBind).getViewport().getView();

        // Determine value property

        String componentProperty = metawidget.getValueProperty(componentToBind);

        if (componentProperty == null)
            return component;

        String path = metawidget.getPath();

        if (PROPERTY.equals(elementName))
            path += StringUtils.SEPARATOR_FORWARD_SLASH_CHAR + attributes.get(NAME);

        try {
            // Convert 'com.Foo/bar/baz' into BeanUtils notation 'bar.baz'

            String names = PathUtils.parsePath(path, StringUtils.SEPARATOR_FORWARD_SLASH_CHAR).getNames().replace(
                    StringUtils.SEPARATOR_FORWARD_SLASH_CHAR, StringUtils.SEPARATOR_DOT_CHAR);

            Object sourceValue;

            try {
                sourceValue = retrieveValueFromObject(metawidget, metawidget.getToInspect(), names);
            } catch (NoSuchMethodException e) {
                throw WidgetProcessorException.newException("Property '" + names + "' has no getter");
            }

            SavedBinding binding = new SavedBinding(componentToBind, componentProperty, names, TRUE.equals(attributes
                    .get(NO_SETTER)));
            saveValueToWidget(binding, sourceValue);

            State state = getState(metawidget);

            if (state.bindings == null)
                state.bindings = CollectionUtils.newHashSet();

            state.bindings.add(binding);
        } catch (Exception e) {
            throw WidgetProcessorException.newException(e);
        }

        return component;
    }

    /**
     * Rebinds the Metawidget to the given Object.
     * <p>
     * This method is an optimization that allows clients to load a new object
     * into the binding <em>without</em> calling setToInspect, and therefore
     * without reinspecting the object or recreating the components. It is the
     * client's responsbility to ensure the rebound object is compatible with
     * the original setToInspect.
     */

    @Override
    public void rebind(Object toRebind, SwingMetawidget metawidget) {
        metawidget.updateToInspectWithoutInvalidate(toRebind);
        State state = getState(metawidget);

        // Our bindings

        if (state.bindings != null) {
            try {
                for (SavedBinding binding : state.bindings) {
                    Object sourceValue;
                    String names = binding.getNames();

                    try {
                        sourceValue = retrieveValueFromObject(metawidget, toRebind, names);
                    } catch (NoSuchMethodException e) {
                        throw WidgetProcessorException.newException("Property '" + names + "' has no getter");
                    }

                    saveValueToWidget(binding, sourceValue);
                }
            } catch (Exception e) {
                throw WidgetProcessorException.newException(e);
            }
        }

        // Nested bindings

        for (Component component : metawidget.getComponents()) {
            if (component instanceof SwingMetawidget)
                rebind(toRebind, (SwingMetawidget) component);
        }
    }

    @Override
    public void save(SwingMetawidget metawidget) {
        State state = getState(metawidget);

        // Our bindings

        if (state.bindings != null) {
            try {
                for (SavedBinding binding : state.bindings) {
                    if (!binding.isSettable())
                        continue;

                    Object componentValue = retrieveValueFromWidget(binding);
                    saveValueToObject(metawidget, binding.getNames(), componentValue);
                }
            } catch (Exception e) {
                throw WidgetProcessorException.newException(e);
            }
        }

        // Nested bindings

        for (Component component : metawidget.getComponents()) {
            if (component instanceof SwingMetawidget)
                save((SwingMetawidget) component);
        }
    }

    //
    // Protected methods
    //

    /**
     * Retrieve value identified by the given names from the given source.
     * <p>
     * Clients may override this method to incorporate their own getter
     * convention.
     */

    @Override
    protected Object retrieveValueFromObject(SwingMetawidget metawidget, Object source, String names) throws Exception {
        return PropertyUtils.getProperty(source, names);
    }

    /**
     * Save the given value into the given source at the location specified by
     * the given names.
     * <p>
     * Clients may override this method to incorporate their own setter
     * convention.
     * 
     * @param componentValue
     *            the raw value from the <code>JComponent</code>
     */

    @Override
    protected void saveValueToObject(SwingMetawidget metawidget, String names, Object componentValue) throws Exception {
        Object source = metawidget.getToInspect();

        BeanUtils.setProperty(source, names, componentValue);
    }

    protected Object retrieveValueFromWidget(SavedBinding binding) throws Exception {
        return PropertyUtils.getProperty(binding.getComponent(), binding.getComponentProperty());
    }

    protected void saveValueToWidget(SavedBinding binding, Object sourceValue) throws Exception {
        if (sourceValue != null && !(sourceValue instanceof String) && binding.getComponent() instanceof JTextComponent) {
            sourceValue = ConvertUtils.convert(sourceValue);
        }
        PropertyUtils.setProperty(binding.getComponent(), binding.getComponentProperty(), sourceValue);
    }

    private State getState(SwingMetawidget metawidget) {
        State state = (State) metawidget.getClientProperty(BeanUtilsBindingProcessor.class);

        if (state == null) {
            state = new State();
            metawidget.putClientProperty(BeanUtilsBindingProcessor.class, state);
        }

        return state;
    }

    //
    // Inner class
    //

    /**
     * Simple, lightweight structure for saving state.
     */

    /* package private */class State {
        /* package private */Set<SavedBinding> bindings;
    }

    class SavedBinding {
        //
        //
        // Private members
        //
        //

        private Component mComponent;

        private String mComponentProperty;

        private String mNames;

        private boolean mNoSetter;

        //
        //
        // Constructor
        //
        //

        public SavedBinding(Component component, String componentProperty, String names, boolean noSetter) {
            mComponent = component;
            mComponentProperty = componentProperty;
            mNames = names;
            mNoSetter = noSetter;
        }

        //
        //
        // Public methods
        //
        //

        public Component getComponent() {
            return mComponent;
        }

        public String getComponentProperty() {
            return mComponentProperty;
        }

        /**
         * Property names into the source object.
         * <p>
         * Stored in BeanUtils style <code>foo.bar.baz</code>.
         */

        public String getNames() {
            return mNames;
        }

        public boolean isSettable() {
            return !mNoSetter;
        }
    }
}
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
package de.erdesignerng.dialect;

import de.erdesignerng.model.ModelItem;
import de.erdesignerng.model.ModelProperties;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;

import java.beans.PropertyDescriptor;

public class ModelItemProperties<T extends ModelItem> {

	protected ModelItemProperties() {
	}

	public void copyTo(final T aObject) {

		final ModelProperties theProperties = aObject.getProperties();

		try {
			for (final PropertyDescriptor theDescriptor : PropertyUtils.getPropertyDescriptors(this)) {
				if (theDescriptor.getReadMethod() != null && theDescriptor.getWriteMethod() != null) {
					final Object theValue = PropertyUtils.getProperty(this, theDescriptor.getName());
					if (theValue != null) {
						theProperties.setProperty(theDescriptor.getName(), theValue.toString());
					} else {
						theProperties.setProperty(theDescriptor.getName(), null);
					}
				}
			}
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void initializeFrom(final T aObject) {
		final ModelProperties theProperties = aObject.getProperties();

		try {
			for (final PropertyDescriptor theDescriptor : PropertyUtils.getPropertyDescriptors(this)) {
				if (theDescriptor.getReadMethod() != null && theDescriptor.getWriteMethod() != null) {
					final String theValue = theProperties.getProperty(theDescriptor.getName());
					if (!StringUtils.isEmpty(theValue)) {
						final Class theType = theDescriptor.getPropertyType();

						if (theType.isEnum()) {
							PropertyUtils.setProperty(this, theDescriptor.getName(), Enum.valueOf(theType, theValue));
						}
						if (String.class.equals(theType)) {
							PropertyUtils.setProperty(this, theDescriptor.getName(), theValue);
						}
						if (Long.class.equals(theType) || long.class.equals(theType)) {
							PropertyUtils.setProperty(this, theDescriptor.getName(), Long.parseLong(theValue));
						}
						if (Integer.class.equals(theType) || int.class.equals(theType)) {
							PropertyUtils.setProperty(this, theDescriptor.getName(), Integer.parseInt(theValue));
						}
						if (Boolean.class.equals(theType) || boolean.class.equals(theType)) {
							PropertyUtils.setProperty(this, theDescriptor.getName(), Boolean.parseBoolean(theValue));
						}
					}
				}
			}
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}

	}
}

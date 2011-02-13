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
package de.erdesignerng.visual.editor;

import javax.swing.*;

public class NullsafeSpinnerModel extends AbstractSpinnerModel {

	private Integer value;

	@Override
	public Object getNextValue() {
		if (value != null) {
			return value + 1;
		}
		return 1;
	}

	@Override
	public Object getPreviousValue() {
		if (value != null) {
			if (value > 1) {
				return value - 1;
			}
			return value;
		}
		return 1;
	}

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public void setValue(Object aValue) {
		if (aValue instanceof Long) {
			aValue = ((Long) aValue).intValue();
		}
		if (aValue != null) {
			if (!aValue.equals(value)) {

				value = (Integer) aValue;

				fireStateChanged();
			}
		} else {
			if (value != null) {
				value = null;
				fireStateChanged();
			}
		}
	}
}

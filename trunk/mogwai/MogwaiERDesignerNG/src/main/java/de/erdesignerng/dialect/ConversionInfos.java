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

import java.util.HashMap;
import java.util.Map;

/**
 * Information to do a model conversion.
 * 
 * @author $Author$
 */
public class ConversionInfos {

	private Dialect targetDialect;

	private Map<DataType, DataType> typeMapping = new HashMap<DataType, DataType>();

	public Dialect getTargetDialect() {
		return targetDialect;
	}

	public void setTargetDialect(Dialect targetDialect) {
		this.targetDialect = targetDialect;
	}

	public Map<DataType, DataType> getTypeMapping() {
		return typeMapping;
	}

	public void setTypeMapping(Map<DataType, DataType> typeMapping) {
		this.typeMapping = typeMapping;
	}
}
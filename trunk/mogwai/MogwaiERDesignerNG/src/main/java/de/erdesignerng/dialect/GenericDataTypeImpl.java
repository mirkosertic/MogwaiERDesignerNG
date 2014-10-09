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

import de.erdesignerng.model.Attribute;
import org.apache.commons.lang.StringUtils;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * An implementation of a DataType.
 *
 * @author $Author: mirkosertic $
 * @version $Date: 2008-06-15 10:57:04 $
 */
public abstract class GenericDataTypeImpl implements DataType {
	public static final String SIZE_TOKEN = "$size";

	public static final String FRACTION_TOKEN = "$fraction";

	public static final String SCALE_TOKEN = "$scale";

	public static final String EXTRA_TOKEN = "$extra";

	private static final int PRIME = 31;

	protected String name;

	protected final String definition;

	protected final int[] jdbcType;

	private boolean identity;

	private boolean array = false;

	protected int maxOccursPerTable = -1;

	private boolean supportsSize;

	private boolean supportsFraction;

	private boolean supportsScale;

	private boolean supportsExtra;

	protected GenericDataTypeImpl(String aName, String aDefinition, int... aJdbcType) {
		name = aName;
		definition = aDefinition;
		jdbcType = aJdbcType;

		for (StringTokenizer theST = new StringTokenizer(aDefinition.trim(), ","); theST.hasMoreTokens(); ) {
			String theToken = theST.nextToken().trim();

			// Bug Fixing 2876916 [ERDesignerNG] Reverse-Eng. PgSQL VARCHAR
			// max-length wrong
			// to also match optional tokens
			theToken = theToken.replace("[", "").replace("]", "");

			if (SIZE_TOKEN.equals(theToken)) {
				supportsSize = true;
			} else {
				if (FRACTION_TOKEN.equals(theToken)) {
					supportsFraction = true;
				} else {
					if (SCALE_TOKEN.equals(theToken)) {
						supportsScale = true;
					} else {
						if (EXTRA_TOKEN.equals(theToken)) {
							supportsExtra = true;
						} else {
							throw new IllegalArgumentException("Invalid Token : " + theToken);
						}
					}
				}
			}
		}
	}

	protected GenericDataTypeImpl(String aName, String aDefinition, boolean anIdentity, int... aJdbcDataType) {
		this(aName, aDefinition, aJdbcDataType);

		identity = anIdentity;
		if (anIdentity) {
			maxOccursPerTable = 1;
		}
	}

	protected GenericDataTypeImpl(String aName, String aDefinition, boolean anIdentity, boolean anArray, int... aJdbcDataType) {
		this(aName, aDefinition, anIdentity, aJdbcDataType);

		array = anArray;
	}

	/**
	 * Gibt den Wert des Attributs <code>definition</code> zurück.
	 *
	 * @return Wert des Attributs definition.
	 */
	public String getPattern() {
		return definition;
	}

	/**
	 * Gibt den Wert des Attributs <code>name</code> zurück.
	 *
	 * @return Wert des Attributs name.
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	protected String patternToType(Attribute aAttribute) {
		Map<String, String> theMapping = new HashMap<>();
		theMapping.put(SIZE_TOKEN, ((aAttribute.getSize() == null) ? null : "" + aAttribute.getSize()));
		theMapping.put(FRACTION_TOKEN, ((aAttribute.getFraction() == null) ? null : "" + aAttribute.getFraction()));
		theMapping.put(SCALE_TOKEN, "" + aAttribute.getScale());
		theMapping.put(EXTRA_TOKEN, "" + aAttribute.getExtra());

		String theResult = "";
		StringTokenizer theSt = new StringTokenizer(definition, ",");

		while (theSt.hasMoreTokens()) {
			boolean isOptional = false;
			String theToken = theSt.nextToken().trim();
			if (theToken.startsWith("[")) {
				isOptional = true;
				theToken = theToken.replace("[", "").replace("]", "");
			}

			String theValue = theMapping.get(theToken);
			if (StringUtils.isEmpty(theValue)) {
				if (!isOptional) {
					throw new RuntimeException("No value for required token " + theToken);
				}
			} else {
				theResult += ((theResult.length() > 0) ? ", " : "") + theValue;
			}
		}

		return theResult;
	}

	@Override
	public String createTypeDefinitionFor(Attribute aAttribute) {
		if (definition == null) {
			return name;
		}

		String theAppend = patternToType(aAttribute);
		if (theAppend.length() == 0) {
			return name;
		}

		int p = name.indexOf("(");
		if (p > 0) {
			return new StringBuilder(name).insert(p + 1, theAppend).toString();
		}

		return name + "(" + theAppend + ")";
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public boolean isIdentity() {
		return identity;
	}

	@Override
	public boolean isArray() {
		return array;
	}

	public int getMaxOccursPerTable() {
		return maxOccursPerTable;
	}

	@Override
	public boolean supportsFraction() {
		return supportsFraction;
	}

	@Override
	public boolean supportsScale() {
		return supportsScale;
	}

	@Override
	public boolean supportsSize() {
		return supportsSize;
	}

	@Override
	public boolean supportsExtra() {
		return supportsExtra;
	}

	@Override
	public int hashCode() {
		int result = 1;
		result = PRIME * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		DataType other = (DataType) obj;
		if (name == null) {
			if (other.getName() != null) {
				return false;
			}
		} else if (!name.equals(other.getName())) {
			return false;
		}
		return true;
	}

	/**
	 * Test if the Type is a String type.
	 *
	 * @return true if it is a string type, else false
	 */
	@Override
	public boolean isJDBCStringType() {
		for (int theType : jdbcType) {
			switch (theType) {
				case Types.CHAR:
				case Types.LONGVARCHAR:
				case Types.VARCHAR:
					return true;
			}
		}
		return false;
	}

	@Override
	public boolean isDomain() {
		return false;
	}

	@Override
	public int[] getJDBCType() {
		return jdbcType;
	}

	@Override
	public String getDefinition() {
		return definition;
	}

	@Override
	public boolean isSpatial() {
		for (int theType : jdbcType) {
			if (theType == Dialect.SPATIAL_COLUMN_TYPE) {
				return true;
			}
		}
		return false;
	}

}
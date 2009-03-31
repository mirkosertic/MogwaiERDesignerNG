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

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import de.erdesignerng.model.Attribute;

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

    public static final GenericDataTypeImpl UNDEFINED = new GenericDataTypeImpl("UNDEFINED", "", Types.OTHER) {
    };

    private static final int PRIME = 31;

    protected String name;

    protected String pattern;

    protected int[] jdbcType;

    protected boolean identity;

    protected int maxOccoursPerTable = -1;

    private boolean supportsSize;

    private boolean supportsFraction;

    private boolean supportsScale;

    protected GenericDataTypeImpl(String aName, String aDefinition, int... aJdbcType) {
        name = aName;
        pattern = aDefinition;
        jdbcType = aJdbcType;

        for (StringTokenizer theST = new StringTokenizer(aDefinition, ","); theST.hasMoreTokens();) {
            String theToken = theST.nextToken();
            if (SIZE_TOKEN.equals(theToken)) {
                supportsSize = true;
            } else {
                if (FRACTION_TOKEN.equals(theToken)) {
                    supportsFraction = true;
                } else {
                    if (SCALE_TOKEN.equals(theToken)) {
                        supportsScale = true;
                    } else {
                        throw new IllegalArgumentException("Invalid Token : " + theToken);
                    }
                }
            }
        }
    }

    /**
     * Gibt den Wert des Attributs <code>definition</code> zurück.
     * 
     * @return Wert des Attributs definition.
     */
    public String getPattern() {
        return pattern;
    }

    /**
     * Gibt den Wert des Attributs <code>name</code> zurück.
     * 
     * @return Wert des Attributs name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *                the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    protected String patternToType(Attribute aDomain) {

        Map<String, String> theMapping = new HashMap<String, String>();
        theMapping.put(SIZE_TOKEN, "" + aDomain.getSize());
        theMapping.put(FRACTION_TOKEN, "" + aDomain.getFraction());
        theMapping.put(SCALE_TOKEN, "" + aDomain.getScale());

        StringBuilder theResult = new StringBuilder();
        StringTokenizer theSt = new StringTokenizer(pattern, ",");
        while (theSt.hasMoreTokens()) {
            String theToken = theSt.nextToken();
            boolean isOptional = false;
            if (theToken.startsWith("[")) {
                isOptional = true;
                theToken = theToken.substring(1, theToken.length());
            }

            String theValue = theMapping.get(theToken);
            if ((theValue == null) && (!isOptional)) {
                throw new RuntimeException("No value for required token" + theToken);
            }

            if (isOptional) {
                if ((theValue != null) && (theValue.length() > 0)) {
                    if (theResult.length() > 0) {
                        theResult.append(",");
                    }
                    theResult.append(theValue);
                }
            } else {
                if (theResult.length() > 0) {
                    theResult.append(",");
                }
                theResult.append(theValue);
            }
        }
        return theResult.toString();
    }

    public String createTypeDefinitionFor(Attribute aAttribute) {

        if (pattern == null) {
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

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return name;
    }

    public int compareTo(DataType aType) {
        return name.compareTo(aType.getName());
    }

    /**
     * @return the identity
     */
    public boolean isIdentity() {
        return identity;
    }

    /**
     * @return the maxOccoursPerTable
     */
    public int getMaxOccoursPerTable() {
        return maxOccoursPerTable;
    }

    /**
     * @return the supportsPrecision
     */
    public boolean supportsFraction() {
        return supportsFraction;
    }

    /**
     * @return the supportsScale
     */
    public boolean supportsScale() {
        return supportsScale;
    }

    /**
     * @return the supportsSize
     */
    public boolean supportsSize() {
        return supportsSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = 1;
        result = PRIME * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
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
        final DataType other = (DataType) obj;
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
        return pattern;
    }
}
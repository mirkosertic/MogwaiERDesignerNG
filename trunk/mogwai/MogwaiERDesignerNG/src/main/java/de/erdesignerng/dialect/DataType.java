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
import java.util.StringTokenizer;

import de.erdesignerng.model.Domain;

/**
 * A database data type.
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2008-01-31 20:08:52 $
 */
public class DataType implements Comparable<DataType> {

    public static final String SIZE_TOKEN = "$size";

    public static final String PRECISION_TOKEN = "$precision";

    public static final String SCALE_TOKEN = "$scale";

    protected String name;

    protected String pattern;

    protected int jdbcType;

    protected boolean identity;

    protected int maxOccoursPerTable = -1;

    private boolean supportsSize = false;

    private boolean supportsPrecision = false;

    private boolean supportsScale = false;

    protected DataType(String aName, String aDefinition, int aJdbcType) {
        name = aName;
        pattern = aDefinition;
        jdbcType = aJdbcType;

        for (StringTokenizer theST = new StringTokenizer(aDefinition, ","); theST.hasMoreTokens();) {
            String theToken = theST.nextToken();
            if (SIZE_TOKEN.equals(theToken)) {
                supportsSize = true;
            } else {
                if (PRECISION_TOKEN.equals(theToken)) {
                    supportsPrecision = true;
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

    protected String patternToType(Domain aDomain) {

        Map<String, String> theMapping = new HashMap<String, String>();
        theMapping.put(SIZE_TOKEN, "" + aDomain.getSize());
        theMapping.put(PRECISION_TOKEN, "" + aDomain.getPrecision());
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

    public String createTypeDefinitionFor(Domain aDomain) {

        if (pattern == null) {
            return name;
        }

        String theAppend = patternToType(aDomain);
        if (theAppend.length() == 0) {
            return name;
        }

        int p = pattern.indexOf("(");
        if (p > 0) {
            return new StringBuilder(pattern).insert(p + 1, theAppend).toString();
        }

        return name + "(" + theAppend + ")";
    }

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
    public boolean supportsPrecision() {
        return supportsPrecision;
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
}
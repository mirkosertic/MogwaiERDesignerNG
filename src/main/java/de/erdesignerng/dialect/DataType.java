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

/**
 * A database data type.
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2008-01-17 19:34:29 $
 */
public class DataType {

    protected String name;

    protected String pattern;

    protected DataType(String aName, String aDefinition) {
        name = aName;
        pattern = aDefinition;
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

    protected String patternToType(String aSize, String aDecimalDigits) {

        Map<String, String> theMapping = new HashMap<String, String>();
        theMapping.put("$size", aSize);
        theMapping.put("$decimal", aDecimalDigits);

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

    public String createTypeDefinitionFor(String aSize, String aDecimalDigits) {

        if (pattern == null) {
            return name;
        }

        String theAppend = patternToType(aSize, aDecimalDigits);
        if (theAppend.length() == 0) {
            return name;
        }

        int p = pattern.indexOf("(");
        if (p > 0) {
            return new StringBuilder(pattern).insert(p + 1, theAppend).toString();
        }

        return name + "(" + theAppend + ")";
    }
}
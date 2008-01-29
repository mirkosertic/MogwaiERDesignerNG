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
 * @version $Date: 2008-01-29 22:04:11 $
 */
public class DataType {
    
    protected String id;

    protected String name;

    protected String pattern;

    protected DataType(String aId, String aName, String aDefinition) {
        name = aName;
        pattern = aDefinition;
        id = aId;
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
     * @return the id
     */
    public String getId() {
        return id;
    }

    protected String patternToType(Domain aDomain) {

        Map<String, String> theMapping = new HashMap<String, String>();
        theMapping.put("$size", "" + aDomain.getDomainSize());
        theMapping.put("$decimal", "" + aDomain.getFraction());
        theMapping.put("$radix", "" + aDomain.getRadix());

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
}
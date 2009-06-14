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
package de.erdesignerng.plugins.sqleonardo;

import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;

import de.erdesignerng.model.View;
import de.erdesignerng.model.ViewAttribute;
import de.erdesignerng.model.ViewAttributeList;

/**
 * Common SQL Utilities.
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-13 15:40:33 $
 */
public final class SQLUtils {

    private static final String SELECT_CLAUSE = "SELECT ";

    private static final String FROM_CLAUSE = " FROM ";

    private static final String AS_CLAUSE = " AS ";

    private SQLUtils() {
    }

    public static void updateViewAttributesFromSQL(View aView, String aStatement) throws Exception {

        ViewAttributeList theList = aView.getAttributes();
        theList.clear();

        if (StringUtils.isEmpty(aStatement)) {
            throw new Exception("The SQL must not be empty");
        }
        String theUppserSQL = aStatement.toUpperCase();

        int theSelectStart = theUppserSQL.indexOf(SELECT_CLAUSE);
        int theFromStart = theUppserSQL.indexOf(FROM_CLAUSE);

        if (theSelectStart < 0) {
            throw new Exception("The SQL must contain the SELECT keyword");
        }
        if (theFromStart < 0) {
            throw new Exception("The SQL must contain the FROM keyword");
        }
        if (theSelectStart > theFromStart) {
            throw new Exception("Syntax error");
        }

        String theSelectFields = aStatement.substring(theSelectStart + SELECT_CLAUSE.length(), theFromStart).trim();
        if (StringUtils.isEmpty(theSelectFields)) {
            throw new Exception("No fields are selected");
        }

        // TODO: Implement better parsing here
        StringTokenizer theST = new StringTokenizer(theSelectFields, ",");
        while (theST.hasMoreTokens()) {
            String theToken = theST.nextToken();
            int p = theToken.toUpperCase().indexOf(AS_CLAUSE);

            String theExpression = theToken;
            if (p > 0) {
                theExpression = theToken.substring(p + AS_CLAUSE.length()).trim();
            }
            
            theExpression = theExpression.trim();

            ViewAttribute theAttribute = new ViewAttribute();
            theAttribute.setName(theExpression);
            theList.add(theAttribute);
        }
    }
}
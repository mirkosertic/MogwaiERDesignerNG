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
package de.erdesignerng.util;

import de.erdesignerng.model.View;
import de.erdesignerng.model.ViewAttribute;
import de.erdesignerng.model.ViewAttributeList;
import org.apache.commons.lang.StringUtils;
import org.hibernate.jdbc.util.BasicFormatterImpl;

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

	private static void addViewAttribute(String aExpression, View aView) {
		int p = aExpression.toUpperCase().indexOf(AS_CLAUSE);
		if (p > 0) {
			aExpression = aExpression.substring(p + AS_CLAUSE.length()).trim();
		} else {
			p = aExpression.lastIndexOf(' ');
			if (p > 0) {
				aExpression = aExpression.substring(p + 1).trim();
			}
		}
		ViewAttribute theAttribute = new ViewAttribute();
		theAttribute.setName(aExpression);

		aView.getAttributes().add(theAttribute);
	}

	public static void updateViewAttributesFromSQL(View aView, String aStatement) throws Exception {

		ViewAttributeList theList = aView.getAttributes();
		theList.clear();

		if (StringUtils.isEmpty(aStatement)) {
			throw new Exception("The SQL must not be empty");
		}

		// Remove line breaks and other special characters
		aStatement = aStatement.replace('\t', ' ');
		aStatement = aStatement.replace('\n', ' ');
		aStatement = aStatement.replace('\f', ' ');
		aStatement = aStatement.replace('\r', ' ');

		String theUpperSQL = aStatement.toUpperCase();

		int theSelectStart = theUpperSQL.indexOf(SELECT_CLAUSE);
		int theFromStart = theUpperSQL.indexOf(FROM_CLAUSE);

		if (theSelectStart < 0) {
			throw new Exception("The SQL must contain the SELECT keyword : " + aStatement);
		}
		if (theFromStart < 0) {
			throw new Exception("The SQL must contain the FROM keyword : " + aStatement);
		}
		if (theSelectStart > theFromStart) {
			throw new Exception("Syntax error : " + aStatement);
		}

		String theSelectFields = aStatement.substring(theSelectStart + SELECT_CLAUSE.length(), theFromStart).trim();
		if (StringUtils.isEmpty(theSelectFields)) {
			throw new Exception("No fields are selected : " + aStatement);
		}

		String theCurrentToken = "";
		int p = 0;
		int bracesCounter = 0;
		boolean inString = false;
		while (p < theSelectFields.length()) {

			char theCurrentChar = theSelectFields.charAt(p);
			switch (theCurrentChar) {
			case '(':
				if (!inString) {
					bracesCounter++;
					theCurrentToken += theCurrentChar;
				} else {
					theCurrentToken += theCurrentChar;
				}
				break;
			case ')':
				if (!inString) {
					bracesCounter--;
				} else {
					theCurrentToken += theCurrentChar;
				}
				theCurrentToken += theCurrentChar;
				break;
			case '\"':
				inString = !inString;
				theCurrentToken += theCurrentChar;
				break;
			case '\'':
				inString = !inString;
				theCurrentToken += theCurrentChar;
				break;
			case ',':

				if (bracesCounter == 0 && !inString) {
					addViewAttribute(theCurrentToken.trim(), aView);
					theCurrentToken = "";
				} else {
					theCurrentToken += theCurrentChar;
				}
				break;
			default:
				theCurrentToken += theCurrentChar;
				break;
			}

			p++;
		}

		theCurrentToken = theCurrentToken.trim();
		if (StringUtils.isNotEmpty(theCurrentToken)) {
			addViewAttribute(theCurrentToken, aView);
		}
	}

	/**
	 * Prettyformat a SQL Statement.
	 * 
	 * This is based on the Hibernate Formatter implementation.
	 * 
	 * @param aSQLStatement
	 *		  - SQL statement to format
	 * @return pretty formatted SQL
	 */
	public static String prettyFormat(String aSQLStatement) {
		if (StringUtils.isEmpty(aSQLStatement)) {
			return aSQLStatement;
		}
		return new BasicFormatterImpl().format(aSQLStatement);
	}
}
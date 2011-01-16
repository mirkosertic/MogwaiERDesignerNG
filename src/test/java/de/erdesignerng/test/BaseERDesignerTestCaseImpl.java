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
package de.erdesignerng.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.apache.commons.lang.StringUtils;

import de.erdesignerng.dialect.SQLGenerator;
import de.erdesignerng.dialect.Statement;
import de.erdesignerng.dialect.StatementList;
import de.erdesignerng.util.ApplicationPreferences;

public abstract class BaseERDesignerTestCaseImpl extends TestCase {

	@Override
	protected void setUp() throws Exception {
		ApplicationPreferences.getInstance().setXmlIndentation(0);
	}

	protected boolean compareStrings(String aString1, String aString2) {
		aString1 = StringUtils.remove(aString1, (char) 13);
		aString1 = StringUtils.remove(aString1, (char) 10);

		aString2 = StringUtils.remove(aString2, (char) 13);
		aString2 = StringUtils.remove(aString2, (char) 10);

		System.out.println(aString1 + "\n" + aString1.length());
		System.out.println(aString2 + "\n" + aString2.length());

		return aString1.equals(aString2);
	}

	protected String readResourceFile(String aResourceName) throws IOException {
		StringWriter theStringWriter = new StringWriter();
		PrintWriter thePrintWriter = new PrintWriter(theStringWriter);
		BufferedReader theBr = new BufferedReader(new InputStreamReader(
				getClass().getResourceAsStream(aResourceName)));
		while (theBr.ready()) {
			String theLine = theBr.readLine();
			if (theLine != null && theLine.length() > 0) {
				thePrintWriter.println(theLine);
			}
		}
		theBr.close();
		thePrintWriter.flush();
		return theStringWriter.toString().trim();

	}

	protected String statementListToString(StatementList aStatements,
			SQLGenerator aGenerator) {
		StringWriter theStringWriter = new StringWriter();
		PrintWriter thePrintWriter = new PrintWriter(theStringWriter);
		for (Statement theStatement : aStatements) {
			thePrintWriter.print(theStatement.getSql());
			thePrintWriter.println(aGenerator.createScriptStatementSeparator());
		}
		thePrintWriter.flush();
		return theStringWriter.toString().trim();
	}
}

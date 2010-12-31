package de.erdesignerng.test.view;

import de.erdesignerng.model.View;
import de.erdesignerng.model.ViewAttributeList;
import de.erdesignerng.test.BaseERDesignerTestCaseImpl;
import de.erdesignerng.util.SQLUtils;

public class ParserTest extends BaseERDesignerTestCaseImpl {

	public void testParseSingleExpression() throws Exception {
		View theView = new View();
		String theStatement = "select name1 from table";

		SQLUtils.updateViewAttributesFromSQL(theView, theStatement);
		ViewAttributeList theAtrributes = theView.getAttributes();

		assertTrue(theAtrributes.size() == 1);
		assertTrue("name1".equals(theAtrributes.get(0).getName()));
	}

	public void testParseDoubleExpression() throws Exception {
		View theView = new View();
		String theStatement = "select name1 , name2 from table";

		SQLUtils.updateViewAttributesFromSQL(theView, theStatement);
		ViewAttributeList theAtrributes = theView.getAttributes();

		assertTrue(theAtrributes.size() == 2);
		assertTrue("name1".equals(theAtrributes.get(0).getName()));
		assertTrue("name2".equals(theAtrributes.get(1).getName()));
	}

	public void testParseDoubleExpressionWithAlias() throws Exception {
		View theView = new View();
		String theStatement = "select name1 , name2 as alias from table";

		SQLUtils.updateViewAttributesFromSQL(theView, theStatement);
		ViewAttributeList theAtrributes = theView.getAttributes();

		assertTrue(theAtrributes.size() == 2);
		assertTrue("name1".equals(theAtrributes.get(0).getName()));
		assertTrue("alias".equals(theAtrributes.get(1).getName()));
	}

	public void testSyntaxError1() throws Exception {
		View theView = new View();
		String theStatement = "";

		try {
			SQLUtils.updateViewAttributesFromSQL(theView, theStatement);
			fail("Syntax error in " + theStatement + " not recognized");
		} catch (Exception e) {

		}
	}

	public void testSyntaxError2() throws Exception {
		View theView = new View();
		String theStatement = "select name1";

		try {
			SQLUtils.updateViewAttributesFromSQL(theView, theStatement);
			fail("Syntax error in " + theStatement + " not recognized");
		} catch (Exception e) {

		}
	}

	public void testSyntaxError3() throws Exception {
		View theView = new View();
		String theStatement = "from select name1";

		try {
			SQLUtils.updateViewAttributesFromSQL(theView, theStatement);
			fail("Syntax error in " + theStatement + " not recognized");
		} catch (Exception e) {

		}
	}

	public void testSyntaxError4() throws Exception {
		View theView = new View();
		String theStatement = "from name1";

		try {
			SQLUtils.updateViewAttributesFromSQL(theView, theStatement);
			fail("Syntax error in " + theStatement + " not recognized");
		} catch (Exception e) {

		}
	}

	public void testSyntaxError5() throws Exception {
		View theView = new View();
		String theStatement = "select from table1";

		try {
			SQLUtils.updateViewAttributesFromSQL(theView, theStatement);
			fail("Syntax error in " + theStatement + " not recognized");
		} catch (Exception e) {

		}
	}

	public void testComplexQuery1() throws Exception {
		View theView = new View();
		String theStatement = readResourceFile("query1.sql");

		SQLUtils.updateViewAttributesFromSQL(theView, theStatement);

		assertTrue(theView.getAttributes().size() == 35);
		assertTrue("\"FirstName\"".equals(theView.getAttributes().get(0).getName()));
		assertTrue("\"UnitKey\"".equals(theView.getAttributes().get(2).getName()));
	}

	public void testComplexQuery2() throws Exception {
		View theView = new View();
		String theStatement = readResourceFile("query2.sql");

		System.out.println(theStatement);

		SQLUtils.updateViewAttributesFromSQL(theView, theStatement);

		assertTrue(theView.getAttributes().size() == 39);
		assertTrue("\"UnitKey\"".equals(theView.getAttributes().get(0).getName()));
		assertTrue("\"Echinococcosis/hydatidosis\"".equals(theView.getAttributes().get(1).getName()));
	}

}

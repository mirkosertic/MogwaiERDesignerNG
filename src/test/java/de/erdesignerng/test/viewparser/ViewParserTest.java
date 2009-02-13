package de.erdesignerng.test.viewparser;

import java.io.IOException;

import junit.framework.TestCase;

import nickyb.sqleonardo.querybuilder.QueryModel;
import nickyb.sqleonardo.querybuilder.syntax.SQLParser;

public class ViewParserTest extends TestCase {

    public void testSQLParser() throws IOException {
        QueryModel theModel = SQLParser.toQueryModel("SELECT A6AT008.LA0010A AS A6AT008_LA0010A, "
                + " A6AT009.LA0018A AS A6AT009_LA0018A, A6AT009.LA0107A AS A6AT009_LA0107A  FROM "
                + " A6AT008 A6AT008, A6AT009 A6AT009 ");
        assertTrue(theModel.getQueryExpression().getQuerySpecification().getSelectList().length == 3);
        
        QueryModel theModel1 = SQLParser.toQueryModel(theModel.toString());
        assertTrue(theModel1.getQueryExpression().getQuerySpecification().getSelectList().length == 3);
        
        QueryModel theModel2 = SQLParser.toQueryModel(theModel.toString(true));
        assertTrue(theModel2.getQueryExpression().getQuerySpecification().getSelectList().length == 3);
        
        QueryModel theModel3 = SQLParser.toQueryModel("SELECT\nA6AT008.\"LA0010A\"  AS  A6AT008_LA0010A, \nA6AT009.\"LA0018A\"  AS  A6AT009_LA0018A,\nA6AT009.\"LA0107A\"  AS  A6AT009_LA0107A\nFROM\n      A6AT008,\n      A6AT009");
        assertTrue(theModel3.getQueryExpression().getQuerySpecification().getSelectList().length == 3);
        
    }
}
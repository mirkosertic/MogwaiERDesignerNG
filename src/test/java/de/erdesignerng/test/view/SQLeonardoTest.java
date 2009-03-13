package de.erdesignerng.test.view;

import java.io.IOException;

import junit.framework.TestCase;
import nickyb.sqleonardo.querybuilder.QueryModel;
import nickyb.sqleonardo.querybuilder.syntax.QueryTokens;
import nickyb.sqleonardo.querybuilder.syntax.SQLParser;
import nickyb.sqleonardo.querybuilder.syntax.QueryTokens.Column;
import nickyb.sqleonardo.querybuilder.syntax.QueryTokens._Expression;

public class SQLeonardoTest extends TestCase {
    
    public void testParseSimpleSQL() throws IOException {
        String theSQL = "SELECT T_ATTACHMENT.ID AS FIELD1,T_ATTACHMENT.CREATIONDATE AS FIELD2,T_ATTACHMENT.CREATIONUSERID AS FIELD3,T_ATTACHMENT.LASTMODIFICATIONUSERID AS FIELD4 FROM T_ATTACHMENT T_ATTACHMENT";
        QueryModel theModel = SQLParser.toQueryModel(theSQL);
        _Expression[] theExpressions = theModel.getQueryExpression().getQuerySpecification().getSelectList();
        assertTrue(theExpressions.length == 4);
        QueryTokens.Column theColumn = (Column) theExpressions[0];
        assertTrue("FIELD1".equals(theColumn.getAlias()));
        theColumn = (Column) theExpressions[1];
        assertTrue("FIELD2".equals(theColumn.getAlias()));
        theColumn = (Column) theExpressions[2];
        assertTrue("FIELD3".equals(theColumn.getAlias()));
        theColumn = (Column) theExpressions[3];
        assertTrue("FIELD4".equals(theColumn.getAlias()));
    }

    public void testParseAssembleAndParseSimpleSQL() throws IOException {
        String theSQL = "SELECT T_ATTACHMENT.ID AS FIELD1,T_ATTACHMENT.CREATIONDATE AS FIELD2,T_ATTACHMENT.CREATIONUSERID AS FIELD3,T_ATTACHMENT.LASTMODIFICATIONUSERID AS FIELD4 FROM T_ATTACHMENT T_ATTACHMENT";
        QueryModel theModel = SQLParser.toQueryModel(theSQL);
        theSQL = theModel.toString(true);
        theModel = SQLParser.toQueryModel(theSQL);
        _Expression[] theExpressions = theModel.getQueryExpression().getQuerySpecification().getSelectList();
        assertTrue(theExpressions.length == 4);
        QueryTokens.Column theColumn = (Column) theExpressions[0];
        assertTrue("FIELD1".equals(theColumn.getAlias()));
        theColumn = (Column) theExpressions[1];
        assertTrue("FIELD2".equals(theColumn.getAlias()));
        theColumn = (Column) theExpressions[2];
        assertTrue("FIELD3".equals(theColumn.getAlias()));
        theColumn = (Column) theExpressions[3];
        assertTrue("FIELD4".equals(theColumn.getAlias()));
    }    
}

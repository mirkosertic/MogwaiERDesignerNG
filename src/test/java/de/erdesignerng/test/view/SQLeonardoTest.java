package de.erdesignerng.test.view;

import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.erdesignerng.model.Model;
import de.erdesignerng.model.ModelIOUtilities;
import de.erdesignerng.plugins.sqleonardo.ERConnection;

import junit.framework.TestCase;
import nickyb.sqleonardo.querybuilder.QueryBuilder;
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
    
    public void testWithQueryBuilder1() throws SAXException, IOException, ParserConfigurationException {
        
        String theQuery = "SELECT TABLE1.TB1_AT1 AS TABLE1_TB1_AT1, TABLE2.TB2_AT2 AS TABLE2_TB2_AT2, TABLE1.TB1_AT3 AS TABLE1_TB1_AT3 FROM TABLE1";
        QueryModel theQueryModel = SQLParser.toQueryModel(theQuery);
        System.out.println(theQueryModel.toString(false));
        
        Model theModel = ModelIOUtilities.getInstance().deserializeModelFromXML(getClass().getResourceAsStream("examplemodel.mxm"));
        QueryBuilder theBuilder = new QueryBuilder();
        QueryBuilder.autoJoin=false;
        theBuilder.setConnection(new ERConnection(theModel));
        theBuilder.setQueryModel(theQueryModel);
        
        JDialog theBuilderWindow = new JDialog();
        theBuilderWindow.setContentPane(theBuilder);
        
        String theNewSQL = theBuilder.getQueryModel().toString(false);
        System.out.println(theNewSQL);
    }
}

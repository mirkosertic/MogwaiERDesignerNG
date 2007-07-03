package de.mogwai.erdesignerng.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import junit.framework.TestCase;
import de.mogwai.erdesignerng.exception.ReverseEngineeringException;
import de.mogwai.erdesignerng.io.ModelIOUtilities;
import de.mogwai.erdesignerng.model.Model;
import de.mogwai.erdesignerng.reverseengineering.JDBCReverseEngineeringStrategy;
import de.mogwai.erdesignerng.util.dialect.mysql.MySQLDialect;

public class ReverseEngineeringTest extends TestCase {

	public ReverseEngineeringTest() throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, SQLException {

	}

	public void testReverseEngineeringAMIS() throws SQLException,
			TransformerException, ParserConfigurationException, IOException,
			InstantiationException, IllegalAccessException,
			ClassNotFoundException, ReverseEngineeringException {

		Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
		Connection theConnection = DriverManager.getConnection(
				"jdbc:oracle:thin:@sassalta.ad.bedag.ch:1521:a6xt", "amis",
				"amis");

		MySQLDialect theDialect = new MySQLDialect();

		JDBCReverseEngineeringStrategy theRe = theDialect
				.getReverseEngineeringStrategy();

		Model theModel = theRe.createModelFromConnection(theConnection, "AMIS");

		File theTempFile = new File("c:\\temp\\amis.xml");

		FileOutputStream theStream = new FileOutputStream(theTempFile);
		ModelIOUtilities.getInstance().serializeModelToXML(theModel, theStream);
		theStream.close();

	}

	public void testReverseEngineeringLOSPO() throws SQLException,
			TransformerException, ParserConfigurationException, IOException,
			InstantiationException, IllegalAccessException,
			ClassNotFoundException, ReverseEngineeringException {

		Class.forName("com.microsoft.jdbc.sqlserver.SQLServerDriver");
		Connection theConnection = DriverManager
				.getConnection(
						"jdbc:microsoft:sqlserver://geltenhorn:1433;DatabaseName=lospo_et",
						"lospo", "lospo!");

		MySQLDialect theDialect = new MySQLDialect();
		JDBCReverseEngineeringStrategy theRe = theDialect
				.getReverseEngineeringStrategy();

		Model theModel = theRe
				.createModelFromConnection(theConnection, "lospo");

		File theTempFile = new File("c:\\temp\\lospo.xml");

		FileOutputStream theStream = new FileOutputStream(theTempFile);
		ModelIOUtilities.getInstance().serializeModelToXML(theModel, theStream);
		theStream.close();

	}

	public void testReverseEngineeringMVP() throws SQLException,
			TransformerException, ParserConfigurationException, IOException,
			InstantiationException, IllegalAccessException,
			ClassNotFoundException, ReverseEngineeringException {

		Class.forName("com.mysql.jdbc.Driver");
		Connection theConnection = DriverManager.getConnection(
				"jdbc:mysql://geltenhorn:3306/mvp_et", "k27", "k27");

		MySQLDialect theDialect = new MySQLDialect();
		JDBCReverseEngineeringStrategy theRe = theDialect
				.getReverseEngineeringStrategy();

		Model theModel = theRe.createModelFromConnection(theConnection, null);

		File theTempFile = new File("c:\\temp\\mvp.xml");

		FileOutputStream theStream = new FileOutputStream(theTempFile);
		ModelIOUtilities.getInstance().serializeModelToXML(theModel, theStream);
		theStream.close();

	}

}

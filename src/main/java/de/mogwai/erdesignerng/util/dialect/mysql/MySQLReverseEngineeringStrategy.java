package de.mogwai.erdesignerng.util.dialect.mysql;

import java.sql.Connection;
import java.sql.SQLException;

import de.mogwai.erdesignerng.exception.ReverseEngineeringException;
import de.mogwai.erdesignerng.model.Model;
import de.mogwai.erdesignerng.reverseengineering.JDBCReverseEngineeringStrategy;
import de.mogwai.erdesignerng.util.dialect.Dialect;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2007-07-08 10:06:47 $
 */
public class MySQLReverseEngineeringStrategy extends
		JDBCReverseEngineeringStrategy {

	public MySQLReverseEngineeringStrategy(Dialect aDialect) {
		super(aDialect);
	}

	/**
	 * Reverse engineer the existing schemas.
	 * 
	 * @param aModel
	 * @param aConnection
	 * @throws SQLException
	 * @throws ReverseEngineeringException
	 */
	protected void reverseEngineerSchemas(Model aModel, String aSchemaName,
			Connection aConnection) throws SQLException,
			ReverseEngineeringException {

		reverseEnginnerTables(aModel, null, aConnection);
	}
}

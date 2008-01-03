package de.erdesignerng.dialect.mysql;

import java.sql.Connection;
import java.sql.SQLException;

import de.erdesignerng.dialect.Dialect;
import de.erdesignerng.dialect.JDBCReverseEngineeringStrategy;
import de.erdesignerng.exception.ReverseEngineeringException;
import de.erdesignerng.model.Model;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-01-03 13:11:29 $
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
	@Override
	protected void reverseEngineerSchemas(Model aModel, String aSchemaName,
			Connection aConnection) throws SQLException,
			ReverseEngineeringException {

		reverseEnginnerTables(aModel, null, aConnection);
	}
}

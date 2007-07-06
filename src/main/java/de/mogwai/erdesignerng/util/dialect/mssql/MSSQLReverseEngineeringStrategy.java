package de.mogwai.erdesignerng.util.dialect.mssql;

import de.mogwai.erdesignerng.reverseengineering.JDBCReverseEngineeringStrategy;
import de.mogwai.erdesignerng.util.dialect.Dialect;

/**
 * Oracle reverse engineering.
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2007-07-06 20:35:18 $
 */
public class MSSQLReverseEngineeringStrategy extends
		JDBCReverseEngineeringStrategy {

	public MSSQLReverseEngineeringStrategy(Dialect aDialect) {
		super(aDialect);
	}
}

package de.mogwai.erdesignerng.dialect.mssql;

import de.mogwai.erdesignerng.dialect.Dialect;
import de.mogwai.erdesignerng.dialect.JDBCReverseEngineeringStrategy;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2007-07-08 10:29:40 $
 */
public class MSSQLReverseEngineeringStrategy extends
		JDBCReverseEngineeringStrategy {

	public MSSQLReverseEngineeringStrategy(Dialect aDialect) {
		super(aDialect);
	}
}

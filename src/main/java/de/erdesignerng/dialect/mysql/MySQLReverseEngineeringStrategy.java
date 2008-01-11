package de.erdesignerng.dialect.mysql;

import de.erdesignerng.dialect.Dialect;
import de.erdesignerng.dialect.JDBCReverseEngineeringStrategy;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-01-11 18:40:39 $
 */
public class MySQLReverseEngineeringStrategy extends
		JDBCReverseEngineeringStrategy {

	public MySQLReverseEngineeringStrategy(Dialect aDialect) {
		super(aDialect);
	}
}

package de.mogwai.erdesignerng.dialect.postgres;

import de.mogwai.erdesignerng.dialect.Dialect;
import de.mogwai.erdesignerng.dialect.JDBCReverseEngineeringStrategy;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2007-07-28 11:08:10 $
 */
public class PostgresReverseEngineeringStrategy extends
		JDBCReverseEngineeringStrategy {

	public PostgresReverseEngineeringStrategy(Dialect aDialect) {
		super(aDialect);
	}
}

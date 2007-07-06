package de.mogwai.erdesignerng.util.dialect.oracle;

import de.mogwai.erdesignerng.reverseengineering.JDBCReverseEngineeringStrategy;
import de.mogwai.erdesignerng.util.dialect.Dialect;

/**
 * Oracle reverse engineering.
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2007-07-06 20:35:28 $
 */
public class OracleReverseEngineeringStrategy extends
		JDBCReverseEngineeringStrategy {

	public OracleReverseEngineeringStrategy(Dialect aDialect) {
		super(aDialect);
	}
}

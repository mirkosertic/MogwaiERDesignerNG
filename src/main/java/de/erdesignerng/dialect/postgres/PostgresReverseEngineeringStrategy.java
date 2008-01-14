package de.erdesignerng.dialect.postgres;

import de.erdesignerng.dialect.Dialect;
import de.erdesignerng.dialect.JDBCReverseEngineeringStrategy;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-01-14 20:01:14 $
 */
public class PostgresReverseEngineeringStrategy extends JDBCReverseEngineeringStrategy {

    public PostgresReverseEngineeringStrategy(Dialect aDialect) {
        super(aDialect);
    }
}

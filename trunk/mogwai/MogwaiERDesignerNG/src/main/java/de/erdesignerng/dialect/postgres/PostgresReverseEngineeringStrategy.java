package de.erdesignerng.dialect.postgres;

import de.erdesignerng.dialect.Dialect;
import de.erdesignerng.dialect.JDBCReverseEngineeringStrategy;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-01-15 19:22:45 $
 */
public class PostgresReverseEngineeringStrategy extends JDBCReverseEngineeringStrategy {

    public PostgresReverseEngineeringStrategy(Dialect aDialect) {
        super(aDialect);
    }
}

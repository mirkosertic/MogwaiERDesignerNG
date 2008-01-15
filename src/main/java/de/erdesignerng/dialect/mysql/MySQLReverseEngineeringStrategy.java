package de.erdesignerng.dialect.mysql;

import de.erdesignerng.dialect.Dialect;
import de.erdesignerng.dialect.JDBCReverseEngineeringStrategy;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-01-15 19:22:45 $
 */
public class MySQLReverseEngineeringStrategy extends JDBCReverseEngineeringStrategy {

    public MySQLReverseEngineeringStrategy(Dialect aDialect) {
        super(aDialect);
    }
}

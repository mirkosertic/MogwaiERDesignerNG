package de.erdesignerng.dialect.mysql;

import de.erdesignerng.dialect.Dialect;
import de.erdesignerng.dialect.JDBCReverseEngineeringStrategy;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-01-14 20:01:15 $
 */
public class MySQLReverseEngineeringStrategy extends JDBCReverseEngineeringStrategy {

    public MySQLReverseEngineeringStrategy(Dialect aDialect) {
        super(aDialect);
    }
}

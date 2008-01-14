package de.erdesignerng.dialect.mssql;

import de.erdesignerng.dialect.Dialect;
import de.erdesignerng.dialect.JDBCReverseEngineeringStrategy;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-01-14 20:01:06 $
 */
public class MSSQLReverseEngineeringStrategy extends JDBCReverseEngineeringStrategy {

    public MSSQLReverseEngineeringStrategy(Dialect aDialect) {
        super(aDialect);
    }
}

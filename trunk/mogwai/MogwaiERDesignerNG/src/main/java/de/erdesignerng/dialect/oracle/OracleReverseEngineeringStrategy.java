package de.erdesignerng.dialect.oracle;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import de.erdesignerng.dialect.Dialect;
import de.erdesignerng.dialect.JDBCReverseEngineeringStrategy;
import de.erdesignerng.dialect.SchemaEntry;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-01-15 19:22:45 $
 */
public class OracleReverseEngineeringStrategy extends JDBCReverseEngineeringStrategy {

    public OracleReverseEngineeringStrategy(Dialect aDialect) {
        super(aDialect);
    }

    @Override
    public List<SchemaEntry> getSchemaEntries(Connection aConnection) throws SQLException {

        List<SchemaEntry> theList = new ArrayList<SchemaEntry>();

        DatabaseMetaData theMetadata = aConnection.getMetaData();
        ResultSet theResult = theMetadata.getSchemas();

        while (theResult.next()) {
            String theSchemaName = theResult.getString("TABLE_SCHEM");
            String theCatalogName = null;

            theList.add(new SchemaEntry(theCatalogName, theSchemaName));
        }

        return theList;
    }
}

/**
 * Mogwai ERDesigner. Copyright (C) 2002 The Mogwai Project.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 */
package de.erdesignerng.dialect.h2;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import de.erdesignerng.dialect.JDBCReverseEngineeringStrategy;
import de.erdesignerng.dialect.SchemaEntry;
import de.erdesignerng.dialect.TableEntry;
import de.erdesignerng.exception.ReverseEngineeringException;
import de.erdesignerng.model.View;

/**
 * @author $Author: gniddelgesicht $
 * @version $Date: 2008/06/13 16:49:00 $
 */
public class H2ReverseEngineeringStrategy extends JDBCReverseEngineeringStrategy<H2Dialect> {

    public H2ReverseEngineeringStrategy(H2Dialect aDialect) {
        super(aDialect);
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    protected String reverseEngineerViewSQL(TableEntry aViewEntry, Connection aConnection, View aView)
            throws SQLException, ReverseEngineeringException {
        PreparedStatement theStatement = aConnection
                .prepareStatement("SELECT * FROM INFORMATION_SCHEMA.SYSTEM_VIEWS WHERE TABLE_NAME = ? AND TABLE_SCHEMA = ?");
        theStatement.setString(1, aViewEntry.getTableName());
        theStatement.setString(2, aViewEntry.getSchemaName());
        ResultSet theResult = null;
        try {
            theResult = theStatement.executeQuery();
            while (theResult.next()) {
                String theViewDefinition = theResult.getString("VIEW_DEFINITION");
                String theViewDefinitionLower = theViewDefinition.toLowerCase();
                if (theViewDefinitionLower.startsWith("create view ")) {
                    int p = theViewDefinitionLower.indexOf(" as ");
                    if (p >= 0) {
                        theViewDefinition = theViewDefinition.substring(p + 4);
                    }
                }
                return theViewDefinition;
            }
            return null;
        } finally {
            if (theResult != null) {
                theResult.close();
            }
            theStatement.close();
        }
    }
}

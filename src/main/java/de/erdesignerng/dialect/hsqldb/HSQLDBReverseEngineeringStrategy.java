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
package de.erdesignerng.dialect.hsqldb;

import de.erdesignerng.dialect.JDBCReverseEngineeringStrategy;
import de.erdesignerng.dialect.SchemaEntry;
import de.erdesignerng.dialect.TableEntry;
import de.erdesignerng.model.View;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author $Author: gniddelgesicht $
 * @version $Date: 2008/06/13 16:49:00 $
 */
public class HSQLDBReverseEngineeringStrategy extends JDBCReverseEngineeringStrategy<HSQLDBDialect> {

    public HSQLDBReverseEngineeringStrategy(final HSQLDBDialect aDialect) {
        super(aDialect);
    }

    @Override
    public List<SchemaEntry> getSchemaEntries(final Connection aConnection) throws SQLException {

        final List<SchemaEntry> theList = new ArrayList<>();

        final DatabaseMetaData theMetadata = aConnection.getMetaData();
        final ResultSet theResult = theMetadata.getSchemas();

        while (theResult.next()) {
            final String theSchemaName = theResult.getString("TABLE_SCHEM");
            final String theCatalogName = null;

            theList.add(new SchemaEntry(theCatalogName, theSchemaName));
        }

        return theList;
    }

    @Override
    protected String reverseEngineerViewSQL(final TableEntry aViewEntry, final Connection aConnection, final View aView)
            throws SQLException {
        PreparedStatement theStatement = null;
        ResultSet theResult = null;
        try {
            theStatement = aConnection
                    .prepareStatement("SELECT * FROM INFORMATION_SCHEMA.VIEWS WHERE TABLE_NAME = ? AND TABLE_SCHEMA = ?");
            theStatement.setString(1, aViewEntry.getTableName());
            theStatement.setString(2, aViewEntry.getSchemaName());

            theResult = theStatement.executeQuery();
            if (theResult.next()) {
                String theViewDefinition = theResult.getString("VIEW_DEFINITION");
                theViewDefinition = extractSelectDDLFromViewDefinition(theViewDefinition);
                return theViewDefinition;
            }
            return null;
        } finally {
            if (theStatement != null) {
                try {
                    theStatement.close();
                } catch (final Exception e) {
                    // ignore this
                }
            }
            if (theResult != null) {
                try {
                    theResult.close();
                } catch (final Exception e) {
                    // Ignore this
                }
            }
        }
    }
}

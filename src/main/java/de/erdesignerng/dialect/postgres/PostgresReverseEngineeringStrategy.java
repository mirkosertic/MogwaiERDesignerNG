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
package de.erdesignerng.dialect.postgres;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import de.erdesignerng.dialect.JDBCReverseEngineeringStrategy;
import de.erdesignerng.dialect.ReverseEngineeringNotifier;
import de.erdesignerng.dialect.ReverseEngineeringOptions;
import de.erdesignerng.dialect.SchemaEntry;
import de.erdesignerng.dialect.TableEntry;
import de.erdesignerng.exception.ReverseEngineeringException;
import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.Domain;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.View;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-09 19:07:30 $
 */
public class PostgresReverseEngineeringStrategy extends JDBCReverseEngineeringStrategy<PostgresDialect> {

    public PostgresReverseEngineeringStrategy(PostgresDialect aDialect) {
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

    // Bug Fixing 2876916 [ERDesignerNG] Reverse-Eng. PgSQL VARCHAR max-length
    // wrong
    @Override
    protected void reverseEngineerAttribute(Model aModel, Attribute aAttribute, ReverseEngineeringOptions aOptions,
            ReverseEngineeringNotifier aNotifier, TableEntry aTableEntry, Connection aConnection) throws SQLException {

        if ((aAttribute.getDatatype().getName().equalsIgnoreCase("varchar"))
                || (aAttribute.getDatatype().getName().equalsIgnoreCase("character varying"))) {
            // PostgreSQL liefert Integer.MAX_VALUE (2147483647), wenn VARCHAR
            // ohne Parameter definiert wurde, obwohl 1073741823 korrekt
            // wäre
            if (new Integer(Integer.MAX_VALUE).equals(aAttribute.getSize())) {
                aAttribute.setSize(null);
            }
        }
    }

    // Bug Fixing 2895202 [ERDesignerNG] RevEng PostgreSQL domains shows
    // VARCHAR(0)
    @Override
    protected void reverseEngineerDomain(Model aModel, Domain aDomain, ReverseEngineeringOptions aOptions,
            ReverseEngineeringNotifier aNotifier, Connection aConnection) throws SQLException {

        if ((aDomain.getConcreteType().getName().equalsIgnoreCase("varchar"))
                || (aDomain.getConcreteType().getName().equalsIgnoreCase("character varying"))) {
            // PostgreSQL liefert 0, wenn VARCHAR ohne Parameter definiert wurde
            if (new Integer(0).equals(aDomain.getSize())) {
                aDomain.setSize(null);
            }
        }
    }

    @Override
    protected String reverseEngineerViewSQL(TableEntry aViewEntry, Connection aConnection, View aView)
            throws SQLException, ReverseEngineeringException {
        PreparedStatement theStatement = aConnection
                .prepareStatement("SELECT * FROM information_schema.views WHERE table_name = ?");
        theStatement.setString(1, aViewEntry.getTableName());
        ResultSet theResult = null;
        try {
            theResult = theStatement.executeQuery();
            while (theResult.next()) {
                String theViewDefinition = theResult.getString("view_definition");
                theViewDefinition = extractSelectDDLFromViewDefinition(theViewDefinition);
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

    @Override
    // TODO [mirkosertic] Add units tests to test this strange behavior
    protected String getEscapedPattern(DatabaseMetaData aMetaData, String aValue) throws SQLException {
        return aValue;
    }
    
}
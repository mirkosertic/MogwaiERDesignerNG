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
package de.erdesignerng.dialect.mssql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.erdesignerng.dialect.JDBCReverseEngineeringStrategy;
import de.erdesignerng.dialect.TableEntry;
import de.erdesignerng.exception.ReverseEngineeringException;
import de.erdesignerng.model.CascadeType;
import de.erdesignerng.model.View;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-09 19:07:29 $
 */
public class MSSQLReverseEngineeringStrategy extends JDBCReverseEngineeringStrategy<MSSQLDialect> {

    public MSSQLReverseEngineeringStrategy(MSSQLDialect aDialect) {
        super(aDialect);
    }

    @Override
    protected boolean isTableTypeView(String aTableType) {
        return VIEW_TABLE_TYPE.equals(aTableType);
    }

    @Override
    protected String reverseEngineerViewSQL(TableEntry aViewEntry, Connection aConnection, View aView)
            throws SQLException, ReverseEngineeringException {
        PreparedStatement theStatement = aConnection
                .prepareStatement("SELECT * FROM INFORMATION_SCHEMA.VIEWS WHERE TABLE_NAME = ?");
        theStatement.setString(1, aViewEntry.getTableName());
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

    @Override
    protected CascadeType getCascadeType(int aValue) {
        switch (aValue) {
        case DatabaseMetaData.importedKeyRestrict:
            // Restrict is not supported my this db
            aValue = DatabaseMetaData.importedKeyNoAction;
            break;
        }
        return super.getCascadeType(aValue);
    }
}
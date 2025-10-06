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

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.dialect.JDBCReverseEngineeringStrategy;
import de.erdesignerng.dialect.ReverseEngineeringNotifier;
import de.erdesignerng.dialect.ReverseEngineeringOptions;
import de.erdesignerng.dialect.TableEntry;
import de.erdesignerng.exception.ReverseEngineeringException;
import de.erdesignerng.model.CascadeType;
import de.erdesignerng.model.CustomType;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.View;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-09 19:07:29 $
 */
public class MSSQLReverseEngineeringStrategy extends
        JDBCReverseEngineeringStrategy<MSSQLDialect> {

    public MSSQLReverseEngineeringStrategy(final MSSQLDialect aDialect) {
        super(aDialect);
    }

    @Override
    protected String reverseEngineerViewSQL(final TableEntry aViewEntry,
                                            final Connection aConnection, final View aView) throws SQLException {
        final PreparedStatement theStatement = aConnection
                .prepareStatement("SELECT * FROM INFORMATION_SCHEMA.VIEWS WHERE TABLE_NAME = ?");
        theStatement.setString(1, aViewEntry.getTableName());
        ResultSet theResult = null;
        try {
            theResult = theStatement.executeQuery();
            if (theResult.next()) {
                String theViewDefinition = theResult
                        .getString("VIEW_DEFINITION");
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
    protected CascadeType getCascadeType(int aValue) {
        switch (aValue) {
            case DatabaseMetaData.importedKeyRestrict:
                // Restrict is not supported my this db
                aValue = DatabaseMetaData.importedKeyNoAction;
                break;
        }
        return super.getCascadeType(aValue);
    }

    @Override
    protected void reverseEngineerCustomType(final Model aModel,
                                             final CustomType aCustomType, final ReverseEngineeringOptions aOptions,
                                             final ReverseEngineeringNotifier aNotifier, final Connection aConnection) {
        // TODO Auto-generated method stub
    }

    @Override
    protected void reverseEngineerCustomTypes(final Model aModel,
                                              final ReverseEngineeringOptions aOptions,
                                              final ReverseEngineeringNotifier aNotifier, final Connection aConnection)
            throws SQLException, ReverseEngineeringException {
        final String theQuery = "select b.principal_id,b.name,a.* from sys.types a,sys.schemas b where a.schema_id = b.schema_id and a.is_table_type = 0 and a.is_user_defined = 1";
        try (final PreparedStatement theStatement = aConnection.prepareStatement(theQuery); final ResultSet theResult = theStatement.executeQuery()) {
            while (theResult.next()) {
                final String theCustomTypeName = theResult.getString("name");

                aNotifier.notifyMessage(ERDesignerBundle.ENGINEERINGCUSTOMTYPE,
                        theCustomTypeName);

                CustomType theCustomType = aModel.getCustomTypes()
                        .findByNameAndSchema(theCustomTypeName, null);
                if (theCustomType != null) {
                    throw new ReverseEngineeringException(
                            "Duplicate custom datatype found : "
                                    + theCustomTypeName);
                }

                theCustomType = new CustomType();
                theCustomType.setName(theCustomTypeName);

                aModel.addCustomType(theCustomType);

                reverseEngineerCustomType(aModel, theCustomType, aOptions,
                        aNotifier, aConnection);
            }
        }
    }
}
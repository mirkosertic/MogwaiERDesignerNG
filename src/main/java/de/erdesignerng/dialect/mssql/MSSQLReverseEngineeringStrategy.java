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
import de.erdesignerng.modificationtracker.VetoException;

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

    public MSSQLReverseEngineeringStrategy(MSSQLDialect aDialect) {
        super(aDialect);
    }

    @Override
    protected String reverseEngineerViewSQL(TableEntry aViewEntry,
                                            Connection aConnection, View aView) throws SQLException {
        PreparedStatement theStatement = aConnection
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
    protected void reverseEngineerCustomType(Model aModel,
                                             CustomType aCustomType, ReverseEngineeringOptions aOptions,
                                             ReverseEngineeringNotifier aNotifier, Connection aConnection) {
        // TODO Auto-generated method stub
    }

    @Override
    protected void reverseEngineerCustomTypes(Model aModel,
                                              ReverseEngineeringOptions aOptions,
                                              ReverseEngineeringNotifier aNotifier, Connection aConnection)
            throws SQLException, ReverseEngineeringException {
        String theQuery = "select b.principal_id,b.name,a.* from sys.types a,sys.schemas b where a.schema_id = b.schema_id and a.is_table_type = 0 and a.is_user_defined = 1";
        ResultSet theResult = null;
        try (PreparedStatement theStatement = aConnection.prepareStatement(theQuery)) {
            theResult = theStatement.executeQuery();
            while (theResult.next()) {
                String theCustomTypeName = theResult.getString("name");

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

                try {
                    aModel.addCustomType(theCustomType);
                } catch (VetoException e) {
                    throw new ReverseEngineeringException(e.getMessage(), e);
                }

                reverseEngineerCustomType(aModel, theCustomType, aOptions,
                        aNotifier, aConnection);
            }
        } finally {
            if (theResult != null) {
                theResult.close();
            }

        }
    }
}
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
package de.erdesignerng.dialect;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;

import de.erdesignerng.exception.ReverseEngineeringException;
import de.erdesignerng.model.CascadeType;
import de.erdesignerng.model.Model;
import de.erdesignerng.visual.common.ERDesignerWorldConnector;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-03-02 16:11:21 $
 * @param <T>
 *            the dialect
 */
public abstract class ReverseEngineeringStrategy<T extends Dialect> {

    protected T dialect;

    protected ReverseEngineeringStrategy(T aDialect) {
        dialect = aDialect;
    }

    public abstract void updateModelFromConnection(Model aModel, ERDesignerWorldConnector aConnector,
            Connection aConnection, ReverseEngineeringOptions aOptions, ReverseEngineeringNotifier aNotifier)
            throws SQLException, ReverseEngineeringException;

    public abstract List<SchemaEntry> getSchemaEntries(Connection aConnection) throws SQLException;

    /**
     * Convert a JDBC Cascade Type to the Mogwai CascadeType.
     * 
     * Default is CASCADE.
     * 
     * @param aValue
     *            the JDBC type
     * @return the CascadeType
     */
    protected CascadeType getCascadeType(int aValue) {
        switch (aValue) {
        case DatabaseMetaData.importedKeyNoAction:
            return CascadeType.NOTHING;
        case DatabaseMetaData.importedKeySetNull:
            return CascadeType.SET_NULL;
        case DatabaseMetaData.importedKeyCascade:
            return CascadeType.CASCADE;
        case DatabaseMetaData.importedKeyRestrict:
            return CascadeType.RESTRICT;
        default:
            return CascadeType.CASCADE;
        }
    }

    protected String convertColumnTypeToRealType(String aTypeName) {
        return aTypeName;
    }

    public abstract List<TableEntry> getTablesForSchemas(Connection aConnection, List<SchemaEntry> aSchemaEntries)
            throws SQLException;
}
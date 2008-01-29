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
import de.erdesignerng.model.Domain;
import de.erdesignerng.model.Model;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-01-29 22:04:11 $
 * @param <T>
 *            the dialect
 */
public abstract class ReverseEngineeringStrategy<T extends Dialect> {

    protected T dialect;

    protected ReverseEngineeringStrategy(T aDialect) {
        dialect = aDialect;
    }

    public abstract Model createModelFromConnection(Connection aConnection, ReverseEngineeringOptions aOptions,
            ReverseEngineeringNotifier aNotifier) throws SQLException, ReverseEngineeringException;

    public abstract List<SchemaEntry> getSchemaEntries(Connection aConnection) throws SQLException;

    protected CascadeType getCascadeType(int aValue) {
        switch (aValue) {
        case DatabaseMetaData.importedKeyNoAction:
            return CascadeType.NOTHING;
        case DatabaseMetaData.importedKeySetNull:
            return CascadeType.SET_NULL;
        case DatabaseMetaData.importedKeyCascade:
            return CascadeType.CASCADE;
        default:
            return CascadeType.CASCADE;
        }
    }
    
    protected String convertColumnTypeToRealType(String aTypeName) {
        return aTypeName;
    }
    
    protected Domain createDomainFor(Model aModel, String aColumnName, String aTypeName, int aSize,
            int aFraction, int aRadix, ReverseEngineeringOptions aOptions) throws ReverseEngineeringException {

        DataType theDataType = dialect.getDataType(convertColumnTypeToRealType(aTypeName));
        if (theDataType == null) {
            throw new ReverseEngineeringException("Unknown data type " + aTypeName);
        }

        Domain theDomain = aModel.getDomains().findByDataType(theDataType.getName(), aSize, aFraction, aRadix);
        if (theDomain != null) {

            if (theDomain.getName().equals(aColumnName)) {
                return theDomain;
            }

            for (int i = 0; i < 10000; i++) {
                String theName = aColumnName;
                if (i > 0) {
                    theName = theName + "_" + i;
                }

                theDomain = aModel.getDomains().findByName(theName);
                if (theDomain != null) {
                    if (theDomain.getName().equals(aColumnName)) {
                        return theDomain;
                    }
                }

                if (!aModel.getDomains().elementExists(theName, dialect.isCaseSensitive())) {

                    theDomain = new Domain();
                    theDomain.setName(theName);
                    theDomain.setDatatype(theDataType);
                    theDomain.setDomainSize(aSize);
                    theDomain.setFraction(aFraction);
                    theDomain.setRadix(aRadix);

                    aModel.getDomains().add(theDomain);

                    return theDomain;
                }
            }

        } else {
            theDomain = new Domain();
            theDomain.setName(aColumnName);
            theDomain.setDatatype(theDataType);
            theDomain.setDomainSize(aSize);
            theDomain.setFraction(aFraction);
            theDomain.setRadix(aRadix);

            aModel.getDomains().add(theDomain);
        }

        return theDomain;
    }    
}
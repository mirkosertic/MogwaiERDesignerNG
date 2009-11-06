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
package de.erdesignerng.plugins.squirrel.dialect;

import java.sql.Connection;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.session.ISession;
import de.erdesignerng.dialect.DataType;
import de.erdesignerng.dialect.DataTypeList;
import de.erdesignerng.dialect.Dialect;
import de.erdesignerng.dialect.JDBCReverseEngineeringStrategy;
import de.erdesignerng.dialect.NameCastType;
import de.erdesignerng.dialect.SQLGenerator;
import de.erdesignerng.exception.ElementInvalidNameException;

public class SquirrelDialect extends Dialect {

    private Dialect dialect;

    private ISession session;

    public SquirrelDialect(Dialect aDialect, ISession aSession) {
        dialect = aDialect;
        session = aSession;

        setGeneratesManagedConnection(true);
    }

    @Override
    public String checkName(String aName) throws ElementInvalidNameException {
        return dialect.checkName(aName);
    }

    @Override
    public Connection createConnection(ClassLoader aClassLoader, String aDriver, String aUrl, String aUser,
            String aPassword, boolean aPromptForPassword) throws ClassNotFoundException, InstantiationException,
            IllegalAccessException, SQLException {
        return session.getSQLConnection().getConnection();
    }

    @Override
    public SQLGenerator createSQLGenerator() {
        return dialect.createSQLGenerator();
    }

    @Override
    public NameCastType getCastType() {
        return dialect.getCastType();
    }

    @Override
    public DataTypeList getDataTypes() {
        return dialect.getDataTypes();
    }

    @Override
    public String getDriverClassName() {
        return dialect.getDriverClassName();
    }

    @Override
    public String getDriverURLTemplate() {
        return dialect.getDriverURLTemplate();
    }

    @Override
    public int getMaxObjectNameLength() {
        return dialect.getMaxObjectNameLength();
    }

    @Override
    public JDBCReverseEngineeringStrategy<Dialect> getReverseEngineeringStrategy() {
        return dialect.getReverseEngineeringStrategy();
    }

    @Override
    public String getUniqueName() {
        return dialect.getUniqueName();
    }

    @Override
    public boolean isCaseSensitive() {
        return dialect.isCaseSensitive();
    }

    @Override
    public boolean isNullablePrimaryKeyAllowed() {
        return dialect.isNullablePrimaryKeyAllowed();
    }

    @Override
    public boolean isSpacesAllowedInObjectNames() {
        return dialect.isSpacesAllowedInObjectNames();
    }

    @Override
    public boolean isSupportsOnDelete() {
        return dialect.isSupportsOnDelete();
    }

    @Override
    public boolean isSupportsOnUpdate() {
        return dialect.isSupportsOnUpdate();
    }

    @Override
    public void setCaseSensitive(boolean aCaseSensitive) {
        dialect.setCaseSensitive(aCaseSensitive);
    }

    @Override
    public void setCastType(NameCastType aCastType) {
        dialect.setCastType(aCastType);
    }

    @Override
    public void setMaxObjectNameLength(int aMaxObjectNameLength) {
        dialect.setMaxObjectNameLength(aMaxObjectNameLength);
    }

    @Override
    public void setNullablePrimaryKeyAllowed(boolean aNullablePrimaryKeyAllowed) {
        dialect.setNullablePrimaryKeyAllowed(aNullablePrimaryKeyAllowed);
    }

    @Override
    public void setSpacesAllowedInObjectNames(boolean aSpacesAllowedInObjectNames) {
        dialect.setSpacesAllowedInObjectNames(aSpacesAllowedInObjectNames);
    }

    @Override
    public void setSupportsOnDelete(boolean supportsOnDelete) {
        dialect.setSupportsOnDelete(supportsOnDelete);
    }

    @Override
    public void setSupportsOnUpdate(boolean supportsOnUpdate) {
        dialect.setSupportsOnUpdate(supportsOnUpdate);
    }

    @Override
    public boolean isSupportsSchemaInformation() {
        return dialect.isSupportsSchemaInformation();
    }

    @Override
    public String toString() {
        return dialect.toString();
    }

    @Override
    public Class getHibernateDialectClass() {
        return dialect.getHibernateDialectClass();
    }

    @Override
    public DataType createDataType(String name, String definition, int... jdbcType) {
        return dialect.createDataType(name, definition, jdbcType);
    }

    @Override
    public DataType createDataType(String name, String definition, boolean identity, int... jdbcType) {
        return dialect.createDataType(name, definition, identity, jdbcType);
    }
}
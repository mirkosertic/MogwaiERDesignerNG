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
package de.erdesignerng.dialect.msaccess;

import java.io.File;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.erdesignerng.dialect.DataType;
import de.erdesignerng.dialect.JDBCReverseEngineeringStrategy;
import de.erdesignerng.dialect.NameCastType;
import de.erdesignerng.dialect.SQLGenerator;
import de.erdesignerng.dialect.sql92.SQL92Dialect;

/**
 * Works only on Windows-based Systems due to the need of the JET/ACE-Engine.
 * 
 * JET 3 for MSAccess 95 (MSOffice 7 JET 3.5 for MSAccess 97 (MSOffice 8) JET 4
 * for MSAccess 2000 to 2003 (MSOffice 9 to 11)
 * 
 * ACE for MSAccess 2007+ (MSOffice 12)
 * 
 * While the JET-Database-Engine comes with the Windows-OS, the new ACE-Engine
 * comes with the installation of MSOffice 12 or by download from:
 * http://www.microsoft
 * .com/downloads/details.aspx?displaylang=de&FamilyID=7554f536
 * -8c28-4598-9b72-ef94e038c891
 * 
 * @author $Author: dr-death $
 * @version $Date: 2009-11-06 01:30:00 $
 */
public class MSAccessDialect extends SQL92Dialect {

    @Override
    public Connection createConnection(ClassLoader aClassLoader, String aDriver, String aUrl, String aUser,
            String aPassword, boolean aPromptForPassword) throws ClassNotFoundException, InstantiationException,
            IllegalAccessException, SQLException {
        File workgroupFile = null;
        String systemDB = "";

        try {
            workgroupFile = new File(getClass().getResource("/de/erdesignerng/System.mdw").toURI());
            systemDB = "SystemDB=" + workgroupFile.getAbsoluteFile() + ";";
        } catch (URISyntaxException ex) {
            Logger.getLogger(MSAccessDialect.class.getName()).log(Level.SEVERE, null, ex);
        }

        String database = "jdbc:odbc:Driver={" + aDriver + "};DBQ=" + aUrl + ";ExtendedAnsiSQL=1" + ";" + systemDB;
        Connection connection = DriverManager.getConnection(database, aUser, aPassword);

        // Schreibt die Berechtigung zum Lesen der angegebenen Tabellen in die
        // im DSN-Parameter 'SystemDB' angegebene *.mdw Datei.
        // Um die Einstellungen der "echten" System.mdw in
        // %HOMEDRIVE%%HOMEPATH%\Anwendungsdaten\Microsoft\Access\System.mdw
        // nicht zu überschreiben wird eine "eigene" System.mdw benutzt.
        Statement statement = connection.createStatement();
        statement.execute("GRANT SELECT ON TABLE MSysObjects TO " + aUser);
        statement.execute("GRANT SELECT ON TABLE MSysRelationships TO " + aUser);
        statement.execute("GRANT SELECT ON TABLE MSysQueries TO " + aUser);

        return connection;
    }

    public MSAccessDialect() {
        setSpacesAllowedInObjectNames(true);
        setCaseSensitive(false);
        setMaxObjectNameLength(64);
        setNullablePrimaryKeyAllowed(false);
        setCastType(NameCastType.NOTHING);
        setSupportsDomains(false);
        setSupportsSchemaInformation(false);

        // @see http://msdn.microsoft.com/en-us/library/bb177899.aspx
        registerType(createDataType("integer", "", Types.INTEGER)); // LONG
        registerType(createDataType("varchar", "$size", Types.VARCHAR)); // TEXT
        registerType(createDataType("counter", "", true, Types.INTEGER)); // AUTOINCREMENT
        registerType(createDataType("datetime", "", Types.DATE)); // DATE
        registerType(createDataType("byte", "", Types.TINYINT)); // BYTE
        registerType(createDataType("bit", "", Types.BOOLEAN)); // YESNO
        registerType(createDataType("longchar", "", Types.LONGNVARCHAR)); // MEMO
        registerType(createDataType("smallint", "", Types.SMALLINT)); // INTEGER
        registerType(createDataType("double", "", Types.DOUBLE)); // DOUBLE
        registerType(createDataType("real", "", Types.REAL)); // SINGLE
        registerType(createDataType("currency", "", Types.BIGINT)); // CURRENCY
        registerType(createDataType("longbinary", "", Types.BLOB)); // OLE-Objekt
        registerType(createDataType("decimal", "", Types.DECIMAL)); // DECIMAL

        seal();

    }

    @Override
    public JDBCReverseEngineeringStrategy getReverseEngineeringStrategy() {
        return new MSAccessReverseEngineeringStrategy(this);
    }

    @Override
    public String getUniqueName() {
        return "MSAccessDialect (experimental)";
    }

    @Override
    public String getDefaultUserName() {
        return "Admin";
    }

    @Override
    public String getDriverClassName() {
        return "Microsoft Access Driver (*.mdb)";
    }

    @Override
    public String getDriverURLTemplate() {
        return "C:\\<dbname.mdb>";
    }

    @Override
    public SQLGenerator createSQLGenerator() {
        return new MSAccessSQLGenerator(this);
    }

    @Override
    public Class getHibernateDialectClass() {
        throw new UnsupportedOperationException("MSAccessDialect.getHibernateDialectClass() is not supported yet.");
    }

    @Override
    public DataType createDataType(String aName, String aDefinition, int... aJdbcType) {
        return new MSAccessDataType(aName, aDefinition, aJdbcType);
    }

    @Override
    public DataType createDataType(String aName, String aDefinition, boolean aIdentity, int... aJdbcType) {
        return new MSAccessDataType(aName, aDefinition, aIdentity, aJdbcType);
    }
}
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

import de.erdesignerng.dialect.DataType;
import de.erdesignerng.dialect.JDBCReverseEngineeringStrategy;
import de.erdesignerng.dialect.NameCastType;
import de.erdesignerng.dialect.SQLGenerator;
import de.erdesignerng.dialect.Dialect;

import java.io.File;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Locale;

/**
 * Works only on Windows-based Systems due to the need of the JET/ACE-Engine.
 * <p/>
 * Jet 1.0 - Access  1.0 - n. a.
 * Jet 1.1 - Access  1.1 - n. a.          - 1992
 * Jet 2.X - Access  2.0 - Office 4.3 Pro - 1993
 * Jet 3.0 - Access  7.0 - Office 95      - 1995
 * Jet 3.5 - Access  8.0 - Office 97      - 1997
 * Jet 4.0 - Access  9.0 - Office 2000    - 1999
 * Jet 4.0 - Access 10.0 - Office 2002    - 2001
 * Jet 4.0 - Access 11.5 - Office 2003    - 2003
 * ACE     - Access 12.0 - Office 2007    - 2007
 *
 * @author $Author: dr-death $
 * @version $Date: 2009-11-06 01:30:00 $
 * @see http://en.wikipedia.org/wiki/Microsoft_Access
 * @see http://en.wikipedia.org/wiki/Microsoft_Jet_Database_Engine
 *      <p/>
 *      While the JET-Database-Engine comes with the Windows-OS, the new ACE-Engine
 *      comes with the installation of MSOffice 12 or by download from:
 * @see http://www.microsoft.com/downloads/details.aspx?FamilyID=
 *      7554f536-8c28-4598-9b72-ef94e038c891
 */
public final class MSAccessDialect extends Dialect {

	private static final int ERROR_FILE_NOT_FOUND = -1811;

	private static final int ERROR_FILE_TOO_NEW = -1028;

	@Override
	public Connection createConnection(ClassLoader aClassLoader, String aDriver, String aUrl, String aUser, String aPassword, boolean aPromptForPassword) throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {

		File workgroupFile;
		String systemDB;

		try {
			workgroupFile = new File(getClass().getResource("/de/erdesignerng/System.mdw").toURI());
			systemDB = "SystemDB=" + workgroupFile.getAbsoluteFile() + ";";
		} catch (URISyntaxException ex) {
			throw new RuntimeException("File '" + aUrl + "' not found!");
		}

		String database = "jdbc:odbc:Driver={" + aDriver + "};DBQ=" + aUrl + ";ExtendedAnsiSQL=1" + ";" + systemDB;
		Connection connection;

		try {
			connection = DriverManager.getConnection(database, aUser, aPassword);

			// Schreibt die Berechtigung zum Lesen der angegebenen Tabellen in die
			// im DSN-Parameter 'SystemDB' angegebene *.mdw Datei.
			// Um die Einstellungen der "echten" System.mdw in
			// %APPDATA%\Microsoft\Access\System.mdw
			// nicht zu überschreiben wird eine "eigene" System.mdw benutzt.
			Statement statement = connection.createStatement();
			statement.execute("GRANT SELECT ON TABLE MSysObjects TO " + aUser);
			statement.execute("GRANT SELECT ON TABLE MSysRelationships TO " + aUser);
			statement.execute("GRANT SELECT ON TABLE MSysQueries TO " + aUser);

		} catch (SQLException e) {
			switch (e.getErrorCode()) {
				case ERROR_FILE_TOO_NEW:
					int theVersion = MSAccessFormats.getVersion(aUrl);

					if (theVersion == MSAccessFormats.VERSION_2007) {
						throw new SQLException("Sie versuchen eine Access 2007 Datenbank zu Öffnen.\n" +
								"Dazu benötigen sie entweder eine Office 2007 Installtion oder die Office 2007 Datenkonnektivitätskomponenten.\n\n" +
								"Diese können sie hier herunterladen:\n" +
								"http://www.microsoft.com/downloads/details.aspx?FamilyID=7554F536-8C28-4598-9B72-EF94E038C891&displaylang=" + Locale.getDefault().getLanguage() + "\n" +
								"http://www.microsoft.com/downloads/details.aspx?FamilyID=6f4edeed-d83f-4c31-ae67-458ae365d420&displaylang=" + Locale.getDefault().getLanguage());
					} else if ((theVersion == MSAccessFormats.VERSION_200X) || (theVersion == MSAccessFormats.VERSION_2000) || (theVersion == MSAccessFormats.VERSION_2002) || (theVersion == MSAccessFormats.VERSION_2003)) {
						throw new SQLException("Sie versuchen eine Access 2000+ Datenbank zu �ffnen.\n" +
								"Dazu ben�tigen sie mindestens die Jet 4.0 Engine.\n\n" +
								"Diese k�nnen sie hier herunterladen:\n" +
								"http://www.microsoft.com/downloads/details.aspx?familyid=2deddec4-350e-4cd0-a12a-d7f70a153156&displaylang=" + Locale.getDefault().getLanguage());
					}
					break;

				case ERROR_FILE_NOT_FOUND:
					throw new SQLException("Die Datei '" + aUrl + "' konnte nicht gefunden werden!");

				default:
					throw new SQLException(e.getMessage());
			}

			throw new SQLException(e.getMessage());
		}

		return connection;
	}

	public MSAccessDialect() {
		setSpacesAllowedInObjectNames(true);
		setCaseSensitive(false);
		setMaxObjectNameLength(64);
		setNullablePrimaryKeyAllowed(false);
		setCastType(NameCastType.NOTHING);
		setSupportsCustomTypes(false);
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
		registerType(createDataType("longbinary", "", Types.BLOB)); // OLE-Object
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
		return "C:\\<filename.mdb>";
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
	public DataType createDataType(String aName, String aDefinition, boolean anIdentity, int... aJdbcType) {
		return new MSAccessDataType(aName, aDefinition, anIdentity, aJdbcType);
	}

	@Override
	public DataType createDataType(String aName, String aDefinition, boolean anIdentity, boolean anArray, int... aJdbcType) {
		return new MSAccessDataType(aName, aDefinition, anIdentity, anArray, aJdbcType);
	}

}
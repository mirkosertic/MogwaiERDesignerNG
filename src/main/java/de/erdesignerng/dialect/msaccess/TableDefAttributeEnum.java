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

/**
 * @author $Author: dr-death $
 * @version $Date: 2009-11-06 14:15:00 $
 * @see http://msdn.microsoft.com/en-us/library/bb225819.aspx
 */
public final class TableDefAttributeEnum {

    public static final int DB_NONE = 0; // 0

    /**
     * Die Tabelle ist eine Systemtabelle, die vom Microsoft Jet-Datenbankmodul
     * bereitgestellt wird. Sie können diese Konstante auf ein angef�gtes
     * TableDef-Objekt festlegen.
     */
    public static final int DB_SYSTEM_OBJECT = -2147483646; // (-2^31 | 2^1)

    /**
     * Die Tabelle ist eine verborgene Tabelle, die vom Microsoft
     * Jet-Datenbankmodul bereitgestellt wird. Sie können diese Konstante auf     * ein angefügtes TableDef- Objekt festlegen.
     * <p/>
     * Diese Einstellung kann *nicht* über das Access-GUI eingestellt werden.
     */
    public static final int DB_HIDDEN_OBJECT = 1; // 2^0

    /**
     * Die Tabelle ist eine verborgene Tabelle, die vom Microsoft
     * Jet-Datenbankmodul bereitgestellt wird. Sie können diese Konstante auf
     * ein angefügtes TableDef- Objekt festlegen.
     * <p/>
     * Diese Einstellung kann über das GUI eingestellt werden - Eigenschaft
     * "Ausgeblendet".
     */
    public static final int DB_INVISIBLE_OBJECT = 8; // 2^3

    /**
     * Bei Datenbanken, die das Microsoft Jet-Datenbankmodul verwenden, ist die
     * Tabelle eine verknüpfte Tabelle, die für den exklusiven Zugriff ge�ffnet
     * wurde. Sie können die Konstanten auf einem angefügten TableDef-Objekt
     * einer lokalen Tabelle festlegen, nicht jedoch für eine Remote-Tabelle.
     */
    public static final int DB_ATTACHED_EXCLUSIVE = 65536; // 2^16

    /**
     * Bei Datenbanken, die das Microsoft Jet-Datenbankmodul verwenden, sind die
     * Benutzerkennung und das Kennwort für die verknüpfte Tabelle zusammen mit
     * den Verbindungsinformationen gespeichert. Sie können diese Konstanten auf
     * ein hinzugefügtes TableDef-Objekt einer Remote-Tabelle festlegen, nicht
     * jedoch auf eine lokale Tabelle.
     */
    public static final int DB_ATTACHED_SAVE_PWD = 131072; // 2^17

    /**
     * Die Tabelle ist eine verknüpfte Tabelle aus einer ODBC- Datenbank, z.B.
     * aus einer Datenbank des Microsoft SQL Server (schreibgesch�tzt).
     */
    public static final int DB_ATTACHED_ODBC = 536870912; // 2^19

    /**
     * Die Tabelle ist eine verknüpfte Tabelle aus einer Nicht-ODBC-Datenquelle,
     * z.B. aus einer Microsoft Jet- oder Paradox-Datenbank (schreibgesch�tzt).
     */
    public static final int DB_ATTACHED_TABLE = 1073741824; // 2^30

}
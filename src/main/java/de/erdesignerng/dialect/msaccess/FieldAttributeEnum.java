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
 * @see http://msdn.microsoft.com/en-us/library/bb242646.aspx
 */
public final class FieldAttributeEnum {

    public static final int DB_NONE = 0; // 0

    /**
     * Das Feld wird in absteigender Reihenfolge (Z bis A oder 100 bis 0)
     * sortiert (nur bei einem Field-Objekt in einer Fields-Auflistung eines
     * Index-Objekts). Wenn Sie diese Konstante nicht angeben, wird das Feld in
     * aufsteigender Reihenfolge (A bis Z oder 0 bis 100) sortiert
     * (Voreinstellung). Dies ist der Standardwert f�r Index- und
     * TableDef-Felder (nur Microsoft Jet- Arbeitsbereich).
     */
    public static final int DB_DESCENDING = 1; // 2^0

    /**
     * Die Feldgr��e ist festgelegt (Standard bei numerischen Feldern).
     */
    public static final int DB_FIXED_FIELD = 1; // 2^0

    /**
     * Die Feldgr��e kann ge�ndert werden (nur Textfelder).
     */
    public static final int DB_VARIABLE_FIELD = 2; // 2^1

    /**
     * Der Feldwert wird f�r neue Datens�tze automatisch um eins erh�ht (mit dem
     * Ergebnis Long Integer) und ergibt einen eindeutigen Wert, der nicht
     * ge�ndert werden kann (in einem Microsoft Jet-Arbeitsbereich, werden nur
     * Microsoft Jet Datenbank (.mdb)-Tabellen unterst�tzt).
     */
    public static final int DB_AUTO_INCR_FIELD = 16; // 2^4

    /**
     * Der Feldwert kann ge�ndert werden.
     */
    public static final int DB_UPDATABLE_FIELD = 32; // 2^5

    /**
     * Das Feld speichert Informationen f�r Replikate; dieser Feldtyp kann nicht
     * gel�scht werden (gilt nur f�r Microsoft Jet-Arbeitsbereiche).
     */
    public static final int DB_SYSTEM_FIELD = 8192; // 2^13

    /**
     * Das Feld enth�lt Hyperlink-Informationen (nur Memofelder).
     */
    public static final int DB_HYPERLINK_FIELD = 32768; // 2^15

}
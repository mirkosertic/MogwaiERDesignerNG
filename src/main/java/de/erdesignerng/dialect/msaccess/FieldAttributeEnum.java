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
     * (Voreinstellung). Dies ist der Standardwert für Index- und
     * TableDef-Felder (nur Microsoft Jet- Arbeitsbereich).
     */
    public static final int DB_DESCENDING = 1; // 2^0

    /**
     * Die Feldgröße ist festgelegt (Standard bei numerischen Feldern).
     */
    public static final int DB_FIXED_FIELD = 1; // 2^0

    /**
     * Die Feldgröße kann geändert werden (nur Textfelder).
     */
    public static final int DB_VARIABLE_FIELD = 2; // 2^1

    /**
     * Der Feldwert wird für neue Datensätze automatisch um eins erh�ht (mit dem
     * Ergebnis Long Integer) und ergibt einen eindeutigen Wert, der nicht
     * geändert werden kann (in einem Microsoft Jet-Arbeitsbereich, werden nur
     * Microsoft Jet Datenbank (.mdb)-Tabellen unterstützt).
     */
    public static final int DB_AUTO_INCR_FIELD = 16; // 2^4

    /**
     * Der Feldwert kann geändert werden.
     */
    public static final int DB_UPDATABLE_FIELD = 32; // 2^5

    /**
     * Das Feld speichert Informationen für Replikate; dieser Feldtyp kann nicht
     * gelöscht werden (gilt nur für Microsoft Jet-Arbeitsbereiche).
     */
    public static final int DB_SYSTEM_FIELD = 8192; // 2^13

    /**
     * Das Feld enthält Hyperlink-Informationen (nur Memofelder).
     */
    public static final int DB_HYPERLINK_FIELD = 32768; // 2^15

}
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
 * @see http://msdn.microsoft.com/en-us/library/bb225809.aspx
 */
public final class RelationAttributeEnum {

    public static final int DB_RELATION_NONE = 0; // 0

    /**
     * Es handelt sich um eine 1:1-Beziehung.
     */
    public static final int DB_RELATION_UNIQUE = 1; // 2^0

    /**
     * Die referentielle Integrität der Beziehung wird nicht erzwungen.
     */
    public static final int DB_RELATION_DONT_ENFORCE = 2; // 2^1

    /**
     * Die Beziehung existiert zwischen zwei verknüpften Tabellen aus einer
     * Datenbank, die nicht die aktuelle Datenbank ist.
     */
    public static final int DB_RELATION_INHERITED = 4; // 2^2

    /**
     * Aktualisierungen werden weitergegeben.
     */
    public static final int DB_RELATION_UPDATE_CASCADE = 256; // 2^8

    /**
     * Löschvorgänge werden weitergegeben.
     */
    public static final int DB_RELATION_DELETE_CASCADE = 4096; // 2^12

    /**
     * Beim Löschen werden betroffene Fremdschlüssel auf NULL gesetzt.
     */
    public static final int DB_RELATION_DELETE_SET_NULL = 8192;// 2^13

    /**
     * Nur in Microsoft Access. Zeigen Sie in der Entwurfsansicht ein LEFT JOIN
     * als standardmäßigen Verknüpfungstyp an.
     */
    public static final int DB_RELATION_LEFT = 16777216; // 2^24

    /**
     * Nur in Microsoft Access. Zeigen Sie in der Entwurfsansicht ein RIGHT JOIN
     * als standardmäßigen Verknüpfungstyp an.
     */
    public static final int DB_RELATION_RRIGHT = 33554432; // 2^25

}

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
 * @version $Date: 2009-11-08 22:50:00 $
 */
public final class QueryProperties {

    public static final class CreationType {

        public static final short ID = 0;

        public static final int ASSISTANT = 0;

        public static final int MANUALLY = 1;

    }

    public static final class QueryType {

        public static final short ID = 1;

        public static final int SELECT = 1;

        public static final int SELECT_INTO = 2;

        public static final int INSERT_INTO = 3;

        public static final int UPDATE = 4;

        public static final int TRANSFORM = 6;

        public static final int DDL = 7;

        public static final int PASS_THROUGH = 8;

        public static final int UNION = 9;

        public static final int DEFAULT = SELECT;

    }

    public static final class QueryOptions {

        public static final short ID = 3;

        /**
         * Alle Felder ausgeben: Nein; SELECT fieldName
         */
        public static final int RETURNS_SPECIFIED_FIELDS = 0; // 0

        /**
         * Alle Felder ausgeben: Ja; SELECT *; SELECT fieldName, *
         */
        public static final int RETURNS_ALL_FIELDS = 1; // 2^0

        public static final int DISTINCT = 2; // 2^1

        /**
         * ... WITH OWNERACCESS OPTION
         */
        public static final int OWNER_ACCESS_OPTION = 4; // 2^2

        public static final int DISTINCTROW = 8; // 2^3

        public static final int TOP_COUNT = 16; // 2^4

        public static final int TOP_PERCENT = 32; // 2^5

        public static final int DEFAULT = RETURNS_ALL_FIELDS;

    }

    /**
     * SELECT ... FROM ... IN SourceDatabase
     */
    public static final class SourceDatabase {

        public static final short ID = 4;

    }

    public static final class InputTables {

        public static final short ID = 5;

    }

    public static final class InputFields {
        // TRANSFORM 0
        // SELECT 2
        // FROM
        // GROUP BY
        // PIVOT 1
        public static final short ID = 6;

        /**
         * Wert
         */
        public static final int TRANSFORM = 0;

        /**
         * Spaltenüberschrift
         */
        public static final int PIVOT = 1;

        /**
         * Zeilenüberschrift
         */
        public static final int SELECT = 2;

        public static final int DEFAULT = TRANSFORM;

    }

    public static final class JoinTypes {

        public static final short ID = 7;

        public static final int INNER_JOIN = 1;

        public static final int LEFT_JOIN = 2;

        public static final int RIGHT_JOIN = 3;

    }

    public static final class WhereExpression {

        public static final short ID = 8;

    }

    public static final class GroupByExpression {

        public static final short ID = 9;

    }

    public static final class HavingExpression {

        public static final short ID = 10;

    }

    public static final class ColumnOrder {

        public static final short ID = 11;

        public static final String ASCENDING = "";

        public static final String DESCENDING = "D";

    }

    public static final class EndOfDefinition {

        public static final short ID = 255;

    }

}
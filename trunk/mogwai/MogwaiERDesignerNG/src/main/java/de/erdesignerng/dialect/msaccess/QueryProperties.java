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

    public final class CreationType {

        public static final int ID = 0;

        public static final int ASSISTANT = 0;

        public static final int MANUALLY = 1;

    }

    public final class QueryType {

        public static final int ID = 1;

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

    public final class QueryOptions {

        public static final int ID = 3;

        /**
         * Alle Felder ausgeben: Nein;
         * SELECT fieldName
         */
        public static final int RETURNS_SPECIFIED_FIELDS = 0;

        /**
         * Alle Felder ausgeben: Ja;
         * SELECT *; SELECT fieldName, *
         */
        public static final int RETURNS_ALL_FIELDS = 2^0;     // 1

        public static final int DISTINCT = 2^1;               // 2

        /**
         * ...
         * WITH OWNERACCESS OPTION
         */
        public static final int OWNER_ACCESS_OPTION = 2^2;    // 4

        public static final int DISTINCTROW = 2^3;            // 8

        public static final int TOP_TOTAL = 2^4;              // 16

        public static final int TOP_PERCENT = 2^5;            // 32

        public static final int DEFAULT = RETURNS_ALL_FIELDS;

    }

    /**
     * SELECT ... FROM ... IN SourceDatabase
     */
    public final class SourceDatabase {

        public static final int ID = 4;

    }

    public final class InputTables {

        public static final int ID = 5;

    }

    public final class Rows { //i.e. Fieldnames, Domain-Functions etc.

        public static final int ID = 6;

    }

    public final class JoinTypes {

        public static final int ID = 7;

        public static final int INNER_JOIN = 1;

        public static final int LEFT_JOIN = 2;

        public static final int RIGHT_JOIN = 3;

    }

    public final class WhereExpression {

        public static final int ID = 8;

    }

    public final class GroupByExpression {

        public static final int ID = 9;

    }

    public final class HavingExpression {

        public static final int ID = 10;

    }

    public final class ColumnOrder {

        public static final int ID = 11;

        public static final String ASCENDING = "";

        public static final String DESCENDING = "D";

    }

    public final class EndOfDefinition {

        public static final int ID = 255;

    }

}
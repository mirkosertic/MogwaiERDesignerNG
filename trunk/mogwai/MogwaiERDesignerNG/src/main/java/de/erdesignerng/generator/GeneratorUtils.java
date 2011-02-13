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
package de.erdesignerng.generator;

import de.erdesignerng.dialect.DataType;

import java.sql.Types;

public final class GeneratorUtils {

	private GeneratorUtils() {
	}

	public static String findClosestJavaTypeFor(DataType aType) {
		return findClosestJavaTypeFor(aType, true);
	}

	public static String findClosestJavaTypeFor(DataType aType, boolean aNullable) {
        return findClosestJavaTypeFor(aType.getJDBCType()[0], aNullable);
	}

    public static String findClosestJavaTypeFor(int aJdbcType, boolean aNullable) {
        switch (aJdbcType) {
            case Types.CHAR:
                return "String";
            case Types.VARCHAR:
                return "String";
            case Types.LONGVARCHAR:
                return "String";
            case Types.NUMERIC:
                return "java.math.BigDecimal";
            case Types.DECIMAL:
                return "java.math.BigDecimal";
            case Types.BIT:
                return aNullable ? "Boolean" : "boolean";
            case Types.TINYINT:
                return aNullable ? "Byte" : "byte";
            case Types.SMALLINT:
                return aNullable ? "Short" : "short";
            case Types.INTEGER:
                return aNullable ? "Integer" : "int";
            case Types.BIGINT:
                return aNullable ? "Long" : "long";
            case Types.REAL:
                return aNullable ? "Float" : "float";
            case Types.FLOAT:
                return aNullable ? "Double" : "double";
            case Types.DOUBLE:
                return aNullable ? "Double" : "double";
            case Types.BINARY:
                return "byte[]";
            case Types.VARBINARY:
                return "byte[]";
            case Types.LONGVARBINARY:
                return "byte[]";
            case Types.DATE:
                return "java.sql.Date";
            case Types.TIME:
                return "java.sql.Time";
            case Types.TIMESTAMP:
                return "java.sql.Timestamp";
            }
            return "String";
    }
}

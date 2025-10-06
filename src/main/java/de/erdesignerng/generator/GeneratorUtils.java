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

	public static String findClosestJavaTypeFor(final DataType aType) {
		return findClosestJavaTypeFor(aType, true);
	}

	public static String findClosestJavaTypeFor(final DataType aType, final boolean aNullable) {
		return findClosestJavaTypeFor(aType.getJDBCType()[0], aNullable);
	}

	public static String findClosestJavaTypeFor(final int aJdbcType, final boolean aNullable) {
        return switch (aJdbcType) {
            case Types.CHAR -> "String";
            case Types.VARCHAR -> "String";
            case Types.LONGVARCHAR -> "String";
            case Types.NUMERIC -> "java.math.BigDecimal";
            case Types.DECIMAL -> "java.math.BigDecimal";
            case Types.BIT -> aNullable ? "Boolean" : "boolean";
            case Types.TINYINT -> aNullable ? "Byte" : "byte";
            case Types.SMALLINT -> aNullable ? "Short" : "short";
            case Types.INTEGER -> aNullable ? "Integer" : "int";
            case Types.BIGINT -> aNullable ? "Long" : "long";
            case Types.REAL -> aNullable ? "Float" : "float";
            case Types.FLOAT -> aNullable ? "Double" : "double";
            case Types.DOUBLE -> aNullable ? "Double" : "double";
            case Types.BINARY -> "byte[]";
            case Types.VARBINARY -> "byte[]";
            case Types.LONGVARBINARY -> "byte[]";
            case Types.DATE -> "java.sql.Date";
            case Types.TIME -> "java.sql.Time";
            case Types.TIMESTAMP -> "java.sql.Timestamp";
            default -> "String";
        };
    }
}

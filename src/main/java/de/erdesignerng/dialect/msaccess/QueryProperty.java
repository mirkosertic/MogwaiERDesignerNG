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
 * @version $Date: 2009-11-09 09:30:00 $
 */
public class QueryProperty {
    private Integer propertyType = null;

    private String leadingSQL = "";

    private String trailingSQL = "";

    public QueryProperty(Integer propertyType, String leadingSQL) {
        this(propertyType, leadingSQL, "");
    }

    public QueryProperty(Integer propertyType, String leadingSQL, String trailingSQL) {
        this.propertyType = propertyType;
        this.leadingSQL = leadingSQL;
        this.trailingSQL = trailingSQL;
    }

    public Integer getPropertyType() {
        return propertyType;
    }

    public String getLeadingSQL() {
        return leadingSQL;
    }

    public String getTrailingSQL() {
        return trailingSQL;
    }

}
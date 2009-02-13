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
package de.erdesignerng.plugins.sqleonardo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ERRowInfo {

    private List<String> columns = new ArrayList<String>();
    private List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
    private int currentRowNum = -1;

    private Iterator<Map<String, Object>> iterator;
    private Map<String, Object> currentRow;

    public ERRowInfo() {
    }

    public void addRow(Map<String, Object> aRow) {
        result.add(aRow);
    }

    public void addField(String aFieldName) {
        columns.add(aFieldName);
    }

    public int findColumn(String columnName) {
        return columns.indexOf(columnName) + 1;
    }

    public <T> T getData(int i, Class<T> aClass) {
        if (i < 0 || i > columns.size()) {
            throw new IllegalArgumentException("Unknown column : " + i);
        }
        return getData(columns.get(i - 1), aClass);
    }

    public <T> T getData(String aColumnName, Class<T> aClass) {
        if (!columns.contains(aColumnName)) {
            throw new IllegalStateException("Unknown column : " + aColumnName);
        }
        if (currentRow.containsKey(aColumnName)) {
            Object theValue = currentRow.get(aColumnName);
            if (theValue == null) {
                return null;
            }
            if (theValue.getClass().equals(aClass)) {
                return (T) theValue;
            }
            throw new IllegalStateException("Invalid type for column " + aColumnName + " expected " + aClass
                    + " but got " + theValue.getClass());
        }
        return null;
    }

    public int getRow() {
        return currentRowNum;
    }

    public boolean next() {
        if (iterator == null) {
            iterator = result.iterator();
        }
        if (iterator.hasNext()) {
            currentRow = iterator.next();
            currentRowNum++;
            return true;
        }
        return false;
    }

}

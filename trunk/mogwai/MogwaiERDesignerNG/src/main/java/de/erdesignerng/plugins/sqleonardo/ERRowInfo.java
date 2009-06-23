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

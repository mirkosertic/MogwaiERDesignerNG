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

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

import org.apache.commons.lang.NotImplementedException;

public class ERResultSet implements ResultSet {

    private ERRowInfo rowinfo;

    public ERResultSet(ERRowInfo aInfo) {
        rowinfo = aInfo;
    }

    public void close() throws SQLException {
    }

    public int findColumn(String aColumnName) throws SQLException {
        return rowinfo.findColumn(aColumnName);
    }

    public boolean getBoolean(int columnIndex) throws SQLException {
        return rowinfo.getData(columnIndex, Boolean.class);
    }

    public boolean getBoolean(String columnName) throws SQLException {
        return rowinfo.getData(columnName, Boolean.class);
    }

    public byte getByte(int columnIndex) throws SQLException {
        return rowinfo.getData(columnIndex, Byte.class);
    }

    public byte getByte(String columnName) throws SQLException {
        return rowinfo.getData(columnName, Byte.class);
    }

    public int getConcurrency() throws SQLException {
        return ResultSet.CONCUR_READ_ONLY;
    }

    public String getCursorName() throws SQLException {
        return "CURSOR";
    }

    public Date getDate(int columnIndex) throws SQLException {
        return rowinfo.getData(columnIndex, Date.class);
    }

    public Date getDate(String columnName) throws SQLException {
        return rowinfo.getData(columnName, Date.class);
    }

    public double getDouble(int columnIndex) throws SQLException {
        return rowinfo.getData(columnIndex, Double.class);
    }

    public double getDouble(String columnName) throws SQLException {
        return rowinfo.getData(columnName, Double.class);
    }

    public int getFetchDirection() throws SQLException {
        return ResultSet.FETCH_FORWARD;
    }

    public int getFetchSize() throws SQLException {
        return 1;
    }

    public float getFloat(int columnIndex) throws SQLException {
        return rowinfo.getData(columnIndex, Float.class);
    }

    public float getFloat(String columnName) throws SQLException {
        return rowinfo.getData(columnName, Float.class);
    }

    public int getInt(int columnIndex) throws SQLException {
        return rowinfo.getData(columnIndex, Integer.class);
    }

    public int getInt(String columnName) throws SQLException {
        return rowinfo.getData(columnName, Integer.class);
    }

    public long getLong(int columnIndex) throws SQLException {
        return rowinfo.getData(columnIndex, Long.class);
    }

    public long getLong(String columnName) throws SQLException {
        return rowinfo.getData(columnName, Long.class);
    }

    public Object getObject(int columnIndex) throws SQLException {
        return rowinfo.getData(columnIndex, Object.class);
    }

    public Object getObject(String columnName) throws SQLException {
        return rowinfo.getData(columnName, Object.class);
    }

    public int getRow() throws SQLException {
        return rowinfo.getRow();
    }

    public short getShort(int columnIndex) throws SQLException {
        return rowinfo.getData(columnIndex, Short.class);
    }

    public short getShort(String columnName) throws SQLException {
        return rowinfo.getData(columnName, Short.class);
    }

    public String getString(int columnIndex) throws SQLException {
        return rowinfo.getData(columnIndex, String.class);
    }

    public String getString(String columnName) throws SQLException {
        return rowinfo.getData(columnName, String.class);
    }

    public Time getTime(int columnIndex) throws SQLException {
        return rowinfo.getData(columnIndex, Time.class);
    }

    public Time getTime(String columnName) throws SQLException {
        return rowinfo.getData(columnName, Time.class);
    }

    public Timestamp getTimestamp(int columnIndex) throws SQLException {
        return rowinfo.getData(columnIndex, Timestamp.class);
    }

    public Timestamp getTimestamp(String columnName) throws SQLException {
        return rowinfo.getData(columnName, Timestamp.class);
    }

    public int getType() throws SQLException {
        return ResultSet.TYPE_FORWARD_ONLY;
    }

    public SQLWarning getWarnings() throws SQLException {
        return null;
    }

    public boolean next() throws SQLException {
        return rowinfo.next();
    }

    // Not used methods

    public Statement getStatement() throws SQLException {
        throw new NotImplementedException();
    }

    public ResultSetMetaData getMetaData() throws SQLException {
        throw new NotImplementedException();
    }

    public byte[] getBytes(int columnIndex) throws SQLException {
        throw new NotImplementedException();
    }

    public byte[] getBytes(String columnName) throws SQLException {
        throw new NotImplementedException();
    }

    public Date getDate(int columnIndex, Calendar cal) throws SQLException {
        throw new NotImplementedException();
    }

    public Date getDate(String columnName, Calendar cal) throws SQLException {
        throw new NotImplementedException();
    }

    public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
        throw new NotImplementedException();
    }

    public Timestamp getTimestamp(String columnName, Calendar cal) throws SQLException {
        throw new NotImplementedException();
    }

    public Time getTime(int columnIndex, Calendar cal) throws SQLException {
        throw new NotImplementedException();
    }

    public Time getTime(String columnName, Calendar cal) throws SQLException {
        throw new NotImplementedException();
    }

    public Array getArray(int i) throws SQLException {
        throw new NotImplementedException();
    }

    public Array getArray(String colName) throws SQLException {
        throw new NotImplementedException();
    }

    public BigDecimal getBigDecimal(int i) throws SQLException {
        throw new NotImplementedException();
    }

    public BigDecimal getBigDecimal(String columnName) throws SQLException {
        throw new NotImplementedException();
    }

    public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
        throw new NotImplementedException();
    }

    public BigDecimal getBigDecimal(String columnName, int scale) throws SQLException {
        throw new NotImplementedException();
    }

    public Blob getBlob(int i) throws SQLException {
        throw new NotImplementedException();
    }

    public Blob getBlob(String colName) throws SQLException {
        throw new NotImplementedException();
    }

    public Ref getRef(int i) throws SQLException {
        throw new NotImplementedException();
    }

    public Ref getRef(String colName) throws SQLException {
        throw new NotImplementedException();
    }

    public boolean last() throws SQLException {
        throw new NotImplementedException();
    }

    public URL getURL(int columnIndex) throws SQLException {
        throw new NotImplementedException();
    }

    public URL getURL(String columnName) throws SQLException {
        throw new NotImplementedException();
    }

    public Reader getCharacterStream(int columnIndex) throws SQLException {
        throw new NotImplementedException();
    }

    public Reader getCharacterStream(String columnName) throws SQLException {
        throw new NotImplementedException();
    }

    public Clob getClob(int i) throws SQLException {
        throw new NotImplementedException();
    }

    public Clob getClob(String colName) throws SQLException {
        throw new NotImplementedException();
    }

    public InputStream getUnicodeStream(int columnIndex) throws SQLException {
        throw new NotImplementedException();
    }

    public InputStream getUnicodeStream(String columnName) throws SQLException {
        throw new NotImplementedException();
    }

    public InputStream getBinaryStream(int columnIndex) throws SQLException {
        throw new NotImplementedException();
    }

    public InputStream getBinaryStream(String columnName) throws SQLException {
        throw new NotImplementedException();
    }

    public InputStream getAsciiStream(int columnIndex) throws SQLException {
        throw new NotImplementedException();
    }

    public InputStream getAsciiStream(String columnName) throws SQLException {
        throw new NotImplementedException();
    }

    public boolean first() throws SQLException {
        throw new NotImplementedException();
    }

    public void insertRow() throws SQLException {
        throw new NotImplementedException();
    }

    public boolean isAfterLast() throws SQLException {
        throw new NotImplementedException();
    }

    public boolean isBeforeFirst() throws SQLException {
        throw new NotImplementedException();
    }

    public boolean isFirst() throws SQLException {
        throw new NotImplementedException();
    }

    public boolean isLast() throws SQLException {
        throw new NotImplementedException();
    }

    public void deleteRow() throws SQLException {
        throw new NotImplementedException();
    }

    public void moveToCurrentRow() throws SQLException {
        throw new NotImplementedException();
    }

    public void moveToInsertRow() throws SQLException {
        throw new NotImplementedException();
    }

    public boolean previous() throws SQLException {
        throw new NotImplementedException();
    }

    public void refreshRow() throws SQLException {
        throw new NotImplementedException();
    }

    public boolean relative(int rows) throws SQLException {
        throw new NotImplementedException();
    }

    public void setFetchDirection(int direction) throws SQLException {
        throw new NotImplementedException();
    }

    public void setFetchSize(int rows) throws SQLException {
        throw new NotImplementedException();
    }

    public boolean wasNull() throws SQLException {
        throw new NotImplementedException();
    }

    public boolean absolute(int row) throws SQLException {
        throw new NotImplementedException();
    }

    public void afterLast() throws SQLException {
        throw new NotImplementedException();
    }

    public void beforeFirst() throws SQLException {
        throw new NotImplementedException();
    }

    public void cancelRowUpdates() throws SQLException {
        throw new NotImplementedException();
    }

    public void clearWarnings() throws SQLException {
        throw new NotImplementedException();
    }

    public boolean rowDeleted() throws SQLException {
        throw new NotImplementedException();
    }

    public boolean rowInserted() throws SQLException {
        throw new NotImplementedException();
    }

    public boolean rowUpdated() throws SQLException {
        throw new NotImplementedException();
    }

    public void updateArray(int columnIndex, Array x) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateArray(String columnName, Array x) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateAsciiStream(String columnName, InputStream x, int length) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateBigDecimal(String columnName, BigDecimal x) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateBinaryStream(String columnName, InputStream x, int length) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateBlob(int columnIndex, Blob x) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateBlob(String columnName, Blob x) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateBoolean(int columnIndex, boolean x) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateBoolean(String columnName, boolean x) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateByte(int columnIndex, byte x) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateByte(String columnName, byte x) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateBytes(int columnIndex, byte[] x) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateBytes(String columnName, byte[] x) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateCharacterStream(String columnName, Reader reader, int length) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateClob(int columnIndex, Clob x) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateClob(String columnName, Clob x) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateDate(int columnIndex, Date x) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateDate(String columnName, Date x) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateDouble(int columnIndex, double x) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateDouble(String columnName, double x) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateFloat(int columnIndex, float x) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateFloat(String columnName, float x) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateInt(int columnIndex, int x) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateInt(String columnName, int x) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateLong(int columnIndex, long x) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateLong(String columnName, long x) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateNull(int columnIndex) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateNull(String columnName) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateObject(int columnIndex, Object x) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateObject(String columnName, Object x) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateObject(int columnIndex, Object x, int scale) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateObject(String columnName, Object x, int scale) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateRef(int columnIndex, Ref x) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateRef(String columnName, Ref x) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateRow() throws SQLException {
        throw new NotImplementedException();
    }

    public void updateShort(int columnIndex, short x) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateShort(String columnName, short x) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateString(int columnIndex, String x) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateString(String columnName, String x) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateTime(int columnIndex, Time x) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateTime(String columnName, Time x) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateTimestamp(String columnName, Timestamp x) throws SQLException {
        throw new NotImplementedException();
    }

    public int getHoldability() throws SQLException {
        throw new NotImplementedException();
    }

    public Reader getNCharacterStream(int arg0) throws SQLException {
        throw new NotImplementedException();
    }

    public Reader getNCharacterStream(String arg0) throws SQLException {
        throw new NotImplementedException();
    }

    public NClob getNClob(int arg0) throws SQLException {
        throw new NotImplementedException();
    }

    public NClob getNClob(String arg0) throws SQLException {
        throw new NotImplementedException();
    }

    public String getNString(int arg0) throws SQLException {
        throw new NotImplementedException();
    }

    public String getNString(String arg0) throws SQLException {
        throw new NotImplementedException();
    }

    public Object getObject(int arg0, Map<String, Class<?>> arg1) throws SQLException {
        throw new NotImplementedException();
    }

    public Object getObject(String arg0, Map<String, Class<?>> arg1) throws SQLException {
        throw new NotImplementedException();
    }

    public RowId getRowId(int arg0) throws SQLException {
        throw new NotImplementedException();
    }

    public RowId getRowId(String arg0) throws SQLException {
        throw new NotImplementedException();
    }

    public SQLXML getSQLXML(int arg0) throws SQLException {
        throw new NotImplementedException();
    }

    public SQLXML getSQLXML(String arg0) throws SQLException {
        throw new NotImplementedException();
    }

    public boolean isClosed() throws SQLException {
        throw new NotImplementedException();
    }

    public void updateAsciiStream(int arg0, InputStream arg1) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateAsciiStream(String arg0, InputStream arg1) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateAsciiStream(int arg0, InputStream arg1, long arg2) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateAsciiStream(String arg0, InputStream arg1, long arg2) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateBinaryStream(int arg0, InputStream arg1) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateBinaryStream(String arg0, InputStream arg1) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateBinaryStream(int arg0, InputStream arg1, long arg2) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateBinaryStream(String arg0, InputStream arg1, long arg2) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateBlob(int arg0, InputStream arg1) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateBlob(String arg0, InputStream arg1) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateBlob(int arg0, InputStream arg1, long arg2) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateBlob(String arg0, InputStream arg1, long arg2) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateCharacterStream(int arg0, Reader arg1) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateCharacterStream(String arg0, Reader arg1) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateCharacterStream(int arg0, Reader arg1, long arg2) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateCharacterStream(String arg0, Reader arg1, long arg2) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateClob(int arg0, Reader arg1) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateClob(String arg0, Reader arg1) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateClob(int arg0, Reader arg1, long arg2) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateClob(String arg0, Reader arg1, long arg2) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateNCharacterStream(int arg0, Reader arg1) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateNCharacterStream(String arg0, Reader arg1) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateNCharacterStream(int arg0, Reader arg1, long arg2) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateNCharacterStream(String arg0, Reader arg1, long arg2) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateNClob(int arg0, NClob arg1) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateNClob(String arg0, NClob arg1) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateNClob(int arg0, Reader arg1) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateNClob(String arg0, Reader arg1) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateNClob(int arg0, Reader arg1, long arg2) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateNClob(String arg0, Reader arg1, long arg2) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateNString(int arg0, String arg1) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateNString(String arg0, String arg1) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateRowId(int arg0, RowId arg1) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateRowId(String arg0, RowId arg1) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateSQLXML(int arg0, SQLXML arg1) throws SQLException {
        throw new NotImplementedException();
    }

    public void updateSQLXML(String arg0, SQLXML arg1) throws SQLException {
        throw new NotImplementedException();
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new NotImplementedException();
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new NotImplementedException();
    }
}
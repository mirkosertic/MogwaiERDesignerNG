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
package de.erdesignerng.dialect.mysql;

import de.erdesignerng.dialect.TableProperties;

public class MySQLTableProperties extends TableProperties {

	public enum EngineEnum {
		MyISAM, MEMORY, MERGE, BDB, EXAMPLE, NDBCLUSTER, ARCHIVE, CSV, BLACKHOLE, FEDERATED;
	};

	public enum RowFormatEnum {
		DEFAULT, DYNAMIC, FIXED, COMPRESSED, REDUNDANT, COMPACT;
	};

	public enum InsertMethodEnum {
		NO, FIRST, LAST;
	}

	private EngineEnum engine = EngineEnum.MyISAM;
	private int avgRowLength = -1;
	private int maxRows = -1;
	private int minRows = -1;
	private boolean checksum;
	private boolean packKeys;
	private boolean delayKeyWrite;
	private RowFormatEnum rowFormat;
	private InsertMethodEnum insertMethod;
	private String characterSet;

	public EngineEnum getEngine() {
		return engine;
	}

	public void setEngine(EngineEnum engine) {
		this.engine = engine;
	}

	public int getAvgRowLength() {
		return avgRowLength;
	}

	public void setAvgRowLength(int avgRowLength) {
		this.avgRowLength = avgRowLength;
	}

	public int getMaxRows() {
		return maxRows;
	}

	public void setMaxRows(int maxRows) {
		this.maxRows = maxRows;
	}

	public int getMinRows() {
		return minRows;
	}

	public void setMinRows(int minRows) {
		this.minRows = minRows;
	}

	public boolean isChecksum() {
		return checksum;
	}

	public void setChecksum(boolean checksum) {
		this.checksum = checksum;
	}

	public boolean isPackKeys() {
		return packKeys;
	}

	public void setPackKeys(boolean packKeys) {
		this.packKeys = packKeys;
	}

	public boolean isDelayKeyWrite() {
		return delayKeyWrite;
	}

	public void setDelayKeyWrite(boolean delayKeyWrite) {
		this.delayKeyWrite = delayKeyWrite;
	}

	public RowFormatEnum getRowFormat() {
		return rowFormat;
	}

	public void setRowFormat(RowFormatEnum rowFormat) {
		this.rowFormat = rowFormat;
	}

	public InsertMethodEnum getInsertMethod() {
		return insertMethod;
	}

	public void setInsertMethod(InsertMethodEnum insertMethod) {
		this.insertMethod = insertMethod;
	}

	public String getCharacterSet() {
		return characterSet;
	}

	public void setCharacterSet(String characterSet) {
		this.characterSet = characterSet;
	}
}
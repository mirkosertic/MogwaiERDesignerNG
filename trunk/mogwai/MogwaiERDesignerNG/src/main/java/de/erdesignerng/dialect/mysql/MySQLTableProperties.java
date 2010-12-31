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
		InnoDB, MyISAM, MEMORY, MERGE, BDB, EXAMPLE, NDBCLUSTER, ARCHIVE, CSV, BLACKHOLE, FEDERATED
	}

	public enum RowFormatEnum {
		DEFAULT, DYNAMIC, FIXED, COMPRESSED, REDUNDANT, COMPACT
	}

	public enum InsertMethodEnum {
		NO, FIRST, LAST
	}

	private EngineEnum engine = EngineEnum.MyISAM;

	private Integer avgRowLength;

	private Integer maxRows;

	private Integer minRows;

	private Boolean checksum;

	private Boolean packKeys;

	private Boolean delayKeyWrite;

	private RowFormatEnum rowFormat;

	private InsertMethodEnum insertMethod;

	private String characterSet;

	public EngineEnum getEngine() {
		return engine;
	}

	public void setEngine(EngineEnum engine) {
		this.engine = engine;
	}

	public Integer getAvgRowLength() {
		return avgRowLength;
	}

	public void setAvgRowLength(Integer avgRowLength) {
		this.avgRowLength = avgRowLength;
	}

	public Integer getMaxRows() {
		return maxRows;
	}

	public void setMaxRows(Integer maxRows) {
		this.maxRows = maxRows;
	}

	public Integer getMinRows() {
		return minRows;
	}

	public void setMinRows(Integer minRows) {
		this.minRows = minRows;
	}

	public Boolean getChecksum() {
		return checksum;
	}

	public void setChecksum(Boolean checksum) {
		this.checksum = checksum;
	}

	public Boolean getPackKeys() {
		return packKeys;
	}

	public void setPackKeys(Boolean packKeys) {
		this.packKeys = packKeys;
	}

	public Boolean getDelayKeyWrite() {
		return delayKeyWrite;
	}

	public void setDelayKeyWrite(Boolean delayKeyWrite) {
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
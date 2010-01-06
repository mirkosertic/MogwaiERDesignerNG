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
package de.erdesignerng.dialect.mssql;

import de.erdesignerng.dialect.ViewProperties;
import de.erdesignerng.model.View;

public class MSSQLViewProperties extends ViewProperties {
	
	private boolean checkOption;
	
	private boolean encryption;
	
	private boolean schemaBinding;
	
	private boolean viewMetaData;

	public MSSQLViewProperties(View aView) {
		super(aView);
	}

	public boolean isCheckOption() {
		return checkOption;
	}

	public void setCheckOption(boolean checkOption) {
		this.checkOption = checkOption;
	}

	public boolean isEncryption() {
		return encryption;
	}

	public void setEncryption(boolean encryption) {
		this.encryption = encryption;
	}

	public boolean isSchemaBinding() {
		return schemaBinding;
	}

	public void setSchemaBinding(boolean schemaBinding) {
		this.schemaBinding = schemaBinding;
	}

	public boolean isViewMetaData() {
		return viewMetaData;
	}

	public void setViewMetaData(boolean viewMetaData) {
		this.viewMetaData = viewMetaData;
	}
}
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

public class MSSQLViewProperties extends ViewProperties {

    private Boolean checkOption;

    private Boolean encryption;

    private Boolean schemaBinding;

    private Boolean viewMetaData;

    public Boolean getCheckOption() {
        return checkOption;
    }

    public void setCheckOption(Boolean checkOption) {
        this.checkOption = checkOption;
    }

    public Boolean getEncryption() {
        return encryption;
    }

    public void setEncryption(Boolean encryption) {
        this.encryption = encryption;
    }

    public Boolean getSchemaBinding() {
        return schemaBinding;
    }

    public void setSchemaBinding(Boolean schemaBinding) {
        this.schemaBinding = schemaBinding;
    }

    public Boolean getViewMetaData() {
        return viewMetaData;
    }

    public void setViewMetaData(Boolean viewMetaData) {
        this.viewMetaData = viewMetaData;
    }
}
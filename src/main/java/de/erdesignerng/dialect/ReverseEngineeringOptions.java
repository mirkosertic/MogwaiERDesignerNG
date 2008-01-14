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
package de.erdesignerng.dialect;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-01-14 20:01:05 $
 */
public class ReverseEngineeringOptions {

    private DefaultValueNamingEnum defaultValueNaming;

    private DomainNamingEnum domainNaming;

    private TableNamingEnum tableNaming;

    private Object[] schemaList;

    /**
     * @return the defaultValueNaming
     */
    public DefaultValueNamingEnum getDefaultValueNaming() {
        return defaultValueNaming;
    }

    /**
     * @param defaultValueNaming
     *            the defaultValueNaming to set
     */
    public void setDefaultValueNaming(DefaultValueNamingEnum defaultValueNaming) {
        this.defaultValueNaming = defaultValueNaming;
    }

    /**
     * @return the domainNaming
     */
    public DomainNamingEnum getDomainNaming() {
        return domainNaming;
    }

    /**
     * @param domainNaming
     *            the domainNaming to set
     */
    public void setDomainNaming(DomainNamingEnum domainNaming) {
        this.domainNaming = domainNaming;
    }

    /**
     * @return the schemaList
     */
    public Object[] getSchemaList() {
        return schemaList;
    }

    /**
     * @param schemaList
     *            the schemaList to set
     */
    public void setSchemaList(Object[] schemaList) {
        this.schemaList = schemaList;
    }

    /**
     * @return the tableNaming
     */
    public TableNamingEnum getTableNaming() {
        return tableNaming;
    }

    /**
     * @param tableNaming
     *            the tableNaming to set
     */
    public void setTableNaming(TableNamingEnum tableNaming) {
        this.tableNaming = tableNaming;
    }

}
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

import de.erdesignerng.model.TableType;
import java.util.ArrayList;
import java.util.List;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-06-13 16:48:58 $
 */
public class ReverseEngineeringOptions {

    private TableNamingEnum tableNaming;

    private List<SchemaEntry> schemaEntries = new ArrayList<>();

    private List<TableEntry> tableEntries = new ArrayList<>();

    public TableNamingEnum getTableNaming() {
        return tableNaming;
    }

    public void setTableNaming(TableNamingEnum tableNaming) {
        this.tableNaming = tableNaming;
    }

    public List<SchemaEntry> getSchemaEntries() {
        return schemaEntries;
    }

    public void setSchemaEntries(List<SchemaEntry> schemaEntries) {
        this.schemaEntries = schemaEntries;
    }

    @Override
    public String toString() {
        return schemaEntries.toString();
    }

    public List<TableEntry> getTableEntries() {
        return tableEntries;
    }

    public void setTableEntries(List<TableEntry> tableNames) {
        tableEntries = tableNames;
    }

    public List<TableType> getAvailableTableTypes() {
        List<TableType> theResult = new ArrayList<>();
        tableEntries.stream().filter(theEntry -> !theResult.contains(theEntry.getTableType())).forEach(theEntry -> {
            theResult.add(theEntry.getTableType());
        });
        return theResult;
    }
}
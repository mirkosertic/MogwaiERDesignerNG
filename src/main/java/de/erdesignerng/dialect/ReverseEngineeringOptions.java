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

import java.util.ArrayList;
import java.util.List;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-06-13 16:48:58 $
 */
public class ReverseEngineeringOptions {

    private TableNamingEnum tableNaming;

    private List<SchemaEntry> schemaEntries = new ArrayList<SchemaEntry>();

    private List<TableEntry> tableEntries = new ArrayList<TableEntry>();

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

    /**
     * Gibt den Wert des Attributs <code>schemaEntries</code> zurück.
     * 
     * @return Wert des Attributs schemaEntries.
     */
    public List<SchemaEntry> getSchemaEntries() {
        return schemaEntries;
    }

    /**
     * Setzt den Wert des Attributs <code>schemaEntries</code>.
     * 
     * @param schemaEntries
     *            Wert für das Attribut schemaEntries.
     */
    public void setSchemaEntries(List<SchemaEntry> schemaEntries) {
        this.schemaEntries = schemaEntries;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return schemaEntries.toString();
    }

    /**
     * @return the tableNames
     */
    public List<TableEntry> getTableEntries() {
        return tableEntries;
    }

    /**
     * @param tableNames
     *            the tableNames to set
     */
    public void setTableEntries(List<TableEntry> tableNames) {
        this.tableEntries = tableNames;
    }
}
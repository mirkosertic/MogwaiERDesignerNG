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
package de.erdesignerng.model;

import java.util.*;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-09 19:07:29 $
 */
public class RelationList extends ModelItemVector<Relation> {

    private static final long serialVersionUID = 330168987165235683L;

    // Just a cache for all foreign keys in the model
    private Set<Attribute> foreignKeyCache = new HashSet<Attribute>();

    private Map<Table, List<Relation>> relationsByImportingTable = new HashMap<Table, List<Relation>>();
    private Map<Table, List<Relation>> relationsByExportingTable = new HashMap<Table, List<Relation>>();

    private void updateForeignKeyCache() {
        synchronized (foreignKeyCache) {
            foreignKeyCache.clear();
            for (Relation theRelation : this) {
                addToForeignKeyCache(theRelation);
            }
        }
    }

    private void addToForeignKeyCache(Relation theRelation) {
        synchronized (foreignKeyCache) {
            Map<IndexExpression, Attribute> theMap = theRelation.getMapping();
            foreignKeyCache.addAll(theMap.values());
        }
    }

    /**
     * Test if an attribute is a foreign key attribute.
     *
     * @param aAttribute the attribute
     * @return true if yes, else false
     */
    public boolean isForeignKeyAttribute(Attribute aAttribute) {

        synchronized (foreignKeyCache) {
            return foreignKeyCache.contains(aAttribute);
        }
    }

    /**
     * Remove all relations that are connected to a given table.
     *
     * @param aTable the table
     */
    public void removeByTable(Table aTable) {
        List<Relation> theRelationsToRemove = new ArrayList<Relation>();
        for (Relation theRelation : this) {
            if (theRelation.getImportingTable().equals(aTable)) {
                theRelationsToRemove.add(theRelation);
            } else {
                if (theRelation.getExportingTable().equals(aTable)) {
                    theRelationsToRemove.add(theRelation);
                }
            }
        }
        removeAll(theRelationsToRemove);
        updateForeignKeyCache();
    }

    public List<Relation> getForeignKeysFor(Table aTable) {
        List<Relation> theResult = new ArrayList<Relation>();
        List<Relation> theByImportingTable = relationsByImportingTable.get(aTable);
        if (theByImportingTable != null) {
            theResult.addAll(theByImportingTable);
        }
        return theResult;
    }

    public List<Relation> getExportedKeysFor(Table aTable) {
        List<Relation> theResult = new ArrayList<Relation>();
        List<Relation> theByExportingTable = relationsByExportingTable.get(aTable);
        if (theByExportingTable != null) {
            theResult.addAll(theByExportingTable);
        }
        return theResult;
    }

    @Override
    public boolean add(Relation e) {

        List<Relation> byImporting = relationsByImportingTable.get(e.getImportingTable());
        if (byImporting == null) {
            byImporting = new ArrayList<Relation>();
            relationsByImportingTable.put(e.getImportingTable(), byImporting);
        }
        if (!byImporting.contains(e)) {
            byImporting.add(e);
        }

        List<Relation> byExporting = relationsByExportingTable.get(e.getExportingTable());
        if (byExporting == null) {
            byExporting = new ArrayList<Relation>();
            relationsByExportingTable.put(e.getExportingTable(), byExporting);
        }
        if (!byExporting.contains(e)) {
            byExporting.add(e);
        }

        boolean theResult = super.add(e);

        addToForeignKeyCache(e);

        return theResult;
    }

    @Override
    public boolean remove(Object o) {
        if (o instanceof Relation) {
            Relation theRelation = (Relation) o;
            relationsByExportingTable.remove(theRelation.getExportingTable());
            relationsByImportingTable.remove(theRelation.getImportingTable());
        }
        boolean theResult = super.remove(o);

        updateForeignKeyCache();

        return theResult;
    }

    public void clearCache() {
        updateForeignKeyCache();
    }
}
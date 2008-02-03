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

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2008-02-03 13:43:30 $
 */
public class Relation extends OwnedModelItem<Model> implements ModelItemClonable<Relation> {

    public static final String PROPERTY_POINTS = "points";

    public static final String PROPERTY_TEXT_OFFSET = "textoffset";

    private Table importingTable;

    private Table exportingTable;

    private Map<Attribute, Attribute> mapping = new HashMap<Attribute, Attribute>();

    private CascadeType onDelete = CascadeType.CASCADE;

    private CascadeType onUpdate = CascadeType.CASCADE;

    /**
     * @return the end
     */
    public Table getExportingTable() {
        return exportingTable;
    }

    /**
     * @param end
     *            the end to set
     */
    public void setExportingTable(Table end) {
        exportingTable = end;
    }

    /**
     * @return the start
     */
    public Table getImportingTable() {
        return importingTable;
    }

    /**
     * @param start
     *            the start to set
     */
    public void setImportingTable(Table start) {
        importingTable = start;
    }

    /**
     * @return the mapping
     */
    public Map<Attribute, Attribute> getMapping() {
        return mapping;
    }

    public CascadeType getOnDelete() {
        return onDelete;
    }

    public void setOnDelete(CascadeType aOnDelete) {
        onDelete = aOnDelete;
    }

    public CascadeType getOnUpdate() {
        return onUpdate;
    }

    public void setOnUpdate(CascadeType aOnUpdate) {
        onUpdate = aOnUpdate;
    }

    @Override
    public Relation clone() {
        Relation theClone = new Relation();
        theClone.setImportingTable(getImportingTable());
        theClone.setExportingTable(getExportingTable());
        theClone.setOnDelete(getOnDelete());
        theClone.setOnUpdate(getOnUpdate());
        for (Attribute theAttribute : getMapping().keySet()) {
            theClone.getMapping().put(theAttribute, getMapping().get(theAttribute));
        }
        return theClone;
    }

    public void restoreFrom(Relation aValue) throws Exception {
        setName(aValue.getName());
        setImportingTable(aValue.getImportingTable());
        setExportingTable(aValue.getExportingTable());
        setOnDelete(aValue.getOnDelete());
        setOnUpdate(aValue.getOnUpdate());
        for (Attribute theAttribute : aValue.getMapping().keySet()) {
            mapping.put(theAttribute, aValue.getMapping().get(theAttribute));
        }
    }

    public boolean isModified(Relation aRelation) {
        if (isStringModified(getName(), aRelation.getName())) {
            return true;
        }
        if (!aRelation.getOnDelete().equals(getOnDelete())) {
            return true;
        }
        if (!aRelation.getOnUpdate().equals(getOnUpdate())) {
            return true;
        }
        
        for (Attribute thePK : getMapping().keySet()) {
            Attribute theFK = getMapping().get(thePK);
            Attribute theForeignFK = aRelation.getMapping().get(thePK);
            if (!theFK.equals(theForeignFK)) {
                return true;
            }
            
        }        
        return false;
    }
}
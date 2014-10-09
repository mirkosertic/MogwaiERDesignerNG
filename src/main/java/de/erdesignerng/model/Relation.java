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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.map.ListOrderedMap;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-09 19:07:29 $
 */
public class Relation extends OwnedModelItem<Model> implements
        ModelItemCloneable<Relation> {

    public static final String PROPERTY_POINTS = "points";

    public static final String PROPERTY_TEXT_OFFSET = "textoffset";

    public static final String PROPERTY_LABEL_POSITION = "labelposition";

    private Table importingTable;

    private Table exportingTable;

    private final Map<IndexExpression, Attribute<Table>> mapping = new ListOrderedMap();

    private CascadeType onDelete = CascadeType.CASCADE;

    private CascadeType onUpdate = CascadeType.CASCADE;

    /**
     * @return the end
     */
    public Table getExportingTable() {
        return exportingTable;
    }

    /**
     * @param end the end to set
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
     * @param start the start to set
     */
    public void setImportingTable(Table start) {
        importingTable = start;
    }

    /**
     * @return the mapping
     */
    public Map<IndexExpression, Attribute<Table>> getMapping() {
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
        theClone.getProperties().copyFrom(getProperties());
        for (IndexExpression theExpression : getMapping().keySet()) {
            theClone.getMapping().put(theExpression,
                    getMapping().get(theExpression));
        }
        return theClone;
    }

    @Override
    public void restoreFrom(Relation aValue) {
        setName(aValue.getName());
        setImportingTable(aValue.getImportingTable());
        setExportingTable(aValue.getExportingTable());
        setOnDelete(aValue.getOnDelete());
        setOnUpdate(aValue.getOnUpdate());
        getProperties().copyFrom(aValue.getProperties());
        for (IndexExpression theExpression : aValue.getMapping().keySet()) {
            mapping.put(theExpression, aValue.getMapping().get(theExpression));
        }
    }

    public boolean isModified(Relation aRelation, boolean aUseName) {
        if (isStringModified(getName(), aRelation.getName())) {
            return true;
        }
        if (!(aRelation.getOnDelete() == getOnDelete())) {
            return true;
        }
        if (!(aRelation.getOnUpdate() == getOnUpdate())) {
            return true;
        }

        if (getProperties().isModified(aRelation.getProperties())) {
            return true;
        }

        List<Attribute> theMyAttributes = new ArrayList<>(mapping
                .values());
        List<Attribute> theOtherAttributes = new ArrayList<>(aRelation
                .getMapping().values());
        if (theMyAttributes.size() != theOtherAttributes.size()) {
            return true;
        }
        for (int i = 0; i < theMyAttributes.size(); i++) {
            if (aUseName) {
                if (!theMyAttributes.get(i).getName().equals(
                        theOtherAttributes.get(i).getName())) {
                    return true;
                }
            } else {
                if (!theMyAttributes.get(i).equals(theOtherAttributes.get(i))) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Check if it is a one to one relation.
     * <p/>
     * It is one to one if the referecing columns of the importing table are also the primary key,
     * so for every entry of the exporting table there can only be zero to one entries in the
     * importing table.
     *
     * @return true if is a one to one relation.
     */
    public boolean isOneToOne() {
        boolean isOneToOne = false;
        Index thePrimaryKey = importingTable.getPrimarykey();
        if (thePrimaryKey != null) {
            int thePrimaryKeyLength = thePrimaryKey.getExpressions().size();
            if (thePrimaryKeyLength == mapping.size()) {
                isOneToOne = true;
                for (Map.Entry<IndexExpression, Attribute<Table>> theEntry : mapping.entrySet()) {
                    if (!theEntry.getValue().isPrimaryKey()) {
                        isOneToOne = false;
                    }
                }
            }
        }
        return isOneToOne;
    }

    /**
     * Check if the relation is identifying.
     * <p/>
     * A relation is identifying if referencing columns of the importing table are not nullable.
     * so every entry of the importing table references one entry of the exporting table.
     * If one referencing column of the importing table is nullable, then every entry of
     * the importing table can reference the exporting table, so it is zero to one and not identifying.
     *
     * @return true if the relation is identifying.
     */
    public boolean isIdentifying() {
        boolean isIdentifying = true;
        for (Map.Entry<IndexExpression, Attribute<Table>> theEntry : mapping.entrySet()) {
            if (theEntry.getValue().isNullable()) {
                isIdentifying = false;
            }
        }

        return isIdentifying;
    }

    /**
     * Check if the relation is self referencing.
     *
     * @return true if yes
     */
    public boolean isSelfReference() {
        return importingTable == exportingTable;
    }
}
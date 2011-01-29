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

import de.erdesignerng.exception.ElementAlreadyExistsException;
import de.erdesignerng.exception.ElementInvalidNameException;
import org.apache.commons.lang.StringUtils;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-09 19:07:29 $
 */
public class Table extends OwnedModelItem<Model> {

    private AttributeList attributes = new AttributeList();

    private IndexList indexes = new IndexList();

    private String schema;

    /**
     * Add an attribute to the table.
     *
     * @param aModel     the model
     * @param aAttribute the table
     * @throws ElementAlreadyExistsException is thrown in case of an error
     * @throws ElementInvalidNameException   is thrown in case of an error
     */
    public void addAttribute(Model aModel, Attribute aAttribute) throws ElementAlreadyExistsException,
            ElementInvalidNameException {

        ModelUtilities.checkNameAndExistence(attributes, aAttribute, aModel.getDialect());

        aAttribute.setOwner(this);
        attributes.add(aAttribute);
    }

    /**
     * Add an index to the table.
     *
     * @param aModel the model
     * @param aIndex the table
     * @throws ElementAlreadyExistsException is thrown in case of an error
     * @throws ElementInvalidNameException   is thrown in case of an error
     */
    public void addIndex(Model aModel, Index aIndex) throws ElementAlreadyExistsException, ElementInvalidNameException {

        ModelUtilities.checkNameAndExistence(indexes, aIndex, aModel.getDialect());
        aIndex.setOwner(this);

        indexes.add(aIndex);
    }

    public AttributeList getAttributes() {
        return attributes;
    }

    public boolean isForeignKey(Attribute aAttribute) {
        Model theOwner = getOwner();
        if (theOwner != null) {
            return getOwner().getRelations().isForeignKeyAttribute(aAttribute);
        }
        return false;
    }

    public IndexList getIndexes() {
        return indexes;
    }

    public Index getPrimarykey() {
        for (Index theIndex : getIndexes()) {
            if (IndexType.PRIMARYKEY.equals(theIndex.getIndexType())) {
                return theIndex;
            }
        }
        return null;
    }

    /**
     * Test if the Table has a primary key.
     *
     * @return true if yes, else false
     */
    public boolean hasPrimaryKey() {
        Index theIndex = getPrimarykey();
        if (theIndex != null) {
            return theIndex.getExpressions().size() > 0;
        }
        return false;
    }

    /**
     * Test if the attribute is part of the primary key-
     *
     * @param aAttribute the attribute
     * @return true if yes, else false
     */
    public boolean isPrimaryKey(Attribute aAttribute) {
        Index thePrimaryKey = getPrimarykey();
        if (thePrimaryKey != null) {
            return thePrimaryKey.getExpressions().findByAttribute(aAttribute) != null;
        }
        return false;
    }

    /**
     * @return the schema
     */
    public String getSchema() {
        return schema;
    }

    /**
     * @param schema the schema to set
     */
    public void setSchema(String schema) {
        this.schema = schema;
    }

    @Override
    public String getUniqueName() {
        if (!StringUtils.isEmpty(schema)) {
            return schema + "." + getName();
        }
        return super.getUniqueName();
    }
}
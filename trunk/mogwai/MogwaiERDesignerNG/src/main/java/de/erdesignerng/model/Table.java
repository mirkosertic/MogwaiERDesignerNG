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

import de.erdesignerng.exception.CannotDeleteException;
import de.erdesignerng.exception.ElementAlreadyExistsException;
import de.erdesignerng.exception.ElementInvalidNameException;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-01-28 21:39:40 $
 */
public class Table extends OwnedModelItem<Model> implements OwnedModelItemVerifier {

    private AttributeList attributes = new AttributeList();

    private IndexList indexes = new IndexList();

    /**
     * Add an attribute to the table.
     * 
     * @param aModel
     *            the model
     * @param aAttribute
     *            the table
     * @throws ElementAlreadyExistsException is thrown in case of an error
     * @throws ElementInvalidNameException is thrown in case of an error
     */
    public void addAttribute(Model aModel, Attribute aAttribute) throws ElementAlreadyExistsException,
            ElementInvalidNameException {

        ModelUtilities.checkNameAndExistance(attributes, aAttribute, aModel.getDialect());

        aAttribute.setOwner(this);
        attributes.add(aAttribute);
    }

    /**
     * Add an index to the table.
     * 
     * @param aModel
     *            the model
     * @param aIndex
     *            the table
     * @throws ElementAlreadyExistsException is thrown in case of an error
     * @throws ElementInvalidNameException is thrown in case of an error
     */
    public void addIndex(Model aModel, Index aIndex) throws ElementAlreadyExistsException, ElementInvalidNameException {

        Model theOwner = getOwner();
        if (theOwner != null) {
            ModelUtilities.checkNameAndExistance(indexes, aIndex, theOwner.getDialect());
        }

        aIndex.setOwner(this);

        indexes.add(aIndex);
    }

    public void checkNameAlreadyExists(ModelItem aSender, String aName) throws ElementAlreadyExistsException {

        Model theOwner = getOwner();

        if (aSender instanceof Attribute) {
            ModelUtilities.checkExistance(attributes, aName, theOwner.getDialect());
        }
        if (aSender instanceof Index) {
            ModelUtilities.checkExistance(indexes, aName, theOwner.getDialect());
        }

    }

    public void delete(ModelItem aSender) throws CannotDeleteException {

        Model theOwner = getOwner();

        if (aSender instanceof Attribute) {
            if (attributes.size() == 1) {
                throw new CannotDeleteException("Table must have at least one attribute!");
            }

            Attribute theAttribute = (Attribute) aSender;
            attributes.remove(theAttribute);

            return;
        }

        if (aSender instanceof Index) {

            Index theIndex = (Index) aSender;

            indexes.remove(theIndex);

            return;
        }

        throw new UnsupportedOperationException("Unknown element " + aSender);

    }

    public String checkName(String aName) throws ElementInvalidNameException {
        Model theOwner = getOwner();
        if (theOwner != null) {
            return theOwner.checkName(aName);
        }

        return aName;
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

    public void setIndexes(IndexList aIndexes) {
        indexes = aIndexes;
    }

    public void setAttributes(AttributeList aAttributes) {
        attributes = aAttributes;
    }

    public Index getPrimarykey() {
        for (Index theIndex : getIndexes()) {
            if (IndexType.PRIMARYKEY.equals(theIndex.getIndexType())) {
                return theIndex;
            }
        }
        return null;
    }
}

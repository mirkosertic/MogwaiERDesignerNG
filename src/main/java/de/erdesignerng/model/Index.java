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

/**
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2008-01-22 20:54:05 $
 */
public class Index extends OwnedModelItem<Table> implements ModelItemClonable<Index> {

    private IndexType indexType = IndexType.UNIQUE;

    private AttributeList attributes = new AttributeList();

    public AttributeList getAttributes() {
        return attributes;
    }

    public void setAttributes(AttributeList aAttributes) {
        attributes = aAttributes;
    }

    public IndexType getIndexType() {
        return indexType;
    }

    public void setIndexType(IndexType aIndexType) {
        indexType = aIndexType;
    }

    @Override
    public Index clone() {
        Index theIndex = new Index();
        theIndex.setSystemId(getSystemId());
        theIndex.setOwner(getOwner());
        theIndex.setName(getName());
        theIndex.setIndexType(getIndexType());

        for (Attribute theAttribute : attributes) {
            theIndex.getAttributes().add(theAttribute.clone());
        }
        return theIndex;
    }

    public void restoreFrom(Index aValue) throws Exception {
        setName(aValue.getName());
        setIndexType(aValue.getIndexType());
        setOwner(aValue.getOwner());

        attributes.clear();

        for (Attribute theAttribute : aValue.getAttributes()) {
            Attribute theFound = getOwner().getAttributes().findBySystemId(theAttribute.getSystemId());
            if (theFound != null) {
                attributes.add(theFound);
            } else {
                throw new Exception("Cannot find attribute " + theAttribute.getName());
            }
        }
    }

    public boolean isModified(Index aIndex) {

        if (!getName().equals(aIndex.getName())) {
            return true;
        }

        if (!indexType.equals(aIndex.getIndexType())) {
            return true;
        }

        for (Attribute theAttribute : getAttributes()) {
            if (aIndex.getAttributes().findBySystemId(theAttribute.getSystemId()) == null) {
                return true;
            }
        }

        for (Attribute theAttribute : aIndex.getAttributes()) {
            if (getAttributes().findBySystemId(theAttribute.getSystemId()) == null) {
                return true;
            }
        }

        return false;
    }
}

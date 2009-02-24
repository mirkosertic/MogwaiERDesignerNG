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
 * @version $Date: 2009-02-24 19:36:28 $
 */
public class Index extends OwnedModelItem<Table> implements ModelItemClonable<Index> {

    private IndexType indexType = IndexType.UNIQUE;

    private IndexExpressionList expressions = new IndexExpressionList();

    public IndexExpressionList getExpressions() {
        return expressions;
    }

    public void setExpressions(IndexExpressionList expressions) {
        this.expressions = expressions;
    }

    public IndexType getIndexType() {
        return indexType;
    }

    public void setIndexType(IndexType aIndexType) {
        indexType = aIndexType;
    }

    /**
     * Test if this index contains an attribute.
     * 
     * @param aAttribute
     *                the attribute
     * @return true if yes, else false
     */
    public boolean containsAttribute(Attribute aAttribute) {
        for (IndexExpression theExpression : expressions) {
            Attribute theRefAttribute = theExpression.getAttributeRef();
            if (theRefAttribute != null) {
                if (aAttribute.getSystemId().equals(theRefAttribute.getSystemId())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Index clone() {
        Index theIndex = new Index();
        theIndex.setSystemId(getSystemId());
        theIndex.setOwner(getOwner());
        theIndex.setName(getName());
        theIndex.setIndexType(getIndexType());

        for (IndexExpression theExpression : expressions) {
            theIndex.getExpressions().add(theExpression.clone());
        }
        return theIndex;
    }

    public void restoreFrom(Index aValue) throws Exception {
        setName(aValue.getName());
        setIndexType(aValue.getIndexType());
        setOwner(aValue.getOwner());

        expressions.clear();

        for (IndexExpression theAttribute : aValue.getExpressions()) {
            expressions.add(theAttribute);
        }
    }

    public boolean isModified(Index aIndex, boolean aUseName) {

        if (!getName().equals(aIndex.getName())) {
            return true;
        }

        if (!indexType.equals(aIndex.getIndexType())) {
            return true;
        }

        if (expressions.size() != aIndex.getExpressions().size()) {
            return true;
        }

        for (int i = 0; i < expressions.size(); i++) {
            IndexExpression theMyExpression = expressions.get(i);
            IndexExpression theOtherExpression = aIndex.getExpressions().get(i);

            if (theMyExpression.isModified(theOtherExpression, aUseName)) {
                return true;
            }
        }

        for (int i = 0; i < aIndex.getExpressions().size(); i++) {
            IndexExpression theOtherExpression = aIndex.getExpressions().get(i);
            IndexExpression theMyExpression = expressions.get(i);

            if (theMyExpression.isModified(theOtherExpression, aUseName)) {
                return true;
            }
        }

        return false;
    }
}

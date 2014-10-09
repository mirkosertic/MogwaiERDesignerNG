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

import java.util.stream.Collectors;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-09 19:07:29 $
 */
public class Index extends OwnedModelItem<Table> implements
        ModelItemCloneable<Index> {

    private IndexType indexType = IndexType.UNIQUE;

    private final IndexExpressionList expressions = new IndexExpressionList();

    public IndexExpressionList getExpressions() {
        return expressions;
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
        theIndex.getProperties().copyFrom(getProperties());

        for (IndexExpression theExpression : expressions) {
            theIndex.getExpressions().add(theExpression.clone());
        }
        return theIndex;
    }

    @Override
    public void restoreFrom(Index aValue) {
        setName(aValue.getName());
        setIndexType(aValue.getIndexType());
        setOwner(aValue.getOwner());
        getProperties().copyFrom(aValue.getProperties());

        expressions.clear();

        expressions.addAll(aValue.getExpressions().stream().collect(Collectors.toList()));
    }

    public boolean isModified(Index aIndex, boolean aUseName) {

        if (!getName().equals(aIndex.getName())) {
            return true;
        }

        if (!(indexType == aIndex.getIndexType())) {
            return true;
        }

        if (expressions.size() != aIndex.getExpressions().size()) {
            return true;
        }

        if (getProperties().isModified(aIndex.getProperties())) {
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
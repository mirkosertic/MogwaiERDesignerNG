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
package de.erdesignerng.model.check;

import de.erdesignerng.exception.ElementAlreadyExistsException;
import de.erdesignerng.model.*;

/**
 * Check for Foreign keys without an index (performance problem).
 */
public class ForeignKeyWithoutIndexCheck implements ModelCheck {
    @Override
    public void check(Model aModel, ModelChecker aChecker) {
        for (Relation theRelation : aModel.getRelations()) {
            Table theImportingTable = theRelation.getImportingTable();

            boolean containsValidIndex = false;

            IndexExpressionList theList = new IndexExpressionList();
            for (Attribute theAttribute : theRelation.getMapping().values()) {
                try {
                    theList.addExpressionFor(theAttribute);
                } catch (ElementAlreadyExistsException e) {
                    throw new RuntimeException(e);
                }
            }

            for (Index theIndex : theImportingTable.getIndexes()) {
                if (theIndex.getExpressions().containsAllExpressions(theList)) {
                    containsValidIndex = true;
                }
            }

            if (!containsValidIndex) {
                aChecker.addError(new ModelError("Table " + theImportingTable.getName() + " does not have a matching index for " + theRelation.getName() + " " + theRelation.getExportingTable() + " ->  " + theRelation.getMapping().values()));
            }
        }
    }
}

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
import de.erdesignerng.exception.ElementInvalidNameException;
import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.Index;
import de.erdesignerng.model.IndexExpressionList;
import de.erdesignerng.model.IndexType;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.Relation;
import de.erdesignerng.model.Table;
import de.erdesignerng.modificationtracker.VetoException;

/**
 * Check for Foreign keys without an index (performance problem).
 */
public class ForeignKeyWithoutIndexCheck implements ModelCheck {

	private static class CreateIndexForRelationQuickFix implements QuickFix {

		private Relation relation;

		private CreateIndexForRelationQuickFix(Relation aRelation) {
			relation = aRelation;
		}

		@Override
		public Object[] applyTo(Model aModel) throws VetoException, ElementAlreadyExistsException,
				ElementInvalidNameException {
			Table theImportingTable = relation.getImportingTable();
			Index theIndex = new Index();
			boolean validName = false;
			int counter = 1;
			while (!validName) {
				theIndex.setName(theImportingTable.getName() + "_FK" + counter++);
				if (theImportingTable.getIndexes().findByName(theIndex.getName()) == null) {
					validName = true;
				}
			}
			theIndex.setIndexType(IndexType.NONUNIQUE);
			for (Attribute<Table> theAttribute : relation.getMapping().values()) {
				theIndex.getExpressions().addExpressionFor(theAttribute);
			}
			aModel.addIndexToTable(theImportingTable, theIndex);

			return new Object[]{theImportingTable};
		}
	}

	@Override
	public void check(Model aModel, ModelChecker aChecker) {
		for (Relation theRelation : aModel.getRelations()) {
			Table theImportingTable = theRelation.getImportingTable();

			boolean containsValidIndex = false;

			IndexExpressionList theList = new IndexExpressionList();
			for (Attribute<Table> theAttribute : theRelation.getMapping().values()) {
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
				aChecker.addError(new ModelError("Table " + theImportingTable.getName() + " does not have a matching index for " + theRelation.getName() + " " + theRelation.getExportingTable() + " ->  " + theRelation.getMapping().values(), new CreateIndexForRelationQuickFix(theRelation)));
			}
		}
	}
}

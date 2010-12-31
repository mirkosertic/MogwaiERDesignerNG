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
package de.erdesignerng.visual.common;

import java.text.MessageFormat;

import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.Index;
import de.erdesignerng.model.IndexExpression;
import de.erdesignerng.model.ModelUtilities;
import de.erdesignerng.model.Relation;
import de.erdesignerng.model.Table;
import de.erdesignerng.visual.cells.RelationEdge;
import de.erdesignerng.visual.cells.TableCell;
import de.erdesignerng.visual.editor.DialogConstants;
import de.erdesignerng.visual.editor.relation.RelationEditor;

public class AddRelationCommand extends UICommand {

	private final TableCell exportingTable;

	private final TableCell importingTable;

	public AddRelationCommand(ERDesignerComponent component, TableCell aImportingCell, TableCell aExportingCell) {
		super(component);
		exportingTable = aExportingCell;
		importingTable = aImportingCell;
	}

	@Override
	public void execute() {
		Table theImportingTable = (Table) importingTable.getUserObject();
		Table theExportingTable = (Table) exportingTable.getUserObject();

		Relation theRelation = createPreparedRelationFor(theImportingTable, theExportingTable);

		RelationEditor theEditor = new RelationEditor(theImportingTable.getOwner(), getDetailComponent());
		theEditor.initializeFor(theRelation);

		if (theEditor.showModal() == DialogConstants.MODAL_RESULT_OK) {

			RelationEdge theEdge = new RelationEdge(theRelation, importingTable, exportingTable);

			try {
				theEditor.applyValues();
				component.graph.getGraphLayoutCache().insert(theEdge);

				refreshDisplayOf(null);
			} catch (Exception e) {
				getWorldConnector().notifyAboutException(e);
			}
		}
	}

	private Relation createPreparedRelationFor(Table aSourceTable, Table aTargetTable) {
		Relation theRelation = new Relation();
		theRelation.setImportingTable(aSourceTable);
		theRelation.setExportingTable(aTargetTable);
		theRelation.setOnUpdate(getPreferences().getOnUpdateDefault());
		theRelation.setOnDelete(getPreferences().getOnDeleteDefault());

		String thePattern = getPreferences().getAutomaticRelationAttributePattern();
		String theTargetTableName = component.getModel().getDialect().getCastType().cast(aTargetTable.getName());

		// Create the foreign key suggestions
		Index thePrimaryKey = aTargetTable.getPrimarykey();
		for (IndexExpression theExpression : thePrimaryKey.getExpressions()) {
			Attribute theAttribute = theExpression.getAttributeRef();
			if (theAttribute != null) {
				String theNewname = MessageFormat.format(thePattern, theTargetTableName, theAttribute.getName());
				Attribute theNewAttribute = aSourceTable.getAttributes().findByName(theNewname);
				if (theNewAttribute == null) {
					theNewAttribute = theAttribute.clone();
					theNewAttribute.setSystemId(ModelUtilities.createSystemIdFor(theNewAttribute));
					theNewAttribute.setOwner(null);
					theNewAttribute.setName(theNewname);
				}
				theRelation.getMapping().put(theExpression, theNewAttribute);
			}
		}
		return theRelation;
	}
}
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

import de.erdesignerng.model.*;
import de.erdesignerng.util.ApplicationPreferences;
import de.erdesignerng.visual.editor.DialogConstants;
import de.erdesignerng.visual.editor.relation.RelationEditor;

import java.text.MessageFormat;

public class AddRelationCommand extends UICommand {

    private final Table exportingTable;

    private final Table importingTable;

    public AddRelationCommand(Table aImportingCell, Table aExportingCell) {
        exportingTable = aExportingCell;
        importingTable = aImportingCell;
    }

    @Override
    public void execute() {

        Relation theRelation = createPreparedRelationFor(importingTable,
                exportingTable);

        RelationEditor theEditor = new RelationEditor(importingTable
                .getOwner(), getDetailComponent());
        theEditor.initializeFor(theRelation);

        if (theEditor.showModal() == DialogConstants.MODAL_RESULT_OK) {

            try {

                theEditor.applyValues();

                ERDesignerComponent.getDefault().commandCreateRelation(theRelation);

                refreshDisplayAndOutline();
            } catch (Exception e) {
                getWorldConnector().notifyAboutException(e);
            }
        }
    }

    private Relation createPreparedRelationFor(Table aSourceTable,
                                               Table aTargetTable) {
        Relation theRelation = new Relation();
        theRelation.setImportingTable(aSourceTable);
        theRelation.setExportingTable(aTargetTable);
        theRelation.setOnUpdate(ApplicationPreferences.getInstance()
                .getOnUpdateDefault());
        theRelation.setOnDelete(ApplicationPreferences.getInstance()
                .getOnDeleteDefault());

        String thePattern = ApplicationPreferences.getInstance()
                .getAutomaticRelationAttributePattern();
        String theTargetTableName = ERDesignerComponent.getDefault().getModel().getDialect()
                .getCastType().cast(aTargetTable.getName());

        // Create the foreign key suggestions
        Index thePrimaryKey = aTargetTable.getPrimarykey();
        for (IndexExpression theExpression : thePrimaryKey.getExpressions()) {
            Attribute<Table> theAttribute = theExpression.getAttributeRef();
            if (theAttribute != null) {
                String theNewname = MessageFormat.format(thePattern,
                        theTargetTableName, theAttribute.getName());
                Attribute<Table> theNewAttribute = aSourceTable.getAttributes().findByName(theNewname);
                if (theNewAttribute == null) {
                    theNewAttribute = theAttribute.clone();
                    theNewAttribute.setSystemId(ModelUtilities
                            .createSystemIdFor());
                    theNewAttribute.setOwner(null);
                    theNewAttribute.setName(theNewname);
                }
                theRelation.getMapping().put(theExpression, theNewAttribute);
            }
        }
        return theRelation;
    }
}
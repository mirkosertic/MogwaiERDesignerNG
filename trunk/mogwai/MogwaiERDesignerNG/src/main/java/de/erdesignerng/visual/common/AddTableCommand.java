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

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import de.erdesignerng.model.SubjectArea;
import de.erdesignerng.model.Table;
import de.erdesignerng.modificationtracker.VetoException;
import de.erdesignerng.visual.cells.SubjectAreaCell;
import de.erdesignerng.visual.cells.TableCell;
import de.erdesignerng.visual.editor.DialogConstants;
import de.erdesignerng.visual.editor.table.TableEditor;

public class AddTableCommand extends UICommand {

	private final Point2D location;

	private final TableCell exportingCell;

	private final boolean newTableIsChild;

	public AddTableCommand(ERDesignerComponent component, Point2D aLocation, TableCell aExportingCell,
			boolean aNewTableIsChild) {
		super(component);

		location = aLocation;
		exportingCell = aExportingCell;
		newTableIsChild = aNewTableIsChild;
	}

	@Override
	public void execute() {
		if (!component.checkForValidConnection()) {
			return;
		}

		Table theTable = new Table();
		TableEditor theTableEditor = new TableEditor(component.getModel(), getDetailComponent());
		theTableEditor.initializeFor(theTable);
		if (theTableEditor.showModal() == DialogConstants.MODAL_RESULT_OK) {
			try {

				try {
					theTableEditor.applyValues();
				} catch (VetoException e) {
					getWorldConnector().notifyAboutException(e);
					return;
				}

				TableCell theImportingCell = new TableCell(theTable);
				theImportingCell.transferPropertiesToAttributes(theTable);

				Object theTargetCell = component.graph.getFirstCellForLocation(location.getX(), location.getY());
				if (theTargetCell instanceof SubjectAreaCell) {
					SubjectAreaCell theSACell = (SubjectAreaCell) theTargetCell;
					SubjectArea theArea = (SubjectArea) theSACell.getUserObject();
					theArea.getTables().add(theTable);

					theSACell.add(theImportingCell);
				}

				theImportingCell.setBounds(new Rectangle2D.Double(location.getX(), location.getY(), -1, -1));

				if (exportingCell != null) {

					// If the user cancels the add relation dialog
					// the table is added, too
					if (newTableIsChild) {
						new AddRelationCommand(component, theImportingCell, exportingCell).execute();
					} else {
						new AddRelationCommand(component, exportingCell, theImportingCell).execute();
					}
				}

				component.graph.getGraphLayoutCache().insert(theImportingCell);

				theImportingCell.transferAttributesToProperties(theImportingCell.getAttributes());

				component.graph.doLayout();

				refreshDisplayOf(null);

			} catch (Exception e) {
				getWorldConnector().notifyAboutException(e);
			}
		}
	}
}

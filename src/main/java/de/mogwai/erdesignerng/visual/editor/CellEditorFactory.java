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
package de.mogwai.erdesignerng.visual.editor;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.util.EventObject;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.jgraph.JGraph;
import org.jgraph.graph.DefaultGraphCellEditor;

import de.mogwai.erdesignerng.model.Relation;
import de.mogwai.erdesignerng.model.Table;
import de.mogwai.erdesignerng.visual.cells.RelationEdge;
import de.mogwai.erdesignerng.visual.cells.TableCell;
import de.mogwai.erdesignerng.visual.editor.relation.RelationEditor;
import de.mogwai.erdesignerng.visual.editor.table.TableEditor;

/**
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2007-07-08 17:55:45 $
 */
public class CellEditorFactory extends DefaultGraphCellEditor {

	private Object editingValue;

	protected BaseEditor createEditDialogForValue(JFrame aParent, Object aValue) {

		if (aValue instanceof TableCell) {
			TableCell theCell = (TableCell) aValue;

			Table theTable = (Table) theCell.getUserObject();
			TableEditor theEditor = new TableEditor(theTable.getOwner(),
					aParent);
			theEditor.initializeFor(theTable);
			return theEditor;
		}

		if (aValue instanceof RelationEdge) {
			RelationEdge theCell = (RelationEdge) aValue;

			Relation theRelation = (Relation) theCell.getUserObject();

			RelationEditor theEditor = new RelationEditor(theRelation
					.getOwner(), aParent);
			theEditor.initializeFor(theRelation);

			return theEditor;
		}

		throw new IllegalArgumentException();
	}

	public Component getGraphCellEditorComponent(JGraph aGraph, Object aValue,
			boolean arg2) {

		editingValue = aValue;

		JFrame theParent = (JFrame) SwingUtilities.getRoot(aGraph);

		BaseEditor theEditor = createEditDialogForValue(theParent, aValue);

		theEditor.validate();

		Dimension w2 = theEditor.getSize();
		Dimension w1 = theParent.getSize();

		Point thePoint = theParent.getLocation();
		theEditor.setLocation(thePoint.x + w1.width / 2 - w2.width / 2,
				thePoint.y + w1.height / 2 - w2.height / 2);

		editingValue = aValue;
		return theEditor;
	}

	@Override
	public boolean isCellEditable(EventObject aEvent) {
		return true;
	}
}

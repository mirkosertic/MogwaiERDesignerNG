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
package de.mogwai.erdesignerng.visual.plaf.basic;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.SwingUtilities;

import org.jgraph.JGraph;
import org.jgraph.graph.CellView;
import org.jgraph.plaf.basic.BasicGraphUI;

import de.mogwai.erdesignerng.visual.editor.BaseEditor;

public class ERDesignerGraphUI extends BasicGraphUI {

	public class MyMouseHandler extends MouseHandler {

		@Override
		public void mouseClicked(MouseEvent aEvent) {
			if (aEvent.isPopupTrigger()) {
				System.out.println(aEvent.getX() + " " + aEvent.getY()
						+ " for " + cell);
			}
		}
	};

	@Override
	protected MouseListener createMouseListener() {
		return new MyMouseHandler();
	}

	@Override
	protected boolean startEditing(Object cell, MouseEvent event) {
		completeEditing();
		if (graph.isCellEditable(cell)) {
			CellView tmp = graphLayoutCache.getMapping(cell, false);
			cellEditor = tmp.getEditor();
			editingComponent = cellEditor.getGraphCellEditorComponent(graph,
					cell, graph.isCellSelected(cell));
			if (cellEditor.isCellEditable(event)) {

				editingCell = cell;

				editingCell = cell;
				editingComponent.validate();

				if (cellEditor.shouldSelectCell(event)
						&& graph.isSelectionEnabled()) {
					stopEditingInCompleteEditing = false;
					try {
						graph.setSelectionCell(cell);
					} catch (Exception e) {
						System.err.println("Editing exception: " + e);
					}
					stopEditingInCompleteEditing = true;
				}

				if (event instanceof MouseEvent) {
					/*
					 * Find the component that will get forwarded all the mouse
					 * events until mouseReleased.
					 */
					Point componentPoint = SwingUtilities.convertPoint(graph,
							new Point(event.getX(), event.getY()),
							editingComponent);

					/*
					 * Create an instance of BasicTreeMouseListener to handle
					 * passing the mouse/motion events to the necessary
					 * component.
					 */
					// We really want similiar behavior to getMouseEventTarget,
					// but it is package private.
					Component activeComponent = SwingUtilities
							.getDeepestComponentAt(editingComponent,
									componentPoint.x, componentPoint.y);
					if (activeComponent != null) {
						new MouseInputHandler(graph, activeComponent, event);
					}
				}

				BaseEditor theDialog = (BaseEditor) editingComponent;
				if (theDialog.showModal() == BaseEditor.MODAL_RESULT_OK) {
					try {
						theDialog.applyValues();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				editingComponent = null;

				graph.invalidate();
				graph.repaint();

				return false;
			} else {
				editingComponent = null;
			}
		}
		return false;
	}

	@Override
	public boolean isEditing(JGraph graph) {
		return false;
	}
}

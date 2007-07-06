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
import java.util.List;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.event.CellEditorListener;

import org.jgraph.JGraph;
import org.jgraph.graph.GraphCellEditor;

/**
 * The base class for all editing dialogs.
 * 
 * @author Mirko Sertic
 */
public class BaseEditor extends JDialog implements DialogConstants,
		GraphCellEditor {

	private int modalResult;

	private javax.swing.JPanel jContentPane = null;

	protected JFrame parent;

	private List<CellEditorListener> listener = new Vector<CellEditorListener>();

	private Object currentValue;

	public BaseEditor() {
		this(null);
	}

	/**
	 * Initialize.
	 * 
	 * @param parent
	 *            the parent Frame
	 */
	public BaseEditor(JFrame parent) {
		super(parent, true);
		initialize();
		this.parent = parent;
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(300, 200);
		this.setContentPane(getJContentPane());
		this.setResizable(false);
		this.setModal(true);
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new javax.swing.JPanel();
			jContentPane.setLayout(new java.awt.BorderLayout());
		}
		return jContentPane;
	}

	/**
	 * Set the dialogs modal result and hide it.
	 * 
	 * @param modalResult
	 *            the modal result.
	 */
	public void setModalResult(int modalResult) {
		this.modalResult = modalResult;
		this.hide();
	}

	public int showModal() {
		this.modalResult = DialogConstants.MODAL_RESULT_CANCEL;
		setVisible(true);
		return this.modalResult;
	}

	public void setVisible(boolean aStatus) {

		if (this.parent != null) {

			validate();

			Dimension w2 = getSize();
			Dimension w1 = parent.getSize();

			Point thePoint = parent.getLocation();
			setLocation(thePoint.x + w1.width / 2 - w2.width / 2, thePoint.y
					+ w1.height / 2 - w2.height / 2);

		}

		super.setVisible(true);
	}

	public Component getGraphCellEditorComponent(JGraph aGraph, Object aValue,
			boolean arg2) {

		JFrame theParent = (JFrame) SwingUtilities.getRoot(aGraph);

		validate();

		Dimension w2 = getSize();
		Dimension w1 = theParent.getSize();

		Point thePoint = theParent.getLocation();
		setLocation(thePoint.x + w1.width / 2 - w2.width / 2, thePoint.y
				+ w1.height / 2 - w2.height / 2);

		System.out.println("Editing value " + aValue);

		currentValue = aValue;
		setVisible(true);

		return new JLabel();
	}

	public void addCellEditorListener(CellEditorListener aListener) {
		listener.add(aListener);
	}

	public void removeCellEditorListener(CellEditorListener aListener) {
		listener.remove(aListener);
	}

	public void cancelCellEditing() {
		setVisible(false);
	}

	public Object getCellEditorValue() {
		System.out.println("Returning value");
		return currentValue;
	}

	public boolean isCellEditable(EventObject anEvent) {
		return true;
	}

	public boolean shouldSelectCell(EventObject anEvent) {
		return true;
	}

	public boolean stopCellEditing() {
		return false;
	}
}

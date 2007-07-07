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
package de.mogwai.erdesignerng.visual.editor.relation;

import javax.swing.JFrame;

import de.mogwai.erdesignerng.model.Model;
import de.mogwai.erdesignerng.model.Relation;
import de.mogwai.erdesignerng.visual.editor.BaseEditor;
import de.mogwai.erdesignerng.visual.editor.DialogConstants;

/**
 * @author Mirko Sertic
 */
public class RelationEditor extends BaseEditor {

	private Model m_model;

	private Relation m_relation;

	private RelationEditorView m_view;

	/**
	 * @param parent
	 */
	public RelationEditor(JFrame aParent) {
		super(aParent);

		initialize();
	}

	/**
	 * This method initializes this.
	 */
	private void initialize() {

		m_view = new RelationEditorView() {

			public void handleOKButtonActionPerformed(String actionCommand) {
				handleClose();
			}

			public void handleCancelButtonActionPerformed(String actionCommand) {
				setModalResult(MODAL_RESULT_CANCEL);
			}

		};

		setContentPane(m_view);

		setTitle("Relation editor");
		setResizable(false);
		pack();

	}

	private void handleClose() {

		setModalResult(DialogConstants.MODAL_RESULT_OK);
	}

	public void applyChanges(Relation aRelation) {
	}

}

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

import de.mogwai.binding.BindingInfo;
import de.mogwai.erdesignerng.model.Model;
import de.mogwai.erdesignerng.model.Relation;
import de.mogwai.erdesignerng.visual.editor.BaseEditor;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2007-07-08 10:06:40 $
 */
public class RelationEditor extends BaseEditor {

	private Model model;

	private BindingInfo<Relation> bindingInfo = new BindingInfo<Relation>();

	private RelationEditorView editingView;

	/**
	 * @param parent
	 */
	public RelationEditor(Model aModel, JFrame aParent) {
		super(aParent);

		initialize();
		
		model = aModel;
		
		bindingInfo.addBinding("name", editingView.getRelationname(),true);
		bindingInfo.configure();
	}

	/**
	 * This method initializes this.
	 */
	private void initialize() {

		editingView = new RelationEditorView() {

			public void handleOKButtonActionPerformed(String actionCommand) {
				commandOk();
			}

			public void handleCancelButtonActionPerformed(String actionCommand) {
				setModalResult(MODAL_RESULT_CANCEL);
			}

		};

		setContentPane(editingView);

		setTitle("Relation editor");
		setResizable(false);
		pack();

	}
	
	public void initializeFor(Relation aRelation) {
		bindingInfo.setDefaultModel(aRelation);
		bindingInfo.model2view();
	}

	private void commandOk() {
		if (bindingInfo.validate().size() == 0) {
			setModalResult(MODAL_RESULT_OK);
		}
	}

	@Override
	public void applyValues() throws Exception {
		Relation theRelation = bindingInfo.getDefaultModel();
		bindingInfo.view2model();

		if (!model.getRelations().contains(theRelation)) {
			model.addRelation(theRelation);
		}
		
	}
}

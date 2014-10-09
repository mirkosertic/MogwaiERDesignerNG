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
package de.erdesignerng.visual.editor.comment;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.model.Comment;
import de.erdesignerng.model.Model;
import de.erdesignerng.visual.editor.BaseEditor;
import de.mogwai.common.client.binding.BindingInfo;
import de.mogwai.common.client.looks.UIInitializer;

import java.awt.*;

/**
 * Editor for comments.
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-13 15:40:33 $
 */
public class CommentEditor extends BaseEditor {

	private final BindingInfo<Comment> bindingInfo = new BindingInfo<>();

	private CommentEditorView editingView;

	private final Model model;

	/**
	 * Create a relation editor.
	 * 
	 * @param aModel
	 *			the db model
	 * @param aParent
	 *			the parent container
	 */
	public CommentEditor(Model aModel, Component aParent) {
		super(aParent, ERDesignerBundle.COMMENTEDITOR);

		initialize();

		model = aModel;

		bindingInfo.addBinding("comment", editingView.getComment(), true);
		bindingInfo.configure();
	}

	/**
	 * This method initializes this.
	 */
	private void initialize() {

		editingView = new CommentEditorView();
		editingView.getOKButton().setAction(okAction);
		editingView.getCancelButton().setAction(cancelAction);

		setContentPane(editingView);
		setResizable(false);

		pack();

		UIInitializer.getInstance().initialize(this);
	}

	public void initializeFor(Comment aArea) {

		bindingInfo.setDefaultModel(aArea);
		bindingInfo.model2view();
	}

	@Override
	protected void commandOk() {
		if (bindingInfo.validate().isEmpty()) {
			setModalResult(MODAL_RESULT_OK);
		}
	}

	@Override
	public void applyValues() throws Exception {

		Comment theComment = bindingInfo.getDefaultModel();

		bindingInfo.view2model();
		if (!model.getComments().contains(theComment)) {
			model.addComment(theComment);
		}
	}
}
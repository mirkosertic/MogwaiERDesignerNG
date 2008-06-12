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

import java.awt.Component;
import java.awt.event.ActionEvent;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.model.Comment;
import de.erdesignerng.model.Model;
import de.erdesignerng.visual.editor.BaseEditor;
import de.mogwai.common.client.binding.BindingInfo;
import de.mogwai.common.client.looks.UIInitializer;
import de.mogwai.common.client.looks.components.action.ActionEventProcessor;
import de.mogwai.common.client.looks.components.action.DefaultAction;

/**
 * Editor for comments.
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2008-06-12 20:15:01 $
 */
public class CommentEditor extends BaseEditor {

    private BindingInfo<Comment> bindingInfo = new BindingInfo<Comment>();

    private CommentEditorView editingView;
    
    private Model model;

    private DefaultAction okAction = new DefaultAction(new ActionEventProcessor() {

        public void processActionEvent(ActionEvent e) {
            commandOk();
        }
    }, this, ERDesignerBundle.OK);

    private DefaultAction cancelAction = new DefaultAction(new ActionEventProcessor() {

        public void processActionEvent(ActionEvent e) {
            commandCancel();
        }
    }, this, ERDesignerBundle.CANCEL);

    /**
     * Create a relation editor.
     * 
     * @param aModel the db model
     * @param aParent
     *            the parent container
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

    private void commandOk() {
        if (bindingInfo.validate().size() == 0) {
            setModalResult(MODAL_RESULT_OK);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void applyValues() throws Exception {
        
        Comment theComment = bindingInfo.getDefaultModel();
        
        bindingInfo.view2model();
        if (!model.getComments().contains(theComment)) {
            model.addComment(theComment);
        }
    }
}
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
package de.erdesignerng.visual.editor.subjectarea;

import java.awt.Component;
import java.awt.event.ActionEvent;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.model.SubjectArea;
import de.erdesignerng.visual.editor.BaseEditor;
import de.mogwai.common.client.binding.BindingInfo;
import de.mogwai.common.client.looks.UIInitializer;
import de.mogwai.common.client.looks.components.action.ActionEventProcessor;
import de.mogwai.common.client.looks.components.action.DefaultAction;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-06-12 20:15:05 $
 */
public class SubjectAreaEditor extends BaseEditor {

    private BindingInfo<SubjectArea> bindingInfo = new BindingInfo<SubjectArea>();

    private SubjectAreaEditorView editingView;

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
     * @param aParent
     *            the parent container
     */
    public SubjectAreaEditor(Component aParent) {
        super(aParent, ERDesignerBundle.SUBJECTAREAEDITOR);

        initialize();

        bindingInfo.addBinding("name", editingView.getSubjectAreaName(), true);

        bindingInfo.configure();
    }

    /**
     * This method initializes this.
     */
    private void initialize() {

        editingView = new SubjectAreaEditorView();
        editingView.getOKButton().setAction(okAction);
        editingView.getCancelButton().setAction(cancelAction);

        setContentPane(editingView);
        setResizable(false);

        pack();

        UIInitializer.getInstance().initialize(this);
    }

    public void initializeFor(SubjectArea aArea) {
        
        editingView.getColorPanel().setBackground(aArea.getColor());
        
        bindingInfo.setDefaultModel(aArea);
        bindingInfo.model2view();
    }

    private void commandOk() {
        if (bindingInfo.validate().size() == 0) {
            
            SubjectArea theArea = bindingInfo.getDefaultModel();
            theArea.setColor(editingView.getColorPanel().getBackground());
            setModalResult(MODAL_RESULT_OK);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void applyValues() throws Exception {
        bindingInfo.view2model();
    }
}

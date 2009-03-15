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
package de.erdesignerng.visual.editor.convertmodel;

import java.awt.Component;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.Relation;
import de.erdesignerng.visual.editor.BaseEditor;
import de.mogwai.common.client.binding.BindingInfo;
import de.mogwai.common.client.looks.UIInitializer;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-13 15:40:33 $
 */
public class ConvertModelEditor extends BaseEditor {

    private Model model;

    private BindingInfo<Relation> bindingInfo = new BindingInfo<Relation>();

    private ConvertModelEditorView editingView;

    /**
     * Create a relation editor.
     * 
     * @param aModel
     *            the model
     * @param aParent
     *            the parent container
     */
    public ConvertModelEditor(Model aModel, Component aParent) {
        super(aParent, ERDesignerBundle.CONVERTMODEL);

        initialize();

        model = aModel;

        bindingInfo.addBinding("name", editingView.getTargetDataType(), true);

        bindingInfo.configure();
    }

    /**
     * This method initializes this.
     */
    private void initialize() {

        editingView = new ConvertModelEditorView();
        editingView.getOKButton().setAction(okAction);
        editingView.getCancelButton().setAction(cancelAction);

        setContentPane(editingView);
        setResizable(false);

        pack();

        UIInitializer.getInstance().initialize(this);
    }

    public void initializeFor(Relation aRelation) {
        bindingInfo.setDefaultModel(aRelation);
        bindingInfo.model2view();
    }

    @Override
    protected void commandOk() {
        if (bindingInfo.validate().size() == 0) {
            setModalResult(MODAL_RESULT_OK);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void applyValues() throws Exception {
        Relation theRelation = bindingInfo.getDefaultModel();

        if (!model.getRelations().contains(theRelation)) {

            bindingInfo.view2model();
            model.addRelation(theRelation);

        } else {

            Relation theTempRelation = theRelation.clone();
            bindingInfo.setDefaultModel(theTempRelation);
            bindingInfo.view2model();

            if (theRelation.isModified(theTempRelation, false)) {
                model.changeRelation(theRelation, theTempRelation);
            }
        }
    }
}

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
package de.erdesignerng.visual.editor.view;

import java.awt.Component;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.exception.ElementAlreadyExistsException;
import de.erdesignerng.exception.ElementInvalidNameException;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.View;
import de.erdesignerng.modificationtracker.VetoException;
import de.erdesignerng.util.SQLUtils;
import de.erdesignerng.visual.editor.BaseEditor;
import de.mogwai.common.client.binding.BindingInfo;
import de.mogwai.common.client.looks.UIInitializer;

/**
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-13 15:40:33 $
 */
public class ViewEditor extends BaseEditor {

    private Model model;

    private ViewEditorView editingView;

    private BindingInfo<View> viewBindingInfo = new BindingInfo<View>();

    public ViewEditor(Model aModel, Component aParent) {
        super(aParent, ERDesignerBundle.VIEWEDITOR);

        initialize();

        // Connection initialisieren

        model = aModel;

        viewBindingInfo.addBinding("name", editingView.getEntityName(), true);
        viewBindingInfo.addBinding("sql", editingView.getSqlText(), true);
        viewBindingInfo.addBinding("comment", editingView.getEntityComment());
        viewBindingInfo.configure();

        UIInitializer.getInstance().initialize(this);
    }

    /**
     * This method initializes this.
     */
    private void initialize() {

        editingView = new ViewEditorView();
        editingView.getOkButton().setAction(okAction);
        editingView.getCancelButton().setAction(cancelAction);

        setContentPane(editingView);

        pack();
    }

    public void initializeFor(View aView) {

        viewBindingInfo.setDefaultModel(aView);
        viewBindingInfo.model2view();
    }

    @Override
    protected void commandOk() {
        if (viewBindingInfo.validate().size() == 0) {

            try {
                // Test if every expression has an assigned alias
                SQLUtils.updateViewAttributesFromSQL(new View(), editingView.getSqlText().getText());

                setModalResult(MODAL_RESULT_OK);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void applyValues() throws ElementAlreadyExistsException, ElementInvalidNameException, VetoException {

        View theView = viewBindingInfo.getDefaultModel();

        viewBindingInfo.view2model();

        theView.getAttributes().clear();

        try {
            SQLUtils.updateViewAttributesFromSQL(theView, editingView.getSqlText().getText());
        } catch (Exception e) {
            // This exception is checked in commandOk before
        }

        if (!model.getViews().contains(theView)) {

            model.addView(theView);

        } else {

            model.changeView(theView);
        }
    }
}
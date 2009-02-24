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
import java.awt.event.ActionEvent;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.exception.ElementAlreadyExistsException;
import de.erdesignerng.exception.ElementInvalidNameException;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.View;
import de.erdesignerng.modificationtracker.VetoException;
import de.erdesignerng.plugins.sqleonardo.ERConnection;
import de.erdesignerng.plugins.sqleonardo.SQLParser;
import de.erdesignerng.plugins.sqleonardo.SQLUtils;
import de.erdesignerng.visual.editor.BaseEditor;
import de.mogwai.common.client.binding.BindingInfo;
import de.mogwai.common.client.looks.UIInitializer;
import de.mogwai.common.client.looks.components.action.ActionEventProcessor;
import de.mogwai.common.client.looks.components.action.DefaultAction;

/**
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2009-02-24 19:36:28 $
 */
public class ViewEditor extends BaseEditor {

    private Model model;

    private ViewEditorView editingView;

    private BindingInfo<View> viewBindingInfo = new BindingInfo<View>();

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

    public ViewEditor(Model aModel, Component aParent) {
        super(aParent, ERDesignerBundle.ENTITYEDITOR);

        initialize();

        // Connection initialisieren
        editingView.getBuilder().setConnection(new ERConnection(aModel));

        model = aModel;

        viewBindingInfo.addBinding("name", editingView.getEntityName(), true);
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

        UIInitializer.getInstance().initialize(editingView.getBuilder());

        pack();
    }

    public void initializeFor(View aView) {

        viewBindingInfo.setDefaultModel(aView);
        viewBindingInfo.model2view();

        editingView.getEntityName().setName(aView.getName());
        if (!StringUtils.isEmpty(aView.getSql())) {
            try {
                editingView.getBuilder().setQueryModel(SQLParser.toQueryModel(aView.getSql()));
            } catch (IOException e) {
                logFatalError(e);
            }
        }
    }

    private void commandOk() {
        if (viewBindingInfo.validate().size() == 0) {

            try {

                // Test if every expression has an assigned alias
                SQLUtils.updateViewAttributesFromQueryModel(new View(), editingView.getBuilder().getQueryModel());

                setModalResult(MODAL_RESULT_OK);
            } catch (Exception e) {
                // Handle error here
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

        theView.getAttributes().clear();

        try {
            SQLUtils.updateViewAttributesFromQueryModel(new View(), editingView.getBuilder().getQueryModel());
        } catch (Exception e) {
            // This exception is checked in commandOk before
        }

        theView.setSql(editingView.getBuilder().getQueryModel().toString(true));
        System.out.println("Current SQL : " + theView.getSql());

        if (!model.getViews().contains(theView)) {

            viewBindingInfo.view2model();

            model.addView(theView);

        } else {
            // The table exists already in the model
            View theTempTable = new View();

            viewBindingInfo.setDefaultModel(theTempTable);
            viewBindingInfo.view2model();

            model.changeView(theView);
        }
    }
}
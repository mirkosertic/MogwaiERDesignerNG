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
import java.io.IOException;

import nickyb.sqleonardo.querybuilder.QueryBuilder;
import nickyb.sqleonardo.querybuilder.QueryModel;
import nickyb.sqleonardo.querybuilder.syntax.SQLParser;

import org.apache.commons.lang.StringUtils;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.exception.ElementAlreadyExistsException;
import de.erdesignerng.exception.ElementInvalidNameException;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.View;
import de.erdesignerng.modificationtracker.VetoException;
import de.erdesignerng.plugins.sqleonardo.ERConnection;
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
        editingView.getBuilder().setConnection(new ERConnection(aModel));
        QueryBuilder.autoJoin = false;

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

            String theSQL = aView.getSql();
            
            System.out.println("Entering for SQL " + theSQL);
            try {
                QueryModel theModel = SQLParser.toQueryModel(theSQL);
                editingView.getBuilder().setQueryModel(theModel);
            } catch (IOException e) {
                logFatalError(e);
            }
        }
    }

    @Override
    protected void commandOk() {
        if (viewBindingInfo.validate().size() == 0) {

            /*
             * try {
             *  // Test if every expression has an assigned alias
             * SQLUtils.updateViewAttributesFromQueryModel(new View(),
             * editingView.getBuilder().getQueryModel());
             * 
             * setModalResult(MODAL_RESULT_OK); } catch (Exception e) { //
             * Handle error here e.printStackTrace(); }
             */

            setModalResult(MODAL_RESULT_OK);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void applyValues() throws ElementAlreadyExistsException, ElementInvalidNameException, VetoException {

        View theView = viewBindingInfo.getDefaultModel();

        theView.getAttributes().clear();

        /*
         * try { SQLUtils.updateViewAttributesFromQueryModel(new View(),
         * editingView.getBuilder().getQueryModel()); } catch (Exception e) { //
         * This exception is checked in commandOk before }
         */

        theView.setSql(editingView.getBuilder().getQueryModel().toString(false));
        System.out.println("Current SQL : " + theView.getSql());

        viewBindingInfo.view2model();

        if (!model.getViews().contains(theView)) {

            model.addView(theView);

        } else {

            model.changeView(theView);
        }
    }
}
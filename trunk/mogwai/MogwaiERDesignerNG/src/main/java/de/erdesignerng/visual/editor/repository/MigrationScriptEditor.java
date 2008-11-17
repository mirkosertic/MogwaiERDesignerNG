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
package de.erdesignerng.visual.editor.repository;

import java.awt.Component;
import java.awt.event.ActionEvent;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.visual.editor.BaseEditor;
import de.erdesignerng.visual.editor.DialogConstants;
import de.mogwai.common.client.binding.BindingInfo;
import de.mogwai.common.client.looks.UIInitializer;
import de.mogwai.common.client.looks.components.action.ActionEventProcessor;
import de.mogwai.common.client.looks.components.action.DefaultAction;

/**
 * Editor to save models to a repository.
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2008-11-17 20:53:55 $
 */
public class MigrationScriptEditor extends BaseEditor {

    private DefaultAction okAction = new DefaultAction(new ActionEventProcessor() {

        public void processActionEvent(ActionEvent e) {
            commandClose();
        }
    }, this, ERDesignerBundle.OK);

    private DefaultAction cancelAction = new DefaultAction(new ActionEventProcessor() {

        public void processActionEvent(ActionEvent e) {
            commandCancel();
        }
    }, this, ERDesignerBundle.CANCEL);

    private MigrationScriptView view = new MigrationScriptView();

    private BindingInfo<MigrationScriptDataModel> bindingInfo = new BindingInfo<MigrationScriptDataModel>(
            new MigrationScriptDataModel());

    public MigrationScriptEditor(Component aParent) {
        super(aParent, ERDesignerBundle.CREATEMIGRATIONSCRIPT);

        initialize();

        bindingInfo.addBinding("sourceChange", view.getSourceVersion(), true);
        bindingInfo.addBinding("destinationChange", view.getDestinationVersion(), true);
        bindingInfo.configure();
    }

    private void initialize() {

        view.getOkButton().setAction(okAction);
        view.getCancelButton().setAction(cancelAction);

        setContentPane(view);
        setResizable(false);

        pack();

        UIInitializer.getInstance().initialize(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void applyValues() throws Exception {
    }

    private void commandClose() {

        setModalResult(DialogConstants.MODAL_RESULT_OK);
    }
}
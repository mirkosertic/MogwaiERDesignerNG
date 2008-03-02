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
package de.erdesignerng.visual.editor.reverseengineer;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.ListSelectionModel;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.dialect.ReverseEngineeringOptions;
import de.erdesignerng.dialect.TableEntry;
import de.erdesignerng.visual.editor.BaseEditor;
import de.mogwai.common.client.binding.BindingInfo;
import de.mogwai.common.client.looks.UIInitializer;
import de.mogwai.common.client.looks.components.DefaultCheckBoxListModel;
import de.mogwai.common.client.looks.components.action.ActionEventProcessor;
import de.mogwai.common.client.looks.components.action.DefaultAction;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-03-02 13:44:51 $
 */
public class TablesSelectEditor extends BaseEditor {

    private BindingInfo<ReverseEngineeringOptions> bindingInfo = new BindingInfo<ReverseEngineeringOptions>(
            new ReverseEngineeringOptions());

    private TablesSelectEditorView editingView;

    private DefaultCheckBoxListModel<TableEntry> tableList;

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
     * Create a table selection editor.
     * 
     * @param aParent
     *            the parent container
     * @param aOptions
     *            the options
     */
    public TablesSelectEditor(ReverseEngineeringOptions aOptions, Component aParent) {
        super(aParent, ERDesignerBundle.TABLESELECTION);

        initialize();

        bindingInfo.setDefaultModel(aOptions);
        tableList = editingView.getTableList().getModel();
        tableList.addAll(aOptions.getTableEntries());

        bindingInfo.addBinding("tableEntries", new TableEntryPropertyAdapter(editingView.getTableList(),
                null));

        bindingInfo.configure();
        bindingInfo.model2view();
    }

    /**
     * This method initializes this.
     */
    private void initialize() {

        editingView = new TablesSelectEditorView();
        editingView.getOkButton().setAction(okAction);
        editingView.getCancelButton().setAction(cancelAction);

        editingView.getTableList().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        setContentPane(editingView);
        setResizable(false);

        pack();
        
        UIInitializer.getInstance().initialize(this);
    }

    private void commandOk() {
        if (bindingInfo.validate().size() == 0) {
            bindingInfo.view2model();
            setModalResult(MODAL_RESULT_OK);
        }
    }

    @Override
    public void applyValues() throws Exception {
    }
}

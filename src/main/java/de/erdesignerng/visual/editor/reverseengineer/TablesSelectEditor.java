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
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ListSelectionModel;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.dialect.ReverseEngineeringOptions;
import de.erdesignerng.dialect.TableEntry;
import de.erdesignerng.visual.editor.BaseEditor;
import de.mogwai.common.client.binding.BindingInfo;
import de.mogwai.common.client.looks.UIInitializer;
import de.mogwai.common.client.looks.components.DefaultCheckBoxList;
import de.mogwai.common.client.looks.components.DefaultCheckBoxListModel;
import de.mogwai.common.client.looks.components.action.DefaultAction;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-13 15:40:33 $
 */
public class TablesSelectEditor extends BaseEditor {

    private final BindingInfo<ReverseEngineeringOptions> bindingInfo = new BindingInfo<ReverseEngineeringOptions>(
            new ReverseEngineeringOptions());

    private TablesSelectEditorView editingView;

    private final DefaultCheckBoxListModel<TableEntry> tableList;

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

        bindingInfo.addBinding("tableEntries", new TableEntryPropertyAdapter(editingView.getTableList(), null));

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

        DefaultAction theSelectAllAction = new DefaultAction(this, ERDesignerBundle.SELECTALL);
        theSelectAllAction.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultCheckBoxList theList = editingView.getTableList();
                theList.setSelectedItems(bindingInfo.getDefaultModel().getTableEntries());
            }
        });
        DefaultAction theDeselectAllAction = new DefaultAction(this, ERDesignerBundle.DESELECTALL);
        theDeselectAllAction.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultCheckBoxList theList = editingView.getTableList();
                theList.setSelectedItems(new ArrayList<TableEntry>());
            }
        });
        DefaultAction theInvertSelectionAction = new DefaultAction(this, ERDesignerBundle.INVERTSELECTION);
        theInvertSelectionAction.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultCheckBoxList theList = editingView.getTableList();
                List<TableEntry> theSelecion = theList.getSelectedItems();
                List<TableEntry> theNewSelection = new ArrayList<TableEntry>();
                for (TableEntry theEntry : bindingInfo.getDefaultModel().getTableEntries()) {
                    if (!theSelecion.contains(theEntry)) {
                        theNewSelection.add(theEntry);
                    }
                }
                theList.setSelectedItems(theNewSelection);
            }
        });

        editingView.getSelectAll().setAction(theSelectAllAction);
        editingView.getDeselectAll().setAction(theDeselectAllAction);
        editingView.getInvertSelection().setAction(theInvertSelectionAction);

        setContentPane(editingView);
        setResizable(false);

        pack();

        UIInitializer.getInstance().initialize(this);
    }

    @Override
    protected void commandOk() {
        if (bindingInfo.validate().size() == 0) {
            bindingInfo.view2model();
            setModalResult(MODAL_RESULT_OK);
        }
    }

    @Override
    public void applyValues() throws Exception {
    }
}
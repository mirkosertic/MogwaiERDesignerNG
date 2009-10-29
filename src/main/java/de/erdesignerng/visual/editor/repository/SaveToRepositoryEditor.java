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
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.model.serializer.repository.RepositoryEntryDesciptor;
import de.erdesignerng.visual.editor.BaseEditor;
import de.erdesignerng.visual.editor.DialogConstants;
import de.mogwai.common.client.binding.BindingInfo;
import de.mogwai.common.client.looks.UIInitializer;

/**
 * Editor to save models to a repository.
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-13 15:40:33 $
 */
public class SaveToRepositoryEditor extends BaseEditor {

    private SaveToRepositoryView view = new SaveToRepositoryView() {

        @Override
        public void commandChangeRepositoryEntry() {
            SaveToRepositoryEditor.this.commandChangeRepositoryEntry();
        }

    };

    private BindingInfo<SaveToRepositoryDataModel> bindingInfo1;

    private BindingInfo<SaveToRepositoryDataModel> bindingInfo2;

    public SaveToRepositoryEditor(Component aParent, List<RepositoryEntryDesciptor> aEntries,
            RepositoryEntryDesciptor aCurrentEntry) {
        super(aParent, ERDesignerBundle.SAVEMODELTODB);

        initialize();

        SaveToRepositoryDataModel theBindModel = new SaveToRepositoryDataModel();
        bindingInfo1 = new BindingInfo<SaveToRepositoryDataModel>(theBindModel);
        bindingInfo2 = new BindingInfo<SaveToRepositoryDataModel>(theBindModel);

        DefaultComboBoxModel theModel = new DefaultComboBoxModel();
        for (RepositoryEntryDesciptor theEntry : aEntries) {
            theModel.addElement(theEntry);
        }
        view.getExistingNameBox().setModel(theModel);
        if (theModel.getSize() > 0) {
            theBindModel.setExistingEntry((RepositoryEntryDesciptor) theModel.getElementAt(0));
            theBindModel.setNameForExistantEntry(theBindModel.getExistingEntry().getName());
        }

        if (aCurrentEntry == null) {
            view.getNewEntryButton().setSelected(true);
        } else {
            view.getExistingEntryButton().setSelected(true);
            theBindModel.setExistingEntry(aCurrentEntry);
            theBindModel.setNameForExistantEntry(aCurrentEntry.getName());
        }

        bindingInfo1.addBinding("nameForNewEntry", view.getNewNameField(), true);
        bindingInfo1.configure();

        bindingInfo2.addBinding("existingEntry", view.getExistingNameBox(), true);
        bindingInfo2.addBinding("nameForExistantEntry", view.getExistingNameField(), true);
        bindingInfo2.configure();

        bindingInfo2.model2view();
    }

    private void initialize() {

        ButtonGroup theGroup = new ButtonGroup();
        theGroup.add(view.getExistingEntryButton());
        theGroup.add(view.getNewEntryButton());

        view.getExistingEntryButton().setSelected(true);

        view.getOkButton().setAction(okAction);
        view.getCancelButton().setAction(cancelAction);

        setContentPane(view);
        setResizable(false);

        pack();

        UIInitializer.getInstance().initialize(this);
    }

    @Override
    public void applyValues() throws Exception {
    }

    private void commandChangeRepositoryEntry() {
        RepositoryEntryDesciptor theDesc = (RepositoryEntryDesciptor) view.getExistingNameBox().getSelectedItem();
        if (theDesc != null) {
            SaveToRepositoryDataModel theModel = bindingInfo1.getDefaultModel();
            theModel.setExistingEntry(theDesc);
            theModel.setNameForExistantEntry(theDesc.getName());
            bindingInfo2.model2view();
        }
    }

    @Override
    protected void commandOk() {

        if (view.getNewEntryButton().isSelected()) {
            if (bindingInfo1.validate().size() == 0) {
                bindingInfo1.view2model();
                setModalResult(DialogConstants.MODAL_RESULT_OK);
            }
        }

        if (view.getExistingEntryButton().isSelected()) {
            if (bindingInfo2.validate().size() == 0) {
                bindingInfo2.view2model();
                setModalResult(DialogConstants.MODAL_RESULT_OK);
            }
        }
    }

    /**
     * Create a repository descriptor for the selected entry.
     * 
     * @return the descriptor
     */
    public RepositoryEntryDesciptor getRepositoryDescriptor() {

        SaveToRepositoryDataModel theModel = bindingInfo1.getDefaultModel();

        if (view.getNewEntryButton().isSelected()) {
            RepositoryEntryDesciptor theDesc = new RepositoryEntryDesciptor();
            theDesc.setName(theModel.getNameForNewEntry());
            return theDesc;
        }

        RepositoryEntryDesciptor theDesc = theModel.getExistingEntry();
        theDesc.setName(theModel.getNameForExistantEntry());
        return theDesc;
    }
}
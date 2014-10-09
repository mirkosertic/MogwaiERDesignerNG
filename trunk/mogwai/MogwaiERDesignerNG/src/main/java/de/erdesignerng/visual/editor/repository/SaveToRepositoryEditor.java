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

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.model.serializer.repository.RepositoryEntryDescriptor;
import de.erdesignerng.visual.editor.BaseEditor;
import de.erdesignerng.visual.editor.DialogConstants;
import de.mogwai.common.client.binding.BindingInfo;
import de.mogwai.common.client.looks.UIInitializer;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Editor to save models to a repository.
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-13 15:40:33 $
 */
public class SaveToRepositoryEditor extends BaseEditor {

	private final SaveToRepositoryView view = new SaveToRepositoryView() {

		@Override
		public void commandChangeRepositoryEntry() {
			SaveToRepositoryEditor.this.commandChangeRepositoryEntry();
		}

	};

	private final BindingInfo<SaveToRepositoryDataModel> bindingInfo1;

	private final BindingInfo<SaveToRepositoryDataModel> bindingInfo2;

	public SaveToRepositoryEditor(Component aParent, List<RepositoryEntryDescriptor> aEntries,
			RepositoryEntryDescriptor aCurrentEntry) {
		super(aParent, ERDesignerBundle.SAVEMODELTODB);

		initialize();

		SaveToRepositoryDataModel theBindModel = new SaveToRepositoryDataModel();
		bindingInfo1 = new BindingInfo<>(theBindModel);
		bindingInfo2 = new BindingInfo<>(theBindModel);

		DefaultComboBoxModel theModel = new DefaultComboBoxModel();
        aEntries.forEach(theModel::addElement);
		view.getExistingNameBox().setModel(theModel);
		if (theModel.getSize() > 0) {
			theBindModel.setExistingEntry((RepositoryEntryDescriptor) theModel.getElementAt(0));
			theBindModel.setNameForExistingEntry(theBindModel.getExistingEntry().getName());
		}

		if (aCurrentEntry == null) {
			view.getNewEntryButton().setSelected(true);
		} else {
			view.getExistingEntryButton().setSelected(true);
			theBindModel.setExistingEntry(aCurrentEntry);
			theBindModel.setNameForExistingEntry(aCurrentEntry.getName());
		}

		bindingInfo1.addBinding("nameForNewEntry", view.getNewNameField(), true);
		bindingInfo1.configure();

		bindingInfo2.addBinding("existingEntry", view.getExistingNameBox(), true);
		bindingInfo2.addBinding("nameForExistingEntry", view.getExistingNameField(), true);
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
		RepositoryEntryDescriptor theDesc = (RepositoryEntryDescriptor) view.getExistingNameBox().getSelectedItem();
		if (theDesc != null) {
			SaveToRepositoryDataModel theModel = bindingInfo1.getDefaultModel();
			theModel.setExistingEntry(theDesc);
			theModel.setNameForExistingEntry(theDesc.getName());
			bindingInfo2.model2view();
		}
	}

	@Override
	protected void commandOk() {

		if (view.getNewEntryButton().isSelected()) {
			if (bindingInfo1.validate().isEmpty()) {
				bindingInfo1.view2model();
				setModalResult(DialogConstants.MODAL_RESULT_OK);
			}
		}

		if (view.getExistingEntryButton().isSelected()) {
			if (bindingInfo2.validate().isEmpty()) {
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
	public RepositoryEntryDescriptor getRepositoryDescriptor() {

		SaveToRepositoryDataModel theModel = bindingInfo1.getDefaultModel();

		if (view.getNewEntryButton().isSelected()) {
			RepositoryEntryDescriptor theDesc = new RepositoryEntryDescriptor();
			theDesc.setName(theModel.getNameForNewEntry());
			return theDesc;
		}

		RepositoryEntryDescriptor theDesc = theModel.getExistingEntry();
		theDesc.setName(theModel.getNameForExistingEntry());
		return theDesc;
	}
}
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
 * Editor to load models from a repository.
 *
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-13 15:40:33 $
 */
public class LoadFromRepositoryEditor extends BaseEditor {

	private final LoadFromRepositoryView view = new LoadFromRepositoryView();

	private final BindingInfo<LoadFromRepositoryDataModel> bindingInfo = new BindingInfo<>(
			new LoadFromRepositoryDataModel());

	public LoadFromRepositoryEditor(Component aParent,
									List<RepositoryEntryDescriptor> aEntries) {
		super(aParent, ERDesignerBundle.LOADMODELFROMDB);

		DefaultComboBoxModel theModel = new DefaultComboBoxModel();
        aEntries.forEach(theModel::addElement);
		view.getExistingNameBox().setModel(theModel);

		initialize();

		bindingInfo.addBinding("entry", view.getExistingNameBox(), true);
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

	@Override
	public void applyValues() throws Exception {
	}

	@Override
	protected void commandOk() {

		if (bindingInfo.validate().isEmpty()) {

			bindingInfo.view2model();

			setModalResult(DialogConstants.MODAL_RESULT_OK);
		}
	}

	/**
	 * Get the data model.
	 *
	 * @return the data model
	 */
	public LoadFromRepositoryDataModel getModel() {
		return bindingInfo.getDefaultModel();
	}
}

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

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.dialect.ReverseEngineeringOptions;
import de.erdesignerng.visual.editor.BaseEditor;
import de.mogwai.common.client.binding.BindingInfo;
import de.mogwai.common.client.looks.UIInitializer;
import de.mogwai.common.client.looks.components.action.DefaultAction;

import java.awt.*;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-13 15:40:33 $
 */
public class TablesSelectEditor extends BaseEditor {

	private final BindingInfo<ReverseEngineeringOptions> bindingInfo = new BindingInfo<>(
			new ReverseEngineeringOptions());

	private TablesSelectEditorView editingView;

	/**
	 * Create a table selection editor.
	 *
	 * @param aParent  the parent container
	 * @param aOptions the options
	 */
	public TablesSelectEditor(ReverseEngineeringOptions aOptions,
							  Component aParent) {
		super(aParent, ERDesignerBundle.TABLESELECTION);

		initialize();

		bindingInfo.setDefaultModel(aOptions);

		bindingInfo.addBinding("tableEntries", new TableEntryPropertyAdapter(
				editingView.getTableList()));

		bindingInfo.configure();
		bindingInfo.model2view();

		for (int i = 0; i < editingView.getTableList().getRowCount(); i++) {
			editingView.getTableList().expandRow(i);
		}
	}

	/**
	 * This method initializes this.
	 */
	private void initialize() {

		editingView = new TablesSelectEditorView();
		editingView.getOkButton().setAction(okAction);
		editingView.getCancelButton().setAction(cancelAction);

		DefaultAction theSelectAllAction = new DefaultAction(this,
				ERDesignerBundle.SELECTALL);
		theSelectAllAction.addActionListener(e -> {
            SelectableTableModel theModel = (SelectableTableModel) editingView
                    .getTableList().getModel();
            theModel.selectAll();
        });
		DefaultAction theDeselectAllAction = new DefaultAction(this,
				ERDesignerBundle.DESELECTALL);
		theDeselectAllAction.addActionListener(e -> {
            SelectableTableModel theModel = (SelectableTableModel) editingView
                    .getTableList().getModel();
            theModel.deselectAll();
        });
		DefaultAction theInvertSelectionAction = new DefaultAction(this,
				ERDesignerBundle.INVERTSELECTION);
		theInvertSelectionAction.addActionListener(e -> {
            SelectableTableModel theModel = (SelectableTableModel) editingView
                    .getTableList().getModel();
            theModel.invertSelection();
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
		if (bindingInfo.validate().isEmpty()) {
			bindingInfo.view2model();
			setModalResult(MODAL_RESULT_OK);
		}
	}

	@Override
	public void applyValues() throws Exception {
	}
}
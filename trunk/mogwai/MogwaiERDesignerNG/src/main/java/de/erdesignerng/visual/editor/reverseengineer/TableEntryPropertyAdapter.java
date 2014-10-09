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

import de.erdesignerng.dialect.ReverseEngineeringOptions;
import de.erdesignerng.dialect.TableEntry;
import de.erdesignerng.model.TableType;
import de.erdesignerng.util.SelectableWrapper;
import de.mogwai.common.client.binding.BindingBundle;
import de.mogwai.common.client.binding.PropertyAdapter;
import de.mogwai.common.client.binding.validator.ValidationError;
import de.mogwai.common.client.looks.components.DefaultTree;
import de.mogwai.common.i18n.ResourceHelper;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-03-02 13:44:51 $
 */
public class TableEntryPropertyAdapter extends PropertyAdapter {

	private final ResourceHelper helper = ResourceHelper
			.getResourceHelper(BindingBundle.BUNDLE_NAME);

	public TableEntryPropertyAdapter(DefaultTree aComponent) {
		super(aComponent, null);
	}

	@Override
	public void model2view(Object aModel, String aPropertyName) {

		DefaultTree theComponent = (DefaultTree) getComponent()[0];

		ReverseEngineeringOptions theModel = (ReverseEngineeringOptions) aModel;

		DefaultMutableTreeNode theRootNode = new DefaultMutableTreeNode();
		for (TableType theTableType : theModel.getAvailableTableTypes()) {
			SelectableWrapper<String> theWrapper = new SelectableWrapper<>(theTableType.toString());
			DefaultMutableTreeNode theNode = new DefaultMutableTreeNode(theWrapper);

            theModel.getTableEntries().stream().filter(theTableEntry -> theTableType.equals(theTableEntry.getTableType())).forEach(theTableEntry -> {

                SelectableWrapper<TableEntry> theWrapper2 = new SelectableWrapper<>(
                        theTableEntry);
                DefaultMutableTreeNode theNode2 = new DefaultMutableTreeNode(
                        theWrapper2);

                theNode.add(theNode2);
            });

			theRootNode.add(theNode);
		}

		theComponent.setModel(new SelectableTableModel(theRootNode));
	}

	@Override
	public void view2model(Object aModel, String aPropertyName) {

		ReverseEngineeringOptions theDataModel = (ReverseEngineeringOptions) aModel;

		SelectableTableModel theModel = (SelectableTableModel) ((DefaultTree) getComponent()[0])
				.getModel();

		Collection theSelectedEntries = theModel.getSelectedEntries();
		theDataModel.getTableEntries().clear();
		theDataModel.getTableEntries().addAll(theSelectedEntries);
	}

	@Override
	public List<ValidationError> validate() {
		List<ValidationError> theResult = new ArrayList<>();

		SelectableTableModel theModel = (SelectableTableModel) ((DefaultTree) getComponent()[0])
				.getModel();

		Collection theSelectedEntries = theModel.getSelectedEntries();

		if (theSelectedEntries.isEmpty()) {
			theResult.add(new ValidationError(this, helper
					.getText(BindingBundle.MISSINGREQUIREDFIELD)));
		}
		if (theResult.size() > 0) {
			markInvalid(theResult);
		} else {
			markValid();
		}

		return theResult;
	}
}

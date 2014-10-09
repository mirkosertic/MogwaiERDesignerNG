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
import de.erdesignerng.dialect.JDBCReverseEngineeringStrategy;
import de.erdesignerng.dialect.ReverseEngineeringOptions;
import de.erdesignerng.dialect.SchemaEntry;
import de.erdesignerng.dialect.TableNamingEnum;
import de.erdesignerng.model.Model;
import de.erdesignerng.visual.MessagesHelper;
import de.erdesignerng.visual.editor.BaseEditor;
import de.mogwai.common.client.binding.BindingInfo;
import de.mogwai.common.client.binding.adapter.ComboboxModelAdapter;
import de.mogwai.common.client.looks.UIInitializer;
import de.mogwai.common.client.looks.components.action.DefaultAction;
import de.mogwai.common.client.looks.components.list.DefaultListModel;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.util.List;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-13 15:40:33 $
 */
public class ReverseEngineerEditor extends BaseEditor {

	private final Model model;

	private final BindingInfo<ReverseEngineerDataModel> bindingInfo = new BindingInfo<>(
			new ReverseEngineerDataModel());

	private ReverseEngineerView editingView;

	private final DefaultListModel<SchemaEntry> schemaList;

	private final DefaultAction updateAction = new DefaultAction(
            e -> commandUpdate(), this, ERDesignerBundle.UPDATE);

	/**
	 * Create a reverse engineering editor.
	 * 
	 * @param aModel
	 *			the model
	 * @param aParent
	 *			the parent container
	 */
	public ReverseEngineerEditor(Model aModel, Component aParent) {
		super(aParent, ERDesignerBundle.REVERSEENGINEER);

		model = aModel;

		initialize();

		ReverseEngineerDataModel theModel = bindingInfo.getDefaultModel();

		theModel.getTableOptions().add(
				new NameValuePair(TableNamingEnum.STANDARD, getResourceHelper()
						.getText(ERDesignerBundle.STANDARD)));
		theModel.getTableOptions().add(
				new NameValuePair(TableNamingEnum.INCLUDE_SCHEMA,
						getResourceHelper().getText(
								ERDesignerBundle.INCLUDESCHEMAINNAME)));

		bindingInfo.addBinding("tableGenerator", editingView.getNaming(), true);
		bindingInfo.addBinding("tableOptions", new ComboboxModelAdapter(
				editingView.getNaming()));

		schemaList = editingView.getSchemaList().getModel();

		bindingInfo.configure();
		bindingInfo.model2view();

		// Bug Fixing 2876904 [ERDesignerNG] ReverseEng dialog does'nt show
		// avail. schemas
		if (model.getDialect().isSupportsSchemaInformation()) {
			// initially show available schemas
			commandUpdate();

			// Bug Fixing 2899094 [ERDesignerNG] select first *non-system*
			// schema in RevEngEd
			// initially preselect first *non-system* schema if possible
			if (schemaList.getSize() > 0) {
				List<String> systemSchemas = model.getDialect().getSystemSchemas();
				Integer selectedIndex = null;
				int theAlternativeIndex = 0;

				if (systemSchemas != null) {
					int i = 0;
					while ((i < schemaList.getSize()) && (selectedIndex == null)) {
						boolean isSystemSchema = false;
						int j = 0;
						while ((j < systemSchemas.size()) && (!isSystemSchema)) {
							if (schemaList.get(i).getSchemaName().equals(model.getDialect().getDefaultSchemaName())) {
								theAlternativeIndex = i;
								isSystemSchema = true;
							} else {
								isSystemSchema = schemaList.get(i).getSchemaName().equals(systemSchemas.get(j));
							}

							j++;
						}

						if (!isSystemSchema) {
							selectedIndex = i;
						}

						i++;
					}
				}

				if (selectedIndex == null) {
					selectedIndex = theAlternativeIndex;
				}

				editingView.getSchemaList().setSelectedIndex(selectedIndex);
			}
		}

		// initially preselect first table-generation item, if possible
		if (editingView.getNaming().getModel().getSize() > 0) {
			editingView.getNaming().setSelectedItem(
					editingView.getNaming().getModel().getElementAt(0));
		}
	}

	/**
	 * This method initializes this.
	 */
	private void initialize() {

		editingView = new ReverseEngineerView();
		editingView.getStartButton().setAction(okAction);
		editingView.getCancelButton().setAction(cancelAction);
		editingView.getRefreshButton().setAction(updateAction);
		editingView.getSchemaList().setSelectionMode(
				ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		editingView.getSchemaList().setEnabled(
				model.getDialect().isSupportsSchemaInformation());
		editingView.getRefreshButton().setEnabled(
				model.getDialect().isSupportsSchemaInformation());

		setContentPane(editingView);
		setResizable(false);

		pack();

		UIInitializer.getInstance().initialize(this);
	}

	@Override
	protected void commandOk() {
		if (bindingInfo.validate().isEmpty()) {
			List<Object> theSelectedValues = editingView.getSchemaList()
					.getSelectedValuesList();
			if (((theSelectedValues == null) || (theSelectedValues.size() == 0))
					&& (model.getDialect().isSupportsSchemaInformation())) {
				MessagesHelper.displayErrorMessage(this, getResourceHelper()
						.getText(ERDesignerBundle.CHOOSEONESCHEMA));
				return;
			}
			setModalResult(MODAL_RESULT_OK);
		}
	}

	private void commandUpdate() {

		if (model.getDialect().isSupportsSchemaInformation()) {

			schemaList.clear();

			Connection theConnection = null;
			try {
				theConnection = model.createConnection();
				if (theConnection == null) {
					return;
				}

				schemaList.clear();

				JDBCReverseEngineeringStrategy theStrategy = model.getDialect()
						.getReverseEngineeringStrategy();

				List<SchemaEntry> theEntries = theStrategy
						.getSchemaEntries(theConnection);
                theEntries.forEach(schemaList::add);

			} catch (Exception e) {
				MessagesHelper.displayErrorMessage(this, e.getMessage());
			} finally {
				if (!model.getDialect().generatesManagedConnection()) {
					if (theConnection != null) {
						try {
							theConnection.close();
						} catch (Exception e) {
							// Nothing will happen here
						}
					}
				}
			}
		} else {
			// Here happens nothing :-)
		}
	}

	public ReverseEngineeringOptions createREOptions() {
		ReverseEngineerDataModel theModel = bindingInfo.getDefaultModel();
		bindingInfo.view2model();

		ReverseEngineeringOptions theOptions = new ReverseEngineeringOptions();
		theOptions.setTableNaming((TableNamingEnum) theModel
				.getTableGenerator().getValue());

		if (model.getDialect().isSupportsSchemaInformation()) {
			for (Object theEntry : editingView.getSchemaList()
					.getSelectedValuesList()) {
				theOptions.getSchemaEntries().add((SchemaEntry) theEntry);
			}
		}

		return theOptions;
	}

	@Override
	public void applyValues() throws Exception {
	}
}

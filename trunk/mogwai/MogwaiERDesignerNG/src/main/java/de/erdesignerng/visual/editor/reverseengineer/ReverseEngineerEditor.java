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
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

import javax.swing.ListSelectionModel;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.dialect.DefaultValueNamingEnum;
import de.erdesignerng.dialect.DomainNamingEnum;
import de.erdesignerng.dialect.ReverseEngineeringOptions;
import de.erdesignerng.dialect.TableNamingEnum;
import de.erdesignerng.model.Model;
import de.erdesignerng.util.ApplicationPreferences;
import de.erdesignerng.visual.editor.BaseEditor;
import de.mogwai.common.client.binding.BindingInfo;
import de.mogwai.common.client.binding.adapter.ComboboxModelAdapter;
import de.mogwai.common.client.looks.UIInitializer;
import de.mogwai.common.client.looks.components.action.ActionEventProcessor;
import de.mogwai.common.client.looks.components.action.DefaultAction;
import de.mogwai.common.client.looks.components.list.DefaultListModel;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-01-11 18:40:39 $
 */
public class ReverseEngineerEditor extends BaseEditor {

	private Model model;

	private BindingInfo<ReverseEngineerDataModel> bindingInfo = new BindingInfo<ReverseEngineerDataModel>(
			new ReverseEngineerDataModel());

	private ReverseEngineerView editingView;

	private ApplicationPreferences preferences;

	private DefaultListModel<String> schemaList;

	private DefaultAction okAction = new DefaultAction(
			new ActionEventProcessor() {

				public void processActionEvent(ActionEvent e) {
					commandOk();
				}
			}, this, ERDesignerBundle.OK);

	private DefaultAction cancelAction = new DefaultAction(
			new ActionEventProcessor() {

				public void processActionEvent(ActionEvent e) {
					commandCancel();
				}
			}, this, ERDesignerBundle.CANCEL);

	private DefaultAction updateAction = new DefaultAction(
			new ActionEventProcessor() {

				public void processActionEvent(ActionEvent e) {
					commandUpdate();
				}
			}, this, ERDesignerBundle.UPDATE);

	/**
	 * @param parent
	 */
	public ReverseEngineerEditor(Model aModel, Component aParent,
			ApplicationPreferences aPreferences) {
		super(aParent, ERDesignerBundle.REVERSEENGINEER);

		initialize();

		ReverseEngineerDataModel theModel = bindingInfo.getDefaultModel();

		theModel.getTableOptions().add(
				new NameValuePair(TableNamingEnum.STANDARD, getResourceHelper()
						.getText(ERDesignerBundle.STANDART)));
		theModel.getDomainOptions()
				.add(
						new NameValuePair(DomainNamingEnum.STANDARD,
								getResourceHelper().getText(
										ERDesignerBundle.STANDART)));
		theModel.getDefaultValueOptions()
				.add(
						new NameValuePair(DefaultValueNamingEnum.STANDARD,
								getResourceHelper().getText(
										ERDesignerBundle.STANDART)));

		bindingInfo.addBinding("tableGenerator", editingView.getNaming(), true);
		bindingInfo.addBinding("domainGenerator", editingView
				.getDomaingeneration(), true);
		bindingInfo.addBinding("defaultValueGenerator", editingView
				.getDefaultvaluegeneration(), true);
		bindingInfo.addBinding("tableOptions", new ComboboxModelAdapter(
				editingView.getNaming()));
		bindingInfo.addBinding("domainOptions", new ComboboxModelAdapter(
				editingView.getDomaingeneration()));
		bindingInfo.addBinding("defaultValueOptions", new ComboboxModelAdapter(
				editingView.getDefaultvaluegeneration()));

		schemaList = editingView.getschemaList().getModel();

		model = aModel;

		preferences = aPreferences;

		bindingInfo.configure();
		bindingInfo.model2view();
	}

	/**
	 * This method initializes this.
	 */
	private void initialize() {

		editingView = new ReverseEngineerView();
		editingView.getstartbutton().setAction(okAction);
		editingView.getcancelbutton().setAction(cancelAction);
		editingView.getrefreshbutton().setAction(updateAction);
		editingView.getschemaList().setSelectionMode(
				ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		setContentPane(editingView);
		setResizable(false);
		pack();

		UIInitializer.getInstance().initialize(this);
	}

	private void commandOk() {
		if (bindingInfo.validate().size() == 0) {
			Object[] theSelectesValues = editingView.getschemaList()
					.getSelectedValues();
			if ((theSelectesValues == null) || (theSelectesValues.length == 0)) {
				displayErrorMessage(getResourceHelper().getText(
						ERDesignerBundle.CHOOSEONESCHEMA));
				return;
			}
			setModalResult(MODAL_RESULT_OK);
		}
	}

	private void commandUpdate() {
		Connection theConnection = null;
		try {
			theConnection = model.createConnection(preferences);

			schemaList.clear();

			DatabaseMetaData theMetadata = theConnection.getMetaData();
			ResultSet theResult = theMetadata.getCatalogs();
			while (theResult.next()) {
				String theSchemaName = theResult.getString("TABLE_CAT");

				schemaList.add(theSchemaName);
			}
			theResult.close();

		} catch (Exception e) {
			displayErrorMessage(e.getMessage());
		} finally {
			if (theConnection != null) {
				try {
					theConnection.close();
				} catch (Exception e) {
				}
			}
		}
	}
	
	public ReverseEngineeringOptions createREOptions() {
		ReverseEngineerDataModel theModel = bindingInfo.getDefaultModel();
		bindingInfo.view2model();

		ReverseEngineeringOptions theOptions = new ReverseEngineeringOptions();
		theOptions.setDefaultValueNaming((DefaultValueNamingEnum) theModel.getDefaultValueGenerator().getValue());
		theOptions.setDomainNaming((DomainNamingEnum) theModel.getDomainGenerator().getValue());
		theOptions.setTableNaming((TableNamingEnum) theModel.getTableGenerator().getValue());
		theOptions.setSchemaList(editingView.getschemaList().getSelectedValues());

		return theOptions;
	}

	@Override
	public void applyValues() throws Exception {
	}
	
}

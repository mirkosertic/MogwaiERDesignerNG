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
package de.erdesignerng.visual.editor.relation;

import java.awt.Component;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.CascadeType;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.Relation;
import de.erdesignerng.visual.editor.BaseEditor;
import de.mogwai.binding.BindingInfo;
import de.mogwai.binding.adapter.RadioButtonAdapter;
import de.mogwai.common.client.looks.UIInitializer;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-01-03 18:28:04 $
 */
public class RelationEditor extends BaseEditor {

	private Model model;

	private BindingInfo<Relation> bindingInfo = new BindingInfo<Relation>();

	private RelationEditorView editingView;

	/**
	 * @param parent
	 */
	public RelationEditor(Model aModel, Component aParent) {
		super(aParent, ERDesignerBundle.RELATIONEDITOR);

		initialize();

		model = aModel;

		bindingInfo.addBinding("name", editingView.getRelationname(), true);

		RadioButtonAdapter theOnDeleteAdapter = new RadioButtonAdapter();
		theOnDeleteAdapter.addMapping(CascadeType.NOTHING, editingView
				.getOnDeleteCascadeNothing());
		theOnDeleteAdapter.addMapping(CascadeType.CASCADE, editingView
				.getOnDeleteCascade());
		theOnDeleteAdapter.addMapping(CascadeType.SET_NULL, editingView
				.getOnDeleteSetNull());
		bindingInfo.addBinding("onDelete", theOnDeleteAdapter);

		RadioButtonAdapter theOnUpdateAdapter = new RadioButtonAdapter();
		theOnUpdateAdapter.addMapping(CascadeType.NOTHING, editingView
				.getOnUpdateCascadeNothing());
		theOnUpdateAdapter.addMapping(CascadeType.CASCADE, editingView
				.getOnUpdateCascade());
		theOnUpdateAdapter.addMapping(CascadeType.SET_NULL, editingView
				.getOnUpdateSetNull());
		bindingInfo.addBinding("onUpdate", theOnUpdateAdapter);

		bindingInfo.configure();
	}

	/**
	 * This method initializes this.
	 */
	private void initialize() {

		editingView = new RelationEditorView() {

			@Override
			public void handleOKButtonActionPerformed(String actionCommand) {
				commandOk();
			}

			@Override
			public void handleCancelButtonActionPerformed(String actionCommand) {
				setModalResult(MODAL_RESULT_CANCEL);
			}

		};

		setContentPane(editingView);
		setResizable(false);
		pack();

		UIInitializer.getInstance().initialize(this);		
	}

	private de.erdesignerng.visual.editor.relation.AttributeTableModel tableModel;

	public void initializeFor(Relation aRelation) {
		bindingInfo.setDefaultModel(aRelation);
		bindingInfo.model2view();

		List<Attribute> thePrimaryKey = aRelation.getExportingTable()
				.getPrimaryKey();

		Attribute[] theAssigned = new Attribute[thePrimaryKey.size()];
		for (int count = 0; count < thePrimaryKey.size(); count++) {
			theAssigned[count] = aRelation.getMapping().get(
					thePrimaryKey.get(count));
		}
		tableModel = new AttributeTableModel(aRelation.getExportingTable()
				.getName(), aRelation.getImportingTable().getName(),
				thePrimaryKey, theAssigned);

		editingView.getComponent_5().setModel(tableModel);
		editingView.getComponent_5().getTableHeader().setReorderingAllowed(
				false);

		JComboBox theAttributes = new JComboBox(aRelation.getImportingTable()
				.getAttributes());
		editingView.getComponent_5().getColumnModel().getColumn(1)
				.setCellEditor(new DefaultCellEditor(theAttributes));
	}

	private void commandOk() {
		if (bindingInfo.validate().size() == 0) {
			setModalResult(MODAL_RESULT_OK);
		}
	}

	@Override
	public void applyValues() throws Exception {
		Relation theRelation = bindingInfo.getDefaultModel();
		bindingInfo.view2model();

		if (!model.getRelations().contains(theRelation)) {
			model.addRelation(theRelation);

		}

		for (int i = 0; i < tableModel.getRowCount(); i++) {
			Attribute thePKAttribute = (Attribute) tableModel.getValueAt(i, 0);
			Attribute theAssignedAttribute = (Attribute) tableModel.getValueAt(
					i, 1);

			theRelation.getMapping().put(thePKAttribute, theAssignedAttribute);
		}
	}
}

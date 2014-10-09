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
package de.erdesignerng.visual.editor.convertmodel;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.dialect.ConversionInfos;
import de.erdesignerng.dialect.DataType;
import de.erdesignerng.dialect.Dialect;
import de.erdesignerng.dialect.DialectFactory;
import de.erdesignerng.model.Model;
import de.erdesignerng.visual.editor.BaseEditor;
import de.mogwai.common.client.binding.BindingInfo;
import de.mogwai.common.client.looks.UIInitializer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-13 15:40:33 $
 */
public class ConvertModelEditor extends BaseEditor {

	private final Model model;

	private final BindingInfo<ConversionInfos> bindingInfo = new BindingInfo<>(new ConversionInfos());

	private ConvertModelEditorView editingView;

	/**
	 * @param aModel
	 *			the model
	 * @param aParent
	 *			the parent container
	 */
	public ConvertModelEditor(Model aModel, Component aParent) {
		super(aParent, ERDesignerBundle.CONVERTMODEL);

		initialize();

		model = aModel;

		DefaultComboBoxModel theDialectModel = new DefaultComboBoxModel();
		DialectFactory theFactory = DialectFactory.getInstance();

        theFactory.getSupportedDialects().stream().filter(theDialect -> !theDialect.getUniqueName().equals(aModel.getDialect().getUniqueName())).forEach(theDialectModel::addElement);
		editingView.getTargetDialect().setModel(theDialectModel);
		bindingInfo.getDefaultModel().setTargetDialect((Dialect) theDialectModel.getElementAt(0));

		editingView.getTargetDialect().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				initializeMappingModelFor((Dialect) editingView.getTargetDialect().getSelectedItem());
				bindingInfo.model2view();
			}

		});

		bindingInfo.addBinding("targetDialect", editingView.getTargetDialect(), true);
		bindingInfo.addBinding("typeMapping", new ConvertPropertyAdapter(editingView.getMappingTable(), null,
				getResourceHelper()));

		bindingInfo.configure();
		bindingInfo.model2view();
	}

	/**
	 * Initialize the current mapping model for a target dialect.
	 * 
	 * @param aDialect
	 *			the target dialect
	 */
	private void initializeMappingModelFor(Dialect aDialect) {

		ConversionInfos theInfos = bindingInfo.getDefaultModel();
		theInfos.setTargetDialect(aDialect);
		theInfos.getTypeMapping().clear();

		// Try to map the types
		for (DataType theCurrentType : model.getUsedDataTypes()) {
			theInfos.getTypeMapping().put(theCurrentType, aDialect.findClosestMatchingTypeFor(theCurrentType));
		}
	}

	/**
	 * This method initializes this.
	 */
	private void initialize() {

		editingView = new ConvertModelEditorView();
		editingView.getOKButton().setAction(okAction);
		editingView.getCancelButton().setAction(cancelAction);

		setContentPane(editingView);
		setResizable(false);

		pack();

		UIInitializer.getInstance().initialize(this);
	}

	@Override
	protected void commandOk() {
		if (bindingInfo.validate().isEmpty()) {
			setModalResult(MODAL_RESULT_OK);
		}
	}

	@Override
	public void applyValues() throws Exception {

		ConversionInfos theInfos = bindingInfo.getDefaultModel();
		bindingInfo.view2model(theInfos);

		model.convert(theInfos);
	}
}
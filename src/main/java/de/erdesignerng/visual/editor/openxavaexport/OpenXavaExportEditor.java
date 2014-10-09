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
package de.erdesignerng.visual.editor.openxavaexport;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.dialect.DataType;
import de.erdesignerng.generator.GeneratorUtils;
import de.erdesignerng.generator.openxava.OpenXavaGenerator;
import de.erdesignerng.generator.openxava.OpenXavaOptions;
import de.erdesignerng.generator.openxava.OpenXavaTypeMap;
import de.erdesignerng.model.Model;
import de.erdesignerng.visual.editor.BaseEditor;
import de.mogwai.common.client.binding.BindingInfo;
import de.mogwai.common.client.looks.UIInitializer;
import de.mogwai.common.client.looks.components.action.DefaultAction;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Map;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-13 15:40:33 $
 */
public class OpenXavaExportEditor extends BaseEditor {

	private static final String OX_STYPE_ = "OX_STYPE_";

	private static final String OX_TYPE_ = "OX_TYPE_";

	private static final String OX_PACKAGE = "OX_PACKAGE";

	private static final String OX_SRCDIR = "OX_SRCDIR";

	private final Model model;

	private final BindingInfo<OpenXavaOptions> bindingInfo = new BindingInfo<>(new OpenXavaOptions());

	private OpenXavaExportEditorView editingView;

	protected final DefaultAction chooseSrcDirectoryAction = new DefaultAction(e -> commandChooseSrcDirectory(), this, ERDesignerBundle.FILE);

	/**
	 * Create a relation editor.
	 * 
	 * @param aModel
	 *			the model
	 * @param aParent
	 *			the parent container
	 */
	public OpenXavaExportEditor(Model aModel, Component aParent) {
		super(aParent, ERDesignerBundle.OPENXAVAEXPORT);

		initialize();

		model = aModel;

		bindingInfo.addBinding("srcDirectory", editingView.getSrcDirectory(), true);
		bindingInfo.addBinding("packageName", editingView.getPackageName(), true);

		initializeMappingModelFor(aModel);
		bindingInfo.addBinding("typeMapping", new ConvertPropertyAdapter(editingView.getMappingTable(), null,
				getResourceHelper()));

		bindingInfo.configure();
		bindingInfo.model2view();
	}

	/**
	 * Initialize the current mapping model for a target dialect.
	 * 
	 * @param aModel
	 *			the target model
	 */
	private void initializeMappingModelFor(Model aModel) {

		OpenXavaOptions theOptions = bindingInfo.getDefaultModel();

		theOptions.setSrcDirectory(aModel.getProperties().getProperty(OX_SRCDIR));
		theOptions.setPackageName(aModel.getProperties().getProperty(OX_PACKAGE));

		for (DataType theType : aModel.getUsedDataTypes()) {
			OpenXavaTypeMap theMapping = new OpenXavaTypeMap();
			String theJavaType = aModel.getProperties().getProperty(OX_TYPE_ + theType.getName());
			String theStereoType = aModel.getProperties().getProperty(OX_STYPE_ + theType.getName());
			if (StringUtils.isEmpty(theJavaType)) {
				theJavaType = GeneratorUtils.findClosestJavaTypeFor(theType);
			}
			theMapping.setJavaType(theJavaType);
			theMapping.setStereoType(theStereoType);

			theOptions.getTypeMapping().put(theType, theMapping);
		}
	}

	/**
	 * This method initializes this.
	 */
	private void initialize() {

		editingView = new OpenXavaExportEditorView();
		editingView.getOKButton().setAction(okAction);
		editingView.getCancelButton().setAction(cancelAction);
		editingView.getSearchDirectoryButton().setAction(chooseSrcDirectoryAction);

		setContentPane(editingView);
		setResizable(false);

		pack();

		UIInitializer.getInstance().initialize(this);
	}

	private void commandChooseSrcDirectory() {
		JFileChooser theChooser = new JFileChooser();
		theChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (theChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			File theBaseDirectory = theChooser.getSelectedFile();
			editingView.getSrcDirectory().setText(theBaseDirectory.toString());
		}
	}

	@Override
	protected void commandOk() {
		if (bindingInfo.validate().isEmpty()) {

			bindingInfo.view2model();

			OpenXavaOptions theOptions = bindingInfo.getDefaultModel();

			model.getProperties().setProperty(OX_SRCDIR, theOptions.getSrcDirectory());
			model.getProperties().setProperty(OX_PACKAGE, theOptions.getPackageName());

			for (Map.Entry<DataType, OpenXavaTypeMap> theEntry : theOptions.getTypeMapping().entrySet()) {

				model.getProperties().setProperty(OX_TYPE_ + theEntry.getKey().getName(),
						theEntry.getValue().getJavaType());
				model.getProperties().setProperty(OX_STYPE_ + theEntry.getKey().getName(),
						theEntry.getValue().getStereoType());

			}
			setModalResult(MODAL_RESULT_OK);
		}
	}

	@Override
	public void applyValues() throws Exception {
		OpenXavaOptions theOptions = bindingInfo.getDefaultModel();

		OpenXavaGenerator theGenerator = new OpenXavaGenerator();
		theGenerator.generate(model, theOptions);
	}
}
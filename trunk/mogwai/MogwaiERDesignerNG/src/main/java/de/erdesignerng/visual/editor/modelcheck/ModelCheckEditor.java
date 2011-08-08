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
package de.erdesignerng.visual.editor.modelcheck;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.check.ModelChecker;
import de.erdesignerng.model.check.ModelError;
import de.erdesignerng.util.ApplicationPreferences;
import de.erdesignerng.visual.editor.BaseEditor;
import de.erdesignerng.visual.editor.DialogConstants;
import de.mogwai.common.client.looks.UIInitializer;
import de.mogwai.common.client.looks.components.action.ActionEventProcessor;
import de.mogwai.common.client.looks.components.action.DefaultAction;
import de.mogwai.common.client.looks.components.list.DefaultListModel;

import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Editor for model checks.
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-09 19:07:29 $
 */
public class ModelCheckEditor extends BaseEditor {

	private final DefaultAction closeAction = new DefaultAction(
			new ActionEventProcessor() {

		@Override
				public void processActionEvent(ActionEvent e) {
					commandClose();
				}
			}, this, ERDesignerBundle.CLOSE);


	private ModelCheckEditorView view = new ModelCheckEditorView();

    private Model model;

	public ModelCheckEditor(Component aParent, Model aModel) {
		super(aParent, ERDesignerBundle.MODELCHECKRESULT);

        model = aModel;

		initialize();

		view.getCloseButton().setAction(closeAction);

        ModelChecker theChecker = new ModelChecker();
        theChecker.check(model);

		DefaultListModel theModel = view.getErrorList().getModel();
        for (ModelError theError : theChecker.getErrors()) {
			theModel.add(theError);
		}
	}

	private void initialize() {

		setContentPane(view);
		setResizable(true);

		pack();

		setMinimumSize(getSize());
		ApplicationPreferences.getInstance().setWindowSize(
				getClass().getSimpleName(), this);

		UIInitializer.getInstance().initialize(this);
	}

	@Override
	public void applyValues() throws Exception {
	}

	private void commandClose() {
		ApplicationPreferences.getInstance().updateWindowSize(
				getClass().getSimpleName(), this);
		setModalResult(DialogConstants.MODAL_RESULT_OK);
	}
}
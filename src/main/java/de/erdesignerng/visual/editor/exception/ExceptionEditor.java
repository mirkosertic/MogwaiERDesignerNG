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
package de.erdesignerng.visual.editor.exception;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.visual.editor.BaseEditor;
import de.erdesignerng.visual.editor.DialogConstants;
import de.mogwai.common.client.looks.UIInitializer;
import de.mogwai.common.client.looks.components.action.DefaultAction;
import org.apache.log4j.Logger;

import java.awt.*;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Editor for exceptions.
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2008-06-13 16:48:59 $
 */
public class ExceptionEditor extends BaseEditor {

	private static final Logger LOGGER = Logger.getLogger(ExceptionEditor.class);

	private final ExceptionEditorView view = new ExceptionEditorView();

	private final DefaultAction closeAction = new DefaultAction(e -> commandClose(), this, ERDesignerBundle.CLOSE);

	public ExceptionEditor(Component aParent, Exception aException) {
		super(aParent, ERDesignerBundle.EXCEPTIONWINDOW);

		LOGGER.error("Exception", aException);

		initialize();

		StringWriter theWriter = new StringWriter();
		PrintWriter thePrintWriter = new PrintWriter(theWriter);
		aException.printStackTrace(thePrintWriter);
		thePrintWriter.flush();

		view.getExceptionText().setText(theWriter.toString());
	}

	private void initialize() {

		view.getCloseButton().setAction(closeAction);

		setContentPane(view);
		setResizable(false);
		pack();

		UIInitializer.getInstance().initialize(this);
	}

	@Override
	public void applyValues() throws Exception {
	}

	private void commandClose() {
		setModalResult(DialogConstants.MODAL_RESULT_OK);
	}
}
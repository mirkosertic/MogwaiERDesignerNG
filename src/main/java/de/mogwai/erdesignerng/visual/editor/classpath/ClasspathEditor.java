package de.mogwai.erdesignerng.visual.editor.classpath;

import javax.swing.JFrame;

import de.mogwai.erdesignerng.model.Model;
import de.mogwai.erdesignerng.visual.editor.BaseEditor;
import de.mogwai.erdesignerng.visual.editor.DialogConstants;

/**
 * Editor for the database connection.
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2007-07-30 15:21:28 $
 */
public class ClasspathEditor extends BaseEditor {

	private ClasspathEditorView view = new ClasspathEditorView() {

		@Override
		protected void handleCancel() {
			commandCancel();
		}

		@Override
		protected void handleOk() {
			commandClose();
		}
	};

	private Model model;

	public ClasspathEditor(JFrame aParent, Model aModel) {
		super(aParent);

		model = aModel;

		initialize();

	}

	private void initialize() {

		setContentPane(view);
		setTitle("Classpath");
		setResizable(false);
		pack();
	}

	@Override
	public void applyValues() throws Exception {

	}

	private void commandClose() {

		setModalResult(DialogConstants.MODAL_RESULT_OK);
	}

	private void commandCancel() {

		setModalResult(DialogConstants.MODAL_RESULT_CANCEL);
	}
}

package de.erdesignerng.visual.editor.classpath;

import java.awt.Component;
import java.io.File;
import java.util.List;

import javax.swing.DefaultListModel;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.util.ApplicationPreferences;
import de.erdesignerng.visual.editor.BaseEditor;
import de.erdesignerng.visual.editor.DialogConstants;
import de.mogwai.looks.UIInitializer;

/**
 * Editor for the database connection.
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2008-01-03 13:11:30 $
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

	private DefaultListModel list = new DefaultListModel();

	private ApplicationPreferences preferences;

	public ClasspathEditor(Component aParent, ApplicationPreferences aPreferences) {
		super(aParent , ERDesignerBundle.CLASSPATHCONFIGURATION);

		initialize();

		view.getClasspath().setModel(list);

		List<File> theFiles = aPreferences.getClasspathFiles();
		for (File theFile : theFiles) {
			list.addElement(theFile);
		}

		preferences = aPreferences;
	}

	private void initialize() {

		setContentPane(view);
		setResizable(false);
		pack();
		
		UIInitializer.getInstance().initialize(this);
	}

	@Override
	public void applyValues() throws Exception {

		List<File> theFiles = preferences.getClasspathFiles();
		theFiles.clear();

		for (int i = 0; i < list.getSize(); i++) {
			theFiles.add((File) list.get(i));
		}
	}

	private void commandClose() {

		setModalResult(DialogConstants.MODAL_RESULT_OK);
	}

	private void commandCancel() {

		setModalResult(DialogConstants.MODAL_RESULT_CANCEL);
	}
}

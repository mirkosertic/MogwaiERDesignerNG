package de.erdesignerng.visual.editor.classpath;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;

import javax.swing.JFileChooser;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.io.GenericFileFilter;
import de.erdesignerng.util.ApplicationPreferences;
import de.erdesignerng.visual.editor.BaseEditor;
import de.erdesignerng.visual.editor.DialogConstants;
import de.mogwai.common.client.looks.UIInitializer;
import de.mogwai.common.client.looks.components.action.ActionEventProcessor;
import de.mogwai.common.client.looks.components.action.DefaultAction;
import de.mogwai.common.client.looks.components.list.DefaultListModel;

/**
 * Editor for the database connection.
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2008-01-12 17:10:02 $
 */
public class ClasspathEditor extends BaseEditor {

	private DefaultAction okAction = new DefaultAction(
			new ActionEventProcessor() {

				public void processActionEvent(ActionEvent e) {
					commandClose();
				}
			}, this, ERDesignerBundle.OK);

	private DefaultAction cancelAction = new DefaultAction(
			new ActionEventProcessor() {

				public void processActionEvent(ActionEvent e) {
					commandCancel();
				}
			}, this, ERDesignerBundle.CANCEL);

	private DefaultAction addAction = new DefaultAction(
			new ActionEventProcessor() {

				public void processActionEvent(ActionEvent e) {
					commandFolderAdd();
				}
			}, this, ERDesignerBundle.ADDFOLDER);

	private DefaultAction removeAction = new DefaultAction(
			new ActionEventProcessor() {

				public void processActionEvent(ActionEvent e) {
					commandFolderRemove();
				}
			}, this, ERDesignerBundle.REMOVEFOLDER);

	private ClasspathEditorView view = new ClasspathEditorView();

	private ApplicationPreferences preferences;

	private File lastDir;

	public ClasspathEditor(Component aParent,
			ApplicationPreferences aPreferences) {
		super(aParent, ERDesignerBundle.CLASSPATHCONFIGURATION);

		initialize();

		DefaultListModel theModel = (DefaultListModel) view.getClasspath()
				.getModel();
		view.getClasspath().setModel(theModel);

		List<File> theFiles = aPreferences.getClasspathFiles();
		for (File theFile : theFiles) {
			theModel.add(theFile);
		}

		preferences = aPreferences;
	}

	private void initialize() {

		view.getOkButton().setAction(okAction);
		view.getCancelButton().setAction(cancelAction);
		view.getAddButton().setAction(addAction);
		view.getRemoveButton().setAction(removeAction);

		setContentPane(view);
		setResizable(false);
		pack();

		UIInitializer.getInstance().initialize(this);
	}

	@Override
	public void applyValues() throws Exception {

		DefaultListModel theModel = (DefaultListModel) view.getClasspath()
				.getModel();

		List<File> theFiles = preferences.getClasspathFiles();
		theFiles.clear();

		for (int i = 0; i < theModel.getSize(); i++) {
			theFiles.add((File) theModel.get(i));
		}
	}

	private void commandClose() {

		setModalResult(DialogConstants.MODAL_RESULT_OK);
	}

	protected void commandFolderAdd() {

		DefaultListModel theModel = (DefaultListModel) view.getClasspath()
				.getModel();

		JFileChooser theChooser = new JFileChooser();
		if (lastDir != null) {
			theChooser.setCurrentDirectory(lastDir);
		}
		theChooser.setMultiSelectionEnabled(true);
		theChooser.setFileFilter(new GenericFileFilter(".jar", "Java archive"));
		if (theChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			File[] theFiles = theChooser.getSelectedFiles();

			for (File theFile : theFiles) {
				if (!theModel.contains(theFile)) {
					theModel.add(theFile);
				}
			}

			lastDir = theChooser.getCurrentDirectory();
		}
	}

	protected void commandFolderRemove() {

		DefaultListModel theModel = (DefaultListModel) view.getClasspath()
				.getModel();

		Object[] theValues = view.getClasspath().getSelectedValues();
		for (Object theValue : theValues) {
			theModel.remove(theValue);
		}
	}

}

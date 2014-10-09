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
package de.erdesignerng.visual.editor.classpath;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.io.GenericFileFilter;
import de.erdesignerng.util.ApplicationPreferences;
import de.erdesignerng.visual.editor.BaseEditor;
import de.mogwai.common.client.looks.UIInitializer;
import de.mogwai.common.client.looks.components.action.DefaultAction;
import de.mogwai.common.client.looks.components.list.DefaultListModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.List;

/**
 * Editor for the class path entries.
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-13 15:40:33 $
 */
public class ClasspathEditor extends BaseEditor {

	private final DefaultAction addAction = new DefaultAction(
            e -> commandFolderAdd(), this, ERDesignerBundle.ADDFOLDER);

	private final DefaultAction removeAction = new DefaultAction(
            e -> commandFolderRemove(), this, ERDesignerBundle.REMOVEFOLDER);

	private final ClasspathEditorView view = new ClasspathEditorView();

	private File lastDir;

	public ClasspathEditor(Component aParent) {
		super(aParent, ERDesignerBundle.CLASSPATHCONFIGURATION);

		initialize();

		DefaultListModel theModel = (DefaultListModel) view.getClasspath()
				.getModel();
		view.getClasspath().setModel(theModel);

		List<File> theFiles = ApplicationPreferences.getInstance()
				.getClasspathFiles();
        theFiles.forEach(theModel::add);
	}

	private void initialize() {

		view.getOkButton().setAction(okAction);
		view.getCancelButton().setAction(cancelAction);
		view.getAddButton().setAction(addAction);
		view.getRemoveButton().setAction(removeAction);

		view.getCancelButton().registerKeyboardAction(cancelAction,
				KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
				JComponent.WHEN_IN_FOCUSED_WINDOW);

		setContentPane(view);
		setResizable(false);

		pack();

		UIInitializer.getInstance().initialize(this);
	}

	@Override
	public void applyValues() throws Exception {

		DefaultListModel theModel = (DefaultListModel) view.getClasspath()
				.getModel();

		List<File> theFiles = ApplicationPreferences.getInstance()
				.getClasspathFiles();
		theFiles.clear();

		for (int i = 0; i < theModel.getSize(); i++) {
			theFiles.add((File) theModel.get(i));
		}
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

		for (Object theValue : view.getClasspath().getSelectedValuesList()) {
			theModel.remove(theValue);
		}
	}
}
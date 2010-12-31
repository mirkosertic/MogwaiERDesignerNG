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
package de.erdesignerng.visual.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.swing.JFileChooser;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.io.ModelFileFilter;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.ModelIOUtilities;
import de.erdesignerng.visual.MessagesHelper;

public class OpenFromFileCommand extends UICommand {

	public OpenFromFileCommand(ERDesignerComponent component) {
		super(component);
	}

	@Override
	public void execute() {
		ModelFileFilter theFiler = new ModelFileFilter();

		JFileChooser theChooser = new JFileChooser();
		theChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		theChooser.setFileFilter(theFiler);
		if (theChooser.showOpenDialog(getDetailComponent()) == JFileChooser.APPROVE_OPTION) {

			File theFile = theFiler.getCompletedFile(theChooser.getSelectedFile());

			execute(theFile);
		}
	}

	void execute(File aFile) {
		FileInputStream theStream = null;

		try {
			component.setIntelligentLayoutEnabled(false);

			theStream = new FileInputStream(aFile);

			Model theModel = ModelIOUtilities.getInstance().deserializeModelFromXML(theStream);
			getWorldConnector().initializeLoadedModel(theModel);

			component.setModel(theModel);

			getPreferences().addRecentlyUsedFile(aFile);

			component.addCurrentConnectionToConnectionHistory();

			component.setupViewFor(aFile);
			getWorldConnector().setStatusText(component.getResourceHelper().getText(ERDesignerBundle.FILELOADED));

		} catch (Exception e) {

			MessagesHelper.displayErrorMessage(getDetailComponent(), component.getResourceHelper().getText(
					ERDesignerBundle.ERRORLOADINGFILE));

			getWorldConnector().notifyAboutException(e);
		} finally {
			if (theStream != null) {
				try {
					theStream.close();
				} catch (IOException e) {
					// Ignore this exception
				}
			}

			component.setIntelligentLayoutEnabled(getPreferences().isIntelligentLayout());
		}
	}
}
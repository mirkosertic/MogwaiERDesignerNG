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

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.io.ModelFileFilter;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.ModelIOUtilities;
import de.erdesignerng.visual.MessagesHelper;
import de.erdesignerng.visual.editor.completecompare.CompleteCompareEditor;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class CompleteCompareWithOtherModelCommand extends UICommand {

    public CompleteCompareWithOtherModelCommand() {
    }

    @Override
    public void execute() {

        ERDesignerComponent component = ERDesignerComponent.getDefault();

        Model theCurrentModel = component.getModel();

        ModelFileFilter theFiler = new ModelFileFilter();

        JFileChooser theChooser = new JFileChooser();
        theChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        theChooser.setFileFilter(theFiler);
        if (theChooser.showOpenDialog(getDetailComponent()) == JFileChooser.APPROVE_OPTION) {

            File theFile = theFiler.getCompletedFile(theChooser
                    .getSelectedFile());

            InputStream theStream = null;
            try {

                theStream = new FileInputStream(theFile);

                Model theNewModel = ModelIOUtilities.getInstance()
                        .deserializeModelFromXML(theStream);

                CompleteCompareEditor theCompare = new CompleteCompareEditor(
                        getDetailComponent(), theCurrentModel, theNewModel,
                        ERDesignerBundle.COMPLETECOMPAREWITHOTHERMODEL);
                theCompare.showModal();

            } catch (Exception e) {
                MessagesHelper.displayErrorMessage(getDetailComponent(),
                        component.getResourceHelper().getText(
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
            }
        }
    }
}
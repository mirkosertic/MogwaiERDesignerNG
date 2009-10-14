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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JFileChooser;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.dialect.SQLGenerator;
import de.erdesignerng.dialect.Statement;
import de.erdesignerng.dialect.StatementList;
import de.erdesignerng.io.ModelFileFilter;
import de.erdesignerng.model.ModelIOUtilities;
import de.erdesignerng.modificationtracker.HistoryModificationTracker;

public class SaveToFileCommand extends UICommand {

    public SaveToFileCommand(ERDesignerComponent component) {
        super(component);
    }

    @Override
    public void execute() {
        if (component.currentEditingFile != null) {
            executeSaveToFile(component.currentEditingFile);
        } else {
            executeSaveFileAs();
        }
    }

    public void executeSaveFileAs() {

        ModelFileFilter theFiler = new ModelFileFilter();

        JFileChooser theChooser = new JFileChooser();
        theChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        theChooser.setFileFilter(theFiler);
        theChooser.setSelectedFile(component.currentEditingFile);
        if (theChooser.showSaveDialog(component.scrollPane) == JFileChooser.APPROVE_OPTION) {

            File theFile = theFiler.getCompletedFile(theChooser.getSelectedFile());
            executeSaveToFile(theFile);

        }
    }

    private void executeSaveToFile(File aFile) {

        DateFormat theFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        Date theNow = new Date();

        FileOutputStream theStream = null;
        PrintWriter theWriter = null;
        try {

            component.setIntelligentLayoutEnabled(false);

            if (aFile.exists()) {
                File theBakFile = new File(aFile.toString() + "_" + theFormat.format(theNow));
                aFile.renameTo(theBakFile);
            }

            theStream = new FileOutputStream(aFile);

            ModelIOUtilities.getInstance().serializeModelToXML(component.model, theStream);

            component.worldConnector.initTitle();

            component.preferences.addRecentlyUsedFile(aFile);

            component.updateRecentlyUsedMenuEntries();

            if (component.model.getModificationTracker() instanceof HistoryModificationTracker) {
                HistoryModificationTracker theTracker = (HistoryModificationTracker) component.model.getModificationTracker();
                StatementList theStatements = theTracker.getNotSavedStatements();
                if (theStatements.size() > 0) {
                    StringBuilder theFileName = new StringBuilder(aFile.toString());
                    int p = theFileName.lastIndexOf(".");
                    if (p > 0) {

                        SQLGenerator theGenerator = component.model.getDialect().createSQLGenerator();

                        theFileName = new StringBuilder(theFileName.substring(0, p));

                        theFileName.insert(p, "_" + theFormat.format(theNow));
                        theFileName.append(".sql");

                        theWriter = new PrintWriter(new File(theFileName.toString()));
                        for (Statement theStatement : theStatements) {
                            theWriter.print(theStatement.getSql());
                            theWriter.println(theGenerator.createScriptStatementSeparator());
                            theStatement.setSaved(true);

                        }
                    }
                }
            }

            component.setupViewFor(aFile);
            component.worldConnector.setStatusText(component.getResourceHelper().getText(ERDesignerBundle.FILESAVED));

        } catch (Exception e) {
            component.worldConnector.notifyAboutException(e);
        } finally {
            if (theStream != null) {
                try {
                    theStream.close();
                } catch (IOException e) {
                    // Ignore this exception
                }
            }
            if (theWriter != null) {
                theWriter.close();
            }

            component.setIntelligentLayoutEnabled(component.preferences.isIntelligentLayout());
        }
    }
}

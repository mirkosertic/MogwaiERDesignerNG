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

import de.erdesignerng.io.GenericFileFilter;
import de.erdesignerng.model.ModelItem;
import de.erdesignerng.visual.ExportType;
import de.erdesignerng.visual.jgraph.JGraphEditor;
import de.erdesignerng.visual.jgraph.cells.views.TableCellView;
import de.erdesignerng.visual.jgraph.export.Exporter;
import org.jgraph.graph.CellView;
import org.jgraph.graph.DefaultGraphCell;

import javax.swing.JFileChooser;
import java.io.File;
import java.io.FileOutputStream;

public class ExportGraphicsCommand extends UICommand {

    private final Exporter exporter;

    private final ExportType exportType;

    private final JGraphEditor editor;

    public ExportGraphicsCommand(final JGraphEditor aEditor, final Exporter aExporter, final ExportType aExportType) {
        editor = aEditor;
        exporter = aExporter;
        exportType = aExportType;
    }

    @Override
    public void execute() {

        if (exportType == ExportType.ONE_PER_FILE) {

            final JFileChooser theChooser = new JFileChooser();
            theChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (theChooser.showSaveDialog(getDetailComponent()) == JFileChooser.APPROVE_OPTION) {
                final File theBaseDirectory = theChooser.getSelectedFile();

                final CellView[] theViews = editor.getGraph().getGraphLayoutCache().getAllViews();
                for (final CellView theView : theViews) {
                    if (theView instanceof TableCellView) {
                        final TableCellView theItemCellView = (TableCellView) theView;
                        final DefaultGraphCell theItemCell = (DefaultGraphCell) theItemCellView.getCell();
                        final ModelItem theItem = (ModelItem) theItemCell.getUserObject();

                        final File theOutputFile = new File(theBaseDirectory, theItem.getName() + exporter.getFileExtension());
                        try {
                            exporter.exportToStream(theItemCellView.getRendererComponent(editor.getGraph(), false, false,
                                    false), new FileOutputStream(theOutputFile));
                        } catch (final Exception e) {
                            getWorldConnector().notifyAboutException(e);
                        }
                    }
                }
            }

        } else {

            final JFileChooser theChooser = new JFileChooser();
            final GenericFileFilter theFilter = new GenericFileFilter(exporter.getFileExtension(), exporter
                    .getFileExtension()
                    + " File");
            theChooser.setFileFilter(theFilter);
            if (theChooser.showSaveDialog(getDetailComponent()) == JFileChooser.APPROVE_OPTION) {

                final File theFile = theFilter.getCompletedFile(theChooser.getSelectedFile());
                try {
                    exporter.fullExportToStream(editor.getGraph(), new FileOutputStream(theFile));
                } catch (final Exception e) {
                    getWorldConnector().notifyAboutException(e);
                }
            }

        }
    }
}
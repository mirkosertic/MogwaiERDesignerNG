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
import de.erdesignerng.PlatformConfig;
import de.erdesignerng.model.ModelIOUtilities;
import de.erdesignerng.util.JasperUtils;
import de.erdesignerng.visual.LongRunningTask;
import de.mogwai.common.client.looks.components.DefaultDialog;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.swing.JRViewer;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class GenerateDocumentationCommand extends UICommand {

    private final File JRXMLFile;

    public GenerateDocumentationCommand(final File aJRXMLFile) {
        JRXMLFile = aJRXMLFile;
    }

    @Override
    public void execute() {

        final ERDesignerComponent component = ERDesignerComponent.getDefault();

        if (!component.checkForValidConnection()) {
            return;
        }

        final LongRunningTask<JasperPrint> theTask = new LongRunningTask<>(getWorldConnector()) {

            @Override
            public JasperPrint doWork(final MessagePublisher aMessagePublisher) throws Exception {

                aMessagePublisher.publishMessage(component.getResourceHelper().getText(ERDesignerBundle.DOCSTEP1));

                final ModelIOUtilities theUtils = ModelIOUtilities.getInstance();
                final File theTempFile = File.createTempFile("mogwai", ".mxm");
                theUtils.serializeModelToXML(component.getModel(), new OutputStreamWriter(new FileOutputStream(theTempFile), PlatformConfig.getXMLEncoding()));

                aMessagePublisher.publishMessage(component.getResourceHelper().getText(ERDesignerBundle.DOCSTEP2));

                final JasperPrint thePrint = JasperUtils.runJasperReport(theTempFile, JRXMLFile);

                aMessagePublisher.publishMessage(component.getResourceHelper().getText(ERDesignerBundle.DOCSTEP3));

                return thePrint;
            }

            @Override
            public void handleResult(final JasperPrint aResult) {

                final JRViewer theViewer = new JRViewer(aResult);

                final DefaultDialog theResult = new DefaultDialog(getDetailComponent(), component.getResourceHelper(),
                        ERDesignerBundle.CREATEDBDOCUMENTATION);
                theResult.setContentPane(theViewer);
                theResult.setMinimumSize(new Dimension(640, 480));
                theResult.pack();
                theViewer.setFitPageZoomRatio();

                theResult.setVisible(true);
            }

        };
        theTask.start();
    }
}
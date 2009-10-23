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

import java.awt.Dimension;
import java.io.File;
import java.io.FileWriter;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.swing.JRViewer;
import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.model.ModelIOUtilities;
import de.erdesignerng.util.JasperUtils;
import de.erdesignerng.visual.LongRunningTask;
import de.mogwai.common.client.looks.components.DefaultDialog;

public class GenerateDocumentationCommand extends UICommand {

    private File JRXMLFile;
    
    public GenerateDocumentationCommand(ERDesignerComponent component, File aJRXMLFile) {
        super(component);
        JRXMLFile = aJRXMLFile;
    }

    @Override
    public void execute() {
        if (!component.checkForValidConnection()) {
            return;
        }

        LongRunningTask<JasperPrint> theTask = new LongRunningTask<JasperPrint>(getWorldConnector()) {

            @Override
            public JasperPrint doWork(MessagePublisher aMessagePublisher) throws Exception {

                aMessagePublisher.publishMessage(component.getResourceHelper().getText(ERDesignerBundle.DOCSTEP1));

                ModelIOUtilities theUtils = ModelIOUtilities.getInstance();
                File theTempFile = File.createTempFile("mogwai", ".mxm");
                theUtils.serializeModelToXML(component.getModel(), new FileWriter(theTempFile));

                aMessagePublisher.publishMessage(component.getResourceHelper().getText(ERDesignerBundle.DOCSTEP2));

                JasperPrint thePrint = JasperUtils.runJasperReport(theTempFile, JRXMLFile);

                aMessagePublisher.publishMessage(component.getResourceHelper().getText(ERDesignerBundle.DOCSTEP3));

                return thePrint;
            }

            @Override
            public void handleResult(JasperPrint aResult) {

                JRViewer theViewer = new JRViewer(aResult);

                DefaultDialog theResult = new DefaultDialog(getDetailComponent(), component.getResourceHelper(),
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
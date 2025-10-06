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
package de.erdesignerng.visual.jgraph.export;

import de.erdesignerng.PlatformConfig;
import de.erdesignerng.visual.jgraph.ERDesignerGraph;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-01-15 19:22:45 $
 */
public class SVGExporter implements Exporter {

    @Override
    public void fullExportToStream(final ERDesignerGraph aGraph, final OutputStream aStream) throws IOException {
        final Object[] cells = aGraph.getRoots();
        final Rectangle2D bounds = aGraph.toScreen(aGraph.getCellBounds(cells));
        if (bounds != null) {
            final DOMImplementation theDomImpl = SVGDOMImplementation.getDOMImplementation();
            final String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
            final Document theDocument = theDomImpl.createDocument(svgNS, "svg", null);
            final SVGGraphics2D theSvgGenerator = new SVGGraphics2D(theDocument);
            theSvgGenerator.translate(-bounds.getX() + 10, -bounds.getY() + 0);
            final RepaintManager theRepaintManager = RepaintManager.currentManager(aGraph);
            theRepaintManager.setDoubleBufferingEnabled(false);
            final boolean theDoubleBuffered = aGraph.isDoubleBuffered();
            // Disable double buffering to allow Batik to render svg elements
            // instead of images
            aGraph.setDoubleBuffered(false);
            aGraph.paint(theSvgGenerator);
            aGraph.setDoubleBuffered(theDoubleBuffered);
            final Writer theWriter = new OutputStreamWriter(aStream, PlatformConfig.getXMLEncoding());
            theSvgGenerator.stream(theWriter, false);
            theRepaintManager.setDoubleBufferingEnabled(true);

            theWriter.flush();
            theWriter.close();
        }
    }

    @Override
    public String getFileExtension() {
        return ".svg";
    }

    @Override
    public void exportToStream(final Component aComponent, final OutputStream aStream) throws IOException {
        final DOMImplementation theDomImpl = GenericDOMImplementation.getDOMImplementation();
        final Document theDocument = theDomImpl.createDocument(null, "svg", null);
        final SVGGraphics2D theSvgGenerator = new SVGGraphics2D(theDocument);
        final RepaintManager theRepaintManager = RepaintManager.currentManager(aComponent);
        theRepaintManager.setDoubleBufferingEnabled(false);

        final Dimension theSize = aComponent.getPreferredSize();
        aComponent.setSize(theSize);

        aComponent.paint(theSvgGenerator);
        final Writer theWriter = new OutputStreamWriter(aStream, PlatformConfig.getXMLEncoding());
        theSvgGenerator.stream(theWriter, false);
        theRepaintManager.setDoubleBufferingEnabled(true);

        theWriter.flush();
        theWriter.close();
    }
}

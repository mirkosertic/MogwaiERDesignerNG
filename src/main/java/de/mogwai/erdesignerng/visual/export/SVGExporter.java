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
package de.mogwai.erdesignerng.visual.export;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.swing.RepaintManager;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import de.mogwai.erdesignerng.visual.ERDesignerGraph;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2007-07-08 17:55:40 $
 */
public class SVGExporter implements Exporter {

	public void fullExportToStream(ERDesignerGraph aGraph, OutputStream aStream)
			throws IOException {
		Object[] cells = aGraph.getRoots();
		Rectangle2D bounds = aGraph.toScreen(aGraph.getCellBounds(cells));
		if (bounds != null) {
			DOMImplementation theDomImpl = GenericDOMImplementation
					.getDOMImplementation();
			Document theDocument = theDomImpl.createDocument(null, "svg", null);
			SVGGraphics2D theSvgGenerator = new SVGGraphics2D(theDocument);
			theSvgGenerator.translate(-bounds.getX() + 10, -bounds.getY() + 0);
			RepaintManager theRepaintManager = RepaintManager
					.currentManager(aGraph);
			theRepaintManager.setDoubleBufferingEnabled(false);
			aGraph.paint(theSvgGenerator);
			Writer theWriter = new OutputStreamWriter(aStream, "UTF-8");
			theSvgGenerator.stream(theWriter, false);
			theRepaintManager.setDoubleBufferingEnabled(true);

			theWriter.flush();
			theWriter.close();
		}
	}

	public String getFileExtension() {
		return ".svg";
	}
}

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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Vector;

import javax.imageio.ImageIO;

import de.mogwai.erdesignerng.visual.ERDesignerGraph;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2007-07-08 18:49:39 $
 */
public class ImageExporter implements Exporter {

	private String ext;

	public static List<String> getSupportedFormats() {

		Vector<String> theKnown = new Vector<String>();
		String[] theList = ImageIO.getWriterMIMETypes();
		for (String theEntry : theList) {
			theKnown.add(theEntry.toUpperCase());
		}
		return theKnown;
	}

	public ImageExporter(String aExt) {
		ext = aExt;
	}

	public void fullExportToStream(ERDesignerGraph aGraph, OutputStream aStream)
			throws IOException {
		Color theBackgroundColor = aGraph.getBackground();
		BufferedImage theImage = aGraph.getImage(theBackgroundColor, 10);
		ImageIO.write(theImage, ext, aStream);
		aStream.flush();
		aStream.close();
	}

	public String getFileExtension() {
		return "." + ext.toLowerCase();
	}

	public void exportToStream(Component aComponent, OutputStream aStream) throws IOException {
		Dimension theSize = aComponent.getPreferredSize();
		aComponent.setSize(theSize);
	    BufferedImage theImage = new BufferedImage(theSize.width+10, theSize.height+10, BufferedImage.TYPE_INT_RGB);
	    Graphics theGraphics = theImage.getGraphics();
	    theGraphics.setColor(Color.white);
	    theGraphics.fillRect(0,0,theSize.width+10,theSize.height+10);
	    theGraphics.translate(5, 5);
	    theGraphics.setColor(Color.black);
	    aComponent.paint(theGraphics);
	    theGraphics.dispose();
		ImageIO.write(theImage, ext, aStream);
		aStream.flush();
		aStream.close();
	}
}

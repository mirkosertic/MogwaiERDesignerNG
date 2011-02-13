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
package de.erdesignerng.io;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2008-06-13 16:49:00 $
 */
public class GenericFileFilter extends FileFilter {

	private final String extension;

	private final String description;

	public GenericFileFilter(String aExtension, String aDescription) {
		extension = aExtension;
		description = aDescription;
	}

	@Override
	public boolean accept(File aFileName) {
		if (aFileName.isDirectory()) {
			return true;
		}
		return aFileName.getName().toLowerCase().endsWith(extension);
	}

	@Override
	public String getDescription() {
		return description;
	}

	public File getCompletedFile(File aFile) {
		if (!aFile.getName().toLowerCase().endsWith(extension)) {
			return new File(aFile.toString() + extension);
		}
		return aFile;
	}
}

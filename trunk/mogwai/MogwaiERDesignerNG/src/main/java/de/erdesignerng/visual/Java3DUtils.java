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
package de.erdesignerng.visual;

import org.apache.commons.lang.SystemUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;

public final class Java3DUtils {

    private static final boolean VM_32_BIT = System.getProperty("sun.arch.data.model").equals("32");

    private static void addLibraryPath(String pathToAdd) throws Exception{
        Field usrPathsField = ClassLoader.class.getDeclaredField("usr_paths");
        usrPathsField.setAccessible(true);

        //get array of paths
        String[] paths = (String[])usrPathsField.get(null);

        //check if the path to add is already present
        for(String path : paths) {
            if(path.equals(pathToAdd)) {
                return;
            }
        }

        //add the new path
        String[] newPaths = Arrays.copyOf(paths, paths.length + 1);
        newPaths[newPaths.length-1] = pathToAdd;
        usrPathsField.set(null, newPaths);
    }

    private Java3DUtils() {
    }

    public static void initializeLibraryPath() throws Exception {
        File theJava3DFile = new File("java3d");
        File theJava3DLibraryFile = null;
        if (SystemUtils.IS_OS_WINDOWS) {
            if (VM_32_BIT) {
                theJava3DLibraryFile =  new File(theJava3DFile, "win32");
            } else {
                theJava3DLibraryFile =  new File(theJava3DFile, "win64");
            }
        }
        if (SystemUtils.IS_OS_LINUX) {
            if (VM_32_BIT) {
                theJava3DLibraryFile =  new File(theJava3DFile, "linux32");
            } else {
                theJava3DLibraryFile =  new File(theJava3DFile, "linux64");
            }
        }
        if (SystemUtils.IS_OS_MAC) {
            theJava3DLibraryFile =  new File(theJava3DFile, "macos-universal");
        }
        if (theJava3DLibraryFile != null) {
            addLibraryPath(theJava3DLibraryFile.getAbsolutePath());
        }
    }
}

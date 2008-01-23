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
package de.erdesignerng.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Vector;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Class for handling application preferences, LRUfiles and so on.
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2008-01-23 21:01:23 $
 */
public class ApplicationPreferences {

    private static final String LRUPREFIX = "file_";
    
    private static final String DOT_PATH = "DOT_PATH";

    private static final String CLASSPATHPREFIX = "classpath_";

    private int size;

    private List<File> LRUfiles = new Vector<File>();

    private List<File> classpathfiles = new Vector<File>();

    private Preferences preferences;
    
    private String dotPath;
    
    private static ApplicationPreferences me;
    
    public static ApplicationPreferences getInstance() {
        
        if (me == null) {
            try {
                me = new ApplicationPreferences(20);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return me;
    }

    protected ApplicationPreferences(int aSize) throws BackingStoreException {

        preferences = Preferences.userNodeForPackage(ApplicationPreferences.class);
        String[] theNames = preferences.keys();
        for (String theName : theNames) {
            if (theName.startsWith(LRUPREFIX)) {
                File theFile = new File(preferences.get(theName, ""));
                if (theFile.exists()) {
                    LRUfiles.add(theFile);
                }
            }
            if (theName.startsWith(CLASSPATHPREFIX)) {
                File theFile = new File(preferences.get(theName, ""));
                if (theFile.exists()) {
                    classpathfiles.add(theFile);
                }
            }

        }

        size = aSize;
        
        dotPath = preferences.get(DOT_PATH, "");

    }

    /**
     * Add a file to the recently used LRUfiles list.
     * 
     * @param aFile
     *            the file to add
     */
    public void addLRUFile(File aFile) {

        if (!LRUfiles.contains(aFile)) {
            LRUfiles.add(aFile);
            if (LRUfiles.size() > size) {
                LRUfiles.remove(0);
            }
        } else {
            LRUfiles.remove(aFile);
            LRUfiles.add(0, aFile);
        }
    }

    public List<File> getLRUfiles() {
        return LRUfiles;
    }

    public List<File> getClasspathFiles() {
        return classpathfiles;
    }

    /**
     * Save the preferences.
     * 
     * @throws BackingStoreException is thrown if the operation fails
     */
    public void store() throws BackingStoreException {

        String[] theNames = preferences.childrenNames();
        for (String theName : theNames) {
            if (theName.startsWith(LRUPREFIX)) {
                preferences.remove(theName);
            }
            if (theName.startsWith(CLASSPATHPREFIX)) {
                preferences.remove(theName);
            }
        }

        for (int i = 0; i < LRUfiles.size(); i++) {
            preferences.put(LRUPREFIX + i, LRUfiles.get(i).toString());
        }

        for (int i = 0; i < classpathfiles.size(); i++) {
            preferences.put(CLASSPATHPREFIX + i, classpathfiles.get(i).toString());
        }

        preferences.put(DOT_PATH, dotPath);
        
        preferences.flush();
    }

    public ClassLoader createDriverClassLoader() {

        URL[] theUrls = new URL[classpathfiles.size()];
        for (int i = 0; i < classpathfiles.size(); i++) {
            try {
                theUrls[i] = classpathfiles.get(i).toURL();
            } catch (MalformedURLException e) {
                // This will never happen
            }
        }

        return new URLClassLoader(theUrls);
    }

    /**
     * @return the dotPath
     */
    public String getDotPath() {
        return dotPath;
    }

    /**
     * @param dotPath the dotPath to set
     */
    public void setDotPath(String dotPath) {
        this.dotPath = dotPath;
    }
}

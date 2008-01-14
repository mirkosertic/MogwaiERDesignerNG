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
 * @version $Date: 2008-01-14 20:01:16 $
 */
public class ApplicationPreferences {

    private static final String LRUPREFIX = "file_";

    private static final String CLASSPATHPREFIX = "classpath_";

    private int size;

    private List<File> LRUfiles = new Vector<File>();

    private List<File> classpathfiles = new Vector<File>();

    private Preferences preferences;

    public ApplicationPreferences(Object aOwner, int aSize) throws BackingStoreException {

        preferences = Preferences.userNodeForPackage(aOwner.getClass());
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
     * @throws BackingStoreException
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
}

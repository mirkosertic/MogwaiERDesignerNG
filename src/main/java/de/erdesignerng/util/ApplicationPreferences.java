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

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.JFrame;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Class for handling application preferences, LRUfiles and so on.
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-13 15:40:33 $
 */
public class ApplicationPreferences {

    private static final Logger LOGGER = Logger.getLogger(ApplicationPreferences.class);

    private static final String LRUPREFIX = "file_";

    private static final String DOT_PATH = "DOT_PATH";

    private static final String CLASSPATHPREFIX = "classpath_";

    private static final String LRCPREFIX = "lrc_";

    private static final String RPCPREFIX = "rpc_";

    private static final String GRIDSIZE = "gridsize";

    private static final String WINDOWSTATEPREFIX = "windowstate_";

    private static final String WINDOWXPREFIX = "windowx_";

    private static final String WINDOWYPREFIX = "windowy_";

    private static final String WINDOWWIDTHPREFIX = "windowswidth_";

    private static final String WINDOWHEIGHTPREFIX = "windowheight_";

    private int size;

    private List<File> recentlyUsedFiles = new ArrayList<File>();

    private List<File> classpathfiles = new ArrayList<File>();

    private List<ConnectionDescriptor> recentlyUsedConnections = new ArrayList<ConnectionDescriptor>();

    private Preferences preferences;

    private String dotPath;

    private int gridSize;

    private ConnectionDescriptor repositoryConnection;

    private Map<String, String> windowDefinitions = new HashMap<String, String>();

    private static ApplicationPreferences me;

    public static ApplicationPreferences getInstance() {

        if (me == null) {
            try {
                me = new ApplicationPreferences(20);
            } catch (Exception e) {
                LOGGER.error("Error loading preferences", e);
            }
        }
        return me;
    }

    protected ApplicationPreferences(int aSize) throws BackingStoreException {

        preferences = Preferences.userNodeForPackage(ApplicationPreferences.class);
        List<String> theNames = Arrays.asList(preferences.keys());
        for (String theName : theNames) {

            // Locate the window defaults here
            if (theName.startsWith("window")) {
                windowDefinitions.put(theName, preferences.get(theName, null));
            }
            if (theName.startsWith(LRUPREFIX)) {
                File theFile = new File(preferences.get(theName, ""));
                if ((theFile.exists()) && (!recentlyUsedFiles.contains(theFile))) {
                    recentlyUsedFiles.add(theFile);
                }
            }
            if (theName.startsWith(CLASSPATHPREFIX)) {
                File theFile = new File(preferences.get(theName, ""));
                if (theFile.exists()) {
                    classpathfiles.add(theFile);
                }
            }

        }

        if (theNames.contains(RPCPREFIX + "DIALECT")) {
            String theDialect = preferences.get(RPCPREFIX + "DIALECT", "");
            String theURL = preferences.get(RPCPREFIX + "URL", "");
            String theUser = preferences.get(RPCPREFIX + "USER", "");
            String theDriver = preferences.get(RPCPREFIX + "DRIVER", "");
            String thePass = preferences.get(RPCPREFIX + "PASS", "");

            repositoryConnection = new ConnectionDescriptor(null, theDialect, theURL, theUser, theDriver, thePass,
                    false);
        }

        for (int i = 0; i < aSize; i++) {
            if (theNames.contains(LRCPREFIX + "DIALECT_" + i)) {
                String theAlias = preferences.get(LRCPREFIX + "ALIAS_" + i, "");
                String theDialect = preferences.get(LRCPREFIX + "DIALECT_" + i, "");
                String theURL = preferences.get(LRCPREFIX + "URL_" + i, "");
                String theUser = preferences.get(LRCPREFIX + "USER_" + i, "");
                String theDriver = preferences.get(LRCPREFIX + "DRIVER_" + i, "");
                String thePass = preferences.get(LRCPREFIX + "PASS_" + i, "");
                String thePrompt = preferences.get(LRCPREFIX + "PROMPT_" + i, "");
                boolean theBooleanPrompt = false;
                if (!StringUtils.isEmpty(thePrompt)) {
                    theBooleanPrompt = Boolean.parseBoolean(thePrompt);
                }

                ConnectionDescriptor theConnection = new ConnectionDescriptor(theAlias, theDialect, theURL, theUser,
                        theDriver, thePass, theBooleanPrompt);
                if (!recentlyUsedConnections.contains(theConnection)) {
                    recentlyUsedConnections.add(theConnection);
                }
            }
        }

        size = aSize;
        gridSize = preferences.getInt(GRIDSIZE, 10);

        dotPath = preferences.get(DOT_PATH, "");

    }

    /**
     * Add a file to the recently used LRUfiles list.
     * 
     * @param aFile
     *                the file to add
     */
    public void addRecentlyUsedFile(File aFile) {

        if (!recentlyUsedFiles.contains(aFile)) {
            recentlyUsedFiles.add(aFile);
            if (recentlyUsedFiles.size() > size) {
                recentlyUsedFiles.remove(0);
            }
        } else {
            recentlyUsedFiles.remove(aFile);
            recentlyUsedFiles.add(0, aFile);
        }
    }

    /**
     * Add a last used connection to the list.
     * 
     * @param aConnection
     *                the connection
     */
    public void addRecentlyUsedConnection(ConnectionDescriptor aConnection) {
        if (!recentlyUsedConnections.contains(aConnection)) {
            recentlyUsedConnections.add(aConnection);
            if (recentlyUsedConnections.size() > size) {
                recentlyUsedConnections.remove(0);
            }
        } else {
            recentlyUsedConnections.remove(aConnection);
            recentlyUsedConnections.add(0, aConnection);
        }

    }

    public List<File> getRecentlyUsedFiles() {
        return recentlyUsedFiles;
    }

    public List<ConnectionDescriptor> getRecentlyUsedConnections() {
        return recentlyUsedConnections;
    }

    public List<File> getClasspathFiles() {
        return classpathfiles;
    }

    /**
     * @return the gridSize
     */
    public int getGridSize() {
        return gridSize;
    }

    /**
     * @param gridSize
     *                the gridSize to set
     */
    public void setGridSize(int gridSize) {
        this.gridSize = gridSize;
    }

    /**
     * Save the preferences.
     * 
     * @throws BackingStoreException
     *                 is thrown if the operation fails
     */
    public void store() throws BackingStoreException {

        String[] theNames = preferences.childrenNames();
        for (String theName : theNames) {
            if (theName.startsWith(LRUPREFIX)) {
                preferences.remove(theName);
            }
            if (theName.startsWith(LRCPREFIX)) {
                preferences.remove(theName);
            }
            if (theName.startsWith(CLASSPATHPREFIX)) {
                preferences.remove(theName);
            }
            if (theName.startsWith(RPCPREFIX)) {
                preferences.remove(theName);
            }
        }

        for (int i = 0; i < recentlyUsedFiles.size(); i++) {
            preferences.put(LRUPREFIX + i, recentlyUsedFiles.get(i).toString());
        }

        for (int i = 0; i < recentlyUsedConnections.size(); i++) {
            ConnectionDescriptor theConnection = recentlyUsedConnections.get(i);
            if (!StringUtils.isEmpty(theConnection.getAlias())) {
                preferences.put(LRCPREFIX + "ALIAS_" + i, theConnection.getAlias());
            }
            preferences.put(LRCPREFIX + "DIALECT_" + i, theConnection.getDialect());
            preferences.put(LRCPREFIX + "URL_" + i, theConnection.getUrl());
            preferences.put(LRCPREFIX + "USER_" + i, theConnection.getUsername());
            preferences.put(LRCPREFIX + "DRIVER_" + i, theConnection.getDriver());
            preferences.put(LRCPREFIX + "PASS_" + i, theConnection.getPassword());
            preferences.put(LRCPREFIX + "PROMPT_" + i, Boolean.toString(theConnection.isPromptForPassword()));
        }

        for (int i = 0; i < classpathfiles.size(); i++) {
            preferences.put(CLASSPATHPREFIX + i, classpathfiles.get(i).toString());
        }

        preferences.put(DOT_PATH, dotPath);
        preferences.putInt(GRIDSIZE, gridSize);

        if (repositoryConnection != null) {
            preferences.put(RPCPREFIX + "DIALECT", repositoryConnection.getDialect());
            preferences.put(RPCPREFIX + "URL", repositoryConnection.getUrl());
            preferences.put(RPCPREFIX + "USER", repositoryConnection.getUsername());
            preferences.put(RPCPREFIX + "DRIVER", repositoryConnection.getDriver());
            preferences.put(RPCPREFIX + "PASS", repositoryConnection.getPassword());
        }

        for (Map.Entry<String, String> theWindowEntry : windowDefinitions.entrySet()) {
            preferences.put(theWindowEntry.getKey(), theWindowEntry.getValue());
        }

        preferences.flush();
    }

    public ClassLoader createDriverClassLoader() {

        final URL[] theUrls = new URL[classpathfiles.size()];
        for (int i = 0; i < classpathfiles.size(); i++) {
            try {
                theUrls[i] = classpathfiles.get(i).toURL();
            } catch (MalformedURLException e) {
                // This will never happen
            }
        }

        return AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {

            public ClassLoader run() {
                return new URLClassLoader(theUrls, Thread.currentThread().getContextClassLoader());
            }
        });
    }

    /**
     * @return the dotPath
     */
    public String getDotPath() {
        return dotPath;
    }

    /**
     * @param dotPath
     *                the dotPath to set
     */
    public void setDotPath(String dotPath) {
        this.dotPath = dotPath;
    }

    /**
     * @return the repositoryConnection
     */
    public ConnectionDescriptor getRepositoryConnection() {
        return repositoryConnection;
    }

    /**
     * @param repositoryConnection
     *                the repositoryConnection to set
     */
    public void setRepositoryConnection(ConnectionDescriptor repositoryConnection) {
        this.repositoryConnection = repositoryConnection;
    }

    /**
     * Update the last position of a window.
     * 
     * @param aAlias
     *                the alias of the window
     * @param aWindow
     *                the window
     */
    public void updateWindowDefinition(String aAlias, JFrame aWindow) {
        windowDefinitions.put(WINDOWSTATEPREFIX + aAlias, "" + aWindow.getExtendedState());
        updateWindowLocation(aAlias, aWindow);
        updateWindowSize(aAlias, aWindow);
    }

    /**
     * Set the current window state as stored by updateWindowDefinition.
     * 
     * @param aAlias
     *                the alias of the window
     * @param aFrame
     *                the window
     */
    public void setWindowState(String aAlias, JFrame aFrame) {

        if (windowDefinitions.containsKey(WINDOWSTATEPREFIX + aAlias)) {
            try {
                aFrame.setExtendedState(Integer.parseInt(windowDefinitions.get(WINDOWSTATEPREFIX + aAlias)));
                setWindowLocation(aAlias, aFrame);
                setWindowSize(aAlias, aFrame);

            } catch (Exception e) {
                aFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            }
        }
    }

    public void updateWindowLocation(String aAlias, Window aWindow) {
        Point theLocation = aWindow.getLocation();
        windowDefinitions.put(WINDOWXPREFIX + aAlias, "" + theLocation.x);
        windowDefinitions.put(WINDOWYPREFIX + aAlias, "" + theLocation.y);
    }

    public void updateWindowSize(String aAlias, Window aWindow) {
        Dimension theSize = aWindow.getSize().getSize();
        windowDefinitions.put(WINDOWWIDTHPREFIX + aAlias, "" + theSize.width);
        windowDefinitions.put(WINDOWHEIGHTPREFIX + aAlias, "" + theSize.height);
    }

    public void setWindowSize(String aAlias, Window aWindow) {
        try {
            int width = Integer.parseInt(windowDefinitions.get(WINDOWWIDTHPREFIX + aAlias));
            int height = Integer.parseInt(windowDefinitions.get(WINDOWHEIGHTPREFIX + aAlias));

            aWindow.setSize(width, height);
        } catch (Exception e) {
            // If no old size is known, an Exception is thrown
            // This can be ignored
        }
    }

    public void setWindowLocation(String aAlias, Window aWindow) {
        try {
            int x = Integer.parseInt(windowDefinitions.get(WINDOWXPREFIX + aAlias));
            int y = Integer.parseInt(windowDefinitions.get(WINDOWYPREFIX + aAlias));

            // Only set the size and location if its within the available
            // screen resolution
            Dimension theCurrentScreenSize = Toolkit.getDefaultToolkit().getScreenSize();
            if (x < theCurrentScreenSize.width && y < theCurrentScreenSize.height) {
                aWindow.setLocation(x, y);
            }
        } catch (Exception e) {
            // If no old location is known, an Exception is thrown
            // This can be ignored
        }
    }
}
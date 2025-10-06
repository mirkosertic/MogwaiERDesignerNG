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

import de.erdesignerng.model.CascadeType;
import de.erdesignerng.visual.EditorMode;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.*;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Class for handling application preferences, LRU-files and so on.
 *
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-13 15:40:33 $
 */
public class ApplicationPreferences {

	private static final Logger LOGGER = Logger
			.getLogger(ApplicationPreferences.class);

	private static final String LRUPREFIX = "file_";

	private static final String CLASSPATHPREFIX = "classpath_";

	private static final String LRCPREFIX = "lrc_";

	private static final String RPCPREFIX = "rpc_";

	private static final String GRIDSIZE = "gridsize";

	private static final String XMLINDENTATION = "xmlindentation";

	private static final String TABLEGRIDWIDTHAFTERREVERSEENGINEERING = "tablegridwidthafterre";

	private static final String AUTOMATICRELATIONATTRIBUTEPATTERN = "automaticrelationattributepattern";

	private static final String ONUPDATEDEFAULT = "onupdatedefault";

	private static final String ONDELETEDEFAULT = "onupdatedefault";

	private static final String WINDOWSTATEPREFIX = "windowstate_";

	private static final String WINDOWXPREFIX = "windowx_";

	private static final String WINDOWYPREFIX = "windowy_";

	private static final String WINDOWWIDTHPREFIX = "windowswidth_";

	private static final String WINDOWHEIGHTPREFIX = "windowheight_";

	private static final String INTELLIGENTLAYOUT = "intelligentlayout_";

	private static final String LAYOUT = "layout";

	private static final String EDITORMODE = "editormode";

	private final int size;

	private final List<File> recentlyUsedFiles = new ArrayList<>();

	private final List<File> classpathfiles = new ArrayList<>();

	private final List<ConnectionDescriptor> recentlyUsedConnections = new ArrayList<>();

	private final Preferences preferences;

	private int gridSize;

	private int xmlIndentation;

	private ConnectionDescriptor repositoryConnection;

	private final Map<String, String> windowDefinitions = new HashMap<>();

	private File baseDir;

	private boolean intelligentLayout = true;

	private String automaticRelationAttributePattern;

	private CascadeType onUpdateDefault;

	private CascadeType onDeleteDefault;

	private byte[] windowLayout;

	private int gridWidthAfterReverseEngineering;

	private EditorMode editorMode;

	private static ApplicationPreferences me;

	public static ApplicationPreferences getInstance() {

		if (me == null) {
			try {
				me = new ApplicationPreferences();
			} catch (final BackingStoreException e) {
				LOGGER.error("Error loading preferences", e);
			}
		}
		return me;
	}

	protected ApplicationPreferences() throws BackingStoreException {

		preferences = Preferences
				.userNodeForPackage(ApplicationPreferences.class);
		final List<String> theNames = Arrays.asList(preferences.keys());
		for (final String theName : theNames) {

			// Locate the window defaults here
			if (theName.startsWith("window")) {
				windowDefinitions.put(theName, preferences.get(theName, null));
			}
			if (theName.startsWith(LRUPREFIX)) {
				final File theFile = new File(preferences.get(theName, ""));
				if ((theFile.exists())
						&& (!recentlyUsedFiles.contains(theFile))) {
					recentlyUsedFiles.add(theFile);
				}
			}
			if (theName.startsWith(CLASSPATHPREFIX)) {
				final File theFile = new File(preferences.get(theName, ""));
				if (theFile.exists()) {
					classpathfiles.add(theFile);
				}
			}

		}

		if (theNames.contains(RPCPREFIX + "DIALECT")) {
			final String theDialect = preferences.get(RPCPREFIX + "DIALECT", "");
			final String theURL = preferences.get(RPCPREFIX + "URL", "");
			final String theUser = preferences.get(RPCPREFIX + "USER", "");
			final String theDriver = preferences.get(RPCPREFIX + "DRIVER", "");
			final String thePass = preferences.get(RPCPREFIX + "PASS", "");

			repositoryConnection = new ConnectionDescriptor(null, theDialect,
					theURL, theUser, theDriver, thePass, false);
		}

		for (int i = 0; i < 20; i++) {
			if (theNames.contains(LRCPREFIX + "DIALECT_" + i)) {
				final String theAlias = preferences.get(LRCPREFIX + "ALIAS_" + i, "");
				final String theDialect = preferences.get(LRCPREFIX + "DIALECT_" + i,
						"");
				final String theURL = preferences.get(LRCPREFIX + "URL_" + i, "");
				final String theUser = preferences.get(LRCPREFIX + "USER_" + i, "");
				final String theDriver = preferences.get(LRCPREFIX + "DRIVER_" + i,
						"");
				final String thePass = preferences.get(LRCPREFIX + "PASS_" + i, "");
				final String thePrompt = preferences.get(LRCPREFIX + "PROMPT_" + i,
						"");
				boolean theBooleanPrompt = false;
				if (StringUtils.isNotEmpty(thePrompt)) {
					theBooleanPrompt = Boolean.parseBoolean(thePrompt);
				}

				final ConnectionDescriptor theConnection = new ConnectionDescriptor(
						theAlias, theDialect, theURL, theUser, theDriver,
						thePass, theBooleanPrompt);
				if (!recentlyUsedConnections.contains(theConnection)) {
					recentlyUsedConnections.add(theConnection);
				}
			}
		}

		size = 20;
		gridSize = preferences.getInt(GRIDSIZE, 10);
		intelligentLayout = preferences.getBoolean(INTELLIGENTLAYOUT, true);
		automaticRelationAttributePattern = preferences.get(
				AUTOMATICRELATIONATTRIBUTEPATTERN, "FK_{0}_{1}");
		windowLayout = preferences.getByteArray(LAYOUT, new byte[0]);
		gridWidthAfterReverseEngineering = preferences.getInt(
				TABLEGRIDWIDTHAFTERREVERSEENGINEERING, 8);
		xmlIndentation = preferences.getInt(XMLINDENTATION, 4);

		onUpdateDefault = CascadeType.fromString(preferences.get(ONUPDATEDEFAULT, CascadeType.NOTHING.toString()));
		onDeleteDefault = CascadeType.fromString(preferences.get(ONDELETEDEFAULT, CascadeType.NOTHING.toString()));

		final String theMode = preferences.get(EDITORMODE, null);
		if (theMode == null) {
			editorMode = EditorMode.CLASSIC;
		} else {
			editorMode = EditorMode.valueOf(theMode);
		}
	}

	/**
	 * Add a file to the least recently used (LRU) files list.
	 *
	 * @param aFile the file to add
	 */
	public void addRecentlyUsedFile(final File aFile) {

		if (!recentlyUsedFiles.contains(aFile)) {
			recentlyUsedFiles.add(aFile);
			if (recentlyUsedFiles.size() > size) {
				recentlyUsedFiles.removeFirst();
			}
		} else {
			recentlyUsedFiles.remove(aFile);
			recentlyUsedFiles.addFirst(aFile);
		}
	}

	/**
	 * Add a last used connection to the list.
	 *
	 * @param aConnection the connection
	 */
	public void addRecentlyUsedConnection(final ConnectionDescriptor aConnection) {
		if (!recentlyUsedConnections.contains(aConnection)) {
			recentlyUsedConnections.add(aConnection);
			if (recentlyUsedConnections.size() > size) {
				recentlyUsedConnections.removeFirst();
			}
		} else {
			recentlyUsedConnections.remove(aConnection);
			recentlyUsedConnections.addFirst(aConnection);
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
	 * @param gridSize the gridSize to set
	 */
	public void setGridSize(final int gridSize) {
		this.gridSize = gridSize;
	}

	/**
	 * Save the preferences.
	 *
	 * @throws BackingStoreException is thrown if the operation fails
	 */
	public void store() throws BackingStoreException {

		final String[] theNames = preferences.childrenNames();
		for (final String theName : theNames) {
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
			final ConnectionDescriptor theConnection = recentlyUsedConnections.get(i);
			if (StringUtils.isNotEmpty(theConnection.getAlias())) {
				preferences.put(LRCPREFIX + "ALIAS_" + i, theConnection
						.getAlias());
			}
			preferences.put(LRCPREFIX + "DIALECT_" + i, theConnection
					.getDialect());
			preferences.put(LRCPREFIX + "URL_" + i, theConnection.getUrl());
			preferences.put(LRCPREFIX + "USER_" + i, theConnection
					.getUsername());
			preferences.put(LRCPREFIX + "DRIVER_" + i, theConnection
					.getDriver());
			preferences.put(LRCPREFIX + "PASS_" + i, theConnection
					.getPassword());
			preferences.put(LRCPREFIX + "PROMPT_" + i, Boolean
					.toString(theConnection.isPromptForPassword()));
		}

		for (int i = 0; i < classpathfiles.size(); i++) {
			preferences.put(CLASSPATHPREFIX + i, classpathfiles.get(i)
					.toString());
		}

		preferences.putInt(GRIDSIZE, gridSize);
		preferences.putInt(XMLINDENTATION, xmlIndentation);
		preferences.put(AUTOMATICRELATIONATTRIBUTEPATTERN,
				automaticRelationAttributePattern);
		preferences.putBoolean(INTELLIGENTLAYOUT, intelligentLayout);
		preferences.put(ONUPDATEDEFAULT, onUpdateDefault.toString());
		preferences.put(ONDELETEDEFAULT, onDeleteDefault.toString());
		preferences.putByteArray(LAYOUT, windowLayout);
		preferences.putInt(TABLEGRIDWIDTHAFTERREVERSEENGINEERING,
				gridWidthAfterReverseEngineering);

		if (repositoryConnection != null) {
			preferences.put(RPCPREFIX + "DIALECT", repositoryConnection
					.getDialect());
			preferences.put(RPCPREFIX + "URL", repositoryConnection.getUrl());
			preferences.put(RPCPREFIX + "USER", repositoryConnection
					.getUsername());
			preferences.put(RPCPREFIX + "DRIVER", repositoryConnection
					.getDriver());
			preferences.put(RPCPREFIX + "PASS", repositoryConnection
					.getPassword());
		}

		for (final Map.Entry<String, String> theWindowEntry : windowDefinitions
				.entrySet()) {
			preferences.put(theWindowEntry.getKey(), theWindowEntry.getValue());
		}

		preferences.put(EDITORMODE, editorMode.toString());

		preferences.flush();
	}

	public ClassLoader createDriverClassLoader() {

		final URL[] theUrls = new URL[classpathfiles.size()];
		for (int i = 0; i < classpathfiles.size(); i++) {
			try {
				theUrls[i] = classpathfiles.get(i).toURI().toURL();
			} catch (final MalformedURLException e) {
				// This will never happen
			}
		}

		return AccessController
				.doPrivileged((PrivilegedAction<ClassLoader>) () -> new URLClassLoader(theUrls, Thread
                        .currentThread().getContextClassLoader()));
	}

	/**
	 * @return the repositoryConnection
	 */
	public ConnectionDescriptor getRepositoryConnection() {
		return repositoryConnection;
	}

	/**
	 * @param repositoryConnection the repositoryConnection to set
	 */
	public void setRepositoryConnection(
            final ConnectionDescriptor repositoryConnection) {
		this.repositoryConnection = repositoryConnection;
	}

	/**
	 * Update the last position of a window.
	 *
	 * @param aAlias  the alias of the window
	 * @param aWindow the window
	 */
	public void updateWindowDefinition(final String aAlias, final JFrame aWindow) {
		windowDefinitions.put(WINDOWSTATEPREFIX + aAlias, ""
				+ aWindow.getExtendedState());
		updateWindowLocation(aAlias, aWindow);
		updateWindowSize(aAlias, aWindow);
	}

	/**
	 * Set the current window state as stored by updateWindowDefinition.
	 *
	 * @param aAlias the alias of the window
	 * @param aFrame the window
	 */
	public void setWindowState(final String aAlias, final JFrame aFrame) {

		if (windowDefinitions.containsKey(WINDOWSTATEPREFIX + aAlias)) {
			try {
				aFrame.setExtendedState(Integer.parseInt(windowDefinitions
						.get(WINDOWSTATEPREFIX + aAlias)));
				setWindowLocation(aAlias, aFrame);
				setWindowSize(aAlias, aFrame);

			} catch (final NumberFormatException e) {
				aFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
			}
		}
	}

	public void updateWindowLocation(final String aAlias, final Window aWindow) {
		final Point theLocation = aWindow.getLocation();
		windowDefinitions.put(WINDOWXPREFIX + aAlias, "" + theLocation.x);
		windowDefinitions.put(WINDOWYPREFIX + aAlias, "" + theLocation.y);
	}

	public void updateWindowSize(final String aAlias, final Window aWindow) {
		final Dimension theSize = aWindow.getSize().getSize();
		windowDefinitions.put(WINDOWWIDTHPREFIX + aAlias, "" + theSize.width);
		windowDefinitions.put(WINDOWHEIGHTPREFIX + aAlias, "" + theSize.height);
	}

	public void setWindowSize(final String aAlias, final Window aWindow) {
		try {
			final int width = Integer.parseInt(windowDefinitions
					.get(WINDOWWIDTHPREFIX + aAlias));
			final int height = Integer.parseInt(windowDefinitions
					.get(WINDOWHEIGHTPREFIX + aAlias));

			aWindow.setSize(width, height);
		} catch (final NumberFormatException e) {
			// If no old size is known, an Exception is thrown
			// This can be ignored
		}
	}

	public void setWindowLocation(final String aAlias, final Window aWindow) {
		try {
			final int x = Integer.parseInt(windowDefinitions.get(WINDOWXPREFIX
					+ aAlias));
			final int y = Integer.parseInt(windowDefinitions.get(WINDOWYPREFIX
					+ aAlias));

			// Only set the size and location if its within the available
			// screen resolution
			final Dimension theCurrentScreenSize = Toolkit.getDefaultToolkit()
					.getScreenSize();
			if (x < theCurrentScreenSize.width
					&& y < theCurrentScreenSize.height) {
				aWindow.setLocation(x, y);
			}
		} catch (final HeadlessException | NumberFormatException e) {
			// If no old location is known, an Exception is thrown
			// This can be ignored
		}
	}

	/**
	 * @return the baseDir
	 */
	public File getBaseDir() {
		return baseDir;
	}

	/**
	 * @param baseDir the baseDir to set
	 */
	public void setBaseDir(final File baseDir) {
		this.baseDir = baseDir;
	}

	/**
	 * @return the intelligentLayout
	 */
	public boolean isIntelligentLayout() {
		return intelligentLayout;
	}

	/**
	 * @param intelligentLayout the intelligentLayout to set
	 */
	public void setIntelligentLayout(final boolean intelligentLayout) {
		this.intelligentLayout = intelligentLayout;
	}

	public File getRelativeFile(final String aName) {
		if (baseDir != null) {
			return new File(baseDir, aName);
		}
		return new File(aName);
	}

	/**
	 * Get the directory where the report templates are located.
	 *
	 * @return the directory
	 */
	public File getReportsDirectory() {
		return getRelativeFile("reports");
	}

	/**
	 * Test if we are running in development mode.
	 *
	 * @return true, if in development mode; false else
	 */
	public boolean isInDevelopmentMode() {
		final String theVersionNumber = MavenPropertiesLocator.getERDesignerVersionInfo();

		return theVersionNumber.equals(MavenPropertiesLocator.CANNOT_IDENTIFY_VERSION);
	}

	/**
	 * Get the directory where the datatype configuration is located.
	 *
	 * @return the directory
	 */
	public File getDatatypeConfigDirectory() {
		final File theUserHomeFile = SystemUtils.getUserHome();
		if (theUserHomeFile == null) {
			return getRelativeFile("dataTypes");
		}

		String theVersionNumber = MavenPropertiesLocator
				.getERDesignerVersionInfo();
		if (theVersionNumber
				.equals(MavenPropertiesLocator.CANNOT_IDENTIFY_VERSION)) {
			theVersionNumber = "development";
		}
		theVersionNumber = theVersionNumber.replace(".", "_");
		theVersionNumber = theVersionNumber.replace(" ", "_");
		theVersionNumber = theVersionNumber.replace("-", "_");

		final File theMogwaiHome = new File(theUserHomeFile, ".mogwai");
		final File theVersionHome = new File(theMogwaiHome, theVersionNumber);
		return new File(theVersionHome, "dataTypes");
	}

	/**
	 * Get the online help pdf file.
	 *
	 * @return the file
	 * @throws java.lang.Exception
	 */
	public URI getOnlineHelpPDFFile() throws Exception {
		return new URL("http://mogwai.sourceforge.net/userdoc/MogwaiERDesignerNG.pdf").toURI();
	}

	public String getAutomaticRelationAttributePattern() {
		return automaticRelationAttributePattern;
	}

	public void setAutomaticRelationAttributePattern(
            final String automaticRelationAttributePattern) {
		this.automaticRelationAttributePattern = automaticRelationAttributePattern;
	}

	public CascadeType getOnUpdateDefault() {
		return onUpdateDefault;
	}

	public void setOnUpdateDefault(final CascadeType onUpdateDefault) {
		this.onUpdateDefault = onUpdateDefault;
	}

	public CascadeType getOnDeleteDefault() {
		return onDeleteDefault;
	}

	public void setOnDeleteDefault(final CascadeType onDeleteDefault) {
		this.onDeleteDefault = onDeleteDefault;
	}

	public byte[] getWindowLayout() {
		return windowLayout;
	}

	public void setWindowLayout(final byte[] windowLayout) {
		this.windowLayout = windowLayout;
	}

	public int getGridWidthAfterReverseEngineering() {
		return gridWidthAfterReverseEngineering;
	}

	public void setGridWidthAfterReverseEngineering(
            final int gridWidthAfterReverseEngineering) {
		this.gridWidthAfterReverseEngineering = gridWidthAfterReverseEngineering;
	}

	public int getXmlIndentation() {
		return xmlIndentation;
	}

	public void setXmlIndentation(final int xmlIndentation) {
		this.xmlIndentation = xmlIndentation;
	}

	public EditorMode getEditorMode() {
		return editorMode;
	}

	public void setEditorMode(final EditorMode editorMode) {
		this.editorMode = editorMode;
	}
}
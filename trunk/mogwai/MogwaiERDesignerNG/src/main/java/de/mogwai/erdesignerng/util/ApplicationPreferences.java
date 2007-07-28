package de.mogwai.erdesignerng.util;

import java.io.File;
import java.util.List;
import java.util.Vector;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Class for handling application preferences, lru files and so on.
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2007-07-28 14:35:50 $
 */
public class ApplicationPreferences {

	private final static String PREFIX = "file_";

	private int size;

	private List<File> files = new Vector<File>();

	private Preferences preferences;

	public ApplicationPreferences(Object aOwner, int aSize)
			throws BackingStoreException {

		preferences = Preferences.userNodeForPackage(aOwner.getClass());
		String[] theNames = preferences.keys();
		for (String theName : theNames) {
			if (theName.startsWith(PREFIX)) {
				File theFile = new File(preferences.get(theName, ""));
				if (theFile.exists()) {
					files.add(theFile);
				}
			}
		}

		size = aSize;

	}

	/**
	 * Add a file to the recently used files list.
	 * 
	 * @param aFile the file to add
	 */
	public void addFile(File aFile) {

		if (!files.contains(aFile)) {
			files.add(aFile);
			if (files.size() > size) {
				files.remove(0);
			}
		} else {
			files.remove(aFile);
			files.add(0, aFile);
		}
	}

	public List<File> getFiles() {
		return files;
	}

	/**
	 * Save the preferences.
	 * 
	 * @throws BackingStoreException
	 */
	public void store() throws BackingStoreException {

		String[] theNames = preferences.childrenNames();
		for (String theName : theNames) {
			if (theName.startsWith(PREFIX)) {
				preferences.remove(theName);
			}
		}

		for (int i = 0; i < files.size(); i++) {
			preferences.put(PREFIX + i, files.get(i).toString());
		}

		preferences.flush();
	}
}

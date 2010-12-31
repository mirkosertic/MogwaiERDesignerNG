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
package de.erdesignerng.plugins.squirrel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.prefs.BackingStoreException;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import de.erdesignerng.dialect.DataTypeIO;
import de.erdesignerng.dialect.Dialect;
import de.erdesignerng.plugins.squirrel.action.StartMogwaiAction;
import de.erdesignerng.plugins.squirrel.dialect.SquirrelDialect;
import de.erdesignerng.plugins.squirrel.preferences.SquirrelMogwaiPreferences;
import de.erdesignerng.util.ApplicationPreferences;
import de.erdesignerng.util.MavenPropertiesLocator;
import de.mogwai.common.client.looks.UIConfiguration;
import de.mogwai.common.client.looks.UIInitializer;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-11-15 14:21:15 $
 */
public class SquirrelMogwaiPluginDelegate extends DefaultSessionPlugin {

	private final Hashtable<IIdentifier, SquirrelMogwaiController[]> controllersBySessionID = new Hashtable<IIdentifier, SquirrelMogwaiController[]>();

	private ApplicationPreferences preferences;

	private SquirrelMogwaiPreferences preferencesPanel;

	public SquirrelMogwaiPluginDelegate() {
	}

	public String getInternalName() {
		return "mogwai";
	}

	public String getDescriptiveName() {
		return "Mogwai ERDesigner";
	}

	public String getVersion() {
		return MavenPropertiesLocator.getERDesignerVersionInfo();
	}

	public String getAuthor() {
		return "Mirko Sertic";
	}

	@Override
	public String getChangeLogFileName() {
		return "RELEASENOTES.txt";
	}

	@Override
	public String getHelpFileName() {
		return "readme.html";
	}

	@Override
	public String getLicenceFileName() {
		return "licence.txt";
	}

	@Override
	public synchronized void initialize() throws PluginException {
		super.initialize();

		try {
			// Initialize the application base directory
			ApplicationPreferences thePreferences = ApplicationPreferences.getInstance();
			thePreferences.setBaseDir(getPluginAppSettingsFolder());

			DataTypeIO.getInstance().loadUserTypes(thePreferences);
		} catch (Exception e) {
			throw new PluginException(e);
		}

		// Initialize Mogwai Looks
		UIConfiguration theConfig = new UIConfiguration();
		theConfig.setApplyConfiguration(false);
		UIInitializer.getInstance(theConfig);

		SquirrelMogwaiPluginResources resources= new SquirrelMogwaiPluginResources(this);
		preferences = ApplicationPreferences.getInstance();

		preferencesPanel = new SquirrelMogwaiPreferences(this, preferences);

		IApplication theApplication = getApplication();

		ActionCollection theActionCollection = theApplication.getActionCollection();
		theActionCollection.add(new StartMogwaiAction(theApplication, resources, this));
	}

	@Override
	public IGlobalPreferencesPanel[] getGlobalPreferencePanels() {
		return new IGlobalPreferencesPanel[] { preferencesPanel };
	}

	@Override
	public void unload() {
		try {
			preferences.store();
		} catch (BackingStoreException e) {
			// Nothing will happen here
		}
		super.unload();
	}

	public PluginSessionCallback sessionStarted(final ISession session) {

		IObjectTreeAPI theAPI = session.getSessionInternalFrame().getObjectTreeAPI();

		ActionCollection theActionCollection = getApplication().getActionCollection();
		theAPI.addToPopup(DatabaseObjectType.CATALOG, theActionCollection.get(StartMogwaiAction.class));
		theAPI.addToPopup(DatabaseObjectType.SCHEMA, theActionCollection.get(StartMogwaiAction.class));

		PluginSessionCallback ret = new PluginSessionCallback() {

			public void sqlInternalFrameOpened(SQLInternalFrame sqlInternalFrame, ISession session) {
			}

			public void objectTreeInternalFrameOpened(ObjectTreeInternalFrame objectTreeInternalFrame, ISession session) {
			}
		};

		return ret;
	}

	@Override
	public void sessionEnding(ISession session) {
		SquirrelMogwaiController[] theControllers = controllersBySessionID.remove(session.getIdentifier());
		if (theControllers != null) {
			for (SquirrelMogwaiController theController : theControllers) {
				theController.sessionEnding();
			}
		}
	}

	public SquirrelMogwaiController[] getGraphControllers(ISession session) {
		return controllersBySessionID.get(session.getIdentifier());
	}

	public SquirrelMogwaiController createNewGraphControllerForSession(ISession aSession, Dialect aDialect) {

		SquirrelDialect theSquirrelDialect = new SquirrelDialect(aDialect, aSession);

		SquirrelMogwaiController[] theControllers = controllersBySessionID.get(aSession.getIdentifier());

		List<SquirrelMogwaiController> theTemp = new ArrayList<SquirrelMogwaiController>();
		if (null != theControllers) {
			theTemp.addAll(Arrays.asList(theControllers));
		}
		SquirrelMogwaiController theResult = new SquirrelMogwaiController(theSquirrelDialect, aSession, this);
		theTemp.add(theResult);

		theControllers = theTemp.toArray(new SquirrelMogwaiController[theTemp.size()]);
		controllersBySessionID.put(aSession.getIdentifier(), theControllers);

		return theResult;
	}

	public void removeGraphController(SquirrelMogwaiController toRemove, ISession session) {
		SquirrelMogwaiController[] theControllers = controllersBySessionID.get(session.getIdentifier());
		List<SquirrelMogwaiController> theTemp = new ArrayList<SquirrelMogwaiController>();
		for (SquirrelMogwaiController theController : theControllers) {
			if (!theController.equals(toRemove)) {
				theTemp.add(theController);
			}
		}

		theControllers = theTemp.toArray(new SquirrelMogwaiController[theTemp.size()]);
		controllersBySessionID.put(session.getIdentifier(), theControllers);
	}

	public void shutdownEditor(SquirrelMogwaiController controller) {
	}

	/**
	 * The preferences were changed, so they need to be published to all
	 * controllers.
	 */
	public void refreshPreferences() {
		for (SquirrelMogwaiController[] theControllers : controllersBySessionID.values()) {
			for (SquirrelMogwaiController theController : theControllers) {
				theController.refreshPreferences(preferences);
			}
		}
	}
}
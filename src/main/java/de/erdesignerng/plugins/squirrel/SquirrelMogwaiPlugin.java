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

import java.util.Arrays;
import java.util.Hashtable;
import java.util.Vector;
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
 * @version $Date: 2008-06-13 16:48:58 $
 */
public class SquirrelMogwaiPlugin extends DefaultSessionPlugin {

    private Hashtable<IIdentifier, SquirrelMogwaiController[]> controllersBySessionID = new Hashtable<IIdentifier, SquirrelMogwaiController[]>();

    private SquirrelMogwaiPluginResources resources;

    private ApplicationPreferences preferences;

    private SquirrelMogwaiPreferences preferencesPanel;

    /**
     * {@inheritDoc}
     */
    public String getInternalName() {
        return "mogwai";
    }

    /**
     * {@inheritDoc}
     */
    public String getDescriptiveName() {
        return "Mogwai ERDesigner";
    }

    /**
     * {@inheritDoc}
     */
    public String getVersion() {
        return MavenPropertiesLocator.getERDesignerVersionInfo();
    }

    /**
     * {@inheritDoc}
     */
    public String getAuthor() {
        return "Mirko Sertic";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getChangeLogFileName() {
        return "RELEASENOTES.txt";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getHelpFileName() {
        return "readme.html";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLicenceFileName() {
        return "licence.txt";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void initialize() throws PluginException {
        super.initialize();

        // Initialize Mogwai Looks
        UIConfiguration theConfig = new UIConfiguration();
        theConfig.setApplyConfiguration(false);
        UIInitializer.getInstance(theConfig);

        IApplication theApplication = getApplication();

        resources = new SquirrelMogwaiPluginResources(this);
        preferences = ApplicationPreferences.getInstance();

        preferencesPanel = new SquirrelMogwaiPreferences(this, preferences);

        ActionCollection theActionCollection = theApplication.getActionCollection();
        theActionCollection.add(new StartMogwaiAction(theApplication, resources, this));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IGlobalPreferencesPanel[] getGlobalPreferencePanels() {
        return new IGlobalPreferencesPanel[] { preferencesPanel };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unload() {
        try {
            preferences.store();
        } catch (BackingStoreException e) {
            // Nothing will happen here
        }
        super.unload();
    }

    /**
     * {@inheritDoc}
     */
    public PluginSessionCallback sessionStarted(final ISession session) {

        IObjectTreeAPI theAPI = session.getSessionInternalFrame().getObjectTreeAPI();

        ActionCollection theActionCollection = getApplication().getActionCollection();
        theAPI.addToPopup(DatabaseObjectType.CATALOG, theActionCollection.get(StartMogwaiAction.class));
        theAPI.addToPopup(DatabaseObjectType.SCHEMA, theActionCollection.get(StartMogwaiAction.class));

        PluginSessionCallback ret = new PluginSessionCallback() {

            public void sqlInternalFrameOpened(SQLInternalFrame sqlInternalFrame, ISession sess) {
            }

            public void objectTreeInternalFrameOpened(ObjectTreeInternalFrame objectTreeInternalFrame, ISession sess) {
            }
        };

        return ret;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sessionEnding(ISession session) {
        SquirrelMogwaiController[] theControllers = controllersBySessionID.remove(session.getIdentifier());
        if (theControllers != null) {
            for (int i = 0; i < theControllers.length; i++) {
                theControllers[i].sessionEnding();
            }
        }
    }

    public SquirrelMogwaiController[] getGraphControllers(ISession session) {
        return controllersBySessionID.get(session.getIdentifier());
    }

    public SquirrelMogwaiController createNewGraphControllerForSession(ISession aSession, Dialect aDialect) {

        SquirrelDialect theSquirrelDialect = new SquirrelDialect(aDialect, aSession);

        SquirrelMogwaiController[] theControllers = controllersBySessionID.get(aSession.getIdentifier());

        Vector<SquirrelMogwaiController> theTemp = new Vector<SquirrelMogwaiController>();
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
        Vector<SquirrelMogwaiController> theTemp = new Vector<SquirrelMogwaiController>();
        for (int i = 0; i < theControllers.length; i++) {
            if (!theControllers[i].equals(toRemove)) {
                theTemp.add(theControllers[i]);
            }
        }

        theControllers = theTemp.toArray(new SquirrelMogwaiController[theTemp.size()]);
        controllersBySessionID.put(session.getIdentifier(), theControllers);
    }

    public void shutdownEditor(SquirrelMogwaiController controller) {
    }

    /**
     * The preferences were changed, so they need to be published to all controllers. 
     */
    public void refreshPreferences() {
        for (SquirrelMogwaiController[] theControllers : controllersBySessionID.values()) {
            for (SquirrelMogwaiController theController : theControllers) {
                theController.refreshPreferences(preferences);
            }
        }
    }
}
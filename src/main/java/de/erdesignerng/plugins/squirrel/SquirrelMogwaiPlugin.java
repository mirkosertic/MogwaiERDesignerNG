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

import de.erdesignerng.plugins.squirrel.action.StartMogwaiAction;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-01-18 21:05:26 $
 */
public class SquirrelMogwaiPlugin extends DefaultSessionPlugin {

    private Hashtable<IIdentifier, SquirrelMogwaiController[]> controllersBySessionID = new Hashtable<IIdentifier, SquirrelMogwaiController[]>();

    private static ILogger LOGGER = LoggerController.createLogger(SquirrelMogwaiPlugin.class);

    private SquirrelMogwaiPluginResources resources;

    public String getInternalName() {
        return "mogwai";
    }

    public String getDescriptiveName() {
        return "Mogwai ERDesigner";
    }

    public String getVersion() {
        return "1.0";
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
        IApplication theApplication = getApplication();

        resources = new SquirrelMogwaiPluginResources(this);

        ActionCollection theActionCollection = theApplication.getActionCollection();
        theActionCollection.add(new StartMogwaiAction(theApplication, resources, this));
    }

    @Override
    public void unload() {
        super.unload();
    }

    public PluginSessionCallback sessionStarted(final ISession session) {
        SquirrelMogwaiController[] theControllers = new SquirrelMogwaiController[0];

        for (int i = 0; i < theControllers.length; i++) {
            theControllers[i] = new SquirrelMogwaiController(session, this);
        }

        controllersBySessionID.put(session.getIdentifier(), theControllers);

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

    @Override
    public void sessionEnding(ISession session) {
        SquirrelMogwaiController[] theControllers = controllersBySessionID.remove(session.getIdentifier());

        for (int i = 0; i < theControllers.length; i++) {
            theControllers[i].sessionEnding();
        }
    }

    public SquirrelMogwaiController[] getGraphControllers(ISession session) {
        return controllersBySessionID.get(session.getIdentifier());
    }


    public SquirrelMogwaiController createNewGraphControllerForSession(ISession session) {
        SquirrelMogwaiController[] theControllers = controllersBySessionID.get(session.getIdentifier());

        Vector<SquirrelMogwaiController> theTemp = new Vector<SquirrelMogwaiController>();
        if (null != theControllers) {
            theTemp.addAll(Arrays.asList(theControllers));
        }
        SquirrelMogwaiController theResult = new SquirrelMogwaiController(session, this);
        theTemp.add(theResult);

        theControllers = theTemp.toArray(new SquirrelMogwaiController[theTemp.size()]);
        controllersBySessionID.put(session.getIdentifier(), theControllers);

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
}
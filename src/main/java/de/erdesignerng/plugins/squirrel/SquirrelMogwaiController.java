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

import de.erdesignerng.plugins.squirrel.dialect.SquirrelDialect;
import net.sourceforge.squirrel_sql.client.session.ISession;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-06-13 16:48:58 $
 */
public class SquirrelMogwaiController {

    private final ISession session;

    private final SquirrelMogwaiPluginDelegate plugin;

    private final SquirrelMogwaiTabSheet tabsheet;

    private final SquirrelDialect dialect;

    public SquirrelMogwaiController(SquirrelDialect aDialect,
                                    ISession aSession, SquirrelMogwaiPluginDelegate aPlugin) {
        session = aSession;
        plugin = aPlugin;
        dialect = aDialect;

        tabsheet = new SquirrelMogwaiTabSheet(this);

        session.getSessionSheet().selectMainTab(
                session.getSessionSheet().addMainTab(tabsheet));
    }

    public void sessionEnding() {
        tabsheet.sessionEnding(session);
    }

    public void startReverseEngineering() {
        tabsheet.startReverseEngineering();
    }

    public void notifyAboutException(Exception aException) {
        session.showErrorMessage(aException);
    }

    public SquirrelMogwaiPluginDelegate getPlugin() {
        return plugin;
    }

    public void exitApplication() {
        plugin.shutdownEditor();
    }

    public SquirrelDialect getDialect() {
        return dialect;
    }

    public ISession getSession() {
        return session;
    }

    /**
     * The preferences were changed, so they need to be reconfigured.
     *
     * @param aPreferences the preferences
     */
    public void refreshPreferences() {
        tabsheet.refreshPreferences();
    }
}
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

import net.sourceforge.squirrel_sql.client.session.ISession;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-01-18 21:05:27 $
 */
public class SquirrelMogwaiController {

    private ISession session;

    private SquirrelMogwaiPlugin plugin;
    
    private SquirrelMogwaiTabSheet tabsheet;

    public SquirrelMogwaiController(ISession aSession, SquirrelMogwaiPlugin aPlugin) {
        session = aSession;
        plugin = aPlugin;
        
        tabsheet = new SquirrelMogwaiTabSheet(aSession, aPlugin);
        
        session.getSessionSheet().selectMainTab(
                session.getSessionSheet().addMainTab(tabsheet));
    }

    public void sessionEnding() {
    }
}
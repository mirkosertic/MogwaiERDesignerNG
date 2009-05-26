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
package de.erdesignerng.plugins.squirrel.preferences;

import java.awt.Component;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.plugins.squirrel.SquirrelMogwaiPluginDelegate;
import de.erdesignerng.util.ApplicationPreferences;
import de.erdesignerng.visual.editor.preferences.PreferencesPanel;
import de.mogwai.common.i18n.ResourceHelper;

/**
 * The preferences dialog for Squirrel.
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2009-03-13 15:40:34 $
 */
public class SquirrelMogwaiPreferences implements IGlobalPreferencesPanel {

    private PreferencesPanel panel;
    
    private ApplicationPreferences preferences;
    
    private SquirrelMogwaiPluginDelegate plugin;
    
    public SquirrelMogwaiPreferences(SquirrelMogwaiPluginDelegate aPlugin, ApplicationPreferences aPreferences) {
        preferences = aPreferences;
        plugin = aPlugin;
        panel = new PreferencesPanel();
    }
    
    public void initialize(IApplication aApplication) {
        panel.initValues(preferences);
    }

    public void uninitialize(IApplication aApplication) {
    }

    public void applyChanges() {
        if (!panel.applyValues(preferences)) {
            plugin.refreshPreferences();
            //TODO [mirkosertic] How to prevent it from closing if validation fails?
        }
    }

    public String getHint() {
        return "";
    }

    public Component getPanelComponent() {
        return panel;
    }

    public String getTitle() {
        return ResourceHelper.getResourceHelper(ERDesignerBundle.BUNDLE_NAME).getText(ERDesignerBundle.TITLE);
    }
}

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
package de.erdesignerng.visual.editor.preferences;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.util.ApplicationPreferences;
import de.mogwai.common.client.binding.BindingInfo;
import de.mogwai.common.client.looks.UIInitializer;
import de.mogwai.common.client.looks.components.DefaultLabel;
import de.mogwai.common.client.looks.components.DefaultPanel;
import de.mogwai.common.client.looks.components.DefaultSpinner;
import de.mogwai.common.i18n.ResourceHelper;
import de.mogwai.common.i18n.ResourceHelperProvider;

public class PreferencesPanel extends DefaultPanel implements ResourceHelperProvider {

    private DefaultSpinner gridSize = new DefaultSpinner();
    
    private BindingInfo<ApplicationPreferences> bindinginfo;

    public PreferencesPanel() {
        initialize();
    }

    private void initialize() {

        String theColDef = "2dlu,p,2dlu,p:grow,2dlu,20dlu,2";
        String theRowDef = "2dlu,p,2dlu,p,50dlu";

        FormLayout theLayout = new FormLayout(theColDef, theRowDef);
        setLayout(theLayout);

        CellConstraints cons = new CellConstraints();

        add(new DefaultLabel(ERDesignerBundle.EDITORGRIDSIZE), cons.xy(2, 2));
        add(gridSize, cons.xywh(4, 2 , 3, 1));

        UIInitializer.getInstance().initialize(this);
        
        bindinginfo = new BindingInfo<ApplicationPreferences>();
        bindinginfo.addBinding("gridSize", gridSize, true);        
        
        bindinginfo.configure();
    }
    
    /**
     * Initialize the view with values from the preferences.
     * 
     * @param aPreferences the preferences
     */
    public void initValues(ApplicationPreferences aPreferences) {
        bindinginfo.setDefaultModel(aPreferences);
        bindinginfo.model2view();
    }
    
    /**
     * Apply the current view values to the preferences after validation.
     * if validation fails, no changes are made.
     *  
     * @param aPreferences the preferences
     * @return true if validation is ok, else false
     */
    public boolean applyValues(ApplicationPreferences aPreferences) {
        
        bindinginfo.setDefaultModel(aPreferences);
        
        if (bindinginfo.validate().size() == 0) {
            bindinginfo.view2model();
            return true;
        }
        return false;
    }

    public ResourceHelper getResourceHelper() {
        return ResourceHelper.getResourceHelper(ERDesignerBundle.BUNDLE_NAME);
    }
}
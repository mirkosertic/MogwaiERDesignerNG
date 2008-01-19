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
package de.erdesignerng.visual;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.model.Model;
import de.erdesignerng.visual.common.ERDesignerComponent;
import de.erdesignerng.visual.common.ERDesignerWorldConnector;
import de.mogwai.common.client.looks.UIInitializer;
import de.mogwai.common.client.looks.components.DefaultFrame;
import de.mogwai.common.client.looks.components.DefaultToolbar;
import de.mogwai.common.i18n.ResourceHelper;

/**
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2008-01-19 15:25:31 $
 */
public class ERDesignerMainFrame extends DefaultFrame implements ERDesignerWorldConnector {

    private ERDesignerComponent component;

    public ERDesignerMainFrame() {
        super(ERDesignerBundle.TITLE);
        initialize();
        
        setSize(800, 600);
        
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                component.savePreferences();
            }
        });
        
        UIInitializer.getInstance().initialize(this);
    }

    @Override
    public ResourceHelper getResourceHelper() {
        return ResourceHelper.getResourceHelper(ERDesignerBundle.BUNDLE_NAME);
    }

    private void initialize() {
        component = new ERDesignerComponent(this);
        getDefaultFrameContent().setDetailComponent(component.getDetailComponent());
    }

    public void initTitle() {
        initTitle(null);
    }

    public DefaultToolbar getToolBar() {
        return getDefaultFrameContent().getToolbar();
    }

    public void initTitle(File aFile) {

        StringBuffer theTitle = new StringBuffer();
        if (aFile != null) {
            theTitle.append(" - ").append(aFile.toString());
        }

        setTitle(getResourceHelper().getText(getResourceBundleID()) + theTitle);
        
    }

    public void setStatusText(String aMessage) {
        getDefaultFrameContent().getStatusBar().setText(aMessage);
    }

    public void setModel(Model aModel) {
        component.setModel(aModel);
    }

    public boolean supportsClasspathEditor() {
        return true;
    }

    public boolean supportsConnectionEditor() {
        return true;
    }

    public boolean supportsExitApplication() {
        return true;
    }

    public Model createNewModel() {
        return new Model();
    }
}
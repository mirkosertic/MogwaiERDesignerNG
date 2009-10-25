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

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.model.Model;
import de.erdesignerng.modificationtracker.HistoryModificationTracker;
import de.erdesignerng.util.ApplicationPreferences;
import de.erdesignerng.util.MavenPropertiesLocator;
import de.erdesignerng.visual.common.DockingHelper;
import de.erdesignerng.visual.common.ERDesignerComponent;
import de.erdesignerng.visual.common.ERDesignerWorldConnector;
import de.erdesignerng.visual.common.OutlineComponent;
import de.erdesignerng.visual.editor.exception.ExceptionEditor;
import de.mogwai.common.client.looks.UIInitializer;
import de.mogwai.common.client.looks.components.DefaultFrame;
import de.mogwai.common.client.looks.components.DefaultToolbar;
import de.mogwai.common.i18n.ResourceHelper;

/**
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2008-11-06 22:01:08 $
 */
public class ERDesignerMainFrame extends DefaultFrame implements ERDesignerWorldConnector {

    private static final String WINDOW_ALIAS = "ERDesignerMainFrame";

    private ERDesignerComponent component;

    private final ApplicationPreferences preferences;

    private OutlineComponent outlineComponent;
    
    private DockingHelper dockingHelper;

    public ERDesignerMainFrame(ApplicationPreferences aPreferences) {
        super(ERDesignerBundle.TITLE);

        preferences = aPreferences;

        initialize();

        setSize(800, 600);
        setExtendedState(MAXIMIZED_BOTH);

        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                preferences.updateWindowDefinition(WINDOW_ALIAS, ERDesignerMainFrame.this);
                dockingHelper.saveLayoutToPreferences();
                component.savePreferences();
            }
        });

        UIInitializer.getInstance().initialize(this);
        initTitle();
    }

    @Override
    public ResourceHelper getResourceHelper() {
        return ResourceHelper.getResourceHelper(ERDesignerBundle.BUNDLE_NAME);
    }

    private void initialize() {
        component = new ERDesignerComponent(preferences, this);
        outlineComponent = new OutlineComponent(component);

        dockingHelper = new DockingHelper(preferences, component, outlineComponent);
        
        try {
            dockingHelper.initialize();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        getDefaultFrameContent().setDetailComponent(dockingHelper.getRootWindow());
    }

    @Override
    public void initTitle() {
        initTitle(null);
    }

    public DefaultToolbar getToolBar() {
        return getDefaultFrameContent().getToolbar();
    }

    @Override
    public void initTitle(String aFile) {

        StringBuffer theTitle = new StringBuffer();
        if (aFile != null) {
            theTitle.append(" - ").append(aFile);
        }

        String theVersion = MavenPropertiesLocator.getERDesignerVersionInfo();
        setTitle(getResourceHelper().getText(getResourceBundleID()) + " " + theVersion + " " + theTitle);
    }

    @Override
    public void setStatusText(String aMessage) {
        getDefaultFrameContent().getStatusBar().setText(aMessage);
    }

    public void setModel(Model aModel) {
        component.setModel(aModel);
    }

    @Override
    public boolean supportsClasspathEditor() {
        return true;
    }

    @Override
    public boolean supportsConnectionEditor() {
        return true;
    }

    @Override
    public boolean supportsExitApplication() {
        return true;
    }

    @Override
    public Model createNewModel() {
        Model theModel = new Model();
        theModel.setModificationTracker(new HistoryModificationTracker(theModel));
        return theModel;
    }

    @Override
    public boolean supportsPreferences() {
        return true;
    }

    @Override
    public void initializeLoadedModel(Model aModel) {
        aModel.setModificationTracker(new HistoryModificationTracker(aModel));
    }

    @Override
    public void notifyAboutException(Exception aException) {
        ExceptionEditor theEditor = new ExceptionEditor(this, aException);
        theEditor.showModal();
    }

    public void exitApplication() {
        component.savePreferences();
        System.exit(0);
    }

    @Override
    public void setVisible(boolean aVisible) {
        super.setVisible(aVisible);

        if (aVisible) {
            preferences.setWindowState(WINDOW_ALIAS, this);
        }
    }

    @Override
    public boolean supportsRepositories() {
        return true;
    }

    @Override
    public boolean supportsHelp() {
        return true;
    }

    @Override
    public boolean supportsReporting() {
        return true;
    }

    @Override
    public OutlineComponent getOutlineComponent() {
        return outlineComponent;
    }
}
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

import java.awt.Component;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.BaseMainPanelTab;
import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.model.Model;
import de.erdesignerng.modificationtracker.HistoryModificationTracker;
import de.erdesignerng.util.ApplicationPreferences;
import de.erdesignerng.visual.common.DockingHelper;
import de.erdesignerng.visual.common.ERDesignerComponent;
import de.erdesignerng.visual.common.ERDesignerWorldConnector;
import de.erdesignerng.visual.common.OutlineComponent;
import de.erdesignerng.visual.common.ReverseEngineerCommand;
import de.mogwai.common.client.looks.UIInitializer;
import de.mogwai.common.client.looks.components.DefaultFrameContent;
import de.mogwai.common.client.looks.components.DefaultToolbar;
import de.mogwai.common.client.looks.components.menu.DefaultMenuBar;
import de.mogwai.common.i18n.ResourceHelper;

public class SquirrelMogwaiTabSheet extends BaseMainPanelTab implements ERDesignerWorldConnector {

    private DockingHelper dockingHelper;
    
    private ERDesignerComponent component;
    
    private OutlineComponent outline;

    private ResourceHelper helper = ResourceHelper.getResourceHelper(ERDesignerBundle.BUNDLE_NAME);

    private DefaultFrameContent content = new DefaultFrameContent();

    private ResourceProviderPanel panel = new ResourceProviderPanel();

    private String title;

    private SquirrelMogwaiController controller;

    public SquirrelMogwaiTabSheet(SquirrelMogwaiController aController) {

        controller = aController;
        
        ApplicationPreferences thePreferences = ApplicationPreferences.getInstance();

        component = new ERDesignerComponent(thePreferences, this);
        outline = new OutlineComponent();
        
        dockingHelper = new DockingHelper(thePreferences, component, outline);
        try {
            dockingHelper.initialize();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        content.setDetailComponent(dockingHelper.getRootWindow());
        panel.setContent(content);

        component.setModel(createNewModel());

        UIInitializer.getInstance().initialize(panel);
    }

    @Override
    protected void refreshComponent() {
    }

    public Component getComponent() {
        return panel;
    }

    public String getHint() {
        return "";
    }

    public String getTitle() {
        return title;
    }

    public DefaultMenuBar getComponentMenuBar() {
        return new DefaultMenuBar();
    }

    public DefaultToolbar getToolBar() {
        return content.getToolbar();
    }

    @Override
    public void initTitle(String aFile) {
        StringBuffer theTitle = new StringBuffer();
        if (aFile != null) {
            theTitle.append(" - ").append(aFile);
        }

        title = helper.getText(ERDesignerBundle.TITLE) + theTitle;

    }

    @Override
    public void initTitle() {
        initTitle(null);
    }

    public void setStatusText(String aMessage) {
        content.getStatusBar().setText(aMessage);
        controller.getSession().showMessage(aMessage);
    }

    @Override
    public boolean supportsClasspathEditor() {
        return false;
    }

    @Override
    public boolean supportsConnectionEditor() {
        return false;
    }

    @Override
    public boolean supportsExitApplication() {
        return false;
    }

    @Override
    public Model createNewModel() {
        Model theModel = new Model();
        theModel.setDialect(controller.getDialect());
        theModel.setModificationTracker(new HistoryModificationTracker(theModel));
        return theModel;
    }

    @Override
    public void sessionEnding(ISession aSession) {
        super.sessionEnding(aSession);

        dockingHelper.saveLayoutToPreferences();
        component.savePreferences();
    }

    public void startReverseEngineering() {
        new ReverseEngineerCommand(component).execute();
    }

    @Override
    public boolean supportsPreferences() {
        return false;
    }

    @Override
    public void initializeLoadedModel(Model aModel) {
        aModel.setDialect(controller.getDialect());
        aModel.setModificationTracker(new HistoryModificationTracker(aModel));
    }

    @Override
    public void notifyAboutException(Exception aException) {
        controller.notifyAboutException(aException);
    }

    public void exitApplication() {
        controller.exitApplication();
    }

    /**
     * The preferences were changed, so they need to be reloaded.
     * 
     * @param aPreferences
     *            the preferences
     */
    public void refreshPreferences(ApplicationPreferences aPreferences) {
        component.refreshPreferences(aPreferences);
    }

    @Override
    public boolean supportsRepositories() {
        return false;
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
        return outline;
    }
}
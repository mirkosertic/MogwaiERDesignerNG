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
import java.io.File;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.BaseMainPanelTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.model.Model;
import de.erdesignerng.plugins.squirrel.dialect.SquirrelDialect;
import de.erdesignerng.util.ApplicationPreferences;
import de.erdesignerng.visual.common.ERDesignerComponent;
import de.erdesignerng.visual.common.ERDesignerWorldConnector;
import de.mogwai.common.client.looks.UIInitializer;
import de.mogwai.common.client.looks.components.DefaultFrameContent;
import de.mogwai.common.client.looks.components.DefaultToolbar;
import de.mogwai.common.client.looks.components.menu.DefaultMenuBar;
import de.mogwai.common.i18n.ResourceHelper;

public class SquirrelMogwaiTabSheet extends BaseMainPanelTab implements ERDesignerWorldConnector {

    private ISession session;
    
    private SquirrelMogwaiPlugin plugin;
    
    private ERDesignerComponent component;
    
    private ResourceHelper helper = ResourceHelper.getResourceHelper(ERDesignerBundle.BUNDLE_NAME);
    
    private DefaultFrameContent content = new DefaultFrameContent();
    
    private ResourceProviderPanel panel = new ResourceProviderPanel();
    
    private String title;
    
    private ObjectTreeNode node;
    
    public SquirrelMogwaiTabSheet(ApplicationPreferences aPreferences, ISession aSession, SquirrelMogwaiPlugin aPlugin, ObjectTreeNode aNode) {
        session = aSession;
        plugin = aPlugin;
        node = aNode;
        component = new ERDesignerComponent(aPreferences, this);
        
        content.setDetailComponent(component.getDetailComponent());
        panel.setContent(content);

        Model theModel = new Model();
        theModel.setDialect(new SquirrelDialect(aSession, aNode));
        component.setModel(theModel);

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

    public void initTitle(File aFile) {
        StringBuffer theTitle = new StringBuffer();
        if (aFile != null) {
            theTitle.append(" - ").append(aFile.toString());
        }

        title = helper.getText(ERDesignerBundle.TITLE) + theTitle;
        
    }

    public void initTitle() {
        initTitle(null);
    }

    public void setStatusText(String aMessage) {
        content.getStatusBar().setText(aMessage);
    }

    public boolean supportsClasspathEditor() {
        return false;
    }

    public boolean supportsConnectionEditor() {
        return false;
    }

    public boolean supportsExitApplication() {
        return false;
    }

    public Model createNewModel() {
        Model theModel = new Model();
        theModel.setDialect(new SquirrelDialect(session, node));
        return theModel;
    }

    @Override
    public void sessionEnding(ISession aSession) {
        super.sessionEnding(aSession);
        
        component.savePreferences();
    }

    public void startReverseEngineering() {
        component.commandReverseEngineer();
    }

    public boolean supportsPreferences() {
        return false;
    }

    public void initializeLoadedModel(Model aModel) {
    }

    public void notifyAboutException(Exception aException) {
        session.showErrorMessage(aException);
    }
}
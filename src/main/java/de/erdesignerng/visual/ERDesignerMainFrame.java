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

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.model.Model;
import de.erdesignerng.modificationtracker.HistoryModificationTracker;
import de.erdesignerng.util.ApplicationPreferences;
import de.erdesignerng.util.MavenPropertiesLocator;
import de.erdesignerng.visual.common.*;
import de.erdesignerng.visual.editor.exception.ExceptionEditor;
import de.mogwai.common.client.looks.UIInitializer;
import de.mogwai.common.client.looks.components.DefaultFrame;
import de.mogwai.common.client.looks.components.DefaultToolbar;
import de.mogwai.common.i18n.ResourceHelper;
import org.apache.log4j.Logger;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-11-06 22:01:08 $
 */
public class ERDesignerMainFrame extends DefaultFrame implements
        ERDesignerWorldConnector {

    private static final Logger LOGGER = Logger.getLogger(ERDesignerMainFrame.class);

    private static final String WINDOW_ALIAS = "ERDesignerMainFrame";

    private ERDesignerComponent component;

    private DockingHelper dockingHelper;

    public ERDesignerMainFrame() {
        super(ERDesignerBundle.TITLE);

        initialize();

        setSize(800, 600);
        setExtendedState(MAXIMIZED_BOTH);
        setIconImage(IconFactory.getERDesignerIcon().getImage());

        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                exitApplication();
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

        component = ERDesignerComponent.initializeComponent(this);
        OutlineComponent.initializeComponent();
        SQLComponent.initializeComponent();
        dockingHelper = new DockingHelper();

        try {
            dockingHelper.initialize();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        getDefaultFrameContent().setDetailComponent(
                dockingHelper.getRootWindow());
    }

    @Override
    public final void initTitle() {
        initTitle(null);
    }

    @Override
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
        setTitle(getResourceHelper().getText(getResourceBundleID()) + " "
                + theVersion + " " + theTitle);
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
        theModel
                .setModificationTracker(new HistoryModificationTracker(theModel));
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

    @Override
    public void exitApplication() {
        ApplicationPreferences.getInstance().updateWindowDefinition(
                WINDOW_ALIAS, this);
        dockingHelper.saveLayoutToPreferences();
        component.savePreferences();
        setVisible(false);
    }

    @Override
    public void setVisible(boolean aVisible) {

        super.setVisible(aVisible);

        if (aVisible) {
            ApplicationPreferences.getInstance().setWindowState(WINDOW_ALIAS,
                    this);
        } else {
            component.savePreferences();
            System.exit(0);
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

    /**
     * Open a specific file in the editor.
     *
     * @param aFile
     */
    public void commandOpenFile(File aFile) {
        component.commandOpenFile(aFile);
    }
}
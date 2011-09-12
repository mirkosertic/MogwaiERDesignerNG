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
package de.erdesignerng.visual.common;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.util.ApplicationPreferences;
import de.mogwai.common.client.looks.UIInitializer;
import de.mogwai.common.i18n.ResourceHelper;
import de.mogwai.common.i18n.ResourceHelperProvider;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import javax.swing.SwingUtilities;

import net.infonode.docking.*;
import net.infonode.docking.util.DockingUtil;
import net.infonode.docking.util.ViewMap;
import net.infonode.util.Direction;
import org.apache.log4j.Logger;

public class DockingHelper extends DockingWindowAdapter implements
        ResourceHelperProvider {

    private static final Logger LOGGER = Logger.getLogger(DockingHelper.class);

    private RootWindow rootWindow;

    public void initialize() throws InterruptedException,
            InvocationTargetException {

        final ViewMap theViewMap = new ViewMap();
        final View[] theViews = new View[4];
        theViews[0] = new View(getResourceHelper().getFormattedText(
                ERDesignerBundle.EDITOR), null, ERDesignerComponent
                .getDefault().getDetailComponent());
        theViews[0].getWindowProperties().setCloseEnabled(false);
        theViews[0].getWindowProperties().setUndockEnabled(false);
        theViews[0].getWindowProperties().setUndockOnDropEnabled(false);
        theViews[1] = new View(getResourceHelper().getFormattedText(
                ERDesignerBundle.OUTLINE), null, OutlineComponent.getDefault());
        theViews[1].getWindowProperties().setCloseEnabled(false);
        theViews[1].getWindowProperties().setUndockEnabled(false);
        theViews[1].getWindowProperties().setUndockOnDropEnabled(false);
        theViews[2] = new View(getResourceHelper().getFormattedText(
                ERDesignerBundle.SQL), null, SQLComponent.getDefault());
        theViews[2].getWindowProperties().setCloseEnabled(false);
        theViews[2].getWindowProperties().setUndockEnabled(false);
        theViews[2].getWindowProperties().setUndockOnDropEnabled(false);
        theViewMap.addView(0, theViews[0]);
        theViewMap.addView(1, theViews[1]);
        theViewMap.addView(2, theViews[2]);

        Runnable theRunnable = new Runnable() {

            @Override
            public void run() {

                Thread.currentThread().setContextClassLoader(
                        DockingHelper.class.getClassLoader());

                rootWindow = DockingUtil.createRootWindow(theViewMap, true);
                byte[] windowLayout = ApplicationPreferences.getInstance()
                        .getWindowLayout();
                boolean layoutRestored = false;
                if (windowLayout != null && windowLayout.length > 0) {
                    try {
                        rootWindow.read(new ObjectInputStream(
                                new ByteArrayInputStream(windowLayout)));
                        layoutRestored = true;
                        LOGGER.info("Workbench layout restored");
                    } catch (Exception e) {
                        LOGGER.error("Failed to restore window state", e);
                    }
                }

                if (!layoutRestored) {
                    SplitWindow theRightWindow = new SplitWindow(false, 0.8f,
                            theViews[1], theViews[2]);
                    SplitWindow theSplitWindow = new SplitWindow(true, 0.8f,
                            theViews[0], theRightWindow);
                    rootWindow.setWindow(theSplitWindow);
                }

                rootWindow.getWindowBar(Direction.RIGHT).setEnabled(true);
                rootWindow.getRootWindowProperties().setRecursiveTabsEnabled(
                        false);
            }

        };

        // The Docking initialization must be performed in the EDT. If we are
        // not there,
        // invoke it there, else invoke it directly
        if (SwingUtilities.isEventDispatchThread()) {
            theRunnable.run();
        } else {
            SwingUtilities.invokeAndWait(theRunnable);
        }

        theViews[0].addListener(this);
        theViews[1].addListener(this);
        theViews[2].addListener(this);
    }

    public RootWindow getRootWindow() {
        return rootWindow;
    }

    public void saveLayoutToPreferences() {
        ByteArrayOutputStream theBos = new ByteArrayOutputStream();
        ObjectOutputStream theOs;
        try {
            theOs = new ObjectOutputStream(theBos);
            rootWindow.write(theOs);
            theOs.close();

            ApplicationPreferences.getInstance().setWindowLayout(
                    theBos.toByteArray());

            LOGGER.info("Workbench layout saved. ");
        } catch (IOException e) {
            LOGGER.error("Failed to store window state", e);
        }
    }

    @Override
    public ResourceHelper getResourceHelper() {
        return ResourceHelper.getResourceHelper(ERDesignerBundle.BUNDLE_NAME);
    }

    @Override
    public void windowRestored(DockingWindow aWindow) {
        UIInitializer.getInstance().initialize(rootWindow);
    }

    @Override
    public void windowAdded(DockingWindow arg0, DockingWindow arg1) {
        UIInitializer.getInstance().initialize(rootWindow);
    }
}
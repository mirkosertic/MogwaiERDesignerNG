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

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import net.infonode.docking.DockingWindow;
import net.infonode.docking.DockingWindowAdapter;
import net.infonode.docking.RootWindow;
import net.infonode.docking.SplitWindow;
import net.infonode.docking.View;
import net.infonode.docking.util.DockingUtil;
import net.infonode.docking.util.ViewMap;
import net.infonode.util.Direction;
import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.util.ApplicationPreferences;
import de.mogwai.common.i18n.ResourceHelper;
import de.mogwai.common.i18n.ResourceHelperProvider;

public class DockingHelper extends DockingWindowAdapter implements ResourceHelperProvider {

    private ERDesignerComponent component;

    private OutlineComponent outline;

    private RootWindow rootWindow;

    private ApplicationPreferences preferences;

    public DockingHelper(ApplicationPreferences aPreferences, ERDesignerComponent aComponent, OutlineComponent aOutline) {
        component = aComponent;
        outline = aOutline;
        preferences = aPreferences;
    }

    public void initialize() throws InterruptedException, InvocationTargetException {

        final ViewMap theViewMap = new ViewMap();
        final View[] theViews = new View[2];
        theViews[0] = new View(getResourceHelper().getFormattedText(ERDesignerBundle.EDITOR), null, component
                .getDetailComponent());
        theViews[0].getWindowProperties().setCloseEnabled(false);
        theViews[0].getWindowProperties().setUndockEnabled(false);
        theViews[0].getWindowProperties().setUndockOnDropEnabled(false);
        theViews[1] = new View(getResourceHelper().getFormattedText(ERDesignerBundle.OUTLINE), null, outline);
        theViews[1].getWindowProperties().setCloseEnabled(false);
        theViews[1].getWindowProperties().setUndockEnabled(false);
        theViews[1].getWindowProperties().setUndockOnDropEnabled(false);
        theViewMap.addView(0, theViews[0]);
        theViewMap.addView(1, theViews[1]);

        SwingUtilities.invokeAndWait(new Runnable() {

            @Override
            public void run() {
                rootWindow = DockingUtil.createRootWindow(theViewMap, true);
                SplitWindow theSplitWindow = new SplitWindow(true, 0.8f, theViews[0], theViews[1]);
                rootWindow.setWindow(theSplitWindow);

                rootWindow.getWindowBar(Direction.RIGHT).setEnabled(true);
                rootWindow.getRootWindowProperties().setRecursiveTabsEnabled(false);
            }

        });

        theViews[1].addListener(this);
        theViews[0].addListener(this);
    }

    public RootWindow getRootWindow() {
        return rootWindow;
    }

    public void saveLayoutToPreferences() {
        // TODO [mirkosertic]
    }

    @Override
    public ResourceHelper getResourceHelper() {
        return ResourceHelper.getResourceHelper(ERDesignerBundle.BUNDLE_NAME);
    }

    @Override
    public void windowClosed(DockingWindow arg0) {
        System.out.println("Closed");
    }

    @Override
    public void windowDocked(DockingWindow arg0) {
        System.out.println("Docked");
    }

    @Override
    public void windowHidden(DockingWindow arg0) {
        System.out.println("Hidden");
    }

    @Override
    public void windowMaximized(DockingWindow arg0) {
        System.out.println("Maximized");
    }

    @Override
    public void windowMinimized(DockingWindow arg0) {
        System.out.println("Minimized");
    }

    @Override
    public void windowRemoved(DockingWindow arg0, DockingWindow arg1) {
        System.out.println("Removed");
    }

    @Override
    public void windowRestored(DockingWindow aWindow) {
        System.out.println("Restored");
    }

    @Override
    public void windowAdded(DockingWindow arg0, DockingWindow arg1) {
        System.out.println("Added");
    }

    @Override
    public void windowShown(DockingWindow arg0) {
        System.out.println("Shown");
    }

    @Override
    public void windowUndocked(DockingWindow arg0) {
        System.out.println("Undocked");
    }
}
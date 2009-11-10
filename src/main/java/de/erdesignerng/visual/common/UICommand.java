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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;

import de.erdesignerng.util.ApplicationPreferences;
import de.mogwai.common.client.looks.components.action.ActionEventProcessor;

public abstract class UICommand implements ActionEventProcessor, ActionListener {

    protected ERDesignerComponent component;

    private final ApplicationPreferences preferences;

    public UICommand(ERDesignerComponent aComponent) {
        component = aComponent;
        preferences = ApplicationPreferences.getInstance();
    }

    protected ERDesignerWorldConnector getWorldConnector() {
        return component.getWorldConnector();
    }

    protected JComponent getDetailComponent() {
        return component.getDetailComponent();
    }

    protected ApplicationPreferences getPreferences() {
        return preferences;
    }

    public abstract void execute();

    @Override
    public void processActionEvent(ActionEvent e) {
        execute();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        execute();
    }

    /**
     * Refresh the display of a specific object.
     * 
     * @param aChangedObject
     *            the object to update
     */
    public void refreshDisplayOf(Object aChangedObject) {
        component.repaintGraph();
        OutlineComponent.getDefault().refresh(component.getModel(), aChangedObject);
    }
}
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

import de.mogwai.common.client.looks.components.action.ActionEventProcessor;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public abstract class UICommand implements ActionEventProcessor, ActionListener {

    public UICommand() {
    }

    protected ERDesignerWorldConnector getWorldConnector() {
        return ERDesignerComponent.getDefault().getWorldConnector();
    }

    protected JComponent getDetailComponent() {
        return ERDesignerComponent.getDefault().getDetailComponent();
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
     */
    public void refreshDisplayAndOutline() {
        ERDesignerComponent component = ERDesignerComponent.getDefault();
        component.repaintGraph();
        OutlineComponent.getDefault().refresh(component.getModel());
    }
}
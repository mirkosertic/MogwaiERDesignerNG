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
package de.erdesignerng.visual.editor.table;

import de.erdesignerng.model.Index;
import de.mogwai.common.client.binding.PropertyAdapter;
import de.mogwai.common.client.looks.components.DefaultCheckBoxList;

public class IndexAttributesPropertyAdapter extends PropertyAdapter {

    public IndexAttributesPropertyAdapter(DefaultCheckBoxList aComponent, String aPropertyName) {
        super(aComponent, aPropertyName);
    }

    @Override
    public void model2view(Object aModel, String aPropertyName) {

        DefaultCheckBoxList theComponent = (DefaultCheckBoxList) getComponent()[0];

        Index theIndex = (Index) aModel;
        theComponent.setSelectedItems(theIndex.getAttributes());
    }

    @Override
    public void view2model(Object aModel, String aPropertyName) {
        DefaultCheckBoxList theComponent = (DefaultCheckBoxList) getComponent()[0];

        Index theIndex = (Index) aModel;
        theIndex.getAttributes().clear();
        theIndex.getAttributes().addAll(theComponent.getSelectedItems());

    }
}

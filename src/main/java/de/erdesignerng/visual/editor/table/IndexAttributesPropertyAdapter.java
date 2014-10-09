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
import de.erdesignerng.model.IndexExpression;
import de.mogwai.common.client.binding.PropertyAdapter;
import de.mogwai.common.client.binding.validator.ValidationError;
import de.mogwai.common.client.looks.components.DefaultList;
import de.mogwai.common.client.looks.components.list.DefaultListModel;

import java.util.ArrayList;
import java.util.List;

public class IndexAttributesPropertyAdapter extends PropertyAdapter {

	public IndexAttributesPropertyAdapter(DefaultList aComponent) {
		super(aComponent, null);
	}

	@Override
	public void model2view(Object aModel, String aPropertyName) {

		DefaultList theComponent = (DefaultList) getComponent()[0];

		Index theIndex = (Index) aModel;
		DefaultListModel theModel = theComponent.getModel();
		theModel.clear();
        theIndex.getExpressions().forEach(theModel::add);
	}

	@Override
	public void view2model(Object aModel, String aPropertyName) {
		DefaultList theComponent = (DefaultList) getComponent()[0];

		Index theIndex = (Index) aModel;
		theIndex.getExpressions().clear();
		DefaultListModel<IndexExpression> theModel = theComponent.getModel();
		for (int i = 0; i < theModel.getSize(); i++) {
			theIndex.getExpressions().add(theModel.get(i));
		}

	}

	@Override
	public List<ValidationError> validate() {
		List<ValidationError> theResult = new ArrayList<>();
		markValid();
		return theResult;
	}
}

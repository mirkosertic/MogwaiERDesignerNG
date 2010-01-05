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
package de.erdesignerng.visual.scaffolding;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.apache.commons.lang.StringUtils;
import org.metawidget.layout.iface.Layout;
import org.metawidget.swing.SwingMetawidget;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class JGoodiesTableLayout implements Layout<JComponent, SwingMetawidget> {

	class Entry {
		JComponent component;
		String elementName;
		Map<String, String> attributes;
	}

	private List<Entry> components;

	@Override
	public void onStartBuild(SwingMetawidget metawidget) {
		components = new ArrayList<Entry>();
	}

	@Override
	public void layoutChild(JComponent aWidget, String aElementName,
			Map<String, String> aAttributes, SwingMetawidget aMetaWidget) {
		Entry theEntry = new Entry();
		theEntry.component = aWidget;
		theEntry.elementName = aElementName;
		theEntry.attributes = aAttributes;

		components.add(theEntry);
	}

	@Override
	public void onEndBuild(SwingMetawidget aWidget) {
		String theColDef = "2dlu,p,2dlu,p,2dlu";
		String theRowDef = "2dlu";
		for (int i = 0; i < components.size(); i++) {
			theRowDef = theRowDef + ",p,2dlu";
		}

		FormLayout theLayout = new FormLayout(theColDef, theRowDef);
		CellConstraints cons = new CellConstraints();

		aWidget.setLayout(theLayout);
		for (int i = 0; i < components.size(); i++) {

			int theRow = 2 + i * 2;

			Entry theEntry = components.get(i);

			String labelText = null;

			if (theEntry.attributes != null) {
				labelText = aWidget.getLabelString(theEntry.attributes);
			}

			if (!StringUtils.isEmpty(labelText)) {
				JLabel theLabel = new JLabel();
				theLabel.setText(labelText + ":");

				aWidget.add(theLabel, cons.xy(2, theRow));
			}

			aWidget.add(theEntry.component, cons.xy(4, theRow));
		}
	}
}

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
package de.erdesignerng.visual.editor.repository;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import de.erdesignerng.ERDesignerBundle;
import de.mogwai.common.client.looks.components.DefaultButton;
import de.mogwai.common.client.looks.components.DefaultComboBox;
import de.mogwai.common.client.looks.components.DefaultLabel;

import javax.swing.*;

/**
 * View for the save to dictionary dialog.
 * 
 * @author mirkosertic
 */
public class LoadFromRepositoryView extends JPanel {

	private final DefaultComboBox existingNameBox = new DefaultComboBox();

	private final DefaultButton okButton = new DefaultButton();

	private final DefaultButton cancelButton = new DefaultButton();

	public LoadFromRepositoryView() {
		initialize();
	}

	private void initialize() {

		String theColDef = "2dlu,50dlu,2dlu,fill:100dlu:grow,2dlu";
		String theRowDef = "2dlu,p,50dlu,p,2dlu";

		FormLayout theLayout = new FormLayout(theColDef, theRowDef);
		setLayout(theLayout);

		CellConstraints cons = new CellConstraints();

		add(new DefaultLabel(ERDesignerBundle.NAME), cons.xy(2, 2));
		add(existingNameBox, cons.xy(4, 2));

		JPanel thePanel = new JPanel();

		theColDef = "60dlu,fill:2dlu:grow,60dlu";
		theRowDef = "p";

		theLayout = new FormLayout(theColDef, theRowDef);
		thePanel.setLayout(theLayout);

		thePanel.add(okButton, cons.xy(1, 1));
		okButton.setText("Ok");
		thePanel.add(cancelButton, cons.xy(3, 1));
		cancelButton.setText("Cancel");

		add(thePanel, cons.xywh(2, 4, 3, 1));
	}

	/**
	 * @return the existingNameBox
	 */
	public DefaultComboBox getExistingNameBox() {
		return existingNameBox;
	}

	/**
	 * @return the okButton
	 */
	public DefaultButton getOkButton() {
		return okButton;
	}

	/**
	 * @return the cancelButton
	 */
	public DefaultButton getCancelButton() {
		return cancelButton;
	}
}
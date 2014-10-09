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
package de.erdesignerng.visual.editor.preferences;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import de.mogwai.common.client.looks.UIInitializer;
import de.mogwai.common.client.looks.components.DefaultButton;

import javax.swing.*;

public class PreferencesEditorView extends JPanel {

	private PreferencesPanel preferences;

	private final DefaultButton okButton = new DefaultButton();

	private final DefaultButton cancelButton = new DefaultButton();

	public PreferencesEditorView() {
		initialize();
	}

	private void initialize() {

		preferences = new PreferencesPanel();

		preferences.initValues();

		String theColDef = "2dlu,fill:250dlu:grow,2dlu";
		String theRowDef = "2dlu,p,10dlu,p,2dlu";

		FormLayout theLayout = new FormLayout(theColDef, theRowDef);
		setLayout(theLayout);

		CellConstraints cons = new CellConstraints();

		UIInitializer.getInstance().initialize(this);
		add(preferences, cons.xy(2, 2));

		JPanel thePanel = new JPanel();
		UIInitializer.getInstance().initialize(thePanel);

		theColDef = "60dlu,2dlu:grow,60dlu";
		theRowDef = "p";

		theLayout = new FormLayout(theColDef, theRowDef);
		thePanel.setLayout(theLayout);

		thePanel.add(okButton, cons.xy(1, 1));
		okButton.setText("Ok");
		thePanel.add(cancelButton, cons.xy(3, 1));
		cancelButton.setText("Cancel");

		add(thePanel, cons.xy(2, 4));
	}

	/**
	 * @return the cancelButton
	 */
	public DefaultButton getCancelButton() {
		return cancelButton;
	}

	/**
	 * @return the okButton
	 */
	public DefaultButton getOkButton() {
		return okButton;
	}

	/**
	 * @return the preferences
	 */
	public PreferencesPanel getPreferences() {
		return preferences;
	}
}
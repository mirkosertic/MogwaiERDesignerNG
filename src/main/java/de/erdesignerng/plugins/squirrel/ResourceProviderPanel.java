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
package de.erdesignerng.plugins.squirrel;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;

import de.erdesignerng.ERDesignerBundle;
import de.mogwai.common.i18n.ResourceHelper;
import de.mogwai.common.i18n.ResourceHelperProvider;

public class ResourceProviderPanel extends JPanel implements ResourceHelperProvider {

	public ResourceProviderPanel() {
		setLayout(new BorderLayout());
	}

	public ResourceHelper getResourceHelper() {
		return ResourceHelper.getResourceHelper(ERDesignerBundle.BUNDLE_NAME);
	}

	public void setContent(JComponent aComponent) {
		add(aComponent, BorderLayout.CENTER);
	}
}

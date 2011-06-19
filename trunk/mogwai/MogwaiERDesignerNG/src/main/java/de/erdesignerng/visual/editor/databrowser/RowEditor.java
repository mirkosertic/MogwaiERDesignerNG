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
package de.erdesignerng.visual.editor.databrowser;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.visual.editor.BaseEditor;
import de.mogwai.common.client.looks.UIInitializer;

import javax.swing.*;
import java.sql.SQLException;

/**
 * Editor for a jdbc row.
 */
public class RowEditor extends BaseEditor {

	public RowEditor(JComponent aParent, PaginationDataModel aModel, int aRow) throws SQLException {
		super(aParent, ERDesignerBundle.EDITROW);

		initialize(aModel, aRow);
	}

	private void initialize(PaginationDataModel aModel, int aRow) throws SQLException {

		RowEditorView view = new RowEditorView(aModel, aRow);

		view.getOkButton().setAction(okAction);
		view.getCancelButton().setAction(cancelAction);

		setContentPane(view);
		setResizable(true);

		pack();

		UIInitializer.getInstance().initialize(view);
		UIInitializer.getInstance().initialize(this);
	}

	@Override
	public void applyValues() throws Exception {
	}
}